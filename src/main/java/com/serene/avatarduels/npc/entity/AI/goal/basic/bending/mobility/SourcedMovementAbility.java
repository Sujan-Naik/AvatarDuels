package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class SourcedMovementAbility extends MovementAbility {

    public SourcedMovementAbility(String name, BendingNPC npc, String abilityName, double distance) {
        super(name, npc, abilityName, distance);
    }

    public SourcedMovementAbility(String name, BendingNPC npc, String abilityName) {
        super(name, npc, abilityName, 5);
    }

    @Override
    public void tick() {
        super.tick();
        if (  !CoreAbility.hasAbility(player, bPlayer.getBoundAbility().getClass())){
            remove();
        }
    }
}