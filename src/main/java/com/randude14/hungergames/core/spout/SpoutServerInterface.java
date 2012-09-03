package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.core.LocalWorld;
import com.randude14.hungergames.core.ServerInterface;

import org.spout.api.Engine;
import org.spout.api.geo.World;
import org.spout.api.scheduler.TaskPriority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpoutServerInterface extends ServerInterface {
    public Engine game;

    public SpoutServerInterface(Engine game) {
        this.game = game;
    }

    @Override
    public int scheduleAsyncRepeatingTask(Runnable task, long delay, long period) {
	return game.getScheduler().scheduleAsyncRepeatingTask(HungerGames.getInstance(), task, delay * 50, period * 50, TaskPriority.NORMAL);
    }

    @Override
    public List<LocalWorld> getWorlds() {
        Collection<World> worlds = game.getWorlds();
        List<LocalWorld> ret = new ArrayList<LocalWorld>(worlds.size());

        for (World world : worlds) {
            ret.add(SpoutUtil.getLocalWorld(world));
        }

        return ret;
    }
}
