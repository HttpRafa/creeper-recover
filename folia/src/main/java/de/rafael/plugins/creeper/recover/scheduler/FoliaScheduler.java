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

package de.rafael.plugins.creeper.recover.scheduler;

import de.rafael.plugins.creeper.recover.common.CreeperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Rafael K.
 * @since 06/07/2023
 */

public class FoliaScheduler implements CreeperPlugin.Scheduler {

    @Override
    public void runOnCorrectThread(Location location, Runnable runnable) {
        Bukkit.getRegionScheduler().run(CreeperPlugin.instance(), location, scheduledTask -> runnable.run());
    }

    @Override
    public void runAsync(Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(CreeperPlugin.instance(), scheduledTask -> runnable.run());
    }

    @Override
    public void runAsyncAtFixedRate(Consumer<Runnable> runnable, int delay, int period, TimeUnit unit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(CreeperPlugin.instance(), scheduledTask -> runnable.accept(scheduledTask::cancel), delay, period, unit);
    }

}
