package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.pathfinding.Navigation;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.Vec3Utils;
import net.minecraft.world.phys.Vec3;

public abstract class Movement extends BasicGoal {

    private Vec3 goalPos;

    protected double requiredDistance;

    private Navigation navigation;

    public Movement(String name, BendingNPC npc, int priority, Vec3 goalPos, double requiredDistance) {
        super(name, npc, priority);

        this.goalPos = goalPos;
        this.requiredDistance = requiredDistance;

        navigation = npc.getNavigation();

    }

    public Vec3 getGoalPos() {
        return goalPos;
    }

    public void setGoalPos(Vec3 goalPos) {
        this.goalPos = goalPos;
    }

    public double getDistance() {
        return goalPos.distanceTo(npc.getOnPos().getCenter());
    }

    private boolean isNavigating;
    @Override
    public void tick() {

//            if (npc.getNavigation().isStuck() ) {
//                npc.getNavigation().recomputePath();
//            }

//        if (npc.getNavigation().isStuck()){
//            npc.getNavigation().moveTo(goalPos.x, goalPos.y, goalPos.z, 10);
//
//        }
        if (goalPos != null) {
            if (getDistance() > requiredDistance ) {
                if (navigation.getGoalPos() != goalPos) {
                    navigation.navigateToPos(goalPos);
                }

//                npc.getNavigation().moveTo(goalPos.x, goalPos.y, goalPos.z, 10);
                //  npc.getNavigation().createPath(BlockPos.containing(goalPos), 1000);
            } else {
                navigation.navigateToPos(null);
                finished = true;
            }
        } else {
            finished = true;
            navigation.navigateToPos(null);

        }
    }
}
