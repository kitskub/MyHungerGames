package me.kitskub.hungergames;

import com.google.common.base.Strings;

import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.*;
import me.kitskub.hungergames.games.User;

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


public class GameManager extends me.kitskub.hungergames.api.GameManager {
	private static final HungerGames plugin = HungerGames.getInstance();
	//private static Map<String, Map<EquatableWeakReference<HungerGame>, PlayerStat>> stats = new HashMap<String, Map<EquatableWeakReference<HungerGame>, PlayerStat>>();
	private static final Set<HungerGame> games = new TreeSet<HungerGame>();
	private static final Map<String, EquatableWeakReference<HungerGame>> spectators = new HashMap<String, EquatableWeakReference<HungerGame>>(); // <player, game>
	private static final Map<String, Location> frozenPlayers = new HashMap<String, Location>();
	private static final Set<String> globalSubscribedPlayers = new HashSet<String>();
	private static final Map<EquatableWeakReference<HungerGame>, Set<String>> subscribedPlayers = new HashMap<EquatableWeakReference<HungerGame>, Set<String>>();
	private static final Map<String, Location> playerBackLocations = new HashMap<String, Location>();
	
	@Override
	public HungerGame createGame(String name) {
	    HungerGame game = new HungerGame(name);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
		return game;
	    } else {
		    return null;
	    }
	}

	@Override
	public HungerGame createGame(String name, String setup){
	    HungerGame game = new HungerGame(name, setup);
	    boolean attempt = games.add(game);
	    if(attempt){
		saveGames();
		return game;
	    } else {
		return null;
	    }
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
		
	//public PlayerStat createStat(HungerGame game, Player player) {
	//	PlayerStat stat = new PlayerStat(game, player);
	//	if (stats.get(player.getName()) == null) stats.put(player.getName(), new HashMap<EquatableWeakReference<HungerGame>, PlayerStat>());
	//	stats.get(player.getName()).put(new EquatableWeakReference<HungerGame>(game), stat);
	//	return stat;
	//}
	
	//public void clearGamesForPlayer(String player, HungerGame game) {
	//	stats.get(player).remove(new EquatableWeakReference<HungerGame>(game));
	//}

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
	public boolean doesNameExist(String name) {
		return getRawGame(name) != null;
	}

	public void playerLeftServer(Player player) {
		if (spectators.containsKey(player.getName())) {
			WeakReference<HungerGame> spectated = spectators.remove(player.getName());
			if (spectated.get() == null || spectated == null) return;
			spectated.get().removeSpectator(player);
			return;
		}
		User.get(player).leaveGame();
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

	@Override
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

	@Override
	public void saveGame(Game game) {
		ConfigurationSection section = Files.GAMES.getConfig().getConfigurationSection("games");
		if(section == null){
		    section = Files.GAMES.getConfig().createSection("games");
		}
		ConfigurationSection saveSection = section.createSection(game.getName());
		((HungerGame) game).saveTo(saveSection);
		Files.GAMES.save();
	}
	
	@Override
	public boolean addSponsor(Player player, Player playerToBeSponsored) {
	    Game game = User.get(playerToBeSponsored).getGameInEntry().getGame();
	    if (game == null) {
		    ChatUtils.error(player, player.getName() + " is not playing in a game.");
		    return false;
	    }
	    ConversationFactory convo = new ConversationFactory(plugin);
	    convo.withFirstPrompt(new SponsorBeginPrompt(((HungerGame) game), player, playerToBeSponsored));
	    convo.withEscapeSequence("quit");
	    convo.withTimeout(120);
	    convo.thatExcludesNonPlayersWithMessage("Players only!");
	    convo.buildConversation(player).begin();
	    ((HungerGame) game).addSponsor(player.getName(), playerToBeSponsored.getName());
	    return true;
	}

	@Override
	public boolean addSpectator(Player player, Game game, Player spectated) {
		if (spectators.containsKey(player.getName())) return false;
		if (!((HungerGame) game).addSpectator(player, spectated)) return false;
		spectators.put(player.getName(), new EquatableWeakReference<HungerGame>((HungerGame) game));
		return true;
	}
	
	@Override
	public EquatableWeakReference<HungerGame> getSpectating(Player player) {
	    if (player == null) return null;    
	    if (!spectators.containsKey(player.getName())) return null;
	    return spectators.get(player.getName());
	}
	
	@Override
	public boolean removeSpectator(Player player) {
		WeakReference<HungerGame> game = spectators.remove(player.getName());
		if (game != null && game.get() != null) {
			game.get().removeSpectator(player);
			return true;
		}
		return false;
	}
	
	@Override
	public void freezePlayer(Player player) {
		frozenPlayers.put(player.getName(), player.getLocation());
	}

	@Override
	public void unfreezePlayer(Player player) {
		frozenPlayers.remove(player.getName());
	}

	@Override
	public boolean isPlayerFrozen(Player player) {
		return frozenPlayers.containsKey(player.getName());
	}
	
	@Override
	public Location getFrozenLocation(Player player) {
		if (!frozenPlayers.containsKey(player.getName())) {
			return null;
		}
		return frozenPlayers.get(player.getName());
	}
	
	@Override
	public boolean isPlayerSubscribed(Player player, Game game) {
		if (HungerGames.hasPermission(player, Defaults.Perm.USER_AUTO_SUBSCRIBE)) return true;
		if (game != null){
			if (subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)) == null) {
				subscribedPlayers.put(new EquatableWeakReference<HungerGame>(((HungerGame) game)), new HashSet<String>());
			}
			if (subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)).contains(player.getName())) return true;
		}
		return globalSubscribedPlayers.contains(player.getName());
	}
	
	@Override
	public void removedSubscribedPlayer(Player player, Game game) {
		if (game != null) {
			if (subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)) == null) {
				subscribedPlayers.put(new EquatableWeakReference<HungerGame>(((HungerGame) game)), new HashSet<String>());
			}
			subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)).remove(player.getName());
		}
		else {
			globalSubscribedPlayers.remove(player.getName());
		}
	}
	
	@Override
	public void addSubscribedPlayer(Player player, Game game) {
		if (game != null) {
			if (subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)) == null) {
				subscribedPlayers.put(new EquatableWeakReference<HungerGame>(((HungerGame) game)), new HashSet<String>());
			}
			subscribedPlayers.get(new EquatableWeakReference<HungerGame>((HungerGame) game)).add(player.getName());
		}
		else {
			globalSubscribedPlayers.add(player.getName());
		}
	}
	
	public Set<String> getSubscribedPlayers(HungerGame game) {
		Set<String> set = new HashSet<String>();
		if (game != null) {
			set.addAll(subscribedPlayers.get(new EquatableWeakReference<HungerGame>(game)));
		} else {
			set.addAll(globalSubscribedPlayers);
		}
		return set;
	}
	
	public void addBackLocation(Player player) {
		playerBackLocations.put(player.getName(), player.getLocation());
	}
	
	public Location getAndRemoveBackLocation(Player player) {
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
			if (game == null) {
				cc.setSessionData("cancelled", true);
				return "This game no longer exists. Reply to exit.";
			}
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
