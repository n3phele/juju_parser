package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import charms.CharmOption;
import charms.JujuCharmCommand;

public class HtmlCharmParser {

	public HtmlCharmParser() {
	}

	/**
	 * Get all the URLs of the charms based on the JSON of the URL passed as param.
	 * 
	 * @param url
	 * @return List with the charms URLs
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ArrayList<JujuCharmCommand> getCharms(String url) throws IOException, ParseException {

		JujuCharmCommand charm;
		
		Document document = Jsoup.connect(url).ignoreContentType(true).get();

		ArrayList<JujuCharmCommand> list = new ArrayList<JujuCharmCommand>();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(document.text());
		JSONArray jsonArray = (JSONArray) jsonObject.get("result");
		for (Object objCharm : jsonArray) {
			charm = new JujuCharmCommand();
			
			jsonObject = (JSONObject) objCharm;
			jsonObject = (JSONObject) jsonObject.get("charm");
			charm.setName(jsonObject.get("name").toString());
			charm.setDescription(jsonObject.get("summary").toString());

			jsonObject = (JSONObject) jsonObject.get("options");
			CharmOption charmOption;

			for (Object obj : jsonObject.entrySet()) {
				Entry<String, Map> entry = (Entry<String, Map>) obj;
				charmOption = new CharmOption();
				charmOption.setName(entry.getKey());
				if (entry.getValue().containsKey("default")) {
					charmOption.setDefaultValue(entry.getValue().get("default").toString());
				}
				else {
					charmOption.setDefaultValue("");
				}
				if (entry.getValue().containsKey("description")) {
					charmOption.setDescription(entry.getValue().get("description").toString());
				}
				else {
					charmOption.setDescription("");
				}
				charmOption.setType(entry.getValue().get("type").toString());
				charmOption.setOptional(true);
				charm.addOption(charmOption);
			}
			list.add(charm);
		}

		return list;
	}

	public static String capitalize(String s) {
		String string = s.substring(0, 1).toUpperCase() + s.substring(1);
		int index = string.indexOf('-');
		while (index != -1) {
			string = string.replaceFirst("-", "");
			string = string.substring(0, index) + string.substring(index, index + 1).toUpperCase() + string.substring(index + 1);
			index = string.indexOf('-');
		}
		return string;
	}

}
