package me.kitskub.hungergames.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.ItemConfig;
import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.WorldNotFoundException;
import org.apache.commons.lang.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GeneralUtils {
	public static boolean equals(Location loc1, Location loc2) {
		return loc1.getWorld() == loc2.getWorld()
			&& loc1.getBlockX() == loc2.getBlockX()
			&& loc1.getBlockY() == loc2.getBlockY()
			&& loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	public static String parseToString(Location loc) {
		if (loc == null) return "";
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		df.setGroupingUsed(false);
		return String.format("%s %s %s %s %s %s", df.format((Number) loc.getX()), df.format((Number) loc.getY()), df.format((Number) loc.getZ()), df.format((Number) loc.getYaw()), 
			df.format((Number) loc.getPitch()), loc.getWorld().getName());
	}

	public static Location parseToLoc(String str) throws NumberFormatException, WorldNotFoundException, IllegalArgumentException {
		Strings.emptyToNull(str);
		if (str == null) {
			throw new IllegalArgumentException("Location can not be null.");
		}
		String[] strs = str.split(" ");
		double x = Double.parseDouble(strs[0]);
		double y = Double.parseDouble(strs[1]);
		double z = Double.parseDouble(strs[2]);
		float yaw = Float.parseFloat(strs[3]);
		float pitch = Float.parseFloat(strs[4]);
		World world = Bukkit.getServer().getWorld(strs[5]);
		if (world == null) throw new WorldNotFoundException("Could not load world \"" + strs[5] + "\" when loading location \"" + str);
		return new Location(world, x, y, z, yaw, pitch);
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
	
	public static boolean hasInventoryBeenCleared(Player player) {
		PlayerInventory inventory = player.getInventory();
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		return true;
	}

	public static void fillFixedChest(Chest chest, String name) {
		chest.getInventory().clear();
		List<ItemStack> items = ItemConfig.getFixedChest(name);
		for (ItemStack stack : items) {
			int index = 0;
			do {
				index = HungerGames.getRandom().nextInt(chest.getInventory().getSize());
			} while (chest.getInventory().getItem(index) != null);
			
			chest.getInventory().setItem(index, stack);
		}
	}

	private static List<Integer> range(int min, int max) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = min; i <= max; i++) {
			list.add(i);
		}
		return list;
	}

	public static void fillChest(Chest chest, float weight, List<String> itemsets) {
		if (weight == 0 || (ItemConfig.getGlobalChestLoot().isEmpty() && (itemsets == null || itemsets.isEmpty()))) {
			return;
		}
		chest.getInventory().clear();

		//Contains the whole itemset.
		Map<ItemStack, Double> map = ItemConfig.getAllChestLootWithGlobal(itemsets);

		//Chest size
		final int size = chest.getInventory().getSize() - 1;

		List<Integer> slots = range(1, size);//By adding this, we know that we won't pick an index that has been used before

		//This calculate the amount of items that will be in the chest.
		final int maxItemSize = 100;
		final int amountCount = map.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(map.size()))/Math.sqrt(maxItemSize));

		//Let's calculate what item we can have.
		Iterator<Map.Entry<ItemStack, Double>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<ItemStack, Double> entry = iterator.next();
			if (HungerGames.getRandom().nextDouble() >= entry.getValue()) {
				iterator.remove();
			}

		}

		ArrayList<ItemStack> arrayItemStack = new ArrayList<ItemStack>(map.keySet());
		//We add the items in the chest.
		for (int i = 0; i < amountCount; i++) {
			ItemStack stack = arrayItemStack.get(HungerGames.getRandom().nextInt(arrayItemStack.size()));
			int slot = HungerGames.getRandom().nextInt(slots.size());
			chest.getInventory().setItem(slot, stack);
			slots.remove(slot);
		}
	}

	public static void rewardPlayer(Player player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.addAll(ItemConfig.getStaticRewards());
		Logging.debug("rewardPlayer: items after static: " + ArrayUtils.toString(items));
		Map<ItemStack, Double> itemMap = ItemConfig.getRandomRewards();

		int size = ItemConfig.getMaxRandomItems();
		final int maxItemSize = 25;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		Logging.debug("rewardPlayer: items after random: " + ArrayUtils.toString(items));
		for (int cntr = 0; cntr < numItems; cntr++) {			
			ItemStack item = null;
			while (item == null) { // TODO items should not have any null elements, but do.
				item = items.get(HungerGames.getRandom().nextInt(items.size()));
			}
			if (itemMap.get(item) >= HungerGames.getRandom().nextFloat()) {
				items.add(item);
			}

		}
		for (ItemStack i : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
			player.getLocation().getWorld().dropItem(player.getLocation(), i);
		}
	}
	
	public static String getNonPvpDeathCause(PlayerDeathEvent e) {
		String cause = "Unknown";
		Player player = e.getEntity();
		if (player.getLastDamageCause() != null) {
			EntityDamageEvent lastDemageCause = player.getLastDamageCause();
			EntityDamageEvent.DamageCause damageCause = lastDemageCause.getCause();
						
			// was killed by entity
			if ((lastDemageCause instanceof EntityDamageByEntityEvent)) {
				EntityDamageByEntityEvent kie = (EntityDamageByEntityEvent) lastDemageCause;
				Entity damager = kie.getDamager();
				if (damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
					if ((damager instanceof Zombie))
						cause = "Zombie";
					if ((damager instanceof Creeper))
						cause = "Creeper";
					if ((damager instanceof Spider))
						cause = "Spider";
					if ((damager instanceof CaveSpider))
						cause = "CaveSpider";
					if ((damager instanceof Enderman))
						cause = "Enderman";
					if ((damager instanceof Silverfish))
						cause = "Silverfish";
					if ((damager instanceof MagmaCube))
						cause = "MagmaCube";
					if ((damager instanceof Slime))
						cause = "Slime";
					if ((damager instanceof Wolf))
						cause = "Wolf";
					if ((damager instanceof PigZombie))
						cause = "PigZombie";
					if ((damager instanceof IronGolem))
						cause = "IronGolem";
					if ((damager instanceof Giant))
						cause = "Giant";
					if (damager.getType() == EntityType.SKELETON) {
						Skeleton skeleton = (Skeleton) damager;
						if (skeleton.getSkeletonType().equals(Skeleton.SkeletonType.NORMAL))
							cause = "SkeletonMelee";
						if (skeleton.getSkeletonType().equals(Skeleton.SkeletonType.WITHER))
							cause = "WitherSkeleton";

					}

				} else if (damageCause == EntityDamageEvent.DamageCause.PROJECTILE) {
					Projectile projectile = (Projectile) damager;

					if ((projectile instanceof Arrow)) {
						if ((projectile.getShooter() instanceof Skeleton))
							cause = "SkeletonArcher";
						else {
							cause = "Arrow";
						}
					} else if ((projectile instanceof Snowball)) {
						if ((projectile.getShooter() instanceof Snowman))
							cause = "Snowman";
					} else if ((projectile instanceof Fireball)) {
						if ((projectile.getShooter() instanceof Ghast))
							cause = "Ghast";
						else if ((projectile.getShooter() instanceof Blaze))
							cause = "Blaze";
						else if ((projectile.getShooter() instanceof Wither))
							cause = "Wither";
						else {
							cause = "Fireball";
						}
					}

				} else if ((damageCause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
						|| (damageCause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
					if ((damager instanceof Creeper))
						cause = "Creeper";
					if ((damager instanceof TNTPrimed))
						cause = "TNT";
				}

			} else {
				if (damageCause == EntityDamageEvent.DamageCause.DROWNING)
					cause = "Drowning";
				if (damageCause == EntityDamageEvent.DamageCause.STARVATION)
					cause = "Starvation";
				if (damageCause == EntityDamageEvent.DamageCause.CONTACT)
					cause = "Cactus";
				if (damageCause == EntityDamageEvent.DamageCause.CUSTOM)
					cause = "Unknown";
				if (damageCause == EntityDamageEvent.DamageCause.FIRE)
					cause = "Fire";
				if (damageCause == EntityDamageEvent.DamageCause.FIRE_TICK)
					cause = "Fire";
				if (damageCause == EntityDamageEvent.DamageCause.LAVA)
					cause = "Lava";
				if (damageCause == EntityDamageEvent.DamageCause.LIGHTNING)
					cause = "Lightning";
				if (damageCause == EntityDamageEvent.DamageCause.POISON)
					cause = "Poison";
				if (damageCause == EntityDamageEvent.DamageCause.SUFFOCATION)
					cause = "Suffocation";
				if (damageCause == EntityDamageEvent.DamageCause.VOID)
					cause = "Void";
				if (damageCause == EntityDamageEvent.DamageCause.FALL)
					cause = "Fall";
				if (damageCause == EntityDamageEvent.DamageCause.SUICIDE)
					cause = "Suicide";
				if (damageCause == EntityDamageEvent.DamageCause.MAGIC)
					cause = "PotionofHarming";
				if (damageCause == EntityDamageEvent.DamageCause.WITHER)
					cause = "WitherEffect";
				if (damageCause == EntityDamageEvent.DamageCause.FALLING_BLOCK)
					cause = "Anvil";
			}
		}

		return cause;
	}
}
