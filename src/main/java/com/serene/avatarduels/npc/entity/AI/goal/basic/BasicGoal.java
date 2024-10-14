package com.serene.avatarduels.npc.entity.AI.goal.basic;

import com.serene.avatarduels.npc.entity.AI.goal.BaseGoal;
import com.serene.avatarduels.npc.entity.SereneHumanEntity;

public abstract class BasicGoal extends BaseGoal {


    private int priority;

    public BasicGoal(String name, SereneHumanEntity npc, int priority) {
        super(name, npc);
        this.priority = priority;
    }


    public int getPriority() {
        return priority;
    }
}
