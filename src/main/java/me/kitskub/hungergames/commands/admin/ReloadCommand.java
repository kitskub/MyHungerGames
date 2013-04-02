package me.kitskub.hungergames.commands.admin;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.Command;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {

	public ReloadCommand() {
		super(Perm.ADMIN_RELOAD, "reload", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		HungerGames.reload();
		ChatUtils.send(cs, ChatUtils.getPrefix() + "Reloaded %s", HungerGames.getInstance().getDescription().getVersion());
	}

	@Override
	public String getInfo() {
		return "reload MyHungerGames";
	}

	@Override
	protected String getPrivateUsage() {
		return "reload";
	}
    
}
