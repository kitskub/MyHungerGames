package com.randude14.hungergames.stats;

import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SQLStat {
	public Date lastLogin;
	public Integer totalGames;
	public Time totalTime;
	public Integer wins;
	public Integer kills;
	public Integer deaths;
	public List<SQLGameStat> games = new ArrayList<SQLGameStat>();


		
	public class SQLGameStat {
		public Date startTime;
		public Time totalDuration;
		public String winner;
		public Integer totalPlayers;
		public List<String> players = new ArrayList<String>();
		public List<String> sponsors = new ArrayList<String>();
	}
}
