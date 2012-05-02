package com.randude14.lotteryplus.lottery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.Config;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.util.SignFormatter;
import com.randude14.lotteryplus.util.TimeConstants;

public class Lottery implements TimeConstants, Runnable {
	private final Plugin plugin;
	private final LotteryTimer timer;
	private final List<ItemStack> itemRewards;
	private final List<Sign> signs;
	private final List<String> players;
	private final String name;
	private SignFormatter formatter;
	private String winner;
	private long cooldown;
	private double ticketCost;
	private double pot;
	private double ticketTax;
	private double potTax;
	private boolean itemOnly;
	private boolean drawing;
	private boolean repeat;
	private int maxTickets;
	private int maxPlayers;
	private int minPlayers;
	private int maxTicketsDraw;

	public Lottery(Plugin plugin, String name) {
		this.plugin = plugin;
		this.itemRewards = new ArrayList<ItemStack>();
		this.signs = new ArrayList<Sign>();
		this.players = new ArrayList<String>();
		this.name = name;
		this.timer = new LotteryTimer(this);
		this.formatter = Config.getLotterySignFormatter(this);
		drawing = false;
	}

	public void readSavedData(ConfigurationSection section) {
		this.repeat = section.getBoolean("repeat", Boolean.TRUE);
		this.ticketCost = section.getDouble("ticketcost",
				Config.getDefaultTicketCost());
		this.pot = section.getDouble("pot", Config.getDefaultPot());
		this.itemOnly = section.getBoolean("item-only", Boolean.FALSE);
		this.maxTickets = section.getInt("max-tickets",
				Config.getDefaultMaxTickets());
		this.maxPlayers = section.getInt("max-players",
				Config.getDefaultMaxPlayers());
		this.minPlayers = section.getInt("min-players",
				Config.getDefaultMinPlayers());
		this.winner = section.getString("past-winner");
		this.ticketTax = section.getDouble("ticket-tax", 0.0);
		this.potTax = section.getDouble("pot-tax", 0.0);
		this.cooldown = section.getLong("cooldown", 0L);
		this.maxTicketsDraw = section.getInt("max-tickets-draw", -1);

		timer.setTime(section.getLong("save-time"));
		timer.setResetTime(section.getLong("reset-time"));
		timer.setRunning(section.getBoolean("timer-running"));

		if (timer.isOver() && !timer.isRunning()) {
			timer.setRunning(true);
		}

		if (section.contains("players")) {
			ConfigurationSection players = section
					.getConfigurationSection("players");

			for (String player : players.getKeys(false)) {
				int ticketsBought = players.getInt(player);

				for (int cntr = 0; cntr < ticketsBought; cntr++) {
					this.players.add(player);
				}

			}

		}
		if (section.contains("signs")) {
			ConfigurationSection signSection = section
					.getConfigurationSection("signs");

			for (String signName : signSection.getKeys(false)) {
				ConfigurationSection locSection = signSection
						.getConfigurationSection(signName);
				double x = locSection.getDouble("x");
				double y = locSection.getDouble("y");
				double z = locSection.getDouble("z");
				World world = plugin.getServer().getWorld(
						locSection.getString("world"));
				if (world != null) {
					Location loc = new Location(world, x, y, z);
					if(!plugin.isSign(loc)) continue;
					signs.add((Sign)loc.getBlock().getState());
				}

			}

		}
		if (section.contains("item-rewards")) {
			ConfigurationSection itemRewardsSection = section
					.getConfigurationSection("item-rewards");

			for (String matName : itemRewardsSection.getKeys(false)) {
				ConfigurationSection itemSection = itemRewardsSection
						.getConfigurationSection(matName);
				Material material = Material.getMaterial(matName);

				if (material != null) {
					int amount = itemSection.getInt("stack-size", 1);
					ItemStack item = null;
					if (material.getData() != null
							&& itemSection.contains("data")) {
						byte data = (byte) itemSection.getInt("data");
						item = new ItemStack(material, amount, data);
					}

					else {
						item = new ItemStack(material, amount);
					}

					for (String enchantName : itemSection.getKeys(false)) {
						Enchantment enchantment = Enchantment
								.getByName(enchantName);

						if (enchantment != null
								&& enchantment.canEnchantItem(item)) {
							int level = itemSection.getInt(enchantName, 1);

							try {
								item.addEnchantment(enchantment, level);
							} catch (Exception ex) {
								plugin.warning(name + " - invalid enchantment "
										+ enchantment.getName()
										+ " when applying " + level
										+ " to item " + material.name());
							}

						}

					}
					itemRewards.add(item);
				}

			}

		}

	}

