package com.serene.avatarduels.spirits.ability.api;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.SpiritElement;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class SpiritAbility extends ElementalAbility implements AddonAbility {

    public static final String OFFENSE = "Offense";
    public static final String DEFENSE = "Defense";
    public static final String MOBILITY = "Mobility";
    public static final String UTILITY = "Utility";
    public static final String PASSIVE = "Passive";


    public SpiritAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return SpiritElement.NEUTRAL;
    }

    @Override
    public boolean isEnabled() {
        String extra = this instanceof ComboAbility ? ".Combo" : (this instanceof PassiveAbility ? ".Passive" : "");
        SpiritElement se = ((SpiritElement) this.getElement());
        //System.out.println("Abilities.Spirits." + se.getConfigName() + combo + "." + getName() + ".Enabled");
        ConfigurationSection config = AvatarDuels.getConfig(getName());

        return config.getBoolean("Abilities.Spirits." + se.getConfigName() + extra + "." + getName() + ".Enabled");
    }

    @Override
    public String getAuthor() {
        return this.getElement().getColor().toString() + AvatarDuels.plugin.getDescription().getAuthors();
    }

    @Override
    public String getVersion() {
        return this.getElement().getColor().toString() + AvatarDuels.plugin.getDescription().getVersion();
    }

    @Override
    public String getDescription() {
        String combo = this instanceof ComboAbility ? " Combo" : "";
        String extra = this instanceof ComboAbility ? ".Combo" : (this instanceof PassiveAbility ? ".Passive" : "");
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        return ChatColor.BOLD + getAbilityType() + combo + ": " + ChatColor.WHITE +
                config.getString("Language.Abilities." + getElement().getName() + extra + "." + getName() + ".Description");
    }

    @Override
    public String getInstructions() {
        String extra = this instanceof ComboAbility ? ".Combo" : (this instanceof PassiveAbility ? ".Passive" : "");
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        return this.getElement().getColor().toString() +
                config.getString("Language.Abilities." + this.getElement().getName() + extra + "." + getName() + ".Instructions");
    }

    @Override
    public void load() {
    }

    @Override
    public void stop() {
    }

    public abstract String getAbilityType();
}
