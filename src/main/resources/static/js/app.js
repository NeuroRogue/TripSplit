// ==========================================================
// TripSplit - Frontend Application Controller
// Connects to REST APIs: /api/auth, /api/trips, /api/activities,
// /api/expenses, /api/settlements, /api/dashboard
// ==========================================================

const API_BASE = '/api';

// State Management
let currentUser = { id: 1, name: 'Rahul Sharma (Lead)', email: 'rahul@example.com' };
let allUsers = [];
let allTrips = [];
let currentTrip = null;
let currentTripMembers = [];
let categoryChartInstance = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    // Set default dates in inputs
    const today = new Date().toISOString().split('T')[0];
    document.querySelectorAll('input[type="date"]').forEach(input => {
        if (!input.value) input.value = today;
    });

    loadInitialData();
});

async function loadInitialData() {
    await fetchUsers();
    await loadDashboard();
}

// -----------------------------------------------------------------------------
// UI Navigation
// -----------------------------------------------------------------------------
function showSection(sectionName) {
    document.getElementById('section-dashboard').classList.add('d-none');
    document.getElementById('section-trips').classList.add('d-none');
    document.getElementById('section-trip-detail').classList.add('d-none');
    document.getElementById('section-viva').classList.add('d-none');

    document.querySelectorAll('.navbar-nav .nav-link').forEach(l => l.classList.remove('active'));

    if (sectionName === 'dashboard') {
        document.getElementById('section-dashboard').classList.remove('d-none');
        document.getElementById('nav-dashboard').classList.add('active');
        loadDashboard();
    } else if (sectionName === 'trips') {
        document.getElementById('section-trips').classList.remove('d-none');
        document.getElementById('nav-trips').classList.add('active');
        loadTrips();
    } else if (sectionName === 'viva') {
        document.getElementById('section-viva').classList.remove('d-none');
        document.getElementById('nav-viva').classList.add('active');
    }
}

function showAlert(message, type = 'success') {
    const box = document.getElementById('alertBox');
    const msg = document.getElementById('alertMessage');
    box.className = `alert alert-${type} alert-dismissible fade show`;
    msg.textContent = message;
    box.classList.remove('d-none');
    setTimeout(() => { hideAlert(); }, 4500);
}

function hideAlert() {
    const box = document.getElementById('alertBox');
    box.classList.add('d-none');
}

// -----------------------------------------------------------------------------
// Users & Auth
// -----------------------------------------------------------------------------
async function fetchUsers() {
    try {
        const res = await fetch(`${API_BASE}/users`);
        const json = await res.json();
        if (json.success) {
            allUsers = json.data;
            renderUserDropdown();
        }
    } catch (err) {
        console.error('Failed to load users:', err);
    }
}

function renderUserDropdown() {
    const menu = document.getElementById('userSwitchDropdown');
    menu.innerHTML = '<li><h6 class="dropdown-header">Switch Logged-in User</h6></li>';
    allUsers.forEach(u => {
        const li = document.createElement('li');
        li.innerHTML = `<a class="dropdown-item ${u.id === currentUser.id ? 'active' : ''}" href="#" onclick="switchActiveUser(${u.id})">
            <i class="bi bi-person me-2"></i> ${u.name} <span class="text-muted small">(${u.email})</span>
        </a>`;
        menu.appendChild(li);
    });
}

function switchActiveUser(userId) {
    const found = allUsers.find(u => u.id === userId);
    if (found) {
        currentUser = found;
        document.getElementById('activeUserLabel').textContent = found.name;
        renderUserDropdown();
        showAlert(`Active session switched to ${found.name}`, 'info');
    }
}

function openRegisterModal() {
    new bootstrap.Modal(document.getElementById('registerModal')).show();
}

async function handleRegister(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value,
        phone: document.getElementById('regPhone').value
    };

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('User registered successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();
            document.getElementById('registerForm').reset();
            await fetchUsers();
            switchActiveUser(json.data.id);
        } else {
            showAlert(json.message || 'Registration failed', 'danger');
        }
    } catch (err) {
        showAlert('Network error during registration', 'danger');
    }
}

