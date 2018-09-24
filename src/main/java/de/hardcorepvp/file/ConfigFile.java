package de.hardcorepvp.file;

import de.hardcorepvp.model.FileBase;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigFile extends FileBase {

    private String host;
    private int port;
    private String schema;
    private String user;
    private String password;

    public ConfigFile() {
        super("", "config.yml");
        this.readAll();
    }

    @Override
    public void writeDefaults() {
        FileConfiguration fileConfiguration = this.getFileConfiguration();
        fileConfiguration.addDefault("Database.Host", "");
        fileConfiguration.addDefault("Database.Port", "");
        fileConfiguration.addDefault("Database.Schema", "");
        fileConfiguration.addDefault("Database.User", "");
        fileConfiguration.addDefault("Database.Password", "");

        fileConfiguration.options().copyDefaults(true);
        this.saveFile();
    }

    @Override
    public void readAll() {
        FileConfiguration fileConfiguration = this.getFileConfiguration();
        if (fileConfiguration.isSet("Database.Host")) {
            this.host = fileConfiguration.getString("Database.Host");
        }
        if (fileConfiguration.isSet("Database.Port")) {
            this.port = fileConfiguration.getInt("Database.Port");
        }
        if (fileConfiguration.isSet("Database.Schema")) {
            this.schema = fileConfiguration.getString("Database.Schema");
        }
        if (fileConfiguration.isSet("Database.User")) {
            this.user = fileConfiguration.getString("Database.User");
        }
        if (fileConfiguration.isSet("Database.Password")) {
            this.password = fileConfiguration.getString("Database.Password");
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSchema() {
        return schema;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
