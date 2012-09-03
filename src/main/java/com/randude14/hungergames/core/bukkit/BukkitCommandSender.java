package com.randude14.hungergames.core.bukkit;

import org.bukkit.command.CommandSender;


public class BukkitCommandSender extends com.randude14.hungergames.core.CommandSender {
    private CommandSender sender;

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public void send(String msg) {
        for (String part : msg.split("\n")) {
            sender.sendMessage("\u00A7d" + part);
        }
    }

    @Override
    public boolean hasPermission(String perm) {
	    return sender.hasPermission(perm);
    }
}
