package charms;

import java.util.ArrayList;
import java.util.List;

import parser.HtmlCharmParser;

public class JujuCharmCommand {
	private String author;
	private String name;
	private String description;
	private String version;
	private boolean preferred;
	private boolean isPublic;
	private String icon;
	private List<CharmOption> options;
	private List<File> inputFiles;
	private String cloudName;
	private String vmName;
	private List<String> commands;
	private boolean haveConfigOptions;

	public JujuCharmCommand() {
		this.options = new ArrayList<CharmOption>();
		this.inputFiles = new ArrayList<File>();
		this.icon = "http://www.n3phele.com/icons/custom";
		this.preferred = true;
		this.isPublic = true;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.replace("\n", " ");
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<File> getInputFiles() {
		return inputFiles;
	}

	public void addInputFiles(String name, String description, boolean optional) {
		inputFiles.add(new File(name, description, optional));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addOption(CharmOption option) {
		options.add(option);
		if (option.isOptional())
			haveConfigOptions = true;
	}

	public void addOptions(List<CharmOption> options) {
		if (options != null) {
			for (CharmOption charmOption : options) {
				addOption(charmOption);
			}
		}
	}

	public boolean haveConfigOptions() {
		return this.haveConfigOptions;
	}

	public List<CharmOption> getOptions() {
		return options;
	}

	public void setOptions(List<CharmOption> options) {
		this.options = options;
	}

	public String getCloudName() {
		return cloudName;
	}

	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public List<String> getCommands() {
		return commands;
	}
	
	public void addCommand(Command command){
		this.addCommand(command.getCommand());
	}
	
	public void addCommand(String command) {
		if (this.commands == null) {
			this.commands = new ArrayList<String>();
			this.commands.add("$$" + vmName + " = CREATEVM --name " + vmName + " --n $$n");
		}
		this.commands.add("ON $$" + vmName + " " + command);
	}

	public String toNShellCommand() {
		StringBuilder nShellCommand = new StringBuilder();
		final String EOL = "\n"; //System.getProperty("line.separator");

		nShellCommand.append("# deploy" + HtmlCharmParser.capitalize(name) + ".n" + EOL);
		nShellCommand.append("# Author: " + author + EOL);
		nShellCommand.append("name	   : " + "deploy" + HtmlCharmParser.capitalize(name) + EOL);
		nShellCommand.append("description: " + description + EOL);
		nShellCommand.append("version	   : " + version + EOL);
		nShellCommand.append("preferred  : " + preferred + EOL);
		nShellCommand.append("public	   : " + isPublic + EOL);
		nShellCommand.append("icon	   : " + icon + EOL);
		nShellCommand.append("parameters :" + EOL);
		for (CharmOption option : options) {
			nShellCommand.append('\t' + option.toNShellParam() + EOL);
		}

		nShellCommand.append("input files:" + EOL);
		for (File file : inputFiles) {
			nShellCommand.append('\t' + file.toNShellInputFile() + EOL);
		}

		nShellCommand.append(cloudName + EOL);
		for (String command : commands) {
			nShellCommand.append('\t' + command + EOL);
		}

		return nShellCommand.toString();
	}

	@Override
	public String toString() {
		return String.format("%s {\n\tauthor: %s\n\tname: %s\n\tdescription: %s\n\tversion: %s\n\tpreferred: %s\n\tisPublic: %s\n\ticon: %s\n\toptions: %s\n\tinputFiles: %s\n}", getClass().getName(), author, name, description, version, preferred, isPublic, icon, options, inputFiles);
	}

	private class File {
		String name;
		String description;
		boolean optional;

		File(String name, String description, boolean optional) {
			super();
			this.name = name;
			this.description = description;
			this.optional = optional;
		}

		public String toNShellInputFile() {
			return String.format("%s%s # %s", optional ? "optional " : "", name, description);
		}

		@Override
		public String toString() {
			return String.format("%s {\n\tname: %s\n\tdescription: %s\n\toptional: %s\n}", getClass().getName(), name, description, optional);
		}

	}

}
