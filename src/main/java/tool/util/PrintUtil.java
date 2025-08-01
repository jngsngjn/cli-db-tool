package tool.util;

public class PrintUtil {

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
}