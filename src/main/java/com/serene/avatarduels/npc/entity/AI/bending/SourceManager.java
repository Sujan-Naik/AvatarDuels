package com.serene.avatarduels.npc.entity.AI.bending;


import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.*;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceManager {

    private static final int DEFAULT_MAX_SOURCE_RANGE = 15;
    private double maxSourceRange;
    private HumanEntity NMSPlayer;

//    private Set<Block> nearbyBlocks = new HashSet<>();

    public SourceManager(HumanEntity NMSPlayer){
        this.NMSPlayer = NMSPlayer;
    }



    private Comparator<Block> getBestSource(Player player, org.bukkit.entity.LivingEntity target){
        return Comparator.comparingDouble(block ->
                (player.getLocation().distanceSquared(block.getLocation()) + (target.getLocation().distanceSquared(block.getLocation()))));
    }



    public void useAbility(CoreAbility coreAbility){
        useAbility(coreAbility, 0, false, DEFAULT_MAX_SOURCE_RANGE);
    }

    public void useAbility(CoreAbility coreAbility, double maxSourceRange){
        useAbility(coreAbility, 0, false, maxSourceRange);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, double maxSourceRange){
        useAbility(coreAbility, chargeTimeMS, false, maxSourceRange);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, boolean clickBefore, double maxSourceRange){
        this.maxSourceRange = maxSourceRange;

        Player player = Bukkit.getPlayer(NMSPlayer.getUUID());
        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();
        if (nmsTarget== null){
            return;
        }

        if (player == null){
            Bukkit.broadcastMessage("source abilities r fucked");
        }
       Block source = getSource(player, nmsTarget, coreAbility);
        if (source == null){
            Bukkit.broadcastMessage("is null");
        }

        if (source != null){
            BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());

            NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, locToVec3(source.getLocation()) );
//            coreAbility.addAttributeModifier("Duration", 0, AttributeModifier.MULTIPLICATION);

            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {

                if (clickBefore) {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                    player.swingMainHand();
                }

                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
                player.setSneaking(true);

                if (chargeTimeMS == 0){
                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {

                        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                        player.setSneaking(false);

                    }, 1L);

                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                        NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.FEET);
//
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                        player.swingMainHand();
                    }, 2L);
                } else {

                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {


                        NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.FEET);
//                player.swingMainHand();
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                        player.swingMainHand();
//                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                    }, Math.ceilDiv(chargeTimeMS, 50) + 4L);

                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                        player.setSneaking(false);
                    }, Math.ceilDiv(chargeTimeMS, 50) + 8L);
                }
            }, 2L);


        }
    }

    public Block getSource(Player player, LivingEntity nmsTarget, CoreAbility coreAbility){
        org.bukkit.entity.LivingEntity bukkitTarget = (org.bukkit.entity.LivingEntity) Bukkit.getEntity(nmsTarget.getUUID());
        Set<Block> nearbyBlocks = GeneralMethods.getBlocksAroundPoint(player.getLocation(), maxSourceRange).stream().filter(block -> block.getRelative(BlockFace.UP).isPassable()).collect(Collectors.toSet());

        Block source = null;
        switch (coreAbility.getElement().getName()) {
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

            default -> {
                Bukkit.broadcastMessage("we didnt match any elements");
                source = null;
            }// Handle unexpected cases
        }

        return source;
    }

    private Block fetchSource(Stream<Block> nearbyBlocks, Predicate<Block> predicate, org.bukkit.entity.LivingEntity bukkitTarget) {
        return nearbyBlocks
                .filter(predicate)
                .min(getBestSource(Bukkit.getPlayer(NMSPlayer.getUUID()), bukkitTarget))
                .orElse(null);
    }



    
    private Vec3 locToVec3(Location loc){
        return new Vec3(loc.toVector().toVector3f());
    }
}
