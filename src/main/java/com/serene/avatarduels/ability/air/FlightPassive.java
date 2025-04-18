package com.serene.avatarduels.ability.air;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FlightAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;

public class FlightPassive extends FlightAbility implements AddonAbility, PassiveAbility {

	private boolean toggled = false, active = false, pickup;
	private float original = 0.8f, speed, flySpeed, startSpeed, maxSpeed, acceleration;

	public FlightPassive(Player player) {
		super(player);

		flySpeed = (float) AvatarDuels.getConfig(getName()).getDouble("Passives.Air.Flying.FlySpeed");
		speed = startSpeed = (float) AvatarDuels.getConfig(getName()).getDouble("Passives.Air.Flying.Glide.StartSpeed");
		maxSpeed = (float) AvatarDuels.getConfig(getName()).getDouble("Passives.Air.Flying.Glide.MaxSpeed");
		acceleration = (float) AvatarDuels.getConfig(getName()).getDouble("Passives.Air.Flying.Acceleration");

		flightHandler.createInstance(player, "FlightPassive");
	}

	@Override
	public void progress() {
		if (!bPlayer.isElementToggled(Element.AIR)) {
			player.setAllowFlight(false);
			clear();
			return;
		}

		if (player.getLocation().getBlock().isLiquid()) {
			clear();
			return;
		}

		player.setAllowFlight(true);

		if (active && toggled) {
			player.setGliding(true);

			if (player.isSneaking() && player.getFlySpeed() < maxSpeed) {
				speed = speed + (float) acceleration;
				if (speed > maxSpeed) {
					speed = (float) maxSpeed;
				}
			}

			player.setVelocity(player.getEyeLocation().getDirection().multiply(speed));
		}
	}

	private void clear() {
		active = false;
		toggled = false;
		player.setFlying(false);
		player.setGliding(false);
	}

	@Override
	public void remove() {
		super.remove();
		clear();
		flightHandler.removeInstance(player, "FlightPassive");
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
		return "Flying";
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public boolean isInstantiable() {
		return isEnabled();
	}

	@Override
	public boolean isProgressable() {
		return true;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}

	@Override
	public String getAuthor() {
		return "AvatarDuels";
	}

	@Override
	public String getVersion() {
		return AvatarDuels.instance.version();
	}

	public void toggleGlide() {
		this.toggled = !toggled;
		if (!toggled) {
			player.setGliding(false);
			player.setFlying(true);
			speed = startSpeed;
		} else {
			player.setGliding(true);
			player.setFlying(false);
		}
	}

	public void fly(boolean flying) {
		if (flying) {
			active = true;
			original = player.getFlySpeed();
			pickup = player.getCanPickupItems();
			player.setFlySpeed(flySpeed);
			player.setCanPickupItems(false);
		} else {
			player.setFlySpeed(original);
			player.setCanPickupItems(pickup);
			active = false;
		}
		toggled = false;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isGliding() {
		return toggled;
	}

	@Override
	public boolean isEnabled() {
		return false;
//		return AvatarDuels.getConfig(getName()).getBoolean("Passives.Air.Flying.Enabled");
	}

	@Override
	public String getDescription() {
		return "A very rare ability for airbenders is being able to fly freely, without the need of any glider. The only airbenders known to have this ability were Guru Laghima and Zaheer.";
	}

	@Override
	public String getInstructions() {
		return "Use double jump to toggle flight, offhand swap to toggle between gliding and creative flight, and sneak while gliding to accelerate!";
	}
}
