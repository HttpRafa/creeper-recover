package net.rafael.plugins.creeper.recover.stats;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/21/2022 at 11:17 PM
// In the project CreeperRecover
//
//------------------------------

import net.rafael.plugins.creeper.recover.config.lib.JsonConfiguration;

import java.io.File;

public class PluginStats {

    private int blocksRecovered = 0;
    private int explosionsRecovered = 0;

    public void load() {
        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "stats.json");

        if (jsonConfiguration.getJson().has("explosionsRecovered")) {
            this.explosionsRecovered = jsonConfiguration.getJson().get("explosionsRecovered").getAsInt();
        } else {
            jsonConfiguration.getJson().addProperty("explosionsRecovered", this.explosionsRecovered);
        }

        if (jsonConfiguration.getJson().has("blocksRecovered")) {
            this.blocksRecovered = jsonConfiguration.getJson().get("blocksRecovered").getAsInt();
        } else {
            jsonConfiguration.getJson().addProperty("blocksRecovered", this.blocksRecovered);
        }

        jsonConfiguration.saveConfig();
    }

    public void save() {
        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "stats.json");

        jsonConfiguration.getJson().addProperty("explosionsRecovered", this.explosionsRecovered);
        jsonConfiguration.getJson().addProperty("blocksRecovered", this.blocksRecovered);

        jsonConfiguration.saveConfig();
    }

    public int getBlocksRecovered() {
        return blocksRecovered;
    }

    public int getExplosionsRecovered() {
        return explosionsRecovered;
    }

    public void blocksRecovered() {
        this.blocksRecovered++;
    }

    public void explosionsRecovered() {
        this.explosionsRecovered++;
    }

}
