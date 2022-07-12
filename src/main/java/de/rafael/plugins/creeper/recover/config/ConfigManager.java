package de.rafael.plugins.creeper.recover.config;

//------------------------------
//
// This class was developed by Rafael K.
// On 1/29/2022 at 4:43 PM
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.rafael.plugins.creeper.recover.config.lib.JsonConfiguration;
import de.rafael.plugins.creeper.recover.CreeperRecover;
import de.rafael.plugins.creeper.recover.config.enums.TargetTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static final int latestConfigVersion = 1;

    private int recoverSpeed = 3;
    private int currentConfigVersion = -1;

    private Sound blockRecoverSound;

    private boolean bStats = true;
    private boolean ignoreUpdates = false;

    private List<JsonObject> targetList;

    public boolean load() {

        try {
            this.blockRecoverSound = Sound.valueOf("BLOCK_ROOTED_DIRT_PLACE");
        } catch (Exception exception) {
            this.blockRecoverSound = Sound.BLOCK_GRAVEL_PLACE;
        }

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "config.json");

        // Config Version
        if(!jsonConfiguration.getJson().has("configVersion")) {
            jsonConfiguration.getJson().addProperty("configVersion", latestConfigVersion);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.currentConfigVersion = jsonConfiguration.getJson().get("configVersion").getAsInt();
            if(this.currentConfigVersion < latestConfigVersion) {
                updateConfig(this.currentConfigVersion, latestConfigVersion);
                return false;
            }
        }

        if(!jsonConfiguration.getJson().has("plugin")) {
            jsonConfiguration.getJson().add("plugin", new JsonObject());
        }
        if(!jsonConfiguration.getJson().has("recover")) {
            jsonConfiguration.getJson().add("recover", new JsonObject());
        }

        // Plugin
        if(!jsonConfiguration.getJson().getAsJsonObject("plugin").has("bStats")) {
            jsonConfiguration.getJson().getAsJsonObject("plugin").addProperty("bStats", this.bStats);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.bStats = jsonConfiguration.getJson().getAsJsonObject("plugin").get("bStats").getAsBoolean();
        }
        if(!jsonConfiguration.getJson().getAsJsonObject("plugin").has("ignoreUpdates")) {
            jsonConfiguration.getJson().getAsJsonObject("plugin").addProperty("ignoreUpdates", this.ignoreUpdates);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.ignoreUpdates = jsonConfiguration.getJson().getAsJsonObject("plugin").get("ignoreUpdates").getAsBoolean();
        }

        // Recover
        if(!jsonConfiguration.getJson().getAsJsonObject("recover").has("recoverSpeed")) {
            jsonConfiguration.getJson().getAsJsonObject("recover").addProperty("recoverSpeed", this.recoverSpeed);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.recoverSpeed = jsonConfiguration.getJson().getAsJsonObject("recover").get("recoverSpeed").getAsInt();
        }
        if(!jsonConfiguration.getJson().getAsJsonObject("recover").has("blockRecoverSound")) {
            jsonConfiguration.getJson().getAsJsonObject("recover").addProperty("blockRecoverSound", this.blockRecoverSound.name());
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.blockRecoverSound = Sound.valueOf(jsonConfiguration.getJson().getAsJsonObject("recover").get("blockRecoverSound").getAsString());
        }

        // Target
        if(!jsonConfiguration.getJson().has("target")) {
            JsonArray jsonArray = new JsonArray();

            JsonObject allEntries = new JsonObject();
            allEntries.addProperty("type", TargetTypes.ENTITY.name());
            allEntries.addProperty("ignore", false);
            allEntries.addProperty("all", true);

            JsonArray entityTypes = new JsonArray();
            entityTypes.add(EntityType.CREEPER.name());
            entityTypes.add(EntityType.PRIMED_TNT.name());

            allEntries.add("entityTypes", entityTypes);
            jsonArray.add(allEntries);

            JsonObject rangeHeight = new JsonObject();
            rangeHeight.addProperty("type", TargetTypes.HEIGHT_RANGE.name());
            rangeHeight.addProperty("ignore", false);
            rangeHeight.addProperty("from", -64);
            rangeHeight.addProperty("to", 320);
            jsonArray.add(rangeHeight);

            JsonObject fixedHeight = new JsonObject();
            fixedHeight.addProperty("type", TargetTypes.HEIGHT_FIXED.name());
            fixedHeight.addProperty("ignore", true);
            fixedHeight.addProperty("fixed", 32);
            jsonArray.add(fixedHeight);

            jsonConfiguration.getJson().add("target", jsonArray);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.targetList = new ArrayList<>();
            for (JsonElement target : jsonConfiguration.getJson().get("target").getAsJsonArray()) {
                this.targetList.add(target.getAsJsonObject());
            }
        }
        jsonConfiguration.saveConfig();

        return true;

    }

    public int getRecoverSpeed() {
        return recoverSpeed;
    }

    public Sound getBlockRecoverSound() {
        return blockRecoverSound;
    }

    public boolean isIgnoreUpdates() {
        return ignoreUpdates;
    }

    public boolean isbStats() {
        return bStats;
    }

    public List<JsonObject> getTargetList() {
        return targetList;
    }

    public boolean usePlugin(EntityExplodeEvent event) {
        boolean usePlugin = true;

        // Entity
        List<JsonObject> entityTargets = this.targetList.stream().filter(item -> item.get("type").getAsString().equals(TargetTypes.ENTITY.name())).toList();
        for (JsonObject entityTarget : entityTargets) {
            if(!entityTarget.get("ignore").getAsBoolean()) {
                if(entityTarget.has("all") && entityTarget.get("all").getAsBoolean()) {
                    continue;
                }

                List<EntityType> entityTypes = new ArrayList<>();
                for (JsonElement types : entityTarget.get("entityTypes").getAsJsonArray()) {
                    entityTypes.add(EntityType.valueOf(types.getAsString()));
                }

                usePlugin = entityTypes.contains(event.getEntity().getType());
            }
        }

        // Height Range
        List<JsonObject> rangeHeightTargets = this.targetList.stream().filter(item -> item.get("type").getAsString().equals(TargetTypes.HEIGHT_RANGE.name())).toList();
        for (JsonObject rangeHeightTarget : rangeHeightTargets) {
            if(!rangeHeightTarget.get("ignore").getAsBoolean()) {
                Location location = event.getLocation().clone();

                int from = rangeHeightTarget.get("from").getAsInt();
                int to = rangeHeightTarget.get("to").getAsInt();
                if (location.getY() >= from && location.getY() <= to) {
                    continue;
                }
                usePlugin = false;
            }
        }

        // Height Fixed
        List<JsonObject> fixedHeightTargets = this.targetList.stream().filter(item -> item.get("type").getAsString().equals(TargetTypes.HEIGHT_FIXED.name())).toList();
        for (JsonObject fixedHeightTarget : fixedHeightTargets) {
            if(!fixedHeightTarget.get("ignore").getAsBoolean()) {
                Location location = event.getLocation().clone();

                int fixed = fixedHeightTarget.get("fixed").getAsInt();
                if (((int)location.getY()) == fixed) {
                    continue;
                }
                usePlugin = false;
            }
        }

        return usePlugin;
    }

    public void updateConfig(int from, int to) {
        File configFolder = new File("plugins//CreeperRecover/");
        String configFileName = "config.json";

        boolean success = false;

        if(from == -1 && to == 1) {
            JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(configFolder, configFileName);

            JsonElement recoverSpeed = jsonConfiguration.getJson().get("recoverSpeed");

            JsonElement bStats = jsonConfiguration.getJson().get("bStats");
            JsonElement ignoreUpdates = jsonConfiguration.getJson().get("ignoreUpdates");

            JsonElement targetList = jsonConfiguration.getJson().get("target");

            jsonConfiguration.clear();
            jsonConfiguration.getJson().addProperty("configVersion", to);

            if (!jsonConfiguration.getJson().has("plugin")) {
                jsonConfiguration.getJson().add("plugin", new JsonObject());
            }
            if (!jsonConfiguration.getJson().has("recover")) {
                jsonConfiguration.getJson().add("recover", new JsonObject());
            }

            jsonConfiguration.getJson().getAsJsonObject("plugin").add("bStats", bStats);
            jsonConfiguration.getJson().getAsJsonObject("plugin").add("ignoreUpdates", ignoreUpdates);

            jsonConfiguration.getJson().getAsJsonObject("recover").add("recoverSpeed", recoverSpeed);

            jsonConfiguration.getJson().add("target", targetList);

            jsonConfiguration.saveConfig();
            success = true;
        }

        if(success) {
            Bukkit.getConsoleSender().sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§7Config updated from version §b" + from + " §7to §3" + to + "§8.");
        }

    }

}
