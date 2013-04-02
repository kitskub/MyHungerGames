package me.kitskub.hungergames.reset;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.games.HungerGame;

public class ResetHandler {
    
    public enum Resetters {
	    INTERNAL,
	    LOGBLOCK,
	    HAWKEYE,
	    MVA;
    }

    private static Resetter resetter;
    
    
    public static void setRessetter(Resetters r) {
	    switch (r) {
		    case HAWKEYE:
			    resetter = new HawkEyeResetter();
			    break;
		    case LOGBLOCK:
			    resetter = new LogBlockResetter();
			    break;
		    case MVA:
			    resetter = new MultiverseAdventureResetter();
			    break;
		    default:
			    resetter = new InternalResetter();
	    }	
	    resetter.init();
    }
    
    public static void gameStarting(HungerGame game) {
	    resetter.beginGame(game);
    }

    public static boolean resetChanges(HungerGame game) {
	    if (!Config.RESET_CHANGES.getBoolean(game.getSetup())) return true;
	    return resetter.resetChanges(game);
    }
}
