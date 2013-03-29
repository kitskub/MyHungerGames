package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.stats.PlayerStat.Team;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.entity.Player;

public class TeamCommand extends PlayerCommand {

	public TeamCommand() {
		super(Perm.USER_TEAM, "team", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
		game = HungerGames.getInstance().getGameManager().getRawSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are not in a game!");
			return;
		}
		if (!Defaults.Config.TEAMS_ALLOW_TEAMS.getBoolean(game.getSetup())) {
			ChatUtils.error(player, "Teams are not enabled for this game!");
			return;
		}
		String team = null;
		if (args.length >= 1) {
			team = args[0];
		}
		PlayerStat stat = game.getPlayerStat(player);
		boolean require = false;
		if (stat.getTeam() != null) {
			stat.setTeam(null);
		} else {
			require = true;
		}
		if (team == null) {
			if (require) {
				ChatUtils.error(player, "Must specify a team!");
			}
		} else {
			game.getPlayerStat(player).setTeam(Team.get(team));
		}
	}

	@Override
	public String getInfo() {
		return "joins the team specified (may create a new one if there is nobody in it) or leaves current team";
	}

	@Override
	protected String getPrivateUsage() {
		return "team <team>";
	}
}
