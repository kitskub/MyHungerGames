package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class AboutCommand extends Command {

	public AboutCommand() {
		super(Perm.USER_ABOUT, "about", Command.USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		ChatUtils.send(cs, ChatUtils.getHeadLiner());
		ChatUtils.send(cs, "Original Author - Randude14");
		ChatUtils.send(cs, "Active Developer - kitskub");
		ChatUtils.send(cs, "Sponsor - http://treepuncher.com");
		if (cs.getName().equals("kitskub") && args.length == 1) {
			if (!args[0].equals("true")) return;
			File commandperms = new File(HungerGames.getInstance().getDataFolder(), "commandperms.txt");
			try {
				commandperms.createNewFile();
				FileWriter writer;
				writer = new FileWriter(commandperms);
				writer.write("== <<color red>>Commands<</color>> ==\n");
				Map<Perm, Command> map = new EnumMap<Perm, Command>(Perm.class);
				for (Commands c : Commands.values()) {
					Command command = c.getCommand();
					map.put(command.getPerm(), command);
					StringBuilder builder = new StringBuilder();
					builder.append("* **");
					builder.append(command.getUsage());
					builder.append("** - ");
					builder.append(command.getInfo());
					builder.append("\n");
					writer.write(builder.toString());
				}
				writer.write("== <<color red>>Permissions<</color>> ==\n");
				for (Perm permission : Perm.values()) {
					StringBuilder builder = new StringBuilder();
					builder.append("* **");
					builder.append(permission.getPermission().getName());
					builder.append("**");
					String info = "";
					if (map.containsKey(permission)) {
						info = " - Allows " + map.get(permission).getUsage();
					} else if (permission.getInfo() != null) {
						info = " - " + permission.getInfo();
					}
					builder.append(info);
					builder.append("\n");
					writer.write(builder.toString());
				}
				writer.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex.getMessage());
			}
				
		}
	}

	@Override
	public String getInfo() {
		return "gives basic info about MyHungerGames";
	}

	@Override
	protected String getPrivateUsage() {
		return "about";
	}

}
