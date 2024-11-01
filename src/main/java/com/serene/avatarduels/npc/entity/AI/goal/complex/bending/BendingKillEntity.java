package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.airbending.AirScooter;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.CircleEntity;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.MoveToEntity;
import com.serene.avatarduels.npc.entity.AI.goal.basic.movement.RunFromEntity;
import com.serene.avatarduels.npc.entity.AI.goal.complex.combat.MasterCombat;
import com.serene.avatarduels.npc.entity.AI.sensing.CombatPositionSelector;
import com.serene.avatarduels.npc.entity.AI.sensing.Heightmap;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BendingKillEntity extends MasterCombat {

    private NPC_STATES state;
    private int lastChangedState;

    private static final int STATE_CHANGE_TICK_COOLDOWN = 60;

    private int lastAttemptedAbility;
    private static final int ABILITY_ATTEMPT_COOLDOWN = 5;
    private static final int MOBILITY_ATTEMPT_COOLDOWN = 20;

    private int lastSeen;
    private static final int ENTITY_MUST_SEE_COOLDOWN = 20;

    private int lastStrafed;
    private static final int STRAFE_COOLDOWN = 2;

    private Vec3 lastStuckPos;
    private int lastStuckRecorded;
    private static final int STUCK_TIME_DIFFERENCE = 100;

    private CombatPositionSelector positionSelector;

    public BendingKillEntity(String name, BendingNPC npc, LivingEntity target) {
        super(name, npc);
        entity = target;

        state = NPC_STATES.RUSHDOWN;

//        movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, 1, entity));
//        lookGoalSelector.addGoal(new LookAtEntity("Look", npc, 1, entity));

        this.lastChangedState = npc.tickCount - STATE_CHANGE_TICK_COOLDOWN + 1;
        this.lastAttemptedAbility = npc.tickCount;
        this.lastSeen = npc.tickCount;
        this.lastStrafed = npc.tickCount;
        npc.getTargetSelector().setCurrentTarget(target);

        this.positionSelector = new CombatPositionSelector(npc, target);

        this.lastStuckPos = npc.getPosition(0);
        this.lastStuckRecorded = npc.tickCount;
    }


    private static final double DISTANCE_TOLERANCE = 1; // Tolerance value to account for floating point precision


    @Override
    public void tick() {
        super.tick();

        if (entity == null || !entity.isAlive() || !npc.isAlive()){
            this.setFinished(true);
            return;
        }



        double distance = Math.sqrt(npc.distanceToSqr(entity));

        if (npc.tickCount - lastStuckRecorded > STUCK_TIME_DIFFERENCE) {
            if (npc.getPosition(0).distanceToSqr(lastStuckPos) < 1 && !bendingGoalSelector.hasGoal()){
                // Bukkit.broadcastMessage("We're stuck so we're strafing");
                movementGoalSelector.removeAllGoals();

                positionSelector.getChunkGrid().stream().min(Comparator.comparingDouble(value -> value.distanceToSqr(npc.getPosition(0))))
                        .ifPresentOrElse(vec3 -> {
                            // Bukkit.broadcastMessage(String.valueOf(vec3));
                            if (npc.isBusyBending()){
                                // Bukkit.broadcastMessage("fuck");
                            }
                            if ( npc.getNavigation().getGoalPos() != vec3) {
                                // Bukkit.broadcastMessage("even a thing ?");
                                npc.getNavigation().navigateToPos(vec3);
                            }

                            }, () -> {
                            npc.moveRelative(10, npc.getForward());
                            // Bukkit.broadcastMessage("WE'RE GOING FORWARDS!!");
                        });
                if (CoreAbility.hasAbility(Bukkit.getPlayer(npc.getUUID()), AirScooter.class)){
                    CoreAbility.getAbility(Bukkit.getPlayer(npc.getUUID()), AirScooter.class).remove();
                }
                this.lastStuckPos = npc.getPosition(0);
                this.lastStuckRecorded = npc.tickCount;
                return;
            }
//
//            else {
//                this.lastStuckPos = npc.getPosition(0);
//                this.lastStuckRecorded = npc.tickCount;
//            }
        }


        ChunkAccess chunkAccess = npc.level().getChunkAt(npc.getOnPos());
        Heightmap.primeHeightmaps(chunkAccess, Arrays.stream(Heightmap.Types.values()).collect(Collectors.toSet()));

        Heightmap heightmap = Heightmap.getOrCreateHeightmapUnprimed( chunkAccess, Heightmap.Types.MOTION_BLOCKING_NO_BARRIERS_OR_WATER);
        Heightmap heightmapWater = Heightmap.getOrCreateHeightmapUnprimed( chunkAccess, Heightmap.Types.MOTION_BLOCKING_NO_BARRIERS);


        if (heightmap.getFirstAvailable( Math.abs((int) npc.getX() % 16), Math.abs((int) npc.getZ() % 16)) !=
                heightmapWater.getFirstAvailable( Math.abs((int) npc.getX() % 16), Math.abs((int) npc.getZ() % 16))){

            positionSelector.getChunkGrid().stream().min(Comparator.comparingDouble(value -> value.distanceToSqr(npc.getPosition(0))))
                    .ifPresentOrElse(vec3 -> {
                        if ( npc.getNavigation().getGoalPos() != vec3) {
                            // Bukkit.broadcastMessage("yeet 2");
                            npc.getNavigation().navigateToPos(vec3);
                        }

                    }, () -> {
                        // Bukkit.broadcastMessage("forward out of da water ?");
                    npc.moveRelative(10, npc.getForward()); });

            movementGoalSelector.removeAllGoals();
            AbilityUsages usage = getRandom(ABIL_CATEGORISATIONS.MOVEMENT);
            bendingGoalSelector.addGoal(usage.makeGoal(npc));
            return;
        }

        if (distance < 50) {
            double health = npc.getHealth();

            boolean canChangeState = npc.tickCount - lastChangedState > STATE_CHANGE_TICK_COOLDOWN;

            if (npc.tickCount - lastStrafed > STRAFE_COOLDOWN) {
                lastStrafed = npc.tickCount;
                npc.getMoveControl().strafe(0, 0.1f);
            }

            if (!npc.hasCompleteLineOfSight(entity) ) {
                if (npc.tickCount - lastSeen > ENTITY_MUST_SEE_COOLDOWN && !movementGoalSelector.doingGoal("Enter LOS")) {
                    movementGoalSelector.removeAllGoals();
                    movementGoalSelector.addGoal(new MoveToEntity("Enter LOS", npc, 1, 5, entity));
                     // Bukkit.broadcastMessage("Attempting to enter LOS");

                }
            } else {
                lastSeen = npc.tickCount;
                if (movementGoalSelector.doingGoal("Enter LOS")) {
                    // // Bukkit.broadcastMessage("Removed defunt LOS goal");

                    movementGoalSelector.removeCurrentGoal();
                }
            }

            if (canChangeState) {
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
                    // // Bukkit.broadcastMessage("circling");
                    circleTarget(idealRange);
                }

                lastChangedState = npc.tickCount;
                // // Bukkit.broadcastMessage(npc.displayName + ": " + state.name());
                if (npc.isBusyBending()) {
                    // // Bukkit.broadcastMessage("Busy");
                }
            }


            if (npc.tickCount - lastAttemptedAbility > ABILITY_ATTEMPT_COOLDOWN && !npc.isBusyBending()) {
                AbilityUsages usage = getRandom(state.getAbilityUsagesList());
                bendingGoalSelector.addGoal(usage.makeGoal(npc));
                this.lastAttemptedAbility = npc.tickCount;
            }
        } else {
            if ( ! positionSelector.tick() ){
                closeTheGap(60);
            } else {
                movementGoalSelector.removeAllGoals();

                if (npc.tickCount - lastAttemptedAbility > MOBILITY_ATTEMPT_COOLDOWN && !npc.isBusyBending() ) {
//                    Vec3 accessibleNavPos = npc.getNavigation().getLowestYAdjustedGoalPos();

                    AbilityUsages usage = getRandom(ABIL_CATEGORISATIONS.MOVEMENT);
                    bendingGoalSelector.addGoal(usage.makeGoal(npc));
                    this.lastAttemptedAbility = npc.tickCount;
                }
            }
        }



    }




    private void closeTheGap(double requiredDistance){
        if (movementGoalSelector.doingGoal("Run") || movementGoalSelector.doingGoal("CircleTarget")) {
            movementGoalSelector.removeCurrentGoal();
        }
        if (!movementGoalSelector.hasGoal()) {
            movementGoalSelector.addGoal(new MoveToEntity("Chase", npc, 1, requiredDistance, entity));
        }

    }

    private void widenTheGap(double requiredDistance){
        if (movementGoalSelector.doingGoal("Chase") || movementGoalSelector.doingGoal("CircleTarget") ) {
            movementGoalSelector.removeCurrentGoal();
        }
        if (!movementGoalSelector.hasGoal()) {
            movementGoalSelector.addGoal(new RunFromEntity("Run", npc, 1, requiredDistance, entity));
        }
    }

    private void circleTarget(double requiredDistance) {


        // Remove any existing movement goals if the NPC is currently chasing
        if (movementGoalSelector.doingGoal("Chase") || movementGoalSelector.doingGoal("Run")) {
            movementGoalSelector.removeCurrentGoal();
        }

        // Define an angular speed (you can adjust this value as needed)
        double angularSpeed = Math.toRadians(45); // Adjust this value based on desired circling speed

        // Add the CircleEntity goal to the movement goal selector
        if (!movementGoalSelector.hasGoal()) {
            movementGoalSelector.addGoal(new CircleEntity("CircleTarget", npc, 1, requiredDistance, angularSpeed, entity));
        }
    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}
