package com.serene.avatarduels.spirits.ability.api;

import com.projectkorra.projectkorra.Element;
import com.serene.avatarduels.spirits.SpiritElement;
import org.bukkit.entity.Player;

public abstract class LightAbility extends SpiritAbility {

    public LightAbility(Player player) {
        super(player);
    }

    @Override
    public Element getElement() {
        return SpiritElement.LIGHT;
    }
}
