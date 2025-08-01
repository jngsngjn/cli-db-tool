package tool.cli;

import java.sql.Connection;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import tool.db.QueryExecutor;

public class CliRunner {

	private final QueryExecutor queryExecutor;

	public CliRunner(Connection conn) {
		this.queryExecutor = new QueryExecutor(conn);
	}

	public void start() {
		LineReader reader = LineReaderBuilder.builder().build();

		while (true) {
			String sql;
			try {
				sql = reader.readLine("sql> ");
			} catch (UserInterruptException e) {
				continue; // Ctrl+C 무시
			} catch (EndOfFileException e) {
				break; // Ctrl+D 종료
			}

			if (sql == null || sql.trim().isEmpty()) {
				continue;
			}

			if (sql.equalsIgnoreCase("exit")) {
				break;
			}

			queryExecutor.execute(sql);
		}
	}
}