// -----------------------------------------------------------------------------
// Dashboard Module
// -----------------------------------------------------------------------------
async function loadDashboard() {
    try {
        const res = await fetch(`${API_BASE}/dashboard/summary`);
        const json = await res.json();
        if (json.success) {
            const data = json.data;
            document.getElementById('dashTotalTrips').textContent = data.totalTrips;
            document.getElementById('dashTotalSpend').textContent = `₹${parseFloat(data.totalGroupSpend).toLocaleString('en-IN', {minimumFractionDigits: 2})}`;
            document.getElementById('dashTotalExpenses').textContent = data.totalExpensesCount;

            renderRecentExpenses(data.recentExpenses);
            renderCategoryChart(data.topCategories);
        }
    } catch (err) {
        console.error('Failed to load dashboard:', err);
    }
}

function renderRecentExpenses(expenses) {
    const tbody = document.getElementById('recentExpensesTableBody');
    document.getElementById('recentExpenseCount').textContent = `${expenses ? expenses.length : 0} entries`;

    if (!expenses || expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-muted">No expenses recorded yet.</td></tr>';
        return;
    }

    tbody.innerHTML = expenses.map(e => `
        <tr>
            <td class="fw-semibold">${e.title}</td>
            <td><span class="badge bg-light text-dark border">${e.category}</span></td>
            <td><i class="bi bi-person me-1 text-muted"></i> ${e.payerName || 'N/A'}</td>
            <td class="text-muted small">${e.expenseDate}</td>
            <td class="text-end fw-bold text-primary">₹${parseFloat(e.amount).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
        </tr>
    `).join('');
}

function renderCategoryChart(categories) {
    const ctx = document.getElementById('categoryChart');
    if (!ctx) return;

    const labels = (categories || []).map(c => c.category);
    const dataVals = (categories || []).map(c => parseFloat(c.amount));
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#64748b'];

    if (categoryChartInstance) {
        categoryChartInstance.destroy();
    }

    if (labels.length === 0) {
        labels.push('No Expenses');
        dataVals.push(1);
    }

    categoryChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataVals,
                backgroundColor: colors.slice(0, labels.length)
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom' }
            }
        }
    });
}

// -----------------------------------------------------------------------------
// Trips Module
// -----------------------------------------------------------------------------
async function loadTrips() {
    try {
        const res = await fetch(`${API_BASE}/trips`);
        const json = await res.json();
        if (json.success) {
            allTrips = json.data;
            renderTripsGrid(allTrips);
        }
    } catch (err) {
        showAlert('Failed to fetch trips', 'danger');
    }
}

