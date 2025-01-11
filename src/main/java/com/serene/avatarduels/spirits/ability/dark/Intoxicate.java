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
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class Intoxicate extends DarkAbility {

    //TODO: Make new sounds

    private final DustOptions black = new DustOptions(Color.fromRGB(0, 0, 0), 1);
    private DustOptions customColor;
    private LivingEntity target;
    private Location location;
    private final Vector vector = new Vector(1, 0, 0);

    private boolean hasReached;
    @Attribute(Attribute.SPEED)
    private double blastSpeed;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("SelfDamage")
    private double selfDamage;
    private int currPoint;
    private int witherDuration, hungerDuration, confusionDuration;
    private int witherPower, hungerPower, confusionPower;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    private long harmInt, potInt;

    public Intoxicate(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) {
            return;
        }

        setFields();
        Entity targetEntity = GeneralMethods.getTargetedEntity(player, range);
        if (targetEntity instanceof LivingEntity) {
            this.target = (LivingEntity) targetEntity;
            start();
        }
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.DarkSpirit.Intoxicate.Cooldown");
        this.range = config.getDouble("Abilities.Spirits.DarkSpirit.Intoxicate.Range");
        this.potInt = config.getLong("Abilities.Spirits.DarkSpirit.Intoxicate.PotionInterval");
        this.harmInt = config.getLong("Abilities.Spirits.DarkSpirit.Intoxicate.HarmInterval");
        this.selfDamage = config.getDouble("Abilities.Spirits.DarkSpirit.Intoxicate.SelfDamage");
        this.blastSpeed = config.getDouble("Abilities.Spirits.DarkSpirit.Intoxicate.BlastSpeed");
        this.witherDuration = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherDuration");
        this.witherPower = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherPower");
        this.hungerDuration = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerDuration");
        this.hungerPower = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerPower");
        this.confusionDuration = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionDuration");
        this.confusionPower = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionPower");

        int red = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Red");
        int green = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Green");
        int blue = config.getInt("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Blue");
        this.customColor = new DustOptions(Color.fromRGB(red, green, blue), 1);

        this.location = player.getLocation().clone().add(0, 1, 0);
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this) || !player.isSneaking()) {
            remove();
            return;
        }

        if (target != null) {
            if (!hasReached) showSelection();
            else doEffects();
        }
    }

    private void showSelection() {
        Location blast = Methods.advanceLocationToPoint(vector, location, target.getLocation().add(0, 1, 0), blastSpeed);

        player.getWorld().spawnParticle(Particle.DUST, blast, 10, 0.1, 0.1, 0.1, 0, black);
        player.getWorld().spawnParticle(Particle.DUST, blast, 10, 0.1, 0.1, 0.1, 0, customColor);

        if (blast.distance(player.getLocation()) > range ||
                blast.getBlock().isLiquid() ||
                GeneralMethods.isSolid(blast.getBlock())) {
            remove();
            return;
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(blast, 1.3)) {
            if (entity.equals(target)) {
                hasReached = true;
                break;
            }
        }
    }

    private void doEffects() {
        showSpirals();

        if (System.currentTimeMillis() - getStartTime() > potInt) {
            for (PotionEffect targetEffect : target.getActivePotionEffects()) {
                if (isPositiveEffect(targetEffect.getType())) {
                    target.removePotionEffect(targetEffect.getType());
                }
            }
        }

        if (System.currentTimeMillis() - getStartTime() > harmInt) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * witherDuration, witherPower, false, true, false));
            target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * hungerDuration, hungerPower, false, true, false));
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * confusionDuration, confusionPower, false, true, false));
            DamageHandler.damageEntity(player, selfDamage, this);
            remove();
            return;
        }

        if (new Random().nextInt(20) == 0) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, -1);
        }
    }

    private void showSpirals() {
        if (target == null) return;

        Location location = target.getLocation();
        for (int i = 0; i < 6; i++) {
            currPoint += 360 / 200;
            if (currPoint > 360) {
                currPoint = 0;
            }
            double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
            double x = (float) 0.04 * (Math.PI * 4 - angle) * Math.cos(angle + i);
            double y = 1.2 * Math.cos(angle) + 1.2;
            double z = (float) 0.04 * (Math.PI * 4 - angle) * Math.sin(angle + i);
            location.add(x, y, z);
            player.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, black);
            player.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, customColor);
            location.subtract(x, y, z);
        }
    }

    @Override
    public void remove() {
        bPlayer.addCooldown(this);
        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return target != null ? target.getLocation() : player.getLocation();
    }

    @Override
    public String getName() {
        return "Intoxicate";
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