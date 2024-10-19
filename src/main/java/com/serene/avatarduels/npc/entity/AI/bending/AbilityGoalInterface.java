package com.serene.avatarduels.npc.entity.AI.bending;


import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.player.Player;

public interface AbilityGoalInterface {
    BendingUseAbility makeGoal(BendingNPC npc);
}
