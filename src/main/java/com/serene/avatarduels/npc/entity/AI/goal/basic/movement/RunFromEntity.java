package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.Vec3Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

public class RunFromEntity extends Movement implements EntityInteraction {

    private LivingEntity targetEntity;


    public RunFromEntity(String name, BendingNPC npc, int priority, double requiredDistance, LivingEntity targetEntity) {
        super(name, npc, priority, null, requiredDistance);
        this.targetEntity = targetEntity;

        setNewGoalPos();
    }

    private void setNewGoalPos(){
        Vec3 targetEntityPos = targetEntity.getPosition(0);
        Vec3 playerCurrentPos = npc.getPosition(0);

        Vec3 runAwayDir = playerCurrentPos.subtract(targetEntityPos).multiply(1,0,1).normalize();

        Vec3 goalPos =  playerCurrentPos.add(runAwayDir.scale(requiredDistance * 4));

        npc.getNavigation().navigateToPos(goalPos);
    }


    @Override
    public LivingEntity getEntity() {
        return targetEntity;
    }

    @Override
    public void tick() {
        if (npc.getNavigation().isStuck()){
            setNewGoalPos();
            npc.getNavigation().setStuck(false);
        }
        if (npc.distanceTo(targetEntity) > requiredDistance){
            finished = true;
        }
    }


}
