package net.rafael.plugins.creeper.recover.update;

//------------------------------
//
// This class was developed by Rafael K.
// On 1/31/2022 at 8:49 AM
// In the project CreeperRecover
//
//------------------------------

import net.rafael.plugins.creeper.recover.CreeperRecover;
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
