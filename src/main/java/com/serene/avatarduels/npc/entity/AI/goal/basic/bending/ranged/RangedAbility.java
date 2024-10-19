package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;

public class RangedAbility extends BendingUseAbility {

    private int maxRange;
    public RangedAbility(String name, BendingNPC npc, String abilityName, int maxRange) {
        super(name, npc, abilityName);
        this.maxRange = maxRange;
    }

    @Override
    public boolean shouldStart() {
        return !npc.hasLineOfSight(target) || target.distanceToSqr(npc) > maxRange * maxRange;
    }

    @Override
    public void tick() {
        super.tick();
    }
}
