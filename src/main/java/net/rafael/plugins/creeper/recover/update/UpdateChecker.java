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
    private String latestVersion = null;

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

    public void isLastestVersion(String currentVersion, Consumer<Boolean> consumer) {
        getLastestVersion(latest ->  {
            this.latestVersion = latest;
            if(latest.equalsIgnoreCase(currentVersion)) {
                this.uptoDate = true;
                consumer.accept(true);
            } else {
                this.uptoDate = false;
                consumer.accept(false);
            }
        });
    }

    public int getResourceId() {
        return resourceId;
    }

    public boolean isUptoDate() {
        return uptoDate;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

}
