package com.avatarduels.project.addons.util;

import com.avatarduels.project.addons.AvatarDuels;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.SubAbility;

public abstract class SoundAbility extends AirAbility implements SubAbility{

	public SoundAbility(Player player) {
		super(player);
	}
	
	@Override
	public Class<? extends Ability> getParentAbility() {
		return AirAbility.class;
	}
	
	@Override
	public Element getElement() {
		return AvatarDuels.instance.getSoundElement();
	}
}
