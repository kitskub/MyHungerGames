package com.randude14.hungergames;

import com.google.common.base.Strings;
import com.randude14.hungergames.core.*;
import com.randude14.hungergames.core.blocks.Chest;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.listeners.*;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.HGPermission;
import com.randude14.hungergames.utils.ChatUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HungerGames {
	public static final String CMD_ADMIN = "hga", CMD_USER = "hg";
	public static final String name = "MyHungerGames";
	private static HungerGamesPlugin plugin;
	private static HGPermission perm;
	private static Economy econ;
	private static Random rand;

	public static void enable(HungerGamesPlugin plugin) {
		HungerGames.plugin = plugin;
		plugin.registerCommands();
		Files.loadAll();
		rand = new Random(name.hashCode());
		plugin.registerEvents();
		updateConfig();
		econ = plugin.loadEconomy();
		perm = plugin.loadPermission();
		plugin.loadResetter();
		callTasks();
		GameManager.INSTANCE.loadGames();
		LobbyListener.load();
		Logging.info("%s games loaded.", GameManager.INSTANCE.getRawGames().size());
		plugin.loadMetrics();
		Logging.info("Enabled.");
	}

	private static void callTasks() {
	    HungerGames.plugin.getServerInterface().scheduleAsyncRepeatingTask(new Runnable() {
		public void run() {
		    if (!latestVersionCheck())
			    Logging.warning("There is a new version: %s (You are running %s)", latestVersion(), HungerGames.plugin.getVersion());
		}
	    }, 0L, Config.getUpdateDelay() * 20L * 60L);
	}

	public static void disable() {
		for (HungerGame game : GameManager.INSTANCE.getRawGames()) {
			game.stopGame(false);
		}
		GameManager.INSTANCE.saveGames();
		SignListener.saveSigns();
		Logging.info("Games saved.");
		Files.saveAll();
		Logging.info("Disabled.");
	}
	
	public static void reload() {
		Files.loadAll();
		GameManager.INSTANCE.loadGames();
		SignListener.loadSigns();
		econ = plugin.loadEconomy();
	}

	public static boolean hasPermission(CommandSender cs, Defaults.Perm toCheck) {
		return perm.hasPermission(cs, toCheck);
	}

	public static boolean isEconomyEnabled() {
		return econ != null;
	}

	public static void withdraw(LocalPlayer player, double amount) {
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
			return;
		}
		econ.withdraw(player.getName(), amount);
	}

	public static void deposit(LocalPlayer player, double amount) {
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
			return;
		}
		econ.deposit(player.getName(), amount);
	}

	public static boolean hasEnough(LocalPlayer player, double amount) {
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
			return false;
		}
		return econ.hasEnough(player.getName(), amount);
	}

	public static boolean isChest(Location loc) {
		return loc.getBlock() instanceof Chest;
	}

	public static Random getRandom() {
		return rand;
	}

	public static void cancelTask(int taskID) {
		plugin.getServerInterface().cancelTask(taskID);
	}
	
	private static void updateConfig() {
		if (Files.CONFIG.getConfig().contains("global.chest-loot")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("global.chest-loot").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("global.chest-loot." + key);
				Files.ITEMCONFIG.getConfig().set("global.chest-loot." + key, value);
				Files.CONFIG.getConfig().set("global.chest-loot." + key, null);
			}
		}
		if (Files.CONFIG.getConfig().contains("global.sponsor-loot")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("global.sponsor-loot").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("global.sponsor-loot." + key);
				Files.ITEMCONFIG.getConfig().set("global.sponsor-loot." + key, value);
				Files.CONFIG.getConfig().set("global.sponsor-loot." + key, null);
			}
		}
		if (Files.CONFIG.getConfig().contains("itemsets")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("itemsets").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("itemsets." + key);
				Files.ITEMCONFIG.getConfig().set("itemsets." + key, value);
				Files.CONFIG.getConfig().set("itemsets." + key, null);
			}
		}
	}

	public static boolean latestVersionCheck(){
		String datePub = null;
		long timeMod = 0;
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/myhungergames/files.rss");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("pubDate");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				datePub = firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
		}
		DateFormat pubDate = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
		try {
			File jarFile = new File(HungerGames.plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			timeMod = jarFile.lastModified();
		} catch (URISyntaxException e1) {
		}
		try {
			return pubDate.parse(datePub).getTime() <= (timeMod + 86400000);
		} catch (ParseException parseException) {
			return false;
		}
	}
	
	public static String latestVersion() {
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/myhungergames/files.rss");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
		}
		return HungerGames.plugin.getVersion();
	}

	public static void callEvent(Event event) {
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public static String parseToString(Location loc) {
		if (loc == null) return "";
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		df.setDecimalFormatSymbols(symbols);
		return String.format("%s %s %s %s %s %s", df.format((Number) loc.getPosition().getX()), df.format((Number) loc.getPosition().getY()), df.format((Number) loc.getPosition().getZ()), df.format((Number) loc.getYaw()), 
			df.format((Number) loc.getPitch()), loc.getWorld().getName());
	}

	public static Location parseToLoc(String str) throws NumberFormatException{
		Strings.emptyToNull(str);
		if (str == null) {
			return null;
		}
		String[] strs = str.split(" ");
		double x = Double.parseDouble(strs[0]);
		double y = Double.parseDouble(strs[1]);
		double z = Double.parseDouble(strs[2]);
		float yaw = Float.parseFloat(strs[3]);
		float pitch = Float.parseFloat(strs[4]);
		LocalWorld world = plugin.getServerInterface().getWorld(strs[5]);
		if (world == null) return null;
		return new Location(world, new Vector(x, y, z), yaw, pitch);
	}
	
	public static String formatTime(int time) {

		List<String> strs = new ArrayList<String>();
		if(time > 3600) {
			strs.add(String.format("%d hour(s)", (time / 3600) % 24));
		}
		if(time > 60) {
			strs.add(String.format("%d minute(s)", (time / 60) % 60));
		}
		strs.add(String.format("%d second(s)", time % 60));
		StringBuilder buff = new StringBuilder();
		String sep = "";
		for (String str : strs) {
			buff.append(sep);
			buff.append(str);
			sep = ", ";
		}
		return buff.toString();
	}

	public static void playerLeftServer(LocalPlayer player) {
		SessionListener.removePlayer(player);
	}

	public static boolean hasInventoryBeenCleared(LocalPlayer player) {
		LocalPlayerInventory inventory = player.getPlayerInventory();
		for (ItemStack item : inventory.getContents()) {
			if (item != null) {
				return false;
			}

		}
		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null) {
				return false;
			}

		}
		return true;
	}

	public static void fillFixedChest(Chest chest, String name) {
		chest.setContents(new ItemStack[chest.getContents().length]);
		List<ItemStack> items = ItemConfig.getFixedChest(name);
		for (ItemStack stack : items) {
			int index = 0;
			do {
				index = rand.nextInt(chest.getContents().length);
			} while (chest.getContents()[index] != null);
			
			chest.setItem(index, stack);
		}
	}
	
	public static void fillChest(Chest chest, float weight, List<String> itemsets) {
		if (ItemConfig.getGlobalChestLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
			return;
		}

		chest.setContents(new ItemStack[chest.getContents().length]);
		Map<ItemStack, Float> itemMap = ItemConfig.getAllChestLootWithGlobal(itemsets);
		List<ItemStack> items = new ArrayList<ItemStack>(itemMap.keySet());
		int size = chest.getContents().length;
		final int maxItemSize = 100;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		int minItems = (int) Math.floor(numItems/2);
		int itemsIn = 0;
		for (int cntr = 0; cntr < numItems || itemsIn < minItems; cntr++) {
			int index = 0;
			do {
				index = rand.nextInt(chest.getContents().length);
			} while (chest.getContents()[index] != null);
			
			ItemStack item = items.get(rand.nextInt(items.size()));
			if (weight * itemMap.get(item) >= rand.nextFloat()) {
				chest.setItem(index, item);
				itemsIn++;
			}

		}
	}

	public static void rewardPlayer(LocalPlayer player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.addAll(ItemConfig.getStaticRewards());
		
		Map<ItemStack, Float> itemMap = ItemConfig.getRandomRewards();

		int size = ItemConfig.getMaxRandomItems();
		final int maxItemSize = 25;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		for (int cntr = 0; cntr < numItems; cntr++) {			
			ItemStack item = items.get(rand.nextInt(items.size()));
			if (itemMap.get(item) >= rand.nextFloat()) {
				items.add(item);
			}

		}
		for (ItemStack i : player.getPlayerInventory().addItem(items.toArray(new ItemStack[0]))) {
			player.getWorld().dropItem(player.getLocation(), i);
		}
	}
	
	public static HungerGamesPlugin getPlugin() {
		return plugin;
	}

	public static boolean checkPermission(CommandSender cs, Defaults.Perm perm) {
		if (!hasPermission(cs, perm)) {
			ChatUtils.error(cs, Lang.getNoPerm());
			return false;
		}
		return true;
	}

}