function renderTripsGrid(trips) {
    const grid = document.getElementById('tripsCardGrid');
    if (!trips || trips.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="bi bi-compass fs-1 text-muted mb-3 d-block"></i>
                <h5>No Trips Found</h5>
                <p class="text-muted">Create your first group trip to start managing expenses.</p>
                <button class="btn btn-primary" onclick="openCreateTripModal()">Create Trip</button>
            </div>
        `;
        return;
    }

    grid.innerHTML = trips.map(t => `
        <div class="col-md-6 col-lg-4">
            <div class="card trip-card border-0 shadow-sm h-100 p-3" onclick="openTripDetail(${t.id})">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <span class="badge bg-primary-subtle text-primary"><i class="bi bi-geo-alt"></i> ${t.destination}</span>
                    <span class="badge bg-secondary-subtle text-secondary"><i class="bi bi-people"></i> ${t.memberCount} members</span>
                </div>
                <h5 class="fw-bold mb-1">${t.title}</h5>
                <p class="text-muted small mb-3">
                    <i class="bi bi-calendar3 me-1"></i> ${t.startDate} &rarr; ${t.endDate}
                </p>
                <div class="mt-auto pt-2 border-top d-flex justify-content-between align-items-center">
                    <span class="small text-muted">Created by: <strong>${t.createdByName || 'N/A'}</strong></span>
                    <span class="text-primary fw-semibold small">Open Workspace &rarr;</span>
                </div>
            </div>
        </div>
    `).join('');
}

function openCreateTripModal() {
    new bootstrap.Modal(document.getElementById('createTripModal')).show();
}

async function handleCreateTrip(e) {
    e.preventDefault();
    const payload = {
        title: document.getElementById('tripTitle').value,
        destination: document.getElementById('tripDestination').value,
        startDate: document.getElementById('tripStartDate').value,
        endDate: document.getElementById('tripEndDate').value,
        createdBy: currentUser.id
    };

    try {
        const res = await fetch(`${API_BASE}/trips`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('Trip created successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('createTripModal')).hide();
            document.getElementById('createTripForm').reset();
            showSection('trips');
            openTripDetail(json.data.id);
        } else {
            showAlert(json.message || 'Failed to create trip', 'danger');
        }
    } catch (err) {
        showAlert('Network error creating trip', 'danger');
    }
}

// -----------------------------------------------------------------------------
// Trip Detail Workspace
// -----------------------------------------------------------------------------
async function openTripDetail(tripId) {
    try {
        const res = await fetch(`${API_BASE}/trips/${tripId}`);
        const json = await res.json();
        if (json.success) {
            currentTrip = json.data;
            currentTripMembers = json.data.members || [];

            // Update UI headers
            document.getElementById('detailTitle').textContent = currentTrip.title;
            document.getElementById('detailDestination').innerHTML = `<i class="bi bi-geo-alt"></i> ${currentTrip.destination}`;
            document.getElementById('detailDates').textContent = `${currentTrip.startDate} to ${currentTrip.endDate}`;
            document.getElementById('detailOrganizer').textContent = currentTrip.createdByName || 'Organizer';
            document.getElementById('detailBreadcrumb').textContent = currentTrip.title;

            // Show Trip Detail Section
            document.getElementById('section-dashboard').classList.add('d-none');
            document.getElementById('section-trips').classList.add('d-none');
            document.getElementById('section-trip-detail').classList.remove('d-none');
            document.getElementById('section-viva').classList.add('d-none');

            // Load subtab data
            switchTripTab('balances');
            loadTripActivities();
            loadTripExpenses();
            renderMembersTab();
        }
    } catch (err) {
        showAlert('Failed to open trip details', 'danger');
    }
}

function switchTripTab(tabName) {
    ['balances', 'expenses', 'activities', 'members'].forEach(t => {
        document.getElementById(`subtab-${t}`).classList.add('d-none');
        document.getElementById(`tab-btn-${t}`).classList.remove('active');
    });

    document.getElementById(`subtab-${tabName}`).classList.remove('d-none');
    document.getElementById(`tab-btn-${tabName}`).classList.add('active');

    if (tabName === 'balances') loadTripBalances();
    if (tabName === 'expenses') loadTripExpenses();
    if (tabName === 'activities') loadTripActivities();
    if (tabName === 'members') renderMembersTab();
}

// -----------------------------------------------------------------------------
// Balances & Settlements Subtab
// -----------------------------------------------------------------------------
async function loadTripBalances() {
    if (!currentTrip) return;
    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/balances`);
        const json = await res.json();
        if (json.success) {
            const data = json.data;
            renderSimplifiedDebts(data.simplifiedDebts);
            renderBalancesTable(data.userBalances);
        }
        loadSettlementsHistory();
    } catch (err) {
        console.error('Failed to load balances:', err);
    }
}

