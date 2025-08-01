package project.database;

import static project.util.PrintUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QueryExecutor {

	private final Connection connection;
	public static boolean AUTO_COMMIT = true;

	public QueryExecutor(Connection connection) {
		this.connection = connection;
	}

	public void execute(String sql) {
		try (Statement stmt = connection.createStatement()) {
			boolean isResultSet = stmt.execute(sql);

			if (isResultSet) {
				try (ResultSet rs = stmt.getResultSet()) {
					printResultSet(rs);
				}
			} else {
				int updated = stmt.getUpdateCount();
				System.out.println("Query OK, " + updated + " row(s) affected.");
			}
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
		}
	}

	public void executeFile(Path filePath) {
		try {
			List<String> lines = Files.readAllLines(filePath);
			StringBuilder queryBuffer = new StringBuilder();

			for (String line : lines) {
				String trimmed = line.trim();

				// Skip empty lines or SQL comments
				if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#")) {
					continue;
				}

				queryBuffer.append(trimmed).append(" ");

				// If the line ends with a semicolon, execute the statement
				if (trimmed.endsWith(";")) {
					String query = queryBuffer.toString().trim();
					query = query.substring(0, query.length() - 1); // remove trailing ;
					if (!query.isEmpty()) {
						System.out.println("Executing: " + query);
						execute(query);
					}
					queryBuffer.setLength(0); // clear buffer
				}
			}

			// If there's a leftover query (no trailing semicolon), execute it
			if (queryBuffer.length() > 0) {
				String query = queryBuffer.toString().trim();
				if (!query.isEmpty()) {
					System.out.println("Executing: " + query);
					execute(query);
				}
			}

		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage());
		}
	}
}