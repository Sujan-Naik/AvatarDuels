package com.serene.avatarduels.npc.entity.AI.bending;


import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.earth.MudSurge;
import com.serene.avatarduels.npc.entity.SereneHumanEntity;
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
import java.util.stream.Collectors;

public class SourceManager {

    private static final int maxSourceRange = 10;
    private SereneHumanEntity NMSPlayer;

//    private Set<Block> nearbyBlocks = new HashSet<>();

    public SourceManager(SereneHumanEntity NMSPlayer){
        this.NMSPlayer = NMSPlayer;
    }



    private Comparator<Block> getBestSource(Player player, org.bukkit.entity.LivingEntity target){
        return Comparator.comparingDouble(block ->
                (player.getLocation().distanceSquared(block.getLocation()) + (target.getLocation().distanceSquared(block.getLocation()))));
    }

    public void useAbility(CoreAbility coreAbility){
        useAbility(coreAbility, 0, false);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS){
        useAbility(coreAbility, chargeTimeMS, false);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, boolean clickBefore){
        Player player = Bukkit.getServer().getPlayer(NMSPlayer.getUUID());
        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();
        if (nmsTarget== null){
            return;
        }
        org.bukkit.entity.LivingEntity target = (org.bukkit.entity.LivingEntity) Bukkit.getEntity(nmsTarget.getUUID());
        Set<Block> nearbyBlocks = GeneralMethods.getBlocksAroundPoint(player.getLocation(), maxSourceRange).stream().filter(block -> block.getRelative(BlockFace.NORTH).isPassable()).collect(Collectors.toSet());

        Block source = null;
        switch (coreAbility.getElement().getName()){
            case "Earth" ->
                source = nearbyBlocks.stream().filter(block -> EarthAbility.isEarth(block.getType())).min(getBestSource(player, target)).orElse(null);
            case "Water" ->
                source = nearbyBlocks.stream().filter(block -> WaterAbility.isWater(block.getType())).min(getBestSource(player, target)).orElse(null);
        }
        if (source != null){
            BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());


            NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, locToVec3(source.getLocation()) );
//            coreAbility.addAttributeModifier("Duration", 0, AttributeModifier.MULTIPLICATION);

            if (clickBefore) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                player.swingMainHand();
            }

            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
            player.setSneaking(true);
            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {


                NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.EYES);
//                player.swingMainHand();
                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR,  null, null, BlockFace.SELF));
                player.swingMainHand();
//                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
            }, Math.ceilDiv(chargeTimeMS,50) + 10L);
            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                player.setSneaking(false);
            }, Math.ceilDiv(chargeTimeMS,50) + 20L);

        }

    }
    
    private Vec3 locToVec3(Location loc){
        return new Vec3(loc.toVector().toVector3f());
    }
}
