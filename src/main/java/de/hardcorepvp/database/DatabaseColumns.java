package de.hardcorepvp.database;

public enum DatabaseColumns {
    REAL_NAME("hc_user_data", "UPDATE `hc_user_data` SET `real_name` = ? WHERE `uniqueId` = ?", "real_name"),
    LAST_NAME("hc_user_data", "UPDATE `hc_user_data` SET `last_name` = ? WHERE `uniqueId` = ?", "last_name"),
    DISCORD_NAME("hc_user_data", "UPDATE `hc_user_data` SET `discord_name` = ? WHERE `uniqueId` = ?", "discord_name"),
    TEAMSPEAK_NAME("hc_user_data", "UPDATE `hc_user_data` SET `teamspeak_name` = ? WHERE `uniqueId` = ?", "teamspeak_name"),
    SKYPE_NAME("hc_user_data", "UPDATE `hc_user_data` SET `real_name` = ? WHERE `uniqueId` = ?", "skype_name"),
    IP("hc_user_data", "UPDATE `hc_user_data` SET `last_ip` = ? WHERE `uniqueId` = ?", "last_ip"),
    ONLINE_TIME("hc_user_data", "UPDATE `hc_user_data` SET `online_time` = ? WHERE `uniqueId` = ?", "online_time"),
    FIRST_LOGIN("hc_user_data", "UPDATE `hc_user_data` SET `first_login` = ? WHERE `uniqueId` = ?", "first_login"),
    LAST_LOGIN("hc_user_data", "UPDATE `hc_user_data` SET `last_login` = ? WHERE `uniqueId` = ?", "last_login"),
    MONEY("hc_user_currency", "UPDATE `hc_user_currency` SET `money` = ? WHERE `uniqueId` = ?", "money"),
    KILLS("hc_user_stats", "UPDATE `hc_user_stats` SET `kills` = ? WHERE `uniqueId` = ?", "kills"),
    DEATHS("hc_user_stats", "UPDATE `hc_user_stats` SET `deaths` = ? WHERE `uniqueId` = ?", "deaths");

    private String table;
    private String updateStatement;
    private String[] columns;

    private DatabaseColumns(String table, String updateStatement, String... columns) {
        this.table = table;
        this.updateStatement = updateStatement;
        this.columns = columns;
    }

    public String getTable() {
        return table;
    }

    public String getUpdateStatement() {
        return updateStatement;
    }

    public String[] getColumns() {
        return columns;
    }
}
