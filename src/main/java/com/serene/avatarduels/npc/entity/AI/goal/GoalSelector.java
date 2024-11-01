package com.serene.avatarduels.npc.entity.AI.goal;

import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.PriorityQueue;

public class GoalSelector {

    public PriorityQueue<BasicGoal> goals;

    public GoalSelector() {
        goals = new PriorityQueue<>(Comparator.comparingInt(BasicGoal::getPriority));
    }


    public void addGoal(BasicGoal goal) {
        goals.add(goal);
    }

    public boolean hasGoal() {
        if (goals.isEmpty()) {
            return false;
        }
        return true;
    }

    public void tick() {
        if (goals.peek() != null) {
            BasicGoal currentBasicGoal = goals.peek();

            if (currentBasicGoal.isFinished()) {
                removeCurrentGoal();
            } else {
                if (currentBasicGoal instanceof BendingUseAbility bendingUseAbility){
                    // // Bukkit.broadcastMessage(bendingUseAbility.getName());
                }
                currentBasicGoal.tick();
            }
            //// // Bukkit.broadcastMessage(currentBasicGoal.getName());

        }
    }

    public boolean doingGoal(String name) {
        if (hasGoal()) {
            return name.equals(goals.peek().getName());
        }
        return false;
    }

    public BasicGoal getCurrentGoal(){
        return goals.peek();
    }



    public void removeCurrentGoal() {
        goals.remove();
    }

    public void removeAllGoals() {
        goals.removeIf(basicGoal -> true);
    }
}
