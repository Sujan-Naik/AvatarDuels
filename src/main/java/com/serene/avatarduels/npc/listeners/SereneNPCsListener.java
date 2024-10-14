package com.serene.avatarduels.npc.listeners;


import com.serene.avatarduels.npc.NPCHandler;

import com.serene.avatarduels.npc.utils.NPCUtils;
import com.serene.avatarduels.npc.utils.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SereneNPCsListener implements Listener {


    private final static Set<Player> CHAT_COOLDOWNS = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            NPCHandler.getNpcs().forEach((serverPlayer) -> {
                NPCUtils.addNPC(player, serverPlayer);
            });
        });

    }

}
