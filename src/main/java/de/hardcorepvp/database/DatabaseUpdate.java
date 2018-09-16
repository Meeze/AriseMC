package de.hardcorepvp.database;

import de.hardcorepvp.Main;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseUpdate {

	private List<Runnable> readyExecutors;
	private boolean isReady;

	public DatabaseUpdate() {
		this.readyExecutors = new CopyOnWriteArrayList<>();
		this.isReady = false;
	}

	public List<Runnable> getReadyExecutors() {
		return readyExecutors;
	}

	public boolean isReady() {
		return isReady;
	}

	public void addReadyExecutor(Runnable runnable) {
		if (this.isReady) {
			Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), runnable);
			return;
		}
		this.readyExecutors.add(runnable);
	}

	public void setReady(boolean ready) {
		this.isReady = ready;
		if (ready) {
			Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
				this.readyExecutors.forEach(Runnable::run);
				this.readyExecutors.clear();
			});
		}
	}
}
