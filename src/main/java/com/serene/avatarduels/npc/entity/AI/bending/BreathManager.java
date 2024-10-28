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
import java.util.UUID;

public class BreathManager {

    private static final long DEFAULT_HOLD_TIME_MS = 3000; // 3 seconds hold time
    private HumanEntity NMSPlayer;

    public BreathManager(HumanEntity NMSPlayer) {
        this.NMSPlayer = NMSPlayer;
    }

    public void useAbility(CoreAbility coreAbility) {
        useAbility(coreAbility, DEFAULT_HOLD_TIME_MS);
    }

    public void useAbility(CoreAbility coreAbility, long holdTimeMS) {
        if (holdTimeMS == 0){
            holdTimeMS = 2000;
        }
        useAbility(coreAbility, holdTimeMS, false);
    }

    public void useAbility(CoreAbility coreAbility, long holdTimeMS, boolean spamLeftClick) {
        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();
        if (nmsTarget == null) {
            return;
        }
        Player player = Bukkit.getServer().getPlayer(NMSPlayer.getUUID());

        BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());


        if (spamLeftClick) {
            int ticks = (int) Math.floorDiv(holdTimeMS, 50);

            for (int i = 0 ; i < ticks; i ++ ) {
                Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.SELF));
                    player.swingMainHand();
                }, i);
            }
        }

        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
        player.setSneaking(true);

        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
            player.setSneaking(false);

        }, Math.ceilDiv(holdTimeMS, 50));
    }



}
