package com.serene.avatarduels.ability.fire;

import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.util.HexColor;
import com.serene.avatarduels.util.SoundEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

public class CombustBeam extends CombustionAbility implements AddonAbility {

	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute("MinChargeTime")
	private long minChargeTime;
	@Attribute("MaxChargeTime")
	private long maxChargeTime;
	@Attribute(Attribute.RANGE)
	private double range;
	@Attribute("MinAngle")
	private double minAngle;
	@Attribute("MaxAngle")
	private double maxAngle;
	@Attribute("MinPower")
	private double minPower;
	@Attribute("MaxPower")
	private double maxPower;
	@Attribute("MaxDamage")
	private double maxDamage;
	@Attribute("MinDamage")
	private double minDamage;
	
	private double power, rotation, angleCheck, damage, health;
	private long chargeTime, revertTime;
	private int counter;
	private boolean charging, charged;
	private Location curr;
	private Vector direction;
	private SoundEffect sound;
	
	private ParticleEffect[] flames = { ParticleEffect.FLAME, ParticleEffect.SOUL_FIRE_FLAME };
	
	public CombustBeam(Player player) {
		super(player);
		
		if (hasAbility(player, CombustBeam.class)) {
			return;
		}
		
		this.cooldown = AvatarDuels.getConfig(getName()).getLong("Abilities.Fire.CombustBeam.Cooldown");
		this.minChargeTime = AvatarDuels.getConfig(getName()).getLong("Abilities.Fire.CombustBeam.Minimum.ChargeTime");
		this.maxChargeTime = AvatarDuels.getConfig(getName()).getLong("Abilities.Fire.CombustBeam.Maximum.ChargeTime");
		this.minAngle = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Minimum.Angle");
		this.maxAngle = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Maximum.Angle");
		this.minPower = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Minimum.Power");
		this.maxPower = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Maximum.Power");
		this.minDamage = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Minimum.Damage");
		this.maxDamage = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Maximum.Damage");
		this.range = AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.Range");
		this.revertTime = AvatarDuels.getConfig(getName()).getLong("Abilities.Fire.CombustBeam.RevertTime");
		this.health = player.getHealth();
		this.charging = true;
		this.charged = false;
		this.rotation = 0;
		this.counter = 0;
		this.sound = new SoundEffect(Sound.ENTITY_WITHER_AMBIENT, 0.01f, 0.6f, 30);
		
		start();
	}

	@Override
	public void progress() {
		if (!player.isOnline() || player.isDead()) {
			remove();
			return;
		}
		
		if (charging) {
			if (!bPlayer.getBoundAbilityName().equalsIgnoreCase("CombustBeam")) {
				remove();
				return;
			} else if (!charged && !player.isSneaking()) {
				remove();
				return;
			} else if (charged && !player.isSneaking()) {
				charging = false;
				curr = player.getEyeLocation();
				direction = player.getEyeLocation().getDirection().clone().normalize();
				
				if (player.getHealth() < health) {
					DamageHandler.damageEntity(player, AvatarDuels.getConfig(getName()).getDouble("Abilities.Fire.CombustBeam.InterruptedDamage"), this);
					explode();
					return;
				}
				
				return;
			}

			sound.play(player.getEyeLocation());
			
			if (getStartTime() + maxChargeTime <= System.currentTimeMillis()) {
				this.chargeTime = maxChargeTime;
				this.angleCheck = minAngle;
				this.power = maxPower;
				this.damage = maxDamage;
				this.charged = true;
				GeneralMethods.displayColoredParticle("ff2424", player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize()), 1, 0.4, 0.4, 0.4);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 5));

