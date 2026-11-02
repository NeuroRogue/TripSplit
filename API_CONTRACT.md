# TripSplit - Official API Contract & Team Integration Spec

This specification defines the shared REST API contracts agreed upon by the 4-member development team for the **TripSplit** Java Mini Project.

---

## 1. Global Conventions

- **Base URL**: `http://localhost:8080/api`
- **Standard Success Response Format**:
  ```json
  {
    "success": true,
    "message": "Operation successful description",
    "data": { ... },
    "timestamp": "2026-11-02T22:00:00"
  }
  ```
- **Standard Error Response Format**:
  ```json
  {
    "success": false,
    "message": "Validation or business error message",
    "errors": ["Specific constraint failed"],
    "timestamp": "2026-11-02T22:00:00"
  }
  ```

---

## 2. Authentication & User Endpoints (`com.tripsplit.auth`)
*Owner: Member 4*

### 2.1 Register User
- **POST** `/api/auth/register`
- **Request Body**:
  ```json
  {
    "name": "Ananya Sharma",
    "email": "ananya@example.com",
    "password": "securePassword123",
    "phone": "9876543210"
  }
  ```
- **Response**: `200 OK` with user object (excluding password).

### 2.2 Login User
- **POST** `/api/auth/login`
- **Request Body**:
  ```json
  {
    "email": "ananya@example.com",
    "password": "securePassword123"
  }
  ```
- **Response**: `200 OK` with active session and user object.

### 2.3 Logout
- **POST** `/api/auth/logout`

### 2.4 Get Current User / List Users
- **GET** `/api/users/me`
- **GET** `/api/users`

---

## 3. Trip Management Endpoints (`com.tripsplit.trip`)
*Owner: Member 2*

### 3.1 Get All Trips
- **GET** `/api/trips`
- **Response**:
  ```json
  {
    "success": true,
    "data": [
      {
        "id": 1,
        "title": "Goa Beach Vacation",
        "destination": "Goa, India",
        "startDate": "2026-11-10",
        "endDate": "2026-11-15",
        "createdById": 1,
        "createdByName": "Rahul Sharma",
        "memberCount": 4
      }
    ]
  }
  ```

### 3.2 Create Trip
- **POST** `/api/trips`
- **Request Body**:
  ```json
  {
    "title": "Manali Snow Trek",
    "destination": "Manali, HP",
    "startDate": "2026-12-01",
    "endDate": "2026-12-07",
    "createdBy": 1
  }
  ```
- **Note**: Automatically assigns `createdBy` as the trip's first member and `ORGANIZER`.

### 3.3 Get Trip Details
- **GET** `/api/trips/{tripId}`
- **Response**: Returns trip info and array of members with their roles and join dates.

### 3.4 Add Member to Trip
- **POST** `/api/trips/{tripId}/members`
- **Request Body**:
  ```json
  {
    "userId": 2,
    "role": "MEMBER"
  }
  ```

---

## 4. Activity Management Endpoints (`com.tripsplit.activity`)
*Owner: Member 2*

### 4.1 Get Activities by Trip
- **GET** `/api/trips/{tripId}/activities`

### 4.2 Create Activity
- **POST** `/api/trips/{tripId}/activities`
- **Categories**: `HOTEL`, `FOOD`, `SIGHTSEEING`, `TRAVEL`, `SHOPPING`, `OTHER`
- **Request Body**:
  ```json
  {
    "name": "Seaside Resort Stay",
    "category": "HOTEL",
    "estimatedCost": 24000.00
  }
  ```

### 4.3 Update / Delete Activity
- **PUT** `/api/activities/{activityId}`
- **DELETE** `/api/activities/{activityId}`

---

## 5. Expense Management Endpoints (`com.tripsplit.expense`)
*Owner: Member 3*

### 5.1 Get Expenses by Trip
- **GET** `/api/trips/{tripId}/expenses`

### 5.2 Record Group Expense (With Deterministic Paise Splitting)
- **POST** `/api/trips/{tripId}/expenses`
- **Request Body**:
  ```json
  {
    "title": "Group Dinner at Fisherman's Wharf",
    "amount": 1000.00,
    "payerId": 2,
    "expenseDate": "2026-11-11",
    "category": "FOOD",
    "activityId": 2,
    "participantUserIds": [1, 2, 3]
  }
  ```
- **Important Split Logic**:
  - Validates that `payerId` and all `participantUserIds` are members of `tripId`.
  - Splits ₹1,000 across 3 members using `MoneyUtils.splitEqually`:
    - Participant 1 (Rahul): ₹333.34
    - Participant 2 (Priya): ₹333.33
    - Participant 3 (Amit): ₹333.33
    - **Total sum:** exactly ₹1000.00.
  - Persists `Expense` and all `ExpenseShare` records in a single database transaction (`@Transactional`).

---

## 6. Balances & Settlements Endpoints (`com.tripsplit.settlement`)
*Owner: Member 1 (Lead)*

### 6.1 Get Trip Balances & Simplified Debts
- **GET** `/api/trips/{tripId}/balances`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "tripId": 1,
      "totalTripExpenses": 25000.00,
      "userBalances": [
        {
          "userId": 1,
          "userName": "Rahul Sharma",
          "totalPaid": 24000.00,
          "totalOwed": 6333.34,
          "settledPaid": 0.00,
          "settledReceived": 2000.00,
          "netBalance": 15666.66,
          "status": "GETS_BACK"
        }
      ],
      "simplifiedDebts": [
        {
          "debtorId": 4,
          "debtorName": "Sneha Kulkarni",
          "creditorId": 1,
          "creditorName": "Rahul Sharma",
          "amount": 6000.00
        }
      ]
    }
  }
  ```

### 6.2 Record Settlement
- **POST** `/api/trips/{tripId}/settlements`
- **Request Body**:
  ```json
  {
    "payerId": 4,
    "payeeId": 1,
    "amount": 6000.00,
    "settlementDate": "2026-11-12",
    "notes": "Paid via PhonePe UPI"
  }
  ```

---

## 7. Dashboard Endpoints (`com.tripsplit.dashboard`)
*Owner: Member 4*

### 7.1 Overall Dashboard Summary
- **GET** `/api/dashboard/summary`
- Returns total trips, total spend, recent expenses, and category metrics.

### 7.2 Trip Specific Summary
- **GET** `/api/trips/{tripId}/summary`

---

## 8. Git Branching Strategy

| Branch Name | Assigned Owner | Responsibility |
|---|---|---|
| `main` | Member 1 (Lead) | Baseline skeleton, merged production builds |
| `feature/trips-activities` | Member 2 | Trip and Activity CRUD & endpoints |
| `feature/expense-split` | Member 3 | Expense recording & share persistence |
| `feature/auth-dashboard` | Member 4 | User login/register & Dashboard charts UI |
| `feature/balance-settlement`| Member 1 | Settlement engine & Net balance algorithms |
