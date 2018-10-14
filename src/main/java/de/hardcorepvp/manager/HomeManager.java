package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserHomes;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Utils;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.com.google.common.cache.CacheLoader;
import net.minecraft.util.com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class HomeManager {

    private final LoadingCache<UUID, UserHomes> userHomeCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(4)
            .build(new CacheLoader<UUID, UserHomes>() {
                @Override
                public UserHomes load(UUID uniqueId) throws Exception {
                    Map<String, Location> homes = new ConcurrentHashMap<>();
                    try (PreparedStatement selectStatement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `name`, `location` FROM `hc_user_homes` WHERE `uniqueId` = ?")) {
                        selectStatement.setString(1, uniqueId.toString());
                        try (ResultSet resultSet = selectStatement.executeQuery()) {
                            while (resultSet.next()) {
                                String name = resultSet.getString("name");
                                Location location = Utils.deserializeLocation(resultSet.getString("location"));
                                homes.put(name, location);
                            }
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    return new UserHomes(uniqueId, homes);
                }
            });

    public void getUserHomes(UUID uniqueId, Callback<UserHomes> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                callback.onResult(this.userHomeCache.get(uniqueId));
            } catch (ExecutionException exception) {
                callback.onFailure(exception.getCause());
            }
        });
    }

    public void createHome(UserHomes homes, String name, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement insertStatement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT IGNORE INTO `hc_user_homes` (`uniqueId`, `name`, `location`) VALUES(?, ?, ?)")) {
                insertStatement.setString(1, homes.getUniqueId().toString());
                insertStatement.setString(2, name);
                insertStatement.setString(3, Utils.serializeLocation(location));
                insertStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void deleteHome(UserHomes homes, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement deleteStatement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_user_homes` WHERE `uniqueId` = ? AND `name` = ?")) {
                deleteStatement.setString(1, homes.getUniqueId().toString());
                deleteStatement.setString(2, name);
                deleteStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}
