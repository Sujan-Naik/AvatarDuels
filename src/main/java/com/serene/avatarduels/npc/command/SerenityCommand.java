package com.serene.avatarduels.npc.command;


import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.earth.MudSurge;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
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
                        NPCHandler.addNPC(NPCUtils.spawnNPC(player.getLocation(), player, strings[1]));
                    }
                    case "bend" -> {
                        NPCHandler.getNpcs().forEach(bendingNPC -> {
//                            bendingNPC.getSourceManager().useAbility(CoreAbility.getAbility(strings[1]));
                            if (strings.length == 1) {

                                bendingNPC.useAbility(getRandom(Arrays.stream(AbilityUsages.values()).collect(Collectors.toSet())));
                            } else {
                                bendingNPC.useAbility(AbilityUsages.valueOf(strings[1]));
                            }
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