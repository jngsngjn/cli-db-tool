# SQL CLI – Multi‑database Command‑Line Client (Java)

A lightweight, single‑JAR SQL client you can run from the terminal. It connects to **PostgreSQL**, **MySQL**, **Oracle**, and **Microsoft SQL Server**, gives you an interactive prompt with **auto‑completion**, prints results in a **pretty table** (with correct width for wide characters like Korean), and adds **safety prompts** for destructive statements.

> Built with Java 21, JLine 3, and the official JDBC drivers.

---

## Features

- **Interactive REPL** powered by JLine (multi‑line input, history, Ctrl+D to exit).
- **Auto‑completion** for table and column names using live database metadata.
- **Pretty table output** that aligns columns and accounts for double‑width characters.
- **Safety checks** that detect:
    - `DELETE` without `WHERE`
    - `UPDATE` without `WHERE`
    - `DROP TABLE`
      …and ask for confirmation before executing.
- **Run scripts**: execute a `.sql` file containing multiple statements separated by semicolons.
- **Autocommit toggle**: quickly switch autocommit on/off.
- **Password prompt**: omit `--password` to be securely prompted at runtime.

---

## Supported databases

| DB | JDBC driver (pom) | Example JDBC URL |
|---|---|---|
| PostgreSQL | `org.postgresql:postgresql:42.7.3` | `jdbc:postgresql://localhost:5432/mydb` |
| MySQL | `com.mysql:mysql-connector-j:9.0.0` | `jdbc:mysql://localhost:3306/mydb?useSSL=false` |
| Oracle | `com.oracle.database.jdbc:ojdbc11:23.4.0.24.05` | `jdbc:oracle:thin:@//localhost:1521/ORCLPDB1` |
| SQL Server | `com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre11` | `jdbc:sqlserver://localhost:1433;databaseName=mydb` |

> **Note:** You only need the driver for the database(s) you actually connect to. Maven downloads them automatically when you build.

---

## Requirements

- **Java 21+**
- **Maven 3.9+** (to build the fat jar)

---

## Build

```bash
mvn -q -DskipTests package
```

The build produces a runnable JAR in `target/`. Depending on your Maven/shade settings it may be named:

- `cli-db-tool-1.0-SNAPSHOT.jar`, **or**
- `cli-db-tool-1.0-SNAPSHOT-shaded.jar`

Use whichever exists in `target/`.

---

## Quick start

### 1) PostgreSQL

```bash
java -jar target/cli-db-tool-1.0-SNAPSHOT.jar \
  --driver postgres \
  --url jdbc:postgresql://localhost:5432/mydb \
  --username myuser
# You will be prompted for the password if --password is omitted
```

### 2) MySQL

```bash
java -jar target/cli-db-tool-1.0-SNAPSHOT.jar \
  --driver mysql \
  --url "jdbc:mysql://localhost:3306/mydb?useSSL=false" \
  --username myuser --password secret
```

### 3) Oracle

```bash
java -jar target/cli-db-tool-1.0-SNAPSHOT.jar \
  --driver oracle \
  --url "jdbc:oracle:thin:@//localhost:1521/ORCLPDB1" \
  --username myuser
```

### 4) SQL Server

```bash
java -jar target/cli-db-tool-1.0-SNAPSHOT.jar \
  --driver mssql \
  --url "jdbc:sqlserver://localhost:1433;databaseName=mydb" \
  --username myuser
```

**CLI options**

- `--driver` (**required**): one of `postgres`, `mysql`, `oracle`, `mssql`
- `--url` (**required**): JDBC URL
- `--username` (**required**)
- `--password` (optional): if omitted you’ll be prompted securely

---

## Interactive commands

At the `sql>` prompt you can use:

| Command | Description |
|---|---|
| `help` | Show a quick help/usage panel. |
| `file <path.sql>` | Execute SQL statements from a file (semicolon `;` separated). |
| `toggle autocommit` | Toggle autocommit on/off (default: **ON**). |
| `clear` | Clear the screen. |
| `exit` | Exit the program (or press **Ctrl+D**). |

**Notes on scripts** (`file <path.sql>`):

- Statements are split on the semicolon (`;`).
- A trailing statement without a final semicolon will still be executed.
- Complex vendor‑specific blocks (e.g., PL/SQL `BEGIN … END;`) may not be parsed as a single unit.

---

## Output formatting

- Results are printed as a fixed‑width table with a header and separators.
- Column widths adapt to the largest cell in each column.
- Double‑width characters (e.g., Korean) are handled correctly so columns stay aligned.

---

## Project structure

```
src/main/java/project/
├─ CommandLineDatabaseToolMain.java     # parses args, opens the connection, starts the REPL
├─ cli/
│  ├─ CommandLineProgram.java           # interactive loop, commands, confirmations
│  └─ SqlCompleter.java                 # JLine completer (tables/columns)
├─ code/
│  └─ Command.java                      # built‑in commands enum (HELP, EXIT, FILE, TOGGLE_AUTOCOMMIT)
├─ database/
│  ├─ DBConnectionManager.java          # loads JDBC drivers, creates connections
│  └─ QueryExecutor.java                # runs queries and .sql files, prints results
└─ util/
   ├─ CommonUtil.java                   # command‑line arg parsing (--key value)
   ├─ ConsoleUtil.java                  # password prompt (System.console fallback)
   └─ PrintUtil.java                    # usage/help and table rendering
```

---

## Troubleshooting

- **"Unsupported driver"** – Check the `--driver` flag. Must be `postgres`, `mysql`, `oracle`, or `mssql`.
- **"Connection failed"** – Verify host/port, database name, credentials, and whether the DB accepts TCP connections.
- **SSL/Timezone parameters** – Add them in your JDBC URL (e.g., `?sslmode=require` for PostgreSQL).
- **Oracle service name vs SID** – Use the correct URL form for your environment.

---