package io.github.ascpialgroup.clp.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import io.github.ascpialgroup.clp.ClutchesPracticePlugin;
import io.github.ascpialgroup.clp.configuration.LoadedConfiguration;
import io.github.ascpialgroup.clp.configuration.Translations;
import io.github.ascpialgroup.clp.tasks.BlockRemoverTask;
import io.github.ascpialgroup.clp.tasks.PushEntityTask;

public class EventsHandlerClp implements Listener {

	private ClutchesPracticePlugin referer;
	private LoadedConfiguration config;
	private Translations lTrans;

	public EventsHandlerClp(ClutchesPracticePlugin referer, LoadedConfiguration config, Translations lTrans) {
		this.referer = referer;
		this.config = config;
		this.lTrans = lTrans;
	}

	@EventHandler
	public void onItemTookFromArmorStand(PlayerArmorStandManipulateEvent e) {
		if (config.activeWorlds.contains(e.getRightClicked().getWorld().getName())) {
			if (referer.isArmorStandExisting(e.getRightClicked().getUniqueId()))
				e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
		if (config.activeWorlds.contains(e.getEntity().getWorld().getName())) {
			if (e.getDamager().getType() == EntityType.PLAYER & e.getEntityType() == EntityType.ARMOR_STAND) {
				Player damager = (Player) e.getDamager();
				if (damager.getGameMode() != GameMode.CREATIVE) {
					if (referer.isArmorStandExisting(e.getEntity().getUniqueId())) {

						ArmorStand a = (ArmorStand) e.getEntity();
						PushEntityTask task = new PushEntityTask(damager, e.getEntity().getLocation().toVector(),
								config, referer, a);
						e.setCancelled(true);
						task.runTaskLater(referer, config.firstKnockbackDelayTicks);
					}
				} else {
					if (damager.hasPermission("clp.removehelper")) {
						referer.removeArmorStandIfExisting(e.getEntity().getUniqueId());
						e.getDamager().sendMessage(lTrans.helperRemoved);
					} else {
						e.setCancelled(true);
						damager.sendMessage(lTrans.noPermissions.replace("${perm}", "clp.removehelper"));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlaced(BlockPlaceEvent e) {
		if (config.activeWorlds.contains(e.getBlock().getWorld().getName())) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				BlockRemoverTask task = new BlockRemoverTask(e.getBlock());
				referer.delayedTasks.add(task);
				task.runTaskLater(referer, config.deletionDelayTicks);
			}
			if (e.getBlock().getLocation()
					.distance(e.getBlock().getWorld().getSpawnLocation()) < config.blocksAllowed) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(lTrans.nearSpawnPlacing);
			}

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDamaged(EntityDamageEvent e) {
		if (e.getEntityType() == EntityType.PLAYER & config.preventPlayerDamages) {
			if (config.activeWorlds.contains(e.getEntity().getWorld().getName())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void foodLevelChanged(FoodLevelChangeEvent e) {
		if (e.getEntityType() == EntityType.PLAYER & config.preventPlayerDamages) {
			if (config.activeWorlds.contains(e.getEntity().getWorld().getName())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		if (config.activeWorlds.contains(e.getPlayer().getWorld().getName())) {
			if (e.getTo().getBlockY() <= config.failedHeight & e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				e.getPlayer().sendMessage(lTrans.tryAgain);

				resetPlayer(e.getPlayer());
			}

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		if (config.activeWorlds.contains(e.getPlayer().getWorld().getName())) {
			// Joining world
			config.commandsToExecJoin.forEach(command -> {
				e.getPlayer().performCommand(command);
			});
			resetPlayer(e.getPlayer());
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getFrom().getWorld() != e.getTo().getWorld()) {
			if (config.activeWorlds.contains(e.getFrom().getWorld().getName())) {
				if (!config.activeWorlds.contains(e.getTo().getWorld().getName())) {
					// Leaving world
					e.getPlayer().getInventory().clear();
					config.commandsToExecLeave.forEach(command -> {
						e.getPlayer().performCommand(command);
					});
				}
			}
		}
	}

	private void resetPlayer(Player p) {
		p.teleport(p.getWorld().getSpawnLocation());
		p.getInventory().clear();
		p.getInventory().addItem(new ItemStack(config.clutchBlockType, config.clutchBlocksAmount));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWeatherChange(WeatherChangeEvent e) {
		if (config.activeWorlds.contains(e.getWorld().getName())) {
			if (config.cancelWeatherChanges) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (config.activeWorlds.contains(e.getPlayer().getWorld().getName())) {
			if (!e.getPlayer().hasPermission("clp.breakblocks")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(lTrans.noPermissions.replace("${perm}", "clp.breakblocks"));
			}
		}
	}

}
