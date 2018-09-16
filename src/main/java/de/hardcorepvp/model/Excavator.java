package de.hardcorepvp.model;

import de.hardcorepvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Excavator {

	private BukkitTask task;
	private ConcurrentLinkedQueue<Block> blockQueue;
	private int blocksPerCycle;
	private long delay;
	private long period;

	public Excavator(ConcurrentLinkedQueue<Block> blockQueue, int blocksPerCycle, long delay, long period) {
		this.blockQueue = blockQueue;
		this.blocksPerCycle = blocksPerCycle;
		this.delay = delay;
		this.period = period;
	}

	public void start() {
		this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				for (int i = 0; i < blocksPerCycle; i++) {
					Block block = blockQueue.poll();
					if (block == null) {
						stop();
						return;
					}
					block.setType(Material.AIR);
				}
			});
		}, this.delay, this.period);
	}

	public void stop() {
		if (this.task == null) {
			return;
		}
		this.task.cancel();
	}
}
