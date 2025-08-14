package project.util;

public final class StringUtil {
	private StringUtil() {
	}

	/** 공통적인 공백/널 체크 */
	public static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}