package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.serene.avatarduels.npc.entity.BendingNPC;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ScooterAbility extends MovementAbility {

    public ScooterAbility(String name, BendingNPC npc, String abilityName, double distance) {
        super(name, npc, abilityName, distance);
    }


    @Override
    public void tick() {
        super.tick();
        if (bPlayer.getBoundAbility() == null){
            remove();
        }
    }

    @Override
    protected void remove(){
        if (hasStarted){
            npc.setBusyBending(false);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
            player.setSneaking(true);
        }
        setFinished(true);
    }

}
