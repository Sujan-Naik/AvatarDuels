package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;

public class MobilityManager {

    private final BendingNPC NMSPlayer;

    public MobilityManager(BendingNPC NMSPlayer) {
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


        // Handle running and jumping logic if specified
        if (runJump) {
            // Add jumping logic here if needed; for example:

            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSprintEvent(player, true));
            NMSPlayer.setSprinting(true);

            Vec3 currentFloorBlock = NMSPlayer.getPosition(0);
            Vec3 dir = NMSPlayer.getForward().scale(1);
            Vec3 wantedPosition = currentFloorBlock.add(dir);
            NMSPlayer.getMoveControl().setWantedPosition(wantedPosition.x, wantedPosition.y, wantedPosition.z, 10);

            NMSPlayer.jumpFromGround();
            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                player.swingMainHand();
            },3L);
        } else {


            Vec3 accessibleNavPos = NMSPlayer.getNavigation().getLowestYAdjustedGoalPos().add(0,2,0);

            // Look at the target
            if (!shouldLookAtGroundWhileShifting) {
                NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.FEET);
            }

            double distRoot = Math.sqrt(nmsTarget.distanceTo(NMSPlayer));

            // Handle shift before movement
            if (shiftBefore) {

                // Optionally look at the ground while shifting
                if (shouldLookAtGroundWhileShifting) {
                    NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, NMSPlayer.getOnPos().getCenter().subtract(0, 2, 0));


                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                        NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, NMSPlayer.getOnPos().getCenter().subtract(0, 2, 0));

                        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
                        player.setSneaking(true);

                        // If click should happen before releasing shift, schedule that
                        if (shouldReleaseShiftBeforeClick) {
                            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                                player.setSneaking(false);

                                if (shouldClick) {
                                    NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, accessibleNavPos);

                                    Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                                    player.swingMainHand();
                                }

                            }, shiftDuration / 50);
                        } else {
                            // Just release shift after duration
                            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {

                                if (shouldClick) {
                                    NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, accessibleNavPos);


                                    Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                                    player.swingMainHand();
                                }

                                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                                player.setSneaking(false);
                            }, shiftDuration / 50);
                        }
                    }, 3L);
                } else {
                    // If click should happen before releasing shift, schedule that
                    if (shouldReleaseShiftBeforeClick) {
                        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {

                            // Release shift after click
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                            player.setSneaking(false);


                            if (shouldClick) {
                                NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, accessibleNavPos);

                                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                                player.swingMainHand();
                            }

                        }, shiftDuration / 50);
                    } else {
                        // Just release shift after duration
                        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                            if (shouldClick) {
                                NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, accessibleNavPos);

                                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                                player.swingMainHand();
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
                            player.setSneaking(false);
                        }, shiftDuration / 50);
                    }
                }
            } else {
                // If shiftBefore is false, we can perform actions immediately
                if (shouldClick) {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                    player.swingMainHand();
                }
            }

        }
    }
}