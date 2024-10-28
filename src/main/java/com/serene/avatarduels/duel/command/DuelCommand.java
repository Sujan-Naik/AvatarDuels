package com.serene.avatarduels.duel.command;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.NPCUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {

            if (strings.length == 0) {

            } else {
                switch (strings[0]) {
                    case "reload" -> {
                        AvatarDuels.instance.getDuelManager().reload();
                    }
                    case "arenas" -> {
                        AvatarDuels.instance.getDuelManager().listArenas();
                    }
                    case "spawn" -> {
//                        Bukkit.getServicesManager().getKnownServices().forEach(aClass -> Bukkit.broadcastMessage(aClass.getName()));
                        if (strings.length == 2){
                            AvatarDuels.instance.getDuelManager().goToArena(strings[1], player, 0);
                        }
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