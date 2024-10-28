package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class MobilityManager {

    private HumanEntity NMSPlayer;

    public MobilityManager(HumanEntity NMSPlayer) {
        this.NMSPlayer = NMSPlayer;
    }

    public void useAbility(CoreAbility coreAbility) {
        useAbility(coreAbility, false, 0, false, false, false);
    }

    public void useAbility(CoreAbility coreAbility, boolean shiftBefore, long shiftDuration, boolean shouldClick) {
        useAbility(coreAbility, shiftBefore, shiftDuration, shouldClick, false, false);
    }

    public void useAbility(CoreAbility coreAbility, boolean shiftBefore, long shiftDuration, boolean shouldClick, boolean shouldReleaseShiftBeforeClick) {
        useAbility(coreAbility, shiftBefore, shiftDuration, shouldClick, shouldReleaseShiftBeforeClick, false);
    }

    public void useAbility(CoreAbility coreAbility, boolean shiftBefore, long shiftDuration, boolean shouldClick, boolean shouldReleaseShiftBeforeClick, boolean runJump) {
        useAbility(coreAbility, shiftBefore, shiftDuration, shouldClick, shouldReleaseShiftBeforeClick, runJump, false);
    }

    public void useAbility(CoreAbility coreAbility, boolean shiftBefore, long shiftDuration, boolean shouldClick, boolean shouldReleaseShiftBeforeClick, boolean runJump, boolean shouldLookAtGroundWhileShifting) {
        Player player = Bukkit.getServer().getPlayer(NMSPlayer.getUUID());
        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();

        if (nmsTarget == null) {
            return;
        }

        // Bind core ability
        BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());

        // Look at the target
        if (!shouldLookAtGroundWhileShifting) {
            NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.FEET);
        }

        // Handle shift before movement
        if (shiftBefore) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
            player.setSneaking(true);

            // Optionally look at the ground while shifting
            if (shouldLookAtGroundWhileShifting) {
                NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, NMSPlayer.getOnPos().getCenter());
            }

            // If click should happen before releasing shift, schedule that
            if (shouldReleaseShiftBeforeClick) {
                Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                    if (shouldClick) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                        player.swingMainHand();
                    }
                    // Release shift after click
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                    player.setSneaking(false);
                }, shiftDuration);
            } else {
                // Just release shift after duration
                Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                    if (shouldClick) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                        player.swingMainHand();
                    }
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                    player.setSneaking(false);
                }, shiftDuration);
            }
        } else {
            // If shiftBefore is false, we can perform actions immediately
            if (shouldClick) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                player.swingMainHand();
            }
        }

        // Handle running and jumping logic if specified
        if (runJump) {
            // Add jumping logic here if needed; for example:
            player.setVelocity(player.getVelocity().add(new Vector(0, 1, 0))); // Jump
            player.setSprinting(true);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
            player.swingMainHand();
        }
    }
}