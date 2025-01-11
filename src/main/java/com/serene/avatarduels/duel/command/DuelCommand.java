package com.serene.avatarduels.duel.command;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.duel.Duel;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.NPCUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
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
//                        Bukkit.getServicesManager().getKnownServices().forEach(aClass -> // Bukkit.broadcastMessage(aClass.getName()));
                        if (strings.length == 2){
                            AvatarDuels.instance.getDuelManager().goToArena(strings[1], player, 0);
                        }
                    }
                    case "start" -> {
                        if (strings.length == 4){
                            startDuel(strings[1], strings[2], strings[3]);
                        } else if (strings.length == 5){

                            if (strings[1].equalsIgnoreCase("ai")){

                                String player1String = strings[2];
                                String player2String = strings[3];
                                Player player1 = Bukkit.getPlayer(player1String);
                                Player player2 = Bukkit.getPlayer(player2String);
                                String world = strings[4];


                                    NPCHandler.getNpcs().stream().filter(bendingNPC -> player1 != null && bendingNPC.getUUID().equals(player1.getUniqueId())).findFirst().
                                            ifPresentOrElse(bendingNPC -> bendingNPC.startDuel(player2), () ->
                                            {
                                                if (player1 == null) {
                                                    BendingNPC npc = NPCUtils.spawnNPC(player.getLocation(), player, player1String);
                                                    NPCHandler.addNPC(npc);

                                                    Bukkit.broadcastMessage("Player 1 is not a valid AI and there is no human player - spawning one in.");


                                                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                                                        Bukkit.broadcastMessage("TARGET ACQUIRED. ELIMINATE " + player2String);
                                                        npc.startDuel(Bukkit.getPlayer(player2String));
                                                    }, 100L);
                                                }
                                            });



                                    NPCHandler.getNpcs().stream().filter(bendingNPC -> player2 != null && bendingNPC.getUUID().equals(player2.getUniqueId())).findFirst().
                                            ifPresentOrElse(bendingNPC -> bendingNPC.startDuel(player1), () ->
                                            {
                                                if (player2 == null) {

                                                    BendingNPC npc = NPCUtils.spawnNPC(player.getLocation(), player, player2String);
                                                    NPCHandler.addNPC(npc);

                                                    Bukkit.broadcastMessage("Player 2 is not a valid AI and there is no human player - spawning one in");

                                                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                                                        Bukkit.broadcastMessage("TARGET ACQUIRED. ELIMINATE " + player1String);
                                                        npc.startDuel(Bukkit.getPlayer(player1String));

                                                    }, 100L);
                                                }
                                            });


                                Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {


                                    startDuel( player1String,  player2String,  world);
                                }, 120L);

                            }
                        }
                    }


                }
            }
        }
        return true;
    }

    private static boolean startDuel(String player1String, String player2String, String world){
        Player player1 = Bukkit.getPlayer(player1String);
        Player player2 = Bukkit.getPlayer(player2String);

        if (player1 != null && player2 != null){
            player1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
            player2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);

            player1.setHealth(100);
            player2.setHealth(100);


            new Duel(player1, player2, world);
            return true;
        }
        return false;
    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}