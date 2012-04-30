package com.randude14.hungergames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Represents a subcommand
 *
 */
public abstract class SubCommand {

    public abstract boolean execute(CommandSender cs, Command cmd, String[] args);
    
}
