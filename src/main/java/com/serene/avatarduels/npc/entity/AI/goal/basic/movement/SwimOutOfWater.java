package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.phys.Vec3;

public class SwimOutOfWater extends Movement{
    public SwimOutOfWater(String name, BendingNPC npc, int priority, Vec3 goalPos, double requiredDistance) {
        super(name, npc, priority, goalPos, requiredDistance);
    }
}
