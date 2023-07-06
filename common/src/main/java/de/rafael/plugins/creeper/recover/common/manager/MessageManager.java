/*
 * Copyright (c) 2023. All rights reserved.
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

import de.rafael.plugins.creeper.recover.common.utils.config.JsonConfiguration;

import java.io.File;
import java.util.HashMap;

/**
 * @author Rafael K.
 * @since 04/07/2023
 */

public class MessageManager {

    private final HashMap<Message, String> messages = new HashMap<>();

    public void load() {
        this.messages.clear();

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "messages.json");

        for (Message message : Message.values()) {
            if (jsonConfiguration.jsonObject().has(message.getId())) {
                messages.put(message, jsonConfiguration.jsonObject().get(message.getId()).getAsString());
            } else {
                messages.put(message, message.getDefaultMessage());
                jsonConfiguration.jsonObject().addProperty(message.getId(), message.getDefaultMessage());
            }
        }

        jsonConfiguration.saveConfig();

    }

    public String getMessage(Message message, Object... objects) {
        return String.format(this.messages.getOrDefault(message, "§cFailed to load message§8[§b" + message.name() + "§8/§b" + message.getId() + "§8]"), objects);
    }

    public enum Message {

        PREFIX("prefix", "§8➜ §3C§breeperRecover §8● §7"),
        NO_PERMISSION("no.permission", "§cYou don't have permission to use this command§8."),
        BLOCKS_RECOVERED("blocks.recovered", "§b%d §7blocks recovered§8."),
        RELOADED("reloaded", "§7Configuration was §breloaded§8."),
        STATS_TITLE("stats.title", "§7Daily§8:"),
        STATS_LINE_BLOCKS("stats.line.blocks", "   §bBlocks recovered §8» §7%d"),
        STATS_LINE_EXPLOSIONS("stats.line.explosions", "   §bExplosions recovered §8» §7%d"),
        HELP_LINE_1("help.line.1", "§8/§7recover §bfix §8[§3blocks§8/§3all§8]"),
        HELP_LINE_2("help.line.2", "§8/§7recover §breload"),
        HELP_LINE_3("help.line.3", "§8/§7recover §bstats");

        private final String id;
        private final String defaultMessage;

        Message(String id, String defaultMessage) {
            this.id = id;
            this.defaultMessage = defaultMessage;
        }

        public String getId() {
            return id;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }

    }

}
