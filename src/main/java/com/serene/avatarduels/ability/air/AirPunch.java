package com.serene.avatarduels.ability.air;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.collision.CollisionDetector;
import com.serene.avatarduels.collision.Sphere;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.util.Collision;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.DamageHandler;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirPunch extends AirAbility implements AddonAbility {

	private final Map<Location, Double> locations = new ConcurrentHashMap<>();

	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	private long threshold;
	@Attribute(Attribute.RANGE)
	private double range;
	@Attribute(Attribute.COOLDOWN)
	private double damage;
	@Attribute("CollisionRadius")
	private double entityCollisionRadius;

	private int shots;
	private long lastShotTime;

	public AirPunch(Player player) {
		super(player);

		if (!bPlayer.canBend(this)) {
			return;
		}

		if (hasAbility(player, AirPunch.class)) {
			AirPunch ap = getAbility(player, AirPunch.class);
			ap.createShot();
			return;
		}
		
		setFields();

		start();
		if (!isRemoved())
			createShot();
	}
	
	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());

		cooldown = config.getLong("Abilities.Air.AirPunch.Cooldown");
		threshold = config.getLong("Abilities.Air.AirPunch.Threshold");
		shots = config.getInt("Abilities.Air.AirPunch.Shots");
		range = config.getDouble("Abilities.Air.AirPunch.Range");
		damage = config.getDouble("Abilities.Air.AirPunch.Damage");
		entityCollisionRadius = config.getDouble("Abilities.Air.AirPunch.EntityCollisionRadius");
	}

	@Override
	public void progress() {
		progressShots();

		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}

		if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
			prepareRemove();
			return;
		}

		if (shots == 0 || System.currentTimeMillis() > lastShotTime + threshold) {
			prepareRemove();
		}
	}

	private void prepareRemove() {
		if (player.isOnline() && !bPlayer.isOnCooldown(this)) {
			bPlayer.addCooldown(this);
		}

		if (locations.isEmpty()) {
			remove();
		}
	}

	private void createShot() {
		if (shots >= 1) {
			lastShotTime = System.currentTimeMillis();
			shots--;
			locations.put(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.5).normalize()), 0D);
		}
	}

	private void progressShots() {
		for (Location l : locations.keySet()) {
			Location loc = l.clone();
			double dist = locations.get(l);
			boolean cancel = false;
			for (int i = 0; i < 3; i++) {
				dist++;
				if (cancel || dist >= range) {
					cancel = true;
					break;
				}
				loc = loc.add(loc.getDirection().clone().multiply(1));
				if (GeneralMethods.isSolid(loc.getBlock()) || isWater(loc.getBlock()) || RegionProtection.isRegionProtected(player, loc, this)) {
					cancel = true;
					break;
				}

				getAirbendingParticles().display(loc, 2, Math.random() / 5, Math.random() / 5, Math.random() / 5, 0.0);
				playAirbendingSound(loc);

				cancel = CollisionDetector.checkEntityCollisions(player, new Sphere(loc.toVector(), entityCollisionRadius), (entity) -> {
					DamageHandler.damageEntity(entity, damage, this);
					return true;
				});
			}

			if (cancel) {
				locations.remove(l);
			} else {
				locations.remove(l);
				locations.put(loc, dist);
			}
		}
	}
	
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public double getCollisionRadius() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return config.getDouble("Abilities.Air.AirPunch.AbilityCollisionRadius");
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public void handleCollision(Collision collision) {
		if (collision.isRemovingFirst()) {
			Location location = collision.getLocationFirst();

			locations.remove(location);
		}
	}

	@Override
	public List<Location> getLocations() {
		return new ArrayList<>(locations.keySet());
	}

	@Override
	public String getName() {
		return "AirPunch";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
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
		return "* AvatarDuels Addon *\n" + config.getString("Abilities.Air.AirPunch.Description");
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getEntityCollisionRadius() {
		return entityCollisionRadius;
	}

	public void setEntityCollisionRadius(double entityCollisionRadius) {
		this.entityCollisionRadius = entityCollisionRadius;
	}

	public int getShots() {
		return shots;
	}

	public void setShots(int shots) {
		this.shots = shots;
	}

	public long getLastShotTime() {
		return lastShotTime;
	}

	public void setLastShotTime(long lastShotTime) {
		this.lastShotTime = lastShotTime;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return config.getBoolean("Abilities.Air.AirPunch.Enabled");
	}
}
