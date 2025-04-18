package com.serene.avatarduels.npc.entity.AI.goal.basic.look;

import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;

public class LookAtEntity extends BasicGoal implements EntityInteraction {

    private LivingEntity entity;

    public LookAtEntity(String name, BendingNPC npc, int priority, LivingEntity entity) {
        super(name, npc, priority);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (entity == null || !entity.isAlive()) {
            finished = true;
        } else {
            npc.lookControl.setLookAt(entity);
        }
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }
}
