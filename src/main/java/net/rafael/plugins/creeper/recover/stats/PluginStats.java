package net.rafael.plugins.creeper.recover.stats;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/21/2022 at 11:17 PM
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.JsonObject;
import net.rafael.plugins.creeper.recover.config.lib.JsonConfiguration;

import java.io.File;

public class PluginStats {

    private static final long DAY_MILLIS = 86400000;
    //private static final long DAY_MILLIS = 300000;

    // Daily
    private long lastReset = System.currentTimeMillis();

    private int blocksRecovered = 0;
    private int explosionsRecovered = 0;

    public void load() {
        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "stats.json");

        if (!jsonConfiguration.getJson().has("daily")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("lastReset", System.currentTimeMillis());
            jsonConfiguration.getJson().add("daily", jsonObject);
        }

        if (jsonConfiguration.getJson().getAsJsonObject("daily").has("lastReset")) {
            this.lastReset = jsonConfiguration.getJson().getAsJsonObject("daily").get("lastReset").getAsLong();
        } else {
            jsonConfiguration.getJson().getAsJsonObject("daily").addProperty("lastReset", this.lastReset);
        }

        if (jsonConfiguration.getJson().getAsJsonObject("daily").has("explosionsRecovered")) {
            this.explosionsRecovered = jsonConfiguration.getJson().getAsJsonObject("daily").get("explosionsRecovered").getAsInt();
        } else {
            jsonConfiguration.getJson().getAsJsonObject("daily").addProperty("explosionsRecovered", this.explosionsRecovered);
        }

        if (jsonConfiguration.getJson().getAsJsonObject("daily").has("blocksRecovered")) {
            this.blocksRecovered = jsonConfiguration.getJson().getAsJsonObject("daily").get("blocksRecovered").getAsInt();
        } else {
            jsonConfiguration.getJson().getAsJsonObject("daily").addProperty("blocksRecovered", this.blocksRecovered);
        }

        jsonConfiguration.saveConfig();
    }

    public void save() {
        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "stats.json");

        jsonConfiguration.getJson().getAsJsonObject("daily").addProperty("explosionsRecovered", this.explosionsRecovered);
        jsonConfiguration.getJson().getAsJsonObject("daily").addProperty("blocksRecovered", this.blocksRecovered);

        jsonConfiguration.saveConfig();
    }

    public int getBlocksRecovered() {
        return blocksRecovered;
    }

    public int getExplosionsRecovered() {
        return explosionsRecovered;
    }

    public void tick() {
        if ((lastReset + DAY_MILLIS) <= System.currentTimeMillis()) {
            JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "stats.json");
            jsonConfiguration.getJson().remove("daily");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("lastReset", System.currentTimeMillis());
            jsonConfiguration.getJson().add("daily", jsonObject);

            this.explosionsRecovered = 0;
            this.blocksRecovered = 0;
        }
        save();
    }

    public void blocksRecovered() {
        this.blocksRecovered++;
    }

    public void explosionsRecovered() {
        this.explosionsRecovered++;
    }

}
