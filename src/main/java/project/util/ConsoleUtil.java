package project.util;

import java.io.Console;
import java.util.Scanner;

public final class ConsoleUtil {

	private ConsoleUtil() {
	}

	private static final String ENTER_PASSWORD_PROMPT = "Enter password: ";

	public static String readPassword() {
		Console console = System.console();

		if (console != null) {
			char[] passwordChars = console.readPassword(ENTER_PASSWORD_PROMPT);
			return new String(passwordChars);
		} else {
			// IDE 실행 시 System.console()이 null일 수 있으므로 대체
			System.out.print(ENTER_PASSWORD_PROMPT);
			Scanner scanner = new Scanner(System.in);
			return scanner.nextLine();
		}
	}
}