function renderSimplifiedDebts(debts) {
    const container = document.getElementById('simplifiedDebtsContainer');
    if (!debts || debts.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-3 text-success">
                <i class="bi bi-check-circle fs-3 d-block mb-1"></i>
                <strong>All balances are fully settled!</strong>
                <div class="small text-muted">No pending debts for this trip.</div>
            </div>
        `;
        return;
    }

    container.innerHTML = debts.map(d => `
        <div class="col-md-6 col-lg-4">
            <div class="p-3 debt-card shadow-sm border">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="badge bg-danger-subtle text-danger">Pending Payment</span>
                    <strong class="text-danger fs-5">₹${parseFloat(d.amount).toFixed(2)}</strong>
                </div>
                <div class="mb-3 small">
                    <strong>${d.debtorName}</strong> owes <strong>${d.creditorName}</strong>
                </div>
                <button class="btn btn-outline-success btn-sm w-100" onclick="quickSettleUp(${d.debtorId}, ${d.creditorId}, ${d.amount})">
                    <i class="bi bi-cash-coin me-1"></i> Settle Up Now
                </button>
            </div>
        </div>
    `).join('');
}

function renderBalancesTable(balances) {
    const tbody = document.getElementById('balancesTableBody');
    if (!balances || balances.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-3 text-muted">No balances available</td></tr>';
        return;
    }

    tbody.innerHTML = balances.map(b => {
        let badgeClass = 'badge-settled';
        let statusText = 'Settled';
        if (b.status === 'OWES') {
            badgeClass = 'badge-owes';
            statusText = `Owes ₹${Math.abs(parseFloat(b.netBalance)).toFixed(2)}`;
        } else if (b.status === 'GETS_BACK') {
            badgeClass = 'badge-gets';
            statusText = `Gets back ₹${parseFloat(b.netBalance).toFixed(2)}`;
        }

        return `
            <tr>
                <td class="fw-semibold"><i class="bi bi-person me-1 text-muted"></i> ${b.userName}</td>
                <td class="text-end">₹${parseFloat(b.totalPaid).toFixed(2)}</td>
                <td class="text-end">₹${parseFloat(b.totalOwed).toFixed(2)}</td>
                <td class="text-end">₹${parseFloat(b.settledPaid).toFixed(2)}</td>
                <td class="text-end">₹${parseFloat(b.settledReceived).toFixed(2)}</td>
                <td class="text-end fw-bold ${parseFloat(b.netBalance) < 0 ? 'text-danger' : (parseFloat(b.netBalance) > 0 ? 'text-success' : '')}">
                    ${parseFloat(b.netBalance) > 0 ? '+' : ''}₹${parseFloat(b.netBalance).toFixed(2)}
                </td>
                <td><span class="badge ${badgeClass} p-2">${statusText}</span></td>
            </tr>
        `;
    }).join('');
}

async function loadSettlementsHistory() {
    if (!currentTrip) return;
    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/settlements`);
        const json = await res.json();
        const tbody = document.getElementById('settlementsHistoryTableBody');
        if (json.success && json.data && json.data.length > 0) {
            tbody.innerHTML = json.data.map(s => `
                <tr>
                    <td class="text-muted small">${s.settlementDate}</td>
                    <td class="fw-semibold text-danger">${s.payerName}</td>
                    <td class="fw-semibold text-success">${s.payeeName}</td>
                    <td class="text-end fw-bold">₹${parseFloat(s.amount).toFixed(2)}</td>
                    <td class="small text-muted">${s.notes || '-'}</td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center py-3 text-muted">No settlements recorded yet.</td></tr>';
        }
    } catch (err) {
        console.error('Failed to load settlements:', err);
    }
}

function openRecordSettlementModal() {
    populateSettlementDropdowns();
    new bootstrap.Modal(document.getElementById('settlementModal')).show();
}

function quickSettleUp(debtorId, creditorId, amount) {
    populateSettlementDropdowns();
    document.getElementById('setPayer').value = debtorId;
    document.getElementById('setPayee').value = creditorId;
    document.getElementById('setAmount').value = amount;
    new bootstrap.Modal(document.getElementById('settlementModal')).show();
}

function populateSettlementDropdowns() {
    const pSelect = document.getElementById('setPayer');
    const rSelect = document.getElementById('setPayee');
    const options = currentTripMembers.map(m => `<option value="${m.userId}">${m.name}</option>`).join('');
    pSelect.innerHTML = options;
    rSelect.innerHTML = options;
}

async function handleRecordSettlement(e) {
    e.preventDefault();
    const payload = {
        payerId: parseInt(document.getElementById('setPayer').value),
        payeeId: parseInt(document.getElementById('setPayee').value),
        amount: parseFloat(document.getElementById('setAmount').value),
        settlementDate: document.getElementById('setDate').value,
        notes: document.getElementById('setNotes').value
    };

    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/settlements`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('Settlement recorded successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('settlementModal')).hide();
            document.getElementById('settlementForm').reset();
            loadTripBalances();
        } else {
            showAlert(json.message || 'Failed to record settlement', 'danger');
        }
    } catch (err) {
        showAlert('Network error recording settlement', 'danger');
    }
}

// -----------------------------------------------------------------------------
// Activities Subtab
// -----------------------------------------------------------------------------
async function loadTripActivities() {
    if (!currentTrip) return;
    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/activities`);
        const json = await res.json();
        if (json.success) {
            const activities = json.data || [];
            document.getElementById('tabBadgeActivities').textContent = activities.length;
            renderActivitiesGrid(activities);
        }
    } catch (err) {
        console.error('Failed to load activities:', err);
    }
}

function renderActivitiesGrid(activities) {
    const grid = document.getElementById('activitiesCardGrid');
    if (!activities || activities.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-4 text-muted">
                <i class="bi bi-list-check fs-2 d-block mb-2"></i>
                No activities planned yet. Add hotel, meals, or sightseeing activities!
            </div>
        `;
        return;
    }

    grid.innerHTML = activities.map(a => `
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm p-3 h-100">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="badge bg-primary-subtle text-primary">${a.category}</span>
                    <strong class="text-primary">₹${parseFloat(a.estimatedCost).toFixed(2)}</strong>
                </div>
                <h6 class="fw-bold mb-0">${a.name}</h6>
            </div>
        </div>
    `).join('');
}

