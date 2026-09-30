import { useEffect, useState } from 'react'
import { api, getToken, setToken } from './api'

const COLS = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED']
const today = () => new Date().toISOString().slice(0, 10)

export default function App() {
  const [authed, setAuthed] = useState(!!getToken())
  if (!authed) return <Auth onDone={() => setAuthed(true)} />
  return <Board onLogout={() => { setToken(null); setAuthed(false) }} />
}

function Auth({ onDone }) {
  const [mode, setMode] = useState('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    try {
      const r = await api(`/auth/${mode}`, 'POST', { email, password })
      setToken(r.token)
      onDone()
    } catch (x) { setErr(x.message) }
  }

  return (
    <form className="auth" onSubmit={submit}>
      <h1>Job Tracker</h1>
      <p className="muted">Track applications and score your resume against job descriptions.</p>
      <input placeholder="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
      <input placeholder="Password (min 6)" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
      {err && <div className="err">{err}</div>}
      <button>{mode === 'login' ? 'Log in' : 'Create account'}</button>
      <a onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setErr('') }}>
        {mode === 'login' ? 'New here? Register' : 'Have an account? Log in'}
      </a>
    </form>
  )
}

function Board({ onLogout }) {
  const [apps, setApps] = useState([])
  const [resume, setResume] = useState(localStorage.getItem('resume') || '')
  const [form, setForm] = useState({ company: '', role: '', jobDescription: '', followUpOn: '' })
  const [err, setErr] = useState('')

  const load = () => api('/applications').then(setApps).catch((e) => setErr(e.message))
  useEffect(() => { load() }, [])

  const saveResume = (v) => { setResume(v); localStorage.setItem('resume', v) }

  const add = async (e) => {
    e.preventDefault()
    try {
      let matchScore = null, notes = ''
      if (resume && form.jobDescription) {
        const m = await api('/match', 'POST', { resumeText: resume, jobDescription: form.jobDescription })
        matchScore = m.score
        if (m.missing.length) notes = 'Missing skills: ' + m.missing.join(', ')
      }
      const body = { ...form, followUpOn: form.followUpOn || null, matchScore, notes }
      await api('/applications', 'POST', body)
      setForm({ company: '', role: '', jobDescription: '', followUpOn: '' })
      setErr('')
      load()
    } catch (x) { setErr(x.message) }
  }

  const move = async (id, status) => {
    setApps((a) => a.map((x) => (x.id === id ? { ...x, status } : x)))
    await api(`/applications/${id}/status`, 'PATCH', { status }).catch(load)
  }
  const remove = async (id) => { await api(`/applications/${id}`, 'DELETE'); load() }

  const total = apps.length
  const interviews = apps.filter((a) => a.status === 'INTERVIEW' || a.status === 'OFFER').length

  return (
    <div className="wrap">
      <header>
        <h1>Job Tracker</h1>
        <span className="muted">{total} applications · {total ? Math.round((100 * interviews) / total) : 0}% reached interview</span>
        <button className="ghost" onClick={onLogout}>Log out</button>
      </header>

      <div className="panels">
        <form className="panel" onSubmit={add}>
          <h3>Add application</h3>
          <input placeholder="Company" value={form.company} onChange={(e) => setForm({ ...form, company: e.target.value })} required />
          <input placeholder="Role" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })} />
          <textarea placeholder="Paste job description (used for match score)" value={form.jobDescription} onChange={(e) => setForm({ ...form, jobDescription: e.target.value })} />
          <label className="muted">Follow up on <input type="date" value={form.followUpOn} onChange={(e) => setForm({ ...form, followUpOn: e.target.value })} /></label>
          {err && <div className="err">{err}</div>}
          <button>Add</button>
        </form>
        <div className="panel">
          <h3>My resume text</h3>
          <textarea className="tall" placeholder="Paste your resume text once. It is saved in this browser only." value={resume} onChange={(e) => saveResume(e.target.value)} />
        </div>
      </div>

      <div className="board">
        {COLS.map((col) => (
          <div key={col} className="col" onDragOver={(e) => e.preventDefault()}
               onDrop={(e) => move(Number(e.dataTransfer.getData('id')), col)}>
            <h4>{col} <span>{apps.filter((a) => a.status === col).length}</span></h4>
            {apps.filter((a) => a.status === col).map((a) => (
              <div key={a.id} className="card" draggable onDragStart={(e) => e.dataTransfer.setData('id', a.id)}>
                <b>{a.company}</b>
                <div className="muted">{a.role}</div>
                {a.matchScore != null && <span className={'badge ' + (a.matchScore >= 70 ? 'good' : a.matchScore >= 40 ? 'mid' : 'low')}>{a.matchScore}% match</span>}
                {a.followUpOn && (
                  <div className={a.followUpOn < today() && (col === 'APPLIED' || col === 'INTERVIEW') ? 'due' : 'muted'}>
                    Follow up: {a.followUpOn}{a.followUpOn < today() && (col === 'APPLIED' || col === 'INTERVIEW') ? ' (overdue)' : ''}
                  </div>
                )}
                {a.notes && <div className="note">{a.notes}</div>}
                <button className="x" onClick={() => remove(a.id)}>Delete</button>
              </div>
            ))}
          </div>
        ))}
      </div>
    </div>
  )
}
