package tool.code;

public enum Command {

	HELP, EXIT, FILE, TOGGLE_AUTOCOMMIT;

	public static Command parse(String command) {
		for (Command cmd : Command.values()) {
			if (cmd.name().equalsIgnoreCase(command)) {
				return cmd;
			}
		}
		return null;
	}
}