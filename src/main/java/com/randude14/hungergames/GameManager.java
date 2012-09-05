package com.randude14.hungergames;

import com.google.common.base.Strings;
import com.randude14.hungergames.api.Game;
import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.core.Location;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;


public class GameManager extends com.randude14.hungergames.api.GameManager {
	private static final HungerGamesPlugin plugin = HungerGames.getPlugin();
	public static final GameManager INSTANCE = new GameManager();
	private static Map<String, Map<EquatableWeakReference<HungerGame>, PlayerStat>> stats = new HashMap<String, Map<EquatableWeakReference<HungerGame>, PlayerStat>>();
	private static final Set<HungerGame> games = new TreeSet<HungerGame>();
	private static final Map<LocalPlayer, Location> respawnLocation = new HashMap<LocalPlayer, Location>();
	private static final Map<String, EquatableWeakReference<HungerGame>> spectators = new HashMap<String, EquatableWeakReference<HungerGame>>(); // <player, game>
	private static final Map<String, Location> frozenPlayers = new HashMap<String, Location>();
	private static final Set<String> subscribedPlayers = new HashSet<String>();
	private static final Map<String, Location> playerBackLocations = new HashMap<String, Location>();
	
	@Override
	public boolean createGame(String name) {
	    HungerGame game = new HungerGame(name);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}

	@Override
	public boolean createGame(String name, String setup){
	    HungerGame game = new HungerGame(name, setup);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}
	
	@Override
	public boolean removeGame(String name) {
		HungerGame game = null;
		if (Strings.nullToEmpty(name).equals("")) return false;
		for (HungerGame g : games) {
			if (g.getName().equalsIgnoreCase(name)) {
				game = g;
			}

		}
		if(game == null) return false;
		boolean attempt = games.remove(game);
		game.delete();
		if(attempt){
			saveGames();
		}
		return attempt;
	}
		
	public PlayerStat createStat(HungerGame game, LocalPlayer player) {
		PlayerStat stat = new PlayerStat(game, player);
		if (stats.get(player.getName()) == null) stats.put(player.getName(), new HashMap<EquatableWeakReference<HungerGame>, PlayerStat>());
		stats.get(player.getName()).put(new EquatableWeakReference<HungerGame>(game), stat);
		return stat;
	}
	
	public void clearGamesForPlayer(String player, HungerGame game) {
		stats.get(player).remove(new EquatableWeakReference<HungerGame>(game));
	}

	@Override
	public List<HungerGame> getRawGames() {
		return new ArrayList<HungerGame>(games);
	}

	
	@Override
	public List<EquatableWeakReference<HungerGame>> getGames() {
		List<EquatableWeakReference<HungerGame>> list = new ArrayList<EquatableWeakReference<HungerGame>>();
		for (HungerGame game : games ) {
			list.add(new EquatableWeakReference<HungerGame>(game));
		}
		return list;
	}

	@Override
	public EquatableWeakReference<HungerGame> getGame(String name) {
		HungerGame game = getRawGame(name);
		if (game != null) return new EquatableWeakReference<HungerGame>(game);
		return null;
	}

	@Override
	public HungerGame getRawGame(String name) {
		if (Strings.nullToEmpty(name).equals("")) return null;
		for (HungerGame game : games) {
			if (game.getName().equalsIgnoreCase(name)) {
				return game;
			}

		}
		return null;
	}
	
	

	@Override
	public WeakReference<HungerGame> getSession(LocalPlayer player) {
		if (stats.get(player.getName()) != null) {
			for (EquatableWeakReference<HungerGame> gameGotten : stats.get(player.getName()).keySet()) {
				PlayerStat stat = stats.get(player.getName()).get(gameGotten);
				if (stat != null && stat.getState() != PlayerStat.PlayerState.DEAD && stat.getState() != PlayerStat.PlayerState.NOT_IN_GAME) return gameGotten;
			}
		}
		return null; 
	}

	@Override
	public HungerGame getRawSession(LocalPlayer player) {
		WeakReference<HungerGame> session = getSession(player);
		return session == null ? null : session.get();
	}

	@Override
	public WeakReference<HungerGame> getPlayingSession(LocalPlayer player) {
		if (stats.get(player.getName()) != null) {
			for (EquatableWeakReference<HungerGame> gameGotten : stats.get(player.getName()).keySet()) {
				PlayerStat stat = stats.get(player.getName()).get(gameGotten);
				if (stat != null && (stat.getState() == PlayerStat.PlayerState.PLAYING || stat.getState() == PlayerStat.PlayerState.WAITING)) return gameGotten;
			}
		}
		return null;
	}

	@Override
	public HungerGame getRawPlayingSession(LocalPlayer player) {
		WeakReference<HungerGame> session = getPlayingSession(player);
		return session == null ? null : session.get();
	}

	@Override
	public boolean doesNameExist(String name) {
		return getRawGame(name) != null;
	}

	public void playerLeftServer(LocalPlayer player) {
		if (spectators.containsKey(player.getName())) {
			WeakReference<HungerGame> spectated = spectators.remove(player.getName());
			if (spectated.get() == null || spectated == null) return;
			spectated.get().removeSpectator(player);
			return;
		}
		WeakReference<HungerGame> game = getSession(player);
		if (game == null || game.get() == null) return;
		game.get().leave(player, true);
	}

