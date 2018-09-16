package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.model.Callback;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PunishmentManager {

	public PunishmentManager() {
		this.setupTables();
	}

	private void setupTables() {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_mutes` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `mute_reason` CHAR(100) NOT NULL,"
					+ " `muted_by` CHAR(16) NOT NULL,"
					+ " `mute_time` BIGINT NOT NULL,"
					+ " `mute_timestamp` BIGINT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " INDEX `muted_by_INDEX` (`muted_by`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement.executeUpdate();
			}
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_mutes_history` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `mute_reason` CHAR(100) NOT NULL,"
					+ " `muted_by` CHAR(16) NOT NULL,"
					+ " `mute_time` BIGINT NOT NULL,"
					+ " `mute_timestamp` BIGINT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " INDEX `muted_by_INDEX` (`muted_by`),"
					+ " INDEX `uniqueId_UNIQUE` (`uniqueId`))")) {
				statement.executeUpdate();
			}
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_bans` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `ban_reason` CHAR(100) NOT NULL,"
					+ " `banned_by` CHAR(16) NOT NULL,"
					+ " `ban_time` BIGINT NOT NULL,"
					+ " `ban_timestamp` BIGINT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " INDEX `banned_by_INDEX` (`banned_by`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement.executeUpdate();
			}
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_bans_history` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `ban_reason` CHAR(100) NOT NULL,"
					+ " `banned_by` CHAR(16) NOT NULL,"
					+ " `ban_time` BIGINT NOT NULL,"
					+ " `ban_timestamp` BIGINT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " INDEX `banned_by_INDEX` (`banned_by`),"
					+ " INDEX `uniqueId_UNIQUE` (`uniqueId`))")) {
				statement.executeUpdate();
			}
			Main.getDatabaseManager().getConnection().commit();
			Main.getDatabaseManager().getConnection().setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//Tabelle

	public void setBanned(BanData banData, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			System.out.println("lul 1");
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_bans` (`uniqueId`, `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
					statement.setString(1, banData.getUniqueId().toString());
					statement.setString(2, banData.getBanReason());
					statement.setString(3, banData.getBannedBy());
					statement.setLong(4, banData.getBanTime());
					statement.setLong(5, banData.getBanTimestamp());
					statement.executeUpdate();
				}
				callback.onResult(true);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void getBanData(UUID uniqueId, Callback<BanData> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp` FROM `hc_bans` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						if (!resultSet.next()) {
							callback.onResult(null);
							return;
						}
						if (resultSet.next()) {
							String reason = resultSet.getString("ban_reason");
							String bannedBy = resultSet.getString("banned_by");
							long banTime = resultSet.getLong("ban_time");
							long banTimestamp = resultSet.getLong("ban_timestamp");
							callback.onResult(new BanData(uniqueId, reason, bannedBy, banTime, banTimestamp));
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void getBanHistory(UUID uniqueId, Callback<List<BanData>> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			List<BanData> banHistory = new CopyOnWriteArrayList<>();
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp` FROM `hc_bans_history` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							String reason = resultSet.getString("ban_reason");
							String bannedBy = resultSet.getString("banned_by");
							long banTime = resultSet.getLong("ban_time");
							long banTimestamp = resultSet.getLong("ban_timestamp");
							banHistory.add(new BanData(uniqueId, reason, bannedBy, banTime, banTimestamp));
						}
					}
				}
				callback.onResult(banHistory);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void deleteBanData(UUID uniqueId, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_bans` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					statement.executeUpdate();
					callback.onResult(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void unban(UUID uniqueId, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				BanData banData = this.getBanData(uniqueId);
				if (banData != null) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_bans` WHERE `uniqueId` = ?")) {
						statement.setString(1, uniqueId.toString());
						statement.executeUpdate();
					}
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_bans_history` (`uniqueId`, `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
						statement.setString(1, banData.getUniqueId().toString());
						statement.setString(2, banData.getBanReason());
						statement.setString(3, banData.getBannedBy());
						statement.setLong(4, banData.getBanTime());
						statement.setLong(5, banData.getBanTimestamp());
						statement.executeUpdate();
					}
				}
				callback.onResult(true);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public boolean setBanned(BanData banData) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_bans` (`uniqueId`, `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
				statement.setString(1, banData.getUniqueId().toString());
				statement.setString(2, banData.getBanReason());
				statement.setString(3, banData.getBannedBy());
				statement.setLong(4, banData.getBanTime());
				statement.setLong(5, banData.getBanTimestamp());
				statement.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public BanData getBanData(UUID uniqueId) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp` FROM `hc_bans` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						String reason = resultSet.getString("ban_reason");
						String bannedBy = resultSet.getString("banned_by");
						long banTime = resultSet.getLong("ban_time");
						long banTimestamp = resultSet.getLong("ban_timestamp");
						return new BanData(uniqueId, reason, bannedBy, banTime, banTimestamp);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<BanData> getBanHistory(UUID uniqueId) {
		List<BanData> banHistory = new CopyOnWriteArrayList<>();
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp` FROM `hc_bans_history` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						String reason = resultSet.getString("ban_reason");
						String bannedBy = resultSet.getString("banned_by");
						long banTime = resultSet.getLong("ban_time");
						long banTimestamp = resultSet.getLong("ban_timestamp");
						banHistory.add(new BanData(uniqueId, reason, bannedBy, banTime, banTimestamp));
					}
				}
			}
			return banHistory;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return banHistory;
	}

	public boolean deleteBanData(UUID uniqueId) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_bans` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				statement.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unban(UUID uniqueId) {
		try {
			BanData banData = this.getBanData(uniqueId);
			if (banData != null) {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_bans` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					statement.executeUpdate();
				}
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_bans_history` (`uniqueId`, `ban_reason`, `banned_by`, `ban_time`, `ban_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
					statement.setString(1, banData.getUniqueId().toString());
					statement.setString(2, banData.getBanReason());
					statement.setString(3, banData.getBannedBy());
					statement.setLong(4, banData.getBanTime());
					statement.setLong(5, banData.getBanTimestamp());
					statement.executeUpdate();
				}
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setMuted(MuteData muteData, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_mutes` (`uniqueId`, `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
					statement.setString(1, muteData.getUniqueId().toString());
					statement.setString(2, muteData.getMuteReason());
					statement.setString(3, muteData.getMutedBy());
					statement.setLong(4, muteData.getMuteTime());
					statement.setLong(5, muteData.getMuteTimestamp());
					statement.executeUpdate();
				}
				callback.onResult(true);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void getMuteData(UUID uniqueId, Callback<MuteData> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp` FROM `hc_mutes` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						if (!resultSet.next()) {
							callback.onResult(null);
							return;
						}
						if (resultSet.next()) {
							String reason = resultSet.getString("mute_reason");
							String mutedBy = resultSet.getString("muted_by");
							long muteTime = resultSet.getLong("mute_time");
							long muteTimestamp = resultSet.getLong("mute_timestamp");
							callback.onResult(new MuteData(uniqueId, reason, mutedBy, muteTime, muteTimestamp));
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void getMuteHistory(UUID uniqueId, Callback<List<MuteData>> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			List<MuteData> muteHistory = new CopyOnWriteArrayList<>();
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp` FROM `hc_mutes_history` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							String reason = resultSet.getString("mute_reason");
							String mutedBy = resultSet.getString("muted_by");
							long muteTime = resultSet.getLong("mute_time");
							long muteTimestamp = resultSet.getLong("mute_timestamp");
							muteHistory.add(new MuteData(uniqueId, reason, mutedBy, muteTime, muteTimestamp));
						}
					}
				}
				callback.onResult(muteHistory);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void deleteMuteData(UUID uniqueId, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_mutes` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					statement.executeUpdate();
					callback.onResult(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void unmute(UUID uniqueId, Callback<Boolean> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				MuteData muteData = this.getMuteData(uniqueId);
				if (muteData != null) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_mutes` WHERE `uniqueId` = ?")) {
						statement.setString(1, uniqueId.toString());
						statement.executeUpdate();
					}
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_mutes_history` (`uniqueId`, `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
						statement.setString(1, muteData.getUniqueId().toString());
						statement.setString(2, muteData.getMuteReason());
						statement.setString(3, muteData.getMutedBy());
						statement.setLong(4, muteData.getMuteTime());
						statement.setLong(5, muteData.getMuteTimestamp());
						statement.executeUpdate();
					}
				}
				callback.onResult(true);
			} catch (SQLException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public boolean setMuted(MuteData muteData) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_mutes` (`uniqueId`, `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
				statement.setString(1, muteData.getUniqueId().toString());
				statement.setString(2, muteData.getMuteReason());
				statement.setString(3, muteData.getMutedBy());
				statement.setLong(4, muteData.getMuteTime());
				statement.setLong(5, muteData.getMuteTimestamp());
				statement.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public MuteData getMuteData(UUID uniqueId) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp` FROM `hc_mutes` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						String reason = resultSet.getString("mute_reason");
						String mutedBy = resultSet.getString("muted_by");
						long muteTime = resultSet.getLong("mute_time");
						long muteTimestamp = resultSet.getLong("mute_timestamp");
						return new MuteData(uniqueId, reason, mutedBy, muteTime, muteTimestamp);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<MuteData> getMuteHistory(UUID uniqueId) {
		List<MuteData> muteHistory = new CopyOnWriteArrayList<>();
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp` FROM `hc_mutes_history` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						String reason = resultSet.getString("mute_reason");
						String mutedBy = resultSet.getString("muted_by");
						long muteTime = resultSet.getLong("mute_time");
						long muteTimestamp = resultSet.getLong("mute_timestamp");
						muteHistory.add(new MuteData(uniqueId, reason, mutedBy, muteTime, muteTimestamp));
					}
				}
			}
			return muteHistory;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return muteHistory;
	}

	public boolean deleteMuteData(UUID uniqueId) {
		try {
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_mutes` WHERE `uniqueId` = ?")) {
				statement.setString(1, uniqueId.toString());
				statement.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unmute(UUID uniqueId) {
		try {
			MuteData muteData = this.getMuteData(uniqueId);
			if (muteData != null) {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_mutes` WHERE `uniqueId` = ?")) {
					statement.setString(1, uniqueId.toString());
					statement.executeUpdate();
				}
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_mutes_history` (`uniqueId`, `mute_reason`, `muted_by`, `mute_time`, `mute_timestamp`) VALUES(?, ?, ?, ?, ?)")) {
					statement.setString(1, muteData.getUniqueId().toString());
					statement.setString(2, muteData.getMuteReason());
					statement.setString(3, muteData.getMutedBy());
					statement.setLong(4, muteData.getMuteTime());
					statement.setLong(5, muteData.getMuteTimestamp());
					statement.executeUpdate();
				}
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static class MuteData {

		private UUID uniqueId;
		private String muteReason;
		private String mutedBy;
		private long muteTime;
		private long muteTimestamp;

		public MuteData(UUID uniqueId, String muteReason, String mutedBy, long muteTime, long muteTimestamp) {
			this.uniqueId = uniqueId;
			this.muteReason = muteReason;
			this.mutedBy = mutedBy;
			this.muteTime = muteTime;
			this.muteTimestamp = muteTimestamp;
		}

		public UUID getUniqueId() {
			return uniqueId;
		}

		public String getMuteReason() {
			return muteReason;
		}

		public String getMutedBy() {
			return mutedBy;
		}

		public long getMuteTime() {
			return muteTime;
		}

		public long getMuteTimestamp() {
			return muteTimestamp;
		}

		public boolean isMuted() {
			return this.muteTime == -1L;
		}

		public boolean isTemporarilyMuted() {
			return this.muteTime > 0L;
		}
	}

	public static class BanData {

		private UUID uniqueId;
		private String banReason;
		private String bannedBy;
		private long banTime;
		private long banTimestamp;

		public BanData(UUID uniqueId, String banReason, String bannedBy, long banTime, long banTimestamp) {
			this.uniqueId = uniqueId;
			this.banReason = banReason;
			this.bannedBy = bannedBy;
			this.banTime = banTime;
			this.banTimestamp = banTimestamp;
		}

		public UUID getUniqueId() {
			return uniqueId;
		}

		public String getBanReason() {
			return banReason;
		}

		public String getBannedBy() {
			return bannedBy;
		}

		public long getBanTime() {
			return banTime;
		}

		public long getBanTimestamp() {
			return banTimestamp;
		}

		public boolean isTemporarilyBanned() {
			return this.banTime > 0L;
		}
	}

	public static class MuteHistory {

		private UUID uniqueId;
		private List<MuteData> muteArchive;

		public MuteHistory(UUID uniqueId, List<MuteData> muteArchive) {
			this.uniqueId = uniqueId;
			this.muteArchive = muteArchive;
		}

		public UUID getUniqueId() {
			return uniqueId;
		}

		public List<MuteData> getMuteArchive() {
			return muteArchive;
		}
	}

	public static class BanHistory {

		private UUID uniqueId;
		private List<BanData> banArchive;

		public BanHistory(UUID uniqueId, List<BanData> banArchive) {
			this.uniqueId = uniqueId;
			this.banArchive = banArchive;
		}

		public UUID getUniqueId() {
			return uniqueId;
		}

		public List<BanData> getBanArchive() {
			return banArchive;
		}
	}
}
