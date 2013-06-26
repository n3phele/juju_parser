import java.io.FileOutputStream;
import java.util.Collections;

import Utils.UnicodeUtil;

import parser.HtmlCharmParser;
import charms.CharmOption;
import charms.Command;
import charms.JujuCharmCommand;

public class App {

	public static void main(String[] args) throws Exception {
		
		String path = "C:\\Users\\LIS\\Desktop\\Charms";

		HtmlCharmParser jujuParser = new HtmlCharmParser();
		JujuCharmCommand charmCommand;
		for (String url : jujuParser.getCharmsUrl("http://jujucharms.com/charms/precise")) {
			charmCommand = jujuParser.parseCharm(url + "/json");
			charmCommand.setAuthor("Alexandre Tavares");
			charmCommand.setCloudName("HPZone1: # HP Cloud");
			charmCommand.setVersion("1.0.0");
			charmCommand.setVmName("vmTavares");
			charmCommand.setPreferred(true);
			charmCommand.setPublic(true);
			charmCommand.addOption(new CharmOption("n", "int", "1", "How many to create", false));
			charmCommand.addOption(new CharmOption("juju_version", "string", "0.6", "Version to download of juju", false));
			charmCommand.addOption(new CharmOption("quanty", "string", "1", "How many machines to the service", false));
			charmCommand.addOption(new CharmOption("service_name", "string", charmCommand.getName() + "01", "A name to the service", false));

			//Just to the required options stay on the top of the list
			Collections.sort(charmCommand.getOptions());

			charmCommand.addInputFiles("environments.yaml", "Input file to configure juju", false);
			charmCommand.addInputFiles("id_rsa.pub", "Input ssh public key to access environment", false);
			charmCommand.addInputFiles("id_rsa.txt", "Input ssh private key to access environment", false);
			if (charmCommand.haveConfigOptions()) {
				charmCommand.addInputFiles("config.yaml", "an optional configuration file to your charm", true);
			}

			Command command = new Command("if [ -d ~/.juju ]");
			command.addLine("then rm -rf ~/.juju");
			command.addLine("fi");
			charmCommand.addCommand(command);

			command = new Command("mkdir ~/.juju");

			command.addLine("mv id_rsa.txt ~/.ssh/id_rsa");
			command.addLine("mv id_rsa.pub ~/.ssh/id_rsa.pub");
			command.addLine("mv environments.yaml ~/.juju/environments.yaml");
			command.addLine("chmod go-rxw ~/.ssh/id_rsa");
			command.addLine("cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys");
			command.addLine("ssh -t -t -v -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ubuntu@localhost exec ssh-agent ssh-add");
			charmCommand.addCommand(command);

			charmCommand.addCommand("sudo apt-get -y install python-software-properties");
			charmCommand.addCommand("sudo add-apt-repository -y ppa:juju/$$juju_version");
			charmCommand.addCommand("sudo apt-get update -y && sudo apt-get install -y -qq juju");
			charmCommand.addCommand("echo -e \"Host *\\nStrictHostKeyChecking no\" >> ~/.ssh/config");

			// echo options in config.yaml
			if (charmCommand.haveConfigOptions()) {
				command = new Command("if [ ! -e config.yaml ]");
				command.addLine("then printf \"%s:\\\\n\" $$service_name > config.yaml");
				for (CharmOption option : charmCommand.getOptions()) {
					if (option.isOptional()) {
						if (option.getType().equals("string"))
							command.addLine("echo \"  " + option.getOriginalName() + ": ' $$" + option.getName() + " '\" >> config.yaml");
						//command.addLine("printf \" '%s'\\\\n\" $$" + option.getName() + " >> config.yaml");
						else
							command.addLine("echo \"  " + option.getOriginalName() + ": $$" + option.getName() + " \" >> config.yaml");
						//command.addLine("printf \" %s\\\\n\" $$" + option.getName() + " >> config.yaml");
					}
				}
				command.addLine("fi");
				charmCommand.addCommand(command.getCommand());
				//----------------------------
				charmCommand.addCommand("juju deploy --config config.yaml --num-units $$quanty " + charmCommand.getName() + " $$service_name");
				charmCommand.addCommand("rm config.yaml");
			}
			else {
				charmCommand.addCommand("juju deploy --num-units $$quanty " + charmCommand.getName() + " $$service_name");
			}
			charmCommand.addCommand("rm -f ~/.ssh/config");

			FileOutputStream writer = new FileOutputStream(path + "\\deploy" + HtmlCharmParser.capitalize(charmCommand.getName()) + ".n");
			//writer = new BufferedWriter(new FileWriter("C:\\Users\\LIS\\Desktop\\Charms\\deploy" + HtmlCharmParser.capitalize(charmCommand.getName()) + ".n"));
			System.out.println(charmCommand.getName());
			writer.write(UnicodeUtil.convert(charmCommand.toNShellCommand().getBytes(), "ASCII"));
			writer.flush();
			writer.close();

		}

	}
}
