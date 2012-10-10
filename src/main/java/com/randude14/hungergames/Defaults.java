package com.randude14.hungergames;

import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.commands.admin.add.*;
import com.randude14.hungergames.commands.admin.*;
import com.randude14.hungergames.commands.admin.remove.*;
import com.randude14.hungergames.commands.admin.set.*;
import com.randude14.hungergames.commands.user.*;
import org.bukkit.permissions.Permission;

public class Defaults {
	
    public enum Lang {
	
	JOIN("<player> has joined the game <game>."),
	REJOIN("<player> has rejoined the game <gam"),
	LEAVE("<player> has left the game <game>."),
	QUIT("<player> has quit the game <game>."),
	VOTE("<player> is ready to play <game>. Type /hg vote when you are ready to play."),
	KILL("<killer> killed <killed> in game <game>."),
	DEATH("<player> died in <game>"),
	NO_PERM("You do not have permission."),
	NO_WINNER("You do not have permission."),
	WIN("You do not have permission."),
	ALREADY_COUNTING_DOWN("<game> is already counting down."),
	NOT_ENABLED("<game> is currently not enabled."),
	NOT_RUNNING("<game> is not running."),
	NOT_EXIST("<item> does not exist."),
	RUNNING("<game> is an already running game."),
	IN_GAME("You are in <game>."),
	NOT_IN_GAME("You are not in a game.");
	
	
	private String value;
	
	private Lang(String message) {
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
	AUTO_JOIN(false),
	DEFAULT_TIME(10),
	ALLOW_REJOIN(true),
	ALLOW_JOIN_WHILE_RUNNING(false),
	WINNER_KEEPS_ITEMS(true),
	RESPAWN_ON_DEATH(false),
	DEFAULT_GAME("Test"),
	LIVES(1),
	CLEAR_INV(true),
	REQUIRE_INV_CLEAR(false),
	ALL_VOTE(false),
	AUTO_VOTE(false),
	CAN_PLACE_BLOCK(false),
	CAN_BREAK_BLOCK(false),
	CAN_INTERACT_BLOCK(false),
	CAN_TELEPORT(false),
	USE_COMMAND(false),
	AUTO_ADD(true),
	RESET_CHANGES(true),
	FORCE_SURVIVAL(true),
	FREEZE_PLAYERS(true),
	FORCE_DAMAGE(false),
	FORCE_INTERNAL(false),
	ISOLATE_PLAYER_CHAT(true),
	CHAT_DISTANCE(15),
	ALLOW_MINIMAL_MESSAGES(true),
	REMOVE_ITEMS(true),
	USE_MATCH_MATERIAL(true),
	MAX_RANDOM_ITEMS(5),
	SPECTATOR_SPONSOR_PERIOD(0),
	WEBSTATS_IP("http://myhungergames.fragzone.org/dbproxy.php"),
	DEATH_CANNON(1),
	AUTO_JOIN_ALLOWED(true),
	MAX_GAME_DURATION(0),
	USE_SPAWN(true),
	GRACE_PERIOD(0d),
	TIMEOUT(300),
	TAKE_LIFE_ON_LEAVE(true),
	START_TIMER(0),
	STOP_TARGETTING(true),
	HIDE_PLAYERS(true),
	SHOW_DEATH_MESSAGES(1),
	DISABLE_FLY(true);
	
	
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
	
	public double getDouble(){
	    return (Double) value;
	}
	
	public String getString(){
	    return (String) value;	    
	}
    }

    public enum Perm {

