package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;

public class RangedAbility extends BendingUseAbility {

    private double maxRange;
    public RangedAbility(String name, BendingNPC npc, String abilityName, double maxRange) {
        super(name, npc, abilityName);
        this.maxRange = maxRange;

    }

    @Override
    public boolean shouldStart() {
        return npc.hasLineOfSight(target) && target.distanceToSqr(npc) < maxRange * maxRange;
    }


}
