package com.randude14.lotteryplus;

import org.bukkit.configuration.file.FileConfiguration;

import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.lottery.LotterySignFormatter;

public class Config {
	private static final Plugin plugin = Plugin.getInstance();

	public static boolean isPermsEnabled() {
		return plugin.getConfig().getBoolean("properties.permissions");
	}

	public static boolean shouldDefaultToOp() {
		return plugin.getConfig().getBoolean("properties.permissions-op");
	}
	
	public static boolean shouldBroadcastBuy() {
		return plugin.getConfig().getBoolean("properties.should-broadcast-buy");
	}

	public static int getDefaultMaxPlayers() {
		return 10;
	}

	public static int getDefaultMinPlayers() {
		return 2;
	}

	public static double getDefaultPot() {
		return 100.00;
	}

	public static double getDefaultTicketCost() {
		return 1.00;
	}

	public static int getDefaultMaxTickets() {
		return 10;
	}

	public static long getDefaultTime() {
		return 3;
	}

	public static long getReminderMessageTime() {
		return plugin.getConfig().getLong("properties.reminder-message-time");
	}

	public static String getReminderMessage() {
		return plugin.getConfig().getString("properties.reminder-message");
	}
	
	public static boolean shouldReminderMessageEnable() {
		return plugin.getConfig().getBoolean("properties.reminder-message-enable");
	}

	public static long getUpdateDelay() {
		return plugin.getConfig().getLong("properties.update-delay");
	}

	public static long getTimeAfterDraws() {
		return plugin.getConfig().getLong("properties.time-after-draws");
	}

	public static String getBuyMessage() {
		return plugin.getConfig().getString("properties.buy-message");
	}
	
	public static String getSignMessage() {
		return plugin.getConfig().getString("properties.sign-message");
	}
	
	public static String[] getMainLotteries() {
		return plugin.getConfig().getString("properties.main-lotteries").split("[, ]+");
	}
	
	public static LotterySignFormatter getLotterySignFormatter(Lottery lottery) {
		String[] normalArgs = new String[3];
		String[] drawArgs = new String[3];
		String[] endArgs = new String[3];
		FileConfiguration config = plugin.getConfig();
		for(int cntr = 0;cntr < 3;cntr++) {
			normalArgs[cntr] = config.getString("signs.normal.line" + (cntr+1));
			drawArgs[cntr] = config.getString("signs.draw.line" + (cntr+1));
			endArgs[cntr] = config.getString("signs.end.line" + (cntr+1));
		}
		return new LotterySignFormatter(lottery, normalArgs, drawArgs, endArgs);
	}

}
