package com.randude14.lotteryplus;

import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.lottery.LotteryClaim;
import com.randude14.lotteryplus.util.TimeConstants;

public class LotteryCommands implements CommandExecutor, TimeConstants {

	private final Plugin plugin;
	private final LotteryManager manager;

	public LotteryCommands(final Plugin plugin) {
		this.plugin = plugin;
		this.manager = plugin.getLotteryManager();
	}

	private boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String command,
			String[] args) {
		command = command.toLowerCase();

		if (isPlayer(sender)) {
			handlePlayerCommand((Player) sender, args);
		}

		else {
			handleConsoleCommand(sender, args);
		}

		return false;
	}

	private void handlePlayerCommand(Player player, String[] args) {
		Economy econ = plugin.getEconomy();
		String name = player.getName();

		if (args.length == 0) {

			if (!plugin.hasPermission(player, "lottery.help")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			helpMessage(player);
		}

		else if ("list".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.list")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length > 2) {
				plugin.error(player, "Too many arguments.");
				return;
			}

			int page;

			if (args.length == 2) {

				try {
					page = Integer.parseInt(args[1]);
				} catch (Exception ex) {
					plugin.error(player, "Page must be an integer.");
					return;
				}

			}

			else {
				page = 1;
			}

			listLotteries(player, page);
		}

		else if ("info".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.info")) {
				plugin.error(player, "You do not have permission.");
			}

			if (args.length == 2) {
				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					plugin.error(player, "Lottery does not exist.");
					return;
				}

				plugin.help(player,
						"---------------------------------------------------");
				plugin.send(player,
						"[" + plugin.getName() + "] - lottery info for "
								+ ChatColor.GOLD + lottery.getName());
				plugin.send(player, "");
				lottery.sendInfo(player);
				plugin.help(player,
						"---------------------------------------------------");
			}

			else if (args.length > 2) {
				plugin.error(player, "Too many arguments.");
			}

			else if (args.length == 1) {
				plugin.help(player, "/lottery info <lottery name>");
			}

		}

		else if ("buy".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.buy")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length > 3) {
				plugin.error(player, "Too many arguments.");
			}

			else if (args.length < 3) {
				plugin.help(player, "/lottery buy <lottery name> <x tickets>");
			}

			else if (args.length == 3) {

				if (!econ.hasAccount(name)) {
					plugin.error(player, "Player does not have account.");
					return;
				}

				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					plugin.error(player, "Lottery not found.");
					return;
				}

				int maxPlayers = lottery.getMaxPlayers();

				if (lottery.playersEntered() >= maxPlayers && maxPlayers != -1
						&& !lottery.hasPlayerBoughtTicket(name)) {
					plugin.error(player, "Too many players in this lottery.");
					return;
				}

				int tickets = 0;

				try {
					tickets = Integer.parseInt(args[2]);
				} catch (Exception ex) {
					plugin.error(player, "Invalid number.");
					return;
				}

				int ticketsBought = lottery.getTicketsBought(name);
				int maxTickets = lottery.getMaxTickets();

				if (ticketsBought >= maxTickets && maxTickets != -1) {
					plugin.send(player, "You have bought too many tickets.");
					return;
				}

				if (ticketsBought + tickets > maxTickets && maxTickets != -1) {
					plugin.send(player, "You cannot buy this many tickets.");
					return;
				}

				double price = tickets * lottery.getTicketCost();

				if (econ.getBalance(name) < price) {
					plugin.error(player, "Player does not have enought money.");
					return;
				}

				econ.withdrawPlayer(name, price);
				double added = lottery.playerBought(name, tickets);
				String message = plugin.replaceColors(Config.getBuyMessage()
						.replace("<player>", name)
						.replace("<ticket>", String.format("%d", tickets))
						.replace("<lottery>", lottery.getName()));
				if (Config.shouldBroadcastBuy())
					plugin.broadcast(message);
				else
					plugin.send(player, message);
				plugin.send(player, String.format(
						"$%,.2f has been added to %s.", added,
						lottery.getName()));
			}

		}

		else if ("reward".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.admin.reward")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length < 4) {
				plugin.help(player,
						"/lottery reward <player> <lottery name> <x tickets>");
			}

			else if (args.length == 4) {
				String rewarded = args[1];
				OfflinePlayer offline = plugin.getOfflinePlayer(rewarded);

				if (!offline.hasPlayedBefore()) {
					plugin.error(player,
							"Player has not been on this server before.");
					return;
				}

				Lottery lottery = manager.searchLottery(args[2]);

				if (lottery == null) {
					plugin.error(player, "Lottery does not exist.");
					return;
				}

				if (!isValidInt(args[3])) {
					plugin.error(player, "Invalid number.");
					return;
				}

				if (name.equalsIgnoreCase(offline.getName())) {
					plugin.error(player,
							"Player cannot reward himself/herself.");
					return;
				}

				int tickets = Integer.parseInt(args[3]);
				lottery.playerReward(offline.getName(), tickets);
				plugin.send(player, "Player " + offline.getName()
						+ " has been awarded " + tickets
						+ " ticket(s) for lottery " + ChatColor.GOLD.toString()
						+ lottery.getName());
				Player rewardedPlayer = offline.getPlayer();

				if (rewardedPlayer != null) {
					plugin.send(rewardedPlayer, "You have been rewarded "
							+ tickets + " ticket(s) for the lottery "
							+ ChatColor.GOLD + lottery.getName()
							+ ChatColor.YELLOW + " from admin "
							+ ChatColor.GOLD + player.getName());
				}

			}

			else {
				plugin.error(player, "Too many arguments.");
			}

		}

		else if ("addtopot".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.addtopot")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length > 3) {
				plugin.error(player, "Too many arguments.");
			}

			else if (args.length == 3) {

				if (!econ.hasAccount(name)) {
					plugin.error(player, "Player does not have account.");
					return;
				}

				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					plugin.error(player, "Lottery not found.");
					return;
				}

				if (lottery.isItemOnly()) {
					plugin.error(player,
							"Lottery " + ChatColor.GOLD + lottery.getName()
									+ ChatColor.YELLOW
									+ " is currently an 'item-only' lottery");
					return;
				}

				double add = 0;

				try {
					add = Double.parseDouble(args[2]);
				} catch (Exception ex) {
					plugin.error(player, "Invalid number.");
					return;
				}

				if (econ.getBalance(name) < add) {
					plugin.error(player, "Player does not have enought money.");
					return;
				}

				econ.withdrawPlayer(name, add);
				lottery.addToPot(add);
				plugin.send(player, "Player has added " + plugin.format(add)
						+ " to the pot of lottery " + lottery);
			}

			else {
				plugin.help(player, "/lottery addtopot <lottery name>");
			}

		}

		else if ("claim".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.buy")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length > 1) {
				plugin.error(player, "Too many arguments!");
				return;
			}

			List<LotteryClaim> claims = plugin.getClaims(name);

			if (claims == null) {
				plugin.error(player, "There are no lottery claims for " + name);
				return;
			}

			if (claims.isEmpty()) {
				plugin.error(player, "There are no lottery claims for " + name);
				return;
			}

			for (int cntr = 0; cntr < claims.size(); cntr++) {
				LotteryClaim claim = claims.get(cntr);
				double pot = claim.getPot();
				String lottery = claim.getLotteryName();
				List<ItemStack> itemRewards = claim.getItemRewards();

				if (pot != -1) {
					econ.depositPlayer(name, pot);
					plugin.send(player, "Awarded player " + plugin.format(pot)
							+ " from lottery " + lottery);
				}

				if (itemRewards != null && !itemRewards.isEmpty()) {

					Map<Integer, ItemStack> itemsToBeDropped = player
							.getInventory().addItem(
									itemRewards
											.toArray(new ItemStack[itemRewards
													.size()]));
					World world = player.getWorld();
					Location playerLoc = player.getLocation();
					for (ItemStack item : itemsToBeDropped.values()) {
						world.dropItem(playerLoc, item);
					}

					String message = "Awarded player ";
					if (itemRewards.size() == 1) {
						message += "a(n) " + ChatColor.GOLD.toString()
								+ itemRewards.get(0).getType().name() + ".";
					} else {
						message += ChatColor.GOLD.toString()
								+ itemRewards.size() + " items.";
					}
					plugin.send(player, message);
				}
				claims.remove(cntr);
			}

		}

		else if ("winners".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.winners")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			plugin.listWinners(player);
		}

		else if ("draw".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.admin.draw")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length > 2) {
				plugin.error(player, "Too many arguments!");
				return;
			}

			else if (args.length == 2) {
				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					plugin.error(player, "Lottery not found.");
					return;
				}

				lottery.draw(name);
				plugin.getScheduler().scheduleSyncDelayedTask(plugin, lottery,
						SERVER_SECOND * 3);
			}

			else {
				plugin.error(player, "/lottery draw <lottery name>");
			}

		}

		else if ("reload".equalsIgnoreCase(args[0])) {

			if (!plugin.hasPermission(player, "lottery.admin.relaod")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			plugin.reload();
			plugin.send(player, String.format("[%s] - reloaded v%s.",
					plugin.getName(), plugin.getDescription().getVersion()));
		}

		else {

			if (!plugin.hasPermission(player, "lottery.help")) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			helpMessage(player);
		}

	}

	private void handleConsoleCommand(CommandSender sender, String[] args) {

		if (args.length == 0) {
			helpMessage(sender);
		}

		else if ("list".equalsIgnoreCase(args[0])) {

			if (args.length > 2) {
				sender.sendMessage("Too many arguments.");
				return;
			}

			int page;
			if (args.length == 2) {

				if (!isValidInt(args[1])) {
					sender.sendMessage("Page is not a valid integer.");
					return;
				}
				page = Integer.parseInt(args[1]);
			}

			else {
				page = 1;
			}

			listLotteries(sender, page);
		}

		else if ("info".equalsIgnoreCase(args[0])) {

			if (args.length > 2) {
				sender.sendMessage("Too many arguments.");
			}

			else if (args.length == 1) {
				sender.sendMessage("/lottery info <lottery name>");
			}

			else {

				Lottery lottery = manager.searchLottery(args[1]);
				if (lottery == null) {
					sender.sendMessage(String.format("%s does not exist!",
							args[1]));
					return;
				}

				sender.sendMessage("---------------------------------------------------");
				sender.sendMessage(String.format("[%s] - lottery info for %s",
						plugin.getName(), lottery.getName()));
				sender.sendMessage("");
				lottery.sendInfo(sender);
				sender.sendMessage("---------------------------------------------------");
			}

		}

		else if ("reward".equals(args[0])) {

			if (args.length < 4) {
				sender.sendMessage("/lottery reward <player> <lottery name> <x tickets>");
			}

			else if (args.length == 4) {
				String rewarded = args[1];
				OfflinePlayer offline = plugin.getOfflinePlayer(rewarded);

				if (!offline.hasPlayedBefore()) {
					sender.sendMessage(args[1]
							+ " has not been on this server before.");
					return;
				}

				Lottery lottery = manager.searchLottery(args[2]);

				if (lottery == null) {
					sender.sendMessage("Lottery does not exist.");
					return;
				}

				if (!isValidInt(args[3])) {
					sender.sendMessage("Invalid number.");
					return;
				}
				int tickets = Integer.parseInt(args[3]);
				sender.sendMessage("Player " + offline.getName()
						+ " has been awarded " + tickets
						+ " ticket(s) for the lottery "
						+ ChatColor.GOLD.toString() + lottery.getName());
				Player rewardedPlayer = offline.getPlayer();

				if (rewardedPlayer != null) {
					plugin.send(rewardedPlayer, "You have been rewarded "
							+ tickets + " ticket(s) for the lottery "
							+ ChatColor.GOLD + lottery.getName()
							+ ChatColor.YELLOW + " from admin "
							+ ChatColor.GOLD + " CONSOLE.");
				}

				lottery.playerReward(offline.getName(), tickets);
			}

			else {
				sender.sendMessage("Too many arguments.");
			}

		}

		else if ("addtopot".equalsIgnoreCase(args[0])) {

			if (args.length > 3) {
				sender.sendMessage("Too many arguments.");
			}

			else if (args.length == 3) {

				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					sender.sendMessage("Lottery not found.");
					return;
				}

				if (lottery.isItemOnly()) {
					sender.sendMessage("Lottery " + lottery.getName()
							+ " is currently an 'item-only' lottery");
					return;
				}

				double add = 0;

				try {
					add = Double.parseDouble(args[2]);
				} catch (Exception ex) {
					sender.sendMessage("Invalid number.");
					return;
				}

				lottery.addToPot(add);
				sender.sendMessage("You have added " + plugin.format(add)
						+ " to the pot of lottery " + lottery);
			}

			else {
				sender.sendMessage("/lottery addtopot <lottery name> <money>");
			}

		}

		else if ("winners".equalsIgnoreCase(args[0])) {

			if (args.length > 1) {
				sender.sendMessage("Too many arguments.");
				return;
			}

			plugin.listWinners(sender);
		}

		else if ("draw".equalsIgnoreCase(args[0])) {

			if (args.length > 2) {
				sender.sendMessage("Too many arguments.");
				return;
			}

			else if (args.length == 1) {
				sender.sendMessage("/lottery draw <lottery name>");
			}

			else {
				Lottery lottery = manager.searchLottery(args[1]);

				if (lottery == null) {
					sender.sendMessage("Lottery not found.");
					return;
				}

				lottery.draw("CONSOLE");
			}

		}

		else if ("reload".equalsIgnoreCase(args[0])) {

			if (args.length > 1) {
				sender.sendMessage("Too many arguments");
				return;
			}

			plugin.reload();
			plugin.info("reloaded.");
		}

	}

	private boolean isValidInt(String str) {

		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception ex) {
			return false;
		}

	}

	private void listLotteries(Player player, int page) {
		List<Lottery> lotteries = manager.getLotteries();
		int pages = maxPages(lotteries);

		if (pages > pages) {
			page = pages;
		}

		if (page < 1) {
			page = 1;
		}

		plugin.help(player,
				"---------------------------------------------------");
		plugin.send(player, "[" + plugin.getName() + "] - Page " + page + "/"
				+ pages);
		plugin.send(player,
				"type '/lottery info <lottery name>' for more specific information");
		int stop = (page * 10);

		if (lotteries.isEmpty()) {
			plugin.error(player, "There are no lotteries.");
			plugin.help(player,
					"---------------------------------------------------");
			return;
		}

		for (int cntr = stop - 10; cntr < lotteries.size() && cntr < stop; cntr += 2) {
			Lottery lottery1 = lotteries.get(cntr);
			String line = String.format("%d. %s", cntr + 1, lottery1.getName());

			if (cntr != lotteries.size() - 1) {
				Lottery lottery2 = lotteries.get(cntr + 1);
				line += String.format(" %d. %s", cntr + 2, lottery2.getName());
				;
			}

			plugin.send(player, line);
		}

		plugin.help(player,
				"---------------------------------------------------");
	}

	private void listLotteries(CommandSender sender, int page) {
		List<Lottery> lotteries = manager.getLotteries();
		int pages = maxPages(lotteries);

		if (pages > pages) {
			page = pages;
		}

		if (page < 1) {
			page = 1;
		}

		sender.sendMessage("---------------------------------------------------");
		sender.sendMessage("[" + plugin.getName() + "] - Page " + page + "/"
				+ pages);
		sender.sendMessage("type '/lottery info <lottery name>' for more specific information");
		int stop = (page * 10);

		if (lotteries.isEmpty()) {
			sender.sendMessage("There are no lotteries.");
			sender.sendMessage("---------------------------------------------------");
			return;
		}

		for (int cntr = stop - 10; cntr < lotteries.size() && cntr < stop; cntr += 2) {
			Lottery lottery1 = lotteries.get(cntr);
			String line = String.format("%d. %s", cntr + 1, lottery1.getName());

			if (cntr != lotteries.size() - 1) {
				Lottery lottery2 = lotteries.get(cntr + 1);
				line += String.format(" %d. %s", cntr + 2, lottery2.getName());
				;
			}

			sender.sendMessage(line);
		}

		sender.sendMessage("---------------------------------------------------");
	}

	private int maxPages(List<?> list) {
		return (list.size() / 10) + 1;
	}

	private void helpMessage(Player player) {
		plugin.help(player,
				"---------------------------------------------------");
		plugin.send(player, "[" + plugin.getName() + "] - player command list");
		plugin.send(player, "");
		plugin.send(player, "1. /lottery - displays this");
		plugin.send(player, "2. /lottery list <page> - list lotteries");
		plugin.send(player,
				"3. /lottery info <lottery name> - list specific info on a lottery");
		plugin.send(player,
				"4. /lottery buy <lottery name> <x tickets> - buy tickets for a lottery");
		plugin.send(
				player,
				"5. /lottery reward <player> <lottery name> <x tickets> - reward a user tickets for a lottery");
		plugin.send(
				player,
				"6. /lottery addtopot <lottery name> <money> - add money to the pot of a lottery");
		plugin.send(player, "7. /lottery claim - claim an award");
		plugin.send(player, "8. /lottery winners - list past lottery winners");
		plugin.send(player,
				"9. /lottery draw <lottery name> - force draw a lottery");
		plugin.send(player, "10. /lottery reload - reloads plugin");
		plugin.help(player,
				"---------------------------------------------------");
	}

	private void helpMessage(CommandSender sender) {
		sender.sendMessage("---------------------------------------------------");
		sender.sendMessage("[" + plugin.getName() + "] - console command list");
		sender.sendMessage("");
		sender.sendMessage("1. /lottery - displays this");
		sender.sendMessage("2. /lottery list <page> - list lotteries");
		sender.sendMessage("3. /lottery info <lottery name> - list specific info on a lottery");
		sender.sendMessage("4. /lottery reward <player> <lottery name> <money> - reward a user tickets for a lottery");
		sender.sendMessage("5. /lottery addtopot <lottery name> <money> - add money to the pot of a lottery");
		sender.sendMessage("6. /lottery winners - list past lottery winners");
		sender.sendMessage("7. /lottery draw <lottery name> - force draw a lottery");
		sender.sendMessage("8. /lottery reload - reloads plugin");
		sender.sendMessage("---------------------------------------------------");
	}

}