	ALL(new Permission("hungergame.*"), null),
	ADMIN(new Permission("hungergame.admin.*"), ALL),
	ADMIN_ADD_CUBOID(new Permission("hungergame.add.cuboid"), ADMIN),
	ADMIN_ADD_CHEST(new Permission("hungergame.add.chest"), ADMIN),
	ADMIN_ADD_CHEST_LOOT(new Permission("hungergame.add.chestloot"), ADMIN),
	ADMIN_ADD_GAME(new Permission("hungergame.add.game"), ADMIN),
	ADMIN_ADD_GAME_SIGN(new Permission("hungergame.add.game"), ADMIN),
	ADMIN_ADD_HELP(new Permission("hungergame.add.help"), ADMIN),
	ADMIN_ADD_INFO_WALL(new Permission("hungergame.add.infowall"), ADMIN),
	ADMIN_ADD_ITEMSET(new Permission("hungergame.add.itemset"), ADMIN),
	ADMIN_ADD_JOIN_SIGN(new Permission("hungergame.add.joinsign"), ADMIN),
	ADMIN_ADD_REWARD(new Permission("hungergame.add.reward"), ADMIN),
	ADMIN_ADD_SPAWNPOINT(new Permission("hungergame.add.spawnpoint"), ADMIN),
	ADMIN_ADD_SPONSOR_LOOT(new Permission("hungergame.add.sponsorloot"), ADMIN),
	ADMIN_ADD_WORLD(new Permission("hungergame.add.world"), ADMIN),
	ADMIN_CHAT(new Permission("hungergame.admin.chat"), ADMIN),
	ADMIN_CREATE_SIGN(new Permission("hungergame.create.sign"), ADMIN),
	ADMIN_CREATE_SIGN_GAMEEND(new Permission("hungergame.create.sign.gameend"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_GAMEPAUSE(new Permission("hungergame.create.sign.gamepause"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_GAMESTART(new Permission("hungergame.create.sign.gamestart"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_PLAYERJOIN(new Permission("hungergame.create.sign.playerjoin"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_PLAYERKICK(new Permission("hungergame.create.sign.playerkick"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_PLAYERKILL(new Permission("hungergame.create.sign.playerkill"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_PLAYERLEAVE(new Permission("hungergame.create.sign.playerleave"), ADMIN_CREATE_SIGN),
	ADMIN_CREATE_SIGN_PLAYERQUIT(new Permission("hungergame.create.sign.playerquit"), ADMIN_CREATE_SIGN),
	ADMIN_REMOVE_SPAWNPOINT(new Permission("hungergame.remove.spawnpoint"), ADMIN),
	ADMIN_REMOVE_CHEST(new Permission("hungergame.remove.chest"), ADMIN),
	ADMIN_REMOVE_GAME(new Permission("hungergame.remove.game"), ADMIN),
	ADMIN_REMOVE_HELP(new Permission("hungergame.remove.help"), ADMIN),
	ADMIN_REMOVE_ITEMSET(new Permission("hungergame.remove.itemset"), ADMIN),
	ADMIN_REMOVE_SIGN(new Permission("hungergame.remove.sign"), ADMIN),
	ADMIN_SET_ENABLED(new Permission("hungergame.set.enabled"), ADMIN),
	ADMIN_SET_FIXED_CHEST(new Permission("hungergame.set.fixedchest"), ADMIN),
	ADMIN_SET_HELP(new Permission("hungergame.set.help"), ADMIN),
	ADMIN_SET_SPAWN(new Permission("hungergame.set.spawn"), ADMIN),
	ADMIN_FORCE_CLEAR(new Permission("hungergame.game.forceclear"), ADMIN),
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
	USER_ABOUT(new Permission("hungergame.user.about"), USER),
	USER_AUTO_SUBSCRIBE(new Permission("hungergame.user.autosubscribe"), null),
	USER_AUTO_JOIN_ALLOWED(new Permission("hungergame.user.autojoinallowed"), USER),
	USER_BACK(new Permission("hungergame.user.back"), USER),
	USER_ALLOW_FLIGHT(new Permission("hungergame.user.allowflight"), USER),
	USER_JOIN(new Permission("hungergame.user.join"), USER),
	USER_KIT(new Permission("hungergame.user.kit"), USER),
	USER_LEAVE(new Permission("hungergame.user.leave"), USER),
	USER_LIST(new Permission("hungergame.user.list"), USER),
	USER_REJOIN(new Permission("hungergame.user.rejoin"), USER),
	USER_SEARCH(new Permission("hungergame.user.search"), USER),
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
	    
	ADMIN_ADD_HELP(new AddHelp()),
	ADMIN_ADD_CUBOID(new AddCuboidCommand()),
	ADMIN_ADD_CHEST(new AddChestCommand()),
	ADMIN_ADD_CHEST_LOOT(new AddChestLootCommand()),
	ADMIN_ADD_GAME(new AddGameCommand()),
	ADMIN_ADD_GAME_SIGN(new AddGameSignCommand()),
	ADMIN_ADD_INFO_WALL(new AddInfoWallCommand()),
	ADMIN_ADD_ITEMSET(new AddItemSetCommand()),
	ADMIN_ADD_JOIN_SIGN(new AddJoinSignCommand()),
	ADMIN_ADD_REWARD(new AddRewardCommand()),
	ADMIN_ADD_SPAWNPOINT(new AddSpawnPointCommand()),
	ADMIN_ADD_SPONSOR_LOOT(new AddSponsorLootCommand()),
	ADMIN_ADD_WORLD(new AddWorldCommand()),
	ADMIN_REMOVE_HELP(new RemoveHelp()),
	ADMIN_REMOVE_CHEST(new RemoveChestCommand()),
	ADMIN_REMOVE_GAME(new RemoveGameCommand()),
	ADMIN_REMOVE_ITEMSET(new RemoveItemSetCommand()),
	ADMIN_REMOVE_SIGN(new RemoveSignCommand()),
	ADMIN_REMOVE_SPAWNPOINT(new RemoveSpawnPointCommand()),
	ADMIN_SET_HELP(new SetHelp()),
	ADMIN_SET_ENABLED(new SetEnabledCommand()),
	ADMIN_SET_FIXED_CHEST(new SetFixedChestCommand()),
	ADMIN_SET_SPAWN(new SetSpawnCommand()),
	ADMIN_FORCE_CLEAR(new ForceClearCommand()),
	ADMIN_START(new StartCommand()),
	ADMIN_STOP(new StopCommand()),
	ADMIN_PAUSE(new PauseCommand()),
	ADMIN_RESUME(new ResumeCommand()),
	ADMIN_RELOAD(new ReloadCommand()),
	ADMIN_KICK(new KickCommand()),
	ADMIN_KILL(new KillCommand()),
	ADMIN_RESTOCK(new RestockCommand()),
	USER_ABOUT(new AboutCommand()),
	USER_BACK(new BackCommand()),
	USER_JOIN(new JoinCommand()),
	USER_LEAVE(new LeaveCommand()),
	USER_LIST(new ListCommand()),
	USER_QUIT(new QuitCommand()),
	USER_REJOIN(new RejoinCommand()),
	USER_SEARCH(new SearchCommand()),
	USER_SPECTATE(new SpectateCommand()),
	USER_SPONSOR(new SponsorCommand()),
	USER_STAT(new StatCommand()),
	USER_SUBSCRIBE(new SubscribeCommand()),
	USER_VOTE(new VoteCommand());
	
	private Command command;
	
	private Commands(Command command) {
		this.command = command;
	}
	
	public 	com.randude14.hungergames.commands.Command getCommand() {
		return command;
	}
	
	public static void init() {} // Just so the class gets loaded
    }

}