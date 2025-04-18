package com.serene.avatarduels.ability.earth;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.attribute.Attribute;

public class LandLaunch extends EarthAbility implements AddonAbility, PassiveAbility {

	@Attribute("Power")
	private int amp;
	
	public LandLaunch(Player player) {
		super(player);
		
		amp = AvatarDuels.getConfig(getName()).getInt("Passives.Earth.LandLaunch.Power");
	}

	@Override
	public void progress() {
		if (bPlayer.isElementToggled(Element.EARTH) && player.isOnGround() && isEarthbendable(player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 10, amp));
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
		return "LandLaunch";
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
		return AvatarDuels.getConfig(getName()).getBoolean("Passives.Earth.LandLaunch.Enabled");
	}
	
	@Override
	public String getInstructions() {
		return "Jump while on earth";
	}
	
	@Override
	public String getDescription() {
		return "Skilled earthbenders can boost their jumps by bending the earth right below their feet!";
	}
}
