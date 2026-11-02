# TripSplit — College Java Mini Project Comprehensive Guide

> **Project Name**: TripSplit – Smart Group Trip Expense Management System  
> **Course**: Java / Advanced Java / Web Development Mini Project  
> **Team Size**: 4 Members  
> **Tech Stack**: Java 17/21, Spring Boot 3.x, Spring Data JPA (Hibernate), MySQL 8.x / H2, Maven, Bootstrap 5, Chart.js

---

## 1. Project Overview & Problem Statement

When friends, colleagues, or families travel together, multiple individuals incur expenses for accommodations, dining, sightseeing, rentals, and fuel. Calculating who paid what, who shared which item, and finding the minimum number of settlements to clear all debts is mathematically tedious and prone to human disputes.

**TripSplit solves this problem by:**
1. Managing trips, participants, and itineraries (activities).
2. Allowing custom per-expense participant selection (e.g., only 3 people joined a dinner).
3. Using an exact **Paise-Integer Splitting Algorithm** to ensure financial precision without floating-point inaccuracies.
4. Implementing a **Greedy Minimum Cash-Flow Algorithm** to simplify complex multi-person debts into minimal direct payments.
5. Providing an interactive dashboard with category-based visual analytics.

---

## 2. 4-Member Task Distribution Matrix

| Team Member | Module & Package | Primary Responsibilities & Viva Topics |
|---|---|---|
| **Member 1 (Team Lead & Integration)** | `com.tripsplit.common`<br>`com.tripsplit.settlement` | • Project skeleton, database relationships, Spring Boot configuration.<br>• Paise-precision splitting logic (`MoneyUtils.java`).<br>• Net balance & Debt Simplification Algorithm.<br>• Integration testing and Git lead. |
| **Member 2 (Trips & Activities)** | `com.tripsplit.trip`<br>`com.tripsplit.activity` | • Trip Entity, TripMember Entity, Activity Entity.<br>• CRUD controllers & JPA queries.<br>• Member association validation (`existsByTripIdAndUserId`). |
| **Member 3 (Expense Management)** | `com.tripsplit.expense` | • Expense Entity and ExpenseShare Entity.<br>• Participant share calculation and persistence.<br>• Transaction management (`@Transactional`) ensuring atomic expense + shares creation. |
| **Member 4 (Auth & Dashboard UI)** | `com.tripsplit.auth`<br>`com.tripsplit.dashboard` | • User entity and session authentication flow.<br>• Dashboard metrics aggregation service.<br>• Responsive Bootstrap 5 UI & Chart.js dynamic doughnut visualization. |

---

## 3. Database Schema & Entity Relationships

The project implements the exact 7-table normalized relational schema:

```
[ users ] (1)
    │
    ├───────────< (N) [ trips ] (1)
    │                  │
    │                  ├──< (N) [ trip_members ] (N) >──────────┐
    │                  │                                        │
    │                  ├──< (N) [ activities ] (1)              │
    │                  │          │                             │
    │                  │          └─< (N)                       │
    │                  │                │                       │
    │                  └──< (N) [ expenses ] (1)                │
    │                              │                            │
    │                              └──< (N) [ expense_shares ] (N)
    │                                              │
    ├──────────────────────────────────────────────┘
    │
    └───────────< (N) [ settlements ]
```

### Table Definitions:
1. `users`: Stores user profile (`id`, `name`, `email`, `password`, `phone`, `created_at`).
2. `trips`: Stores trip metadata (`id`, `title`, `destination`, `start_date`, `end_date`, `created_by`).
3. `trip_members`: Many-to-many bridge mapping users to trips with a specific `role` (ORGANIZER, MEMBER).
4. `activities`: Categorized trip events (HOTEL, FOOD, SIGHTSEEING, TRAVEL, SHOPPING, OTHER).
5. `expenses`: Master expense record (`title`, `amount`, `payer_id`, `expense_date`, `category`, `activity_id`).
6. `expense_shares`: Detailed breakdown of exact paise amounts owed by each participating user.
7. `settlements`: Settlement payments made between debtor and creditor to clear pending balances.

---

## 4. Key Algorithms (Examiner Viva Highlights)

### A. Exact Equal Split with Zero Loss (`MoneyUtils.splitEqually`)
- **Problem**: When dividing ₹1,000 among 3 participants, floating-point math yields `333.3333...`. Three shares of 333.33 sum to 999.99 (losing 1 paisa).
- **Solution**: Convert the amount to integer minor units (paise):  
  `1000.00 * 100 = 100,000 paise`.
- `base = 100000 / 3 = 33333 paise (₹333.33)`
- `remainder = 100000 % 3 = 1 paisa`
- Distribute remainder paise to the first $N$ participants:
  - Participant 1: `33333 + 1 = 33334 paise (₹333.34)`
  - Participant 2: `33333 paise (₹333.33)`
  - Participant 3: `33333 paise (₹333.33)`
  - **Exact Sum = ₹1,000.00**.

### B. Net Balance Formula
For any user $U$ in a trip:
$$\text{Net Balance}_U = (\text{Total Paid as Payer} - \text{Total Owed in Shares}) + (\text{Settlements Paid} - \text{Settlements Received})$$
- $\text{Net Balance} > 0 \implies$ Creditor (**Gets back** money).
- $\text{Net Balance} < 0 \implies$ Debtor (**Owes** money).
- $\text{Net Balance} = 0 \implies$ **Fully Settled**.

### C. Greedy Debt Simplification Algorithm
Reduces total transactions in a group:
1. Separate users into `debtors` ($\text{net} < 0$) and `creditors` ($\text{net} > 0$).
2. Pair the maximum debtor with the maximum creditor.
3. Settle $\min(\text{debt}, \text{credit})$.
4. Repeat until all debts reach zero.

---

## 5. College Viva Demo Checklist (Follow During Lab Exam)

1. **Launch App**: Open `http://localhost:8080` in Chrome/Edge.
2. **Step 1 (Login/User)**: Show top bar displaying active user **Rahul Sharma (Lead)**.
3. **Step 2 (Create Trip)**: Click "Create Trip" &rarr; Enter "Goa Adventure", Dates, Destination &rarr; Submit.
4. **Step 3 (Add Members)**: Add Priya, Amit, and Sneha to the trip.
5. **Step 4 (Add Activity)**: Add "Seaside Resort Stay" (Category: HOTEL, ₹24,000).
6. **Step 5 (Record Expense)**:
   - Enter "Group Dinner at Wharf", Amount: ₹1,000.
   - Payer: Priya.
   - Select participants: Rahul, Priya, Amit (3 members).
   - Show how the backend creates shares: ₹333.34, ₹333.33, ₹333.33.
7. **Step 6 (Show Balances)**: Open "Balances & Settlements" &rarr; Show net balance table and "Who Owes Whom" cards.
8. **Step 7 (Record Settlement)**: Click "Settle Up Now" &rarr; Amit pays Rahul ₹2,000 &rarr; Balances re-compute in real time!
9. **Step 8 (Dashboard & Charts)**: Open Dashboard &rarr; Show dynamic Chart.js donut chart showing spending by category.
