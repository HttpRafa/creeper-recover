package net.rafael.plugins.creeper.recover.config;

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
import net.rafael.plugins.creeper.recover.config.enums.TargetTypes;
import net.rafael.plugins.creeper.recover.config.lib.JsonConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private int recoverSpeed = 3;

    private boolean bStats = true;
    private boolean ignoreUpdates = false;

    private List<JsonObject> targetList;

    public boolean load() {

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "config.json");
        if(!jsonConfiguration.getJson().has("bStats")) {
            jsonConfiguration.getJson().addProperty("bStats", this.bStats);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.bStats = jsonConfiguration.getJson().get("bStats").getAsBoolean();
        }
        if(!jsonConfiguration.getJson().has("ignoreUpdates")) {
            jsonConfiguration.getJson().addProperty("ignoreUpdates", this.ignoreUpdates);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.ignoreUpdates = jsonConfiguration.getJson().get("ignoreUpdates").getAsBoolean();
        }
        if(!jsonConfiguration.getJson().has("recoverSpeed")) {
            jsonConfiguration.getJson().addProperty("recoverSpeed", this.recoverSpeed);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.recoverSpeed = jsonConfiguration.getJson().get("recoverSpeed").getAsInt();
        }
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

}
