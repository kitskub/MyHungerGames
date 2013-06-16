package me.kitskub.hungergames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.kitskub.hungergames.games.Arena;
import me.kitskub.hungergames.utils.ConfigUtils;
import me.kitskub.hungergames.utils.config.ConfigSection;

public class ArenaMaster {

    private Map<String, Arena> arenas = new HashMap<String, Arena>();

    public Arena createArena(String name) {
        Arena arena = newArena(name);
        saveArena(arena);
        return arena;
    }

    public Set<Arena> getArenas() {
        return new HashSet<Arena>(arenas.values());
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public boolean saveArena(Arena arena) {
        boolean good = arena.save();
        Files.ARENAS.save();
        return good;
    }

    public boolean save() {
        boolean bad = false;
        for (Arena a : arenas.values()) {
            bad |= !saveArena(a);
        }
        return !bad;
    }

    public boolean load() {
        return loadArenas();
    }

    private boolean loadArenas() {
        ConfigSection arenasSection = Files.ARENAS.getConfig();
        List<String> checked = new ArrayList<String>();
        for (Iterator<Arena> it = arenas.values().iterator(); it.hasNext();) {
            Arena arena = it.next();
            checked.add(arena.getName());
            if (arenasSection.contains(arena.getName())) {
                arena.load();
            } else {
                Logging.severe("Please reload Paintball to avoid a memory leak. An arena does not exist that was there before.");
                it.remove();
            }
        }
        for (String name : arenasSection.getKeys(false)) {
            if (checked.contains(name)) {
                continue;
            }
            newArena(name);
        }
        return true;
    }

    private Arena newArena(String name) {
        ConfigSection section = ConfigUtils.getOrCreateSection(Files.ARENAS.getConfig(), name);
        Arena game = new Arena(name, section);
        game.load();
        arenas.put(name, game);
        return game;
    }
}
