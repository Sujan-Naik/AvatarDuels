package com.serene.avatarduels.npc.entity;

import com.mojang.authlib.GameProfile;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.air.GaleGust;
import com.serene.avatarduels.npc.entity.AI.goal.complex.combat.KillTargetEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BendingNPC extends SereneHumanEntity{
    public BendingNPC(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions) {
        super(server, world, profile, clientOptions);
//        bend();
    }

    public void bend(){
        Player player = Bukkit.getPlayer(this.getUUID());
//        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinEvent(player, "poopy butt"));
//        masterGoalSelector.addMasterGoal(new KillTargetEntity("kill", this, targetSelector.retrieveTopPlayer()));


        BendingPlayer.getOrLoadOfflineAsync(player).thenRun(() -> {

        BendingPlayer.getOfflinePlayers().forEach((uuid1, offlineBendingPlayer) -> Bukkit.broadcastMessage(offlineBendingPlayer.getName()));

            BendingPlayer bPlayer =  BendingPlayer.getBendingPlayer(player);

            Arrays.stream(Element.getAllElements()).forEach(element -> {
                bPlayer.addElement(element);

            });


//            for (int i = 0; i < 10000; i+=20){
////                new FireBlast(player);
//                try {
//                    getRandom(CoreAbility.getAbilities().stream().filter(coreAbility -> coreAbility.).collect(Collectors.toSet())).getClass().getDeclaredConstructor(Player.class).newInstance(player);
//                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                         NoSuchMethodException e) {
//                }
//            }


        });
    }

    public static <E> E getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst().get();
    }
}
