package project.database;

import static java.sql.DriverManager.*;

import java.sql.Connection;

public class DBConnectionManager {

	public static Connection connect(String driver, String url, String username, String password) throws Exception {
		switch (driver.toLowerCase()) {
			case "postgres":
				Class.forName("org.postgresql.Driver");
				break;
			case "mysql":
				Class.forName("com.mysql.cj.jdbc.Driver");
				break;
			case "oracle":
				Class.forName("oracle.jdbc.driver.OracleDriver");
				break;
			case "mssql":
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				break;
			case "mariadb":
				Class.forName("org.mariadb.jdbc.Driver");
				break;
			case "db2":
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				break;
			default:
				throw new IllegalArgumentException("Unsupported driver: " + driver);
		}
		return getConnection(url, username, password);
	}
}