	public void loadGames() {
		ConfigurationSection gamesSection = Files.GAMES.getConfig().getConfigurationSection("games");
		if (gamesSection == null) {
			return;
		}
		List<String> checked = new ArrayList<String>();
		for (Iterator<HungerGame> it = games.iterator(); it.hasNext();) {
			HungerGame game = it.next();
			checked.add(game.getName());
			if (gamesSection.contains(game.getName())) {
				game.loadFrom(gamesSection.getConfigurationSection(game.getName()));
			}
			else {
				game.delete();
				it.remove();
			}
		}
		for (String name : gamesSection.getKeys(false)) {
			if (checked.contains(name)) continue;
			HungerGame game = new HungerGame(name);
			game.loadFrom(gamesSection.getConfigurationSection(name));
			games.add(game);
		}
	}

	public void saveGames() {
		for (HungerGame game : games) {
		    saveGame(game);
		}
	}
	
	public void reloadGame(HungerGame game){
		ConfigurationSection gameSection = Files.GAMES.getConfig().getConfigurationSection("games." + game.getName());
		if (gameSection == null) {
			return;
		}
		game.loadFrom(gameSection);
		games.add(game);
	}

	public void saveGame(HungerGame game){
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
	
	@Override
	public boolean addSponsor(LocalPlayer player, LocalPlayer playerToBeSponsored) {
	    WeakReference<HungerGame> game = getPlayingSession(playerToBeSponsored);
	    if (game == null || game.get() == null) {
		    ChatUtils.error(player, player.getName() + " is not playing in a game.");
		    return false;
	    }
	    ConversationFactory convo = new ConversationFactory(plugin);
	    convo.withFirstPrompt(new SponsorBeginPrompt(game, player, playerToBeSponsored));
	    convo.withEscapeSequence("quit");
	    convo.withTimeout(120);
	    convo.thatExcludesNonPlayersWithMessage("Players only!");
	    convo.buildConversation(player).begin();
	    game.get().addSponsor(player.getName(), playerToBeSponsored.getName());
	    return true;
	}

	@Override
	public boolean addSpectator(LocalPlayer player, Game game, LocalPlayer spectated) {
		if (spectators.containsKey(player.getName())) return false;
		if (!((HungerGame) game).addSpectator(player, spectated)) return false;
		spectators.put(player.getName(), new EquatableWeakReference<HungerGame>((HungerGame) game));
		return true;
	}
	
	@Override
	public EquatableWeakReference<HungerGame> getSpectating(LocalPlayer player) {
	    if (player == null) return null;    
	    if (!spectators.containsKey(player.getName())) return null;
	    return spectators.get(player.getName());
	}
	
	@Override
	public boolean removeSpectator(LocalPlayer player) {
		WeakReference<HungerGame> game = spectators.remove(player.getName());
		if (game != null && game.get() != null) {
			game.get().removeSpectator(player);
			return true;
		}
		return false;
	}
	
	@Override
	public void freezePlayer(LocalPlayer player) {
		frozenPlayers.put(player.getName(), player.getLocation());
	}

	@Override
	public void unfreezePlayer(LocalPlayer player) {
		frozenPlayers.remove(player.getName());
	}

	@Override
	public boolean isPlayerFrozen(LocalPlayer player) {
		return frozenPlayers.containsKey(player.getName());
	}
	
	@Override
	public Location getFrozenLocation(LocalPlayer player) {
		if (!frozenPlayers.containsKey(player.getName())) {
			return null;
		}
		return frozenPlayers.get(player.getName());
	}
	
	@Override
	public boolean isPlayerSubscribed(LocalPlayer player) {
		return subscribedPlayers.contains(player.getName()) || HungerGames.hasPermission(player, Defaults.Perm.USER_AUTO_SUBSCRIBE);
	}
	
	@Override
	public void removedSubscribedPlayer(LocalPlayer player) {
		subscribedPlayers.remove(player.getName());
	}
	
	@Override
	public void addSubscribedPlayer(LocalPlayer player) {
		subscribedPlayers.add(player.getName());
	}
	
	public void addBackLocation(LocalPlayer player) {
		playerBackLocations.put(player.getName(), player.getLocation());
	}
	
	public Location getAndRemoveBackLocation(LocalPlayer player) {
		return playerBackLocations.remove(player.getName());
	}

	private static class SponsorBeginPrompt extends NumericPrompt {
		WeakReference<HungerGame> game;
		LocalPlayer player;
		LocalPlayer beingSponsored;
		Map<ItemStack, Double> itemMap = null;
		
		public SponsorBeginPrompt(WeakReference<HungerGame> game, LocalPlayer player, LocalPlayer playerToBeSponsored) {
			this.game = game;
			this.player = player;
			this.beingSponsored = playerToBeSponsored;
		}
		
		public String getPromptText(ConversationContext cc) {
			if (game.get() == null) {
				cc.setSessionData("cancelled", true);
				return "This game no longer exists. Reply to exit.";
			}
			List<String> itemsets = game.get().getItemSets();
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
			if (cc.getSessionData("cancelled") != null && (Boolean) cc.getSessionData("cancelled") == true) {
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
