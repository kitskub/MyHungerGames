package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class GameManager{
	private static final HungerGames plugin = HungerGames.getInstance();
	private static Set<HungerGame> games = new TreeSet<HungerGame>();
	private static CustomYaml yaml = new CustomYaml(new File(plugin.getDataFolder(), "games.yml"));
	private static Map<Player, Location> respawnLocation = new HashMap<Player, Location>();
	private static Map<String, String> sponsors = new HashMap<String, String>(); // <sponsor, sponsee>
	private static Map<String, String> spectators = new HashMap<String, String>(); // <player, game>
	private static Map<String, Location> frozenPlayers = new HashMap<String, Location>();
	
	public static boolean createGame(String name) {
	    HungerGame game = new HungerGame(name);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}

	public static boolean createGame(String name, String setup){
	    HungerGame game = new HungerGame(name, setup);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}
	
	public static boolean removeGame(String name) {
	    HungerGame game = getGame(name);
	    if(game == null) return false;
	    boolean attempt = games.remove(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}

	public static Set<HungerGame> getGames() {
		return games;
	}

	public static HungerGame getGame(String name) {
		for (HungerGame game : games) {
			if (game.getName().equals(name)) {
				return game;
			}

		}
		return null;
	}

	/**
	 * This does not care about whether the player is actually playing the game or not.
	 * If the player has the potential to rejoin, and therefore has lives, that is the game returned.
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public static HungerGame getSession(Player player) {
		for (HungerGame game : games) {
			if (game.contains(player)) {
				return game;
			}

		}
		return null;
	}

	/**
	 * This returns the game a player is playing. If the player is in a game, but not playing, returns null
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public static HungerGame getPlayingSession(Player player) {
		for (HungerGame game : games) {
			if (game.isPlaying(player)) {
				return game;
			}

		}
		return null;
	}

	public static boolean doesNameExist(String name) {
		return getGame(name) != null;
	}
	
	public static void addPlayerRespawn(Player player, Location respawn) {
		if(respawn == null) return;
		respawnLocation.put(player, respawn);
	}
	
	public static Location getRespawnLocation(Player player) {
	    if(respawnLocation.containsKey(player)){
		return respawnLocation.remove(player);
	    }
	    return null;
	}

	public static void playerLeftServer(Player player) {
		sponsors.remove(player.getName());
		HungerGame game = getSession(player);
		if (game == null) return;
		game.quit(player);
		String mess = Config.getQuitMessage(game.getSetup())
			.replace("<player>", player.getName())
			.replace("<game>", game.getName());
		ChatUtils.broadcast(mess);
	}

	public static void loadGames() {
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection gamesSection = config.getConfigurationSection("games");
		if (gamesSection == null) return;
		games.clear();
		for (String name : gamesSection.getKeys(false)) {
			ConfigurationSection gameSection = gamesSection.getConfigurationSection(name);
			HungerGame game = new HungerGame(name);
			game.loadFrom(gameSection);
			games.add(game);
		}
		
	}

	public static void saveGames() {
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection section = config.createSection("games");
		for (HungerGame game : games) {
		    ConfigurationSection saveSection = section.getConfigurationSection(game.getName());
		    if(saveSection == null) {
			saveSection = section.createSection(game.getName());
		    }
		    game.saveTo(saveSection);
		}
		yaml.save();
	}
	
	public static void reloadGame(HungerGame game){
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection gameSection = config.getConfigurationSection("games." + game.getName());
		if (gameSection == null) {
			return;
		}
		game.loadFrom(gameSection);
		games.add(game);
	}

	public static void saveGame(HungerGame game){
	    Logging.log(Level.INFO, "Saving a game");
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection section = config.getConfigurationSection("games");
		if(section == null){
		    section = config.createSection("games");
		}
		ConfigurationSection saveSection = section.getConfigurationSection(game.getName());
		if(saveSection == null) {
		    saveSection = section.createSection(game.getName());
		}
		game.saveTo(saveSection);
		yaml.save();
	}
	
	
	public static boolean addSponsor(Player player, String playerToBeSponsored) {
	    Player sponsoredPlayer = Bukkit.getPlayer(playerToBeSponsored);
	    HungerGame game = GameManager.getPlayingSession(sponsoredPlayer);
	    if (game == null) {
		    ChatUtils.error(player, player.getName() + " is not playing in a game.");
		    return false;
	    }
	    List<String> itemsets = game.getItemSets();
	    if (Config.getGlobalSponsorLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
		    ChatUtils.error(player, "No items are available to sponsor.");
		    return false;
	    }

	    if (!HungerGames.isEconomyEnabled()) {
		    ChatUtils.error(player, "Economy use has been disabled.");
		    return false;
	    }
	    sponsors.put(player.getName(), playerToBeSponsored);
	    ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
	    ChatUtils.send(player, ChatColor.YELLOW, "Type the number next to the item you would like sponsor to %s.",
		    playerToBeSponsored);
	    ChatUtils.send(player, "");
	    int num = 1;
	    Map<ItemStack, Double> itemMap = Config.getAllSponsorLootWithGlobal(itemsets);
	    for (ItemStack item : itemMap.keySet()) {
		    String mess = String.format(">> %d - %s: %d", num, item.getType()
				    .name(), item.getAmount());
		    Set<Enchantment> enchants = item.getEnchantments().keySet();
		    int cntr = 0;
		    if (!enchants.isEmpty()) {
			    mess += ", ";
		    }
		    for (Enchantment enchant : enchants) {
			    mess += String.format("%s: %d", enchant.getName(), item.getEnchantmentLevel(enchant));
			    if (cntr < enchants.size() - 1) {
				    mess += ", ";
			    }
			    cntr++;
		    }
		    ChatUtils.send(player, ChatColor.GOLD, mess);
		    num++;
	    }
	    return true;
	}

	public static Map<String, String> getSponsors() {
		return Collections.unmodifiableMap(sponsors);
	}
	
	public static String removeSponsor(Player player) {
		return sponsors.remove(player.getName());
	}
	
	public static void addSpectator(Player player, String gameName) {
		spectators.put(player.getName(), gameName);
	}
	
	public static String getSpectating(Player player) {
	    if (player == null) return "";    
	    if (!spectators.containsKey(player.getName())) return "";
	    return spectators.get(player.getName());
	}
	
	public static String removeSpectator(Player player) {
		return spectators.remove(player.getName());
	}
	
	public static void freezePlayer(Player player) {
		frozenPlayers.put(player.getName(), player.getLocation());
	}

	public static void unfreezePlayer(Player player) {
		frozenPlayers.remove(player.getName());
	}

	public static boolean isPlayerFrozen(Player player) {
		return frozenPlayers.containsKey(player.getName());
	}
	
	public static Location getFrozenLocation(Player player) {
		if (!frozenPlayers.containsKey(player.getName())) {
			return null;
		}
		return frozenPlayers.get(player.getName());
	}
}
