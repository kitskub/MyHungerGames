package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.stats.SQLStat;
import com.randude14.hungergames.stats.StatHandler;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class SearchCommand extends Command {
	private final List<FutureTask<SQLStat>> searches = new ArrayList<FutureTask<SQLStat>>();

	public SearchCommand() {
		super(Perm.USER_SEARCH, "search", USER_COMMAND);
	}

	@Override
	public void handle(final CommandSender cs, String cmd, final String[] args) {
		if (args.length < 1) {
			ChatUtils.error(cs, "Must have a player to search for!");
			return;
		}
		FutureTask<SQLStat> f = new FutureTask<SQLStat>(new Callable<SQLStat>() {
			public SQLStat call() throws Exception {
				return StatHandler.getStat(args[0]);
			}
		});
		synchronized(searches) {
			searches.add(f);
			Bukkit.getScheduler().runTaskAsynchronously(HungerGames.getInstance(), f);
			Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), new BukkitRunnable() {
				public void run() {
					synchronized(searches) {
						if (searches.isEmpty()) {
							cancel();
							return;
						}
						Iterator<FutureTask<SQLStat>> iterator = searches.iterator();
						while (iterator.hasNext()) {
							FutureTask<SQLStat> next = iterator.next();
							if (next.isCancelled()) {
								cancel();
								return;
							}
							if (!next.isDone()) continue;
							iterator.remove();
							SQLStat stat;
							try {
								stat = next.get();
							} catch (Exception ex) {
								Logging.debug("Exception in search runnable");
								cancel();
								return;
							}
							if (stat == null) {
								ChatUtils.send(cs, "Could not find stat for: %s", args[0]);
							}
							ChatUtils.send(cs, ChatUtils.getHeadLiner());
							ChatUtils.send(cs, "Player stat for: %s", args[0]);
							ChatUtils.send(cs, "%s has a global rank of %s",args[0], stat.rank);
							ChatUtils.send(cs, "%s has played %s games for a total of %s", args[0], stat.totalGames, stat.totalTime);
							ChatUtils.send(cs, "%s has had %s wins, %s deaths, and %s kills", args[0], stat.wins, stat.deaths, stat.kills);
						}
					}
				}
			}, 0, 5);
			
		}
	}

	@Override
	public String getInfo() {
		return "searches for a player's stat and prints out the info";
	}

	@Override
	protected String getPrivateUsage() {
		return "search <player>";
	}

}
