package com.serene.avatarduels.npc.entity.AI.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob entity, Level world) {
        super(entity, world);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, range);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.mob.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public Path createPath(BlockPos target, @javax.annotation.Nullable Entity entity, int distance) { // Paper - EntityPathfindEvent
        LevelChunk levelChunk = this.level
                .getChunkSource()
                .getChunkNow(SectionPos.blockToSectionCoord(target.getX()), SectionPos.blockToSectionCoord(target.getZ()));
        if (levelChunk == null) {
            return null;
        } else {
            if (levelChunk.getBlockState(target).isAir()) {
                BlockPos blockPos = target.below();

                while (blockPos.getY() > this.level.getMinBuildHeight() && levelChunk.getBlockState(blockPos).isAir()) {
                    blockPos = blockPos.below();
                }

                if (blockPos.getY() > this.level.getMinBuildHeight()) {
                    return super.createPath(blockPos.above(), entity, distance); // Paper - EntityPathfindEvent
                }

                while (blockPos.getY() < this.level.getMaxBuildHeight() && levelChunk.getBlockState(blockPos).isAir()) {
                    blockPos = blockPos.above();
                }

                target = blockPos;
            }

            if (!levelChunk.getBlockState(target).isSolid()) {
                return super.createPath(target, entity, distance); // Paper - EntityPathfindEvent
            } else {
                BlockPos blockPos2 = target.above();

                while (blockPos2.getY() < this.level.getMaxBuildHeight() && levelChunk.getBlockState(blockPos2).isSolid()) {
                    blockPos2 = blockPos2.above();
                }

                return super.createPath(blockPos2, entity, distance); // Paper - EntityPathfindEvent
            }
        }
    }

    @Override
    public Path createPath(Entity entity, int distance) {
        return this.createPath(entity.blockPosition(), entity, distance); // Paper - EntityPathfindEvent
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int i = this.mob.getBlockY();
            BlockState blockState = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)i, this.mob.getZ()));
            int j = 0;

            while (blockState.is(Blocks.WATER)) {
                blockState = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)(++i), this.mob.getZ()));
                if (++j > 16) {
                    return this.mob.getBlockY();
                }
            }

            return i;
        } else {
            return Mth.floor(this.mob.getY() + 0.5);
        }
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }

            for (int i = 0; i < this.path.getNodeCount(); i++) {
                Node node = this.path.getNode(i);
                if (this.level.canSeeSky(new BlockPos(node.x, node.y, node.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }
    }

    protected boolean hasValidPathType(PathType pathType) {
        return pathType != PathType.WATER && pathType != PathType.LAVA && pathType != PathType.OPEN;
    }

    public void setCanOpenDoors(boolean canPathThroughDoors) {
        this.nodeEvaluator.setCanOpenDoors(canPathThroughDoors);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean canEnterOpenDoors) {
        this.nodeEvaluator.setCanPassDoors(canEnterOpenDoors);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean avoidSunlight) {
        this.avoidSun = avoidSunlight;
    }

    public void setCanWalkOverFences(boolean canWalkOverFences) {
        this.nodeEvaluator.setCanWalkOverFences(canWalkOverFences);
    }
}
