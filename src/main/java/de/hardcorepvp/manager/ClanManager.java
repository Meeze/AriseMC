package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.clan.ClanMember;
import de.hardcorepvp.clan.ClanRank;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClanManager {

	private List<Clan> clans;
	private Map<UUID, List<Clan>> clanRequests;

	public ClanManager() {
		//Wenn clan gelöscht, dann requests vom clan löschen
		this.clans = new CopyOnWriteArrayList<>();
		this.clanRequests = new ConcurrentHashMap<>();
		this.setupTables();
		this.loadClans();
	}

	private void loadClans() {
		Bukkit.getConsoleSender().sendMessage("§aLade Clans...");
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `clan`, `tag`, `base`, `kills` FROM `hc_clans`")) {
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						String name = resultSet.getString("clan");
						String tag = resultSet.getString("tag");
						Location base = Utils.deserializeLocation(resultSet.getString("base"));
						int kills = resultSet.getInt("kills");

						Clan clan = new Clan(name, tag, base, kills);

						try (PreparedStatement statement1 = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `uniqueId`, `rank` FROM `hc_clans_members` WHERE clan = ?")) {
							statement1.setString(1, clan.getName());
							try (ResultSet resultSet1 = statement1.executeQuery()) {
								while (resultSet1.next()) {
									UUID uniqueId = UUID.fromString(resultSet1.getString("uniqueId"));
									ClanRank rank = ClanRank.valueOf(resultSet1.getString("rank"));
									clan.addMember(new ClanMember(uniqueId, Bukkit.getOfflinePlayer(uniqueId).getName(), clan, rank));
								}
								this.clans.add(clan);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage("§aDie Clans wurden geladen!");
	}

	public List<Clan> getClans() {
		return clans;
	}

	public Clan getClan(String name) {
		for (Clan clan : this.clans) {
			if (clan.getName().equalsIgnoreCase(name) || clan.getTag().equalsIgnoreCase(name)) {
				return clan;
			}
		}
		return null;
	}

	public Clan getClan(UUID uniqueId) {
		for (Clan clan : this.clans) {
			for (ClanMember member : clan.getMembers()) {
				if (member.getUniqueId().equals(uniqueId)) {
					return clan;
				}
			}
		}
		return null;
	}

	public ClanMember getMember(UUID uniqueId, Clan clan) {
		for (ClanMember member : clan.getMembers()) {
			if (member.getUniqueId().equals(uniqueId)) {
				return member;
			}
		}
		return null;
	}

	public ClanMember getMember(String name, Clan clan) {
		for (ClanMember member : clan.getMembers()) {
			if (member.getName().equals(name)) {
				return member;
			}
		}
		return null;
	}

	public ClanMember getMember(UUID uniqueId) {
		for (Clan clan : this.clans) {
			for (ClanMember member : clan.getMembers()) {
				if (member.getUniqueId().equals(uniqueId)) {
					return member;
				}
			}
		}
		return null;
	}

	public ClanMember getMember(String name) {
		for (Clan clan : this.clans) {
			for (ClanMember member : clan.getMembers()) {
				if (member.getName().equals(name)) {
					return member;
				}
			}
		}
		return null;
	}

	public boolean hasClan(UUID uniqueId) {
		return this.getClan(uniqueId) != null;
	}

	public void createClan(String name, String tag, UUID ownerId, String ownerName, Location base) {
		Clan clan = new Clan(name, tag, base, 0);
		clan.addMember(new ClanMember(ownerId, ownerName, clan, ClanRank.OWNER));
		this.clans.add(clan);
	}

	public void joinClan(UUID uniqueId, String name, Clan clan, ClanRank rank) {
		if (!this.hasClan(uniqueId)) {
			ClanMember member = new ClanMember(uniqueId, name, clan, rank);
			clan.getMembers().add(member);
		}
	}

	public void leaveClan(UUID uniqueId) {
		ClanMember member = this.getMember(uniqueId);
		if (member != null) {
			member.delete();
			member.getClan().removeMember(member);
		}
	}

	public void leaveClan(String name) {
		ClanMember member = this.getMember(name);
		if (member != null) {
			member.delete();
			member.getClan().removeMember(member);
		}
	}

	public void deleteClan(String name) {
		for (Clan clan : this.clans) {
			if (clan.getName().equalsIgnoreCase(name)) {
				for (ClanMember member : clan.getMembers()) {
					this.leaveClan(member.getUniqueId());
				}
				clan.delete();
				this.clans.remove(clan);
			}
		}
	}

	public boolean hasClanRequest(UUID uniqueId, Clan clan) {
		if (this.clanRequests.containsKey(uniqueId)) {
			List<Clan> requests = this.clanRequests.get(uniqueId);
			return requests.contains(clan);
		}
		return false;
	}

	public void addClanRequest(UUID uniqueId, Clan clan) {
		if (this.clanRequests.containsKey(uniqueId)) {
			List<Clan> requests = this.clanRequests.get(uniqueId);
			requests.add(clan);
			this.clanRequests.put(uniqueId, requests);
		}
		this.clanRequests.put(uniqueId, new CopyOnWriteArrayList<>(Arrays.asList(clan)));
	}

	public void removeClanRequests(UUID uniqueId) {
		this.clanRequests.remove(uniqueId);
	}

	public void removeClanRequest(UUID uniqueId, Clan clan) {
		if (this.clanRequests.containsKey(uniqueId)) {
			List<Clan> requests = this.clanRequests.get(uniqueId);
			requests.remove(clan);
			this.clanRequests.put(uniqueId, requests);
		}
	}

	private void setupTables() {
		try {
			Main.getDatabaseManager().getConnection().setAutoCommit(false);
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_clans` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `clan` CHAR(16) NOT NULL,"
					+ " `tag` CHAR(5) NOT NULL,"
					+ " `base` TEXT(250) NOT NULL,"
					+ " `kills` INT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " UNIQUE INDEX `clan_UNIQUE` (`clan` ASC))")) {
				statement.executeUpdate();
			}
			try (PreparedStatement statement1 = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_clans_members` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `clan` VARCHAR(16) NOT NULL,"
					+ " `rank` VARCHAR(50) NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement1.executeUpdate();
			}
			Main.getDatabaseManager().getConnection().commit();
			Main.getDatabaseManager().getConnection().setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
