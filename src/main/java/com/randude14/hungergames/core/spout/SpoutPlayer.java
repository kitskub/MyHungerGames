package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.*;

import org.spout.api.entity.Controller;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.ItemStack;
import org.spout.api.entity.Player;
import org.spout.vanilla.material.VanillaMaterial;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.entity.VanillaPlayerController;

public class SpoutPlayer extends LocalPlayer {
    private Player player;


    public SpoutPlayer(ServerInterface server, Player player) {
        super(server);
        this.player = player;
    }

    @Override
    public int getItemInHand() {
        Controller controller = player.getController();
        if (controller instanceof VanillaPlayerController) {
            ItemStack itemStack = ((VanillaPlayerController) controller).getInventory().getQuickbar().getCurrentItem();
            return itemStack != null ? ((VanillaMaterial) itemStack.getMaterial()).getMinecraftId() : 0;
        } else {
            return 0;
        }
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public WorldVector getPosition() {
        Point loc = player.getPosition();
        return new WorldVector(SpoutUtil.getLocalWorld(loc.getWorld()),
                loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public double getPitch() {
        return player.getPitch();
    }

    @Override
    public double getYaw() {
        return player.getYaw();
    }

    @Override
    public void giveItem(int type, int amt) {
        Controller controller = player.getController();
        if (controller instanceof VanillaPlayerController) {
            ((VanillaPlayerController) controller).getInventory()
                    .addItem(new ItemStack(VanillaMaterials.getMaterial((short) type), amt));
        }
    }

    @Override
    public void send(String msg) {
	    player.sendMessage(msg);
    }

    @Override
    public void setPosition(Vector pos, float pitch, float yaw) {
        player.setPosition(SpoutUtil.toPoint(player.getWorld(), pos));
        player.setPitch(pitch);
        player.setYaw(yaw);
        player.getNetworkSynchronizer().setPositionDirty();
    }

    @Override
    public boolean hasPermission(String perm) {
        return player.hasPermission(perm);
    }

    @Override
    public LocalWorld getWorld() {
        return SpoutUtil.getLocalWorld(player.getWorld());
    }

    public Player getPlayer() {
        return player;
    }
}
