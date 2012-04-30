package com.randude14.hungergames;

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
	USER_HELP("hungergame.user.help");

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
	DEFAULT_GAME("Test");
	
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
    
}
