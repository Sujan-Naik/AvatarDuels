package com.serene.avatarduels.npc.entity;

import com.mojang.authlib.GameProfile;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.serene.avatarduels.npc.entity.AI.bending.*;
import com.serene.avatarduels.npc.entity.AI.goal.complex.bending.BendingKillEntity;
import com.serene.avatarduels.npc.utils.Vec3Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BendingNPC extends HumanEntity {

    private MobilityManager mobilityManager;

    public MobilityManager getMobilityManager() {
        return mobilityManager;
    }

    private BreathManager breathManager;

    public BreathManager getBreathManager() {
        return breathManager;
    }

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
        this.breathManager = new BreathManager(this);
        this.mobilityManager = new MobilityManager(this);
    }



    public void enableBending(){
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

    public void startDuel(LivingEntity target){
        masterGoalSelector.addMasterGoal(new BendingKillEntity("kill entity", this, target));
    }

    @Override
    public void tick() {
        super.tick();

//        if (!masterGoalSelector.doingGoal("kill hostile entity")) {
//            if (targetSelector.retrieveTopHostile() instanceof LivingEntity hostile && (!Vec3Utils.isObstructed(this.getPosition(0), hostile.getPosition(0), this.level()))) {
////                masterGoalSelector.addMasterGoal(new KillTargetEntity("kill hostile entity", this, hostile));
//
//                masterGoalSelector.addMasterGoal(new BendingKillEntity("kll hostile entity", this, hostile));
//            }
//        }
    }

    public void useAbility(AbilityUsages abilityUsage){
        abilityUsage.doFunction(this);
    }


}
