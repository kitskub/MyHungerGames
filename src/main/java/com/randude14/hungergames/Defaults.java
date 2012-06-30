package com.randude14.hungergames;

import org.bukkit.permissions.Permission;

public class Defaults {
	
    public enum Message {
	
	JOIN("<player> has joined the game <game>."),
	REJOIN("<player> has rejoined the game <gam"),
	LEAVE("<player> has left the game <game>."),
	QUIT("<player> has quit the game <game>."),
	VOTE("<player> is ready to play <game>. Type /hg vote when you are ready to play."),
	KILL("<killer> killed <killed> in game <game>.");
	
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
	REQUIRE_INV_CLEAR(false),
	ALL_VOTE(false),
	AUTO_VOTE(false),
	CAN_PLACE_BLOCK(false),
	CAN_BREAK_BLOCK(false),
	CAN_INTERACT_BLOCK(false),
	CAN_TELEPORT(false),
	USE_COMMAND(false),
	AUTO_ADD(true),
	RELOAD_WORLD(false),
	RELOAD_WORLD_NAME("Default_world"),
	FORCE_SURVIVAL(true),
	FREEZE_PLAYERS(true),
	FORCE_DAMAGE(false),
	FORCE_INTERNAL(false),
	ISOLATE_PLAYER_CHAT(true),
	CHAT_DISTANCE(15),
	ALLOW_MINIMAL_MESSAGES(true),
	REMOVE_ITEMS(true);
	
	
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
	
	public float getFloat(){
	    return (Float) value;
	}
	
	public String getString(){
	    return (String) value;	    
	}
    }

    public enum Perm {

	ALL(new Permission("hungergame.*"), null),
	ADMIN(new Permission("hungergame.admin.*"), ALL),
	ADMIN_ADD_SPAWNPOINT(new Permission("hungergame.add.spawnpoint"), ADMIN),
	ADMIN_ADD_CUBOID(new Permission("hungergame.add.cuboid"), ADMIN),
	ADMIN_ADD_CHEST(new Permission("hungergame.add.chest"), ADMIN),
	ADMIN_ADD_GAME(new Permission("hungergame.add.game"), ADMIN),
	ADMIN_ADD_ITEMSET(new Permission("hungergame.add.itemset"), ADMIN),
	ADMIN_ADD_WORLD(new Permission("hungergame.add.world"), ADMIN),
	ADMIN_REMOVE_SPAWNPOINT(new Permission("hungergame.remove.spawnpoint"), ADMIN),
	ADMIN_REMOVE_CHEST(new Permission("hungergame.remove.chest"), ADMIN),
	ADMIN_REMOVE_GAME(new Permission("hungergame.remove.game"), ADMIN),
	ADMIN_REMOVE_ITEMSET(new Permission("hungergame.remove.itemset"), ADMIN),
	ADMIN_SET_ENABLED(new Permission("hungergame.set.enabled"), ADMIN),
	ADMIN_SET_FIXED_CHEST(new Permission("hungergame.set.fixedchest"), ADMIN),
	ADMIN_SET_SPAWN(new Permission("hungergame.set.spawn"), ADMIN),
	ADMIN_STOP(new Permission("hungergame.game.stop"), ADMIN),
	ADMIN_START(new Permission("hungergame.game.start"), ADMIN),
	ADMIN_PAUSE(new Permission("hungergame.game.pause"), ADMIN),
	ADMIN_RESUME(new Permission("hungergame.game.resume"), ADMIN),
	ADMIN_RELOAD(new Permission("hungergame.admin.reload"), ADMIN),
	ADMIN_KICK(new Permission("hungergame.admin.kick"), ADMIN),
	ADMIN_KILL(new Permission("hungergame.admin.kill"), ADMIN),
	ADMIN_HELP(new Permission("hungergame.admin.help"), ADMIN),
	ADMIN_RESTOCK(new Permission("hungergame.admin.restock"), ADMIN),
	USER(new Permission("hungergame.user.*"), ALL),
	USER_JOIN(new Permission("hungergame.user.join"), USER),
	USER_LEAVE(new Permission("hungergame.user.leave"), USER),
	USER_LIST(new Permission("hungergame.user.list"), USER),
	USER_REJOIN(new Permission("hungergame.user.rejoin"), USER),
	USER_SPECTATE(new Permission("hungergame.user.spectate"), USER),
	USER_SPONSOR(new Permission("hungergame.user.sponsor"), USER),
	USER_SUBSCRIBE(new Permission("hungergame.user.subscribe"), USER),
	USER_VOTE(new Permission("hungergame.user.vote"), USER),
	USER_STAT(new Permission("hungergame.user.stat"), USER),
	USER_HELP(new Permission("hungergame.user.help"), USER),
	USER_QUIT(new Permission("hungergame.user.quit"), USER);

	private Permission value;
	private Perm parent;
	
	private Perm(Permission permission, Perm parent) {
		this.value = permission;
		this.parent = parent;
	}
	
	public Permission getPermission(){
		return value;
	}
	
	public Perm getParent() {
		return parent;
	}
    }
    
    public enum Commands {	
	    
