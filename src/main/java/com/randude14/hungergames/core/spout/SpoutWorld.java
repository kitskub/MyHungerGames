package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.HungerGamesSpout;
import com.randude14.hungergames.core.*;

import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.vanilla.entity.object.moving.Item;
import org.spout.vanilla.entity.object.moving.PrimedTnt;
import org.spout.vanilla.entity.object.projectile.Arrow;
import org.spout.vanilla.entity.object.vehicle.Boat;
import org.spout.vanilla.entity.object.vehicle.Minecart;
import org.spout.vanilla.material.VanillaMaterial;
import org.spout.vanilla.material.VanillaMaterials;

import java.util.ArrayList;
import java.util.List;
import org.spout.api.inventory.ItemStack;
import org.spout.api.math.Vector3;

public class SpoutWorld extends LocalWorld {
    private World world;

    /**
     * Construct the object.
     * @param world
     */
    public SpoutWorld(World world) {
        this.world = world;
    }

    /**
     * Get the world handle.
     *
     * @return
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the name of the world
     *
     * @return
     */
    @Override
    public String getName() {
        return world.getName();
    }

    /**
     * Set block type.
     *
     * @param pt
     * @param type
     * @return
     */
    @Override
    public boolean setBlockType(Vector pt, int type) {
        Material mat = VanillaMaterials.getMaterial((short) type);
        if (mat != null && mat instanceof BlockMaterial) {
            return world.setBlockMaterial(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(), (BlockMaterial) mat, (short)0, (HungerGamesSpout) HungerGames.getInstance());
        }
        return false;
    }

    /**
     * Set block type.
     *
     * @param pt
     * @param type
     * @return
     */
    @Override
    public boolean setBlockTypeFast(Vector pt, int type) {
        return setBlockType(pt, type);
    }

    /**
     * set block type & data
     * @param pt
     * @param type
     * @param data
     * @return
     */
    @Override
    public boolean setTypeIdAndData(Vector pt, int type, int data) {
        Material mat = VanillaMaterials.getMaterial((short) type);
        if (mat != null && mat instanceof BlockMaterial) {
            return world.setBlockMaterial(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(), (BlockMaterial) mat, (short)data, (HungerGamesSpout) HungerGames.getInstance());
        }
        return false;
    }

    /**
     * set block type & data
     * Everything is threaded, so no need for fastmode here.
     * @param pt
     * @param type
     * @param data
     * @return
     */
    @Override
    public boolean setTypeIdAndDataFast(Vector pt, int type, int data) {
        return setTypeIdAndData(pt, type, data);
    }

    /**
     * Get block type.
     *
     * @param pt
     * @return
     */
    @Override
    public int getBlockType(Vector pt) {
        Material mat = world.getBlockMaterial(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
        return mat instanceof VanillaMaterial? ((VanillaMaterial) mat).getMinecraftId() : 0;
    }

    /**
     * Set block data.
     *
     * @param pt
     * @param data
     */
    @Override
    public void setBlockData(Vector pt, int data) {
        world.setBlockData(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(), (short) data, (HungerGamesSpout) HungerGames.getInstance());
    }

    /**
     * Set block data.
     *
     * @param pt
     * @param data
     */
    @Override
    public void setBlockDataFast(Vector pt, int data) {
        setBlockData(pt, data);
    }

    /**
     * Get block data.
     *
     * @param pt
     * @return
     */
    @Override
    public int getBlockData(Vector pt) {
        return world.getBlockData(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
    }

    /**
     * Get block light level.
     *
     * @param pt
     * @return
     */
    @Override
    public int getBlockLightLevel(Vector pt) {
        return world.getBlockLight(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ());
    }

    /**
     * Remove entities in an area.
     *
     * @param origin
     * @param radius
     * @return
     */
    @Override
    public int removeEntities(EntityType type, Vector origin, int radius) {
        int num = 0;
        double radiusSq = radius * radius;

        for (Entity ent : world.getAll()) {
            if (radius != -1
                    && origin.distanceSq(SpoutUtil.toVector(ent.getPosition())) > radiusSq) {
                continue;
            }

            if (type == EntityType.ARROWS) {
                if (ent.getController() instanceof Arrow) {
                    ent.kill();
                    ++num;
                }
            } else if (type == EntityType.BOATS) {
                if (ent.getController() instanceof Boat) {
                    ent.kill();
                    ++num;
                }
            } else if (type == EntityType.ITEMS) {
                if (ent.getController() instanceof Item) {
                    ent.kill();
                    ++num;
                }
            } else if (type == EntityType.MINECARTS) {
                if (ent.getController() instanceof Minecart) {
                    ent.kill();
                    ++num;
                }
            } /*else if (type == EntityType.PAINTINGS) {
                if (ent.getController() instanceof Painting) {
                    ent.kill();
                    ++num;
                }
            }*/ else if (type == EntityType.TNT) {
                if (ent.getController() instanceof PrimedTnt) {
                    ent.kill();
                    ++num;
                }
            } /*else if (type == EntityType.XP_ORBS) {
                if (ent instanceof ExperienceOrb) {
                    ent.kill();
                    ++num;
                }
            }*/
        }

        return num;
    }


    @Override
    public void checkLoadedChunk(Vector pt) {
        world.getChunk(pt.getBlockX() << Chunk.BLOCKS.BITS, pt.getBlockY() << Chunk.BLOCKS.BITS, pt.getBlockZ() << Chunk.BLOCKS.BITS);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SpoutWorld)) {
            return false;
        }

        return ((SpoutWorld) other).world.equals(world);
    }

    @Override
    public int hashCode() {
        return world.hashCode();
    }

    @Override
    public int getMaxY() {
        return world.getHeight() - 1;
    }

    @Override
    public SpoutEntity[] getEntities(Region region) {
        List<SpoutEntity> entities = new ArrayList<SpoutEntity>();
        for (Vector pt : region.getChunkCubes()) {
            Chunk chunk = world.getChunk(pt.getBlockX(), pt.getBlockY(), pt.getBlockZ(), LoadOption.NO_LOAD);
            if (chunk == null) {
                continue;
            }
            for (Entity ent : chunk.getEntities()) {
                if (region.contains(SpoutUtil.toVector(ent.getPosition()))) {
                    entities.add(new SpoutEntity(SpoutUtil.toLocation(ent), ent.getId(), ent.getController()));
                }
            }
        }
        return entities.toArray(new SpoutEntity[entities.size()]);
    }

    @Override
    public int killEntities(LocalEntity... entities) {
        int amount = 0;
        for (LocalEntity weEnt : entities) {
            SpoutEntity entity = (SpoutEntity) weEnt;
            Entity spoutEntity = world.getEntity(entity.getEntityId());
            if (spoutEntity != null) {
                spoutEntity.kill();
                ++amount;
            }
        }
        return amount;
    }
    
    /**
     * Drop an item.
     *
     * @param pt
     * @param item
     */
    @Override
    public void dropItem(Vector pt, BaseItemStack item) {
        Material mat = VanillaMaterials.getMaterial((short) item.getType());
        if (mat.hasSubMaterials()) {
            mat = mat.getSubMaterial(item.getData());
        }
        ItemStack spoutItem = new ItemStack(mat, item.getData(), item.getAmount());
        world.createAndSpawnEntity(SpoutUtil.toPoint(world, pt), new Item(spoutItem, new Vector3(pt.getX(), pt.getY(), pt.getZ())));
    }
}