	public void loadData(ConfigurationSection section) {
		players.clear();
		itemRewards.clear();
		this.repeat = section.getBoolean("repeat", Boolean.TRUE);
		this.ticketCost = section.getDouble("ticketcost",
				Config.getDefaultTicketCost());
		this.pot = section.getDouble("pot", Config.getDefaultPot());
		this.itemOnly = section.getBoolean("item-only", Boolean.FALSE);
		this.maxTickets = section.getInt("max-tickets",
				Config.getDefaultMaxTickets());
		this.maxPlayers = section.getInt("max-players",
				Config.getDefaultMaxPlayers());
		this.minPlayers = section.getInt("min-players",
				Config.getDefaultMinPlayers());
		double d = section.getDouble("time", Config.getDefaultTime());
		long time = (long) Math.floor(d * HOUR);
		this.timer.setTime(time);
		this.timer.setResetTime(time);
		this.ticketTax = section.getDouble("ticket-tax", 0.0);
		this.potTax = section.getDouble("pot-tax", 0.0);
		this.cooldown = section.getLong("cooldown", 0L);
		this.maxTicketsDraw = section.getInt("max-tickets-draw", -1);

		if (section.contains("item-rewards")) {
			ConfigurationSection itemRewardsSection = section
					.getConfigurationSection("item-rewards");

			for (String matName : itemRewardsSection.getKeys(false)) {
				ConfigurationSection itemSection = itemRewardsSection
						.getConfigurationSection(matName);
				Material material = Material.getMaterial(matName);

				if (material != null) {
					int amount = itemSection.getInt("stack-size", 1);
					ItemStack item = null;
					if (material.getData() != null
							&& itemSection.contains("data")) {
						byte data = (byte) itemSection.getInt("data");
						item = new ItemStack(material, amount, data);
					}

					else {
						item = new ItemStack(material, amount);
					}

					for (String enchantName : itemSection.getKeys(false)) {
						Enchantment enchantment = Enchantment
								.getByName(enchantName);

						if (enchantment != null
								&& enchantment.canEnchantItem(item)) {
							int level = itemSection.getInt(enchantName, 1);

							try {
								item.addEnchantment(enchantment, level);
							} catch (Exception ex) {
								plugin.warning(name + " - invalid enchantment "
										+ enchantment.getName()
										+ " when applying " + level
										+ " to item " + material.name());
							}

						}

					}
					itemRewards.add(item);
				}

			}

		}

	}

	private void readResetData() {
		ConfigurationSection section = getDataSection();
		long time = section.getLong("reset-add-time", 0L);
		this.timer.setResetTime(timer.getResetTime() + time);
		this.ticketCost += section.getDouble("reset-add-ticketcost", 0.0);
		this.pot += section.getDouble("reset-add-pot", 0.0);
		this.cooldown += section.getLong("reset-add-cooldown", 0L);
		this.maxTickets += section.getInt("reset-add-max-tickets", 0);
		this.minPlayers += section.getInt("reset-add-min-players", 0);
		this.maxPlayers += section.getInt("reset-add-max-players", 0);
		this.ticketTax += section.getDouble("reset-add-ticket-tax", 0.0);
		this.potTax += section.getDouble("reset-add-pot-tax", 0.0);

		if (section.contains("reset-add-item-rewards")) {
			ConfigurationSection itemRewardsSection = section
					.getConfigurationSection("reset-add-item-rewards");

			for (String matName : itemRewardsSection.getKeys(false)) {
				ConfigurationSection itemSection = itemRewardsSection
						.getConfigurationSection(matName);
				Material material = Material.getMaterial(matName);

				if (material != null) {
					int amount = itemSection.getInt("stack-size", 1);
					ItemStack item = null;
					if (material.getData() != null
							&& itemSection.contains("data")) {
						byte data = (byte) itemSection.getInt("data");
						item = new ItemStack(material, amount, data);
					}

					else {
						item = new ItemStack(material, amount);
					}

					for (String enchantName : itemSection.getKeys(false)) {
						Enchantment enchantment = Enchantment
								.getByName(enchantName);

						if (enchantment != null
								&& enchantment.canEnchantItem(item)) {
							int level = itemSection.getInt(enchantName, 1);

							try {
								item.addEnchantment(enchantment, level);
							} catch (Exception ex) {
								plugin.warning(name + " - invalid enchantment "
										+ enchantment.getName()
										+ " when applying " + level
										+ " to item " + material.name());
							}

						}

					}
					itemRewards.add(item);
				}

			}

		}

	}

