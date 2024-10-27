package com.serene.avatarduels.npc.entity.AI.goal;

import com.serene.avatarduels.npc.entity.BendingNPC;

public abstract class BaseGoal {

    protected boolean finished;
    protected boolean inProgress;
    protected BendingNPC npc;
    private String name;
    private int priority;

    public BaseGoal(String name, BendingNPC npc) {
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

    public BendingNPC getNpc() {
        return npc;
    }

    public int getPriority() {
        return priority;
    }
}
