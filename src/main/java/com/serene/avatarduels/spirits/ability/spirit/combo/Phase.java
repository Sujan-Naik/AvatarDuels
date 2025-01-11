package com.serene.avatarduels.spirits.ability.spirit.combo;

import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.SpiritAbility;
import com.serene.avatarduels.spirits.utilities.Methods;
import com.serene.avatarduels.spirits.utilities.TempSpectator;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Phase extends SpiritAbility implements ComboAbility {

    //TODO: Implement config for new variables. Variables are already made, just need the config paths.
    //TODO: Feature where flying through entities while phased could give the target and/or spirit effects (shivers, etc)
    //TODO: Update sounds.

    private Location origin;
    private TempSpectator spectator;

    private boolean applyLevitationCD, applyVanishCD, isPhased, playEffects;
    private double multiplier, levitationMultiplier, vanishMultiplier;
    private int minHealth, range;
    private long cooldown, duration, time;

    public Phase(Player player) {
        super(player);

        if (!bPlayer.canBendIgnoreBinds(this))
            return;

        setFields();
        if (player.getHealth() <= minHealth)
            return;

        this.time = System.currentTimeMillis();
        start();
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.multiplier = config.getLong("Abilities.Spirits.Neutral.Combo.Phase.CooldownMultiplier");
        this.duration = config.getLong("Abilities.Spirits.Neutral.Combo.Phase.Duration");
        this.range = config.getInt("Abilities.Spirits.Neutral.Combo.Phase.Range");
        this.minHealth = config.getInt("Abilities.Spirits.Neutral.Combo.Phase.MinHealth");
        this.applyVanishCD = config.getBoolean("Abilities.Spirits.Neutral.Combo.Phase.Vanish.ApplyCooldown");
        this.vanishMultiplier = config.getLong("Abilities.Spirits.Neutral.Combo.Phase.Vanish.CooldownMultiplier");

        applyLevitationCD = false; //Temp because of a PK bug
        levitationMultiplier = 4;

        this.origin = player.getLocation();
        this.spectator = TempSpectator.create(player);
        this.isPhased = false;
        this.playEffects = true;
    }

    @Override
    public void progress() {
        this.cooldown = (long) ((System.currentTimeMillis() - time) * multiplier);

        if (!spectator.canBendIgnoreBinds(this)) {
            remove();
            return;
        }
        setGameMode();
        if (playEffects) {
            playEffects = false;
            playEffects();
        }
        if (System.currentTimeMillis() > time + duration || origin.distanceSquared(player.getLocation()) > range * range) {
            playEffects();
            remove();
            return;
        }
        if (player.isSneaking() && isPhased) {
            playEffects();
            remove();
        }
    }

    @Override
    public void remove() {
        resetGameMode();

        // Cooldown calculations
        long duration = System.currentTimeMillis() - time;
        if (applyVanishCD) {
            long vanishCooldown = (long) (duration * vanishMultiplier);
            bPlayer.addCooldown("Vanish", vanishCooldown);
        }
        if (applyLevitationCD) {
            long levitationCooldown = (long) (duration * levitationMultiplier);
            bPlayer.addCooldown("Levitation", levitationCooldown);
        }
        bPlayer.addCooldown(this);
        super.remove();
    }

    private void setGameMode() {
        spectator.spectator();
        isPhased = true;
        player.setFlySpeed(0.1F);
    }

    private void resetGameMode() {
        spectator.revert();
        isPhased = false;
    }

    private void playEffects() {
        ParticleEffect.PORTAL.display(player.getLocation().add(0, 1, 0), 0, 0, 0, (int) 1.5F, 100);
        Methods.playSpiritParticles(player, player.getLocation().add(0, 1, 0), 1, 1, 1, 0, 20);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, -1);
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Phase(player);
    }

    @Override
    public ArrayList<AbilityInformation> getCombination() {
        ArrayList<AbilityInformation> combo = new ArrayList<AbilityInformation>();
        combo.add(new AbilityInformation("Vanish", ClickType.LEFT_CLICK));
        combo.add(new AbilityInformation("Vanish", ClickType.LEFT_CLICK));
        combo.add(new AbilityInformation("Possess", ClickType.SHIFT_DOWN));
        combo.add(new AbilityInformation("Vanish", ClickType.SHIFT_UP));
        return combo;
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
        return "Phase";
    }

    @Override
    public String getAbilityType() {
        return UTILITY;
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