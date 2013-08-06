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
		for (String url : jujuParser.getCharmsUrl("http://manage.jujucharms.com/charms/precise")) {
			charmCommand = jujuParser.parseCharm(url + "/json");
			charmCommand.setAuthor("Alexandre Tavares");
			charmCommand.setCloudName("HPZone1: # HP Cloud");
			charmCommand.setVersion("1.0.0");
			charmCommand.setVmName("bootstrap");
			charmCommand.setPreferred(true);
			charmCommand.setPublic(true);
			charmCommand.addOption(new CharmOption("service_name", "string", charmCommand.getName() + "01", "A name to the service", false));

			//Just to the required options stay on the top of the list
			Collections.sort(charmCommand.getOptions());

			if (charmCommand.haveConfigOptions()) {
				charmCommand.addInputFiles("config.yaml", "an optional configuration file to your charm", true);
			}

			Command command = new Command();

			// echo options in config.yaml
			if (charmCommand.haveConfigOptions()) {
				command.addLine("if [ ! -e config.yaml ];");
				command.addLine("then echo \"$${$$service_name}:\" > config.yaml;");
				for (CharmOption option : charmCommand.getOptions()) {
					if (option.isOptional()) {
						command.addLine("\techo \"  " + option.getOriginalName() + ": $$" + option.getName() + " \" >> config.yaml;");
					}
				}
				command.addLine("fi;");
				
				command.addLine("juju deploy --config config.yaml " + charmCommand.getName() + " $${$$service_name};");
				command.addLine("rm config.yaml;");
				
			}
			else {
				command.addLine("juju deploy " + charmCommand.getName() + " $${$$service_name};");
			}
			charmCommand.addCommand(command.getCommand());
			
			command = new Command("$$on = ON $$" + charmCommand.getVmName());
			command.addLine("STAT=\"none\";");
			command.addLine("RUN_STATUS=\"started\";");
			command.addLine("while [ \"$STAT\" != \"$RUN_STATUS\" ];");
			command.addLine("do sleep 20;");
			command.addLine("juju status | shyaml get-value services.$${$$service_name} > tmp.txt;");
			command.addLine("STAT=$(sed -n -e '0,/agent-state: /s///p' tmp.txt);");
			command.addLine("STAT=$(echo $STAT | tr -d ' ');");
			command.addLine("done;");
			command.addLine("IPS=$(grep -o '[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}' tmp.txt);");
			command.addLine("IPA=(${IPS//:/ });");
			command.addLine("IP=$(echo ${IPA[0]});");
			command.addLine("IP=$(echo $IP | tr -d ' ');");
			command.addLine("echo $IP");
			
			charmCommand.addCommand(command, true);
			charmCommand.addCommand("$$vm = ASSIMILATEVM --targetIP $$on.stdout", true);

			FileOutputStream writer = new FileOutputStream(path + "\\deploy" + HtmlCharmParser.capitalize(charmCommand.getName()) + ".n");
			//writer = new BufferedWriter(new FileWriter("C:\\Users\\LIS\\Desktop\\Charms\\deploy" + HtmlCharmParser.capitalize(charmCommand.getName()) + ".n"));
			System.out.println(charmCommand.getName());
			writer.write(UnicodeUtil.convert(charmCommand.toNShellCommand().getBytes(), "ASCII"));
			writer.flush();
			writer.close();

		}

	}
}