	public ConfigurationSection getDataSection() {
		LotteryManager manager = plugin.getLotteryManager();
		manager.reloadConfig();
		ConfigurationSection section = manager.getConfig()
				.getConfigurationSection("lotteries")
				.getConfigurationSection(name);
		return section;
	}
	
	public void newSignFormatter() {
		formatter = Config.getLotterySignFormatter(this);
	}

	public boolean isItemOnly() {
		return itemOnly && !itemRewards.isEmpty();
	}

	public boolean isRunByTime() {
		return maxTicketsDraw == -1;
	}

	public int getMaxTickets() {
		return maxTickets;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public List<ItemStack> getItemRewards() {
		return itemRewards;
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public boolean isDrawing() {
		return drawing;
	}

	public boolean isRepeating() {
		return repeat;
	}

	public void setDrawing(boolean flag) {
		this.drawing = flag;
	}

	public void registerSign(Sign sign) {
		signs.add(sign);
		updateSigns();
	}

	public Sign getSignAt(Location location) {

		for (Sign lotterySign : signs) {

			if (plugin.locsInBounds(location, lotterySign.getBlock()
					.getLocation())) {
				return lotterySign;
			}

		}

		return null;
	}

	public boolean unregisterSign(Location location) {

		for (int cntr = 0; cntr < signs.size(); cntr++) {
			Sign sign = signs.get(cntr);

			if (plugin.locsInBounds(location, sign.getBlock().getLocation())) {
				signs.remove(cntr);
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
				return true;
			}

		}

		return false;
	}

	public void updateSigns() {

		for (Sign sign : signs) {
			if (!sign.getBlock().getChunk().isLoaded()) {
				continue;
			}
			formatter.format(sign);
			sign.update(true);
		}

	}

	public boolean signAtLocation(Location location) {

		for (Sign sign : signs) {

			if (plugin.locsInBounds(sign.getBlock().getLocation(), location)) {
				return true;
			}

		}

		return false;
	}

	public boolean hasPlayerBoughtTicket(String player) {
		return players.contains(player);
	}

	public double playerReward(String player, int tickets) {
		return playerBought(player, tickets, false);
	}

	public double playerBought(String player, int tickets) {
		return playerBought(player, tickets, true);
	}

	private double playerBought(String player, int tickets, boolean flag) {
		double value = 0.0;
		for (int cntr = 0; cntr < tickets; cntr++) {
			players.add(player);
			if (flag) {
				value += (isItemOnly()) ? 0 : ticketCost
						- (ticketCost * (ticketTax / 100));
				pot += (isItemOnly()) ? 0 : ticketCost
						- (ticketCost * (ticketTax / 100));
			}
		}
		if (cooldown > 0 && maxTicketsDraw == -1) {
			timer.setTime(timer.getTime() + cooldown);
		}
		if (maxTicketsDraw != -1 && players.size() >= maxTicketsDraw) {
			draw(null);
		}

		return value;
	}

	public void addToPot(double add) {
		pot += add;

		for (String name : getPlayers()) {
			Player player = Bukkit.getPlayer(name);

			if (player != null) {
				plugin.send(player, String.format(
						"%s's pot has been raised to %s", this.name,
						formatPot()));
			}

		}

	}

	public double getTicketCost() {
		return ticketCost;
	}

	public void sendInfo(Player player) {
		if (isRunByTime()) {
			plugin.send(player, "Time Left: " + timer.format());
		} else {
			plugin.send(
					player,
					String.format("Tickets Left: %d",
							maxTicketsDraw - players.size()));
		}
		if (!isItemOnly()) {
			plugin.send(player, "Pot: " + formatPot());
		}
		for (ItemStack itemReward : itemRewards) {
			plugin.send(player, "Item Reward: " + itemReward.getType().name()
					+ " " + itemReward.getAmount());
		}
		plugin.send(player, "Ticket Cost: " + formatTicketCost());
		plugin.send(player, "Ticket Tax: " + formatTicketTax());
		plugin.send(player, "Pot Tax: " + formatPotTax());
		plugin.send(player, "Players Entered: " + playersEntered());
		plugin.send(player,
				"Tickets Bought: " + getTicketsBought(player.getName()));
	}

	public void sendInfo(CommandSender sender) {
		if (isRunByTime()) {
			sender.sendMessage("Time Left: " + timer.format());
		} else {
			sender.sendMessage(String.format("Tickets Left: %d", maxTicketsDraw
					- players.size()));
		}
		if (!isItemOnly()) {
			sender.sendMessage("Pot: " + formatPot());
		}
		for (ItemStack itemReward : itemRewards) {
			sender.sendMessage("Item Reward: " + itemReward.getType().name()
					+ " " + itemReward.getAmount());
		}
		sender.sendMessage("Ticket Cost: " + formatTicketCost());
		sender.sendMessage("Ticket Tax: " + formatTicketTax());
		sender.sendMessage("Pot Tax: " + formatPotTax());
		sender.sendMessage("Players Entered: " + playersEntered());
	}

	public int playersEntered() {
		return getPlayers().size();
	}

	public List<String> getPlayers() {
		List<String> list = new ArrayList<String>();

		for (String player : players) {

			if (!list.contains(player)) {
				list.add(player);
			}

		}

		return list;
	}

	public int getTicketsBought(String name) {
		int tickets = 0;

		for (String player : players) {

			if (player.equals(name)) {
				tickets++;
			}

		}

		return tickets;
	}

	public long getTime() {
		return timer.getTime();
	}

	public void countdown() {
		timer.countdown();
	}

	public String getName() {
		return name;
	}

	public String formatPot() {
		return plugin.format(pot);
	}

	public String formatTicketCost() {
		return plugin.format(ticketCost);
	}

	public String formatTicketTax() {
		return (ticketTax == 0.0) ? "none" : ticketTax + "%";
	}

	public String formatPotTax() {
		return (potTax == 0.0) ? "none" : potTax + "%";
	}

	public String formatTimer() {
		if (isRunByTime()) {
			return timer.format();
		} else {
			return String.format("%d tickets left",
					maxTicketsDraw - players.size());
		}
	}

	public String formatReward() {
		return (!isItemOnly()) ? formatPot() : formatItemRewards();
	}

	private String formatItemRewards() {
		return (itemRewards.size() == 1) ? itemRewards.get(0).getType().name()
				+ " " + itemRewards.get(0).getAmount() : itemRewards.size()
				+ " items";
	}

	public String formatWinner() {
		return (winner == null) ? "No winner" : winner;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	private void reset() {
		itemRewards.clear();
		players.clear();
		plugin.getLotteryManager().reloadLottery(name);
		timer.start();
	}

	public void draw(String name) {
		if (name == null) {
			plugin.broadcast(
					ChatColor.YELLOW.toString() + "[" + plugin.getName()
							+ "] - Lottery " + ChatColor.GOLD.toString()
							+ this.name + ChatColor.YELLOW.toString()
							+ " is ending. and the winner is...", "lottery.buy");
		} else {
			plugin.broadcast(
					ChatColor.YELLOW.toString() + "[" + plugin.getName()
							+ "] - " + ChatColor.GOLD + name + ChatColor.YELLOW
							+ " is force drawing the lottery " + ChatColor.GOLD
							+ this.name + ChatColor.YELLOW
							+ ", and the winner is...", "lottery.buy");
		}
		plugin.getScheduler().scheduleSyncDelayedTask(plugin, this,
				SERVER_SECOND * 3);
		timer.setRunning(false);
		setDrawing(true);
		updateSigns();
	}

	public void run() {
		int count = playersEntered();

		if (count < minPlayers || count < 1) {
			plugin.getServer()
					.broadcastMessage(
							ChatColor.YELLOW
									+ "["
									+ plugin.getName()
									+ "] - no one! there were not enough players entered. restarting lottery.");
			readResetData();
			timer.reset();
			timer.start();
			setDrawing(false);
			return;
		}

		Random random = plugin.getRandom();
		Collections.shuffle(players, random);
		String winner = players.get(random.nextInt(players.size()));
		this.winner = winner;
		double winnings = pot - (pot * (potTax / 100));

		StringBuilder message = new StringBuilder();
		message.append(ChatColor.YELLOW + "[" + plugin.getName() + "] - "
				+ ChatColor.GOLD + winner + "! " + ChatColor.YELLOW
				+ "Player has won ");

		if (isItemOnly()) {

			if (itemRewards.size() == 1) {
				ItemStack itemReward = itemRewards.get(0);
				message.append("a(n) " + ChatColor.GOLD.toString()
						+ itemReward.getType().name());
			}

			else {
				message.append(ChatColor.GOLD.toString() + itemRewards.size()
						+ " items");
			}

		}

		else {
			message.append(ChatColor.GOLD + plugin.format(winnings));

			if (!itemRewards.isEmpty()) {

				if (itemRewards.size() == 1) {
					ItemStack itemReward = itemRewards.get(0);
					message.append(ChatColor.YELLOW + " and a(n) "
							+ ChatColor.GOLD.toString()
							+ itemReward.getType().name());
				}

				else {
					message.append(ChatColor.YELLOW + " and "
							+ ChatColor.GOLD.toString() + itemRewards.size()
							+ " items");
				}

			}

		}

		message.append("!");
		plugin.getServer()
				.broadcastMessage(ChatColor.GOLD.toString() + message);
		StringBuilder logWinner = new StringBuilder(name + ": " + winner);

		if (isItemOnly()) {

			if (itemRewards.size() == 1) {
				ItemStack itemReward = itemRewards.get(0);
				logWinner.append(", Item Reward - ["
						+ itemReward.getType().name() + "]");
			}

			else {
				logWinner.append(", Item Rewards - [" + itemRewards.size()
						+ " items]");
			}

		}

		else {
			logWinner.append(", Winnings - [" + plugin.format(winnings) + "]");

			if (!itemRewards.isEmpty()) {

				if (itemRewards.size() == 1) {
					ItemStack itemReward = itemRewards.get(0);
					logWinner.append(", Item Reward(s) - ["
							+ itemReward.getType().name() + "]");
				}

				else {
					logWinner.append(", Item Reward(s) - ["
							+ itemRewards.size() + " items]");
				}

			}

		}

		plugin.addWinner(logWinner.toString());
		Economy econ = plugin.getEconomy();
		Player pWinner = Bukkit.getPlayer(winner);

		if (pWinner != null) {

			if (!isItemOnly()) {
				econ.depositPlayer(winner, winnings);
			}
			Map<Integer, ItemStack> itemsToBeDropped = pWinner.getInventory()
					.addItem(
							itemRewards.toArray(new ItemStack[itemRewards
									.size()]));
			World world = pWinner.getWorld();
			Location winnerLoc = pWinner.getLocation();
			for (ItemStack item : itemsToBeDropped.values()) {
				world.dropItem(winnerLoc, item);
			}

		}

		else {

			if (isItemOnly()) {
				plugin.addClaim(winner, name, itemRewards);
			}

			else {

				if (itemRewards.isEmpty()) {
					plugin.addClaim(winner, name, winnings);
				}

				else {
					plugin.addClaim(winner, name, itemRewards, winnings);
				}

			}

		}

		if (repeat) {
			reset();
			setDrawing(false);
			return;
		}
		setDrawing(false);
		updateSigns();
		plugin.getLotteryManager().removeLottery(name);
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String toString() {
		return name;
	}

	public void save(ConfigurationSection section) {
		section.set("ticketcost", ticketCost);
		section.set("pot", pot);
		section.set("save-time", timer.getTime());
		section.set("reset-time", timer.getResetTime());
		section.set("timer-running", timer.isRunning());
		section.set("repeat", repeat);
		section.set("item-only", itemOnly);
		section.set("max-tickets", maxTickets);
		section.set("max-players", maxPlayers);
		section.set("min-players", minPlayers);
		section.set("max-tickets-draw", maxTicketsDraw);
		section.set("past-winner", winner);
		section.set("ticket-tax", ticketTax);
		section.set("pot-tax", potTax);
		section.set("cooldown", cooldown);

		if (!players.isEmpty()) {

			Map<String, Object> playerMap = new HashMap<String, Object>();
			for (String player : getPlayers()) {
				playerMap.put(player, getTicketsBought(player));
			}
			section.createSection("players", playerMap);

		}
		if (!signs.isEmpty()) {
			ConfigurationSection signSection = section.createSection("signs");

			int cntr = 1;
			for (Sign sign : signs) {
				Location loc = sign.getBlock().getLocation();
				ConfigurationSection locSection = signSection
						.createSection("sign" + cntr);
				locSection.set("world", loc.getWorld().getName());
				locSection.set("x", loc.getBlockX());
				locSection.set("y", loc.getBlockY());
				locSection.set("z", loc.getBlockZ());
				cntr++;
			}

		}
		if (!itemRewards.isEmpty()) {

			ConfigurationSection itemSection = section
					.createSection("item-rewards");
			for (ItemStack item : itemRewards) {
				Map<String, Object> saveMap = new HashMap<String, Object>();
				saveMap.put("stack-size", item.getAmount());
				if (item.getData() != null) {
					saveMap.put("data", item.getData().getData());
				}
				Map<Enchantment, Integer> enchantments = item.getEnchantments();
				for (Enchantment enchantment : enchantments.keySet()) {
					saveMap.put(enchantment.getName(),
							enchantments.get(enchantment));
				}
				itemSection.createSection(item.getType().name(), saveMap);
			}

		}

	}

}
