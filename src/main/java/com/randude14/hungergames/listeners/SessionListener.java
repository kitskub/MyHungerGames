package com.randude14.hungergames.listeners;

import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SessionListener implements Listener {
	private static final Map<String, Session> sessions = new HashMap<String, Session>(); // <player, session>>

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerClickedBlock(PlayerInteractEvent event) {
	    Player player = event.getPlayer();
	    Action action = event.getAction();
	    if (!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return;
	    Block clickedBlock = event.getClickedBlock();
	    Location clickedLoc = clickedBlock.getLocation();;
	    SessionType type = null;
	    HungerGame game = null;
	    Session session = null;
	    if (sessions.containsKey(player.getName())) {
		    session = sessions.get(player.getName());
		    type = session.getType();
		    game = session.getGame();
	    }
	    else {
		    return;
	    }
	    event.setCancelled(true); // Because if not sign interacting would be handled on monitor TODO: better?

	    if (game == null) {
		    if (type == SessionType.SIGN_REMOVER) {
			    LobbyListener.removeSign(clickedBlock.getLocation());
			    ChatUtils.send(player, "Sign has been removed.");
			    sessions.remove(player.getName());
			    return;
		    }
		    return;
	    }
	    switch(type) {
		case CHEST_ADDER:
			if (action == Action.LEFT_CLICK_BLOCK) {
				if (!(clickedBlock.getState() instanceof Chest)) {
					ChatUtils.error(player, "Block is not a chest.");
					return;
				}
				float weight = session.getData().get("weight") == null ? 1f : (Float) session.getData().get("weight");
				if (game.addChest(clickedBlock.getLocation(), weight)) {
					ChatUtils.send(player, "Chest has been added to %s.", game.getName());
					session.clicked(clickedBlock);
				}
				else {
					ChatUtils.error(player, "Chest has already been added to game %s.",game.getName());
				}
			}
			else {
				ChatUtils.send(player, "You have added %d chests to the game %s.", session.getBlocks().size(), game.getName());
				sessions.remove(player.getName());
			}
			break;
		case CHEST_REMOVER:
			if (action == Action.LEFT_CLICK_BLOCK) {
				if (!(clickedBlock.getState() instanceof Chest)) {
					ChatUtils.error(player, "Block is not a chest.");
					return;
				}
				if (game.removeChest(clickedBlock.getLocation())) {
					ChatUtils.send(player, "Chest has been removed from %s.", game.getName());
					session.clicked(clickedBlock);
				}
				else {
					ChatUtils.send(player, "Chest has been blacklisted from %s.", game.getName());
				}
			}
			else {
			ChatUtils.send(player, "You have removed %d chests from the game %s.", session.getBlocks().size(), game.getName());
			sessions.remove(player.getName());
			}
			break;
		case SPAWN_ADDER:
			clickedLoc.add(.5, 1, .5);
			if (action == Action.LEFT_CLICK_BLOCK) {
				if (game.addSpawnPoint(clickedLoc)) {
					session.clicked(clickedBlock);
					ChatUtils.send(player, "Spawn point %s has been added to %s.", session.getBlocks().size(), game.getName());
				}
				else {
					ChatUtils.error(player, "%s already has this spawn point.", game.getName());
				}
			}
			else {
				ChatUtils.send(player, "You have added %d spawn points to the game %s.", session.getBlocks().size(), game.getName());
				sessions.remove(player.getName());
			}
			break;
		case SPAWN_REMOVER:
			clickedLoc.add(.5, 1, .5);
			if (action == Action.LEFT_CLICK_BLOCK) {
				if (game.removeSpawnPoint(clickedLoc)) {
					session.clicked(clickedBlock);
					ChatUtils.send(player, "Spawn point %s has been removed from %s.", session.getBlocks().size(), game.getName());
				}
				else {
					ChatUtils.error(player, "%s does not contain this spawn point.", game.getName());
				}
			}
			else {
				ChatUtils.send(player, "You have removed %d spawn points from the game %s.", session.getBlocks().size(), game.getName());
				sessions.remove(player.getName());
			}
			break;
		case CUBOID_ADDER:
			if (session.getBlocks().size() < 1) {
				session.clicked(clickedBlock);
				ChatUtils.send(player, "First corner set.");
			}
			else {
				game.addCuboid(session.getBlocks().get(0).getLocation(), clickedBlock.getLocation());
				sessions.remove(player.getName());
				ChatUtils.send(player, "Second corner and cuboid set.");
			}
			break;
		case FIXED_CHEST_ADDER:
			if (game.addFixedChest(clickedBlock.getLocation(), session.getData().get("name").toString())) {
				sessions.remove(player.getName());
				ChatUtils.send(player, "Chest is now a fixed item chest.");
			}
			else {
				ChatUtils.error(player, "That is not a chest!");    
			}
			break;
		case FIXED_CHEST_REMOVER:
			if (game.removeFixedChest(clickedBlock.getLocation())) {
				sessions.remove(player.getName());
				ChatUtils.send(player, "Chest is no longer a fixed item chest.");
			}
			else {
				ChatUtils.error(player, "That is not a chest! Try again!");    
			}
			break;
		case JOIN_SIGN_ADDER:
			if (LobbyListener.addJoinSign(clickedBlock.getLocation(), game.getName())) {
				sessions.remove(player.getName());
				ChatUtils.send(player, "Join sign has been added successfully.");
			}
			else {
				ChatUtils.error(player, "Error when adding join sign!");    
			}
			break;
		case GAME_SIGN_ADDER:
			if (LobbyListener.addGameSign(clickedBlock.getLocation(), session.getData().get("game").toString())) {
				sessions.remove(player.getName());
				ChatUtils.send(player, "Game sign has been added successfully.");
			}
			else {
				ChatUtils.error(player, "Error when adding game sign! Try again!");    
			}
			break;
		case INFO_WALL_ADDER:
			if (session.getBlocks().size() < 1) {
				session.clicked(clickedBlock);
				ChatUtils.send(player, "First corner set.");
			}
			else {
				ChatUtils.send(player, "Second corner and info wall set.");
				LobbyListener.addInfoWall(session.getBlocks().get(0).getLocation(), clickedBlock.getLocation(), event.getBlockFace(), session.getData().get("game").toString());
				sessions.remove(player.getName());
			}
			break;
		default:
		//Logging.debug("Failed to get sessionlistener.");
		break;
	    }
	}
	
	public static void addSession(SessionType type, Player player, String game) {
		sessions.put(player.getName(), new Session(type, game));
	}
	
	public static void addSession(SessionType type, Player player, String game, Object... data) {
		sessions.put(player.getName(),  new Session(type, game, data));
	}

	public static void removePlayer(Player player) {
		sessions.remove(player.getName());
	}
	
	public enum SessionType {
		FIXED_CHEST_ADDER,
		FIXED_CHEST_REMOVER,
		SPAWN_ADDER,
		SPAWN_REMOVER,
		CHEST_ADDER,
		CHEST_REMOVER,
		CUBOID_ADDER,
		JOIN_SIGN_ADDER,
		GAME_SIGN_ADDER,
		INFO_WALL_ADDER,
		SIGN_REMOVER;
	}
	
	private static class Session {
		private final SessionType type;
		private List<Block> blocks;
		private final String game;
		private final Map<Object, Object> data;

		public Session(SessionType type, String game) {
			this(type, game, "");
		}
		
		public Session(SessionType type, String game, Object... args) {
			this.game = game;
			this.type = type;
			this.blocks = new ArrayList<Block>();
			data = new HashMap<Object, Object>();
			if (args.length % 2 == 1) return;
			for (int i = 0; i < args.length; i += 2) {
				data.put(args[i], args[i + 1]);
			}
		}

		public HungerGame getGame() {
			return ((GameManager) HungerGames.getInstance().getGameManager()).getRawGame(game);
		}

		public void clicked(Block block) {
			blocks.add(block);
		}

		public List<Block> getBlocks() {
			return blocks;
		}

		public SessionType getType() {
			return type;
		}

		public Map<Object, Object> getData() {
			return data;
		}
	}
}
