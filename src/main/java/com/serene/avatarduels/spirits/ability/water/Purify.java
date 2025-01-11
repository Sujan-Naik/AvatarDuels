package com.serene.avatarduels.spirits.ability.water;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.SpiritElement;
import com.serene.avatarduels.spirits.ability.api.WaterAbility;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Purify extends WaterAbility {

    public static Set<Integer> heldEntities = new HashSet<Integer>();
    public LivingEntity target;
    public byte stage = 0;
    public Location travelLoc = null;
    public double yaw;
    public Random random;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    private final boolean hasReached = true;
    private int ticks;
    private int chargeTicks;
    private long time;
    private boolean charged = false;
    private boolean setElement;

    public Purify(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
            return;
        }
        firstloop:
        for (int i = 20; i < 100; i++) {
            Location loc = GeneralMethods.getTargetedLocation(player, range);
            for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 10)) {
                if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
                    target = (LivingEntity) e;
                    break firstloop;
                }
            }
        }
        time = System.currentTimeMillis();

        if (target == null) {
            return;
        }
        heldEntities.add(target.getEntityId());
        setFields();
        if (isEnabled()) {
            start();
        }
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.Water.Purify.Cooldown");
        this.duration = config.getLong("Abilities.Spirits.Water.Purify.Duration");
        this.range = config.getDouble("Abilities.Spirits.Water.Purify.Range");
        this.setElement = config.getBoolean("Abilities.Spirits.Water.Purify.SetElement");
    }

    public double calculateSize(LivingEntity entity) {
        return (entity.getEyeLocation().distance(entity.getLocation()) / 2 + 0.8D);
    }

    @Override
    public void remove() {
        super.remove();

        if (target != null) {
            heldEntities.remove(target.getEntityId());
        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getName() {
        return "Purify";
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

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreCooldowns(this)) {
            remove();
            return;
        }

        if (target == null || target.isDead()) {
            remove();
            return;
        }


        if (!target.getWorld().equals(player.getWorld())) {
            remove();
            return;
        }

        if (target.getLocation().distance(player.getLocation()) > 25) {

            remove();
            return;
        }

        if (System.currentTimeMillis() - time > 10000L) {
            MovementHandler mh = new MovementHandler(player, this);
            mh.stop(ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "* READY *");
            charged = true;
            createNewSpirals();
        }

        if (System.currentTimeMillis() - time > duration) {
            remove();
            bPlayer.addCooldown(this);
            return;
        }

        if (charged) {
            if (!player.isSneaking()) {
                if (target instanceof OfflinePlayer && setElement) {
                    BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((OfflinePlayer) target);
                    if (bPlayer.hasElement(SpiritElement.DARK)) {
                        bPlayer.addElement(SpiritElement.LIGHT);
                        bPlayer.getElements().remove(SpiritElement.DARK);
                        GeneralMethods.saveElements(bPlayer);
                        GeneralMethods.removeUnusableAbilities(bPlayer.getName());
                        target.sendMessage(SpiritElement.DARK.getColor() + "You are now a" + ChatColor.BOLD + ChatColor.AQUA + " LightSpirit");
                        ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.0F);
                    } else {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 300, 2));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
                        ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.0F);
                    }
                } else if (target != null) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 300, 2));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
                    ParticleEffect.FIREWORKS_SPARK.display(target.getLocation(), 3, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.0F);
                }
            }
        }

        if (stage == 0) {

            if (!player.isSneaking()) {
                bPlayer.addCooldown(this);
                remove();
                return;
            }

            if (travelLoc == null && this.getStartTime() + duration < System.currentTimeMillis()) {
                remove();
                bPlayer.addCooldown(this);
                travelLoc = player.getEyeLocation();
            } else if (travelLoc == null) {
                ticks++;
                Long chargingTime = System.currentTimeMillis() - getStartTime();
                this.chargeTicks = (int) (chargingTime / 25);
                if (!charged) {
                    createSpirals();
                } else {
                    createNewSpirals();
                }
                //ParticleEffect.MAGIC_CRIT.display(0.3F, 0.3F, 0.3F, 0.1F, 8, target.getLocation().clone().add(0, 0.8, 0), 90);
                //f7f2f6
                for (int i = -180; i < 180; i += 10) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 128));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 300, 128));
                }
            }
        }
    }

    public void paralyze(Entity entity) {
        if (entity instanceof Creature) {
            ((Creature) entity).setTarget(null);
        }

        if (entity instanceof Player) {
            if (Suffocate.isChannelingSphere((Player) entity)) {
                Suffocate.remove((Player) entity);
            }
        }
        MovementHandler mh = new MovementHandler((LivingEntity) entity, this);
        mh.stop(ChatColor.YELLOW + "* PURIFYING *");
    }

    private void createSpirals() {
        Color blue = Color.fromBGR(244, 170, 66);
        DustOptions dustBlue = new DustOptions(blue, 1);

        Color lightBlue = Color.fromBGR(255, 221, 112);
        DustOptions dustLight = new DustOptions(lightBlue, 1);

        if (hasReached) {
            int amount = chargeTicks + 2;
            double maxHeight = 4;
            double distanceFromPlayer = 1.5;

            int angle = 5 * amount + 5 * ticks;
            double x = Math.cos(Math.toRadians(angle)) * distanceFromPlayer;
            double z = Math.sin(Math.toRadians(angle)) * distanceFromPlayer;
            double height = (amount * 0.10) % maxHeight;
            Location displayLoc = target.getLocation().clone().add(x, height, z);

            int angle2 = 5 * amount + 180 + 5 * ticks;
            double x2 = Math.cos(Math.toRadians(angle2)) * distanceFromPlayer;
            double z2 = Math.sin(Math.toRadians(angle2)) * distanceFromPlayer;
            Location displayLoc2 = target.getLocation().clone().add(x2, height, z2);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc, 1, 0, 0, 0, 0, dustBlue, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc2, 1, 0, 0, 0, 0, dustBlue, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc, 1, 0, 0, 0, 0, dustLight, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc2, 1, 0, 0, 0, 0, dustLight, true);
        }
    }

    private void createNewSpirals() {
        Color yellow = Color.fromBGR(155, 255, 250);
        DustOptions dustYellow = new DustOptions(yellow, 1);

        Color darkYellow = Color.fromBGR(94, 255, 246);
        DustOptions dustDark = new DustOptions(darkYellow, 1);
        if (hasReached) {
            int amount = chargeTicks + 2;
            double maxHeight = 4;
            double distanceFromPlayer = 1.5;

            int angle = 5 * amount + 5 * ticks;
            double x = Math.cos(Math.toRadians(angle)) * distanceFromPlayer;
            double z = Math.sin(Math.toRadians(angle)) * distanceFromPlayer;
            double height = (amount * 0.10) / maxHeight;
            Location displayLoc = target.getLocation().clone().add(x, height, z);

            int angle2 = 5 * amount + 180 + 5 * ticks;
            double x2 = Math.cos(Math.toRadians(angle2)) * distanceFromPlayer;
            double z2 = Math.sin(Math.toRadians(angle2)) * distanceFromPlayer;
            Location displayLoc2 = target.getLocation().clone().add(x2, height, z2);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc, 1, 0, 0, 0, 0, dustYellow, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc2, 1, 0, 0, 0, 0, dustYellow, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc, 1, 0, 0, 0, 0, dustDark, true);
            target.getWorld().spawnParticle(Particle.DUST, displayLoc2, 1, 0, 0, 0, 0, dustDark, true);
        }
    }

    @Override
    public String getAuthor() {
        return "Prride";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
