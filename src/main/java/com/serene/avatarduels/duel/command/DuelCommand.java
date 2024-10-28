package com.serene.avatarduels.duel.command;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.duel.Duel;
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
                    case "start" -> {
                        if (strings.length == 4){
                            String player1String = strings[1];
                            String player2String = strings[2];
                            Player player1 = Bukkit.getPlayer(player1String);
                            Player player2 = Bukkit.getPlayer(player2String);
                            String world = strings[3];

                            new Duel(player1, player2, world);
                        }
                    }
                    case "ai" -> {
                        if (strings.length == 4){
                            String player1String = strings[1];
                            String player2String = strings[2];
                            Player player1 = Bukkit.getPlayer(player1String);
                            Player player2 = Bukkit.getPlayer(player2String);
                            String world = strings[3];

                            NPCHandler.getNpcs().stream().filter(bendingNPC -> bendingNPC.getUUID().equals(player1.getUniqueId())).findFirst().
                                    ifPresentOrElse(bendingNPC -> bendingNPC.startDuel(player2), () -> Bukkit.broadcastMessage("Player 1 is not a valid AI"));

                            NPCHandler.getNpcs().stream().filter(bendingNPC -> bendingNPC.getUUID().equals(player2.getUniqueId())).findFirst().
                                    ifPresentOrElse(bendingNPC -> bendingNPC.startDuel(player1), () -> Bukkit.broadcastMessage("Player 2 is not a valid AI"));

                            new Duel(player1, player2, world);
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