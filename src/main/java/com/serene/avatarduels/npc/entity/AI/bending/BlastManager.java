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

public class BlastManager {

    private static final int maxBlastRange = 10;
    private SereneHumanEntity NMSPlayer;

//    private Set<Block> nearbyBlocks = new HashSet<>();

    public BlastManager(SereneHumanEntity NMSPlayer){
        this.NMSPlayer = NMSPlayer;
    }



    private Comparator<Block> getBestBlast(Player player, Player target){
        return Comparator.comparingDouble(block ->
                (player.getLocation().distanceSquared(block.getLocation()) + (target.getLocation().distanceSquared(block.getLocation()))));
    }

    public void useAbility(CoreAbility coreAbility){
        useAbility(coreAbility, 0, false, 1);
    }

    public void useAbility(CoreAbility coreAbility, int shots){
        useAbility(coreAbility, 0, false, shots);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS){
        useAbility(coreAbility, chargeTimeMS, true, 1);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, int shots){
        useAbility(coreAbility, chargeTimeMS, true, shots);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, boolean shiftBefore, int shots){
        useAbility(coreAbility, chargeTimeMS, shiftBefore, false, shots);
    }

    public void useAbility(CoreAbility coreAbility, long chargeTimeMS, boolean shiftBefore, boolean releaseShiftLast, int shots){
        Player player = Bukkit.getServer().getPlayer(NMSPlayer.getUUID());

        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();
        if (nmsTarget== null){
            return;
        }

        BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());

        NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.EYES );

        if (shiftBefore) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
            player.setSneaking(true);


            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                if (releaseShiftLast) {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                    player.setSneaking(false);


                    clickForShots(player, shots);
                } else {

                    clickForShots(player, shots);
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                    player.setSneaking(false);


                }
            }, Math.ceilDiv(chargeTimeMS,50) + 20);


        } else {
            clickForShots( player, shots);
        }
    }

    private void clickForShots(Player player, int shots){
        for (int i = 0; i < shots; i++){
            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
            player.swingMainHand();
        }
    }

}
