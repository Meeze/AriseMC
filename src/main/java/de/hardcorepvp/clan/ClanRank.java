package de.hardcorepvp.clan;

public enum ClanRank {
	MEMBER("§7Member"),
	TRUSTED("§3Trusted"),
	OWNER("§cOwner");

	private String prefix;

	private ClanRank(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static ClanRank getByName(String rank) {
		for (ClanRank current : values()) {
			if (rank.toUpperCase().equalsIgnoreCase(current.name())) {
				return current;
			}
		}
		return null;
	}
}
