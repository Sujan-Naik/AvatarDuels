package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.sourced;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class SourcedAbility extends RangedAbility {

    private double maxSourceRange;


    public SourcedAbility(String name, BendingNPC npc, String abilityName, double maxRange, double maxSourceRange, Element element) {
        super(name, npc, abilityName, maxRange);


        this.maxSourceRange = maxSourceRange;
        start();
    }

    @Override
    public boolean shouldStart() {
        return super.shouldStart() && getSource()!=null;
    }

    private Comparator<Block> getBestSource(Player player, org.bukkit.entity.LivingEntity target){
        return Comparator.comparingDouble(block ->
                (player.getLocation().distanceSquared(block.getLocation()) + (target.getLocation().distanceSquared(block.getLocation()))));
    }



    private Block getSource(){
        org.bukkit.entity.LivingEntity bukkitTarget = (org.bukkit.entity.LivingEntity) Bukkit.getEntity(target.getUUID());
        Player player = Bukkit.getPlayer(npc.getUUID());

        Set<Block> nearbyBlocks = GeneralMethods.getBlocksAroundPoint(player.getLocation(), maxSourceRange).stream().filter(block -> block.getRelative(BlockFace.NORTH).isPassable()).collect(Collectors.toSet());

        Block source = null;

        switch (CoreAbility.getAbility(getAbilityName()).getElement().getName()){
            case "Earth" ->
                    source = nearbyBlocks.stream().filter(block -> EarthAbility.isEarth(block.getType())).min(getBestSource(player, bukkitTarget)).orElse(null);
            case "Water" ->
                    source = nearbyBlocks.stream().filter(block -> WaterAbility.isWater(block.getType())).min(getBestSource(player, bukkitTarget)).orElse(null);
        }

        return source;
    }


}
