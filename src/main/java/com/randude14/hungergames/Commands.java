package com.randude14.hungergames;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.Defaults.Perm;
import org.bukkit.Bukkit;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("In-game use only.");
			return true;
		}
		if (cmd.getLabel().equals(Plugin.CMD_USER)) {
			handleUserCommand((Player) sender, cmd, args);
		} else if (cmd.getLabel().equals(Plugin.CMD_ADMIN)) {
			handleAdminCommand((Player) sender, cmd, args);
		}
		return false;
	}

	private void handleUserCommand(Player player, Command cmd, String[] args) {
		HungerGame game = null;
		if (args.length == 0) {
			if (!Plugin.checkPermission(player, Perm.USER_HELP))
				return;
			getUserCommands(player, cmd);
			return;
		}

		else if ("list".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_LIST))
				return;
			Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
			Collection<HungerGame> games = GameManager.getGames();
			if (games.isEmpty()) {
				Plugin.error(player, "No games have been created yet.");
				return;
			}

			for (HungerGame g : games) {
				Plugin.send(player, ChatColor.GOLD, "- " + g.getInfo());
			}
		}

		else if ("join".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_JOIN))
				return;
			String name = (args.length == 1) ? Config.getDefaultGame()
					: args[1];
			if (name == null) {
				Plugin.send(player, ChatColor.GOLD, "/%s join <game name>",
						cmd.getLabel());
				return;
			}

			game = GameManager.getGame(name);
			if (game == null) {
				Plugin.sendDoesNotExist(player, name);
				return;
			}
			HungerGame currentSession = GameManager.getSession(player);
			if (currentSession != null) {
				Plugin.error(player,
						"You are already in the game '%s'. Leave that game before joining another.",
						currentSession.getName());
				return;
			}
			if (game.join(player)) {
				String mess = Config.getJoinMessage(currentSession.getSetup());
				mess = mess.replace("<player>", player.getName()).replace(
						"<game>", game.getName());
				Plugin.broadcast(mess);
			}
		}

		else if ("leave".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_LEAVE))
				return;

			game = GameManager.getSession(player);
			if (game == null) {
				Plugin.error(player, "You are currently not in a game.");
				return;
			}

			if (game.leave(player)) {
				String mess = Config.getLeaveMessage(game.getSetup());
				mess = mess.replace("<player>", player.getName()).replace(
						"<game>", game.getName());
				Plugin.broadcast(mess);
			}

		}

		else if ("rejoin".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_REJOIN))
				return;
			game = GameManager.getSession(player);
			if (game != null) {
				if (game.rejoin(player)) {
					String mess = Config.getRejoinMessage(game.getSetup());
					mess = mess.replace("<player>", player.getName()).replace(
							"<game>", game.getName());
					Plugin.broadcast(mess);
				}

				else {
					Plugin.error(player, "Failed to rejoin %s.", game.getName());
				}

			}

			else {
				Plugin.error(player, "You are currently not in a game.");
			}

		}

		else if ("sponsor".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_SPONSOR))
				return;

			if (args.length < 2) {
				Plugin.send(player, ChatColor.GOLD, "/%s sponsor <player>",
						cmd.getLabel());
				return;
			}

			Player p = Bukkit.getServer().getPlayer(args[1]);
			if (p == null) {
				Plugin.error(player, "%s is not online.", args[1]);
				return;
			}
			if (GameManager.getSession(p) == null) {
				Plugin.error(player, "%s is not in a game.", p.getName());
				return;
			}
			Plugin.addSponsor(player, p.getName());
		}

		else if ("vote".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_VOTE))
				return;

			game = GameManager.getSession(player);
			if (game == null) {
				Plugin.error(
						player,
						"You must be in a game to vote. You can a game join by '/%s join <game name>'",
						Plugin.CMD_USER);
				return;
			}
			game.addReadyPlayer(player);
			Plugin.send(player, "You have voted that you are ready.");
		}

		else if ("stat".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.USER_STAT))
				return;

			if (args.length == 1) {
				Plugin.send(player, ChatColor.GOLD, "/%s stat <game name>",
						cmd.getLabel());
				return;
			}

			game = GameManager.getGame(args[1]);
			if (game == null) {
				Plugin.sendDoesNotExist(player, args[1]);
				return;
			}
			Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
			game.listStats(player);

		}

		else {
			if (!Plugin.checkPermission(player, Perm.USER_HELP))
				return;
			getUserCommands(player, cmd);
		}
		GameManager.saveGames();// TODO save less
	}

	private void handleAdminCommand(Player player, Command cmd, String[] args) {
		HungerGame game = null;
		GameManager GameManager = Plugin.getGameManager();

		if (args.length == 0) {
			if (!Plugin.hasPermission(player, Perm.ADMIN_HELP))
				return;
			getAdminCommands(player, cmd);
			return;
		}

		if ("add".equals(args[0])) {
			addCommand(player, args);
		}

		else if ("remove".equals(args[0])) {
			removeCommand(player, args);
		}

		else if ("set".equals(args[0])) {
			setCommand(player, args);
		}

		else if ("kick".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.ADMIN_KICK))
				return;

			if (args.length == 1) {
				Plugin.send(player, ChatColor.GOLD, "/%s kick <player>",
						cmd.getLabel());
				return;
			}

			Player kick = Bukkit.getServer().getPlayer(args[1]);
			if (kick != null) {
				game = GameManager.getSession(kick);
				if (game != null) {
					Plugin.broadcast(String.format(
							"%s has been kicked from the game %s.",
							player.getName(), game.getName()));
					game.leave(kick);
				}

				else {
					Plugin.error(player, "%s is currently not in a game.",
							kick.getName());
				}

			}

			else {

				Plugin.error(player, "%s is not online.", args[1]);
			}
		}

		else if ("start".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.ADMIN_START))
				return;

			if (args.length == 1) {
				Plugin.help(player, "/%s start <game name> <seconds>",
						cmd.getLabel());
				return;
			}

			game = GameManager.getGame(args[1]);
			if (game == null) {
				Plugin.sendDoesNotExist(player, args[1]);
				return;
			}

			int seconds;

			if (args.length == 3) {
				try {
					seconds = Integer.parseInt(args[2]);
				} catch (Exception ex) {
					Plugin.error(player, "'%s' is not an integer.", args[2]);
					return;
				}

			}

			else {
				seconds = 10;
			}
			if (!game.start(player, seconds)) {
				Plugin.error(player, "Failed to start %s.", game.getName());
			}
		}

		else if ("reload".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.ADMIN_RELOAD))
				return;
			Plugin.reload();
			Plugin.send(player, Plugin.getPrefix() + "Reloaded v%s", Plugin
					.getInstance().getDescription().getVersion());
		} else {
			if (!Plugin.checkPermission(player, Perm.ADMIN_HELP))
				return;
			getAdminCommands(player, cmd);
		}
		GameManager.saveGames();// TODO save less
	}

	private boolean addCommand(Player player, String[] args) {
		if (!Plugin.hasPermission(player, Perm.ADMIN_ADD_CHEST)
				&& !Plugin.hasPermission(player, Perm.ADMIN_ADD_SPAWNPOINT)
				&& !Plugin.hasPermission(player, Perm.ADMIN_ADD_GAME)) {// TODO specific perms for each subcommand
			Plugin.error(player, "You do not have permission.");
			return true;
		}

		if (args.length == 1 || "?".equals(args[1])) {
			Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
			Plugin.send(player, ChatColor.GOLD,
					"- /%s add spawnpoint <game name> - add a spawnpoint.",
					args[0]);
			Plugin.send(player, ChatColor.GOLD,
					"- /%s add chest <game name> - add a chest.", args[0]);
			Plugin.send(player, ChatColor.GOLD,
					"- /%s add game <game name> <setup> - add a game.", args[0]);
			return true;
		}

		HungerGame game = null;
		if ("spawnpoint".equals(args[1])) {
			if (args.length == 2) {
				Plugin.send(player, "/%s add spawnpoint <game name>", args[0]);
				return true;
			}

			if (GameManager.doesNameExist(args[2])) {
				game = GameManager.getGame(args[2]);
				Plugin.addSpawnAdder(player, game.getName());
				Plugin.send(player, ChatColor.GREEN,
						"Hit a block to add it as a spawn point for %s.",
						game.getName());
			} else {
				Plugin.sendDoesNotExist(player, args[2]);
			}
		}

		else if ("chest".equals(args[1])) {
			if (args.length == 2) {
				Plugin.help(player, "/%s add chest <game name>", args[0]);
				return true;
			}

			if (GameManager.doesNameExist(args[2])) {
				Plugin.addChestAdder(player, args[2]);
				Plugin.send(player, ChatColor.GREEN,
						"Hit a chest to add it to %s.",
						GameManager.getGame(args[2]).getName());
			} else {
				Plugin.sendDoesNotExist(player, args[2]);
			}

		}

		else if ("game".equals(args[1])) {
			if (args.length == 2) {
				Plugin.help(player, "/%s add game <game name>", args[0]);
			}

			if (GameManager.doesNameExist(args[2])) {
				Plugin.error(player, "%s already exists.", args[2]);
				return true;
			}
			if(args.length == 3){
			    GameManager.createGame(args[2]);
			}
			else{
			    GameManager.createGame(args[2], args[3]);
			}
			Plugin.send(player, ChatColor.GREEN, "%s has been created.", args[2]);
		}

		else {
			Plugin.error(player, "'%s' is not recognized.", args[1]);
		}
		return true;
	}

	private boolean removeCommand(Player player, String[] args) {
		if (!Plugin.hasPermission(player, Perm.ADMIN_REMOVE_CHEST)
				&& !Plugin.hasPermission(player, Perm.ADMIN_REMOVE_SPAWNPOINT)
				&& !Plugin.hasPermission(player, Perm.ADMIN_REMOVE_GAME)) {
			Plugin.error(player, "You do not have permission.");
			return true;
		}

		if (args.length == 1 || "?".equals(args[1])) {
			Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
			Plugin.send(player, ChatColor.GOLD,
					"- /%s remove spawnpoint <game name> - remove a spawnpoint.",
					args[0]);
			Plugin.send(player, ChatColor.GOLD,
					"- /%s remove chest <game name> - remove a chest.",
					args[0]);
			Plugin.send(player, ChatColor.GOLD,
					"- /%s remove game <game name> - remove a game.",
					args[0]);
			return true;
		}

		HungerGame game = null;
		if ("spawnpoint".equals(args[1])) {
			if (args.length == 2) {
				Plugin.help(player, "/%s remove spawnpoint <game name>",
						args[0]);
				return true;
			}

			if (GameManager.doesNameExist(args[2])) {
				game = GameManager.getGame(args[2]);
				Plugin.addSpawnRemover(player, game.getName());
				Plugin.send(player, ChatColor.GREEN,
						"Hit a spawn point to remove it from %s.",
						game.getName());
			} else {
				Plugin.sendDoesNotExist(player, args[2]);
			}

		}

		else if ("chest".equals(args[1])) {
			if (args.length == 2) {
				Plugin.help(player, "/%s remove chest <game name>", args[0]);
				return true;
			}

			if (GameManager.doesNameExist(args[2])) {
				Plugin.addChestRemover(player, args[2]);
				Plugin.send(player, ChatColor.GREEN,
						"Hit a chest to remove it from %s.", GameManager
								.getGame(args[2]).getName());
			} else {
				Plugin.sendDoesNotExist(player, args[2]);
			}

		}

		else if ("game".equals(args[1])) {
			if (args.length == 2) {
				Plugin.help(player, "/%s remove game <game name>", args[0]);
				return true;
			}

			if (GameManager.doesNameExist(args[2])) {
				GameManager.removeGame(args[2]);
				Plugin.send(player, ChatColor.GREEN, "%s has been removed.",
						args[2]);
			} else {
				Plugin.sendDoesNotExist(player, args[2]);
			}

		}

		else {
			Plugin.error(player, "'%s' is not recognized.", args[1]);
		}

		return true;
	}

	private boolean setCommand(Player player, String[] args) {
		if (!Plugin.hasPermission(player, Perm.ADMIN_SET_ENABLED)
				&& !Plugin.hasPermission(player, Perm.ADMIN_SET_SPAWN)) {
			Plugin.error(player, "You do not have permission.");
			return true;
		}

		if (args.length == 1 || "?".equals(args[1])) {
			Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
			Plugin.send(
					player,
					ChatColor.GOLD,
					"- /%s set spawn <game name> - set the spawnpoint for a game.",
					args[0]);
			Plugin.send(
					player,
					ChatColor.GOLD,
					"- /%s set enabled <game name> <true/false> - enable or disable a game.",
					args[0]);
			return true;
		}

		HungerGame game = null;
		if ("spawn".equals(args[1])) {
			if (args.length < 3) {
				Plugin.send(player, ChatColor.GOLD,
						"/%s set spawn <game name>", args[0]);
				return true;
			}
			game = GameManager.getGame(args[2]);
			if (game == null) {
				Plugin.sendDoesNotExist(player, args[2]);
			}
			Location loc = player.getLocation();
			game.setSpawn(loc);
			Plugin.send(player, "Spawn has been set for %s.", game.getName());
		}

		else if ("enabled".equals(args[1])) {
			if (args.length < 3) {
				Plugin.send(player, ChatColor.GOLD,
						"/%s set enabled <game name> <true/false>", args[0]);
				return true;
			}

			game = GameManager.getGame(args[2]);
			if (game == null) {
				Plugin.sendDoesNotExist(player, args[2]);
			}
			boolean flag;
			if (args.length == 4) {
				flag = Boolean.valueOf(args[3]);
			} else {
				flag = true;
			}
			game.setEnabled(flag);
			if (flag) {
				Plugin.send(player, "%s has been enabled.", game.getName());
			} else {
				Plugin.send(player,
						String.format("%s has been disabled.", game.getName()));
			}
		}

		else {
			Plugin.error(player, "'%s' is not recognized.", args[1]);
			return true;
		}
		return true;
	}

	private void getUserCommands(Player player, Command cmd) {
		Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		Plugin.send(player, ChatColor.GOLD, "- /%s list - list games",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s join <game name> - join a game", cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD, "- /%s leave - leave current game",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s rejoin - rejoin your current game", cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s sponsor <player> - sponsor a player an item",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s vote - cast your vote that you are ready to play",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s stat <game name> - list stats for a game",
				cmd.getLabel());
	}

	private void getAdminCommands(Player player, Command cmd) {
		Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		Plugin.send(player, ChatColor.GOLD, "- /%s add ? - type for more help",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s remove ? - type for more help", cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD, "- /%s set ? - type for more help",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s kick <player> - kick a player from a game",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD, "- /%s reload - reload Plugin",
				cmd.getLabel());
		Plugin.send(player, ChatColor.GOLD,
				"- /%s start <game name> <seconds> - manually start a game",
				cmd.getLabel());
	}

}
