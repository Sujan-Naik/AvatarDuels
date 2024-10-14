package com.serene.avatarduels.npc.entity.AI.goal.complex;

import com.serene.avatarduels.npc.entity.AI.goal.BaseGoal;
import com.serene.avatarduels.npc.entity.AI.goal.GoalSelector;
import com.serene.avatarduels.npc.entity.AI.goal.NPCStates;
import com.serene.avatarduels.npc.entity.SereneHumanEntity;

public abstract class MasterGoal extends BaseGoal {

    protected GoalSelector actionGoalSelector;
    protected GoalSelector movementGoalSelector;

    protected GoalSelector lookGoalSelector;

    protected NPCStates state;

    public MasterGoal(String name, SereneHumanEntity npc) {
        super(name, npc);

        this.actionGoalSelector = new GoalSelector();
        this.movementGoalSelector = new GoalSelector();
        this.lookGoalSelector = new GoalSelector();
        this.state = NPCStates.RELAXED;
    }


    @Override
    public void tick() {
        actionGoalSelector.tick();
        movementGoalSelector.tick();
        lookGoalSelector.tick();
    }
}
