package de.hardcorepvp.listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {

	@EventHandler
	public void onPlayerVote(VotifierEvent event) {
		Vote vote = event.getVote();
		String name = vote.getUsername();
		if (Bukkit.getPlayer(name) == null) {
			Player player = Bukkit.getPlayer(name);
			player.giveExp(1000);
			Bukkit.broadcastMessage(Messages.formatMessage(name + " hat gevoted!"));
		} else {
			Bukkit.broadcastMessage(Messages.formatMessage(name + " hat gevoted ist jedoch Offline!"));
		}
	}
}
