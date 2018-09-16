package de.hardcorepvp.model;

public enum Ranking {
	KILLS("Kills"),
	DEATHS("Deaths"),
	MONEY("Money"),
	CLAN("Clan");

	private String name;

	private Ranking(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
