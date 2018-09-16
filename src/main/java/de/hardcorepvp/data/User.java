package de.hardcorepvp.data;

import de.hardcorepvp.Main;
import de.hardcorepvp.database.DatabaseUpdate;
import de.hardcorepvp.manager.PunishmentManager;
import de.hardcorepvp.permissions.PermissionGroup;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User extends DatabaseUpdate {

	private UUID uniqueId;
	private PermissionGroup group;
	private PunishmentManager.MuteData muteData;
	private Map<String, Location> homeMap;
	private long money;
	private int kills;
	private int deaths;

	public User(UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.group = Main.getPermissionsFile().getDefaultGroup();
		this.muteData = null;
		this.homeMap = new ConcurrentHashMap<>();
		this.money = 0L;
		this.kills = 0;
		this.deaths = 0;
		this.readFromDatabase();
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public PermissionGroup getGroup() {
		return group;
	}

	public PunishmentManager.MuteData getMuteData() {
		return muteData;
	}

	public Map<String, Location> getHomes() {
		return homeMap;
	}

	public long getMoney() {
		return money;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setGroup(PermissionGroup group) {
		this.group = group;
		this.writeToDatabase(true, false, false, false, true);
	}

	public void setMuteData(PunishmentManager.MuteData muteData) {
		this.muteData = muteData;
	}

	public void addHome(String name, Location location) {
		this.homeMap.put(name, location);
		this.writeToDatabase(false,true, false, false, true);
	}

	public void removeHome(String name) {
		this.homeMap.remove(name);
		this.writeToDatabase(false,true, false, false, true);
	}

	public void addMoney(long money) {
		this.money += money;
		this.writeToDatabase(false,false, true, false, true);
	}

	public void setMoney(long money) {
		this.money = money;
		this.writeToDatabase(false,false, true, false, true);
	}

	public void removeMoney(long money) {
		this.money -= money;
		this.writeToDatabase(false,false, true, false, true);
	}

	public void addKill() {
		this.kills++;
		this.writeToDatabase(false,false, false, true, true);
	}

	public void setKills(int kills) {
		this.kills = kills;
		this.writeToDatabase(false,false, false, true, true);
	}

	public void addDeath() {
		this.deaths++;
		this.writeToDatabase(false,false, false, true, true);
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		this.writeToDatabase(false,false, false, true, true);
	}

	public double getKD() {
		if (getKills() <= 0) {
			return 0.0D;
		}
		if (getDeaths() <= 0) {
			return getKills();
		}
		BigDecimal bigDecimal = new BigDecimal(getKills() / getDeaths());
		bigDecimal.setScale(2, 4);
		return bigDecimal.doubleValue();
	}

	private void readFromDatabase() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				this.muteData = Main.getPunishmentManager().getMuteData(this.uniqueId);
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT a.kills, a.deaths, b.money, c.group, d.name, d.location FROM hc_user_stats a "
						+ "INNER JOIN hc_user_money b ON a.uniqueId = b.uniqueId "
						+ "INNER JOIN hc_user_groups c ON a.uniqueId = c.uniqueId "
						+ "INNER JOIN hc_user_homes d ON a.uniqueId = d.uniqueId "
						+ "WHERE a.uniqueId = ?")) {
					statement.setString(1, uniqueId.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						boolean statsMoneyGroup = false;
						if (!resultSet.next()) {
							this.writeToDatabase(true, true, true, true, false);
							this.setReady(true);
							return;
						}
						while (resultSet.next()) {
							if (!statsMoneyGroup) {
								PermissionGroup group1 = Main.getPermissionsFile().getGroup(resultSet.getString("group"));
								if (group1 != null) {
									this.group = group1;
								}
								this.money = resultSet.getLong("money");
								this.kills = resultSet.getInt("kills");
								this.deaths = resultSet.getInt("deaths");
								statsMoneyGroup = true;
							}
							if (!this.homeMap.containsKey(resultSet.getString("name"))) {
								this.homeMap.put(resultSet.getString("name"), Utils.deserializeLocation(resultSet.getString("location")));
							}
						}
						this.setReady(true);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	private void writeToDatabase(boolean group, boolean homes, boolean money, boolean stats, boolean async) {
		if (!async) {
			try {
				Main.getDatabaseManager().getConnection().setAutoCommit(false);
				if (group) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_groups` (`uniqueId`, `group`)" +
							"VALUES(?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `group` = VALUES(`group`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setString(2, this.group.getName());
						statement.executeUpdate();
					}
				}
				if (homes) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_homes` (`uniqueId`, `name`, `location`)" +
							"VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `name` = VALUES(`name`), `location` = VALUES(`location`)")) {
						for (Map.Entry<String, Location> home : this.homeMap.entrySet()) {
							statement.setString(1, this.uniqueId.toString());
							statement.setString(2, home.getKey());
							statement.setString(3, Utils.serializeLocation(home.getValue()));
							statement.addBatch();
							statement.clearParameters();
						}
						statement.executeBatch();
					}
				}
				if (money) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_money` (`uniqueId`, `money`)" +
							"VALUES(?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `money` = VALUES(`money`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setLong(2, this.money);
						statement.executeUpdate();
					}
				}
				if (stats) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_stats` (`uniqueId`, `kills`, `deaths`)" +
							"VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `kills` = VALUES(`kills`), `deaths` = VALUES(`deaths`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setInt(2, this.kills);
						statement.setInt(3, this.deaths);
						statement.executeUpdate();
					}
				}
				Main.getDatabaseManager().getConnection().commit();
				Main.getDatabaseManager().getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				Main.getDatabaseManager().getConnection().setAutoCommit(false);
				if (group) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_groups` (`uniqueId`, `group`)" +
							"VALUES(?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `group` = VALUES(`group`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setString(2, this.group.getName());
						statement.executeUpdate();
					}
				}
				if (homes) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_homes` (`uniqueId`, `name`, `location`)" +
							"VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `name` = VALUES(`name`), `location` = VALUES(`location`)")) {
						for (Map.Entry<String, Location> home : this.homeMap.entrySet()) {
							statement.setString(1, this.uniqueId.toString());
							statement.setString(2, home.getKey());
							statement.setString(3, Utils.serializeLocation(home.getValue()));
							statement.addBatch();
							statement.clearParameters();
						}
						statement.executeBatch();
					}
				}
				if (money) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_money` (`uniqueId`, `money`)" +
							"VALUES(?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `money` = VALUES(`money`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setLong(2, this.money);
						statement.executeUpdate();
					}
				}
				if (stats) {
					try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_user_stats` (`uniqueId`, `kills`, `deaths`)" +
							"VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `uniqueId` = VALUES(`uniqueId`), `kills` = VALUES(`kills`), `deaths` = VALUES(`deaths`)")) {
						statement.setString(1, this.uniqueId.toString());
						statement.setInt(2, this.kills);
						statement.setInt(3, this.deaths);
						statement.executeUpdate();
					}
				}
				Main.getDatabaseManager().getConnection().commit();
				Main.getDatabaseManager().getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
