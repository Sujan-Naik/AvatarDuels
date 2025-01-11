package com.serene.avatarduels.spirits.ability.light;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.LightAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Orb extends LightAbility {

    //TODO: Add sounds.

    private Location targetLoc;

    private boolean checkEntities, isCharged, playDormant, progressExplosion, registerOrbLoc, registerStageTimer, requireGround;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute("DetonateRange")
    private double detonateRange;
    @Attribute(Attribute.RANGE)
    private double effectRange;
    private int blindDuration, nauseaDuration, plantRange, potionAmp;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    private long stageTime, stageTimer;

    public Orb(Player player) {
        super(player);

        if (!bPlayer.canBend(this))
            return;

        setFields();
        start();
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.LightSpirit.Orb.Cooldown");
        this.chargeTime = config.getLong("Abilities.Spirits.LightSpirit.Orb.ChargeTime");
        this.duration = config.getLong("Abilities.Spirits.LightSpirit.Orb.Duration");
        this.stageTime = config.getLong("Abilities.Spirits.LightSpirit.Orb.WarmUpTime");
        this.damage = config.getDouble("Abilities.Spirits.LightSpirit.Orb.Damage");
        this.plantRange = config.getInt("Abilities.Spirits.LightSpirit.Orb.PlaceRange");
        this.detonateRange = config.getInt("Abilities.Spirits.LightSpirit.Orb.DetonateRange");
        this.effectRange = config.getInt("Abilities.Spirits.LightSpirit.Orb.EffectRange");
        this.blindDuration = config.getInt("Abilities.Spirits.LightSpirit.Orb.BlindnessDuration");
        this.nauseaDuration = config.getInt("Abilities.Spirits.LightSpirit.Orb.NauseaDuration");
        this.potionAmp = config.getInt("Abilities.Spirits.LightSpirit.Orb.PotionPower");
        this.requireGround = config.getBoolean("Abilities.Spirits.LightSpirit.Orb.RequireGround");

        this.registerOrbLoc = true;
        this.registerStageTimer = true;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        if (!isCharged) {
            if (player.isSneaking()) {
                if (System.currentTimeMillis() > getStartTime() + chargeTime)
                    isCharged = true;
            } else remove();
        } else {
            if (player.isSneaking() && !playDormant) {
                Location eyeLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(3));
                player.getWorld().spawnParticle(Particle.CRIT, eyeLoc, 2, 0, 0, 0, 0);
            } else {
                playDormant = true;
                if (registerOrbLoc) {
                    this.targetLoc = GeneralMethods.getTargetedLocation(player, plantRange);
                    if (requireGround) {
                        if (!canSpawn(targetLoc)) {
                            remove();
                            return;
                        } else {
                            targetLoc.add(targetLoc.getDirection().multiply(1.3));
                            if (GeneralMethods.isSolid(targetLoc.getBlock()))
                                targetLoc.add(0, 1, 0);
                        }
                    }
                    registerOrbLoc = false;
                }
                displayOrb(targetLoc);
            }
            explodeOrb();
        }
    }

    private void displayOrb(Location location) {
        if (registerStageTimer) {
            stageTimer = System.currentTimeMillis();
            registerStageTimer = false;
        }
        if (playDormant) {
            progressExplosion = false;
            ParticleEffect.ENCHANTMENT_TABLE.display(location, 3, 1, 3, 0, 1);
            ParticleEffect.END_ROD.display(location, 0, 0, 0, 0, 2);
            ParticleEffect.CRIT_MAGIC.display(location, 3, 0.2F, 0.2F, 0.2F, 0);
            if (player.isSneaking() && hasOrb()) {
                progressExplosion = true;
                playDormant = false;
            }
        }
        if (System.currentTimeMillis() > getStartTime() + duration) {
            playDormant = false;
            player.getWorld().spawnParticle(Particle.FIREWORK, location, 30, 0, 0, 0, 0.09);
            bPlayer.addCooldown(this);
            remove();
        } else {
            bPlayer.addCooldown(this, duration);
            checkEntities = true;
        }
    }

    private void explodeOrb() {
        if (checkEntities && (System.currentTimeMillis() > stageTimer + stageTime)) {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(targetLoc, detonateRange)) {
                if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                    progressExplosion = true;
                    playDormant = false;
                }
            }
        }
        if (progressExplosion) {
            player.getWorld().spawnParticle(Particle.FIREWORK, targetLoc, 30, 0.2, 0.2, 0.2, 0.4);
            ParticleEffect.END_ROD.display(targetLoc, 15, 2, 3, 2, 0);
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(targetLoc, effectRange)) {
                if (entity instanceof LivingEntity le && entity.getUniqueId() != player.getUniqueId()) {
                    le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, potionAmp));
                    le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, nauseaDuration, potionAmp));
                    DamageHandler.damageEntity(entity, damage, this);
                }
            }
            bPlayer.addCooldown(this);
            remove();
        }
    }

    private boolean canSpawn(Location loc) {
        List<Block> relatives = new ArrayList<>();
        for (Block relative : GeneralMethods.getBlocksAroundPoint(loc, 2)) {
            if (GeneralMethods.isSolid(relative))
                relatives.add(relative);
        }
        return !relatives.isEmpty();
    }

    private boolean hasOrb() {
        return bPlayer.getBoundAbility().equals(CoreAbility.getAbility(Orb.class));
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return targetLoc;
    }

    @Override
    public String getName() {
        return "Orb";
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
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
    public boolean isSneakAbility() {
        return false;
    }
}