package com.randude14.hungergames;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.hungergames.games.HungerGame;

public class Commands implements CommandExecutor {
	private final Plugin plugin;

	public Commands(final Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("In-game use only.");
			return false;
		}
		if (cmd.getLabel().equals(Plugin.CMD_USER)) {
			handleUserCommand((Player) sender, cmd, args);
		} else if (cmd.getLabel().equals(Plugin.CMD_ADMIN)) {
			handleAdminCommand((Player) sender, cmd, args);
		}
		return false;
	}

	private void handleUserCommand(Player player, Command cmd, String[] args) {
		GameManager manager = plugin.getGameManager();

		if (args.length == 0) {
			if (!plugin.hasPermission(player, Perm.user_help)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			getUserCommands(player, cmd);
		}

		else if ("list".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_list)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
			Collection<HungerGame> games = manager.getGames();
			if (games.isEmpty()) {
				plugin.error(player, "No games have been created yet.");
			} else {
				for (HungerGame game : games) {
					plugin.send(player, ChatColor.GOLD, "- " + game.getInfo());
				}

			}
		}

		else if ("join".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_join)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			else {
				String name = (args.length == 1) ? plugin.getPluginConfig()
						.getDefaultGame() : args[1];
				if (name == null) {
					plugin.send(
							player,
							ChatColor.GOLD,
							String.format("/%s join <game name>",
									cmd.getLabel()));
					return;
				}
				HungerGame game = manager.getGame(name);
				if (game == null) {
					plugin.error(player,
							String.format("%s does not exist.", name));
					return;
				}
				HungerGame currentSession = manager.getSession(player);
				if (currentSession != null) {
					plugin.error(
							player,
							String.format(
									"You are already in the game '%s'. Leave that game before joining another.",
									currentSession.getName()));
					return;
				}
				if (game.join(player)) {
					String mess = plugin.getPluginConfig().getJoinMessage();
					mess = mess.replace("<player>", player.getName()).replace(
							"<game>", game.getName());
					plugin.broadcast(mess);
				}

			}

		}

		else if ("leave".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_leave)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			HungerGame game = manager.getSession(player);
			if (game == null) {
				plugin.error(player, "You are currently not in a game.");
				return;
			}

			if (game.leave(player)) {
				String mess = plugin.getPluginConfig().getLeaveMessage();
				mess = mess.replace("<player>", player.getName()).replace(
						"<game>", game.getName());
				plugin.broadcast(mess);
			}

		}

		else if ("rejoin".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_rejoin)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			HungerGame game = manager.getSession(player);
			if (game != null) {
				if (game.rejoin(player)) {
					String mess = plugin.getPluginConfig().getRejoinMessage();
					mess = mess.replace("<player>", player.getName()).replace(
							"<game>", game.getName());
					plugin.broadcast(mess);
				}

				else {
					plugin.error(
							player,
							String.format("Failed to rejoin %s.",
									game.getName()));
				}

			}

			else {
				plugin.error(player, "You are currently not in a game.");
			}

		}

		else if ("sponsor".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_sponsor)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length < 2) {
				plugin.send(player, ChatColor.GOLD,
						String.format("/%s sponsor <player>", cmd.getLabel()));
			}

			else {
				Player p = plugin.getServer().getPlayer(args[1]);
				if (p == null) {
					plugin.error(player,
							String.format("%s is not online.", args[1]));
					return;
				}
				if (manager.getSession(p) == null) {
					plugin.error(player,
							String.format("%s is not in a game.", p.getName()));
					return;
				}
				plugin.addSponsor(player, p.getName());
			}

		}

		else if ("vote".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_vote)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			HungerGame game = manager.getSession(player);
			if (game == null) {
				plugin.error(
						player,
						String.format(
								"You must be in a game to vote. You can a game join by '/%s join <game name>'",
								Plugin.CMD_USER));
				return;
			}
			game.addReadyPlayer(player);
		}

		else if ("stat".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.user_stat)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length == 1) {
				plugin.send(player, ChatColor.GOLD,
						String.format("/%s stat <game name>", cmd.getLabel()));
			}

			else {
				HungerGame game = manager.getGame(args[1]);
				if (game == null) {
					plugin.error(player,
							String.format("%s does not exist.", args[1]));
					return;
				}
				plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
				game.listStats(player);
			}

		}

		else {
			if (!plugin.hasPermission(player, Perm.user_help)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			getUserCommands(player, cmd);
		}

	}

	private void handleAdminCommand(Player player, Command cmd, String[] args) {
		GameManager manager = plugin.getGameManager();

		if (args.length == 0) {
			if (!plugin.hasPermission(player, Perm.admin_help)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			getAdminCommands(player, cmd);
		}

		else if ("add".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_add_chest)
					&& !plugin.hasPermission(player, Perm.admin_add_spawnpoint)
					&& !plugin.hasPermission(player, Perm.admin_add_game)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length == 1 || "?".equals(args[1])) {
				plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
				plugin.send(player, ChatColor.GOLD, String.format(
						"- /%s add spawnpoint <game name> - add a spawnpoint.",
						cmd.getLabel()));
				plugin.send(player, ChatColor.GOLD, String.format(
						"- /%s add chest <game name> - add a chest.",
						cmd.getLabel()));
				plugin.send(player, ChatColor.GOLD, String.format(
						"- /%s add game <game name> - add a game.",
						cmd.getLabel()));
			}

			else if ("spawnpoint".equals(args[1])) {
				if (args.length == 2) {
					plugin.send(
							player,
							String.format("/%s add spawnpoint <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					HungerGame game = manager.getGame(args[2]);
					plugin.addSpawnAdder(player, game.getName());
					plugin.send(player, ChatColor.GREEN, String.format(
							"Hit a block to add it as a spawn point for %s.",
							game.getName()));
				} else {
					plugin.error(player,
							String.format("%s does not exist.", args[2]));
				}

			}

			else if ("chest".equals(args[1])) {
				if (args.length == 2) {
					plugin.help(
							player,
							String.format("/%s add chest <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					plugin.addChestAdder(player, args[2]);
					plugin.send(player, ChatColor.GREEN, String.format(
							"Hit a chest to add it to %s.",
							manager.getGame(args[2]).getName()));
				} else {
					plugin.error(player,
							String.format("%s does not exist.", args[2]));
				}

			}

			else if ("game".equals(args[1])) {
				if (args.length == 2) {
					plugin.help(
							player,
							String.format("/%s add game <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					plugin.error(player,
							String.format("%s already exists.", args[2]));
				} else {
					manager.createGame(args[2]);
					plugin.send(player, ChatColor.GREEN,
							String.format("%s has been created.", args[2]));
				}

			}

			else {
				plugin.error(player,
						String.format("'%s' is not recognized.", args[1]));
			}

		}

		else if ("remove".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_remove_chest)
					&& !plugin.hasPermission(player,
							Perm.admin_remove_spawnpoint)
					&& !plugin.hasPermission(player, Perm.admin_remove_game)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length == 1 || "?".equals(args[1])) {
				plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
				plugin.send(
						player,
						ChatColor.GOLD,
						String.format(
								"- /%s remove spawnpoint <game name> - remove a spawnpoint.",
								cmd.getLabel()));
				plugin.send(player, ChatColor.GOLD, String.format(
						"- /%s remove chest <game name> - remove a chest.",
						cmd.getLabel()));
				plugin.send(player, ChatColor.GOLD, String.format(
						"- /%s remove game <game name> - remove a game.",
						cmd.getLabel()));
			}

			else if ("spawnpoint".equals(args[1])) {
				if (args.length == 2) {
					plugin.help(player,
							String.format("/%s remove spawnpoint <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					HungerGame game = manager.getGame(args[2]);
					plugin.addSpawnRemover(player, game.getName());
					plugin.send(player, ChatColor.GREEN, String.format(
							"Hit a spawn point to remove it from %s.",
							game.getName()));
				} else {
					plugin.error(player,
							String.format("%s does not exist.", args[2]));
				}

			}

			else if ("chest".equals(args[1])) {
				if (args.length == 2) {
					plugin.help(
							player,
							String.format("/%s remove chest <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					plugin.addChestRemover(player, args[2]);
					plugin.send(player, ChatColor.GREEN, String.format(
							"Hit a chest to remove it from %s.", manager
									.getGame(args[2]).getName()));
				} else {
					plugin.error(player,
							String.format("%s does not exist.", args[2]));
				}

			}

			else if ("game".equals(args[1])) {
				if (args.length == 2) {
					plugin.help(
							player,
							String.format("/%s remove game <game name>",
									cmd.getLabel()));
				} else if (manager.doesNameExist(args[2])) {
					manager.removeGame(args[2]);
					plugin.send(player, ChatColor.GREEN,
							String.format("%s has been removed.", args[2]));
				} else {
					plugin.error(player,
							String.format("%s does not exist.", args[2]));
				}

			}

			else {
				plugin.error(player,
						String.format("'%s' is not recognized.", args[1]));
			}

		}

		else if ("set".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_set_enabled)
					&& !plugin.hasPermission(player, Perm.admin_set_spawn)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length == 1 || "?".equals(args[1])) {
				plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
				plugin.send(
						player,
						ChatColor.GOLD,
						String.format(
								"- /%s set spawn <game name> - set the spawnpoint for a game.",
								cmd.getLabel()));
				plugin.send(
						player,
						ChatColor.GOLD,
						String.format(
								"- /%s set enabled <game name> <true/false> - enable or disable a game.",
								cmd.getLabel()));

			}

			else if ("spawn".equals(args[1])) {
				if (args.length < 3) {
					plugin.send(
							player,
							ChatColor.GOLD,
							String.format("/%s set spawn <game name>",
									cmd.getLabel()));
					return;
				}
				HungerGame game = manager.getGame(args[2]);
				if (game == null) {
					plugin.send(player,
							String.format("%s does not exist.", args[2]));
				}
				Location loc = player.getLocation();
				game.setSpawn(loc);
				plugin.send(
						player,
						String.format("Spawn has been set for %s.",
								game.getName()));
			}

			else if ("enabled".equals(args[1])) {
				if (args.length < 3) {
					plugin.send(player, ChatColor.GOLD, String.format(
							"/%s set enabled <game name> <true/false>",
							cmd.getLabel()));
					return;
				}
				HungerGame game = manager.getGame(args[2]);
				if (game == null) {
					plugin.send(player,
							String.format("%s does not exist.", args[2]));
				}
				Boolean flag;
				if (args.length == 4) {
					flag = new Boolean(args[3]);
				} else {
					flag = Boolean.TRUE;
				}
				game.setEnabled(flag);
				if (flag) {
					plugin.send(
							player,
							String.format("%s has been enabled.",
									game.getName()));
				} else {
					plugin.send(
							player,
							String.format("%s has been disabled.",
									game.getName()));
				}

			}

			else {
				plugin.error(player,
						String.format("'%s' is not recognized.", args[1]));
			}

		}

		else if ("kick".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_kick)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			if (args.length == 1) {
				plugin.send(player, ChatColor.GOLD,
						String.format("/%s kick <player>", cmd.getLabel()));
			}

			else {
				Player kick = plugin.getServer().getPlayer(args[1]);
				if (kick != null) {
					HungerGame game = manager.getSession(kick);
					if (game != null) {
						plugin.broadcast(String.format(
								"%s has been kicked from the game %s.",
								player.getName(), game.getName()));
						game.leave(kick);
					}

					else {
						plugin.error(player, String.format(
								"%s is currently not in a game.",
								kick.getName()));
					}

				}

				else {
					plugin.error(player,
							String.format("%s is not online.", args[1]));
				}

			}

		}

		else if ("start".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_start)) {
				plugin.error(player, "You do not have permission.");
				return;
			}

			else if (args.length == 1) {
				plugin.help(
						player,
						String.format("/%s start <game name> <seconds>",
								cmd.getLabel()));
			}

			else {
				HungerGame game = manager.getGame(args[1]);
				if (game == null) {
					plugin.error(player,
							String.format("%s does not exist.", args[1]));
					return;
				}

				int seconds;

				if (args.length == 3) {
					try {
						seconds = Integer.parseInt(args[2]);
					} catch (Exception ex) {
						plugin.error(player, String.format(
								"'%s' is not an integer.", args[2]));
						return;
					}

				}

				else {
					seconds = 10;
				}
				if (!game.start(player, seconds)) {
					plugin.error(player, String.format("Failed to start %s.",
							game.getName()));
				}

			}

		}

		else if ("reload".equals(args[0])) {
			if (!plugin.hasPermission(player, Perm.admin_reload)) {
				plugin.error(player, "You do not have permission.");
			}
			plugin.reload();
			plugin.send(
					player,
					plugin.getPrefix()
							+ String.format("reloaded v%s", plugin
									.getDescription().getVersion()));
		}

		else {
			if (!plugin.hasPermission(player, Perm.admin_help)) {
				plugin.error(player, "You do not have permission.");
				return;
			}
			getAdminCommands(player, cmd);
		}

	}

	private void getUserCommands(Player player, Command cmd) {
		plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
		plugin.send(player, ChatColor.GOLD,
				String.format("- /%s list - list games", cmd.getLabel()));
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s join <game name> - join a game",
						cmd.getLabel()));
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s leave - leave current game",
						cmd.getLabel()));
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s rejoin - rejoin your current game",
						cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s sponsor <player> - sponsor a player an item",
				cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s vote - cast your vote that you are ready to play",
				cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s stat <game name> - list stats for a game",
				cmd.getLabel()));
	}

	private void getAdminCommands(Player player, Command cmd) {
		plugin.send(player, ChatColor.GREEN, plugin.getHeadLiner());
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s add ? - type for more help",
						cmd.getLabel()));
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s remove ? - type for more help",
						cmd.getLabel()));
		plugin.send(
				player,
				ChatColor.GOLD,
				String.format("- /%s set ? - type for more help",
						cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s kick <player> - kick a player from a game",
				cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD,
				String.format("- /%s reload - reload plugin", cmd.getLabel()));
		plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s start <game name> <seconds> - manually start a game",
				cmd.getLabel()));
	}

}
