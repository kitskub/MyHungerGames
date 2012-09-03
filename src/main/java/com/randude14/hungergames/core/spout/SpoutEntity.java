package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.LocalEntity;
import com.randude14.hungergames.core.Location;

import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;

/**
 * @author zml2008
 */
public class SpoutEntity extends LocalEntity {
    private final ControllerType type;
    private final int entityId;

    public SpoutEntity(Location position, int id, Controller controller) {
        super(position);
        type = controller.getType();
        this.entityId = id;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public boolean spawn(Location loc) {
        World world = ((SpoutWorld) loc.getWorld()).getWorld();
        Point pos = SpoutUtil.toPoint(world, loc.getPosition());
        Controller controller = type.createController();
        if (controller == null) {
            return false;
        }
        Entity e = world.createAndSpawnEntity(pos, controller);

        if (e != null) {
            e.setPitch(loc.getPitch());
            e.setYaw(loc.getYaw());
            // TODO: Copy datatable info
            return true;
        }
        return false;
    }
}
