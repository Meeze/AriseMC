package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager {

	private Map<UUID, PermissionAttachment> attachmentMap;

	public PermissionManager() {
		this.attachmentMap = new ConcurrentHashMap<>();
	}

	public PermissionAttachment getAttachment(Player player) {
		return attachmentMap.get(player.getUniqueId());
	}

	public void addAttachment(Player player) {
		this.attachmentMap.put(player.getUniqueId(), player.addAttachment(Main.getInstance()));
	}

	public void removeAttachment(Player player) {
		this.removePermissions(player);
		this.attachmentMap.remove(player.getUniqueId());
	}

	public void addPermissions(Player player, List<String> permissions) {
		PermissionAttachment attachment = this.getAttachment(player);
		for (String permission : permissions) {
			System.out.println("Gesetzte Permission: " + permission);
			attachment.setPermission(permission, true);
		}
	}

	public void removePermissions(Player player) {
		PermissionAttachment attachment = this.getAttachment(player);
		for (String permission : attachment.getPermissions().keySet()) {
			System.out.println("Entfernte Permission: " + permission);
			attachment.setPermission(permission, false);
		}
	}
}
