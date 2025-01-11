package com.serene.avatarduels.spirits.ability.spirit;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.SpiritAbility;
import com.serene.avatarduels.spirits.ability.spirit.combo.Levitation;
import com.serene.avatarduels.spirits.utilities.Methods;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Soar extends SpiritAbility {

    //TODO: Update sounds.

    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;

    public Soar(Player player) {
        super(player);

        if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, Levitation.class)) {
            return;
        }
        setFields();
        start();
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.Neutral.Agility.Soar.Cooldown");
        this.duration = config.getLong("Abilities.Spirits.Neutral.Agility.Soar.Duration");
        this.speed = config.getDouble("Abilities.Spirits.Neutral.Agility.Soar.Speed");
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        progressSoar();
    }

    private void progressSoar() {
        if (player.isSneaking()) {
            if (System.currentTimeMillis() > getStartTime() + duration) {
                remove();
            } else {
                GeneralMethods.setVelocity(this, player, player.getLocation().getDirection().multiply(speed));
                if (getRunningTicks() % 5 == 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.3F, 5F);
                }
                Methods.playSpiritParticles(player, player.getLocation(), 0.5, 0.5, 0.5, 0, 2);
            }
        }
    }

    @Override
    public void remove() {
        bPlayer.addCooldown("Soar", cooldown);
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
        return "Agility";
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
        return true;
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