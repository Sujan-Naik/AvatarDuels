package com.serene.avatarduels.ability.avatar.elementsphere;

import com.serene.avatarduels.JCMethods;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.util.RegenTempBlock;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import com.projectkorra.projectkorra.util.TempFallingBlock;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jedk1
 * @author Finn_Bueno_
 */
public class ESStream extends AvatarAbility implements AddonAbility {

	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.KNOCKBACK)
	private double knockback;
	@Attribute(Attribute.RANGE)
	private double range;
	@Attribute(Attribute.DAMAGE)
	private double damage;
	private boolean cancelAbility;
	private int requiredUses;

	@Attribute(Attribute.RADIUS)
	private double radius;
	private long regen;

	private Location stream;
	private Location origin;
	private Vector dir;

	private int an;
	Random rand = new Random();

	public ESStream(Player player) {
		super(player);
		if (!hasAbility(player, ElementSphere.class)) {
			return;
		}
		ElementSphere currES = getAbility(player, ElementSphere.class);
		if (bPlayer.isOnCooldown("ESStream")) {
			return;
		}
		setFields();
		
		if (currES.getAirUses() < requiredUses 
				|| currES.getEarthUses() < requiredUses 
				|| currES.getFireUses() < requiredUses 
				|| currES.getWaterUses() < requiredUses) {
			return;
		}

		if (RegionProtection.isRegionProtected(this, player.getTargetBlock(getTransparentMaterialSet(), (int) range).getLocation())) {
			return;
		}
		
		if (cancelAbility) {
			currES.remove();
		} else {
			currES.setAirUses(currES.getAirUses()-requiredUses);
			currES.setEarthUses(currES.getEarthUses()-requiredUses);
			currES.setFireUses(currES.getFireUses()-requiredUses);
			currES.setWaterUses(currES.getWaterUses()-requiredUses);
		}
		
		stream = player.getEyeLocation();
		origin = player.getEyeLocation();
		dir = player.getEyeLocation().getDirection();
		an = 0;
		
		start();
		if (!isRemoved()) {
			bPlayer.addCooldown("ESStream", getCooldown());
		}
	}
	
	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig("ElementSphere");
		
		cooldown = config.getLong("Abilities.Avatar.ElementSphere.Stream.Cooldown");
		range = config.getDouble("Abilities.Avatar.ElementSphere.Stream.Range");
		damage = config.getDouble("Abilities.Avatar.ElementSphere.Stream.Damage");
		knockback = config.getDouble("Abilities.Avatar.ElementSphere.Stream.Knockback");
		requiredUses = config.getInt("Abilities.Avatar.ElementSphere.Stream.RequiredUses");
		cancelAbility = config.getBoolean("Abilities.Avatar.ElementSphere.Stream.EndAbility");
		radius = config.getInt("Abilities.Avatar.ElementSphere.Stream.ImpactCraterSize");
		regen = config.getLong("Abilities.Avatar.ElementSphere.Stream.ImpactRevert");
	}

	@Override
	public void progress() {
		if (player == null || !player.isOnline()) {
			remove();
			return;
		}

		if (origin.distance(stream) >= range) {
			remove();
			return;
		}

		if (RegionProtection.isRegionProtected(player, stream, this)) {
			remove();
			return;
		}

		for (Entity e : GeneralMethods.getEntitiesAroundPoint(stream, 1.5)) {
			if (e instanceof Player && e == player) {
				continue;
			}
			GeneralMethods.setVelocity(this, e, dir.normalize().multiply(knockback));
			if (e instanceof LivingEntity) {
				DamageHandler.damageEntity(e, damage, this);
			}
		}

		if (!player.isDead() && hasAbility(player, ElementSphere.class)) {
			Location loc = stream.clone();
			dir = GeneralMethods.getDirection(loc, player.getTargetBlock(null, (int) range).getLocation()).normalize().multiply(1.2);
		}

		stream.add(dir);
		
		if (!isTransparent(stream.getBlock())) {
			List<BlockState> blocks = new ArrayList<>();
			for (Location loc : GeneralMethods.getCircle(stream, (int) radius, 0, false, true, 0)) {
				if (JCMethods.isUnbreakable(loc.getBlock())) continue;
				if (RegionProtection.isRegionProtected(this, loc)) continue;
				blocks.add(loc.getBlock().getState());
				new RegenTempBlock(loc.getBlock(), Material.AIR, Material.AIR.createBlockData(), regen, false);
			}
			for (Entity e : GeneralMethods.getEntitiesAroundPoint(stream, radius)) {
				if (e instanceof Player && e == player) {
					continue;
				}
				if (RegionProtection.isRegionProtected(this, e.getLocation()) || ((e instanceof Player) && Commands.invincible.contains(((Player) e).getName()))){
					continue;
				}
				GeneralMethods.setVelocity(this, e, dir.normalize().multiply(knockback));
				if (e instanceof LivingEntity) {
					DamageHandler.damageEntity(e, damage, this);
				}
			}

			ParticleEffect.FLAME.display(stream, 20, Math.random(), Math.random(), Math.random(), 0.5);
			ParticleEffect.SMOKE_LARGE.display(stream, 20, Math.random(), Math.random(), Math.random(), 0.5);
			ParticleEffect.FIREWORKS_SPARK.display(stream, 20, Math.random(), Math.random(), Math.random(), 0.5);
			ParticleEffect.SMOKE_LARGE.display(stream, 20, Math.random(), Math.random(), Math.random(), 0.5);
			ParticleEffect.EXPLOSION_HUGE.display(stream, 5, Math.random(), Math.random(), Math.random(), 0.5);

			stream.getWorld().playSound(stream, (rand.nextBoolean()) ? Sound.ENTITY_FIREWORK_ROCKET_BLAST : Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f);
			stream.getWorld().playSound(stream, (rand.nextBoolean()) ? Sound.ENTITY_FIREWORK_ROCKET_TWINKLE : Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1f, 1f);

			for (BlockState block : blocks) {
				double x = rand.nextDouble() / 3;
				double z = rand.nextDouble() / 3;

				x = (rand.nextBoolean()) ? -x : x;
				z = (rand.nextBoolean()) ? -z : z;

				new TempFallingBlock(block.getLocation().add(0, 1, 0), block.getBlockData(), dir.clone().add(new Vector(x, 0, z)).normalize().multiply(-1), this);
			}
			remove();
			return;
		}
		
		an += 20;
		if (an > 360) {
			an = 0;
		}
		for (int i = 0; i < 4; i++) {
			for (double d = -4; d <= 0; d += .1) {
				if (origin.distance(stream) < d) {
					continue;	
				}
				Location l = stream.clone().add(dir.clone().normalize().multiply(d));
				double r = d * -1 / 5;
				if (r > .75) {
					r = .75;
				}

				Vector ov = GeneralMethods.getOrthogonalVector(dir, an + (90 * i) + d, r);
				Location pl = l.clone().add(ov.clone());
				switch (i) {
					case 0:
						ParticleEffect flame = bPlayer.hasSubElement(Element.BLUE_FIRE) ? ParticleEffect.SOUL_FIRE_FLAME : ParticleEffect.FLAME;
						flame.display(pl, 1, 0.05F, 0.05F, 0.05F, 0.005F);
						break;
					case 1:
						if (rand.nextInt(30) == 0) {
							JCMethods.displayColoredParticles("#FFFFFF", pl, 1, 0, 0, 0, 0.003f);
						} else {
							JCMethods.displayColoredParticles("#FFFFFF", pl, 1, 0.05, 0.05, 0.05, 0.005f, 50);
						}
						break;
					case 2:
						GeneralMethods.displayColoredParticle("06C1FF", pl);
						break;
					case 3:
						GeneralMethods.displayColoredParticle("754719", pl);
						break;
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
		return stream;
	}

	@Override
	public String getName() {
		return "ElementSphereStream";
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

	public double getKnockback() {
		return knockback;
	}

	public void setKnockback(double knockback) {
		this.knockback = knockback;
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

	public boolean cancelsAbility() {
		return cancelAbility;
	}

	public void setCancelsAbility(boolean cancelAbility) {
		this.cancelAbility = cancelAbility;
	}

	public int getRequiredUses() {
		return requiredUses;
	}

	public void setRequiredUses(int requiredUses) {
		this.requiredUses = requiredUses;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public long getRegenTime() {
		return regen;
	}

	public void setRegenTime(long regen) {
		this.regen = regen;
	}

	public Location getOrigin() {
		return origin;
	}

	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	public Vector getDirection() {
		return dir;
	}

	public void setDirection(Vector dir) {
		this.dir = dir;
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
