package com.serene.avatarduels.npc.command;


import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.earth.MudSurge;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.NPCUtils;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SerenityCommand implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {

            if (strings.length == 0) {
                NPCHandler.addNPC(NPCUtils.spawnNPC(player.getLocation(), player, "noob"));
            } else {
                switch (strings[0]) {
                    case "spawn" -> {
                        BendingNPC npc = NPCUtils.spawnNPC(player.getLocation(), player, strings[1]);
                        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                            Bukkit.getPlayer(npc.getUUID()).teleport(player);
                        }, 60L);
                        NPCHandler.addNPC(npc);
                    }
                    case "duel" -> {
                        BendingNPC npc = NPCUtils.spawnNPC(player.getLocation(), player, "Aang");
                        BendingNPC npc2 = NPCUtils.spawnNPC(player.getLocation(), player, "Korra");

                        NPCHandler.addNPC(npc);
                        NPCHandler.addNPC(npc2);

                        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                            Bukkit.getPlayer(npc.getUUID()).teleport(player.getLocation().add(-20,0,0));
                            Bukkit.getPlayer(npc2.getUUID()).teleport(player.getLocation().add(20,0,0));


                            for (int i = 0; i < 60; i+=20)
                            {
                                int finalI = i;
                                Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                                    player.sendTitle("AI DUEL", "The duel will commence in " + (3 - finalI / 20) + "!", 0, 20, 0);
                                }, i);
                            }
                            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                                player.sendTitle("GO!!", "The duel is starting");

                                npc.startDuel(npc2);
                                npc2.startDuel(npc);
                            }, 80L);
                        }, 60L);

                    }
                    case "bend" -> {
                        NPCHandler.getNpcs().forEach(bendingNPC -> {
//                            bendingNPC.getSourceManager().useAbility(CoreAbility.getAbility(strings[1]));
                            bendingNPC.getTargetSelector().setCurrentTarget(((CraftPlayer)player).getHandle());
                            if (strings.length == 1) {
                                AbilityUsages ability = getRandom(Arrays.stream(AbilityUsages.values()).collect(Collectors.toSet()));
                                bendingNPC.useAbility(ability);
                                Bukkit.broadcastMessage(ability.getName());
                            } else {
                                bendingNPC.useAbility(AbilityUsages.valueOf(strings[1]));
                            }
                        });
                    }
//                    case "respawn" -> {
//                        NPCHandler.getNpcs().forEach(bendingNPC -> {
//                            if (bendingNPC.isDeadOrDying()) {
//                                bendingNPC.respawn();
//                                bendingNPC.isRespawnForced()
//                            }
//                        });
//                    }
                    case "jump" -> {
                        NPCHandler.getNpcs().forEach(bendingNPC -> {

                            bendingNPC.getJumpControl().jump();
                        });
                    }
                    default -> {
                        NPCHandler.getNpcs().forEach(bendingNPC -> {
                            try {
                                CoreAbility.getAbility(strings[0]).getClass().getDeclaredConstructor(Player.class).newInstance(Bukkit.getPlayer(bendingNPC.getUUID()));
                            } catch (Exception e) {
                            }
                        });
                    }
                }
            }
        }
        return true;
    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}