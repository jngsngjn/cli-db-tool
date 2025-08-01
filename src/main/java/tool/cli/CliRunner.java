package tool.cli;

import java.io.IOException;
import java.sql.Connection;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import tool.db.QueryExecutor;

public class CliRunner {

	private final QueryExecutor queryExecutor;

	public CliRunner(Connection conn) {
		this.queryExecutor = new QueryExecutor(conn);
	}

	public void start() throws IOException {
		Terminal terminal = TerminalBuilder.builder()
			.system(true)
			.build();

		LineReader reader = LineReaderBuilder.builder()
			.terminal(terminal)
			.build();

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

			if (sql.equalsIgnoreCase("clear")) {

			}

			if (sql.equalsIgnoreCase("help")) {

			}

			if (sql.equalsIgnoreCase("history")) {

			}

			if (sql.equalsIgnoreCase("show tables")) {

			}

			if (sql.equalsIgnoreCase("file")) {

			}

			queryExecutor.execute(sql);
		}
	}
}