package com.serene.avatarduels.ability.water;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;

public class Hydrojet extends WaterAbility implements AddonAbility, PassiveAbility {

	@Attribute(Attribute.SPEED)
	private int amp;
	
	public Hydrojet(Player player) {
		super(player);
		
		amp = AvatarDuels.getConfig(getName()).getInt("Passives.Water.Hydrojet.Speed");
		
		start();
	}

	@Override
	public void progress() {
		if (bPlayer.isElementToggled(Element.WATER) && player.isSwimming()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 25, amp));
		}
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
		return "Hydrojet";
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}

	@Override
	public boolean isProgressable() {
		return true;
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
		return AvatarDuels.getConfig(getName()).getBoolean("Passives.Water.Hydrojet.Enabled");
	}
	
	@Override
	public String getInstructions() {
		return "Go swimming";
	}
	
	@Override
	public String getDescription() {
		return "Skilled waterbenders can boost their swimming speed by bending the water currents around them!";
	}
}
