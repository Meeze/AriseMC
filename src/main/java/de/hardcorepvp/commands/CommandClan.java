package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.clan.ClanMember;
import de.hardcorepvp.clan.ClanRank;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.model.Ranking;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandClan implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		Clan clan = Main.getClanManager().getClan(player.getUniqueId());
		if (args.length == 0 || args.length > 3) {
			this.sendHelpPage(player, 1);
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("1")) {
				this.sendHelpPage(player, 1);
				return true;
			}
			if (args[0].equalsIgnoreCase("2")) {
				this.sendHelpPage(player, 2);
				return true;
			}
			if (args[0].equalsIgnoreCase("verlassen")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() == ClanRank.OWNER) {
					player.sendMessage("Du hat den Clan verlassen.");
					player.sendMessage("Somit wurde der Clan gelöscht");
					for (ClanMember clanMember : clan.getMembers()) {
						Player target = Bukkit.getPlayer(clanMember.getUniqueId());
						if (target != null && target != player) {
							target.sendMessage(player.getName() + " hat den Clan verlassen.");
							target.sendMessage("Somit wurde der Clan gelöscht");
						}
					}
					Main.getClanManager().deleteClan(clan.getName());
					return true;
				}
				player.sendMessage("Du hast den Clan verlassen");
				for (ClanMember clanMember : clan.getMembers()) {
					Player target = Bukkit.getPlayer(clanMember.getUniqueId());
					if (target != null && target != player) {
						target.sendMessage(player.getName() + " hat den Clan verlassen.");
					}
				}
				Main.getClanManager().leaveClan(player.getUniqueId());
				return true;
			}
			if (args[0].equalsIgnoreCase("setbase")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() == ClanRank.MEMBER) {
					player.sendMessage("Du kannst die Clan-Basis erst ab Trusted setzen");
					return true;
				}
				clan.setBase(player.getLocation());
				player.sendMessage("Die Clan-Basis wurde versetzt");
				for (ClanMember clanMember : clan.getMembers()) {
					Player target = Bukkit.getPlayer(clanMember.getName());
					if (target != null && target != player) {
						target.sendMessage("Die Clan-Basis wurde von " + player.getName() + " versetzt");
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("base")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				player.teleport(clan.getBase());
				player.sendMessage("Du wurdest zur Clan-Basis teleportiert");
				return true;
			}
			if (args[0].equalsIgnoreCase("mitglieder")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				this.sendMembers(player, clan);
				return true;
			}
			if (args[0].equalsIgnoreCase("löschen")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() != ClanRank.OWNER) {
					player.sendMessage("Nur der Owner kann den Clan wieder löschen");
					return true;
				}
				player.sendMessage("Du hast den Clan gelöscht");
				for (ClanMember clanMember : clan.getMembers()) {
					Player target = Bukkit.getPlayer(clanMember.getName());
					if (target != null && target != player) {
						target.sendMessage(player.getName() + " hat den Clan gelöscht");
					}
				}
				Main.getClanManager().deleteClan(clan.getName());
				return true;
			}
			if (args[0].equalsIgnoreCase("rang")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				player.sendMessage(" " + ClanRank.MEMBER.getPrefix());
				player.sendMessage(" - In den Clan-Chat schreiben");
				player.sendMessage(" - Zur Clan-Basis teleportieren");
				player.sendMessage(" " + ClanRank.TRUSTED.getPrefix());
				player.sendMessage(" - Clan-Basis versetzen");
				player.sendMessage(" - Clan-Mitglieder rauswerfen");
				player.sendMessage(" - Clan-Ränge vergeben");
				return true;
			}
			if (args[0].equalsIgnoreCase("level")) {
				player.sendMessage("Ab 1000 Kills wird dein Kürzel §ahellgrün");
				player.sendMessage("Ab 2500 Kills wird dein Kürzel §6orange");
				player.sendMessage("Ab 5000 Kills wird dein Kürzel §dpink");
				player.sendMessage("Ab 10000 Kills wird dein Kürzel §5dunkellila");
				player.sendMessage("Ab 20000 Kills wird dein Kürzel §chellrot");
				return true;
			}
			if (args[0].equalsIgnoreCase("ranking")) {
				Main.getRankingManager().getRanking(Ranking.CLAN, new Callback<Map<String, Long>>() {
					@Override
					public void onResult(Map<String, Long> type) {
						int place = 1;
						player.sendMessage("Platz | Clan | Kills");
						for (Map.Entry<String, Long> entry : type.entrySet()) {
							player.sendMessage(place + ". " + entry.getKey() + " | " + entry.getValue());
							place++;
						}
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("Beim laden der Statistiken ist ein Fehler aufgetreten!");
					}
				});
				return true;
			}
			if (args[0].equalsIgnoreCase("stats")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				this.sendStats(player, clan);
				return true;
			}
			this.sendHelpPage(player, 1);
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("stats")) {
				Clan targetClan = Main.getClanManager().getClan(args[1]);
				if (targetClan == null) {
					player.sendMessage("Dieser Clan existiert nicht!");
					return true;
				}
				this.sendStats(player, targetClan);
				return true;
			}
			if (args[0].equalsIgnoreCase("mitglieder")) {
				Clan targetClan = Main.getClanManager().getClan(args[1]);
				if (targetClan == null) {
					player.sendMessage("Dieser Clan existiert nicht!");
					return true;
				}
				this.sendMembers(player, targetClan);
				return true;
			}
			if (args[0].equalsIgnoreCase("einladen")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() == ClanRank.MEMBER) {
					player.sendMessage("Du kannst Mitglieder erst ab Trusted einladen.");
					return true;
				}
				if (clan.getMembers().size() >= clan.getMaxMembers()) {
					player.sendMessage("Der Clan hat die maximale Spielergröße erreicht.");
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					player.sendMessage(Messages.formatMessage(Messages.PLAYER_NOT_FOUND));
					return true;
				}
				if (Main.getClanManager().hasClan(target.getUniqueId())) {
					player.sendMessage("Der Spieler ist bereits in einem Clan.");
					return true;
				}
				if (Main.getClanManager().hasClanRequest(target.getUniqueId(), clan)) {
					player.sendMessage("Der Spieler hat bereits eine Anfrage von deinem Clan erhalten.");
					return true;
				}
				Main.getClanManager().addClanRequest(target.getUniqueId(), clan);
				player.sendMessage("Du hast " + target.getName() + " in den Clan eingeladen");
				target.sendMessage("Du hast eine Clan-Anfrage von " + player.getName() + " erhalten!");
				target.sendMessage("/clan annehmen " + clan.getName() + " um sie anzunehmen!");
				clan.broadcast(player, player.getName() + " hat " + target.getName() + " in den Clan eingeladen!");
				return true;
			}
			if (args[0].equalsIgnoreCase("annehmen")) {
				if (Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist schon in einem Clan.");
					return true;
				}
				Clan targetClan = Main.getClanManager().getClan(args[1]);
				if (targetClan == null) {
					player.sendMessage("Dieser Clan existiert nicht!");
					return true;
				}
				if (!Main.getClanManager().hasClanRequest(player.getUniqueId(), targetClan)) {
					player.sendMessage("Du hast keine Anfrage von diesem Clan erhalten!");
					return true;
				}
				if (targetClan.getMembers().size() >= targetClan.getMaxMembers()) {
					player.sendMessage("Der Clan hat die maximale Spielergröße erreicht.");
					return true;
				}
				Main.getClanManager().removeClanRequests(player.getUniqueId());
				Main.getClanManager().joinClan(player.getUniqueId(), player.getName(), targetClan, ClanRank.MEMBER);
				player.sendMessage("Du bist dem Clan beigetreten.");
				targetClan.broadcast(player, player.getName() + " ist dem Clan beigetreten!");
				return true;
			}
			if (args[0].equalsIgnoreCase("ablehnen")) {
				Clan targetClan = Main.getClanManager().getClan(args[1]);
				if (targetClan == null) {
					player.sendMessage("Dieser Clan existiert nicht!");
					return true;
				}
				if (!Main.getClanManager().hasClanRequest(player.getUniqueId(), targetClan)) {
					player.sendMessage("Du hast keine Clan-Anfrage von diesem Clan erhalten!");
					return true;
				}
				Main.getClanManager().removeClanRequest(player.getUniqueId(), targetClan);
				player.sendMessage("Du hast die Clan-Anfrage abgelehnt!");
				targetClan.broadcast(player, player.getName() + " hat die Anfrage abgelehnt!");
				return true;
			}
			if (args[0].equalsIgnoreCase("kick")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() == ClanRank.MEMBER) {
					player.sendMessage("Du kannst Spieler erst ab Trusted aus dem Clan werfen!");
					return true;
				}
				ClanMember target = Main.getClanManager().getMember(args[1], member.getClan());
				if (target == null) {
					player.sendMessage("Der Spieler konnte nicht gefunden werden!");
					return true;
				}
				if (target == member) {
					player.sendMessage("Du kannst dich nicht selber aus dem Clan werfen!");
					return true;
				}
				if (target.getRank() == ClanRank.TRUSTED || target.getRank() == ClanRank.OWNER) {
					player.sendMessage("Diesen Spieler kannst du nicht aus dem Clan werfen!");
					return true;
				}
				player.sendMessage("Du hast " + target.getName() + " aus dem Clan geworfen!");
				Main.getClanManager().leaveClan(target.getUniqueId());
				Player targetPlayer = Bukkit.getPlayer(target.getName());
				if (targetPlayer != null) {
					targetPlayer.sendMessage("Du wurdest von " + player.getName() + " aus dem Clan geworfen!");
				}
				clan.broadcast(player, args[1] + " wurde von " + player.getName() + " aus dem Clan geworfen!");
				return true;
			}
			this.sendHelpPage(player, 1);
			return true;
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("erstellen")) {
				if (Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist schon in einem Clan.");
					return true;
				}
				String name = args[1];
				String tag = args[2];
				if (name.length() > 16) {
					player.sendMessage("Der Name des Clans darf nicht länger als 16 Zeichen sein!");
					return true;
				}
				if (tag.length() > 5) {
					player.sendMessage("Der Kürzel des Clans darf nicht länger als 5 Zeichen sein!");
					return true;
				}
				if (Main.getClanManager().getClan(name) != null) {
					player.sendMessage("Es gibt bereits einen Clan mit diesem Namen!");
					return true;
				}
				if (Main.getClanManager().getClan(tag) != null) {
					player.sendMessage("Es gibt bereits einen Clan mit dem Kürzel!");
					return true;
				}
				Main.getClanManager().createClan(name, tag, player.getUniqueId(), player.getName(), player.getLocation());
				player.sendMessage("Du hast den Clan " + name  + " mit dem Kürzel " + tag + " gegründet!");
				player.sendMessage("Die Clan-Basis wurde auf deine Position gesetzt.");
				return true;
			}
			if (args[0].equalsIgnoreCase("setrang")) {
				if (!Main.getClanManager().hasClan(player.getUniqueId())) {
					player.sendMessage("Du bist in keinem Clan!");
					return true;
				}
				ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
				if (member.getRank() == ClanRank.MEMBER) {
					player.sendMessage("Du kannst Mitglieder erst ab Trusted befördern!");
					return true;
				}
				ClanMember target = Main.getClanManager().getMember(args[1], member.getClan());
				if (target == null) {
					player.sendMessage("Der Spieler wurde nicht gefunden!");
					return true;
				}
				if (target == member) {
					player.sendMessage("Du kannst deinen eigenen Rang nicht verändern!");
					return true;
				}
				ClanRank rank = ClanRank.getByName(args[2]);
				if (rank == null) {
					player.sendMessage("Bitte gebe einen gültigen Rangnamen an!");
					return true;
				}
				if (rank == ClanRank.OWNER) {
					player.sendMessage("Dieser Rang kann nicht vergeben werden!");
					return true;
				}
				if (rank == target.getRank()) {
					player.sendMessage("Der Spieler besitzt diesen Rang bereits!");
					return true;
				}
				target.setRank(rank);
				Player targetPlayer = Bukkit.getPlayer(target.getName());
				if (targetPlayer != null) {
					targetPlayer.sendMessage("Du wurdest auf " + rank.getPrefix() + " gestuft.");
				}
				clan.broadcast(player.getName() + " hat " + target.getName() + " auf " + rank.getPrefix() + " gestuft!");
				return true;
			}
		}
		this.sendHelpPage(player, 1);
		return true;
	}

	private void sendHelpPage(Player player, int page) {
		if (page == 1) {
			player.sendMessage("/clan erstellen <Name> <Tag> - Erstellt einen Clan"); //3
			player.sendMessage("/clan einladen <Spieler> - Lädt einen Spieler ein"); //2
			player.sendMessage("/clan verlassen - Verlässt einen Clan"); //1
			player.sendMessage("/clan setbase - Setzt die Clan-Basis"); //1
			player.sendMessage("/clan kick <Spieler> - Kickt einen Spieler"); //2
			player.sendMessage("/clan löschen - Löscht den Clan"); //1
			player.sendMessage("/clan annehmen <Name | Tag> - Nimmt eine Anfrage an"); //2
			player.sendMessage("/clan ablehnen <Name | Tag - Lehnt eine Abfrage ab"); //2
			player.sendMessage("/clan 2 - Seite 2 der Clan-Hilfe"); //1
			return;
		}
		player.sendMessage("/clan base - Teleportiert dich zur Clan-Basis");
		player.sendMessage("/clan mitglieder <Name | Tag> Listet alle Clan-Mitglieder auf"); //2
		player.sendMessage("/clan setrang <Spieler> <Rang> - Verändert den Rang eines Mitgliedes"); //3
		player.sendMessage("/clan rang - Zeigt alle vergebaren Ränge an"); //1
		player.sendMessage("/clan stats <Name | Tag> - Zeigt die Statistik des Clans an"); //2
		player.sendMessage("/clan level - Zeigt die erreichbaren Clan-Level an"); //1
		player.sendMessage("/clan ranking - Zeigt das Clan-Ranking an"); //1
		player.sendMessage("/clan 1 - Seite 1 der Clan-Hilfe"); //1
	}

	private void sendMembers(Player player, Clan clan) {
		StringBuilder owner = new StringBuilder();
		StringBuilder trusted = new StringBuilder();
		StringBuilder member = new StringBuilder();
		for (ClanMember clanMember : clan.getMembers()) {
			Player target = Bukkit.getPlayer(clanMember.getUniqueId());
			String name = target == null ? "§7" + clanMember.getName() : "§a" + clanMember.getName();
			switch (clanMember.getRank()) {
				case OWNER: {
					owner.append(name);
					continue;
				}
				case TRUSTED: {
					trusted.append(name);
					continue;
				}
				case MEMBER: {
					member.append(name);
					continue;
				}
				default: {
					continue;
				}
			}
		}
		player.sendMessage("Name: " + clan.getName());
		player.sendMessage("Owner: " + (owner.length() == 0 ? "§7Keiner" : owner.substring(0, owner.length() - 1)));
		player.sendMessage("Trusted: " + (trusted.length() == 0 ? "§7Keiner" : trusted.substring(0, trusted.length() - 1)));
		player.sendMessage("Member: " + (member.length() == 0 ? "§7Keiner" : member.substring(0, member.length() - 1)));
	}

	private void sendStats(Player player, Clan clan) {
		Main.getRankingManager().getClanRank(clan, new Callback<Integer>() {
			@Override
			public void onResult(Integer rank) {
				ClanMember owner = null;
				for (ClanMember member : clan.getMembers()) {
					if (member.getRank() == ClanRank.OWNER) {
						owner = member;
					}
				}
				player.sendMessage("Name: " + clan.getName());
				player.sendMessage("Owner: " + owner.getName());
				player.sendMessage("Kills: " + clan.getKills());
				player.sendMessage("Mitglieder: " + clan.getMembers().size() + "/" + clan.getMaxMembers());
				player.sendMessage("Ranking: " + rank);
			}

			@Override
			public void onFailure(Throwable cause) {
				player.sendMessage("Beim laden des Rankings ist ein Fehler aufgetreten!");
			}
		});
	}
}
