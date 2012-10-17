package com.randude14.hungergames.reset;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseAdventure.api.AdventureWorldsManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;

import org.bukkit.World;


/**
 *
 *
 */
public class MultiverseAdventureResetter extends Resetter{
	
	@Override
	public void init() {
	}

	@Override
	public void beginGame(HungerGame game) {
	}
    
	@Override
	public boolean resetChanges(HungerGame game) {
		boolean success = true;
		for (World world : game.getWorlds()) {
			if (!world.getPlayers().isEmpty()) {
				Logging.warning("Tried to reset world " + world.getName() + " but it was not empty.");
				success = false;
				continue;
			}
			AdventureWorldsManager manager = MultiverseAdventure.getInstance().getAdventureWorldsManager();
			manager.createWorld(world.getName());
			AdventureWorld aWorld = manager.getMVAInfo(world.getName());
			aWorld.resetNow();
		}
		return success;
	}
}
