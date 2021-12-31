package net.rafadev.plugins.creeper.recover;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project CreeperRecover
//
//------------------------------

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.rafadev.plugins.creeper.recover.config.JsonConfiguration;
import net.rafadev.plugins.creeper.recover.listener.ExplosionListener;
import net.rafadev.plugins.creeper.recover.manager.ExplosionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreeperRecover extends JavaPlugin {

    private static CreeperRecover creeperRecover;
    private ExplosionManager explosionManager;

    private int recoverSpeed = 3;
    private List<EntityType> entityTypes = new ArrayList<>(Arrays.stream(new EntityType[] {EntityType.CREEPER}).toList());

    @Override
    public void onLoad() {

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("plugins//CreeperRecover/"), "config.json");
        if(!jsonConfiguration.getJson().has("recoverSpeed")) {
            jsonConfiguration.getJson().addProperty("recoverSpeed", this.recoverSpeed);
        } else {
            this.recoverSpeed = jsonConfiguration.getJson().get("recoverSpeed").getAsInt();
        }
        if(!jsonConfiguration.getJson().has("entityTypes")) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(EntityType.CREEPER.name());
            jsonConfiguration.getJson().add("entityTypes", jsonArray);
        } else {
            this.entityTypes = new ArrayList<>();
            for (JsonElement types : jsonConfiguration.getJson().get("entityTypes").getAsJsonArray()) {
                this.entityTypes.add(EntityType.valueOf(types.getAsString()));
            }
        }
        jsonConfiguration.saveConfig();

    }

    @Override
    public void onEnable() {
        creeperRecover = this;

        this.explosionManager = new ExplosionManager();

        Bukkit.getPluginManager().registerEvents(new ExplosionListener(), this);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            explosionManager.recoverBlock();
        }, 0, this.recoverSpeed);
    }

    @Override
    public void onDisable() {

    }

    public static CreeperRecover getCreeperRecover() {
        return creeperRecover;
    }

    public ExplosionManager getExplosionManager() {
        return explosionManager;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public int getRecoverSpeed() {
        return recoverSpeed;
    }

}
