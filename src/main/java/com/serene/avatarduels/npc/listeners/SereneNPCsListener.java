package com.serene.avatarduels.npc.listeners;


import com.serene.avatarduels.npc.NPCHandler;

import com.serene.avatarduels.npc.utils.NPCUtils;
import com.serene.avatarduels.npc.utils.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

    @EventHandler
    public void suffocate(EntityDamageByBlockEvent event){
        if (event.getEntity() instanceof  Player player){
            if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
                // Bukkit.broadcastMessage("balls");
                ascendLevel(player);
            } else {
                // Bukkit.broadcastMessage(String.valueOf(event.getCause()));
            }
        }
    }
    public boolean ascendLevel(Player player) {
        final World world = player.getWorld();
        final Location pos = player.getLocation();
        final int x = pos.getBlockX();
        int y = Math.max(-128, pos.getBlockY() + 1);
        final int z = pos.getBlockZ();
        int yPlusSearchHeight = y + 20;
        int maxY = Math.min(200, yPlusSearchHeight) + 2;

        while (y <= maxY) {
            Location attemptedNewLoc = new Location(world,x, y, z);
            if (!attemptedNewLoc.getBlock().isPassable()
                    && attemptedNewLoc.getBlock().isPassable() && attemptedNewLoc.getBlock().getRelative(BlockFace.UP).isPassable()) {
                attemptedNewLoc.setDirection(player.getEyeLocation().getDirection());
                player.teleport(attemptedNewLoc);
                return true;
            }
            ++y;
        }

        return false;
    }


}
