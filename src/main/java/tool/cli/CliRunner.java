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

			queryExecutor.execute(input);
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
}