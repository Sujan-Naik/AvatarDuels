package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
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




    @Override
    public void tick() {
        super.tick();

        double distance = Math.sqrt(npc.distanceToSqr(entity));
        double health = npc.getHealth();

        boolean canChangeState = npc.tickCount - lastChangedState > STATE_CHANGE_TICK_COOLDOWN;

        if (canChangeState){
            state = NPC_STATES.getBestState(distance, health);
            if (state.getIdealRange() < distance){
                closeTheGap(state.getIdealRange());
            } else {
                widenTheGap(state.getIdealRange());
            }
            lastChangedState = npc.tickCount;
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


    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}
