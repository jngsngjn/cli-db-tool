package project.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PrintUtil {

	private PrintUtil() {
	}

	public static void printUsage() {
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

	public static void printGoodBye() {
		System.out.println("Exiting Program... Goodbye");
	}

	public static void printResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();

		// 1. 컬럼 폭 계산
		int[] columnWidths = new int[columnCount];
		for (int i = 1; i <= columnCount; i++) {
			columnWidths[i - 1] = getDisplayWidth(meta.getColumnName(i));
		}

		List<String[]> rows = new ArrayList<>();
		while (rs.next()) {
			String[] row = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				String value = rs.getString(i);
				if (value == null) value = "NULL";
				row[i - 1] = value;
				columnWidths[i - 1] = Math.max(columnWidths[i - 1], getDisplayWidth(value));
			}
			rows.add(row);
		}

		// 2. 헤더 출력
		for (int i = 1; i <= columnCount; i++) {
			System.out.print(padRight(meta.getColumnName(i), columnWidths[i - 1]) + " | ");
		}
		System.out.println();
		System.out.println("-".repeat(Arrays.stream(columnWidths).sum() + (3 * columnCount)));

		// 3. 데이터 출력
		for (String[] row : rows) {
			for (int i = 0; i < columnCount; i++) {
				System.out.print(padRight(row[i], columnWidths[i]) + " | ");
			}
			System.out.println();
		}
	}

	// 한글 폭 계산
	private static int getDisplayWidth(String s) {
		int width = 0;
		for (char c : s.toCharArray()) {
			width += (c > 127) ? 2 : 1; // 한글은 2칸
		}
		return width;
	}

	// 오른쪽 패딩
	private static String padRight(String s, int width) {
		int padSize = width - getDisplayWidth(s);
		return s + " ".repeat(Math.max(0, padSize));
	}
}