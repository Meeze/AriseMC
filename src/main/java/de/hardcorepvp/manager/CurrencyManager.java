package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserCurrency;
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

public class CurrencyManager {

    private final LoadingCache<UUID, UserCurrency> userCurrencyCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(4)
            .build(new CacheLoader<UUID, UserCurrency>() {
                @Override
                public UserCurrency load(UUID uniqueId) throws Exception {
                    try (PreparedStatement selectStatement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `money` FROM `hc_user_currency` WHERE `uniqueId` = ?")) {
                        selectStatement.setString(1, uniqueId.toString());
                        try (ResultSet resultSet = selectStatement.executeQuery()) {
                            if (resultSet.next()) {
                                long money = resultSet.getLong("money");
                                return new UserCurrency(uniqueId, money);
                            }
                            try (PreparedStatement insertStatement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT IGNORE INTO `hc_user_currency` (`uniqueId`, `money`) VALUES(?, ?)")) {
                                insertStatement.setString(1, uniqueId.toString());
                                insertStatement.setLong(2, 0L);
                                insertStatement.executeUpdate();
                            }
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    return new UserCurrency(uniqueId, 0L);
                }
            });

    public void getUserCurrency(UUID uniqueId, Callback<UserCurrency> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                callback.onResult(this.userCurrencyCache.get(uniqueId));
            } catch (ExecutionException exception) {
                callback.onFailure(exception.getCause());
            }
        });
    }

    public void update(UserCurrency currency) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement updateStatement = Main.getDatabaseManager().getConnection().prepareStatement("UPDATE `hc_user_currency` SET `money` = ? WHERE `uniqueId` = ?")) {
                updateStatement.setLong(1, currency.getMoney());
                updateStatement.setString(2, currency.getUniqueId().toString());
                updateStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}
