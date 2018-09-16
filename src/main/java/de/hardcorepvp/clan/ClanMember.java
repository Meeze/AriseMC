package de.hardcorepvp.clan;

import de.hardcorepvp.Main;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ClanMember {

	private UUID uniqueId;
	private String name;
	private Clan clan;
	private ClanRank rank;

	public ClanMember(UUID uniqueId, String name, Clan clan, ClanRank rank) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.clan = clan;
		this.rank = rank;
		this.writeToDatabase();
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public String getName() {
		return name;
	}

	public Clan getClan() {
		return clan;
	}

	public ClanRank getRank() {
		return rank;
	}

	public void setName(String name) {
		this.name = name;
		this.writeToDatabase();
	}

	public void setClan(Clan clan) {
		this.clan = clan;
		this.writeToDatabase();
	}

	public void setRank(ClanRank rank) {
		this.rank = rank;
		this.writeToDatabase();
	}

	public void delete() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_clans_members` WHERE `uniqueId` = ?")) {
					statement.setString(1, this.uniqueId.toString());
					statement.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	private void writeToDatabase() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_clans_members` (`uniqueId`, `clan`, `rank`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `clan` = VALUES(`clan`), `rank` = VALUES(`rank`)")) {
					statement.setString(1, this.uniqueId.toString());
					statement.setString(2, this.clan.getName());
					statement.setString(3, this.rank.name());
					statement.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
