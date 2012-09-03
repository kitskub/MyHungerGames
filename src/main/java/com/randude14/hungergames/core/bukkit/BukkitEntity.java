package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.LocalEntity;
import com.randude14.hungergames.core.bukkit.BukkitUtil;

import com.randude14.hungergames.core.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * @author zml2008
 */
public class BukkitEntity extends LocalEntity {
    private final EntityType type;
    private final UUID entityId;

    public BukkitEntity(Location loc, EntityType type, UUID entityId) {
        super(loc);
        this.type = type;
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    @Override
    public boolean spawn(Location weLoc) {
        org.bukkit.Location loc = BukkitUtil.toLocation(weLoc);
        return loc.getWorld().spawn(loc, type.getEntityClass()) != null;
    }
}
