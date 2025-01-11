package com.serene.avatarduels.spirits.ability.spirit;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.SpiritAbility;
import com.serene.avatarduels.spirits.ability.spirit.combo.Levitation;
import com.serene.avatarduels.spirits.utilities.Methods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Vanish extends SpiritAbility {

    //TODO: Update sounds.

    private Location origin;

    private boolean applyInvis = true, isCharged, removeFire;
    private int particleFrequency;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.RADIUS)
    private double radius;
    @Attribute(Attribute.RANGE)
    private double range;

    public Vanish(Player player) {
        super(player);

        if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, Levitation.class)) {
            return;
        }
        setFields();
        start();
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        // Main configuration
        this.cooldown = config.getLong("Abilities.Spirits.Neutral.Vanish.Cooldown");
        this.duration = config.getLong("Abilities.Spirits.Neutral.Vanish.Duration");
        this.chargeTime = config.getLong("Abilities.Spirits.Neutral.Vanish.ChargeTime");
        this.radius = config.getDouble("Abilities.Spirits.Neutral.Vanish.Radius");
        this.particleFrequency = config.getInt("Abilities.Spirits.Neutral.Vanish.ParticleFrequency");
        this.removeFire = config.getBoolean("Abilities.Spirits.Neutral.Vanish.RemoveFire");

        // DivideRange configuration
        boolean doHalfEffect = config.getBoolean("Abilities.Spirits.Neutral.Vanish.DivideRange.Enabled");
        double healthReq = config.getDouble("Abilities.Spirits.Neutral.Vanish.DivideRange.HealthRequired");
        int divideFactor = config.getInt("Abilities.Spirits.Neutral.Vanish.DivideRange.DivideFactor");

        // Health logic
        if (doHalfEffect && player.getHealth() < healthReq) {
            this.range = config.getDouble("Abilities.Spirits.Neutral.Vanish.Range") / divideFactor;
        } else {
            this.range = config.getDouble("Abilities.Spirits.Neutral.Vanish.Range");
        }

        this.origin = player.getLocation();
        this.isCharged = false;
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this) && !isCharged) {
            remove();
            return;
        }
        if (!isCharged) {
            this.chargingSequence();
        } else {
            this.hasChargedSequence();
        }
    }

    private void chargingSequence() {
        if (player.isSneaking()) {
            if (System.currentTimeMillis() > getStartTime() + chargeTime) isCharged = true;
            else if (new Random().nextInt(particleFrequency) == 0)
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0, 1, 0), 1, 0, 0, 0, 0.09);
        } else if (!isCharged) {
            remove();
        }
    }

    private void hasChargedSequence() {
        if (player.isSneaking()) {
            playEffects();

            if ((origin.distanceSquared(player.getLocation()) > radius * radius) || (System.currentTimeMillis() > getStartTime() + duration)) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, -1);
                remove();
            }
        } else {
            Location targetLoc = GeneralMethods.getTargetedLocation(player, range);
            player.teleport(targetLoc);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, -1);
            remove();
        }
        if (removeFire) {
            player.setFireTicks(-1);
        }
    }

    private void playEffects() {
        if (new Random().nextInt(particleFrequency) == 0) {
            Methods.playSpiritParticles(player, player.getLocation().add(0, 1, 0), 0.5, 0.5, 0.5, 0, 1);
        }
        if (applyInvis) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) duration, 2), true);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, -1);
            Methods.animateVanish(player);
            applyInvis = false;
        }
    }

    @Override
    public void remove() {
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (isCharged) Methods.animateVanish(player);
        bPlayer.addCooldown(this);
        super.remove();
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
        return "Vanish";
    }

    @Override
    public String getAbilityType() {
        return MOBILITY;
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
        return true;
    }
}