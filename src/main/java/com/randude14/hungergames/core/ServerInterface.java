package com.randude14.hungergames.core;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author sk89q
 */
public abstract class ServerInterface {

    /**
     * Schedules the given <code>task</code> to be invoked once every <code>period</code> ticks
     * after an initial delay of <code>delay</code> ticks.
     *
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    public abstract int scheduleAsyncRepeatingTask(Runnable task, long delay, long period);
    
    public abstract int scheduleSyncRepeatingTask(Runnable task, long delay, long period);
    
    public abstract void cancelTask(int id);
    
    public abstract LocalWorld getWorld(String name);

    public List<LocalWorld> getWorlds() {
        return Collections.emptyList();
    }
    
    public abstract List<LocalPlayer> getOnlinePlayers();
    
}
