package com.serene.avatarduels.ability.chi;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ChiAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.attribute.Attribute;

public class Dodging extends ChiAbility implements AddonAbility, PassiveAbility {

	@Attribute("Chance")
	private double chance;
	
	public Dodging(Player player) {
		super(player);
		
		chance = AvatarDuels.instance.getConfig(getName()).getDouble("Passives.Chi.Dodging.Chance") / 100;
	}
	
	public boolean check() {
		return Math.random() < chance;
	}

	@Override
	public void progress() {
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public String getName() {
		return "Dodging";
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}

	@Override
	public boolean isProgressable() {
		return false;
	}

	@Override
	public void load() {
	}

	@Override
	public void stop() {
	}

	@Override
	public String getAuthor() {
		return "AvatarDuels";
	}

	@Override
	public String getVersion() {
		return AvatarDuels.instance.version();
	}

	@Override
	public boolean isEnabled() {
		return AvatarDuels.instance.getConfig(getName()).getBoolean("Passives.Chi.Dodging.Enabled");
	}
	
	@Override
	public String getDescription() {
		return "Graceful but unpredictable movements make chiblockers more difficult to hit, having been taught an innate dodging technique!";
	}
	
	@Override
	public String getInstructions() {
		return "Passively active";
	}
}