function openAddActivityModal() {
    new bootstrap.Modal(document.getElementById('addActivityModal')).show();
}

async function handleAddActivity(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('actName').value,
        category: document.getElementById('actCategory').value,
        estimatedCost: parseFloat(document.getElementById('actEstimatedCost').value || 0)
    };

    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/activities`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('Activity added successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addActivityModal')).hide();
            document.getElementById('addActivityForm').reset();
            loadTripActivities();
        } else {
            showAlert(json.message || 'Failed to add activity', 'danger');
        }
    } catch (err) {
        showAlert('Network error adding activity', 'danger');
    }
}

// -----------------------------------------------------------------------------
// Expenses Subtab
// -----------------------------------------------------------------------------
async function loadTripExpenses() {
    if (!currentTrip) return;
    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/expenses`);
        const json = await res.json();
        if (json.success) {
            const expenses = json.data || [];
            document.getElementById('tabBadgeExpenses').textContent = expenses.length;
            renderTripExpensesTable(expenses);
        }
    } catch (err) {
        console.error('Failed to load expenses:', err);
    }
}

function renderTripExpensesTable(expenses) {
    const tbody = document.getElementById('tripExpensesTableBody');
    if (!expenses || expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No expenses recorded for this trip.</td></tr>';
        return;
    }

    tbody.innerHTML = expenses.map(e => {
        const sharesText = (e.shares || []).map(s => `
            <span class="badge bg-light text-dark border me-1 mb-1">
                ${s.userName}: ₹${parseFloat(s.shareAmount).toFixed(2)}
            </span>
        `).join('');

        return `
            <tr>
                <td class="fw-semibold">${e.title}</td>
                <td><span class="badge bg-primary-subtle text-primary">${e.category}</span></td>
                <td>${e.activityName ? `<span class="badge bg-light text-dark border">${e.activityName}</span>` : '-'}</td>
                <td><i class="bi bi-person me-1 text-muted"></i> ${e.payerName}</td>
                <td class="text-muted small">${e.expenseDate}</td>
                <td class="text-end fw-bold text-success">₹${parseFloat(e.amount).toFixed(2)}</td>
                <td>${sharesText}</td>
            </tr>
        `;
    }).join('');
}

