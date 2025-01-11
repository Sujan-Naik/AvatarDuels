package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.sourced;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.*;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourcedAbility extends RangedAbility {

    private double maxSourceRange;


    public SourcedAbility(String name, BendingNPC npc, String abilityName, double maxRange, double maxSourceRange) {
        super(name, npc, abilityName, maxRange);


        this.maxSourceRange = maxSourceRange;
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

        Set<Block> nearbyBlocks = GeneralMethods.getBlocksAroundPoint(player.getLocation(), maxSourceRange).stream().filter(block -> block.getRelative(BlockFace.UP).isPassable()).collect(Collectors.toSet());

        Block source = null;


        switch (CoreAbility.getAbility(getAbilityName()).getElement().getName()) {
            // Main Elements
//            case "Air" -> source = fetchSource(nearbyBlocks.stream(), AirAbility::isAir, bukkitTarget);
            case "Water" -> source = fetchSource(nearbyBlocks.stream(), block ->  ElementalAbility.isWater(block.getType())
                    || ElementalAbility.isIce(block) || ElementalAbility.isPlant(block), bukkitTarget);
            case "Earth" -> source = fetchSource(nearbyBlocks.stream(), block -> ElementalAbility.isEarth(block)
                    || ElementalAbility.isSand(block) || ElementalAbility.isLava(block) || ElementalAbility.isMetal(block), bukkitTarget);
            case "Fire" -> source = fetchSource(nearbyBlocks.stream(), FireAbility::isFire, bukkitTarget);

            // Subelements for Air
            case "Flight" -> {
//                source = fetchSource(nearbyBlocks.stream(), FlightAbility::isFlight, bukkitTarget);
                if (source == null) {
//                    source = fetchSource(nearbyBlocks.stream(), AirAbility::isAir, bukkitTarget);
                }
            }
            case "Spiritual" -> {
//                source = fetchSource(nearbyBlocks.stream(), SpiritualAbility::isSpiritual, bukkitTarget);
                if (source == null) {
//                    source = fetchSource(nearbyBlocks.stream(), AirAbility::isAir, bukkitTarget);
                }
            }

            // Subelements for Water
            case "Blood" -> {
//                source = fetchSource(nearbyBlocks.stream(), BloodAbility::isBlood, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), WaterAbility::isWater, bukkitTarget);
                }
            }
            case "Healing" -> {
//                source = fetchSource(nearbyBlocks.stream(), HealingAbility::isHealing, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), WaterAbility::isWater, bukkitTarget);
                }
            }
            case "Ice" -> {
                source = fetchSource(nearbyBlocks.stream(), IceAbility::isIce, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), WaterAbility::isWater, bukkitTarget);
                }
            }
            case "Plant" -> {
                source = fetchSource(nearbyBlocks.stream(), PlantAbility::isPlant, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), WaterAbility::isWater, bukkitTarget);
                }
            }

            // Subelements for Earth
            case "Lava" -> {
                source = fetchSource(nearbyBlocks.stream(), LavaAbility::isLava, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), EarthAbility::isEarth, bukkitTarget);
                }
            }
            case "Metal" -> {
                source = fetchSource(nearbyBlocks.stream(), MetalAbility::isMetal, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), EarthAbility::isEarth, bukkitTarget);
                }
            }
            case "Sand" -> {
                source = fetchSource(nearbyBlocks.stream(), SandAbility::isSand, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), EarthAbility::isEarth, bukkitTarget);
                }
            }

            // Subelements for Fire
            case "Lightning" -> {
//                source = fetchSource(nearbyBlocks.stream(), LightningAbility::isLightning, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), FireAbility::isFire, bukkitTarget);
                }
            }
            case "Combustion" -> {
//                source = fetchSource(nearbyBlocks.stream(), CombustionAbility::isCombustion, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), FireAbility::isFire, bukkitTarget);
                }
            }
            case "BlueFire" -> {
//                source = fetchSource(nearbyBlocks.stream(), BlueFireAbility::isBlueFire, bukkitTarget);
                if (source == null) {
                    source = fetchSource(nearbyBlocks.stream(), FireAbility::isFire, bukkitTarget);
                }
            }

            default -> source = null; // Handle unexpected cases
        }

        return source;
    }

    private Block fetchSource(Stream<Block> nearbyBlocks, Predicate<Block> predicate, org.bukkit.entity.LivingEntity bukkitTarget) {
        return nearbyBlocks
                .filter(predicate)
                .min(getBestSource(player, bukkitTarget))
                .orElse(null);
    }

}
