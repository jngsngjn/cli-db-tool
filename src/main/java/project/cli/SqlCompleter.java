package project.cli;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

// TODO 테스트
public class SqlCompleter implements Completer {

	private final Connection connection;
	private final List<String> keywords = Arrays.asList(
		"SELECT", "INSERT", "UPDATE", "DELETE",
		"CREATE", "ALTER", "DROP", "TRUNCATE",
		"FROM", "WHERE", "JOIN", "ORDER", "GROUP",
		"LIMIT", "VALUES", "SET"
	);

	public SqlCompleter(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		try {
			// SQL 키워드 추가
			for (String keyword : keywords) {
				candidates.add(new Candidate(keyword));
			}

			// 테이블명 자동완성
			DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
				while (tables.next()) {
					candidates.add(new Candidate(tables.getString("TABLE_NAME")));
				}
			}

			// 컬럼명 자동완성
			try (ResultSet columns = metaData.getColumns(null, null, "%", "%")) {
				while (columns.next()) {
					candidates.add(new Candidate(columns.getString("COLUMN_NAME")));
				}
			}
		} catch (Exception e) {
			// 무시 (오류 발생 시 자동완성만 실패)
		}
	}
}