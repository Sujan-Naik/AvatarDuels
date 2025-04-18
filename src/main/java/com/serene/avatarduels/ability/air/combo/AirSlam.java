package com.serene.avatarduels.ability.air.combo;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.util.ThrownEntityTracker;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.ability.util.ComboUtil;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.region.RegionProtection;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class AirSlam extends AirAbility implements AddonAbility, ComboAbility {

	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.KNOCKBACK)
	private double power;
	@Attribute(Attribute.RANGE)
	private int range;

	private LivingEntity target;

	public AirSlam(Player player) {
		super(player);
		
		if (!bPlayer.canBendIgnoreBinds(this)) {
			return;
		}
		
		setFields();

		Entity target = GeneralMethods.getTargetedEntity(player, range, new ArrayList<>());
		if (!(target instanceof LivingEntity)
				|| RegionProtection.isRegionProtected(this, target.getLocation())
				|| ((target instanceof Player) && Commands.invincible.contains(target.getName())))
			return;
		this.target = (LivingEntity) target;

		start();
		if (!isRemoved()) {
			bPlayer.addCooldown(this);
			GeneralMethods.setVelocity(this, target, new Vector(0, 2, 0));
		}
	}
	
	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());

		cooldown = config.getLong("Abilities.Air.AirCombo.AirSlam.Cooldown");
		power = config.getDouble("Abilities.Air.AirCombo.AirSlam.Power");
		range = config.getInt("Abilities.Air.AirCombo.AirSlam.Range");
	}

	@Override
	public void progress() {
		if (player == null || player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		if (System.currentTimeMillis() > getStartTime() + 50) {
			Vector dir = player.getLocation().getDirection();
			GeneralMethods.setVelocity(this, target, new Vector(dir.getX(), 0.05, dir.getZ()).multiply(power));
			new HorizontalVelocityTracker(target, player, 0L, this);
			new ThrownEntityTracker(this, target, player, 0L);
			target.setFallDistance(0);
		}
		if (System.currentTimeMillis() > getStartTime() + 400) {
			remove();
			return;
		}
		playAirbendingParticles(target.getLocation(), 10);
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return target != null ? target.getLocation() : null;
	}

	@Override
	public String getName() {
		return "AirSlam";
	}
	
	@Override
	public boolean isHiddenAbility() {
		return false;
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
	public Object createNewComboInstance(Player player) {
		return new AirSlam(player);
	}

	@Override
	public ArrayList<AbilityInformation> getCombination() {
		return ComboUtil.generateCombinationFromList(this, AvatarDuelsConfig.getConfig(player).getStringList("Abilities.Air.AirCombo.AirSlam.Combination"));
	}

	@Override
	public String getInstructions() {
		return AvatarDuelsConfig.getConfig(player).getString("Abilities.Air.AirCombo.AirSlam.Instructions");
	}

	@Override
	public String getDescription() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return "* AvatarDuels Addon *\n" + config.getString("Abilities.Air.AirCombo.AirSlam.Description");
	}
	
	@Override
	public String getAuthor() {
		return AvatarDuels.dev;
	}

	@Override
	public String getVersion() {
		return AvatarDuels.version;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public void setTarget(LivingEntity target) {
		this.target = target;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return config.getBoolean("Abilities.Air.AirCombo.AirSlam.Enabled");
	}
}