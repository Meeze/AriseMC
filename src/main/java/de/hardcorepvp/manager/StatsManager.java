package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserStats;
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

public class StatsManager {

    private final LoadingCache<UUID, UserStats> userStatsCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(4)
            .build(new CacheLoader<UUID, UserStats>() {
                @Override
                public UserStats load(UUID uniqueId) throws Exception {
                    try (PreparedStatement selectStatement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `kills`, `deaths` FROM `hc_user_stats` WHERE `uniqueId` = ?")) {
                        selectStatement.setString(1, uniqueId.toString());
                        try (ResultSet resultSet = selectStatement.executeQuery()) {
                            if (resultSet.next()) {
                                int kills = resultSet.getInt("kills");
                                int deaths = resultSet.getInt("deaths");
                                return new UserStats(uniqueId, kills, deaths);
                            }
                            try (PreparedStatement insertStatement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT IGNORE INTO `hc_user_stats` (`uniqueId`, `kills`, `deaths`) VALUES(?, ?, ?)")) {
                                insertStatement.setString(1, uniqueId.toString());
                                insertStatement.setInt(2, 0);
                                insertStatement.setInt(3, 0);
                                insertStatement.executeUpdate();
                            }
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    return new UserStats(uniqueId, 0, 0);
                }
            });

    public void getUserStats(UUID uniqueId, Callback<UserStats> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                callback.onResult(this.userStatsCache.get(uniqueId));
            } catch (ExecutionException exception) {
                callback.onFailure(exception.getCause());
            }
        });
    }

    public void update(UserStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement updateStatement = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE `hc_user_stats` SET `kills` = ?, `deaths` = ? WHERE `uniqueId` = ?")) {
                updateStatement.setInt(1, stats.getKills());
                updateStatement.setInt(2, stats.getDeaths());
                updateStatement.setString(3, stats.getUniqueId().toString());
                updateStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void updateKills(UserStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement updateStatement = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE `hc_user_stats` SET `kills` = ? WHERE `uniqueId` = ?")) {
                updateStatement.setInt(1, stats.getKills());
                updateStatement.setString(2, stats.getUniqueId().toString());
                updateStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void updateDeaths(UserStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement updateStatement = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE `hc_user_stats` SET `deaths` = ? WHERE `uniqueId` = ?")) {
                updateStatement.setInt(1, stats.getDeaths());
                updateStatement.setString(2, stats.getUniqueId().toString());
                updateStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}
