package com.randude14.lotteryplus.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.randude14.lotteryplus.Config;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.FormatOptions;

public class SignListener implements Listener, FormatOptions {

	private final Plugin plugin;
	private final LotteryManager manager;

	public SignListener(final Plugin plugin) {
		this.plugin = plugin;
		this.manager = plugin.getLotteryManager();
	}

	@EventHandler
	public void signPlace(SignChangeEvent event) {

		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		Block block = event.getBlock();
		if(!plugin.isSign(block)) 
			return;
		Sign sign = (Sign) block.getState();

		if (isLotterySign(event)) {

			if (!plugin.hasPermission(player, "lottery.sign.create")) {
				plugin.error(player, "You do not have permission.");
				event.setCancelled(true);
				return;
			}

			if (event.getLine(1).equals("")) {
				plugin.error(player, "Must specify lottery.");
				event.setCancelled(true);
				return;
			}

			Lottery lottery = manager.searchLottery(event.getLine(1));

			if (lottery == null) {
				plugin.error(player, "Lottery does not exist.");
				event.setCancelled(true);
				return;
			}

			lottery.registerSign(sign);
			plugin.send(player, "Lottery sign created.");
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void blockBreak(BlockBreakEvent event) {
		List<Lottery> lotteries = manager.getLotteries();
		Location loc = event.getBlock().getLocation();
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (event.isCancelled()) {
			return;
		}

		if (!plugin.isSign(block)) {
			return;
		}

		for (Lottery lottery : lotteries) {

			if (lottery.signAtLocation(loc)) {

				if (!plugin.hasPermission(player, "lottery.sign.remove")) {
					event.setCancelled(true);
					plugin.error(player,
							"[Lottery+] - You do not have permission.");
					lottery.updateSigns();
				}

				else {
					lottery.unregisterSign(loc);
					plugin.send(player, "Sign removed.");
				}

			}

		}

	}

	@EventHandler
	public void playerRightClick(PlayerInteractEvent event) {
		List<Lottery> lotteries = manager.getLotteries();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		String name = player.getName();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Location loc = block.getLocation();

		for (Lottery lottery : lotteries) {

			if (lottery.signAtLocation(loc)) {
				event.setCancelled(true);

				if (!plugin.hasPermission(player, "lottery.buy")) {
					plugin.error(player, "You do not have permission");
					return;
				}

				if (plugin.isBuyer(name)) {
					plugin.removeBuyer(name);
					plugin.send(player, "Transaction cancelled.");
					plugin.help(player,
							"---------------------------------------------------");
					return;
				}

				int maxPlayers = lottery.getMaxPlayers();

				if (lottery.playersEntered() >= maxPlayers && maxPlayers != -1
						&& !lottery.hasPlayerBoughtTicket(name)) {
					plugin.error(player, "Too many players in this lottery.");
					plugin.send(player, "Transaction cancelled.");
					plugin.help(player,
							"---------------------------------------------------");
					return;
				}

				plugin.help(player,
						"---------------------------------------------------");
				String[] messages = getBuyMessage(lottery);
				for (String message : messages) {
					player.sendMessage(message);
				}
				plugin.send(player, "");
				plugin.send(player, "How many tickets would you like to buy?");
				plugin.addBuyer(player.getName(), lottery.getName());

			}

		}

	}

	private String[] getBuyMessage(Lottery lottery) {
		String buyMessage = Config.getBuyMessage();
			buyMessage = plugin.replaceColors(buyMessage)
					.replace(FORMAT_REWARD, lottery.formatReward())
					.replace(FORMAT_TIME, lottery.formatTimer())
					.replace(FORMAT_NAME, lottery.getName())
					.replace(FORMAT_WINNER, lottery.formatWinner())
					.replace(FORMAT_TICKET_COST, lottery.formatTicketCost())
					.replace(FORMAT_TICKET_TAX, lottery.formatTicketTax())
					.replace(FORMAT_POT_TAX, lottery.formatPotTax());
		return buyMessage.split(FORMAT_NEWLINE);
	}

	private boolean isLotterySign(SignChangeEvent event) {
		return event.getLine(0).equalsIgnoreCase("[Lottery+]");
	}

}
