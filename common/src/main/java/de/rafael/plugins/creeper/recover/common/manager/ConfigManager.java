/*
 * Copyright (c) 2022-2023. All rights reserved.
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

package de.rafael.plugins.creeper.recover.common.manager;

//------------------------------
//
// This class was developed by Rafael K.
// On 1/29/2022 at 4:43 PM
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.rafael.plugins.creeper.recover.common.CreeperPlugin;
import de.rafael.plugins.creeper.recover.common.classes.enums.TargetTypes;
import de.rafael.plugins.creeper.recover.common.utils.config.JsonConfiguration;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ConfigManager {

    public static final Gson GSON = new GsonBuilder().create();

    public static final int latestConfigVersion = 2;

    private int recoverSpeed = 3 /* to milliseconds */ * 50;
    private int recoverDelay = 20 * 5 /* to milliseconds */ * 50;

    private Sound blockRecoverSound;
    private List<Material> blockBlacklist;

    private boolean enabled = true;
    private boolean bStats = true;
    private boolean ignoreUpdates = false;

    private List<JsonObject> targetList;

    public boolean load() {

        this.blockBlacklist = new ArrayList<>();

        try {
            this.blockRecoverSound = Sound.valueOf("BLOCK_ROOTED_DIRT_PLACE");
        } catch (Exception exception) {
            this.blockRecoverSound = Sound.BLOCK_GRAVEL_PLACE;
        }

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "config.json");

        // Config Version
        if (!jsonConfiguration.jsonObject().has("configVersion")) {
            jsonConfiguration.jsonObject().addProperty("configVersion", latestConfigVersion);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            int currentConfigVersion = jsonConfiguration.jsonObject().get("configVersion").getAsInt();
            if (currentConfigVersion < latestConfigVersion) {
                updateConfig(currentConfigVersion, latestConfigVersion);
                return false;
            }
        }

        if (!jsonConfiguration.jsonObject().has("plugin")) {
            jsonConfiguration.jsonObject().add("plugin", new JsonObject());
        }
        if (!jsonConfiguration.jsonObject().has("recover")) {
            jsonConfiguration.jsonObject().add("recover", new JsonObject());
        }

        // Plugin
        if (!jsonConfiguration.jsonObject().getAsJsonObject("plugin").has("enabled")) {
            jsonConfiguration.jsonObject().getAsJsonObject("plugin").addProperty("enabled", this.enabled);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.enabled = jsonConfiguration.jsonObject().getAsJsonObject("plugin").get("enabled").getAsBoolean();
        }
        if (!jsonConfiguration.jsonObject().getAsJsonObject("plugin").has("bStats")) {
            jsonConfiguration.jsonObject().getAsJsonObject("plugin").addProperty("bStats", this.bStats);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.bStats = jsonConfiguration.jsonObject().getAsJsonObject("plugin").get("bStats").getAsBoolean();
        }
        if (!jsonConfiguration.jsonObject().getAsJsonObject("plugin").has("ignoreUpdates")) {
            jsonConfiguration.jsonObject().getAsJsonObject("plugin").addProperty("ignoreUpdates", this.ignoreUpdates);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.ignoreUpdates = jsonConfiguration.jsonObject().getAsJsonObject("plugin").get("ignoreUpdates").getAsBoolean();
        }

        // Recover
        if (!jsonConfiguration.jsonObject().getAsJsonObject("recover").has("recoverSpeed")) {
            jsonConfiguration.jsonObject().getAsJsonObject("recover").addProperty("recoverSpeed", this.recoverSpeed);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.recoverSpeed = jsonConfiguration.jsonObject().getAsJsonObject("recover").get("recoverSpeed").getAsInt();
        }
        if (!jsonConfiguration.jsonObject().getAsJsonObject("recover").has("recoverDelay")) {
            jsonConfiguration.jsonObject().getAsJsonObject("recover").addProperty("recoverDelay", this.recoverDelay);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.recoverDelay = jsonConfiguration.jsonObject().getAsJsonObject("recover").get("recoverDelay").getAsInt();
        }
        if (!jsonConfiguration.jsonObject().getAsJsonObject("recover").has("blockRecoverSound")) {
            jsonConfiguration.jsonObject().getAsJsonObject("recover").addProperty("blockRecoverSound", this.blockRecoverSound.name());
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.blockRecoverSound = Sound.valueOf(jsonConfiguration.jsonObject().getAsJsonObject("recover").get("blockRecoverSound").getAsString());
        }
        if (!jsonConfiguration.jsonObject().getAsJsonObject("recover").has("blockBlacklist")) {
            jsonConfiguration.jsonObject().getAsJsonObject("recover").add("blockBlacklist", GSON.toJsonTree(this.blockBlacklist, new TypeToken<List<Material>>() {
            }.getType()));
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.blockBlacklist = GSON.fromJson(jsonConfiguration.jsonObject().getAsJsonObject("recover").getAsJsonArray("blockBlacklist"), new TypeToken<List<Material>>() {
            }.getType());
        }

        // Target
        if (!jsonConfiguration.jsonObject().has("target")) {
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
                entityTypes.add(EntityType.TNT.name());

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

            jsonConfiguration.jsonObject().add("target", jsonArray);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.targetList = new ArrayList<>();
            for (JsonElement target : jsonConfiguration.jsonObject().get("target").getAsJsonArray()) {
                this.targetList.add(target.getAsJsonObject());
            }
        }
        jsonConfiguration.saveConfig();

        return true;

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

                if(!entityTypes.contains(event.getEntity().getType())) {
                    usePlugin = false;
                }
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

            JsonElement recoverSpeed = jsonConfiguration.jsonObject().get("recoverSpeed");

            JsonElement bStats = jsonConfiguration.jsonObject().get("bStats");
            JsonElement ignoreUpdates = jsonConfiguration.jsonObject().get("ignoreUpdates");

            JsonElement targetList = jsonConfiguration.jsonObject().get("target");

            jsonConfiguration.clear();
            jsonConfiguration.jsonObject().addProperty("configVersion", to);

            if (!jsonConfiguration.jsonObject().has("plugin")) {
                jsonConfiguration.jsonObject().add("plugin", new JsonObject());
            }
            if (!jsonConfiguration.jsonObject().has("recover")) {
                jsonConfiguration.jsonObject().add("recover", new JsonObject());
            }

            jsonConfiguration.jsonObject().getAsJsonObject("plugin").add("bStats", bStats);
            jsonConfiguration.jsonObject().getAsJsonObject("plugin").add("ignoreUpdates", ignoreUpdates);

            jsonConfiguration.jsonObject().getAsJsonObject("recover").add("recoverSpeed", recoverSpeed);

            jsonConfiguration.jsonObject().add("target", targetList);

            jsonConfiguration.saveConfig();
            success = true;
        } else if(from == 1 && to == 2) {
            JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(configFolder, configFileName);
            jsonConfiguration.jsonObject().addProperty("configVersion", to);

            JsonArray targets = jsonConfiguration.jsonObject().getAsJsonArray("target");
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
            Bukkit.getConsoleSender().sendMessage(CreeperPlugin.instance().messageManager().getMessage(MessageManager.Message.PREFIX) + "§7Config updated from version §b" + from + " §7to §3" + to + "§8.");
        }

    }

}
