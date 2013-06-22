package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.games.User;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.stats.PlayerStat.Team;
import me.kitskub.hungergames.utils.ChatUtils;
import org.bukkit.entity.Player;

public class TeamCommand extends PlayerCommand {

	public TeamCommand() {
		super(Perm.USER_TEAM, "team", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String label, String[] args) {
		User get = User.get(player);
		game = get.getGameInEntry().getGame();
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
		
		PlayerStat stat = get.getStat(game);
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
			stat.setTeam(Team.get(team));
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
