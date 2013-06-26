package charms;

public class Command {
	private StringBuilder command;

	public Command(String command) {
		this.command = new StringBuilder(command + '\n');
	}

	public String getCommand() {
		return this.command.toString();
	}

	public void setCommand(String command) {
		this.command = new StringBuilder(command);
	}
	
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
