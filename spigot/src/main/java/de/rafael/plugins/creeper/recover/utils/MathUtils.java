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

package de.rafael.plugins.creeper.recover.utils;

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
