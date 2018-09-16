package de.hardcorepvp.database;

import de.hardcorepvp.Main;
import de.hardcorepvp.file.ConfigFile;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

	private String url;
	private Properties properties;
	private String host;
	private int port;
	private String schema;
	private String user;
	private String password;
	private Connection connection;

	public DatabaseManager(ConfigFile configFile) {
		this.host = configFile.getHost();
		this.port = configFile.getPort();
		this.schema = configFile.getSchema();
		this.user = configFile.getUser();
		this.password = configFile.getPassword();
		this.url = String.format("jdbc:mariadb://%s/%s", this.host, this.schema);
		this.properties = new Properties();
		this.properties.setProperty("user", user);
		this.properties.setProperty("password", password);
		this.properties.setProperty("port", String.valueOf(port));
		this.properties.setProperty("useSSL", "false");
		this.properties.setProperty("verifyServerCertificate", "true");
		this.properties.setProperty("requireSSL", "false");
		this.properties.setProperty("autoReconnect", "true");
	}

	public Connection getConnection() {
		return this.connection;
	}

	public boolean connect() {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			this.connection = DriverManager.getConnection(this.url, properties);
			this.refreshTask();
			return true;
		} catch (SQLException | ClassNotFoundException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public void disconnect() {
		try {
			if (isConnected()) {
				this.connection.close();
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public boolean isConnected() {
		try {
			return this.connection == null || !this.connection.isValid(10) || this.connection.isClosed();
		} catch (SQLException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	private void refreshTask() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT 1;")) {
					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							System.out.println("DEBUG REFRESH TASK");
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, 20, 20 * 30);
	}
}
