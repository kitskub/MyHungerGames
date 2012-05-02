package com.randude14.lotteryplus.lottery;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import com.randude14.lotteryplus.util.FormatOptions;
import com.randude14.lotteryplus.util.SignFormatter;

public class LotterySignFormatter implements SignFormatter, FormatOptions {
	private final String[] normalArgs, drawingArgs, endArgs;
	private final Lottery lottery;

	public LotterySignFormatter(Lottery lottery, String[] normalArgs,
			String[] drawingArgs, String[] endArgs) {

		if (normalArgs.length != 3 || drawingArgs.length != 3
				|| endArgs.length != 3) {
			throw new IllegalArgumentException("All arguments must be length 3");
		}

		this.normalArgs = normalArgs;
		this.drawingArgs = drawingArgs;
		this.endArgs = endArgs;
		this.lottery = lottery;
	}

	public void format(Sign sign) {
		sign.setLine(0, ChatColor.GREEN + "[Lottery+]");

		if (lottery.isRunning()) {
			sign.setLine(1, format(normalArgs[0]));
			sign.setLine(2, format(normalArgs[1]));
			sign.setLine(3, format(normalArgs[2]));
		}

		else if (lottery.isDrawing()) {
			sign.setLine(1, format(drawingArgs[0]));
			sign.setLine(2, format(drawingArgs[1]));
			sign.setLine(3, format(drawingArgs[2]));
		}

		else {
			sign.setLine(1, format(endArgs[0]));
			sign.setLine(2, format(endArgs[1]));
			sign.setLine(3, format(endArgs[2]));
		}

	}

	private String format(String format) {
		String message = lottery.getPlugin().replaceColors(format)
				.replace(FORMAT_REWARD, lottery.formatReward())
				.replace(FORMAT_TIME, lottery.formatTimer())
				.replace(FORMAT_NAME, lottery.getName())
				.replace(FORMAT_WINNER, lottery.formatWinner())
				.replace(FORMAT_TICKET_COST, lottery.formatTicketCost())
		        .replace(FORMAT_TICKET_TAX, lottery.formatTicketTax())
		        .replace(FORMAT_POT_TAX, lottery.formatPotTax());
		return message;
	}

}
