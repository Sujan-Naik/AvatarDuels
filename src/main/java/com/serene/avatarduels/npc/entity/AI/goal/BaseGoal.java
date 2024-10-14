package com.serene.avatarduels.npc.entity.AI.goal;

import com.serene.avatarduels.npc.entity.SereneHumanEntity;

public abstract class BaseGoal {

    protected boolean finished;
    protected boolean inProgress;
    protected SereneHumanEntity npc;
    private String name;
    private int priority;

    public BaseGoal(String name, SereneHumanEntity npc) {
        this.name = name;
        this.npc = npc;
    }

    public abstract void tick();

    public String getName() {
        return name;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public SereneHumanEntity getNpc() {
        return npc;
    }

    public int getPriority() {
        return priority;
    }
}
