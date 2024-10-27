package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import java.util.UUID;

public class BreathManager {

    private static final long DEFAULT_HOLD_TIME_MS = 3000; // 3 seconds hold time
    private HumanEntity NMSPlayer;
    private Player player;

    public BreathManager(HumanEntity NMSPlayer) {
        this.NMSPlayer = NMSPlayer;
        this.player = Bukkit.getServer().getPlayer(NMSPlayer.getUUID());
    }

    public void useAbility(CoreAbility coreAbility) {
        useAbility(coreAbility, DEFAULT_HOLD_TIME_MS);
    }

    public void useAbility(CoreAbility coreAbility, long holdTimeMS) {
        LivingEntity nmsTarget = NMSPlayer.getTargetSelector().getCurrentTarget();
        if (nmsTarget == null) {
            return;
        }

        BendingPlayer.getBendingPlayer(player).bindAbility(coreAbility.getName());

        // Make the player look at the target
        NMSPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, nmsTarget, EntityAnchorArgument.Anchor.EYES);

        // Start sneaking
        startSneaking(holdTimeMS);
    }

    private void startSneaking(long holdTimeMS) {
        // Trigger the sneaking event
        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
        player.setSneaking(true);

        // Schedule releasing the sneak state after the hold time
        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
            releaseSneak();
        }, Math.ceilDiv(holdTimeMS, 50));
    }

    private void releaseSneak() {
        // Release the sneak state
        Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));
        player.setSneaking(false);

        // Here you can trigger any other effects or actions after releasing the sneak
        // For example, you could invoke a method to apply the ability effects
    }
}
