package project.util;

import java.io.Console;
import java.util.Scanner;

import org.jline.reader.EndOfFileException;

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

	/** 터미널 화면 지우기 (대부분의 ANSI 터미널에서 동작) */
	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	/** 종료 처리: 필요 시 리소스 정리 후 System.exit 대신 루프 종료 유도 */
	public static void safeExit() {
		// 연결/리더 정리 필요 시 여기에 수행
		// 예: closeQuietly(connection);
		// 여기서는 단순히 루프 종료를 위해 예외 사용 없이 플로우 제어
		throw new EndOfFileException(); // start()의 catch에서 깔끔히 종료
	}
}