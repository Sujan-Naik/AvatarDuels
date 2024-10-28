package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;

public class MovementAbility extends BendingUseAbility {
    public MovementAbility(String name, BendingNPC npc, String abilityName) {
        super(name, npc, abilityName);
    }

    @Override
    public boolean shouldStart() {
        return false;
    }
}
