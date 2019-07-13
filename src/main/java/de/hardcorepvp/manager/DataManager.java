package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserData;
import de.hardcorepvp.model.Callback;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.com.google.common.cache.CacheLoader;
import net.minecraft.util.com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DataManager {

	private final LoadingCache<UUID, UserData> userDataCache = CacheBuilder.newBuilder().concurrencyLevel(4).build(new CacheLoader<UUID, UserData>() {
		@Override
		public UserData load(UUID uniqueId) throws Exception {
			long timestamp = System.currentTimeMillis();
			try (PreparedStatement selectStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("SELECT `last_name`, `last_ip`, `online_time`, `first_login`, `last_login`, `real_name`, `discord_name`, `teamspeak_name`, " +
                    "`skype_name` FROM `hc_user_data` WHERE `uniqueId` = ?")) {
				selectStatement.setString(1, uniqueId.toString());
				try (ResultSet resultSet = selectStatement.executeQuery()) {
					if (resultSet.next()) {
						String lastName = resultSet.getString("last_name");
						String lastIP = resultSet.getString("last_ip");
						String realName = resultSet.getString("real_name");
						String discordName = resultSet.getString("discord_name");
						String teamspeakName = resultSet.getString("teamspeak_name");
						String skypeName = resultSet.getString("skype_name");
						long onlineTime = resultSet.getLong("online_time");
						long firstLogin = resultSet.getLong("first_login");
						long lastLogin = resultSet.getLong("last_login");
						return new UserData(uniqueId, lastName, lastIP, realName, discordName, teamspeakName, skypeName, onlineTime, firstLogin, lastLogin);
					}
					try (PreparedStatement insertStatement = Main.getDatabaseManager()
						.getConnection()
						.prepareStatement("INSERT IGNORE INTO `hc_user_data` (`uniqueId`, `last_name`, `last_ip`, `online_time`, `first_login`, `last_login`, " +
                            "`real_name`, `discord_name`, `teamspeak_name`, `skype_name`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
						insertStatement.setString(1, uniqueId.toString());
						insertStatement.setString(2, "----");
						insertStatement.setString(3, "0.0.0.0");
						insertStatement.setLong(4, 0L);
						insertStatement.setLong(5, timestamp);
						insertStatement.setLong(6, timestamp);
						insertStatement.setString(7, "----");
						insertStatement.setString(8, "----");
						insertStatement.setString(9, "----");
						insertStatement.setString(10, "----");
						insertStatement.executeUpdate();
					}
				}
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			return new UserData(uniqueId, "----", "0.0.0.0", "----", "----", "----", "----", 0L, timestamp, timestamp);
		}
	});

	public void getUserData(UUID uniqueId, Callback<UserData> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				callback.onResult(this.userDataCache.get(uniqueId));
			} catch (ExecutionException exception) {
				callback.onFailure(exception.getCause());
			}
		});
	}

	public void update(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_currency` SET `last_name` = ?, `last_ip` = ?, `online_time` = ?, `first_login` = ?, `last_login` = ?, " +
                    "`real_name` = ?, `discord_name` = ?, `teamspeak_name` = ?, `skype_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getLastName());
				updateStatement.setString(2, data.getLastIP());
				updateStatement.setLong(3, data.getOnlineTime());
				updateStatement.setLong(4, data.getFirstLogin());
				updateStatement.setLong(5, data.getLastLogin());
				updateStatement.setString(6, data.getRealName());
				updateStatement.setString(7, data.getDiscordName());
				updateStatement.setString(8, data.getTeamspeakName());
				updateStatement.setString(9, data.getSkypeName());
				updateStatement.setString(10, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateLastname(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `last_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getLastName());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateLastIP(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `last_ip` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getLastIP());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateRealName(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `real_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getRealName());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateDiscordName(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `discord_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getDiscordName());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateTeamspeakName(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `teamspeak_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getTeamspeakName());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateSkypeName(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `skype_name` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setString(1, data.getSkypeName());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateOnlineTime(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `online_time` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setLong(1, data.getOnlineTime());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateFirstLogin(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `first_login` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setLong(1, data.getFirstLogin());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}

	public void updateLastLogin(UserData data) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try (PreparedStatement updateStatement = Main.getDatabaseManager()
				.getConnection()
				.prepareStatement("UPDATE `hc_user_data` SET `last_login` = ? WHERE `uniqueId` = ?")) {
				updateStatement.setLong(1, data.getLastLogin());
				updateStatement.setString(2, data.getUniqueId().toString());
				updateStatement.executeUpdate();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		});
	}
}
