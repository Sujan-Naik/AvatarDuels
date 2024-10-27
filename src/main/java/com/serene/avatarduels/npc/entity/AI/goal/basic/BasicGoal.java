package com.serene.avatarduels.npc.entity.AI.goal.basic;

import com.serene.avatarduels.npc.entity.AI.goal.BaseGoal;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.entity.BendingNPC;

public abstract class BasicGoal extends BaseGoal {


    private int priority;

    public BasicGoal(String name, BendingNPC npc, int priority) {
        super(name, npc);
        this.priority = priority;
    }


    public int getPriority() {
        return priority;
    }
}
