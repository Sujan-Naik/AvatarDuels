package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.pathfinding.Navigation;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.phys.Vec3;

public abstract class MovementAbility extends BendingUseAbility {

    protected Vec3 goalPos;

    protected double requiredDistance;

    protected Navigation navigation;


    public MovementAbility(String name, BendingNPC npc, String abilityName, double requiredDistance) {
        super(name, npc, abilityName);

        this.goalPos = target.getPosition(0);
    }

    @Override
    public boolean shouldStart() {
        return target != null && target.isAlive();
    }

    public double getDistance() {
        return goalPos.distanceTo(npc.getOnPos().getCenter());
    }


    @Override
    public void tick(){
        super.tick();
        if (goalPos != null) {
            if (getDistance() > requiredDistance ) {
//                npc.getNavigation().moveTo(goalPos.x, goalPos.y, goalPos.z, 10);
                //  npc.getNavigation().createPath(BlockPos.containing(goalPos), 1000);
            } else {
                remove();
            }
        } else {
            remove();
        }

        if (!npc.hasClearRayForward()){
            this.remove();
        }
    }


}
