package com.serene.avatarduels.npc.entity.AI.pathfinding;

import com.serene.avatarduels.npc.entity.AI.control.MoveControl;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.List;

public class Navigation {

    private final HumanEntity humanEntity;

    private final MoveControl moveControl;

    private Vec3 goalPos;

    private Vec3 newPos;

    private boolean isStuck = false;
    

    public Navigation(HumanEntity humanEntity){
        this.humanEntity = humanEntity;
        this.moveControl = humanEntity.getMoveControl();
    }

    private static final List<Direction> directions = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public void navigateToPos(Vec3 pos){
        this.goalPos = pos;
    }

    public Vec3 getGoalPos() {
        return goalPos;
    }

    private boolean isSolid(Vec3 pos){
        return !humanEntity.level().getBlockState(BlockPos.containing(pos)).isAir();
    }

    private boolean isAir(Vec3 pos){
        return humanEntity.level().getBlockState(BlockPos.containing(pos)).isAir();
    }


    private boolean isAcceptableDirection(Direction direction){
        Vec3 currentPos = humanEntity.getOnPos().getCenter();
        Vec3 newPos = currentPos.relative(direction, 1);

        return (  (isAir(newPos.relative(Direction.UP, 1)) && isSolid(newPos) ) || // forward
                (isAir(newPos) && isSolid(newPos.relative(Direction.DOWN, 1)) ) || // down
                (isAir(newPos.relative(Direction.UP, 2)) && isSolid(newPos.relative(Direction.UP, 1)) )); //up
    }

    public void tick(){
        if (!isStuck && goalPos != null && !(humanEntity instanceof BendingNPC bendingNPC && bendingNPC.isBusyBending())) {
                directions.stream().filter(this::isAcceptableDirection)
                        .min(Comparator.comparingDouble(value -> humanEntity.getPosition(0).relative(value, 1).distanceToSqr(goalPos)))
                        .ifPresentOrElse(direction -> {
                            newPos = humanEntity.getOnPos().getCenter().relative(direction, 1);
                            if ((isAir(newPos.relative(Direction.UP, 2)) && isSolid(newPos.relative(Direction.UP, 1)) )){
                                moveControl.setWantedPosition(newPos.x, newPos.y + 1, newPos.z, 10);
                                humanEntity.getJumpControl().jump();
                            } else {
                                moveControl.setWantedPosition(newPos.x, newPos.y, newPos.z, 10);

                            }
                        }, () -> {
                            isStuck = true;
                        });

        }
    }

    public boolean isStuck() {
        return isStuck;
    }

    public void setStuck(boolean stuck) {
        isStuck = stuck;
    }
}
