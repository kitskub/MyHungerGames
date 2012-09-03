package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class BukkitPlayer extends LocalPlayer {
    private Player player;

    public BukkitPlayer(ServerInterface server, Player player) {
        super(server);
        this.player = player;
    }

    @Override
    public int getItemInHand() {
        ItemStack itemStack = player.getItemInHand();
        return itemStack != null ? itemStack.getTypeId() : 0;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public WorldVector getPosition() {
        Location loc = player.getLocation();
        return new WorldVector(BukkitUtil.getLocalWorld(loc.getWorld()),
                loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public double getPitch() {
        return player.getLocation().getPitch();
    }

    @Override
    public double getYaw() {
        return player.getLocation().getYaw();
    }

    @Override
    public void giveItem(int type, int amt) {
        player.getInventory().addItem(new ItemStack(type, amt));
    }

    @Override
    public void send(String msg) {
	player.sendMessage(msg);
    }

    @Override
    public void setPosition(Vector pos, float pitch, float yaw) {
        player.teleport(new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ(), yaw, pitch));
    }

    @Override
    public boolean hasPermission(String perm) {
	    return player.hasPermission(perm);
    }

    @Override
    public LocalWorld getWorld() {
        return BukkitUtil.getLocalWorld(player.getWorld());
    }

    public Player getPlayer() {
        return player;
    }
}
