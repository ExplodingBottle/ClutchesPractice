package io.github.ascpialgroup.clp.listeners;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.ascpialgroup.clp.ClutchesPracticePlugin;
import io.github.ascpialgroup.clp.configuration.LoadedConfiguration;
import io.github.ascpialgroup.clp.configuration.Translations;

public class CommandHandler implements CommandExecutor {
	private ClutchesPracticePlugin referer;
	private LoadedConfiguration config;
	private Translations lTrans;

	public CommandHandler(ClutchesPracticePlugin referer, LoadedConfiguration config, Translations lTrans) {
		this.referer = referer;
		this.config = config;
		this.lTrans = lTrans;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(lTrans.commandNotExisting);
			return true;
		}
		switch (args[0]) {
		case "summonhelper":
			if (sender.hasPermission("clp.summonhelper")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(lTrans.consoleSummoning);
					referer.getLogger().log(Level.INFO, "A non player has tried to summon an helper.");
				} else {
					Player ref = (Player) sender;
					if (config.activeWorlds.contains(ref.getWorld().getName())) {
						ArmorStand a = (ArmorStand) ref.getWorld().spawnEntity(ref.getLocation(),
								EntityType.ARMOR_STAND);
						ItemStack playerHead = new ItemStack(Material.SKULL_ITEM);
						a.setHelmet(playerHead);
						a.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
						a.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
						a.setBoots(new ItemStack(Material.LEATHER_BOOTS));
						a.setArms(true);
						a.setItemInHand(new ItemStack(Material.STICK));
						a.setCustomName(lTrans.punchMe);
						a.setCustomNameVisible(true);
						referer.registerNewArmorStandIfNotExisting(a.getUniqueId());
						sender.sendMessage(lTrans.helperAdded);
						referer.getLogger().log(Level.INFO, "An helper has been added.");
					} else {
						sender.sendMessage(lTrans.nonActiveWorld.replace("${world}", ref.getWorld().getName()));
						referer.getLogger().log(Level.INFO, "Someone tried to summon an helper in a non active world.");
					}

				}
			} else {
				sender.sendMessage(lTrans.noPermissions.replace("${perm}", "clp.summonhelper"));
			}
			break;
		case "help":
			sender.sendMessage(lTrans.commandHelpMessage);
			break;
		default:
			sender.sendMessage(lTrans.commandNotExisting);
			break;
		}
		return true;
	}

}
