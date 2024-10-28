package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;

public class JumpAbility extends MovementAbility {

    public JumpAbility(String name, BendingNPC npc, String abilityName, double distance) {
        super(name, npc, abilityName, distance);
    }

    public JumpAbility(String name, BendingNPC npc, String abilityName) {
        super(name, npc, abilityName, 5);
    }

    @Override
    public void tick() {
        super.tick();
        if (npc.onGround() &&  !CoreAbility.hasAbility(player, bPlayer.getBoundAbility().getClass())){
            remove();
        }
    }
}