package tool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tool.util.ResultPrinter;

public class QueryExecutor {

	private final Connection connection;

	public QueryExecutor(Connection connection) {
		this.connection = connection;
	}

	public void execute(String sql) {
		try (Statement stmt = connection.createStatement()) {
			boolean isResultSet = stmt.execute(sql);

			if (isResultSet) {
				try (ResultSet rs = stmt.getResultSet()) {
					ResultPrinter.printResultSet(rs);
				}
			} else {
				int updated = stmt.getUpdateCount();
				System.out.println("Query OK, " + updated + " row(s) affected.");
			}
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
		}
	}
}