package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import charms.CharmOption;
import charms.JujuCharmCommand;

public class HtmlCharmParser {

	public HtmlCharmParser() {
	}

	public JujuCharmCommand parseCharm(String url) throws Exception{
		JujuCharmCommand charm = new JujuCharmCommand();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse(Jsoup.connect(url).get().text());
		charm.setName(jsonObject.get("name").toString());
		charm.setDescription(jsonObject.get("summary").toString());
		
		if(!jsonObject.containsKey("config"))
			return charm;
		
		jsonObject = (JSONObject) jsonObject.get("config");
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
		
		return charm;
	}

	public ArrayList<String> getCharmsUrl(String url) throws IOException {
		Document document = Jsoup.connect(url).get();

		ArrayList<String> list = new ArrayList<String>();
		Elements links = document.select("td a");
		for (Element link : links) {
			list.add(link.baseUri() + "/" + link.text());
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
