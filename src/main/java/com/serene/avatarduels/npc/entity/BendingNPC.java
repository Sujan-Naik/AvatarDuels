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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
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

    private boolean isBusyMovementBending;

    public boolean isBusyMovementBending() {
        return isBusyMovementBending;
    }

    public void setBusyMovementBending(boolean busyBending) {
        isBusyMovementBending = busyBending;
    }

    public boolean hasCompleteLineOfSight(Entity entity) {
        if (entity.level() != this.level()) {
            return false;
        } else {
            return hasClearRay(new Vec3(this.getX(), this.getEyeY(), this.getZ()),  new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()))
            && hasClearRay(new Vec3(this.getX(), this.getY(), this.getZ()),  new Vec3(entity.getX(), entity.getY(), entity.getZ()));

        }
    }

    public boolean hasClearRayForward(){
        return hasClearRay(this.getPosition(0).add(this.getForward().scale(1)));
    }

    public boolean hasClearRay(Vec3 vec3d, Vec3 vec3d1){
//        return vec3d1.distanceToSqr(vec3d) > 128.0D * 128.0D ? false : this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
        return this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
    }

    public boolean hasClearRay(Vec3 vec3d){
        Vec3 vec3d1 = new Vec3(this.getX(), this.getY(), this.getZ());
//        return vec3d1.distanceToSqr(vec3d) > 128.0D * 128.0D ? false : this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
        return this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
    }

    public Vec3 getBlockingPos(Vec3 vec3d){
        Vec3 vec3d1 = new Vec3(this.getX(), this.getY(), this.getZ());
//        return vec3d1.distanceToSqr(vec3d) > 128.0D * 128.0D ? null : this.level().clip( new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).getLocation()  ; // Paper - Perf: Use distance squared & strip raytracing
        return this.level().clip( new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).getLocation()  ; // Paper - Perf: Use distance squared & strip raytracing
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
            bPlayer.togglePassive(Element.CHI);
            player.setGlowing(true);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
            player.setHealth(100);

        });

    }

    public void startDuel(LivingEntity target){
        masterGoalSelector.addMasterGoal(new BendingKillEntity("kill entity", this, target));
    }

    public void startDuel(org.bukkit.entity.LivingEntity target){
        masterGoalSelector.addMasterGoal(new BendingKillEntity("kill entity", this, ((CraftLivingEntity)target).getHandle()));
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
