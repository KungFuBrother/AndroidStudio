package yitgogo.consumer.bianmin.game.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "game_id": "221605", "game_area": null, "game_server": null,
 *       "game_name": "梦幻国度" }
 */
public class ModelGame {

	String id = "", area = "", server = "", name = "";

	public ModelGame() {
	}

	public ModelGame(String id, String area, String server, String name) {
		this.area = area;
		this.id = id;
		this.name = name;
		this.server = server;
	}

	public ModelGame(JSONObject object) {
		if (object != null) {
			if (object.has("game_id")) {
				if (!object.optString("game_id").equalsIgnoreCase("null")) {
					id = object.optString("game_id");
				}
			}
			if (object.has("game_area")) {
				if (!object.optString("game_area").equalsIgnoreCase("null")) {
					area = object.optString("game_area");
				}
			}
			if (object.has("game_server")) {
				if (!object.optString("game_server").equalsIgnoreCase("null")) {
					server = object.optString("game_server");
				}
			}
			if (object.has("game_name")) {
				if (!object.optString("game_name").equalsIgnoreCase("null")) {
					name = object.optString("game_name");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getArea() {
		return area;
	}

	public String getServer() {
		return server;
	}

	public String getName() {
		return name;
	}

}
