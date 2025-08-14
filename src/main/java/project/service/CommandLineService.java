package project.service;

import static project.util.PrintUtil.*;

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

import project.cli.SqlCompleter;
import project.database.QueryExecutor;

public class CommandLineService {

	private final Connection connection;
	private final LineReader lineReader;
	private final QueryExecutor queryExecutor;

	public CommandLineService(Connection connection) throws IOException {
		this.connection = connection;
		this.lineReader = createLineReader();
		this.queryExecutor = new QueryExecutor(connection);
	}

	private LineReader createLineReader() throws IOException {
		Terminal terminal = TerminalBuilder.builder()
			.system(true)
			.build();

		return LineReaderBuilder.builder()
			.terminal(terminal)
			.completer(new SqlCompleter(connection))
			.build();
	}

	public void start() {
		while (true) {
			String input;
			try {
				input = lineReader.readLine("sql> ");
			} catch (UserInterruptException e) {
				continue; // Ctrl+C 무시
			} catch (EndOfFileException e) {
				break; // Ctrl+D 종료
			}

			// TODO 리팩토링
			if (input == null || input.trim().isEmpty()) {
				continue;
			}

			if (input.equalsIgnoreCase("exit")) {
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

			// TODO 테스트
			if (input.equalsIgnoreCase("file")) {
				System.out.println("Enter file path: ");
				input = lineReader.readLine();
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

			// TODO 테스트
			if (input.equalsIgnoreCase("toggle autocommit")) {
				QueryExecutor.AUTO_COMMIT = !QueryExecutor.AUTO_COMMIT;
				System.out.println("Autocommit is now " + (QueryExecutor.AUTO_COMMIT ? "ON" : "OFF"));
				continue;
			}

			if (confirmDangerousQuery(input)) {
				queryExecutor.execute(input);
			} else {
				System.out.println("Query cancelled.");
			}
		}
	}

	private boolean confirmDangerousQuery(String sql) {
		String trimmed = sql.trim().toLowerCase();

		// WHERE 없는 DELETE 감지
		if (trimmed.startsWith("delete") && !trimmed.contains("where")) {
			System.out.println("WARNING: This DELETE has no WHERE clause. It will delete ALL rows.");
		}

		// WHERE 없는 UPDATE 감지
		else if (trimmed.startsWith("update") && !trimmed.contains("where")) {
			System.out.println("WARNING: This UPDATE has no WHERE clause. It will update ALL rows.");
		}

		// DROP TABLE 감지
		else if (trimmed.startsWith("drop table")) {
			System.out.println("WARNING: This will DROP a table.");
		} else {
			return true; // 위험하지 않은 쿼리는 확인 없이 바로 실행
		}

		// 사용자 확인
		String answer = lineReader.readLine("Are you sure? (yes/no): ").trim().toLowerCase();
		return answer.equals("yes") || answer.equals("y");
	}
}