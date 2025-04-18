package com.serene.avatarduels.ability.avatar.elementsphere;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.util.RegenTempBlock;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ESWater extends AvatarAbility implements AddonAbility {

	private Location location;
	private Vector direction;
	private double travelled;

	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.RANGE)
	private double range;
	@Attribute(Attribute.DAMAGE)
	private double damage;
	@Attribute(Attribute.SPEED)
	private int speed;

	public ESWater(Player player) {
		super(player);
		if (!hasAbility(player, ElementSphere.class)) {
			return;
		}
		ElementSphere currES = getAbility(player, ElementSphere.class);
		if (currES.getWaterUses() == 0) {
			return;
		}
		if (bPlayer.isOnCooldown("ESWater")) {
			return;
		}
		setFields();
		location = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(1));
		start();
		if (!isRemoved()) {
			bPlayer.addCooldown("ESWater", getCooldown());
			currES.setWaterUses(currES.getWaterUses() - 1);
		}
	}

	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig("ElementSphere");

		cooldown = config.getLong("Abilities.Avatar.ElementSphere.Water.Cooldown");
		range = config.getDouble("Abilities.Avatar.ElementSphere.Water.Range");
		damage = config.getDouble("Abilities.Avatar.ElementSphere.Water.Damage");
		speed = config.getInt("Abilities.Avatar.ElementSphere.Water.Speed");
	}
	
	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		if (travelled >= range) {
			remove();
			return;
		}
		advanceAttack();
	}

	private void advanceAttack() {
		for (int i = 0; i < speed; i++) {
			travelled++;
			if (travelled >= range)
				return;

			if (!player.isDead())
				direction = GeneralMethods.getDirection(player.getLocation(), GeneralMethods.getTargetedLocation(player, range, Material.WATER)).normalize();
			location = location.add(direction.clone().multiply(1));
			if (RegionProtection.isRegionProtected(this, location)){
				travelled = range;
				return;
			}
			if (GeneralMethods.isSolid(location.getBlock()) || !isTransparent(location.getBlock())) {
				travelled = range;
				return;
			}

			WaterAbility.playWaterbendingSound(location);
			if (isWater(location.getBlock()))
				ParticleEffect.WATER_BUBBLE.display(location, 3, 0.5, 0.5, 0.5);
			new RegenTempBlock(location.getBlock(), Material.WATER, Material.WATER.createBlockData(bd -> ((Levelled) bd).setLevel(0)), 100L);

			for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2.5)) {
				if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId() && !(entity instanceof ArmorStand) && !RegionProtection.isRegionProtected(this, entity.getLocation()) && !((entity instanceof Player) && Commands.invincible.contains(((Player) entity).getName()))) {
					DamageHandler.damageEntity(entity, damage, this);
					travelled = range;
				}
			}
		}
	}
	
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return "ElementSphereWater";
	}
	
	@Override
	public boolean isHiddenAbility() {
		return true;
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
		return null;
	}

	public Vector getDirection() {
		return direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}

	public double getDistanceTravelled() {
		return travelled;
	}

	public void setDistanceTravelled(double travelled) {
		this.travelled = travelled;
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

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuels.getConfig("ElementSphere");
		return config.getBoolean("Abilities.Avatar.ElementSphere.Enabled");
	}
}
