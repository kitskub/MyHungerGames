package me.kitskub.hungergames.commands.admin.remove;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.listeners.SessionListener;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveSignCommand extends PlayerCommand {

	public RemoveSignCommand() {
		super(Perm.ADMIN_REMOVE_SIGN, Commands.ADMIN_REMOVE_HELP.getCommand(), "sign");
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		SessionListener.addSession(SessionListener.SessionType.SIGN_REMOVER, player, "");
		ChatUtils.send(player, ChatColor.GREEN, "Hit a sign to remove it. If you do not hit a sign, nothing will happen.");
	}

	@Override
	public String getInfo() {
		return "remove a sign or an info wall that contains the sign";
	}

	@Override
	protected String getPrivateUsage() {
		return "sign";
	}
	
}
