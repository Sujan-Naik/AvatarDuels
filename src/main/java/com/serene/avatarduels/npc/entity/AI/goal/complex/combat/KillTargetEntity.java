package com.serene.avatarduels.npc.entity.AI.goal.complex.combat;

import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.MoveToEntity;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class KillTargetEntity extends MasterCombat {

    private int lastShotBowTicks;

    private int lastPunchTicks;
    private int lastBentTicks;

    public KillTargetEntity(String name, BendingNPC npc, LivingEntity target) {
        super(name, npc);
        entity = target;
//        if (entity == null){
//            finished = true;
//        }

        this.lastShotBowTicks = npc.tickCount;
        this.lastPunchTicks = npc.tickCount;
        this.lastBentTicks = npc.tickCount;

        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, 1, entity));
//        lookGoalSelector.addGoal(new LookAtEntity("Look", npc, 1, entity));

        npc.getTargetSelector().setCurrentTarget(target);
    }

//    private void tickGoalSelectors(){
//        actionGoalSelector.tick();
//        if (!Bukkit.getPlayer(npc.getUUID()).isSneaking() || BendingPlayer.getBendingPlayer("god")) {
//            movementGoalSelector.tick();
//            lookGoalSelector.tick();
//        }
//    }
//
    @Override
    public void tick() {


        super.tick();

        double distance = npc.distanceToSqr(entity);


        if (!movementGoalSelector.doingGoal("Chase")) {
            movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, 15, entity));
        }

//        if (!lookGoalSelector.doingGoal("Look")) {
//            lookGoalSelector.addGoal(new LookAtEntity("Look", npc, 1, entity));
//        }

//        if (distance <= 9 && npc.tickCount - lastPunchTicks > 5) {
//            actionGoalSelector.addGoal(new PunchEntity("Punch", npc, 2, entity));
//            this.lastPunchTicks = npc.tickCount;
//        } else

        if (npc.tickCount - lastBentTicks > 2){
            boolean stillUsing = false;

            if (!stillUsing) {
                npc.useAbility(getRandom(Arrays.stream(AbilityUsages.values()).collect(Collectors.toSet())));
                this.lastBentTicks = npc.tickCount;
            }

        }



    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}
