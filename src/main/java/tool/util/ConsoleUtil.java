package tool.util;

import java.io.Console;
import java.util.Scanner;

public class ConsoleUtil {

	public static String readPassword(String prompt) {
		Console console = System.console();

		if (console != null) {
			char[] passwordChars = console.readPassword(prompt);
			return new String(passwordChars);
		} else {
			// IDE 실행 시 System.console()이 null일 수 있으므로 대체
			System.out.print(prompt);
			Scanner scanner = new Scanner(System.in);
			return scanner.nextLine();
		}
	}
}