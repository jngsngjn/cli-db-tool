package tool;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import tool.cli.CliRunner;
import tool.db.DBConnectionManager;
import tool.util.ConsoleUtil;

public class App {
	public static void main(String[] args) {
		Map<String, String> options = parseArgs(args);

		String driver = options.get("driver");
		String url = options.get("url");
		String username = options.get("username");
		String password = options.get("password");

		if (driver == null || url == null || username == null) {
			System.out.println("Usage: --driver <driver> --url <jdbc-url> --username <username> [--password <password>]");
			System.out.println("Supported drivers: postgres, mysql, oracle, mssql");
			return;
		}

		// 보안 강화: 비밀번호 미입력 시 숨김 모드로 입력
		if (password == null) {
			password = ConsoleUtil.readPassword("Enter password: ");
		}

		try (Connection conn = DBConnectionManager.connect(driver, url, username, password)) {
			System.out.println("Connected to database successfully!");
			new CliRunner(conn).start();
		} catch (Exception e) {
			System.err.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static Map<String, String> parseArgs(String[] args) {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--") && i + 1 < args.length) {
				map.put(args[i].substring(2), args[i + 1]);
				i++;
			}
		}
		return map;
	}
}