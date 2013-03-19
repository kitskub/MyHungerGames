package com.randude14.hungergames.utils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("Item")
public class Item implements ConfigurationSerializable {
	private ItemStack stack;
	private Map<String, Object> values;
				
	public Item(Map<String, Object> map) {
		this((ItemStack) map.get("stack"), map.get("values") == null ? null : (Map<String, Object>) map.get("values"));
	}
	
	public Item(ItemStack stack, Map<String, Object> values) {
		this.stack = stack;
		this.values = values == null ? new HashMap<String, Object>() : values;
	}
	

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stack", stack);
		map.put("values", values);
		return map;
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
}