async function openAddExpenseModal() {
    // Populate payers and activities
    const payerSelect = document.getElementById('expPayer');
    payerSelect.innerHTML = currentTripMembers.map(m => `<option value="${m.userId}">${m.name}</option>`).join('');

    const actSelect = document.getElementById('expActivity');
    actSelect.innerHTML = '<option value="">-- No specific activity --</option>';
    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/activities`);
        const json = await res.json();
        if (json.success && json.data) {
            json.data.forEach(a => {
                actSelect.innerHTML += `<option value="${a.id}">${a.name} (${a.category})</option>`;
            });
        }
    } catch (e) {
        console.warn('Could not load activities for dropdown');
    }

    // Populate participant checkboxes
    const chkContainer = document.getElementById('participantCheckboxesContainer');
    chkContainer.innerHTML = currentTripMembers.map(m => `
        <div class="col-md-6">
            <div class="form-check">
                <input class="form-check-input participant-chk" type="checkbox" value="${m.userId}" id="chk_${m.userId}" checked onchange="calculateSplitPreview()">
                <label class="form-check-label" for="chk_${m.userId}">
                    ${m.name}
                </label>
            </div>
        </div>
    `).join('');

    calculateSplitPreview();
    new bootstrap.Modal(document.getElementById('addExpenseModal')).show();
}

function selectAllParticipants(select) {
    document.querySelectorAll('.participant-chk').forEach(c => c.checked = select);
    calculateSplitPreview();
}

function calculateSplitPreview() {
    const amt = parseFloat(document.getElementById('expAmount').value) || 0;
    const selected = Array.from(document.querySelectorAll('.participant-chk:checked')).map(c => c.value);
    const count = selected.length;
    const textEl = document.getElementById('splitPreviewText');

    if (amt > 0 && count > 0) {
        const base = (amt / count).toFixed(2);
        textEl.innerHTML = `<span class="text-primary fw-semibold">₹${amt.toFixed(2)}</span> split equally among <span class="fw-semibold">${count} members</span> &approx; <strong>₹${base}</strong> per person (paise remainder assigned deterministically).`;
    } else {
        textEl.textContent = 'Enter an amount and select participants to see split preview.';
    }
}

async function handleAddExpense(e) {
    e.preventDefault();
    const selectedParticipants = Array.from(document.querySelectorAll('.participant-chk:checked')).map(c => parseInt(c.value));
    if (selectedParticipants.length === 0) {
        showAlert('Please select at least one participant to share the expense', 'warning');
        return;
    }

    const payload = {
        title: document.getElementById('expTitle').value,
        amount: parseFloat(document.getElementById('expAmount').value),
        payerId: parseInt(document.getElementById('expPayer').value),
        expenseDate: document.getElementById('expDate').value,
        category: document.getElementById('expCategory').value,
        activityId: document.getElementById('expActivity').value ? parseInt(document.getElementById('expActivity').value) : null,
        participantUserIds: selectedParticipants
    };

    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/expenses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('Expense recorded and split successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addExpenseModal')).hide();
            document.getElementById('addExpenseForm').reset();
            loadTripExpenses();
            loadTripBalances();
        } else {
            showAlert(json.message || 'Failed to record expense', 'danger');
        }
    } catch (err) {
        showAlert('Network error recording expense', 'danger');
    }
}

// -----------------------------------------------------------------------------
// Members Subtab
// -----------------------------------------------------------------------------
function renderMembersTab() {
    const tbody = document.getElementById('tripMembersTableBody');
    document.getElementById('tabBadgeMembers').textContent = currentTripMembers.length;

    if (!currentTripMembers || currentTripMembers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center py-3 text-muted">No members found</td></tr>';
        return;
    }

    tbody.innerHTML = currentTripMembers.map(m => `
        <tr>
            <td class="fw-semibold"><i class="bi bi-person-fill me-1 text-primary"></i> ${m.name}</td>
            <td>${m.email || '-'}</td>
            <td>${m.phone || '-'}</td>
            <td><span class="badge ${m.role === 'ORGANIZER' ? 'bg-primary' : 'bg-secondary'}">${m.role}</span></td>
            <td class="small text-muted">${m.joinedAt ? m.joinedAt.substring(0, 10) : '-'}</td>
        </tr>
    `).join('');
}

function openAddMemberModal() {
    const select = document.getElementById('selectUserToAdd');
    // Filter users not already in trip
    const existingIds = new Set(currentTripMembers.map(m => m.userId));
    const availableUsers = allUsers.filter(u => !existingIds.has(u.id));

    if (availableUsers.length === 0) {
        showAlert('All registered users are already members of this trip! Register a new user first.', 'info');
        return;
    }

    select.innerHTML = availableUsers.map(u => `<option value="${u.id}">${u.name} (${u.email})</option>`).join('');
    new bootstrap.Modal(document.getElementById('addMemberModal')).show();
}

async function handleAddMember(e) {
    e.preventDefault();
    const payload = {
        userId: parseInt(document.getElementById('selectUserToAdd').value),
        role: document.getElementById('memberRole').value
    };

    try {
        const res = await fetch(`${API_BASE}/trips/${currentTrip.id}/members`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const json = await res.json();
        if (json.success) {
            showAlert('Member added to trip successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('addMemberModal')).hide();
            // Refresh trip details
            await openTripDetail(currentTrip.id);
            switchTripTab('members');
        } else {
            showAlert(json.message || 'Failed to add member', 'danger');
        }
    } catch (err) {
        showAlert('Network error adding member', 'danger');
    }
}

