package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ScooterAbility extends MovementAbility {

    public ScooterAbility(String name, BendingNPC npc, String abilityName, double distance) {
        super(name, npc, abilityName, distance);
    }


    @Override
    public void tick() {
        super.tick();
        if ( !CoreAbility.hasAbility(player, bPlayer.getBoundAbility().getClass())){
            remove();
        } else {
            npc.lookAt(EntityAnchorArgument.Anchor.EYES, target, EntityAnchorArgument.Anchor.FEET);
        }

        if (npc.getPosition(0).add(npc.getForward()).distanceToSqr(target.getPosition(0)) > npc.getPosition(0).distanceToSqr(target.getPosition(0))  ){
            remove();
        }

    }

    @Override
    protected void remove(){
        if (hasStarted){

            Bukkit.getServer().getPluginManager().callEvent(new PlayerToggleSneakEvent(player, true));
            player.setSneaking(true);
            npc.setBusyBending(false);
        }
        setFinished(true);
    }

}
