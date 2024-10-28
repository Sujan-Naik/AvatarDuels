package com.serene.avatarduels.ability.air;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.collision.CollisionDetector;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.airbending.AirSpout;

import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirGlide extends AirAbility implements AddonAbility {

	// The player must touch the ground for the cooldown to start if this is true.
	private boolean requireGround;
	@Attribute(Attribute.SPEED)
	private double speed;
	private double fallSpeed;
	private int particles;
	private boolean airspout;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.DURATION)
	private long duration;
	private long lastCooldown;
	private boolean progressing;

	public AirGlide(Player player) {
		super(player);

		if (hasAbility(player, AirGlide.class)) {
			AirGlide ag = getAbility(player, AirGlide.class);
			ag.remove();
			return;
		}

		if (bPlayer.isOnCooldown(this) || CollisionDetector.isOnGround(player)) {
			return;
		}

		setFields();

		this.progressing = true;

		start();
	}

	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());

		speed = config.getDouble("Abilities.Air.AirGlide.Speed");
		fallSpeed = config.getDouble("Abilities.Air.AirGlide.FallSpeed");
		particles = config.getInt("Abilities.Air.AirGlide.Particles");
		airspout = config.getBoolean("Abilities.Air.AirGlide.AllowAirSpout");
		cooldown  = config.getLong("Abilities.Air.AirGlide.Cooldown");
		duration  = config.getLong("Abilities.Air.AirGlide.Duration");
		requireGround = config.getBoolean("Abilities.Air.AirGlide.RequireGround") && cooldown > 0;
	}
	
	public void progress() {
		long time = System.currentTimeMillis();

		if (this.progressing) {
			update(time);
		} else {
			if (player.isDead() || !player.isOnline()) {
				this.requireGround = false;
				remove();
				return;
			}

			if (CollisionDetector.isOnGround(this.player)) {
				// Flip this so remove() actually removes the instance.
				this.requireGround = false;
				remove();
			} else {
				// Limit how frequently addCooldown is called so bending board isn't spammed with updates.
				if (time > lastCooldown + cooldown / 2) {
					// Keep resetting the cooldown until the player touches the ground.
					bPlayer.addCooldown(this);
					lastCooldown = time;
				}
			}
		}
	}

	private void update(long time) {
		if (this.duration > 0 && time >= this.getStartTime() + this.duration) {
			remove();
			return;
		}

		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}

		if (!hasAbility(player, AirGlide.class)) {
			remove();
			return;
		}

		if ((airspout && hasAbility(player, AirSpout.class)) || !hasAirGlide()) {
			remove();
			return;
		}

		if (!player.isOnGround()) {
			Location firstLocation = player.getEyeLocation();
			Vector directionVector = firstLocation.getDirection().normalize();
			double distanceFromPlayer = speed;
			Vector shootFromPlayer = new Vector(directionVector.getX() * distanceFromPlayer, -fallSpeed, directionVector.getZ() * distanceFromPlayer);
			firstLocation.add(shootFromPlayer.getX(), shootFromPlayer.getY(), shootFromPlayer.getZ());

			GeneralMethods.setVelocity(this, player, shootFromPlayer);
			playAirbendingParticles(player.getLocation(), particles);
		} else if (!isTransparent(player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
			remove();
		}
	}

	@Override
	public void remove() {
		this.progressing = false;
		bPlayer.addCooldown(this);

		if (!this.requireGround) {
			super.remove();
		}
	}

	private boolean hasAirGlide() {
		return bPlayer.getAbilities().containsValue("AirGlide");
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public String getName() {
		return "AirGlide";
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public String getAuthor() {
		return AvatarDuels.dev;
	}

	@Override
	public String getVersion() {
		return AvatarDuels.version;
	}

	@Override
	public String getDescription() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return "* AvatarDuels Addon *\n" + config.getString("Abilities.Air.AirGlide.Description");
	}

	public boolean isRequireGround() {
		return requireGround;
	}

	public void setRequireGround(boolean requireGround) {
		this.requireGround = requireGround;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getFallSpeed() {
		return fallSpeed;
	}

	public void setFallSpeed(double fallSpeed) {
		this.fallSpeed = fallSpeed;
	}

	public int getParticles() {
		return particles;
	}

	public void setParticles(int particles) {
		this.particles = particles;
	}

	public boolean allowsAirSpout() {
		return airspout;
	}

	public void setAllowAirSpout(boolean airspout) {
		this.airspout = airspout;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getLastCooldown() {
		return lastCooldown;
	}

	public void setLastCooldown(long lastCooldown) {
		this.lastCooldown = lastCooldown;
	}

	public boolean isProgressing() {
		return progressing;
	}

	public void setProgressing(boolean progressing) {
		this.progressing = progressing;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return config.getBoolean("Abilities.Air.AirGlide.Enabled");
	}
}
