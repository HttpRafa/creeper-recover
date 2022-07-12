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

package de.rafael.plugins.creeper.recover.update;

//------------------------------
//
// This class was developed by Rafael K.
// On 1/31/2022 at 8:49 AM
// In the project CreeperRecover
//
//------------------------------

import de.rafael.plugins.creeper.recover.CreeperRecover;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final int resourceId;
    private boolean uptoDate = false;
    private PluginVersion latestVersion = null;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getLastestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(CreeperRecover.getCreeperRecover(), () -> {
           try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream()) {
               Scanner scanner = new Scanner(inputStream);
               if(scanner.hasNext()) {
                   String version = scanner.next();
                   consumer.accept(version);
               } else {
                   consumer.accept(null);
               }
           } catch (IOException exception) {
               exception.printStackTrace();
           }
        });
    }

    public void isLastestVersion(PluginVersion currentVersion, Consumer<Boolean> consumer) {
        getLastestVersion(latest -> {
            this.latestVersion = new PluginVersion().from(latest);
            if (this.latestVersion.compare(currentVersion) == 0) {
                this.uptoDate = true;
                consumer.accept(true);
            } else if (this.latestVersion.compare(currentVersion) < 0) {
                this.uptoDate = false;
                consumer.accept(false);
            } else if (this.latestVersion.compare(currentVersion) < 1) {
                Bukkit.getConsoleSender().sendMessage(CreeperRecover.getCreeperRecover().getPrefix() + "§7You are currently using a §bdeveloper §7version§8.");
                this.uptoDate = true;
                consumer.accept(true);
            }
        });
    }

    public int getResourceId() {
        return resourceId;
    }

    public boolean isUptoDate() {
        return uptoDate;
    }

    public PluginVersion getLatestVersion() {
        return latestVersion;
    }

}
