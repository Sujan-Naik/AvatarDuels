package com.serene.avatarduels.ability.water;

import com.serene.avatarduels.JCMethods;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class IcePassive {

	@SuppressWarnings("deprecation")
	public static void handleSkating() {
		Map<World, Pair<Boolean, Integer>> resultCache = new HashMap<>();

		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			Pair<Boolean, Integer> result = resultCache.get(player.getWorld());
			if (result == null) {
				ConfigurationSection config = AvatarDuelsConfig.getConfig(player);

				boolean enabled = config.getBoolean("Abilities.Water.Ice.Passive.Skate.Enabled");
				int speedFactor = config.getInt("Abilities.Water.Ice.Passive.Skate.SpeedFactor");

				result = new Pair<>(enabled, speedFactor);
				resultCache.put(player.getWorld(), result);
			}

			boolean enabled = result.first;
			int speedFactor = result.second;

			if (!enabled) continue;

			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null && bPlayer.canIcebend() && bPlayer.isElementToggled(Element.WATER) && bPlayer.hasElement(Element.WATER) && !JCMethods.isDisabledWorld(player.getWorld())) {
				if (player.isSprinting() && IceAbility.isIce(player.getLocation().getBlock().getRelative(BlockFace.DOWN)) && player.isOnGround()) {
					ParticleEffect.SNOW_SHOVEL.display(player.getLocation().clone().add(0, 0.2, 0), 15, Math.random()/2, Math.random()/2, Math.random()/2, 0);
					player.removePotionEffect(PotionEffectType.SPEED);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, speedFactor));	
				}
			}
		}
	}

	private static class Pair<T, U> {
		T first;
		U second;

		Pair(T first, U second) {
			this.first = first;
			this.second = second;
		}
	}
}
