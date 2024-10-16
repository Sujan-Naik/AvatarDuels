package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;

public class MoveToEntity extends Movement implements EntityInteraction {

    private LivingEntity targetEntity;

    public MoveToEntity(String name, BendingNPC npc, int priority, double requiredDistance, LivingEntity targetEntity) {
        super(name, npc, priority, targetEntity.getPosition(0), requiredDistance);
        this.targetEntity = targetEntity;
    }

    private void updatePos() {
        this.setGoalPos(targetEntity.getPosition(0));
    }

    @Override
    public LivingEntity getEntity() {
        return targetEntity;
    }

    @Override
    public void tick() {
        super.tick();
        this.updatePos();
    }
}
