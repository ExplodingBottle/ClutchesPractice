package io.github.ascpialgroup.clp.tasks;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import io.github.ascpialgroup.clp.configuration.LoadedConfiguration;

public class PushEntityTask extends BukkitRunnable {
	private Player damager;
	private Vector knockPos;
	private LoadedConfiguration config;
	private JavaPlugin referer;
	private ArmorStand a;

	public PushEntityTask(Player damager, Vector knockPos, LoadedConfiguration config, JavaPlugin referer,
			ArmorStand a) {
		this.damager = damager;
		this.knockPos = knockPos;
		this.config = config;
		this.referer = referer;
		this.a = a;
	}

	@Override
	public void run() {
		a.setRightArmPose(new EulerAngle(-1, 0, 0));
		Vector velo = knockPos.subtract(damager.getLocation().toVector()).normalize().multiply(config.jumpForce * -1);
		if (damager.getVelocity().getY() <= 0) {
			velo.setY(config.jumpForce);
		} else {
			velo.setY(damager.getVelocity().getY());
		}
		damager.damage(0);
		damager.setVelocity(velo);
		new ArmMovementResetTask(a).runTaskLaterAsynchronously(referer, 3);
	}

}
