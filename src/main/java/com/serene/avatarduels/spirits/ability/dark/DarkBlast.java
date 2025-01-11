package com.serene.avatarduels.spirits.ability.dark;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.DarkAbility;
import com.serene.avatarduels.spirits.utilities.Methods;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DarkBlast extends DarkAbility {

    //TODO: Add sounds.

    private final DustOptions black = new DustOptions(Color.fromRGB(0, 0, 0), 1);
    private final DustOptions purple = new DustOptions(Color.fromRGB(150, 0, 216), 1);
    private LivingEntity target;
    private DarkBlastType type;
    private Location blast, location, origin;
    private Vector direction, vector;

    private boolean burst = true, canDamage, controllable, hasReached = false;
    @Attribute(Attribute.RADIUS)
    private double blastRadius;
    @Attribute(Attribute.DAMAGE)
    private double damage, finalBlastSpeed, initialBlastSpeed;
    @Attribute(Attribute.RANGE)
    private double range;
    private int potionDuration, potionPower;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;

    private long selectionDuration, time;

    public DarkBlast(Player player, DarkBlastType type) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        if (type != null) this.type = type;

        if (hasAbility(player, DarkBlast.class)) {
            DarkBlast oldDarkBlast = getAbility(player, DarkBlast.class);
            if (oldDarkBlast.target != null) {
                // Makes sure the player is looking at their target.
                Entity targetEntity = GeneralMethods.getTargetedEntity(player, oldDarkBlast.range);
                if (targetEntity == null || !targetEntity.equals(oldDarkBlast.target)) return;

                oldDarkBlast.location = player.getLocation().add(0, 1, 0);
                oldDarkBlast.canDamage = true;
            }
        } else {
            setFields();
            start();
            time = System.currentTimeMillis();
        }
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.DarkSpirit.DarkBlast.Cooldown");
        this.controllable = config.getBoolean("Abilities.Spirits.DarkSpirit.DarkBlast.Controllable");
        this.damage = config.getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.Damage");
        this.range = config.getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.Range");
        this.selectionDuration = config.getLong("Abilities.Spirits.DarkSpirit.DarkBlast.DurationOfSelection");
        this.potionDuration = config.getInt("Abilities.Spirits.DarkSpirit.DarkBlast.PotionDuration");
        this.potionPower = config.getInt("Abilities.Spirits.DarkSpirit.DarkBlast.PotionPower");
        this.initialBlastSpeed = config.getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.FirstBlastSpeed");
        this.blastRadius = config.getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.BlastRadius");
        this.finalBlastSpeed = config.getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.SecondBlastSpeed");

        this.direction = player.getLocation().getDirection();
        this.origin = player.getLocation().add(0, 1, 0);
        this.location = origin.clone();

        this.vector = new Vector(1, 0, 0);

        this.canDamage = false;
    }

    @Override
    public void progress() {
        if (player.isDead() || !player.isOnline() || location.getWorld() != player.getWorld() ||
                GeneralMethods.isRegionProtectedFromBuild(player, player.getLocation())) {
            remove();
            return;
        }

        if (type == DarkBlastType.CLICK) shootDamagingBlast();
        else if (type == DarkBlastType.SHIFT) shootSelectionBlast();

        showSelectedTarget();

        if (canDamage) shootHomingBlast();
    }

    private void shootDamagingBlast() {
        if (controllable) this.direction = player.getLocation().getDirection();
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
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, target.getLocation().add(0, 1, 0), 10, 0, 0, 0, 0.2);
                remove();
            }
        }
    }

    private void shootSelectionBlast() {
        if (target == null) {
            if (controllable) this.direction = player.getLocation().getDirection();
            this.blast = Methods.advanceLocationToDirection(direction, location, this.initialBlastSpeed);

            genericBlast(blast, true);

            if (origin.distance(blast) > range || GeneralMethods.isSolid(blast.getBlock()) || blast.getBlock().isLiquid()) {
                remove();
                return;
            }

            for (Entity target : GeneralMethods.getEntitiesAroundPoint(blast, this.blastRadius)) {
                if (target instanceof LivingEntity && !target.getUniqueId().equals(player.getUniqueId())) {
                    this.target = (LivingEntity) target;
                }
            }
        } else {
            if (player.getLocation().distance(target.getLocation()) > this.range ||
                    (System.currentTimeMillis() > time + selectionDuration && !canDamage)) {
                remove();
            }
        }
    }

    private void shootHomingBlast() {
        if (!hasReached) {
            this.blast = Methods.advanceLocationToPoint(vector, location, target.getEyeLocation(), this.finalBlastSpeed);

            player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.1, 0.1, 0.1, 0, purple);

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
            this.infectEntity(target);
        }
    }

    private void infectEntity(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof ArmorStand)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                    20 * this.potionDuration, this.potionPower, false, true, false));
            remove();
        }
    }

    private void genericBlast(Location location, boolean healing) {
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 10, 0.1, 0.1, 0.1, 1);
        player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.2, 0.2, 0.2, 0, black);
        if (healing) player.getWorld().spawnParticle(Particle.DUST, location, 2, 0.2, 0.2, 0.2, 0, purple);
        if (burst) {
            player.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 10, 0, 0, 0, 0.1);
            burst = false;
        }
    }

    private void showSelectedTarget() {
        if (target != null)
            player.getWorld().spawnParticle(
                    Particle.HAPPY_VILLAGER, target.getLocation().add(0, 1, 0),
                    10, 0.5, 1, 0.5, 0);
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
        return "DarkBlast";
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public Location getLocation() {
        return blast != null ? blast : player.getLocation();
    }

    public enum DarkBlastType {
        SHIFT, CLICK
    }
}