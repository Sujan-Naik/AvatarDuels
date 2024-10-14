package com.serene.avatarduels.npc.entity.AI.goal.basic.interaction;

import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.goal.interfaces.BlockInteraction;
import com.serene.avatarduels.npc.entity.SereneHumanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BreakBlock extends BasicGoal implements BlockInteraction {

    private BlockPos blockPos;
    private Level level;

    public BreakBlock(String name, SereneHumanEntity npc, BlockPos blockPos) {
        super(name, npc, 1);
        level = npc.level();
        this.blockPos = blockPos;
    }

    @Override
    public void tick() {
        if (!level.getBlockState(blockPos).getBlock().isDestroyable()) {
            finished = true;
        } else {
            //level.getBlockState(blockPos).getBlock().playerDestroy(level, npc, blockPos, level.getBlockState(blockPos),
            //            null, npc.getItemInHand(InteractionHand.MAIN_HAND));
            level.destroyBlock(blockPos, true);
            finished = true;
        }

    }

    @Override
    public Block getBlock() {
        return null;
    }

    @Override
    public BlockPos getBlockPos() {
        return null;
    }
}
