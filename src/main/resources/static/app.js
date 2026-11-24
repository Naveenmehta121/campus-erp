// CampusERP frontend — plain JS, no build step, no framework.
// It talks to the Spring Boot REST API with HTTP Basic auth:
// every fetch sends "Authorization: Basic base64(user:pass)".
let auth = null; // "Basic ..." header value, set on login

function headers() {
  return { 'Content-Type': 'application/json', 'Authorization': auth };
}

async function login() {
  const u = document.getElementById('user').value;
  const p = document.getElementById('pass').value;
  auth = 'Basic ' + btoa(u + ':' + p);
  const res = await fetch('/api/me', { headers: headers() });
  if (!res.ok) { document.getElementById('who').textContent = 'Login failed (401)'; auth = null; return; }
  const me = await res.json();
  document.getElementById('who').textContent = 'Logged in as ' + me.username + ' ' + me.roles.join(' ');
  loadDashboard();
  show('students');
}

async function loadDashboard() {
  const d = await (await fetch('/api/dashboard', { headers: headers() })).json();
  document.getElementById('cards').innerHTML = [
    ['Students', d.students], ['Fees due (₹)', d.feesDue],
    ['Unpaid fee records', d.unpaidFees], ['Attendance rows', d.attendanceRecords],
    ['Timetable slots', d.timetableEntries], ['Inventory items', d.inventoryItems],
  ].map(([k, v]) => `<div class="card"><b>${v}</b><span>${k}</span></div>`).join('');
}

async function show(tab) {
  document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('active'));
  event?.target?.classList.add('active');
  const panel = document.getElementById('panel');
  if (!auth) { panel.innerHTML = '<p class="muted">Log in first.</p>'; return; }
  const url = { students: '/api/students', fees: '/api/fees', attendance: '/api/attendance',
                timetable: '/api/timetable', inventory: '/api/inventory' }[tab];
  const rows = await (await fetch(url, { headers: headers() })).json();
  if (!Array.isArray(rows)) { panel.textContent = 'Error: ' + JSON.stringify(rows); return; }
  const fmt = {
    students: r => `<td>${r.name}</td><td>${r.rollNo}</td><td>${r.email}</td><td>${r.course} · Sem ${r.semester}</td>`,
    fees: r => `<td>${r.student?.name ?? '?'}</td><td>₹${r.amount}</td><td>₹${r.paidAmount}</td><td>${r.status}</td><td>${r.dueDate}</td>`,
    attendance: r => `<td>${r.student?.name ?? '?'}</td><td>${r.date}</td><td>${r.subject}</td><td>${r.status}</td>`,
    timetable: r => `<td>${r.dayOfWeek}</td><td>${r.startTime}–${r.endTime}</td><td>${r.subject}</td><td>${r.facultyName}</td><td>${r.room}</td>`,
    inventory: r => `<td>${r.name}</td><td>${r.category}</td><td>${r.quantity}</td><td>${r.location}</td><td>${r.condition}</td>`,
  }[tab];
  const heads = {
    students: 'Name|Roll no|Email|Course', fees: 'Student|Amount|Paid|Status|Due date',
    attendance: 'Student|Date|Subject|Status', timetable: 'Day|Time|Subject|Faculty|Room',
    inventory: 'Item|Category|Qty|Location|Condition',
  }[tab].split('|').map(h => `<th>${h}</th>`).join('');
  panel.innerHTML = `<table><tr>${heads}</tr>${rows.map(r => `<tr>${fmt(r)}</tr>`).join('')}</table>`;
}

async function addStudent() {
  const body = {
    name: document.getElementById('s-name').value,
    rollNo: document.getElementById('s-roll').value,
    email: document.getElementById('s-email').value,
    course: document.getElementById('s-course').value,
    semester: Number(document.getElementById('s-sem').value),
  };
  const res = await fetch('/api/students', { method: 'POST', headers: headers(), body: JSON.stringify(body) });
  document.getElementById('add-msg').textContent =
    res.ok ? 'Added ✓ (id ' + (await res.json()).id + ')' : 'Failed: ' + res.status + ' — ' + (await res.text());
  if (res.ok) { loadDashboard(); show('students'); }
}
