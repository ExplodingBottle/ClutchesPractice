package io.github.ascpialgroup.clp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {
	private List<String> allPossible;

	public CommandTabCompleter() {
		allPossible = new ArrayList<>();
		allPossible.add("help");
		allPossible.add("summonhelper");
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {

		if (arg3.length == 1) {
			List<String> possible = new ArrayList<>();
			allPossible.forEach(option -> {
				if (option.startsWith(arg3[0]))
					possible.add(option);
			});
			return possible;
		}
		return allPossible;
	}

}
