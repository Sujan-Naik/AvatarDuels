package com.serene.avatarduels.npc.entity.AI.goal.basic.combat;

import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;

public abstract class Combat extends BasicGoal implements EntityInteraction {

    protected LivingEntity entity;

    public Combat(String name, BendingNPC npc, int priority, LivingEntity entity) {
        super(name, npc, priority);

        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }


}
