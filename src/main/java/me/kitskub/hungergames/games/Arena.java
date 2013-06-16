package me.kitskub.hungergames.games;

import java.util.ArrayList;
import java.util.List;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.config.ConfigSection;
import me.kitskub.hungergames.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Arena {
    public enum ArenaState {
        ACTIVE,
        INACTIVE,
        DISABLED;
    }

    private String name;
    private ArenaRegion region;
    private ArenaState state;

    private Game activeGame;
    private final List<Game> addedTo;
    private final ConfigSection section;

    public Arena(String name, ConfigSection section) {
        this.section = section;
        this.name = name;
        this.state = ArenaState.DISABLED;
        this.addedTo = new ArrayList<Game>();
        ConfigSection regionSection = ConfigUtils.getOrCreateSection(section, "region");
        this.region = new ArenaRegion(this, regionSection);
    }

    public String getName() {
        return name;
    }

    public boolean setEnabled(boolean flag) {
        return setEnabled(flag, Bukkit.getConsoleSender());
    }
    
    public boolean setEnabled(boolean flag, CommandSender cs) {
        if (!flag) {
            if (activeGame != null) activeGame.stopGame(false);
	    state = ArenaState.DISABLED;
	}
	if (flag && state == ArenaState.DISABLED) {
            region.verifyData();
            boolean regionSetup = region.isLobbySetup() && region.isMainSetup() && region.isSpecSetup();
            if (!regionSetup) {
                ChatUtils.error(cs, "The region is not properly set up! Cannot enable arena:" + name);
                return false;
            }
            state = ArenaState.INACTIVE;
        }/*
        for (Game g : addedTo) {
            String error = g.validate();
            if (error != null) {                
                ChatUtils.error(cs, error);
            }
        }*/
        return true;
    }

    public ArenaRegion getRegion() {
        return region;
    }

    public Game getActiveGame() {
        return activeGame;
    }

    public List<Game> getAddedToGames() {
        return new ArrayList<Game>(addedTo);
    }

    public void addGame(Game game) {
        addedTo.add(game);
    }

    public void removeGame(Game game) {
        addedTo.remove(game);
    }

    public ArenaState getState() {
        return state;
    }

    public void setActiveGame(Game game) {
        activeGame = game;
    }

    public boolean save() {
        boolean bad = false;
        section.set("enabled", state != ArenaState.DISABLED);
        bad |= !region.save();
        return !bad;
    }

    public boolean load() {
        boolean bad = false;
        bad |= !region.load();
        setEnabled(section.getBoolean("enabled", false));//Last
        return !bad;
    }
}
