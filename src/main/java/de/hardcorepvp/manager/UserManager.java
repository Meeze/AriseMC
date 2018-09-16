package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

	private Map<UUID, User> userMap;

	public UserManager() {
		this.userMap = new ConcurrentHashMap<>();
		this.setupTables();
	}

	public User getUser(UUID uniqueId) {
		if (this.userMap.containsKey(uniqueId)) {
			return userMap.get(uniqueId);
		}
		return null;
	}

	public void addUser(UUID uniqueId) {
		if (!this.userMap.containsKey(uniqueId)) {
			this.userMap.put(uniqueId, new User(uniqueId));
		}
	}

	public void removeUser(UUID uniqueId) {
		this.userMap.remove(uniqueId);
	}

	private void setupTables() {
		try {
			Main.getDatabaseManager().getConnection().setAutoCommit(false);
			try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_user_money` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `money` BIGINT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement.executeUpdate();
			}
			try (PreparedStatement statement1 = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_user_stats` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `kills` INT NOT NULL,"
					+ " `deaths` INT NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement1.executeUpdate();
			}
			try (PreparedStatement statement2 = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_user_homes` ("
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `name` VARCHAR(50) NOT NULL,"
					+ " `location` VARCHAR(250) NOT NULL,"
					+ " INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement2.executeUpdate();
			}
			try (PreparedStatement statement3 = Main.getDatabaseManager().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `hc_user_groups` ("
					+ " `id` INT NOT NULL AUTO_INCREMENT,"
					+ " `uniqueId` CHAR(36) NOT NULL,"
					+ " `group` CHAR(60) NOT NULL,"
					+ " PRIMARY KEY (`id`),"
					+ " UNIQUE INDEX `uniqueId_UNIQUE` (`uniqueId` ASC))")) {
				statement3.executeUpdate();
			}
			Main.getDatabaseManager().getConnection().commit();
			Main.getDatabaseManager().getConnection().setAutoCommit(true);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
}
