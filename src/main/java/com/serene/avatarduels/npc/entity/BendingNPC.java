package com.serene.avatarduels.npc.entity;

import com.mojang.authlib.GameProfile;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.ability.air.GaleGust;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;
import com.serene.avatarduels.npc.entity.AI.bending.BlastManager;
import com.serene.avatarduels.npc.entity.AI.bending.SourceManager;
import com.serene.avatarduels.npc.entity.AI.goal.complex.bending.BendingKillEntity;
import com.serene.avatarduels.npc.entity.AI.goal.complex.combat.KillTargetEntity;
import com.serene.avatarduels.npc.utils.Vec3Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BendingNPC extends SereneHumanEntity{

    private SourceManager sourceManager;

    public SourceManager getSourceManager() {
        return sourceManager;
    }

    private BlastManager blastManager;

    public BlastManager getBlastManager() {
        return blastManager;
    }

    private boolean isBusyBending;

    public boolean isBusyBending() {
        return isBusyBending;
    }

    public void setBusyBending(boolean busyBending) {
        isBusyBending = busyBending;
    }

    public BendingNPC(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions) {
        super(server, world, profile, clientOptions);
        this.sourceManager = new SourceManager(this);
        this.blastManager = new BlastManager(this);


//        bend();
    }

    public void bend(){
        Player player = Bukkit.getPlayer(this.getUUID());

        BendingPlayer.getOrLoadOfflineAsync(player).thenRun(() -> {
            BendingPlayer bPlayer =  BendingPlayer.getBendingPlayer(player);
            Arrays.stream(Element.getAllElements()).forEach(element -> {
                bPlayer.addElement(element);
            });

            Arrays.stream(Element.getAllSubElements()).forEach(subElement -> {
                bPlayer.addSubElement(subElement);
            });

        });
    }

    @Override
    public void tick() {
        serverPlayerTick();
        doTick();

        masterGoalSelector.tick();
        targetSelector.tick();

        inventoryTracker.tick();
        if (!masterGoalSelector.doingGoal("kill hostile entity")) {
            if (targetSelector.retrieveTopHostile() instanceof LivingEntity hostile && (!Vec3Utils.isObstructed(this.getPosition(0), hostile.getPosition(0), this.level()))) {
//                masterGoalSelector.addMasterGoal(new KillTargetEntity("kill hostile entity", this, hostile));

                masterGoalSelector.addMasterGoal(new BendingKillEntity("kill hostile entity", this, hostile));
            }
        }
    }

    public void useAbility(AbilityUsages abilityUsage){
        abilityUsage.doFunction(this);
    }


}
