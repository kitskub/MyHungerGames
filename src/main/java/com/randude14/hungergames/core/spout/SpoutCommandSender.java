package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.CommandSender;

import org.spout.api.command.CommandSource;

public class SpoutCommandSender extends CommandSender {
	private CommandSource sender;

	public SpoutCommandSender(CommandSource sender) {
		this.sender = sender;
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public boolean hasPermission(String perm) {
		return sender.hasPermission(perm);
	}

	@Override
	public void send(String msg) {
		sender.sendMessage(msg);
	}
}
