package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.model.Ranking;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.minecraft.util.com.google.common.cache.CacheLoader;
import net.minecraft.util.com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RankingManager {

	private LoadingCache<UUID, Integer> userRankCache;
	private LoadingCache<Clan, Integer> clanRankCache;
	private LoadingCache<Ranking, Map<String, Long>> rankingCache;

	public RankingManager() {
		this.userRankCache = CacheBuilder
				.newBuilder()
				.expireAfterAccess(3, TimeUnit.MINUTES)
				.build(new CacheLoader<UUID, Integer>() {
					@Override
					public Integer load(UUID uniqueId) throws Exception {
						int rank = -1;
						try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT rank, uniqueId FROM (SELECT @rank:=@rank+1 AS rank, uniqueId FROM hc_user_stats, (SELECT @rank := 0) r ORDER BY kills DESC) t WHERE uniqueId = ?")) {
							statement.setString(1, uniqueId.toString());
							try (ResultSet resultSet = statement.executeQuery()) {
								if (resultSet.next()) {
									rank = resultSet.getInt("rank");
								}
							}
						}
						return rank;
					}
				});
		this.clanRankCache = CacheBuilder
				.newBuilder()
				.expireAfterAccess(3, TimeUnit.MINUTES)
				.build(new CacheLoader<Clan, Integer>() {
					@Override
					public Integer load(Clan clan) throws Exception {
						int rank = -1;
						try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT rank, clan FROM (SELECT @rank:=@rank+1 AS rank, clan FROM hc_clans, (SELECT @rank := 0) r ORDER BY kills DESC) t WHERE clan = ?")) {
							statement.setString(1, clan.getName());
							try (ResultSet resultSet = statement.executeQuery()) {
								if (resultSet.next()) {
									rank = resultSet.getInt("rank");
								}
							}
						}
						return rank;
					}
				});
		this.rankingCache = CacheBuilder
				.newBuilder()
				.expireAfterAccess(3, TimeUnit.MINUTES)
				.build(new CacheLoader<Ranking, Map<String, Long>>() {
					@Override
					public Map<String, Long> load(Ranking ranking) throws Exception {
						if (ranking == Ranking.KILLS) {
							try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `uniqueId`, `kills` FROM `hc_user_stats` ORDER BY `kills` DESC LIMIT 10")) {
								try (ResultSet resultSet = statement.executeQuery()) {
									Map<String, Long> rankingKills = new LinkedHashMap<>();
									while (resultSet.next()) {
										UUID uniqueId = UUID.fromString(resultSet.getString("uniqueId"));
										long kills = resultSet.getInt("kills");
										Player target = Bukkit.getPlayer(uniqueId);
										if (target != null) {
											rankingKills.put(target.getName(), kills);
											continue;
										}
										OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
										if (offlinePlayer.hasPlayedBefore()) {
											rankingKills.put(offlinePlayer.getName(), kills);
										}
										rankingKills.put(uniqueId.toString(), kills);
									}
									return rankingKills;
								}
							}
						}
						if (ranking == Ranking.DEATHS) {
							try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `uniqueId`, `deaths` FROM `hc_user_stats` ORDER BY `deaths` DESC LIMIT 10")) {
								try (ResultSet resultSet = statement.executeQuery()) {
									Map<String, Long> rankingDeaths = new LinkedHashMap<>();
									while (resultSet.next()) {
										UUID uniqueId = UUID.fromString(resultSet.getString("uniqueId"));
										long deaths = resultSet.getInt("deaths");
										Player target = Bukkit.getPlayer(uniqueId);
										if (target != null) {
											rankingDeaths.put(target.getName(), deaths);
											continue;
										}
										OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
										if (offlinePlayer.hasPlayedBefore()) {
											rankingDeaths.put(offlinePlayer.getName(), deaths);
										}
										rankingDeaths.put(uniqueId.toString(), deaths);
									}
									return rankingDeaths;
								}
							}
						}
						if (ranking == Ranking.MONEY) {
							try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `uniqueId`, `money` FROM `hc_user_money` ORDER BY `money` DESC LIMIT 10")) {
								try (ResultSet resultSet = statement.executeQuery()) {
									Map<String, Long> rankingMoney = new LinkedHashMap<>();
									while (resultSet.next()) {
										UUID uniqueId = UUID.fromString(resultSet.getString("uniqueId"));
										long money = resultSet.getLong("money");
										Player target = Bukkit.getPlayer(uniqueId);
										if (target != null) {
											rankingMoney.put(target.getName(), money);
											continue;
										}
										OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
										if (offlinePlayer.hasPlayedBefore()) {
											rankingMoney.put(offlinePlayer.getName(), money);
										}
										rankingMoney.put(uniqueId.toString(), money);
									}
									return rankingMoney;
								}
							}
						}
						if (ranking == Ranking.CLAN) {
							try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("SELECT `clan`, `kills` FROM `hc_clans` ORDER BY `kills` DESC LIMIT 10")) {
								try (ResultSet resultSet = statement.executeQuery()) {
									Map<String, Long> rankingClans = new LinkedHashMap<>();
									while (resultSet.next()) {
										String clan = resultSet.getString("clan");
										long kills = resultSet.getInt("kills");
										rankingClans.put(clan, kills);
									}
									return rankingClans;
								}
							}
						}
						return new ConcurrentHashMap<>();
					}
				});
	}

	public void getUserRank(UUID uniqueId, Callback<Integer> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				callback.onResult(this.userRankCache.get(uniqueId));
			} catch (ExecutionException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public int getUserRank(UUID uniqueId) {
		try {
			return this.userRankCache.get(uniqueId);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void getClanRank(Clan clan, Callback<Integer> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				callback.onResult(this.clanRankCache.get(clan));
			} catch (ExecutionException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public void getRanking(Ranking ranking, Callback<Map<String, Long>> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				callback.onResult(this.rankingCache.get(ranking));
			} catch (ExecutionException e) {
				e.printStackTrace();
				callback.onFailure(e.getCause());
			}
		});
	}

	public Map<String, Long> getRanking(Ranking ranking) {
		try {
			return this.rankingCache.get(ranking);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
