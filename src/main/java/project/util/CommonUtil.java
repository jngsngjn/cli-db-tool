package project.util;

import java.util.HashMap;
import java.util.Map;

public final class CommonUtil {

	private CommonUtil() {
	}

	public static Map<String, String> parseArgs(String[] args) {
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