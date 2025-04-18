package com.serene.avatarduels.scoreboard;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.serene.avatarduels.JCMethods;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.configuration.Config;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;

public class BendingBoard {
	private static final List<String> IGNORE_ABILITIES = Arrays.asList("WaterWave", "FastSwim");
	private static final String OTHER = "Other:";
	public static Map<String, ChatColor> otherAbilities = new HashMap<>();
	public static ConcurrentHashMap<Player, BendingBoard> boards = new ConcurrentHashMap<>();
	public static List<UUID> disabled = new ArrayList<>();
	public static boolean enabled;
	public static String title;
	public static String pointer;
	public static String empty;
	public static String toggleOn;
	public static String toggleOff;
	public static String combo;
	public static Config toggled;
	public static boolean disabledworlds;

	static {
		toggled = new Config(new File("/board/players.yml"));

		List<String> uuids = toggled.getConfig().getStringList("Players");
		if (!uuids.isEmpty()) {
			if (!uuids.get(0).matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
				toggled.getConfig().set("Players", new ArrayList<String>());
				toggled.saveConfig();
			} else {
				for (String s : uuids) {
					disabled.add(UUID.fromString(s));
				}
			}
		}
		setFields();
	}

	private Player player;
	private SimpleScoreboard scoreboard;

	public BendingBoard(Player player) {
		this.player = player;
		this.scoreboard = new SimpleScoreboard(title);
		boards.put(player, this);
	}

	public static void setFields() {
		enabled = AvatarDuelsConfig.board.getConfig().getBoolean("Settings.Enabled");
		title = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.Title"));
		pointer = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.Pointer"));
		empty = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.EmptySlot"));
		combo = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.Combos"));
		toggleOn = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.Toggle.On"));
		toggleOff = ChatUtil.color(AvatarDuelsConfig.board.getConfig().getString("Settings.Toggle.Off"));
		disabledworlds = AvatarDuelsConfig.board.getConfig().getBoolean("Settings.Display.DisabledWorlds");
	}

	public static boolean shouldIgnoreAbility(String abilityName) {
		if (abilityName == null) return false;

		return IGNORE_ABILITIES.contains(abilityName);
	}

	public static void loadOtherCooldowns() {
		ConfigurationSection section = AvatarDuelsConfig.board.getConfig().getConfigurationSection("Settings.OtherCooldowns");

		otherAbilities.clear();

		for (String ability : section.getKeys(false)) {
			ConfigurationSection abilitySection = section.getConfigurationSection(ability);
			if (abilitySection == null) continue;

			boolean enabled = abilitySection.getBoolean("Enabled", true);
			if (!enabled) continue;

			String colorString = abilitySection.getString("Color");
			ChatColor color = null;

			if (colorString != null) {
				color = ChatColor.of(colorString);
			}

			otherAbilities.put(ability, color);
		}
	}

