let token = localStorage.getItem('token')

export const getToken = () => token
export const setToken = (t) => {
  token = t
  t ? localStorage.setItem('token', t) : localStorage.removeItem('token')
}

export async function api(path, method = 'GET', body) {
  const res = await fetch('/api' + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token && { Authorization: 'Bearer ' + token }) },
    body: body ? JSON.stringify(body) : undefined,
  })
  if (res.status === 401 && !path.startsWith('/auth')) {
    setToken(null)
    location.reload()
  }
  if (!res.ok) {
    let msg = 'Request failed'
    try { msg = (await res.json()).message || msg } catch { /* ignore */ }
    throw new Error(msg)
  }
  return res.status === 204 ? null : res.json()
}
