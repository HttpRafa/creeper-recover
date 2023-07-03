/*
 * Copyright (c) 2022. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *         this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *     * Neither the name of the developer nor the names of its contributors
 *         may be used to endorse or promote products derived from this software
 *         without specific prior written permission.
 *     * Redistributions in source or binary form must keep the original package
 *         and class name.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import java.util.Objects;

public class ConfigManager {

    public static final int latestConfigVersion = 2;

    private int recoverSpeed = 3;

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
            int currentConfigVersion = jsonConfiguration.getJson().get("configVersion").getAsInt();
            if(currentConfigVersion < latestConfigVersion) {
                updateConfig(currentConfigVersion, latestConfigVersion);
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

            {
                JsonObject worldTarget = new JsonObject();
                worldTarget.addProperty("type", TargetTypes.WORLD.name());
                worldTarget.addProperty("ignore", true);

                worldTarget.add("whitelist", new JsonArray());
                worldTarget.add("blacklist", new JsonArray());
                jsonArray.add(worldTarget);
            }

            {
                JsonObject entityTarget = new JsonObject();
                entityTarget.addProperty("type", TargetTypes.ENTITY.name());
                entityTarget.addProperty("ignore", true);

                JsonArray entityTypes = new JsonArray();
                entityTypes.add(EntityType.CREEPER.name());
                entityTypes.add(EntityType.PRIMED_TNT.name());

                entityTarget.add("entityTypes", entityTypes);
                jsonArray.add(entityTarget);
            }

            {
                JsonObject rangeHeight = new JsonObject();
                rangeHeight.addProperty("type", TargetTypes.HEIGHT_RANGE.name());
                rangeHeight.addProperty("ignore", true);
                rangeHeight.addProperty("from", -64);
                rangeHeight.addProperty("to", 320);
                jsonArray.add(rangeHeight);
            }

            {
                JsonObject fixedHeight = new JsonObject();
                fixedHeight.addProperty("type", TargetTypes.HEIGHT_FIXED.name());
                fixedHeight.addProperty("ignore", true);
                fixedHeight.addProperty("fixed", 32);
                jsonArray.add(fixedHeight);
            }

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

        // World
        List<JsonObject> worldTargets = this.targetList.stream().filter(item -> item.get("type").getAsString().equals(TargetTypes.WORLD.name())).toList();
        for (JsonObject worldTarget : worldTargets) {
            if(!worldTarget.get("ignore").getAsBoolean()) {
                if(worldTarget.has("whitelist")) {
                    JsonArray whiteList = worldTarget.getAsJsonArray("whitelist");
                    if(whiteList.size() > 0 && whiteList.asList().stream().map(JsonElement::getAsString).noneMatch(s -> Objects.requireNonNull(event.getLocation().getWorld()).getName().equalsIgnoreCase(s))) {
                        usePlugin = false;
                    }
                }
                if(worldTarget.has("blacklist")) {
                    JsonArray blacklist = worldTarget.getAsJsonArray("blacklist");
                    if(blacklist.size() > 0 && blacklist.asList().stream().map(JsonElement::getAsString).anyMatch(s -> Objects.requireNonNull(event.getLocation().getWorld()).getName().equalsIgnoreCase(s))) {
                        usePlugin = false;
                    }
                }
            }
        }

        // Entity
        List<JsonObject> entityTargets = this.targetList.stream().filter(item -> item.get("type").getAsString().equals(TargetTypes.ENTITY.name())).toList();
        for (JsonObject entityTarget : entityTargets) {
            if(!entityTarget.get("ignore").getAsBoolean()) {
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
        } else if(from == 1 && to == 2) {
            JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(configFolder, configFileName);
            jsonConfiguration.getJson().addProperty("configVersion", to);

            JsonArray targets = jsonConfiguration.getJson().getAsJsonArray("target");
            targets.forEach(jsonElement -> {
                JsonObject target = jsonElement.getAsJsonObject();
                if(target.get("type").getAsString().equalsIgnoreCase(TargetTypes.ENTITY.name())) {
                    if(target.has("all")) {
                        if(target.get("all").getAsBoolean()) {
                            target.addProperty("ignore", true);
                        }
                        target.remove("all");
                    }
                }
            });

            jsonConfiguration.saveConfig();
            success = true;
        }

        if(success) {
            Bukkit.getConsoleSender().sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§7Config updated from version §b" + from + " §7to §3" + to + "§8.");
        }

    }

}
