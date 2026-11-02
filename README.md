# TripSplit — Smart Group Trip Expense Management System
### A 4-Member Java Spring Boot Mini Project for College Submission

TripSplit is a full-stack Java web application designed to simplify group expense tracking during shared trips. It features automatic equal expense splitting with zero-loss paise precision, net balance calculation, debt simplification, settlement recording, and an interactive visual dashboard with category analytics.

---

## 🛠 Tech Stack

- **Backend**: Java 17 / 21, Spring Boot 3.2.5
- **ORM & Database**: Spring Data JPA / Hibernate, MySQL 8.x (with optional instant H2 in-memory profile)
- **Validation**: Jakarta Bean Validation (`@Valid`, Hibernate Validator)
- **Frontend**: HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3, Bootstrap Icons, Chart.js
- **Build Tool**: Apache Maven

---

## 🚀 Quick Start Guide

### Prerequisites
1. **JDK 17 or 21** installed on your system.
2. **Maven 3.8+** (or run through your IDE like IntelliJ IDEA, Eclipse, VS Code, NetBeans).
3. **MySQL 8.x** running on port 3306 (or simply run with the H2 profile for instant zero-config testing).

---

### Step 1: Set up MySQL Database (Default Profile)

Open MySQL Workbench or your MySQL terminal and run:

```sql
SOURCE database/schema.sql;
SOURCE database/data.sql;
```

*(Alternatively, copy and paste the contents of `database/schema.sql` and `database/data.sql` into MySQL Workbench and execute).*

Configure your MySQL credentials in `src/main/resources/application-mysql.properties` or via environment variables:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tripsplit_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

---

### Step 2: Run the Application

#### Option A: Running with Maven
```bash
# Run with MySQL:
mvn spring-boot:run

# OR run with zero-config in-memory H2 database (Perfect for college lab demos):
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

#### Option B: Running in IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the project folder in your IDE.
2. Locate `src/main/java/com/tripsplit/TripSplitApplication.java`.
3. Right-click &rarr; **Run 'TripSplitApplication'**.

---

### Step 3: Access the Application

Once started, open your web browser and navigate to:
- **Web UI & Dashboard**: [http://localhost:8080](http://localhost:8080)
- **Health Check API**: [http://localhost:8080/api/health](http://localhost:8080/api/health)
- **Trips API**: [http://localhost:8080/api/trips](http://localhost:8080/api/trips)
- **H2 Web Console** (if using H2 profile): [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:tripsplit_db`, User: `sa`, Password: empty)

---

## 👥 4-Member Work Split & Package Ownership

As prescribed in the project specification:

| Member & Role | Package | Responsibilities |
|---|---|---|
| **Member 1 — Lead / Integration** | `com.tripsplit.common`<br>`com.tripsplit.settlement` | Core architecture, database schema integration, deterministic paise splitting logic (`MoneyUtils`), settlement calculation & simplified debt engine. |
| **Member 2 — Trips & Activities** | `com.tripsplit.trip`<br>`com.tripsplit.activity` | Trip CRUD (`/api/trips`), membership validation, activity itinerary management (`/api/activities`). |
| **Member 3 — Expenses** | `com.tripsplit.expense` | Expense entry workflow, payer & participant validation, transactional persistence (`expense_shares`) with zero penny loss. |
| **Member 4 — Auth & Dashboard UI** | `com.tripsplit.auth`<br>`com.tripsplit.dashboard` | User authentication & session switching, dashboard metrics aggregation, Chart.js category visualizations, Bootstrap 5 UI shell. |

---

## 📋 8-Step Viva Demonstration Checklist

1. **User Authentication**: Select or switch active user (e.g. Rahul Sharma).
2. **Create Trip**: Add a new trip (destination, start & end dates).
3. **Add Trip Members**: Link at least 2 other members to the trip.
4. **Create Activities**: Add hotel, dining, or sightseeing activities.
5. **Record Expense with Equal Split**: Record ₹1,000 dinner paid by Priya split among 3 members (333.34 + 333.33 + 333.33 = exactly 1,000.00).
6. **View Computed Balances**: Verify net balance calculation and simplified debt cards ("Amit owes Rahul").
7. **Record Settlement**: Click "Settle Up" to record a payment and observe balance recalculation.
8. **View Dashboard Charts**: Inspect total group spend and category breakdown charts.

---

## 📁 Project Structure

```
javaMiniProject/
├── pom.xml                               # Maven project definition with Spring Boot 3.x
├── README.md                             # Setup and execution guide
├── API_CONTRACT.md                       # Complete REST API specification
├── COLLEGE_PROJECT_GUIDE.md              # Viva guide, ER relationships & algorithm explanation
├── database/
│   ├── schema.sql                        # DDL script for 7 MySQL tables
│   └── data.sql                          # Seed data for demo and viva
└── src/
    ├── main/
    │   ├── java/com/tripsplit/
    │   │   ├── TripSplitApplication.java # Spring Boot main entry point & /api/health
    │   │   ├── common/                   # Global responses, exceptions & MoneyUtils
    │   │   ├── auth/                     # User entity, AuthService & AuthController
    │   │   ├── trip/                     # Trip & TripMember entities, Service & Controller
    │   │   ├── activity/                 # Activity entity, Service & Controller
    │   │   ├── expense/                  # Expense & ExpenseShare entities, Service & Controller
    │   │   ├── settlement/               # Settlement entity, Balances & Debt simplification
    │   │   └── dashboard/                # Aggregations, Chart DTOs & DashboardController
    │   └── resources/
    │       ├── application.properties    # Base configuration
    │       ├── application-mysql.properties # MySQL configuration
    │       ├── application-h2.properties # In-memory H2 fallback configuration
    │       └── static/                   # Bootstrap 5 Web UI (HTML, CSS, JS, Chart.js)
    └── test/java/com/tripsplit/
        └── TripSplitApplicationTests.java # Unit tests for paise splitting precision
```

