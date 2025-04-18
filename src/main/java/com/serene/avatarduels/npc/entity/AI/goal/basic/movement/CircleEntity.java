package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.AI.sensing.Heightmap;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CircleEntity extends Movement implements EntityInteraction {

    private LivingEntity targetEntity;
    private double angularSpeed; // Speed of circling in radians

    public CircleEntity(String name, BendingNPC npc, int priority, double requiredDistance, double angularSpeed, LivingEntity targetEntity) {
        super(name, npc, priority, targetEntity.getPosition(0).subtract(npc.getForward().scale(requiredDistance)), requiredDistance); // Initialize with null for goalPos
        this.targetEntity = targetEntity;
        this.angularSpeed = angularSpeed;

    }

    private void updatePos() {

        Vec3 diff = goalPos.subtract(targetEntity.getPosition(0)).yRot((float) angularSpeed);
        Vec3 newGoalPos = targetEntity.getPosition(0).add(diff);
//        Vec3 newGoalPos = targetEntity.getOnPos().getCenter().add(radius * Math.cos(angle),0,radius * Math.sin(angle) );

        // Set the goal position for the NPC to move toward

        ChunkAccess chunkAccess = npc.level().getChunkAt(BlockPos.containing(newGoalPos));
        Heightmap.primeHeightmaps(chunkAccess, Arrays.stream(Heightmap.Types.values()).collect(Collectors.toSet()));

        Heightmap heightmap = Heightmap.getOrCreateHeightmapUnprimed( chunkAccess, Heightmap.Types.MOTION_BLOCKING_NO_BARRIERS_OR_WATER);

        newGoalPos = new Vec3(newGoalPos.x(), heightmap.getFirstAvailable( Math.abs((int) newGoalPos.x() % 16), Math.abs((int) newGoalPos.z() % 16)), newGoalPos.z());

        this.setGoalPos(newGoalPos); // Continuously set the new goal position
    }

    @Override
    public LivingEntity getEntity() {
        return targetEntity; // Return the target entity being circled
    }

    @Override
    public void tick() {
        if (npc == null || targetEntity == null || !npc.isAlive() || !targetEntity.isAlive()){
            setFinished(true);
        } else {
            if (npc.getPosition(0).distanceTo(goalPos) < 5) {
                this.updatePos(); // Update position continuously
                navigation.navigateToPos(goalPos);
            } else  if (npc.getBlockingPos(goalPos).distanceTo(npc.position().add(0,2,0)) < 1){ {
                    setFinished(true);
                }
            } else {
                if (navigation.getGoalPos() != goalPos){
                    navigation.navigateToPos(goalPos);

                }
            }
//            } else if (!isClearForMovementBetween(npc, npc.getPosition(0), goalPos, true)) {
//                this.updatePos();
//                if (!isClearForMovementBetween(npc, goalPos ,  npc.getPosition(0), true)) {
//                    setFinished(true);
//                } else {
//                    navigation.navigateToPos(goalPos);
//                }
//            }
        }

    }

//    public static boolean isClearForMovementBetween(HumanEntity entity, Vec3 startPos, Vec3 entityPos, boolean includeFluids) {
//        Vec3 vec3 = new Vec3(entityPos.x, entityPos.y + (double) entity.getBbHeight() * 0.5, entityPos.z);
//        return entity.level().clip(new ClipContext(startPos, vec3, ClipContext.Block.COLLIDER, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
//    }
}