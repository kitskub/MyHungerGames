package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.Location;
import com.randude14.hungergames.core.bukkit.BukkitUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author zml2008
 */
public class BukkitItem extends BukkitEntity {
    private final ItemStack stack;
    public BukkitItem(Location loc, ItemStack stack, UUID entityId) {
        super(loc, EntityType.DROPPED_ITEM, entityId);
        this.stack = stack;
    }

    @Override
    public boolean spawn(Location weLoc) {
        org.bukkit.Location loc = BukkitUtil.toLocation(weLoc);
        return loc.getWorld().dropItem(loc, stack) != null;
    }
}
