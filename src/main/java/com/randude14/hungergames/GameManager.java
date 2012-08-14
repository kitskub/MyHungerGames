package com.randude14.hungergames;

import com.google.common.base.Strings;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class GameManager{
	private static final HungerGames plugin = HungerGames.getInstance();
	private static final Set<HungerGame> games = new TreeSet<HungerGame>();
	private static final Map<Player, Location> respawnLocation = new HashMap<Player, Location>();
	private static final Map<String, String> spectators = new HashMap<String, String>(); // <player, game>
	private static final Map<String, Location> frozenPlayers = new HashMap<String, Location>();
	private static final Set<String> subscribedPlayers = new HashSet<String>();
	private static final Map<String, Location> playerBackLocations = new HashMap<String, Location>();
	
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
	    game.delete();
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}

	public static Set<HungerGame> getGames() {
		return games;
	}

	public static HungerGame getGame(String name) {
		if (Strings.nullToEmpty(name).equals("")) return null;
		for (HungerGame game : games) {
			if (game.getName().equalsIgnoreCase(name)) {
				return game;
			}

		}
		return null;
	}

	/**
	 * This does not care about whether the player is actually playing the game or not.
	 * This also does not care about whether a game is running
	 * If the player has the potential to rejoin, and therefore has lives, that is the game returned.
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public static HungerGame getSession(Player player) {
		return PlayerStat.getGame(player);
	}

	/**
	 * This returns the game a player is playing. If the player is in a game, but not playing, returns null
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public static HungerGame getPlayingSession(Player player) {
		return PlayerStat.getPlayingGame(player);
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
		if (spectators.containsKey(player.getName())) {
			HungerGame spectated = GameManager.getGame(spectators.remove(player.getName()));
			if (spectated == null) return;
			spectated.removeSpectator(player);
			return;
		}
		HungerGame game = getSession(player);
		if (game == null) return;
		game.leave(player, true);
	}

	public static void loadGames() {
		PerformanceMonitor.startActivity("loadGames");
		ConfigurationSection gamesSection = Files.GAMES.getConfig().getConfigurationSection("games");
		if (gamesSection == null) {
			return;
		}
		games.clear();
		for (String name : gamesSection.getKeys(false)) {
			ConfigurationSection gameSection = gamesSection.getConfigurationSection(name);
			HungerGame game = new HungerGame(name);
			game.loadFrom(gameSection);
			games.add(game);
		}
		PerformanceMonitor.stopActivity("loadGames");
	}

	public static void saveGames() {
		PerformanceMonitor.startActivity("saveGames");
		for (HungerGame game : games) {
		    saveGame(game);
		}
		PerformanceMonitor.stopActivity("saveGames");
	}
	
	public static void reloadGame(HungerGame game){
		ConfigurationSection gameSection = Files.GAMES.getConfig().getConfigurationSection("games." + game.getName());
		if (gameSection == null) {
			return;
		}
		game.loadFrom(gameSection);
		games.add(game);
	}

	public static void saveGame(HungerGame game){
		ConfigurationSection section = Files.GAMES.getConfig().getConfigurationSection("games");
		if(section == null){
		    section = Files.GAMES.getConfig().createSection("games");
		}
		ConfigurationSection saveSection = section.getConfigurationSection(game.getName());
		if(saveSection == null) {
		    saveSection = section.createSection(game.getName());
		}
		game.saveTo(saveSection);
		Files.GAMES.save();
	}
	
	
	public static boolean addSponsor(Player player, Player playerToBeSponsored) {
	    HungerGame game = GameManager.getPlayingSession(playerToBeSponsored);
	    if (game == null) {
		    ChatUtils.error(player, player.getName() + " is not playing in a game.");
		    return false;
	    }
	    ConversationFactory convo = new ConversationFactory(plugin);
	    convo.withFirstPrompt(new SponsorBeginPrompt(game, player, playerToBeSponsored));
	    convo.withEscapeSequence("quit");
	    convo.withTimeout(120);
	    convo.thatExcludesNonPlayersWithMessage("Players only!");
	    convo.buildConversation(player).begin();
	    game.addSponsor(player.getName(), playerToBeSponsored.getName());
	    return true;
	}

	public static void addSpectator(Player player, String gameName) {
		spectators.put(player.getName(), gameName);
	}
	
	public static String getSpectating(Player player) {
	    if (player == null) return null;    
	    if (!spectators.containsKey(player.getName())) return null;
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
	
	public static boolean isPlayerSubscribed(Player player) {
		return subscribedPlayers.contains(player.getName()) || HungerGames.hasPermission(player, Defaults.Perm.USER_AUTO_SUBSCRIBE);
	}
	
	public static void removedSubscribedPlayer(Player player) {
		subscribedPlayers.remove(player.getName());
	}
	
	public static void addSubscribedPlayer(Player player) {
		subscribedPlayers.add(player.getName());
	}
	
	public static void addBackLocation(Player player) {
		playerBackLocations.put(player.getName(), player.getLocation());
	}
	
	public static Location getAndRemoveBackLocation(Player player) {
		return playerBackLocations.remove(player.getName());
	}

	private static class SponsorBeginPrompt extends NumericPrompt {
		HungerGame game;
		Player player;
		Player beingSponsored;
		Map<ItemStack, Double> itemMap = null;
		
		public SponsorBeginPrompt(HungerGame game, Player player, Player playerToBeSponsored) {
			this.game = game;
			this.player = player;
			this.beingSponsored = playerToBeSponsored;
		}
		
		public String getPromptText(ConversationContext cc) {
			List<String> itemsets = game.getItemSets();
			if (ItemConfig.getGlobalSponsorLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
				cc.setSessionData("cancelled", true);
				return "No items are available to sponsor. Reply to exit.";
			}
			if (!HungerGames.isEconomyEnabled()) {
				cc.setSessionData("cancelled", true);
				return "Economy is disabled. Reply to exit.";
			}
			cc.getForWhom().sendRawMessage("Available items to be sponsored:");
			int num = 1;
			itemMap = ItemConfig.getAllSponsorLootWithGlobal(itemsets);
			cc.setSessionData("items", itemMap);
			for (ItemStack item : itemMap.keySet()) {
				String mess = String.format(">> %d - %s: %d", num, item.getType().name(), item.getAmount());
				Set<Enchantment> enchants = item.getEnchantments().keySet();
				for (Enchantment enchant : enchants) {
					mess += ", ";
					mess += String.format("%s: %d", enchant.getName(), item.getEnchantmentLevel(enchant));
				}
				cc.getForWhom().sendRawMessage(ChatColor.GOLD + mess);
				num++;
			}
			return "Select an item by typing the number next to it. Type quit at any time to quit";
		}

		@Override
		protected boolean isInputValid(ConversationContext cc, String string) {
			if (cc.getSessionData(cc) != null && (Boolean) cc.getSessionData(cc) == true) {
				return true;
			}
			return super.isInputValid(cc, string);

		}
		
		@Override
		protected boolean isNumberValid(ConversationContext cc, Number number) {
			if (itemMap == null) return false;
			if (number.intValue() >= itemMap.size()) return false;
			return true;
		}
		
		@Override
		protected String getFailedValidationText(ConversationContext context, String invalidInput) {
			return "That is not a valid choice.";
		}

		@Override
		protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
			return "That is not a valid number.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext cc, Number number) {
			if (cc.getSessionData(cc) != null && (Boolean) cc.getSessionData(cc) == true) {
				return END_OF_CONVERSATION;
			}
			
			int choice = number.intValue() - 1;
			ItemStack item = new ArrayList<ItemStack>(itemMap.keySet()).get(choice);
			double price = itemMap.get(item);
			
			if (beingSponsored == null) {
				cc.getForWhom().sendRawMessage("Sponsee is not online anymore.");
				return END_OF_CONVERSATION;
			}
			if (!HungerGames.hasEnough(beingSponsored, price)) {
				cc.getForWhom().sendRawMessage("You do not have enough money.");
				return END_OF_CONVERSATION;
			}
			
			HungerGames.withdraw(player, price);
			if (item.getEnchantments().isEmpty()) {
				ChatUtils.send(beingSponsored, "%s has sponsored you %d %s(s).",
				player.getName(), item.getAmount(), item.getType().name());
			} else {
				ChatUtils.send(beingSponsored, "%s has sponsored you %d enchanted %s(s).",
				player.getName(), item.getAmount(), item.getType().name());
			}

			for (ItemStack drop : beingSponsored.getInventory().addItem(item).values()) {
				beingSponsored.getWorld().dropItem(beingSponsored.getLocation(),drop);
			}

			if (item.getEnchantments().isEmpty()) {
				ChatUtils.send(beingSponsored, "You have sponsored %s %d %s(s) for $%.2f.",
					player.getName(), item.getAmount(), item.getType().name(), price);
			} else {
				ChatUtils.send(beingSponsored, "You have sponsored %s %d enchanted %s(s) for $%.2f.",
					player.getName(), item.getAmount(), item.getType().name(), price);
			}
			return END_OF_CONVERSATION;
		}
	}
}
