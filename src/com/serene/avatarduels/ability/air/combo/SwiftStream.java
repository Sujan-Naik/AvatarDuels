package com.serene.avatarduels.ability.air.combo;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.FlightAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.ability.util.ComboUtil;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SwiftStream extends FlightAbility implements AddonAbility, ComboAbility {

	@Attribute(Attribute.COOLDOWN)
	public long cooldown;
	public double dragFactor;
	@Attribute(Attribute.DURATION)
	public long duration;

	private final List<LivingEntity> affectedEntities = new ArrayList<>();

	public SwiftStream(Player player) {
		super(player);
		if (!bPlayer.canBendIgnoreBinds(this) || !bPlayer.canUseFlight()) {
			return;
		}

		setFields();
		start();
		if (!isRemoved()) {
			launch();
			bPlayer.addCooldown(this);
		}
	}
	
	public void setFields() {
		ConfigurationSection config = AvatarDuelsConfig.getConfig(this.player);

		cooldown = config.getLong("Abilities.Air.AirCombo.SwiftStream.Cooldown");
		dragFactor = config.getDouble("Abilities.Air.AirCombo.SwiftStream.DragFactor");
		duration = config.getLong("Abilities.Air.AirCombo.SwiftStream.Duration");
	}

	public void launch() {
		Vector v = player.getEyeLocation().getDirection().normalize();

		v = v.multiply(5);
		v.add(new Vector(0, 0.2, 0));

		GeneralMethods.setVelocity(this, player, v);
	}

	public void affectNearby() {
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 2.5)) {
			if (e instanceof LivingEntity && !affectedEntities.contains(e) && e.getEntityId() != player.getEntityId()) {
				Vector v = player.getVelocity().clone();

				v = v.multiply(dragFactor);

				v = v.setY(player.getVelocity().getY());

				v = v.add(new Vector(0, 0.15, 0));

				GeneralMethods.setVelocity(this, e, v);
				affectedEntities.add((LivingEntity) e);
				new HorizontalVelocityTracker(e, player, 200, this);
			}
		}
	}

	@Override
	public void progress() {
		if (!player.isOnline() || player.isDead()) {
			remove();
			return;
		}

		if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
			remove();
			return;
		}

		if (System.currentTimeMillis() > getStartTime() + duration) {
			remove();
			return;
		}
		
		playAirbendingParticles(player.getLocation(), 4);
		affectNearby();
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
		return "SwiftStream";
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
		return true;
	}

	@Override
	public Object createNewComboInstance(Player player) {
		return new SwiftStream(player);
	}

	@Override
	public ArrayList<AbilityInformation> getCombination() {
		return ComboUtil.generateCombinationFromList(this, AvatarDuelsConfig.getConfig(player).getStringList("Abilities.Air.AirCombo.SwiftStream.Combination"));
	}

	@Override
	public String getInstructions() {
		return AvatarDuelsConfig.getConfig(player).getString("Abilities.Air.AirCombo.SwiftStream.Instructions");
	}

	@Override
	public String getDescription() {
		ConfigurationSection config = AvatarDuelsConfig.getConfig(this.player);
	   return "* AvatarDuels Addon *\n" + config.getString("Abilities.Air.AirCombo.SwiftStream.Description");
	}
	
	@Override
	public String getAuthor() {
		return AvatarDuels.dev;
	}

	@Override
	public String getVersion() {
		return AvatarDuels.version;
	}

	public double getDragFactor() {
		return dragFactor;
	}

	public void setDragFactor(double dragFactor) {
		this.dragFactor = dragFactor;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public List<LivingEntity> getAffectedEntities() {
		return affectedEntities;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}
	
	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuelsConfig.getConfig(this.player);
		return config.getBoolean("Abilities.Air.AirCombo.SwiftStream.Enabled");
	}
}