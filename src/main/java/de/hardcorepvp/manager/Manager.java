package de.hardcorepvp.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Manager {

	private boolean pvp;
	private int globalmute;
	private List<Player> vanishedPlayers;
	private List<Player> activeItemfilter;
	private List<Player> autoSellList;
	private ConcurrentHashMap<String, Long> tpaCooldownList;
	private HashMap<String, String> currentTpaRequest;
	private HashMap<String, String> currentTpahereRequest;

	public Manager() {
		this.pvp = true;
		this.globalmute = 0;
		this.vanishedPlayers = new ArrayList<>();
		this.tpaCooldownList = new ConcurrentHashMap<>();
		this.currentTpaRequest = new HashMap<>();
		this.currentTpahereRequest = new HashMap<>();
		this.activeItemfilter = new ArrayList<>();
		this.autoSellList = new ArrayList<>();
	}

	public boolean isPvP() {
		return pvp;
	}

	public int getGlobalmute() {
		return globalmute;
	}

	public List<Player> getVanishedPlayers() {
		return vanishedPlayers;
	}

	public List<Player> getItemfilterPlayers() {
		return activeItemfilter;
	}

	public List<Player> getAutosellPlayers() {
		return autoSellList;
	}

	public void setPvP(boolean pvp) {
		this.pvp = pvp;
	}

	public void setGlobalmute(int globalmute) {
		this.globalmute = globalmute;
	}

	public boolean isVanished(Player player) {
		return this.vanishedPlayers.contains(player);
	}

	public boolean isItemfiltered(Player player) {
		return this.activeItemfilter.contains(player);
	}

	public boolean isAutoselling(Player player) {
		return this.autoSellList.contains(player);
	}

	public void addVanishedPlayer(Player player) {
		this.vanishedPlayers.add(player);
	}

	public void removeVanishedPlayer(Player player) {
		this.vanishedPlayers.remove(player);
	}

	public void addItemfilteredPlayer(Player player) {
		this.activeItemfilter.add(player);
	}

	public void removeItemfilteredPlayer(Player player) {
		this.activeItemfilter.remove(player);
	}

	public void addAutoSellingPlayer(Player player) {
		this.autoSellList.add(player);
	}

	public void removeAutoSellingPlayer(Player player) {
		this.autoSellList.remove(player);
	}

	public boolean canBypassGlobalmute(Player player) {
		//TODO DO STUFF BASED ON PERMISSION -> 0 = ALL, 1 = team, 2 = admins
		if (this.globalmute == 2) {
			return false;
		}
		return this.globalmute != 1;
	}

	public void sendTpaRequest(Player sender, Player recipient) {
		if (currentTpahereRequest.values().contains(sender.getName())) {
			Bukkit.broadcastMessage("test1");
			currentTpahereRequest.values().remove(sender.getName());
		}
		if (currentTpaRequest.values().contains(sender.getName())) {
			Bukkit.broadcastMessage("test2");
			currentTpaRequest.values().remove(sender.getName());
		}
		currentTpaRequest.put(recipient.getName(), sender.getName());
	}

	//reciever -> key sender -> value
	public void sendTpahereRequest(Player sender, Player recipient) {
		if (currentTpahereRequest.values().contains(sender.getName())) {
			Bukkit.broadcastMessage("test1");
			currentTpahereRequest.values().remove(sender.getName());
		}
		if (currentTpaRequest.values().contains(sender.getName())) {
			Bukkit.broadcastMessage("test2");
			currentTpaRequest.values().remove(sender.getName());
		}
		currentTpahereRequest.put(recipient.getName(), sender.getName());
	}

	public void killTpaRequest(String key) {
		if (currentTpaRequest.containsKey(key)) {
			Player player = Bukkit.getPlayer(currentTpaRequest.get(key));
			if (player != null) {
				player.sendMessage("Deine Anfrage ist abgelaufen");
			}
			currentTpaRequest.remove(key);
		}
	}

	public void killTpahereRequest(String key) {
		if (currentTpahereRequest.containsKey(key)) {
			Player player = Bukkit.getPlayer(currentTpahereRequest.get(key));
			if (player != null) {
				player.sendMessage("Deine Anfrage ist abgelaufen");
			}
			currentTpahereRequest.remove(key);
		}
	}

	public ConcurrentHashMap<String, Long> getTpaCooldown() {
		return tpaCooldownList;
	}

	public HashMap<String, String> getCurrentTpaRequest() {
		return currentTpaRequest;
	}

	public HashMap<String, String> getCurrentTpahereRequest() {
		return currentTpahereRequest;
	}

	public void removePlayer(Player player) {
		this.vanishedPlayers.remove(player);
	}
}
