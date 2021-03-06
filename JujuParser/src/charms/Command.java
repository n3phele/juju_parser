package charms;

public class Command {
	private StringBuilder command;

	
	public Command() {
		this.command = new StringBuilder('\n');
	}
	
	public Command(String command) {
		this.command = new StringBuilder(command + '\n');
	}

	public String getCommand() {
		return this.command.toString();
	}

	public void setCommand(String command) {
		this.command = new StringBuilder(command);
	}
	/**
	 * Adds new line to the command
	 * @param commandLine
	 */
	public void addLine(String commandLine){
		this.command.append("\t\t");
		this.command.append(commandLine);
		this.command.append('\n');
	}

	@Override
	public String toString() {
		return getCommand();
	}

	
}
