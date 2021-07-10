package io.github.ascpialgroup.clp.tasks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockRemoverTask extends BukkitRunnable {
	private Block toRemove;

	public BlockRemoverTask(Block toRemove) {
		this.toRemove = toRemove;
	}

	@Override
	public void run() {
		toRemove.setType(Material.AIR);

	}

}
