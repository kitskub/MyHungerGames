package com.randude14.hungergames.core;

public abstract class CommandSender {
	
	public abstract String getName();

	public abstract boolean hasPermission(String perm);

	public abstract void send(String msg);
}
