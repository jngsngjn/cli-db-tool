package tool.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import tool.db.QueryExecutor;
import tool.util.SqlCompleter;

public class CliRunner {

	private final Connection conn;
	private final QueryExecutor queryExecutor;

	public CliRunner(Connection conn) {
		this.conn = conn;
		this.queryExecutor = new QueryExecutor(conn);
	}

	public void start() throws IOException {
		Terminal terminal = TerminalBuilder.builder()
			.system(true)
			.build();

		LineReader reader = LineReaderBuilder.builder()
			.terminal(terminal)
			.completer(new SqlCompleter(conn))
			.build();

		printUsage();

		while (true) {
			String input;
			try {
				input = reader.readLine("sql> ");
			} catch (UserInterruptException e) {
				continue; // Ctrl+C 무시
			} catch (EndOfFileException e) {
				printGoodBye();
				break; // Ctrl+D 종료
			}

			if (input == null || input.trim().isEmpty()) {
				continue;
			}

			if (input.equalsIgnoreCase("exit")) {
				printGoodBye();
				break;
			}

			if (input.equalsIgnoreCase("clear")) {
				System.out.print("\033[H\033[2J");
				System.out.flush();
				continue;
			}

			if (input.equalsIgnoreCase("help")) {
				printUsage();
				continue;
			}

			if (input.equalsIgnoreCase("file")) {
				System.out.println("Enter file path: ");
				input = reader.readLine();
				if (input == null || input.trim().isEmpty()) {
					System.out.println("Empty file path..");
					continue;
				}

				input = input.trim();
				Path filePath = Paths.get(input);

				if (!Files.exists(filePath)) {
					System.out.println("File not found: " + input);
					continue;
				}

				queryExecutor.executeFile(filePath);
				continue;
			}

			if (input.equalsIgnoreCase("toggle autocommit")) {
				QueryExecutor.AUTO_COMMIT = !QueryExecutor.AUTO_COMMIT;
				System.out.println("Autocommit is now " + (QueryExecutor.AUTO_COMMIT ? "ON" : "OFF"));
				continue;
			}

			if (confirmDangerousQuery(input, reader)) {
				queryExecutor.execute(input);
			} else {
				System.out.println("Query cancelled.");
			}
		}
	}

	private static void printGoodBye() {
		System.out.println("Exiting Program... Goodbye");
	}

	private void printUsage() {
		System.out.println("====================================");
		System.out.println(" Welcome to SQL CLI ");
		System.out.println(" Commands:");
		System.out.println("  clear        - Clear the screen");
		System.out.println("  exit         - Exit the program");
		System.out.println("  help         - Show this help message");
		System.out.println("  file         - Execute query from a file");
		System.out.println("  toggle autocommit - Toggle autocommit. Default is ON");
		System.out.println();
		System.out.println(" Shortcuts:");
		System.out.println("  Ctrl+D       - Exit the program");
		System.out.println("====================================");
	}

	private boolean confirmDangerousQuery(String sql, LineReader reader) {
		String trimmed = sql.trim().toLowerCase();

		// TODO WHERE 없는 UPDATE

		// WHERE 없는 DELETE 감지
		if (trimmed.startsWith("delete") && !trimmed.contains("where")) {
			System.out.println("WARNING: This DELETE has no WHERE clause. It will delete ALL rows.");
		}
		// DROP TABLE 감지
		else if (trimmed.startsWith("drop table")) {
			System.out.println("WARNING: This will DROP a table.");
		} else {
			return true; // 위험하지 않은 쿼리는 확인 없이 바로 실행
		}

		// 사용자 확인
		String answer = reader.readLine("Are you sure? (yes/no): ").trim().toLowerCase();
		return answer.equals("yes") || answer.equals("y");
	}
}