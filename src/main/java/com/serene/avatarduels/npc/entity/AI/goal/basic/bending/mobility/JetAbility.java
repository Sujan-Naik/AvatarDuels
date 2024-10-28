package com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility;

import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class JetAbility extends MovementAbility {

    public JetAbility(String name, BendingNPC npc, String abilityName, double distance) {
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
