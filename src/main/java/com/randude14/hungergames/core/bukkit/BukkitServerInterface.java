package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.core.LocalWorld;
import com.randude14.hungergames.core.ServerInterface;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

public class BukkitServerInterface extends ServerInterface {
    public Server server;

    public BukkitServerInterface(Server server) {
	    this.server = server;
    }

    @Override
    public int scheduleAsyncRepeatingTask(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask((HungerGamesBukkit) HungerGames.getInstance(), task, delay, period);
    }

    @Override
    public List<LocalWorld> getWorlds() {
        List<World> worlds = server.getWorlds();
        List<LocalWorld> ret = new ArrayList<LocalWorld>(worlds.size());

        for (World world : worlds) {
            ret.add(BukkitUtil.getLocalWorld(world));
        }

        return ret;
    }

}
