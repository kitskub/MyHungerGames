package com.randude14.hungergames;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class Defaults {
    
    public enum Perm {

	ADMIN_ADD_SPAWNPOINT("hungergame.add.spawnpoint"),
	ADMIN_ADD_CHEST("hungergame.add.chest"),
	ADMIN_ADD_GAME("hungergame.add.game"),
    ADMIN_ADD_ITEMSET("hungergame.add.itemset"),
	ADMIN_REMOVE_SPAWNPOINT("hungergame.remove.spawnpoint"),
	ADMIN_REMOVE_CHEST("hungergame.remove.chest"),
	ADMIN_REMOVE_GAME("hungergame.remove.game"),
	ADMIN_REMOVE_ITEMSET("hungergame.remove.itemset"),
	ADMIN_SET_ENABLED("hungergame.set.enabled"),
	ADMIN_SET_SPAWN("hungergame.set.spawn"),
	ADMIN_START("hungergame.game.start"),
	ADMIN_PAUSE("hungergame.game.pause"),
	ADMIN_RESUME("hungergame.game.resume"),
	ADMIN_RELOAD("hungergame.admin.reload"),
	ADMIN_KICK("hungergame.admin.kick"),
	ADMIN_HELP("hungergame.admin.help"),
	USER_JOIN("hungergame.user.join"),
	USER_LEAVE("hungergame.user.leave"),
	USER_LIST("hungergame.user.list"),
	USER_REJOIN("hungergame.user.rejoin"),
	USER_SPONSOR("hungergame.user.sponsor"),
	USER_VOTE("hungergame.user.vote"),
	USER_STAT("hungergame.user.stat"),
	USER_HELP("hungergame.user.help"),
	USER_QUIT("hungergame.user.quit");

	private String value;
	
	private Perm(String permission) {
	    this.value = permission;
	}
	
	public String getPermission(){
	    return value;
	}
	}
    
    public enum Message {
	
	JOIN("<player> has joined the game <game>."),
	REJOIN("<player> has rejoined the game <gam"),
	LEAVE("<player> has left the game <game>."),
	QUIT("<player> has quit the game <game>."),
	VOTE("<killer> killed <killed> in game <game>."),
	KILL("<player> is ready to play <game>. Type /hg vote when you are ready to play.");
	
	private String value;
	
	private Message(String message) {
	    this.value = message;
	}
	
	public String getMessage(){
	    return value;
	}
    }
    
    public enum Config {
	
	MIN_VOTE(5),
	MIN_PLAYERS(2),
	UPDATE_DELAY(30),
	DEFAULT_TIME(10),
	ALLOW_REJOIN(true),
	ALLOW_JOIN_WHILE_RUNNING(false),
	WINNER_KEEPS_ITEMS(true),
	RESPAWN_ON_DEATH(false),
	DEFAULT_GAME("Test"),
	LIVES(1),
	SHOULD_CLEAR_INV(false),
	ALL_VOTE(false),
	AUTO_VOTE(false),
	CAN_PLACE_BLOCK(false),
	CAN_BREAK_BLOCK(false),
	CAN_INTERACT_BLOCK(false),
	CAN_TELEPORT(false),
	USE_COMMAND(false),
	RELOAD_WORLD(false),
	RELOAD_WORLD_NAME("Default_world");
	
	
	private Object value;
	
	private Config(Object message) {
	    this.value = message;
	}
	
	public boolean getBoolean(){
	    return (Boolean) value;
	}
	
	public int getInt(){
	    return (Integer) value;
	}
	
	public String getString(){
	    return (String) value;	    
	}
    }
    
    public enum CommandUsage{
	
	ADMIN_ADD_HELP("/%s add ?", "type for more help"),
	ADMIN_ADD_SPAWNPOINT("/%s add spawnpoint <game name>", "add a spawnpoint"),
	ADMIN_ADD_CHEST("/%s add chest <game name>", "add a chest"),
	ADMIN_ADD_GAME("/%s add game <game name> [setup]", "add a game"),
	ADMIN_ADD_ITEMSET("/%s add itemset <game name> <itemset name>", "add an itemset"),
	ADMIN_REMOVE_HELP("/%s remove ?", "type for more help"),
	ADMIN_REMOVE_SPAWNPOINT("/%s remove spawnpoint <game name>", "remove a spawnpoint"),
	ADMIN_REMOVE_CHEST("/%s remove chest <game name>", "remove a chest"),
	ADMIN_REMOVE_GAME("/%s remove game <game name>", "remove a game"),
	ADMIN_REMOVE_ITEMSET("/%s remove itemset <game name> <itemset name>", "remove a game"),
	ADMIN_SET_HELP("/%s set ?", "type for more help"),
	ADMIN_SET_ENABLED("/%s set enabled <game name> <true/false>", "enable or disable a game"),
	ADMIN_SET_SPAWN("/%s set spawn <game name>", "set the spawnpoint for a game"),
	ADMIN_START("/%s start <game name> [seconds]", "manually start a game"),
	ADMIN_PAUSE("/%s pause <game name>", "pause a game"),
	ADMIN_RESUME("/%s resume <game name>", "resume a game"),
	ADMIN_RELOAD("/%s rejoin", "reload MyHungerGames"),
	ADMIN_KICK("/%s kick <player>", "kick a player from a game"),
	USER_JOIN("/%s join <game name>", "join a game"),
	USER_LEAVE("/%s leave", "leave current game temporarily (if enabled)"),
	USER_LIST("/%s list", "list games"),
	USER_REJOIN("/%s rejoin", "rejoin your current game"),
	USER_SPONSOR("/%s sponsor <player>", "sponsor a player an item"),
	USER_VOTE("/%s vote", "cast your vote that you are ready to play"),
	USER_STAT("/%s stat <game name>", "list stats for a game"),
	USER_QUIT("/%s quit", "quits the current game indefinitely");
	
	private String usage;
	private String info;
	
	private CommandUsage(String usage, String info) {
	    this.usage = usage;
	    this.info = info;
	}
	
	public String getUsage(){
	    return usage;
	}
	
	public String getInfo(){
	    return info;
	}
	
	public String getUsageAndInfo(){
	    return usage + " - " + info;
	}
    }
    
}
