package com.serene.avatarduels.ability.fire;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class Explode extends CombustionAbility implements AddonAbility {
	
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.DAMAGE)
	private double damage;
	@Attribute(Attribute.RADIUS)
	private double radius;
	@Attribute(Attribute.KNOCKBACK)
	private double knockback;
	@Attribute(Attribute.RANGE)
	private double range;
	
	private Location center;

	public Explode(Player player) {
		super(player);
		
		this.cooldown = AvatarDuels.getConfig(getName()).getLong("Abilities.Fire.Explode.Cooldown");
		this.damage = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.Explode.Damage");
		this.radius = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.Explode.Radius");
		this.knockback = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.Explode.Knockback");
		this.range = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.Explode.Range");
		
		start();
	}

	@Override
	public void progress() {
		if (!player.isOnline() || player.isDead()) {
			remove();
			return;
		}
		
		if (player.isSneaking()) {
			this.center = GeneralMethods.getTargetedLocation(player, range, ElementalAbility.getTransparentMaterials());
			
			ParticleEffect.CRIT.display(center, 3, 0.3, 0.3, 0.3);
			player.getWorld().playSound(center, Sound.ENTITY_CREEPER_PRIMED, 0.2f, 8f);
		} else {
			if (center != null) {
				double offset = radius / 2;
				playFirebendingParticles(center, 7, offset, offset, offset);
				ParticleEffect.CRIT.display(center, 6, offset, offset, offset);
				ParticleEffect.EXPLOSION_HUGE.display(center, 1);
				player.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 3f);
				
				for (Entity e : GeneralMethods.getEntitiesAroundPoint(center, radius)) {
					if (e instanceof LivingEntity) {
						Vector direction = GeneralMethods.getDirection(center, ((LivingEntity) e).getEyeLocation()).normalize().multiply(knockback);
						DamageHandler.damageEntity(e, damage, this);
						e.setVelocity(direction);
					}
				}
				
				remove();
			}
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		bPlayer.addCooldown(this);
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public String getName() {
		return "Explode";
	}

	@Override
	public Location getLocation() {
		return center;
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
	
	@Override
	public boolean isEnabled() {
		return AvatarDuels.getConfig(getName()).getBoolean("Abilities.Fire.Explode.Enabled");
	}

	@Override
	public String getDescription() {
		return "Cause a spontaneous explosion where you are looking! Big boom!";
	}
	
	@Override
	public String getInstructions() {
		return "Hold sneak to aim, release sneak to explode!";
	}
}
