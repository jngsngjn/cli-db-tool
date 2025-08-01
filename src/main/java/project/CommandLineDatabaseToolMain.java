package project;

import static project.util.CommonUtil.*;
import static project.util.PrintUtil.*;

import java.sql.Connection;
import java.util.Map;

import project.cli.CommandLineProgram;
import project.database.DBConnectionManager;
import project.util.ConsoleUtil;

public class CommandLineDatabaseToolMain {
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

		// 비밀번호 미입력 시 숨김 모드로 입력
		if (password == null) {
			password = ConsoleUtil.readPassword();
		}

		try (Connection connection = DBConnectionManager.connect(driver, url, username, password)) {
			System.out.println("Connected to database successfully!");
			printUsage();
			new CommandLineProgram(connection).start();
		} catch (Exception e) {
			System.err.println("Connection failed: " + e.getMessage());
		} finally {
			printGoodBye();
		}
	}
}