package com.randude14.hungergames.reset;

import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.games.HungerGame;

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
			    resetter = new LogBlockResetter();
			    break;
		    default:
			    resetter = new InternalResetter();
	    }	
	    resetter.init();
    }
    
    public static void gameStarting(HungerGame game) {
	    resetter.beginGame(game);
    }
    
    private static boolean resetBlockChanges(HungerGame game) {
	    if (!Config.RESET_CHANGES.getBoolean(game.getSetup())) return true;
	    return resetter.resetChanges(game);
    }
    
    public static boolean resetChanges(HungerGame game) {
	return resetBlockChanges(game);
    }
}
