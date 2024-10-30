package com.serene.avatarduels.npc.entity.AI.pathfinding;

import com.serene.avatarduels.npc.entity.AI.control.JumpControl;
import com.serene.avatarduels.npc.entity.AI.control.MoveControl;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Navigation {

    private final HumanEntity humanEntity;

    private final MoveControl moveControl;

    private final JumpControl jumpControl;



    private Vec3 goalPos;
    private Vec3 adjustedGoalPos;

    private Vec3 newPos;

    private boolean isStuck = false;

    private long sinceLastPathRefresh;

    private NavigationMesh navigationMesh;

    public Navigation(HumanEntity humanEntity){
        this.humanEntity = humanEntity;
        this.moveControl = humanEntity.getMoveControl();
        this.jumpControl = humanEntity.getJumpControl();
        this.sinceLastPathRefresh = humanEntity.tickCount;

        this.navigationMesh = new NavigationMesh(humanEntity);
    }


    public void navigateToPos(Vec3 pos){
        if (pos != null) {
            this.goalPos = pos;
            adjustedGoalPos = null;

//            humanEntity.level().setBlock(BlockPos.containing(pos), new BlockState(Blocks.DIRT,  n ));
            Bukkit.broadcastMessage(String.valueOf(pos));
        }
    }

    public Vec3 getGoalPos() {
        return goalPos;
    }

    public Vec3 getLowestYAdjustedGoalPos(){
        if (adjustedGoalPos == null) {
             adjustedGoalPos = goalPos;
            int maximumIterations = (int) (384 - goalPos.y());

            do {
                adjustedGoalPos = adjustedGoalPos.add(0, 1, 0);
                maximumIterations--;
            } while (!humanEntity.hasClearRay(adjustedGoalPos) && maximumIterations > 0);

            if (maximumIterations < 0) {
                Bukkit.broadcastMessage("no clear goalPos");
                return goalPos;
            }
        }
        return adjustedGoalPos;
    }


//    private boolean isSolid(Vec3 pos){
//        return !humanEntity.level().getBlockState(BlockPos.containing(pos)).isAir();
//    }
//
//    private boolean isAir(Vec3 pos){
//        return humanEntity.level().getBlockState(BlockPos.containing(pos)).isAir();
//    }
//
//
//    private boolean isAcceptableDirection(Direction direction){
//        Vec3 floorPos = humanEntity.getOnPos().getCenter();
//        Vec3 newPos = floorPos.relative(direction, 1);
//
//        return ( (isAir(newPos.relative(Direction.UP, 1 + humanEntity.getJumpBoostPower())) && isSolid(newPos) ) || // forward
//                (isAir(newPos) && isSolid(newPos.relative(Direction.DOWN, 1)) ) || // down
//                (isAir(newPos.relative(Direction.UP, 2 + humanEntity.getJumpBoostPower())) && isSolid(newPos.relative(Direction.UP, 1 + humanEntity.getJumpBoostPower())) )); //up
//    }
//
    private static final int PATH_REFRESH_CD = 100;

    private List<NavigationMesh.Node> currentPath = new ArrayList<>();
    public void tick(){


        if ((humanEntity instanceof BendingNPC bendingNPC && bendingNPC.isBusyBending())){
            return;
        }

        if (goalPos != null ) {
            if (currentPath.isEmpty()){
                currentPath = navigationMesh.getPath(goalPos);
                if (currentPath.isEmpty()){

                } else {
                    newPos = currentPath.removeFirst().getPos();
                    if (newPos.y > humanEntity.getOnPos().getY() + 1) {
                        jumpControl.jump();
                    }
                    moveControl.setWantedPosition(newPos.x, newPos.y, newPos.z, 10);
                }
            } else {
                if (humanEntity.tickCount - sinceLastPathRefresh > PATH_REFRESH_CD){
                    currentPath = navigationMesh.getPath(goalPos);
                    newPos = currentPath.removeFirst().getPos();
                    sinceLastPathRefresh = humanEntity.tickCount;
                }

                else if ( newPos.distanceTo(humanEntity.getOnPos().getCenter()) < 1){
                    newPos = currentPath.removeFirst().getPos();
                }
                if (newPos.y > humanEntity.getOnPos().getY() + 1){
                    jumpControl.jump();
                }
                moveControl.setWantedPosition(newPos.x, newPos.y, newPos.z, 10);
            }
        }

    }

    public boolean isStuck() {
        return isStuck;
    }

    public void setStuck(boolean stuck) {
        isStuck = stuck;
    }
}
