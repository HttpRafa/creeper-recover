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

package de.rafael.plugins.creeper.recover.stats;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/21/2022 at 11:17 PM
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.JsonObject;
import de.rafael.plugins.creeper.recover.utils.config.JsonConfiguration;

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
