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

    private static final int VERY_CLOSE = 5, CLOSE = 10, MEDIUM = 15, FAR = 25;

    private static final int STATE_CHANGE_TICK_COOLDOWN = 60;

    public BendingKillEntity(String name, BendingNPC npc, LivingEntity target) {
        super(name, npc);
        entity = target;

        state = NPC_STATES.NEUTRAL;

//        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, 1, entity));
//        lookGoalSelector.addGoal(new LookAtEntity("Look", npc, 1, entity));

        this.lastChangedState = npc.tickCount - STATE_CHANGE_TICK_COOLDOWN + 1;
        npc.getTargetSelector().setCurrentTarget(target);
    }

    private static final List<AbilityUsages> POINT_BLANK_ABILITIES = List.of(AbilityUsages.BLAZE, AbilityUsages.EARTHLINE, AbilityUsages.ACCRETION, AbilityUsages.WALLOFFIRE);
    private static final List<AbilityUsages> RUSHDOWN_ABILITIES = List.of(AbilityUsages.GALEGUST, AbilityUsages.SONICBLAST, AbilityUsages.SHOCKWAVE, AbilityUsages.AIRSWIPE);
    private static final List<AbilityUsages> NEUTRAL_ABILITIES = List.of(AbilityUsages.FIREBURST, AbilityUsages.LIGHTNING);
    private static final List<AbilityUsages> KEEPAWAY_ABILITIES = List.of(AbilityUsages.MUDSURGE, AbilityUsages.FIREBALL, AbilityUsages.FIRECOMET);

    @Override
    public void tick() {
        super.tick();

        double distance = Math.sqrt(npc.distanceToSqr(entity));

        boolean canChangeState = npc.tickCount - lastChangedState > STATE_CHANGE_TICK_COOLDOWN;
            switch (state) {
                case POINT_BLANK -> {
                    bendingGoalSelector.addGoal(getRandom(POINT_BLANK_ABILITIES).makeGoal(npc));


                }
                case RUSHDOWN -> {
                    bendingGoalSelector.addGoal(getRandom(RUSHDOWN_ABILITIES).makeGoal(npc));
//                    bendingGoalSelector.addGoal(new SourcedAbility("Accretion", npc, "Accretion", 10, 10, Element.EARTH));

                }

                case NEUTRAL -> {
                    bendingGoalSelector.addGoal(getRandom(NEUTRAL_ABILITIES).makeGoal(npc));



                }
                case KEEP_AWAY -> {
                    bendingGoalSelector.addGoal(getRandom(KEEPAWAY_ABILITIES).makeGoal(npc));


                }
            }
    }




    private void closeTheGap(int requiredDistance){
        if (movementGoalSelector.doingGoal("Chase")) {
            movementGoalSelector.removeCurrentGoal();
        }
        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, requiredDistance, entity));

    }

    private void widenTheGap(int requiredDistance){
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
