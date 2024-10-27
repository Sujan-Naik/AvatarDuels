package com.serene.avatarduels.listener;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.command.AvatarDuelsCommand;
import com.serene.avatarduels.event.PKCommandEvent;
import com.serene.avatarduels.event.PKCommandEvent.CommandType;
import com.serene.avatarduels.scoreboard.BendingBoard;
import com.projectkorra.projectkorra.command.PKCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

public class CommandListener implements Listener {

	AvatarDuels plugin;
	String[] cmdaliases = {"/bending", "/bend", "/b", "/pk", "/projectkorra", "/korra", "/mtla", "/tla"};
	public static String[] developers = {
			"4eb6315e-9dd1-49f7-b582-c1170e497ab0", //jedk1
			"d57565a5-e6b0-44e3-a026-979d5de10c4d", //s3xi
			"e98a2f7d-d571-4900-a625-483cbe6774fe", //Aztl
			"b6bd2ceb-4922-4707-9173-8a02044e9069" //Cozmyc
	};

	public CommandListener(AvatarDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().toLowerCase();
		String[] args = cmd.split("\\s+");
		if (Arrays.asList(cmdaliases).contains(args[0]) && args.length >= 2) {
			PKCommandEvent new_event = new PKCommandEvent(event.getPlayer(), args, null);
			for (PKCommand command : PKCommand.instances.values()) {
				if (Arrays.asList(command.getAliases()).contains(args[1].toLowerCase())) {
					new_event = new PKCommandEvent(event.getPlayer(), args, CommandType.getType(command.getName()));
				}
			}
			Bukkit.getServer().getPluginManager().callEvent(new_event);
		}
	}

	CommandType[] types = {CommandType.ADD, CommandType.BIND, CommandType.CHOOSE, CommandType.CLEAR, CommandType.PRESET, CommandType.REMOVE};

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPKCommand(final PKCommandEvent event) {
		new BukkitRunnable() {
			public void run() {
				if (event.getType() != null) {
					if (Arrays.asList(types).contains(event.getType())) {
						Player player = event.getSender();
						if (BendingBoard.isDisabled(player)) return;
						BendingBoard.get(player).update();
					}
					if (event.getType().equals(CommandType.WHO) && event.getSender().hasPermission(".command.who")) {
						if (event.getArgs().length == 3) {
							if (Bukkit.getPlayer(event.getArgs()[2]) != null) {
								UUID uuid = Bukkit.getPlayer(event.getArgs()[2]).getUniqueId();
								if (Arrays.asList(developers).contains(uuid.toString())) {
									event.getSender().sendMessage(ChatColor.DARK_AQUA + "AvatarDuels Developer");
								}
							}
						}
						return;
					}
					if (event.getType().equals(CommandType.VERSION) && event.getSender().hasPermission(".command.version")) {
						AvatarDuelsCommand.sendBuildInfo(event.getSender());
					}
				}
			}
		}.runTaskLater(AvatarDuels.plugin, 1);
	}
}
