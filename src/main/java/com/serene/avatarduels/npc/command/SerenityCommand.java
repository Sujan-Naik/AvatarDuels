package com.serene.avatarduels.npc.command;


import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.earth.MudSurge;
import com.serene.avatarduels.npc.NPCHandler;
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

public class SerenityCommand implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {

            if (strings.length == 0) {
                NPCHandler.addNPC(NPCUtils.spawnNPC(player.getLocation(), player, "noob"));
            } else {
                switch (strings[0]) {
                    case "spawn" -> {
                        NPCHandler.addNPC(NPCUtils.spawnNPC(player.getLocation(), player, strings[1]));
                    }
                    case "MudSurge" -> {
                        NPCHandler.getNpcs().forEach(bendingNPC -> {
                            net.minecraft.world.entity.player.Player nmsPlayer = ((CraftPlayer) player).getHandle();
//                            new EarthBlast(Bukkit.getPlayer(bendingNPC.getUUID()));
                            Player NPCPlayer = Bukkit.getPlayer(bendingNPC.getUUID());
                            BendingPlayer bPlayer =  BendingPlayer.getBendingPlayer(NPCPlayer);
                            bPlayer.bindAbility("MudSurge");


//                            Vec3 targetLoc = nmsPlayer.getRayTrace(20, ClipContext.Fluid.NONE).getLocation();
//                            Vec3 dir = targetLoc.subtract(bendingNPC.getEyePosition());
//                            bendingNPC.lookControl.setLookAt(nmsPlayer.getEyePosition().subtract(0,3,0));

                            bendingNPC.lookAt(EntityAnchorArgument.Anchor.EYES, nmsPlayer.getEyePosition().subtract(0,5,0));
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(NPCPlayer, true));

//                            new MudSurge(mcBendingPlayer);
//                            bendingNPC.setShiftKeyDown(true);
//                            mcBendingPlayer.setSneaking(true);

                            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
//                                bendingNPC.setShiftKeyDown(false);
//                                Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(NPCPlayer, false));

//                                mcBendingPlayer.setSneaking(false);

                                bendingNPC.lookAt(EntityAnchorArgument.Anchor.EYES, nmsPlayer, EntityAnchorArgument.Anchor.EYES);
//                                bendingNPC.swing(InteractionHand.MAIN_HAND);
                                CoreAbility.getAbilities(NPCPlayer, MudSurge.class);
                                Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractEvent(NPCPlayer, Action.LEFT_CLICK_AIR,  null, null, BlockFace.SELF));


                            }, 40L);
//                            bendingNPC.lookAt(EntityAnchorArgument.Anchor.EYES, nmsPlayer.getEyePosition());
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
}