	ADMIN_ADD_HELP("ADMIN", "/%s add ?", "type for more help", Perm.ADMIN_HELP),
	ADMIN_ADD_SPAWNPOINT("ADMIN", "/%s add spawnpoint <game name>", "add a spawnpoint", Perm.ADMIN_ADD_SPAWNPOINT),
	ADMIN_ADD_CUBOID("ADMIN", "/%s add cuboid <game name>", "add a cuboid", Perm.ADMIN_ADD_CUBOID),
	ADMIN_ADD_CHEST("ADMIN", "/%s add chest <game name>", "add a chest", Perm.ADMIN_ADD_CHEST),
	ADMIN_ADD_GAME("ADMIN", "/%s add game <game name> [setup]", "add a game", Perm.ADMIN_ADD_GAME),
	ADMIN_ADD_ITEMSET("ADMIN", "/%s add itemset <game name> <itemset name>", "add an itemset", Perm.ADMIN_ADD_ITEMSET),
	ADMIN_ADD_WORLD("ADMIN", "/%s add world <game name> [world]", "adds the world specified or you are currently in to the game", Perm.ADMIN_ADD_WORLD),
	ADMIN_REMOVE_HELP("ADMIN", "/%s remove ?", "type for more help", Perm.ADMIN_HELP),
	ADMIN_REMOVE_SPAWNPOINT("ADMIN", "/%s remove spawnpoint <game name>", "remove a spawnpoint", Perm.ADMIN_REMOVE_SPAWNPOINT),
	ADMIN_REMOVE_CHEST("ADMIN", "/%s remove chest <game name>", "remove a chest", Perm.ADMIN_REMOVE_CHEST),
	ADMIN_REMOVE_GAME("ADMIN", "/%s remove game <game name>", "remove a game", Perm.ADMIN_REMOVE_GAME),
	ADMIN_REMOVE_ITEMSET("ADMIN", "/%s remove itemset <game name> <itemset name>", "remove a game", Perm.ADMIN_REMOVE_ITEMSET),
	ADMIN_SET_HELP("ADMIN", "/%s set ?", "type for more help", Perm.ADMIN_HELP),
	ADMIN_SET_ENABLED("ADMIN", "/%s set enabled <game name> <true/false>", "enable or disable a game", Perm.ADMIN_SET_ENABLED),
	ADMIN_SET_FIXED_CHEST("ADMIN", "/%s set fixedchest <game name> <name|false>", "Sets a chest to a specific fixed chest itemset or removes it from being a fixed chest if name is false", Perm.ADMIN_SET_FIXED_CHEST),
	ADMIN_SET_SPAWN("ADMIN", "/%s set spawn <game name>", "set the spawnpoint for a game", Perm.ADMIN_SET_SPAWN),
	ADMIN_START("ADMIN", "/%s start <game name> [seconds]", "manually start a game", Perm.ADMIN_START),
	ADMIN_STOP("ADMIN", "/%s stop <game name>", "manually stop a game", Perm.ADMIN_STOP),
	ADMIN_PAUSE("ADMIN", "/%s pause <game name>", "pause a game", Perm.ADMIN_PAUSE),
	ADMIN_RESUME("ADMIN", "/%s resume <game name>", "resume a game", Perm.ADMIN_RESUME),
	ADMIN_RELOAD("ADMIN", "/%s rejoin", "reload MyHungerGames", Perm.ADMIN_RELOAD),
	ADMIN_KICK("ADMIN", "/%s kick <player>", "kick a player from a game", Perm.ADMIN_KICK),
	ADMIN_KILL("ADMIN", "/%s kill <player>", "kills a player in a game ", Perm.ADMIN_KILL),
	ADMIN_RESTOCK("ADMIN", "/%s restock <game name>", "restock all a game's chests", Perm.ADMIN_RESTOCK),
	USER_JOIN("USER", "/%s join <game name>", "join a game", Perm.USER_JOIN),
	USER_LEAVE("USER", "/%s leave", "leave current game temporarily (if enabled)", Perm.USER_LEAVE),
	USER_LIST("USER", "/%s list", "list games", Perm.USER_LIST),
	USER_REJOIN("USER", "/%s rejoin", "rejoin your current game", Perm.USER_REJOIN),
	USER_SPECTATE("USER", "/%s spectate <game name> [player]", "sets player to flying to spectate a game or cancels a spectation", Perm.USER_SPECTATE),
	USER_SPONSOR("USER", "/%s sponsor <player>", "sponsor a player an item", Perm.USER_SPONSOR),
	USER_SUBSCRIBE("USER", "/%s subscribe", "subscribe", Perm.USER_SUBSCRIBE),
	USER_VOTE("USER", "/%s vote", "cast your vote that you are ready to play", Perm.USER_VOTE),
	USER_STAT("USER", "/%s stat <game name>", "list stats for a game", Perm.USER_STAT),
	USER_QUIT("USER", "/%s quit", "quits the current game indefinitely", Perm.USER_QUIT);
	
	private String type;
	private String usage;
	private String info;
	private Perm perm;
	
	public static final String ADMIN_COMMAND = "ADMIN";
	public static final String USER_COMMAND = "USER";
	
	private Commands(String type, String usage, String info, Perm perm) {
		this.type = type;
		this.usage = usage;
		this.info = info;
		this.perm = perm;
	}
	
	public String getType() {
		return type;
	}
	
	public Perm getPerm() {
		return perm;
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
