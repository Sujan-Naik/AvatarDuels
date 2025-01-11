package com.serene.avatarduels.spirits.ability.api;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.serene.avatarduels.AvatarDuels;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class WaterAbility extends com.projectkorra.projectkorra.ability.WaterAbility implements AddonAbility {

    public static final String OFFENSE = "Offense";
    public static final String DEFENSE = "Defense";
    public static final String MOBILITY = "Mobility";
    public static final String UTILITY = "Utility";


    public WaterAbility(Player player) {
        super(player);
    }

    @Override
    public boolean isEnabled() {
        String combo = this instanceof ComboAbility ? ".Combo" : "";
        ConfigurationSection config = AvatarDuels.getConfig(getName());

        return config.getBoolean("Abilities.Spirits.Water" + combo + "." + getName() + ".Enabled");
    }

    @Override
    public String getVersion() {
        return this.getElement().getColor().toString() + AvatarDuels.plugin.getDescription().getVersion();
    }

    @Override
    public String getDescription() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        return config.getString("Language.Abilities.Water." + getName() + ".Description");
    }

    @Override
    public String getInstructions() {
        ConfigurationSection config = AvatarDuels.instance.getConfig();
        return this.getElement().getColor().toString() +
                config.getString("Language.Abilities." + this.getElement().getName() + "." + getName() + ".Instructions");
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }
}
