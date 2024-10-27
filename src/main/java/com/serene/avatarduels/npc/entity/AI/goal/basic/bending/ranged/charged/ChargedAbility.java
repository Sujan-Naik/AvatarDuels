package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.charged;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;

import java.util.function.Predicate;

public class ChargedAbility extends RangedAbility {

    protected Predicate<BlockPos> condition;

    public ChargedAbility(String name, BendingNPC npc, String abilityName, double maxRange,Predicate<BlockPos> condition ) {
        super(name, npc, abilityName, maxRange);

        this.condition = condition;

    }

    public ChargedAbility(String name, BendingNPC npc, String abilityName, double maxRange ) {
        super(name, npc, abilityName, maxRange);

        this.condition = condition;

    }

    @Override
    public void tick() {
        super.tick();
        if (condition != null && condition.test(npc.blockPosition())) {
            finished = true;
        } else {
            npc.lookAt(EntityAnchorArgument.Anchor.EYES, target, EntityAnchorArgument.Anchor.EYES );

            if (!npc.hasLineOfSight(target)){
                finished = true;
            }
        }

    }
}
