# 🎓 CampusERP — a mini ERP for a college

A small, working **Enterprise Resource Planning (ERP)** system for a college,
built with **Java + Spring Boot**. One app that keeps the five things a college
office juggles every day in one place:

| Module | What it does |
|---|---|
| **Students** | Student records — name, roll no, course, semester, contact |
| **Fees** | Fee records per student, partial payments, amount still due |
| **Attendance** | Mark present / absent / late per subject per day, % summary per student |
| **Timetable** | Weekly class slots — day, time, subject, faculty, room |
| **Inventory** | Lab / library / furniture stock — quantity, location, condition |

## Why this was built

Real college ERPs (the software that runs admissions, fees and attendance) are
huge, but the *ideas* inside them are simple and very learnable: a database
table per thing, a REST API on top, and rules about **who is allowed to do
what**. CampusERP is that idea at its smallest honest size — every feature
actually works, nothing is a stub, and the code is commented so a BTech
student can read it top to bottom and understand a real Spring Boot app.

## Tech stack

| Layer | Choice | Why |
|---|---|---|
| Language | **Java 21** (LTS) | The language of most college/enterprise backends |
| Framework | **Spring Boot 3.3** | Embedded Tomcat, auto-configuration, minimal boilerplate |
| API | **Spring Web (REST)** | JSON endpoints, testable with `curl` |
| Data | **Spring Data JPA + Hibernate** | Repository interfaces — no hand-written SQL |
| Database | **H2** (file mode, default) | Zero setup; data survives restarts in `./data/` |
| Security | **Spring Security, HTTP Basic** | 3 roles, in-memory demo users |
| Frontend | **Plain HTML/CSS/JS** in `src/main/resources/static/` | No build step, no framework to learn first |
| Tests | **JUnit 5 + MockMvc** | Boots the app, checks seed data + role rules |
| Build | **Maven** | `mvn spring-boot:run` |

## Folder structure

```
campus-erp/
├── pom.xml                                  # dependencies + Java 21
├── src/main/java/com/campuserp/
│   ├── CampusErpApplication.java            # entry point (main method)
│   ├── config/SecurityConfig.java           # roles + demo users + URL rules
│   ├── model/                               # JPA entities (database tables)
│   │   ├── Student.java  Fee.java  Attendance.java
│   │   ├── TimetableEntry.java  InventoryItem.java
│   ├── repository/                          # Spring Data interfaces (auto queries)
│   │   ├── StudentRepository.java  FeeRepository.java  ...
│   ├── controller/                          # REST endpoints
│   │   ├── StudentController.java  FeeController.java
│   │   ├── AttendanceController.java  TimetableController.java
│   │   ├── InventoryController.java  DashboardController.java
│   └── seed/DataSeeder.java                 # seed data on first run
├── src/main/resources/
│   ├── application.properties               # H2 config + MySQL switch (commented)
│   └── static/  index.html  app.js  styles.css   # the web UI
└── src/test/java/com/campuserp/
    └── CampusErpApplicationTests.java        # 5 smoke tests
```

**How a request flows** (the most important thing to learn here):

```
Browser / curl  →  SecurityConfig (are you logged in? is your role allowed?)
                →  Controller (e.g. StudentController — the URL + HTTP method)
                →  Repository (e.g. StudentRepository — findAll(), save(), ...)
                →  Hibernate / JPA (turns it into SQL)
                →  H2 database file (./data/campuserp.mv.db)
```

## How to run

**Prerequisites:** Java 21+ and Maven 3.9+. Check with `java -version` and `mvn -version`.

```bash
# 1. Go to the project
cd campus-erp

# 2. Run it (downloads dependencies the first time — needs internet)
mvn spring-boot:run

# 3. Open the UI
#    http://localhost:8080
```

No database to install, no `.env` file, no extra setup. The first run seeds
the database automatically (8 students, 8 fee records, 16 attendance rows,
15 timetable slots, 8 inventory items) and the H2 file is created in `./data/`.

**Demo logins** (in-memory users, see `SecurityConfig.java`):

| User | Password | Role | Can do |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | Everything |
| `faculty` | `faculty123` | FACULTY | Read all; mark attendance, edit timetable |
| `student` | `student123` | STUDENT | Read-only |

## Demo in 60 seconds

1. Run `mvn spring-boot:run`, open http://localhost:8080, log in as `admin / admin123`.
2. The dashboard cards show the seed data: **8 students, ₹2,91,000 fees due, 15 timetable slots**.
3. Click **Fees** — Priya Patel shows UNPAID ₹85,000.
4. Click **Attendance** → try in a terminal:
   ```bash
   curl -s -u student:student123 http://localhost:8080/api/attendance/summary/1
   # {"studentId":1,"total":4,"present":4,"late":0,"absent":0,"percentage":100.0}
   ```
5. Prove the role rules work — a student **cannot** create a student:
   ```bash
   curl -s -o /dev/null -w "%{http_code}\n" -u student:student123 -X POST \
     -H "Content-Type: application/json" \
     -d '{"name":"Test","rollNo":"T-1","email":"t@college.edu","course":"BTech CSE","semester":6}' \
     http://localhost:8080/api/students
   # 403
   ```

