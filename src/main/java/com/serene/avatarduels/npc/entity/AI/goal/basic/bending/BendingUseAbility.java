package com.serene.avatarduels.npc.entity.AI.goal.basic.bending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.control.MoveControl;
import com.serene.avatarduels.npc.entity.AI.goal.BaseGoal;
import com.serene.avatarduels.npc.entity.AI.goal.basic.BasicGoal;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility.MovementAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.sourced.SourcedAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BendingUseAbility extends BasicGoal {

    private String abilityName;

    protected LivingEntity target;

    protected Player player;

    protected BendingPlayer bPlayer;

    protected boolean hasStarted = false;

    public BendingUseAbility(String name, BendingNPC npc, String abilityName) {
        super(name, npc, 1);
        this.abilityName = abilityName;

        this.target = npc.getTargetSelector().getCurrentTarget();
        this.player = Bukkit.getPlayer(npc.getUUID());
        this.bPlayer = BendingPlayer.getBendingPlayer(player);

        if (npc.isBusyBending() || bPlayer.isOnCooldown(abilityName) || target==null || !target.isAlive()  ) {
            this.setFinished(true);
        }
    }

    protected void start(){
        if (shouldStart()){
            npc.useAbility(AbilityUsages.fromName(abilityName));
            if (this instanceof  MovementAbility){
                npc.setBusyMovementBending(true);
                npc.setBusyBending(true);
                npc.getMoveControl().setOperation(MoveControl.Operation.WAIT);
            } else {

                if (bPlayer.getBoundAbility().isSneakAbility()) {
                    npc.setBusyBending(true);
                } else {
                    npc.setBusyBending(true);
                    Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
                        npc.setBusyBending(false);
                    }, 10L);
                }
            }
        } else {
            setFinished(true);
        }
    }

    public abstract boolean shouldStart();

    public String getAbilityName() {
        return abilityName;
    }

    @Override
    public void tick() {
        if (!hasStarted){
            start();
            hasStarted = true;
        }

        if (bPlayer.getBoundAbility() != null && !(this instanceof MovementAbility)) {
            // Ability is finished using
            if ( !CoreAbility.hasAbility(player, bPlayer.getBoundAbility().getClass()) ) {
                remove();
            } else {
                // Ability is finished but still needs to be disabled, the players part is done
                if (!(this instanceof SourcedAbility) && bPlayer.getBoundAbility().isSneakAbility() && !player.isSneaking()){
                    remove();
                }
            }
        }
    }

    protected void remove() {
        if (hasStarted) {
            npc.setBusyBending(false);
            if (this instanceof MovementAbility) {
                npc.setBusyMovementBending(false);
                npc.getMoveControl().setOperation(MoveControl.Operation.WAIT);
            }

            setFinished(true);
        }
    }


}
