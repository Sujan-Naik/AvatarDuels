package com.serene.avatarduels.spirits.ability.light;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.LightAbility;
import com.serene.avatarduels.spirits.utilities.Methods;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class LightBlast extends LightAbility {

    //TODO: Add sounds.

    private final Particle.DustOptions white = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1);
    private final Particle.DustOptions pink = new Particle.DustOptions(Color.fromRGB(255, 160, 160), 1);
    private Entity target;
    private LightBlastType type;
    private Location blast, location, origin;
    private Vector direction, vector;

    private boolean burst = true, canHeal, controllable, hasReached = false;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.SPEED)
    private double initialBlastSpeed;
    @Attribute(Attribute.RADIUS)
    private double blastRadius;
    @Attribute(Attribute.SPEED)
    private double finalBlastSpeed;
    @Attribute(Attribute.RANGE)
    private double range;
    private int potionDuration, potionPower;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown, selectionDuration, time;

    public LightBlast(Player player, LightBlastType type) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        if (type != null) this.type = type;

        if (hasAbility(player, LightBlast.class) && type == LightBlastType.SHIFT) {
            LightBlast lightBlast = getAbility(player, LightBlast.class);
            if (lightBlast.target != null) {
                // Makes sure the player is looking at their target.
                Entity targetEntity = GeneralMethods.getTargetedEntity(player, lightBlast.range);
                if (targetEntity == null || !targetEntity.equals(lightBlast.target)) return;

                lightBlast.location = player.getLocation().add(0, 1, 0);
                lightBlast.canHeal = true;
            }
        } else {
            setFields();
            start();
            time = System.currentTimeMillis();
        }
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.LightSpirit.LightBlast.Cooldown");
        this.controllable = config.getBoolean("Abilities.Spirits.LightSpirit.LightBlast.Controllable");
        this.damage = config.getDouble("Abilities.Spirits.LightSpirit.LightBlast.Damage");
        this.range = config.getDouble("Abilities.Spirits.LightSpirit.LightBlast.Range");
        this.selectionDuration = config.getLong("Abilities.Spirits.LightSpirit.LightBlast.SelectionDuration");
        this.potionDuration = config.getInt("Abilities.Spirits.LightSpirit.LightBlast.PotionDuration");
        this.potionPower = config.getInt("Abilities.Spirits.LightSpirit.LightBlast.PotionPower");
        this.initialBlastSpeed = config.getDouble("Abilities.Spirits.LightSpirit.LightBlast.FirstBlastSpeed");
        this.blastRadius = config.getDouble("Abilities.Spirits.LightSpirit.LightBlast.BlastRadius");
        this.finalBlastSpeed = config.getDouble("Abilities.Spirits.LightSpirit.LightBlast.SecondBlastSpeed");

        this.direction = player.getLocation().getDirection();
        this.origin = player.getLocation().add(0, 1, 0);
        this.location = origin.clone();

        this.vector = new Vector(1, 0, 0);

        this.canHeal = false;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }

        if (type == LightBlastType.CLICK)
            shootDamagingBlast();
        else if (type == LightBlastType.SHIFT)
            shootSelectionBlast();

        showSelectedTarget();

        if (canHeal)
            shootHomingBlast();
    }

    private void shootDamagingBlast() {
        if (controllable)
            this.direction = player.getLocation().getDirection();

        this.blast = Methods.advanceLocationToDirection(direction, location, this.initialBlastSpeed);

        genericBlast(blast, false);

        if (origin.distance(blast) > range || GeneralMethods.isSolid(blast.getBlock()) || blast.getBlock().isLiquid()) {
            remove();
            return;
        }

        for (Entity target : GeneralMethods.getEntitiesAroundPoint(blast, this.blastRadius)) {
            if (target instanceof LivingEntity && !target.getUniqueId().equals(player.getUniqueId()) &&
                    !(target instanceof ArmorStand)) {
                DamageHandler.damageEntity(target, this.damage, this);
                player.getWorld().spawnParticle(Particle.FIREWORK, target.getLocation().add(0, 1, 0), 10, 0, 0, 0, 0.2);
                remove();
            }
        }
    }

    private void shootSelectionBlast() {
        if (target == null) {
            if (controllable)
                this.direction = player.getLocation().getDirection();

            this.blast = Methods.advanceLocationToDirection(direction, location, this.initialBlastSpeed);

            genericBlast(blast, true);

            if (origin.distance(blast) > range || GeneralMethods.isSolid(blast.getBlock()) || blast.getBlock().isLiquid()) {
                remove();
                return;
            }

            for (Entity target : GeneralMethods.getEntitiesAroundPoint(blast, this.blastRadius)) {
                if (target instanceof LivingEntity && !target.getUniqueId().equals(player.getUniqueId())) {
                    this.target = target;
                }
            }
        } else {
            if (player.getLocation().distance(target.getLocation()) > this.range ||
                    (System.currentTimeMillis() > time + selectionDuration && !canHeal)) {
                remove();
            }
        }
    }

    private void shootHomingBlast() {
        if (!hasReached) {
            this.blast = Methods.advanceLocationToPoint(vector, location, target.getLocation().add(0, 1, 0), this.finalBlastSpeed);

            player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.1, 0.1, 0.1, 0, pink);

            if (player.getLocation().distance(target.getLocation()) > this.range ||
                    origin.distance(target.getLocation()) > this.range ||
                    GeneralMethods.isSolid(blast.getBlock()) || blast.getBlock().isLiquid() ||
                    !player.isSneaking()) {
                remove();
                return;
            }

            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(blast, blastRadius)) {
                if (target.getUniqueId().equals(entity.getUniqueId())) {
                    hasReached = true;
                }
            }
        } else {
            this.healEntity(target);
        }
    }

    private void healEntity(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof ArmorStand)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                    20 * this.potionDuration, this.potionPower, false, true, false));
            remove();
        }
    }

    private void genericBlast(Location location, boolean healing) {
        player.getWorld().spawnParticle(Particle.END_ROD, location, 2, 0.1, 0.1, 0.1, 0);
        player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.2, 0.2, 0.2, 0, white);

        if (healing)
            player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.2, 0.2, 0.2, 0, pink);

        if (burst) {
            player.getWorld().spawnParticle(Particle.FIREWORK, location, 10, 0, 0, 0, 0.1);
            burst = false;
        }
    }

    private void showSelectedTarget() {
        if (target != null)
            player.getWorld().spawnParticle(
                    Particle.DUST, target.getLocation().add(0, 1, 0),
                    2, 0.5, 1, 0.5, 0, white);
    }

    @Override
    public void remove() {
        super.remove();
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
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public double getCollisionRadius() {
        return blastRadius;
    }

    @Override
    public String getName() {
        return "LightBlast";
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public enum LightBlastType {
        SHIFT, CLICK
    }
}