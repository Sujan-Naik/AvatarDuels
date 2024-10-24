package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.CircleEntity;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.MoveToEntity;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.RunFromEntity;
import com.serene.avatarduels.npc.entity.AI.goal.complex.combat.MasterCombat;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.List;

public class BendingKillEntity extends MasterCombat {

    private NPC_STATES state;
    private int lastChangedState;

    private static final int STATE_CHANGE_TICK_COOLDOWN = 60;

    private int lastAttemptedAbility;
    private static final int ABILITY_ATTEMPT_COOLDOWN = 1;

    public BendingKillEntity(String name, BendingNPC npc, LivingEntity target) {
        super(name, npc);
        entity = target;

        state = NPC_STATES.NEUTRAL;

//        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, 1, entity));
//        lookGoalSelector.addGoal(new LookAtEntity("Look", npc, 1, entity));

        this.lastChangedState = npc.tickCount - STATE_CHANGE_TICK_COOLDOWN + 1;
        this.lastAttemptedAbility = npc.tickCount;
        npc.getTargetSelector().setCurrentTarget(target);
    }


    private static final double DISTANCE_TOLERANCE = 3; // Tolerance value to account for floating point precision


    @Override
    public void tick() {
        super.tick();

        double distance = Math.sqrt(npc.distanceToSqr(entity));
        double health = npc.getHealth();

        boolean canChangeState = npc.tickCount - lastChangedState > STATE_CHANGE_TICK_COOLDOWN;

        if (canChangeState){
            state = NPC_STATES.getBestState(distance, health);
            double idealRange = state.getIdealRange();

            if (distance > idealRange + DISTANCE_TOLERANCE) {
                // Too far from the target
                closeTheGap(idealRange);
            } else if (distance < idealRange - DISTANCE_TOLERANCE) {
                // Too close to the target
                widenTheGap(idealRange);
            } else {
                // Within the tolerance range of the ideal distance, switch to circling
                circleTarget(idealRange);
            }
        }

        if (npc.tickCount - lastAttemptedAbility > ABILITY_ATTEMPT_COOLDOWN){
            bendingGoalSelector.addGoal(getRandom(state.getAbilityUsagesList()).makeGoal(npc));
            this.lastAttemptedAbility = npc.tickCount;


        }



    }




    private void closeTheGap(double requiredDistance){
        if (movementGoalSelector.doingGoal("Chase")) {
            movementGoalSelector.removeCurrentGoal();
        }
        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, requiredDistance, entity));

    }

    private void widenTheGap(double requiredDistance){
        if (movementGoalSelector.doingGoal("Chase")) {
            movementGoalSelector.removeCurrentGoal();
        }
        movementGoalSelector.addGoal(new RunFromEntity("Chase", npc,1, requiredDistance, entity ));

    }

    private void circleTarget(double requiredDistance) {

        Bukkit.broadcastMessage("circling");
        // Remove any existing movement goals if the NPC is currently chasing
        if (movementGoalSelector.doingGoal("Chase")) {
            movementGoalSelector.removeCurrentGoal();
        }

        // Define an angular speed (you can adjust this value as needed)
        double angularSpeed = 1.0; // Adjust this value based on desired circling speed

        // Add the CircleEntity goal to the movement goal selector
        movementGoalSelector.addGoal(new CircleEntity("CircleTarget", npc, 1, requiredDistance, angularSpeed, entity));
    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}
