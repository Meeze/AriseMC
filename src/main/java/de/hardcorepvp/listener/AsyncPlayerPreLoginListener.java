package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.manager.PunishmentManager;
import de.hardcorepvp.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerPreLoginListener implements Listener {

	@EventHandler
	public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
		UUID uniqueId = event.getUniqueId();
		//punishment checken, ob ban noch aktuell timestamp
		long timestamp = System.currentTimeMillis();
		PunishmentManager.BanData banData = Main.getPunishmentManager().getBanData(uniqueId);
		System.out.println("Dauer: " + (System.currentTimeMillis() - timestamp));
		if (banData != null) {
			if (banData.getBanTime() != -1L && System.currentTimeMillis() >= banData.getBanTime()) {
				Main.getPunishmentManager().unban(uniqueId);
				return;
			}
			if (banData.isTemporarilyBanned()) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§b▄▆█  §6§lAriseMC.de  §b█▆▄\n\n" +
						"§7Du wurdest §cTEMPORÄR §7gebannt!\n\n" +
						"§7Von: §e" + banData.getBannedBy() + "\n\n" +
						"§7Grund: §e" + banData.getBanReason() + "\n\n" +
						"§7Zeitpunkt: §e" + Utils.formatDate(banData.getBanTimestamp()) + "\n\n" +
						"§7Dein Ban läuft bis: §e" + Utils.formatDate(banData.getBanTime()) + "\n\n" +
						"§7Du kannst auf §ehttp://arisemc.de/forum §7einen Entbannungsantrag stellen!" + "\n\n");
				return;
			}
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§b▄▆█  §6§lAriseMC.de  §b█▆▄\n\n" +
					"§7Du wurdest §4PERMANENT §7gebannt!\n\n" +
					"§7Von: §e" + banData.getBannedBy() + "\n\n" +
					"§7Grund: §e" + banData.getBanReason() + "\n\n" +
					"§7Zeitpunkt: §e" + Utils.formatDate(banData.getBanTimestamp()) + "\n\n" +
					"§7Du kannst auf §ehttp://arisemc.de/forum §7einen Entbannungsantrag stellen!" + "\n\n");
		}
	}
}
