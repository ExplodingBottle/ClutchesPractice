package io.github.ascpialgroup.clp.tasks;

import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class ArmMovementResetTask extends BukkitRunnable {
	private ArmorStand a;

	public ArmMovementResetTask(ArmorStand a) {
		this.a = a;
	}

	@Override
	public void run() {
		a.setRightArmPose(new EulerAngle(0, 0, 0));
	}

}
