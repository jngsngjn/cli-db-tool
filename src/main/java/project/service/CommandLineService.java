package project.service;

import static project.util.ConsoleUtil.*;
import static project.util.ConsoleUtil.clearScreen;
import static project.util.PrintUtil.*;
import static project.util.StringUtil.*;

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

	private static final String CMD_EXIT = "exit";
	private static final String CMD_CLEAR = "clear";
	private static final String CMD_HELP = "help";
	private static final String CMD_FILE = "file";
	private static final String CMD_TOGGLE_AUTOCOMMIT = "toggle autocommit";

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
				// Ctrl+C: 입력 취소만, 루프는 유지
				continue;
			} catch (EndOfFileException e) {
				// Ctrl+D: 종료
				break;
			}

			if (isBlank(input)) {
				continue;
			}

			// 명령 처리: 명령이면 true, 아니면 false
			if (handleCommand(input.trim())) {
				continue;
			}

			// 명령이 아니면 SQL로 간주
			if (confirmDangerousQuery(input)) {
				queryExecutor.execute(input);
			} else {
				System.out.println("Query cancelled.");
			}
		}
	}

	/**
	 * 입력이 사전 정의된 명령인지 판별하고, 해당 로직을 수행한다.
	 * 명령이 아니면 false 반환하여 SQL 흐름으로 넘긴다.
	 */
	private boolean handleCommand(String input) {
		String normalized = input.toLowerCase();

		return switch (normalized) {
			case CMD_EXIT -> {
				safeExit();
				yield true;
			}
			case CMD_CLEAR -> {
				clearScreen();
				yield true;
			}
			case CMD_HELP -> {
				printUsage();
				yield true;
			}
			case CMD_FILE -> {
				handleFileCommand();
				yield true;
			}
			case CMD_TOGGLE_AUTOCOMMIT -> {
				toggleAutocommit();
				yield true;
			}
			default -> false; // 알려진 명령이 아니면 SQL로 처리
		};
	}

	/** 파일 실행 명령 처리 */
	private void handleFileCommand() {
		System.out.println("Enter file path: ");
		String pathInput = lineReader.readLine();

		if (isBlank(pathInput)) {
			System.out.println("Empty file path.");
			return;
		}

		Path filePath = Paths.get(pathInput.trim());
		if (!Files.exists(filePath)) {
			System.out.println("File not found: " + filePath);
			return;
		}

		queryExecutor.executeFile(filePath);
	}

	/** 오토커밋 토글 및 상태 출력 */
	private void toggleAutocommit() {
		QueryExecutor.AUTO_COMMIT = !QueryExecutor.AUTO_COMMIT;
		System.out.println("Autocommit is now " + (QueryExecutor.AUTO_COMMIT ? "ON" : "OFF"));
		// 실제 JDBC 커넥션 적용은 execute()에서 동기화되도록 이미 반영한 상태를 가정
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