	public static void updateOnline() {
		if (!enabled) return;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (disabled.contains(player.getUniqueId())) continue;
			if (!disabledworlds && JCMethods.getDisabledWorlds().contains(player.getWorld().getName())) continue;
			BendingBoard.get(player).update();
		}
	}

	public static void toggle(Player player) {
		if (!enabled) return;
		List<String> uuids = new ArrayList<>(toggled.getConfig().getStringList("Players"));
		if (uuids.contains(player.getUniqueId().toString())) {
			uuids.remove(player.getUniqueId().toString());
			disabled.remove(player.getUniqueId());
			get(player).update();
			player.sendMessage(toggleOn);
		} else {
			uuids.add(player.getUniqueId().toString());
			disabled.add(player.getUniqueId());
			get(player).remove();
			player.sendMessage(toggleOff);
		}
		toggled.getConfig().set("Players", uuids);
		toggled.saveConfig();
	}

	public static void update(Player player) {
		update(player, -1);
	}

	public static void update(Player player, int slot) {
		if (!enabled) { 
			if (disabled.contains(player.getUniqueId())) return;
			if (!boards.containsKey(player)) return;
			get(player).remove();
			return;
		}
		if (disabled.contains(player.getUniqueId())) return;
		if (!disabledworlds && JCMethods.getDisabledWorlds().contains(player.getWorld().getName())) {
			if (boards.containsKey(player)) {
				get(player).remove();
			}
			return;
		}
		get(player).update(slot);
	}

	public static BendingBoard get(Player player) {
		if (!enabled) return null;
		if (boards.containsKey(player)) {
			return boards.get(player);
		} else {
			return new BendingBoard(player);
		}
	}
	
	public static boolean isDisabled(Player player) {
		return !enabled || disabled.contains(player.getUniqueId());
	}
	
	public void remove() {
		scoreboard.reset();
		boards.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public void update() {
		update(-1);
	}

	public void update(final int slot) {
		if (!enabled) return;

		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null) return;

		// Check if the player removed their bending board. It's possible for the board to update after hidden.
		if (boards.get(player) == null) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			return;
		}

		HashMap<Integer, String> abilities = bPlayer.getAbilities();

		int currentSlot = slot;
		if (currentSlot < 0) {
			currentSlot = player.getInventory().getHeldItemSlot();
		}

		List<String> formatted = new ArrayList<>();

		for (int slotIndex = 1; slotIndex < 10; slotIndex++) {
			CoreAbility currentAbility = CoreAbility.getAbility(abilities.get(slotIndex));
			String currentAbilityName = abilities.get(slotIndex);
			StringBuilder sb = new StringBuilder();

			if (currentSlot == (slotIndex - 1)) {
				sb.append(pointer);
			}

			if (abilities.containsKey(slotIndex) && currentAbility != null) {
				for (String str : formatted) {
					String stripped = ChatColor.stripColor(str).replace(pointer, "");

					if (stripped.equalsIgnoreCase(currentAbilityName)) {
						// Add a unique chat color to the beginning, so the ability doesn't override other slots in the map.
						sb.append(ChatColor.getByChar((char) slotIndex));
						break;
					}
				}

				sb.append(currentAbility.getElement().getColor());

				if (bPlayer.isOnCooldown(currentAbilityName)) {
					sb.append(ChatColor.STRIKETHROUGH);
				}

				sb.append(currentAbilityName);
			} else {
				if (abilities.containsKey(slotIndex) && MultiAbilityManager.hasMultiAbilityBound(player)) {
					sb.append(abilities.get(slotIndex));
				} else {
					sb.append(empty.replace("%", String.valueOf(slotIndex)));
				}
			}
			formatted.add(sb.toString());
		}
		boolean combo = false;
		for (String ability : bPlayer.getCooldowns().keySet()) {
			if (JCMethods.getCombos().contains(ability)) {
				if (!combo) {
					formatted.add(BendingBoard.combo);
				}

				combo = true;
				formatted.add("" + CoreAbility.getAbility(ability).getElement().getColor() + ChatColor.STRIKETHROUGH + ability);
			}
		}

		if (!otherAbilities.isEmpty()) {
			boolean other = false;
			for (String ability : bPlayer.getCooldowns().keySet()) {
				if (!otherAbilities.containsKey(ability)) continue;
				ChatColor color = otherAbilities.get(ability);

				if (!other) {
					formatted.add(OTHER);
					other = true;
				}

				if (color == null)
					color = getColor(ability);
				formatted.add("" + color + ChatColor.STRIKETHROUGH + ability);
			}
		}

		if (scoreboard.get(-10, "") != null) {
			for (int i = -9; i > -22; i--) {
				scoreboard.remove(i, "");
			}
		}

		for (String s : formatted) {
			scoreboard.add(s, -(formatted.indexOf(s) + 1));
		}

		scoreboard.update();
		scoreboard.send(player);
	}

	private ChatColor getColor(String abilityName) {
		CoreAbility ability = CoreAbility.getAbility(abilityName);
		if (ability != null)
			return ability.getElement().getColor();
		return ChatColor.WHITE;
	}
}