## API reference

All `/api/**` endpoints need HTTP Basic auth. Replace `admin:admin123` with
any demo user.

```bash
BASE=http://localhost:8080

# Students
curl -s -u admin:admin123 $BASE/api/students
curl -s -u admin:admin123 "$BASE/api/students?course=BTech%20CSE&semester=6"
curl -s -u admin:admin123 $BASE/api/students/1
curl -s -u admin:admin123 -X POST -H "Content-Type: application/json" \
  -d '{"name":"Test User","rollNo":"CSE-2023-009","email":"test@college.edu","course":"BTech CSE","semester":6}' \
  $BASE/api/students

# Fees (+ record a payment)
curl -s -u admin:admin123 "$BASE/api/fees?status=UNPAID"
curl -s -u admin:admin123 -X POST -H "Content-Type: application/json" \
  -d '{"amount":10000}' $BASE/api/fees/3/pay

# Attendance (+ % summary)
curl -s -u faculty:faculty123 $BASE/api/attendance?studentId=1
curl -s -u faculty:faculty123 $BASE/api/attendance/summary/1
curl -s -u faculty:faculty123 -X POST -H "Content-Type: application/json" \
  -d '{"student":{"id":1},"date":"2026-11-24","subject":"DBMS","status":"PRESENT"}' \
  $BASE/api/attendance

# Timetable
curl -s -u admin:admin123 "$BASE/api/timetable?dayOfWeek=MONDAY"
curl -s -u admin:admin123 "$BASE/api/timetable?course=BTech%20CSE&semester=6"

# Inventory
curl -s -u admin:admin123 "$BASE/api/inventory?category=LAB"

# Dashboard + who am I
curl -s -u admin:admin123 $BASE/api/dashboard
curl -s -u admin:admin123 $BASE/api/me
```

Also useful: the **H2 console** at http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:file:./data/campuserp`, user `sa`, empty password) —
you can run SQL against the live database and see the tables Hibernate made.

## Tests

```bash
mvn test
```

5 tests: the app boots, unauthenticated API calls get 401, seed data loads
(8 students), a student-role user gets 403 on create, and the dashboard
counts match the seed (8 students, 15 timetable entries). Tests run against
their own throwaway in-memory H2 database
(`src/test/resources/application.properties`), never the `./data/` file the
app uses — so running tests can never pollute your real data.

## Switching to MySQL

H2 is the default because it needs no installation. To use MySQL instead:

1. Create the database: `CREATE DATABASE campuserp;`
2. In `src/main/resources/application.properties`, comment out the 4 H2
   `spring.datasource.*` lines and uncomment the MySQL block (it is already
   written there — just swap which lines are commented).
3. Add the MySQL driver to `pom.xml`:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
4. Set `spring.h2.console.enabled=false` and restart. No Java code changes —
   JPA/Hibernate generates the right SQL for either database. That is the
   whole point of using JPA: **the code does not care which database it is.**

## Explain this in an interview

- **"What did you build?"** — A mini college ERP in Spring Boot: five modules
  (students, fees, attendance, timetable, inventory) behind REST APIs, with
  role-based access (Admin / Faculty / Student) and a plain HTML/JS frontend.
- **"How does data flow?"** — Controller → Spring Data JPA repository →
  Hibernate → database. Repositories are interfaces; Spring generates the
  queries from method names like `findByStudentId`, so there is no hand-written
  SQL anywhere.
- **"How does security work?"** — Spring Security with HTTP Basic auth and
  URL + method rules: students read-only, faculty can mark attendance and
  edit the timetable, only admins create/delete students, fees and inventory.
- **"Why H2, and what would change for MySQL?"** — H2 file mode for zero-setup
  demos; switching is a config-only change (datasource URL + driver) because
  JPA abstracts the database away. H2 is *not* a production choice.
- **"What would you add next?"** — Database-backed users with BCrypt passwords
  instead of in-memory demo users, JWT/session login instead of HTTP Basic,
  pagination on list endpoints, and fee receipts (PDF) — in that order.

## Honest limits — what it does NOT do

- **Demo users only.** Logins are hard-coded in memory (`SecurityConfig`);
  there is no sign-up, no password reset, no user table. Do not deploy this
  as-is with real users.
- **HTTP Basic auth, no sessions/JWT.** Credentials travel with every request;
  fine on localhost, not how a production app logs people in. No HTTPS.
- **H2, not MySQL, by default.** The H2 file is single-user and lives on disk
  next to the app; it is a learning/demo database, not a production one.
  The MySQL switch above is config-only and documented.
- **No pagination.** List endpoints return every row — fine for 8 students,
  wrong for 8,000. A real version would add `Pageable`.
- **No email/SMS, no payments, no PDF receipts.** Fees are records, not a
  payment gateway. No exam module, no admissions, no hostel/transport.
- **No frontend framework.** The UI is intentionally plain HTML/JS so the
  backend is the thing being learned; it is functional, not pretty.

## License

MIT — see [LICENSE](LICENSE).
