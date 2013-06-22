package me.kitskub.hungergames.api;

import me.kitskub.hungergames.utils.Cuboid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.kitskub.hungergames.games.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Game extends Comparable<Game> {
	public boolean isSpectating(Player player);

	public boolean stopGame(CommandSender notifier, boolean isFinished);
	
	public String stopGame(boolean isFinished);
	
	/**
	 * Starts the game with the specified number of ticks
	 * 
	 * @param player
	 * @param ticks
	 * @return true if game or countdown was successfully started
	 */
	public boolean startGame(CommandSender player, int ticks);
	
	/**
	 * Starts this game with the default time if immediate is true. Otherwise, starts the game immediately.
	 * 
	 * @param notifier who to notify
	 * @param immediate
	 * @return
	 */	
	public boolean startGame(CommandSender notifier, boolean immediate);

	/**
	 * Starts this game with the default time if immediate is true. Otherwise, starts the game immediately.
	 * 
	 * @param immediate
	 * @return
	 */	
	public boolean startGame(boolean immediate);
	
		
	/**
	 * Starts the game
	 * 
	 * @param ticks 
	 * @return Null if game or countdown was successfully started. Otherwise, error message.
	 */
	public String startGame(int ticks);

	public void addAndFillChest(Chest chest);
        
	public void fillInventories();

	public boolean join(Player player);
	
	public boolean quit(Player player, boolean callEvent);
	
	/**
	 * Will be canceled if player is playing and teleporting is not allowed which should not ever happen
	 * @param user
	 */
	public void teleportUserToSpawn(User user);
	
	/**
	 * 
	 * @param notifyOfRemaining
	 * @return true if is over, false if not
	 */
	public boolean checkForGameOver(boolean notifyOfRemaining);
	
	public String getInfo();
	
	/**
	 * Checks if players are in the game and have lives, regardless is game is running and if they are playing.
	 * @param users users to check
	 * @return
	 */
	public boolean contains(User... users);
	
	/**
	 * 
	 * @param users users to check
	 * @return true if players are in the game, have lives, and are playing
	 */
	public boolean isPlaying(User... users);

	/**
	 * Gets the players that have lives and are playing
	 * If game is not yet started remaining players are those that are waiting
	 * 
	 * @return the remaining players that have lives and are playing
	 */
	public List<User> getRemainingPlayers();

	public void listStats(CommandSender notifier);
	
	public String getName();

	public boolean addChest(Location loc, float weight);

	public boolean addFixedChest(Location loc, String fixedChest);

	public boolean addSpawnPoint(Location loc);

	/**
	 * Removes chest from fixedChests and adds it to chests
	 * @param loc
	 * @return
	 */
	public boolean removeFixedChest(Location loc);

	public boolean removeChest(Location loc);

	public boolean removeSpawnPoint(Location loc);

	public void setEnabled(boolean flag);
	
	public void setSpawn(Location newSpawn);

	public List<String> getAllPlayers();

	public List<User> getUsers();
	
	public Location getSpawn();

	public String getSetup();

	public List<String> getItemSets();

	public void addItemSet(String name);

	public void removeItemSet(String name);
		
	public void addWorld(World world);

	public void addCuboid(Location one, Location two);

	public Map<String, List<String>> getSponsors();
	
	public Set<World> getWorlds();
	
	public Set<Cuboid> getCuboids();
	
	public void removeItemsOnGround();
	
	public int getSize();

	public void playCannonBoom();

	public long getStartTime();

	public long getEndTime();
	
	public GameState getState();
	
	public enum GameState {
		DISABLED,
		STOPPED,
		RUNNING,
		COUNTING_FOR_START,
		ABOUT_TO_START;
	}
}
