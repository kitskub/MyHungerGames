package me.kitskub.hungergames.listeners;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.Game.GameState;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.stats.PlayerStat.PlayerState;
import me.kitskub.hungergames.stats.PlayerStat.Team;
import me.kitskub.hungergames.utils.ChatUtils;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		// Games
		Game hurtGame = HungerGames.getInstance().getGameManager().getRawSession(player);
		if (hurtGame != null) {
			if (Config.FORCE_DAMAGE.getBoolean(hurtGame.getSetup())) {
				event.setCancelled(false);
			}
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;
				double period = Config.GRACE_PERIOD.getDouble(hurtGame.getSetup());
				long startTime = hurtGame.getInitialStartTime();
				Entity damager = newEvent.getDamager() instanceof Projectile ? ((Projectile) newEvent.getDamager()).getShooter() : newEvent.getDamager();
				if (((System.currentTimeMillis() - startTime) / 1000) < period) {
					event.setCancelled(true);
					if (damager instanceof Player) {
						ChatUtils.error((Player) damager, "You can't hurt that player during the grace-period!");
					}
				}
				if (damager instanceof Player && hurtGame.contains((Player) damager)) {
					if (!Config.TEAMS_ALLOW_FRIENDLY_DAMAGE.getBoolean(hurtGame.getSetup())) {
						Team hurtTeam = hurtGame.getPlayerStat(player).getTeam();
						if (hurtGame.getPlayerStat(player).getState() == PlayerState.WAITING) {
							event.setCancelled(true);
						} else if (hurtTeam != null){
							Team hurterTeam = hurtGame.getPlayerStat((Player) damager).getTeam();
							if (hurterTeam != null) {
								if (hurtTeam.getName().equals(hurterTeam.getName())) {
									event.setCancelled(true);
									ChatUtils.error((Player) damager, "You can't hurt a player on your team!");
								}
							}
						}
					}
				}
			}
		}

		// Spectators
		if (event.isCancelled()) return;
		if (HungerGames.getInstance().getGameManager().getSpectating(player) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player)) return;
		Player player = (Player) event.getTarget();
		// Games
		Game game = HungerGames.getInstance().getGameManager().getRawSession(player);
		if (game != null) {
			if (game.getPlayerStat(player).getState() == PlayerState.WAITING) {
				if (!Config.STOP_TARGETTING.getBoolean(game.getSetup())) return;
				PlayerStat stat = game.getPlayerStat(player);
				if (stat != null && stat.getState().equals(PlayerState.WAITING)) event.setCancelled(true); 
			}
		}
	}
}
