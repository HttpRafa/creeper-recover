package net.rafadev.plugins.creeper.recover.utils;

//------------------------------
//
// This class was developed by Rafael K.
// On 12/31/2021 at 11:37 PM
// In the project CreeperRecover
//
//------------------------------

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

public class MathUtils {

    public static Random RANDOM = new Random();

    public static int generateRandomInteger(int minimum, int maximum) {
        return minimum + (int) (RANDOM.nextDouble() * ((maximum - minimum) + 1));
    }

    public static Vector calculateVectorBetween2Locations(Location location1, Location location2) {
        return location2.toVector().subtract(location1.toVector());
    }

    public static Location toCenterLocation(Location location) {
        Location centerLoc = location.clone();
        centerLoc.setX((double) location.getBlockX() + 0.5D);
        centerLoc.setY((double) location.getBlockY() + 0.5D);
        centerLoc.setZ((double) location.getBlockZ() + 0.5D);
        return centerLoc;
    }

}
