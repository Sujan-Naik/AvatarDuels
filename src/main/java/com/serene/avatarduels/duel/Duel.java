package com.serene.avatarduels.duel;

import com.serene.avatarduels.AvatarDuels;
import me.moros.gaia.api.arena.Arena;
import me.moros.math.Vector3d;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Duel {

    private Player player1, player2;

    private Arena arena;

    private BukkitTask runner;

    public Duel(Player player1, Player player2, String worldName){
        this.arena = AvatarDuels.instance.getDuelManager().goToArena(worldName, player1, 0);

        if (!arena.equals( AvatarDuels.instance.getDuelManager().goToArena(worldName, player2, 1))){
            return;
        }

        this.player1 = player1;
        this.player2 = player2;

        for (int i = 0; i < 60; i+=20)
        {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                player1.sendTitle("DUEL", "The duel will commence in " + (3 - finalI / 20) + "!", 0, 20, 0);
                player2.sendTitle("DUEL", "The duel will commence in " + (3 - finalI / 20) + "!", 0, 20, 0);
            }, i);
        }
        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
            player1.sendTitle("GO!!", "The duel is starting");
            player1.setGlowing(true);
            player2.sendTitle("GO!!", "The duel is starting");
            player2.setGlowing(true);

            this.runner = Bukkit.getScheduler().runTaskTimer(AvatarDuels.plugin, () -> {
                tick();
            }, 0L, 1L);
        }, 80L);


    }

    private void tick(){
        if (!regionContainsPlayer(player1)){
            finished(player2);
        }
        if (!regionContainsPlayer(player2)){
            finished(player1);
        }
    }

    private boolean regionContainsPlayer(Player player){

        Location location = player.getLocation();
        return player.isOnline() && arena.region().contains(new Vector3d() {
            @Override
            public double x() {
                return location.getX();
            }

            @Override
            public double y() {
                return location.getY();
            }

            @Override
            public double z() {
                return location.getZ();
            }
        }) && !player.isDead();
    }


    private void finished(Player winner){
        winner.sendTitle("YOU WON!", "Well done");
        Bukkit.broadcastMessage(winner.getDisplayName() + " won the duel!");
        player1.setGlowing(false);
        player2.setGlowing(false);
        this.runner.cancel();
    }
}
