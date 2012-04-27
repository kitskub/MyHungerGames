package com.randude14.hungergames;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.hungergames.games.HungerGame;
import java.util.Arrays;
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
			if (!Plugin.checkPermission(player, Perm.user_help)) return;
			getUserCommands(player, cmd);
			return;
		}

		else if ("list".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_list)) return;
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
			if (!Plugin.checkPermission(player, Perm.user_join)) return;
			    String name = (args.length == 1) ? Config.getDefaultGame() : args[1];
			    if (name == null) {
				    Plugin.send(player, ChatColor.GOLD, 
					    String.format("/%s join <game name>",
					    cmd.getLabel()));
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
					    String.format(
					    "You are already in the game '%s'. Leave that game before joining another.",
					    currentSession.getName()));
				    return;
			    }
			    if (game.join(player)) {
				    String mess = Config.getJoinMessage();
				    mess = mess.replace("<player>", player.getName()).replace(
						    "<game>", game.getName());
				    Plugin.broadcast(mess);
			    }
		}

		else if ("leave".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_leave)) return;
			
			game = GameManager.getSession(player);
			if (game == null) {
				Plugin.error(player, "You are currently not in a game.");
				return;
			}

			if (game.leave(player)) {
				String mess = Config.getLeaveMessage();
				mess = mess.replace("<player>", player.getName()).replace(
						"<game>", game.getName());
				Plugin.broadcast(mess);
			}

		}

		else if ("rejoin".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_rejoin)) return;
			game = GameManager.getSession(player);
			if (game != null) {
				if (game.rejoin(player)) {
					String mess = Config.getRejoinMessage();
					mess = mess.replace("<player>", player.getName()).replace(
							"<game>", game.getName());
					Plugin.broadcast(mess);
				}

				else {
					Plugin.error(
							player,
							String.format("Failed to rejoin %s.",
									game.getName()));
				}

			}

			else {
				Plugin.error(player, "You are currently not in a game.");
			}

		}

		else if ("sponsor".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_sponsor)) return;

			if (args.length < 2) {
				Plugin.send(player, ChatColor.GOLD,
						String.format("/%s sponsor <player>",
					cmd.getLabel()));
				return;
			}

			Player p = Bukkit.getServer().getPlayer(args[1]);
			if (p == null) {
				Plugin.error(player,
						String.format("%s is not online.", args[1]));
				return;
			}
			if (GameManager.getSession(p) == null) {
				Plugin.error(player,
						String.format("%s is not in a game.", p.getName()));
				return;
			}
			Plugin.addSponsor(player, p.getName());
		}

		else if ("vote".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_vote)) return;

			game = GameManager.getSession(player);
			if (game == null) {
				Plugin.error(player,
					String.format(
					"You must be in a game to vote. You can a game join by '/%s join <game name>'",
					Plugin.CMD_USER));
				return;
			}
			game.addReadyPlayer(player);
			Plugin.send(player, "You have voted that you are ready.");
		}

		else if ("stat".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.user_stat)) return;

			if (args.length == 1) {
				Plugin.send(player, ChatColor.GOLD,
					String.format("/%s stat <game name>", 
					cmd.getLabel()));
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
			if (!Plugin.checkPermission(player, Perm.user_help)) return;
			getUserCommands(player, cmd);
		}
		GameManager.saveGames();// TODO saveTo less
	}

	private void handleAdminCommand(Player player, Command cmd, String[] args) {
		HungerGame game = null;
		GameManager GameManager = Plugin.getGameManager();

		if (args.length == 0) {
			if (!Plugin.hasPermission(player, Perm.admin_help)) return;
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
			if (!Plugin.checkPermission(player, Perm.admin_kick)) return;

			if (args.length == 1) {
				Plugin.send(player, ChatColor.GOLD,
					String.format("/%s kick <player>",
					cmd.getLabel()));
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
					Plugin.error(player, String.format(
							"%s is currently not in a game.",
							kick.getName()));
				}

			}

			else {
			    
			    Plugin.error(player,
				    String.format("%s is not online.", args[1]));
			}
		}

		else if ("start".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.admin_start)) return;

			if (args.length == 1) {
				Plugin.help(player, 
					String.format("/%s start <game name> <seconds>",
					cmd.getLabel()));
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
					Plugin.error(player, String.format(
							"'%s' is not an integer.", args[2]));
					return;
				}

			}

			else {
				seconds = 10;
			}
			if (!game.start(player, seconds)) {
				Plugin.error(player, String.format("Failed to start %s.",
						game.getName()));
			}
		}

		else if ("reload".equals(args[0])) {
			if (!Plugin.checkPermission(player, Perm.admin_reload)) return;
			Plugin.reload();
			Plugin.send(player,
				Plugin.getPrefix() 
				+ String.format("Reloaded v%s", 
				Plugin.getInstance().getDescription().getVersion()));
		}
		else {
			if (!Plugin.checkPermission(player, Perm.admin_help)) return;
			getAdminCommands(player, cmd);
		}
		GameManager.saveGames();// TODO saveTo less
	}

	private boolean addCommand(Player player , String[] args){
	    if (!Plugin.hasPermission(player, Perm.admin_add_chest)
		    && !Plugin.hasPermission(player, Perm.admin_add_spawnpoint)
		    && !Plugin.hasPermission(player, Perm.admin_add_game)) {
		Plugin.error(player, "You do not have permission.");
		return true;
	    }

	    if (args.length == 1 || "?".equals(args[1])) {
		    Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s add spawnpoint <game name> - add a spawnpoint.",
				    args[0]));
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s add chest <game name> - add a chest.",
				    args[0]));
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s add game <game name> - add a game.",
				    args[0]));
		    return true;
	    }
	    
	    HungerGame game = null;
	    if ("spawnpoint".equals(args[1])) {
		if (args.length == 2) {
			Plugin.send(player, String.format(
				"/%s add spawnpoint <game name>",
				args[0]));
			return true;
		}
		
		if (GameManager.doesNameExist(args[2])) {
			game = GameManager.getGame(args[2]);
			Plugin.addSpawnAdder(player, game.getName());
			Plugin.send(player, ChatColor.GREEN, String.format(
					"Hit a block to add it as a spawn point for %s.",
					game.getName()));
		} else {
		    Plugin.sendDoesNotExist(player, args[2]);
		}
	    }

	    else if ("chest".equals(args[1])) {
		    if (args.length == 2) {
			    Plugin.help(player, String.format(
				    "/%s add chest <game name>",
				    args[0]));
			    return true;
		    }
		    
		    if (GameManager.doesNameExist(args[2])) {
			    Plugin.addChestAdder(player, args[2]);
			    Plugin.send(player, ChatColor.GREEN, String.format(
					    "Hit a chest to add it to %s.",
					    GameManager.getGame(args[2]).getName()));
		    } else {
			Plugin.sendDoesNotExist(player, args[2]);
		    }

	    }

	    else if ("game".equals(args[1])) {
		    if (args.length == 2) {
			    Plugin.help(player, String.format(
				    "/%s add game <game name>",
				    args[0]));
		    }
		    
		    if (GameManager.doesNameExist(args[2])) {
			    Plugin.error(player, String.format(
				    "%s already exists.", args[2]));
		    } else {
			    GameManager.createGame(args[2]);
			    Plugin.send(player, ChatColor.GREEN, String.format(
				    "%s has been created.", args[2]));
		    }

	    }
	    
	    else {
		    Plugin.error(player, String.format(
			    "'%s' is not recognized.", args[1]));
	    }
	    return true;
	}
	
	private boolean removeCommand(Player player, String[] args){
	    if (!Plugin.hasPermission(player, Perm.admin_remove_chest)
		    && !Plugin.hasPermission(player, Perm.admin_remove_spawnpoint)
		    && !Plugin.hasPermission(player, Perm.admin_remove_game)) {
		Plugin.error(player, "You do not have permission.");
		return true;
	    }

	    if (args.length == 1 || "?".equals(args[1])) {
		    Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s remove spawnpoint <game name> - remove a spawnpoint.",
				    args[0]));
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s remove chest <game name> - remove a chest.",
				    args[0]));
		    Plugin.send(player, ChatColor.GOLD, String.format(
				    "- /%s remove game <game name> - remove a game.",
				    args[0]));
		    return true;
	    }
	    
	    HungerGame game = null;
	    if ("spawnpoint".equals(args[1])) {
		    if (args.length == 2) {
			    Plugin.help(player, String.format(
				    "/%s remove spawnpoint <game name>",
				    args[0]));
			    return true;
		    }
		    
		    if (GameManager.doesNameExist(args[2])) {
			    game = GameManager.getGame(args[2]);
			    Plugin.addSpawnRemover(player, game.getName());
			    Plugin.send(player, ChatColor.GREEN, String.format(
					    "Hit a spawn point to remove it from %s.",
					    game.getName()));
		    } else {
			Plugin.sendDoesNotExist(player, args[2]);
		    }

	    }

	    else if ("chest".equals(args[1])) {
		    if (args.length == 2) {
			    Plugin.help(player, String.format(
				    "/%s remove chest <game name>",
				    args[0]));
			    return true;
		    }
		    
		    if (GameManager.doesNameExist(args[2])) {
			    Plugin.addChestRemover(player, args[2]);
			    Plugin.send(player, ChatColor.GREEN, String.format(
					    "Hit a chest to remove it from %s.", 
					    GameManager.getGame(args[2]).getName()));
		    } else {
			Plugin.sendDoesNotExist(player, args[2]);
		    }

	    }

	    else if ("game".equals(args[1])) {
		    if (args.length == 2) {
			    Plugin.help(player, String.format(
				    "/%s remove game <game name>",
				    args[0]));
			    return true;
		    }
		    
		    if (GameManager.doesNameExist(args[2])) {
			    GameManager.removeGame(args[2]);
			    Plugin.send(player, ChatColor.GREEN,
					    String.format("%s has been removed.",
				    args[2]));
		    } else {
			Plugin.sendDoesNotExist(player, args[2]);
		    }

	    }

	    else {
		Plugin.error(player,
			String.format("'%s' is not recognized.", args[1]));
	    }

	    return true;
	}
	
	private boolean setCommand(Player player, String[] args){
	    if (!Plugin.hasPermission(player, Perm.admin_set_enabled)
		    && !Plugin.hasPermission(player, Perm.admin_set_spawn)) {
		Plugin.error(player, "You do not have permission.");
		return true;
	    }

	    if (args.length == 1 || "?".equals(args[1])) {
		    Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		    Plugin.send(player, ChatColor.GOLD, String.format(
			    "- /%s set spawn <game name> - set the spawnpoint for a game.", 
			    args[0]));
		    Plugin.send(player, ChatColor.GOLD, String.format(
			    "- /%s set enabled <game name> <true/false> - enable or disable a game.",
			    args[0]));
		    return true;
	    }
	    
	    HungerGame game = null;
	    if ("spawn".equals(args[1])) {
		    if (args.length < 3) {
			    Plugin.send(player, ChatColor.GOLD, String.format(
				    "/%s set spawn <game name>", 
				    args[0]));
			    return true;
		    }
		    game = GameManager.getGame(args[2]);
		    if (game == null) {
			Plugin.sendDoesNotExist(player, args[2]);
		    }
		    Location loc = player.getLocation();
		    game.setSpawn(loc);
		    Plugin.send(
				    player,
				    String.format("Spawn has been set for %s.",
						    game.getName()));
	    }

	    else if ("enabled".equals(args[1])) {
		    if (args.length < 3) {
			    Plugin.send(player, ChatColor.GOLD, String.format(
					    "/%s set enabled <game name> <true/false>",
					    args[0]));
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
			    Plugin.send(player, String.format(
				    "%s has been enabled.",
				    game.getName()));
		    } else {
			    Plugin.send(player, String.format(
				    "%s has been disabled.",
				    game.getName()));
		    }
	    }

	    else {
		    Plugin.error(player, String.format(
			    "'%s' is not recognized.", args[1]));
		    return true;
	    }
	    return true;
	}
	
	private void getUserCommands(Player player, Command cmd) {
		Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s list - list games", 
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s join <game name> - join a game",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s leave - leave current game",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s rejoin - rejoin your current game",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s sponsor <player> - sponsor a player an item",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s vote - cast your vote that you are ready to play",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s stat <game name> - list stats for a game",
				cmd.getLabel()));
	}

	private void getAdminCommands(Player player, Command cmd) {
		Plugin.send(player, ChatColor.GREEN, Plugin.getHeadLiner());
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s add ? - type for more help",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s remove ? - type for more help",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s set ? - type for more help",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s kick <player> - kick a player from a game",
				cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD,
				String.format("- /%s reload - reload Plugin", cmd.getLabel()));
		Plugin.send(player, ChatColor.GOLD, String.format(
				"- /%s start <game name> <seconds> - manually start a game",
				cmd.getLabel()));
	}

}
