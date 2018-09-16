package de.hardcorepvp.clan;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Utils;
import net.minecraft.server.v1_7_R4.BlockAnvil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clan {

	private List<ClanMember> members;
	private Location base;
	private int maxMembers;
	private int kills;
	private int ranking;
	private String name;
	private String tag;

	public Clan(String name, String tag, Location base, int kills) {
		this.name = name;
		this.tag = tag;
		this.members = new CopyOnWriteArrayList<>();
		this.base = base;
		this.maxMembers = 8;
		this.kills = kills;
		this.ranking = -1;
		this.writeToDatabase();
	}

	public List<ClanMember> getMembers() {
		return members;
	}

	public Location getBase() {
		return base;
	}

	public int getMaxMembers() {
		return maxMembers;
	}

	public int getKills() {
		return kills;
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public String getTagColor() {
		if (this.tag.equalsIgnoreCase("Team")) {
			return "§4";
		}
		if (this.kills == 1000) {
			return "§a";
		}
		if (this.kills == 2500) {
			return "§6";
		}
		if (this.kills == 5000) {
			return "§d";
		}
		if (this.kills == 10000) {
			return "§5";
		}
		if (this.kills >= 20000) {
			return "§c";
		}
		return "§b";
	}

	public void addMember(ClanMember member) {
		this.members.add(member);
	}

	public void removeMember(ClanMember member) {
		this.members.remove(member);
	}

	public void setBase(Location location) {
		this.base = location;
		this.writeToDatabase();
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public void broadcast(Player executor, String message) {
		for (ClanMember member : this.members) {
			Player target = Bukkit.getPlayer(member.getName());
			if (target != null && target != executor) {
				target.sendMessage(message);
			}
		}
	}

	public void broadcast(String message) {
		for (ClanMember member : this.members) {
			Player target = Bukkit.getPlayer(member.getName());
			if (target != null) {
				target.sendMessage(message);
			}
		}
	}

	public void delete() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("DELETE FROM `hc_clans` WHERE `clan` = ?")) {
					statement.setString(1, this.name);
					statement.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	private void writeToDatabase() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				try (PreparedStatement statement = Main.getDatabaseManager().getConnection().prepareStatement("INSERT INTO `hc_clans` (`clan`, `tag`, `base`, `kills`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `clan` = VALUES(`clan`), `tag` = VALUES(`tag`), `base` = VALUES(`base`), `kills` = VALUES(`kills`)")) {
					statement.setString(1, this.name);
					statement.setString(2, this.tag);
					statement.setString(3, Utils.serializeLocation(this.base));
					statement.setInt(4, this.kills);
					statement.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
