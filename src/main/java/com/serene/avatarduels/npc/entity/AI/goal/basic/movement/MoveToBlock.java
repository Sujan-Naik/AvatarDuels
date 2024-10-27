package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.interfaces.BlockInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class MoveToBlock extends Movement implements BlockInteraction {

    private BlockPos blockPos;


    public MoveToBlock(String name, BendingNPC npc, BlockPos blockPos) {
        super(name, npc, 1, blockPos.getCenter(), 3);
        this.setGoalPos(blockPos.getCenter().add(0,2,0));
        this.blockPos = blockPos;
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public Block getBlock() {
        return npc.level().getBlockState(blockPos).getBlock();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }
}
