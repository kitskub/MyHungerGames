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
	private VanillaPlayerController controller;


	public SpoutPlayer(ServerInterface server, Player player) {
		super(server);
		Controller controller = player.getController();
		if (!(controller instanceof VanillaPlayerController)) throw new IllegalArgumentException("player controller must be VanillaPlayerController");
		this.controller = (VanillaPlayerController) controller;
		this.player = player;
	}

	@Override
	public int getItemInHand() {
		ItemStack itemStack = controller.getInventory().getQuickbar().getCurrentItem();
		return itemStack != null ? ((VanillaMaterial) itemStack.getMaterial()).getMinecraftId() : 0;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public com.randude14.hungergames.core.Location getLocation() {
		return SpoutUtil.toLocation(player);
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

	@Override
	public LocalPlayerInventory getPlayerInventory() {
		return new SpoutPlayerInventory(controller.getInventory());
	}
}
