package com.randude14.hungergames;

import static com.randude14.hungergames.Defaults.Lang.*;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lang {
		
	private static String getGlobalOnly(String config, String def) {
		return Files.LANG.getConfig().getString("global-only." + config, def);
	}
		
	private static String getGlobal(String config, String def) {
		return Files.LANG.getConfig().getString("global." + config, def);
	}
	
	private static List<String> getGlobalStringList(String config, List<String> def) {
		if (Files.LANG.getConfig().contains("global." + config)) {
			return Files.LANG.getConfig().getStringList("global." + config);
		}
		return def;
	}
	
	/** 
	 * For safe recursiveness 
	 * return String if found, null if not
	 */	
	private static String getString(String config, String setup, Set<String> checked) {
		if (checked.contains(setup)) return null;
		if (Files.LANG.getConfig().contains("setups." + setup + "." + config)) {
			return Files.LANG.getConfig().getString("setups." + setup + "." + config);
		}
		checked.add(setup);
		for (String parent : Files.LANG.getConfig().getStringList("setups." + setup + ".inherits")) {
			String s = getString(config, parent, checked);
			if (s != null) return s;
		}
		return null;
	}
	private static String getString(String config, String setup, String def) {
		String s = getString(config, setup, new HashSet<String>());
		return s == null ? def : s;
	}
	
	/** 
	 * For safe recursiveness 
	 * return String if found, null if not
	 */	
	private static List<String> getStringList(String config, String setup, Set<String> checked) {
		if (checked.contains(setup)) return null;
		if (Files.LANG.getConfig().contains("setups." + setup + "." + config)) {
			return Files.LANG.getConfig().getStringList("setups." + setup + "." + config);
		}
		checked.add(setup);
		for (String parent : Files.LANG.getConfig().getStringList("setups." + setup + ".inherits")) {
			List<String> s = getStringList(config, parent, checked);
			if (s != null) return s;
		}
		return null;
	}

	private static List<String> getStringList(String config, String setup, List<String> def) {
		List<String> s = getStringList(config, setup, new HashSet<String>());
		return s == null ? def : s;
	}
	
	// Global only
	public static String getNoPerm() {
		return getGlobalOnly("no-perm", NO_PERM.getMessage());
	}

	public static String getNotExist() {
		return getGlobalOnly("not-exist", NOT_EXIST.getMessage());
	}
	
	// Global
	public static String getGlobalJoinMessage() {
		return getGlobal("join-message", JOIN.getMessage());
	}
	
	public static String getGlobalRejoinMessage() {
		return getGlobal("rejoin-message", REJOIN.getMessage());
	}
	
	public static String getGlobalLeaveMessage() {
		return getGlobal("leave-message", LEAVE.getMessage());
	}
	
	public static String getGlobalQuitMessage() {
		return getGlobal("quit-message", QUIT.getMessage());
	}
	
	public static List<String> getGlobalKillMessages() {
		return getGlobalStringList("kill-messages", Arrays.asList(KILL.getMessage()));
	}
	
	public static String getGlobalVoteMessage() {
		return getGlobal("vote-message", VOTE.getMessage());
	}
	
	public static String getGlobalNoWinner() {
		return getGlobal("no-winner", NO_WINNER.getMessage());
	}
	
	public static String getGlobalWin() {
		return getGlobal("win", WIN.getMessage());
	}
	
	public static String getGlobalAlreadyCountingDown() {
		return getGlobal("already-counting-down", ALREADY_COUNTING_DOWN.getMessage());
	}
	
	public static String getGlobalNotEnabled() {
		return getGlobal("not-enabled", NOT_ENABLED.getMessage());
	}
	
	public static String getGlobalNotRunning() {
		return getGlobal("not-running", NOT_RUNNING.getMessage());
	}
	
	public static String getGlobalRunning() {
		return getGlobal("running", RUNNING.getMessage());
	}
	
	public static String getGlobalInGame() {
		return getGlobal("in-game", IN_GAME.getMessage());
	}
	
	public static String getGlobalNotInGame() {
		return getGlobal("not-in-game", NOT_IN_GAME.getMessage());
	}
	
	public static List<String> getGlobalDeathMessages() {
		return getGlobalStringList("death-messages", Arrays.asList(DEATH.getMessage()));
	}
	
	public static String getGlobalVoted() {
		return getGlobal("voted", VOTED.getMessage());
	}
	
	public static String getGlobalGracePeriodStarted() {
		return getGlobal("graceperiod-started", GRACEPERIOD_STARTED.getMessage());
	}
	public static String getGlobalGracePeriodEnded() {
		return getGlobal("graceperiod-ended", GRACEPERIOD_ENDED.getMessage());
	}
	
	public static String getGlobalNonPvPDeathcause(String cause) {
		return getGlobal("deathcause." + cause.toLowerCase(), "default:" + cause);
	}
	
	// Setups
	public static String getJoinMessage(String setup) {
		return getString("join-message", setup, getGlobalJoinMessage());
	}
	
	public static String getRejoinMessage(String setup) {
		return getString("rejoin-message", setup, getGlobalRejoinMessage());
	}
	
	public static String getLeaveMessage(String setup) {
		return getString("leave-message", setup, getGlobalLeaveMessage());
	}
	
	public static String getQuitMessage(String setup) {
		return getString("quit-message", setup, getGlobalQuitMessage());
	}
	
	public static List<String> getKillMessages(String setup) {
		return getStringList("kill-messages", setup, getGlobalKillMessages());
	}
	
	public static String getVoteMessage(String setup) {
		return getString("vote-message", setup, getGlobalVoteMessage());
	}
	
	public static String getNoWinner(String setup) {
		return getString("no-winner", setup, getGlobalNoWinner());
	}
	
	public static String getWin(String setup) {
		return getString("win", setup, getGlobalWin());
	}
	
	public static String getAlreadyCountingDown(String setup) {
		return getString("already-counting-down", setup, getGlobalAlreadyCountingDown());
	}
	
	public static String getNotEnabled(String setup) {
		return getString("not-enabled", setup, getGlobalNotEnabled());
	}
	
	public static String getNotRunning(String setup) {
		return getString("not-running", setup, getGlobalNotRunning());
	}
	
	public static String getRunning(String setup) {
		return getString("running", setup, getGlobalRunning());
	}
	
	public static String getInGame(String setup) {
		return getString("in-game", setup, getGlobalInGame());
	}
	
	public static String getNotInGame(String setup) {
		return getString("not-in-game", setup, getGlobalNotInGame());
	}
	
	public static List<String> getDeathMessages(String setup) {
		return getStringList("death-messages", setup, getGlobalDeathMessages());
	}
	
	public static String getVoted(String setup) {
		return getString("vote", setup, getGlobalVoted());
	}
	
	public static String getGracePeriodStarted(String setup) {
		return getString("graceperiod-started", setup, getGlobalGracePeriodStarted());
	}
	
	public static String getGracePeriodEnded(String setup) {
		return getString("graceperiod-ended", setup, getGlobalGracePeriodEnded());
	}
	
	public static String getNonPvPDeathcause(String setup, String cause) {
		return getString("deathcause." + cause.toLowerCase(), setup, getGlobalNonPvPDeathcause(cause));
	}
}