				ActionBar.sendActionBar(ChatColor.RED + "100%", player);
			} else if (getStartTime() + minChargeTime <= System.currentTimeMillis()) {
				this.chargeTime = System.currentTimeMillis() - getStartTime() - minChargeTime;
				
				double percent = ((double) chargeTime / ((double) (maxChargeTime - minChargeTime)));
				
				this.angleCheck = maxAngle - (maxAngle - minAngle) * percent;
				this.power = minPower + (maxPower - minPower) * percent;
				this.damage = minDamage + (maxDamage - minDamage) * percent;
				this.charged = true;
				
				ActionBar.sendActionBar(ChatColor.RED + (Math.round(percent * 100) + "%"), player);
				
				HexColor color = new HexColor((int) (255 * percent), 136, 136);
				GeneralMethods.displayColoredParticle(color.getHexcode(), player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize()), 1, 0.4, 0.4, 0.4);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, (int) (5 * percent)));
			}
		} else {
			if (player.isSneaking()) {
				Vector to = player.getEyeLocation().getDirection().clone().normalize().multiply(0.3);
				
				if (Math.abs(direction.angle(to)) < angleCheck) {
					direction.add(to.multiply(1.0 / 20));
				}
			}
			
			direction.normalize();
			
			for (int j = 0; j < power; j++) {
				if (player.getEyeLocation().distance(curr) >= range) {
					explode();
					return;
				}
				
				curr.add(direction);
				
				if (!curr.getBlock().isPassable()) {
					explode();
					return;
				} else if (curr.getBlock().getType() == Material.WATER) {
					for (Block b : GeneralMethods.getBlocksAroundPoint(curr, power)) {
						if (b.getType() == Material.WATER) {
							new TempBlock(b, Material.AIR).setRevertTime(100000);
						}
					}
					
					explode();
					return;
				}
				
				GeneralMethods.displayColoredParticle("fefefe", curr, 3, 0.1, 0.1, 0.1);
				
				if (player.hasPermission(".ability.combustbeam.easteregg")) {
					for (int i = 0; i < 2; i++) {
						Vector v = GeneralMethods.getOrthogonalVector(direction, rotation + 180 * i, 0.4);
						Location p = curr.clone().add(v);
						flames[i].display(p, 1);
					}
				} else {
					for (int i = 0; i < 2; i++) {
						Vector v = GeneralMethods.getOrthogonalVector(direction, rotation + 180 * i, 0.4);
						Location p = curr.clone().add(v);
						GeneralMethods.displayColoredParticle("ededed", p);
					}
				}
				
				rotation += 10;
				
				if (counter % 10 == 0) {
					ParticleEffect.EXPLOSION_LARGE.display(curr, 1);
					playCombustionSound(curr);
				}
				
				counter++;
				
				for (Entity e : GeneralMethods.getEntitiesAroundPoint(curr, 1)) {
					if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
						explode();
						return;
					}
				}
			}
		}
	}
	
	public void explode() {
		if (!charging) {
			if (GeneralMethods.isRegionProtectedFromBuild(player, curr)) {
				return;
			}
			
			ParticleEffect.EXPLOSION_HUGE.display(curr, 1);
			player.getWorld().playSound(curr, Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
			for (Block block : GeneralMethods.getBlocksAroundPoint(curr, power)) {
				if (block.getType().getBlastResistance() < power) {
					new TempBlock(block, Material.AIR).setRevertTime(revertTime);
				}
			}
			
			for (Entity e : GeneralMethods.getEntitiesAroundPoint(curr, power)) {
				if (e instanceof LivingEntity) {
					double knockback = power / (0.3 + e.getLocation().distance(curr));
					DamageHandler.damageEntity(e, damage, this);
					e.setVelocity(GeneralMethods.getDirection(curr, e.getLocation().add(0, 1, 0)).normalize().multiply(knockback));
				}
			}
			
			remove();
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
		return "CombustBeam";
	}

	@Override
	public Location getLocation() {
		return curr;
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
		return AvatarDuels.getConfig(getName()).getBoolean("Abilities.Fire.CombustBeam.Enabled");
	}
	
	@Override
	public String getDescription() {
		return "Fire a beam of concentrated energy from your forehead after charging. Longer charge times increase power, speed, and decrease how controllable the beam is. Explodes when hitting blocks and entities. Evaporates nearby water on explosion. Collides with some other abilities.";
	}
	
	@Override
	public String getInstructions() {
		return "Hold sneak to begin charging. Release anytime you see particles in front of you to launch the beam. Hold sneak again to direct the beam to some degree.";
	}
}
