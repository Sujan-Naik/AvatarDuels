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

public class Dash extends SpiritAbility {

    //TODO: Update sounds.

    private Location location;

    @Attribute(Attribute.COOLDOWN)
    private long cooldown;

    @Attribute("Distance")
    private double distance;

    public Dash(Player player) {
        super(player);

        if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, Levitation.class)) {
            return;
        }

        setFields();
        //Even though this is just a dash ability, we do it via start() so that attribute modifiers can work
        start();

    }

    @Override
    public String getAbilityType() {
        return MOBILITY;
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.Neutral." + getName() + ".Dash.Cooldown");
        this.distance = config.getDouble("Abilities.Spirits.Neutral." + getName() + ".Dash.Distance");
        this.location = player.getLocation();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBend(this)) {
            remove();
            return;
        }
        progressDash();
    }

    private void progressDash() {
        GeneralMethods.setVelocity(this, player, Methods.setVelocity(player, (float) distance, 0.2));
        location.getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_HURT, 1.5F, 0.5F);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.3F, 0.5F);
        Methods.playSpiritParticles(player, player.getLocation(), 0.5, 0.5, 0.5, 0, 10);
        remove();
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
        return player.getLocation();
    }

    @Override
    public String getName() {
        return "Agility";
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
        return false;
    }
}