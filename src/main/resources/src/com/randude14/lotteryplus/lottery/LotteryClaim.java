package com.randude14.lotteryplus.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LotteryClaim {
	private List<ItemStack> itemRewards;
	private String lotteryName;
	private double pot;

	public LotteryClaim(String lottery, List<ItemStack> itemRewards, double pot) {
		this.itemRewards = itemRewards;
		this.lotteryName = lottery;
		this.pot = pot;
	}

	public String getLotteryName() {
		return lotteryName;
	}

	public double getPot() {
		return pot;
	}

	public void setPot(double pot) {
		this.pot = pot;
	}

	public List<ItemStack> getItemRewards() {
		return itemRewards;
	}

	
	//////////NEW SAVE SYSTEM
	public void save(ConfigurationSection section) {
		section.set("pot", pot);
		section.set("lottery-name", lotteryName);
		if (itemRewards == null || itemRewards.isEmpty()) {
			return;
		}
		ConfigurationSection itemRewardsSection = section.createSection("item-rewards");
		for (ItemStack itemReward : itemRewards) {
			ConfigurationSection itemSection = itemRewardsSection.createSection(itemReward.getType().name());
			itemSection.set("stack-size", itemReward.getAmount());
			if(itemReward.getData() != null) {
				itemSection.set("data", itemReward.getData().getData());
			}
			for(Enchantment enchant : itemReward.getEnchantments().keySet()) {
				itemSection.set(enchant.getName(), itemReward.getEnchantmentLevel(enchant));
			}
		}
	}

	public static LotteryClaim load(ConfigurationSection section) {
		String lotteryName = section.getString("lottery-name");
		double pot = section.getDouble("pot");
		if(!section.isConfigurationSection("item-rewards")) {
			return new LotteryClaim(lotteryName, null, pot);
		}
		List<ItemStack> itemRewards = new ArrayList<ItemStack>();
		ConfigurationSection itemRewardsSection = section.getConfigurationSection("item-rewards");
		for(String matName : itemRewardsSection.getKeys(false)) {
			Material mat = Material.getMaterial(matName);
			if(mat == null) {
				continue;
			}
			ConfigurationSection itemSection = itemRewardsSection.getConfigurationSection(matName);
			int stackSize = itemSection.getInt("stack-size", 1);
			ItemStack item = null;
			if(itemSection.contains("data")) {
				byte data = (byte) itemSection.getInt("data");
				item = new ItemStack(mat, stackSize, data);
			}
			else {
				item = new ItemStack(mat, stackSize);
			}
			for(String ench : itemSection.getKeys(false)) {
				Enchantment enchant = Enchantment.getByName(ench);
				if(enchant == null) {
					continue;
				}
				try {
					int level = itemSection.getInt(ench, 1);
					item.addEnchantment(enchant, level);
				} catch (Exception ex) {
				}
			}
			itemRewards.add(item);
		}
		return new LotteryClaim(lotteryName, itemRewards, pot);
	}
	
	
	
	////////////////UNRELIABLE OLD METHODS
	public Map<String, Object> serialize() {
		Map<String, Object> serialMap = new HashMap<String, Object>();
		serialMap.put("lottery name", lotteryName);
		serialMap.put("pot", pot);
		int cntr = 1;
		for (ItemStack itemReward : itemRewards) {
			serialMap.put("item reward " + (cntr++), itemReward.serialize());
		}
		return serialMap;
	}

	public static LotteryClaim deserialize(Map<String, Object> serialMap) {
		String lotteryName = (String) serialMap.get("lottery name");
		double pot = (Double) serialMap.get("pot");
		List<ItemStack> itemRewards = new ArrayList<ItemStack>();

		for (int cntr = 1; true; cntr++) {

			if (serialMap.containsKey("item reward " + cntr)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> itemMap = (Map<String, Object>) serialMap
						.get("item reward " + cntr);
				ItemStack itemReward = ItemStack.deserialize(itemMap);
				itemRewards.add(itemReward);
			}

			else {
				break;
			}

		}

		return new LotteryClaim(lotteryName, itemRewards, pot);
	}

}
