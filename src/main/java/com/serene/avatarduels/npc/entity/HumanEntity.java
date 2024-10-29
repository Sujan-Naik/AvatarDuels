package com.serene.avatarduels.npc.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.mojang.authlib.GameProfile;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.entity.AI.control.BodyRotationControl;
import com.serene.avatarduels.npc.entity.AI.control.JumpControl;
import com.serene.avatarduels.npc.entity.AI.control.LookControl;
import com.serene.avatarduels.npc.entity.AI.control.MoveControl;
import com.serene.avatarduels.npc.entity.AI.goal.MasterGoalSelector;
import com.serene.avatarduels.npc.entity.AI.inventory.InventoryTracker;
import com.serene.avatarduels.npc.entity.AI.pathfinding.Navigation;
import com.serene.avatarduels.npc.entity.AI.target.TargetSelector;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;


import org.bukkit.event.entity.*;

import javax.annotation.Nullable;
import java.util.*;

/***
 * Represents a player
 * Lot of useless code here kept for posterity (this is heavily inspired by Minecraft code itself)
 */
public class HumanEntity extends ServerPlayer {

    private final Inventory inventory = new Inventory(this);
    private final ItemCooldowns cooldowns;
    private final PlayerAdvancements advancements;
    public LookControl lookControl;
    public int attackTime = -1;
    public int attackIntervalMin = 20;
    public int timeSinceBowDraw = -1;
    protected MasterGoalSelector masterGoalSelector;
    protected TargetSelector targetSelector;
    protected InventoryTracker inventoryTracker;
    private LivingEntity owner;
//    private GroundPathNavigation navigation;

    private Navigation navigation;
    private MoveControl moveControl;
    private JumpControl jumpControl;
    private BodyRotationControl bodyRotationControl;
    private int noJumpDelay;
    private BlockState feetBlockState;
    private int remainingFireTicks;
    private BlockPos blockPosition;
    private BlockPos lastPos;
    private LivingEntity lastHurtMob;
    private ItemStack lastItemInMainHand;
    private int containerUpdateDelay;
    @Nullable
    private Vec3 levitationStartPos;
    private int levitationStartTime;
    private boolean on = true;



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

    public boolean hasClearRayRelative(Vec3 relative){
        Vec3 vec3d = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 vec3d1 = vec3d.add(relative);
//        return vec3d1.distanceToSqr(vec3d) > 128.0D * 128.0D ? false : this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
        return this.level().clipDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(this)) == HitResult.Type.MISS; // Paper - Perf: Use distance squared & strip raytracing
    }

    public Vec3 getBlockingPos(Vec3 vec3d){
        Vec3 vec3d1 = new Vec3(this.getX(), this.getY(), this.getZ());
//        return vec3d1.distanceToSqr(vec3d) > 128.0D * 128.0D ? null : this.level().clip( new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).getLocation()  ; // Paper - Perf: Use distance squared & strip raytracing
        return this.level().clip( new ClipContext(vec3d, vec3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).getLocation()  ; // Paper - Perf: Use distance squared & strip raytracing
    }


    public HumanEntity(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions) {
        super(server, world, profile, clientOptions);


        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.lookControl = new LookControl(this);
        this.bodyRotationControl = new BodyRotationControl(this);

        this.navigation = new Navigation(this);

        this.masterGoalSelector = new MasterGoalSelector();
        this.targetSelector = new TargetSelector(this);
        this.inventoryTracker = new InventoryTracker(inventory, this);

        this.feetBlockState = null;
        this.remainingFireTicks = -this.getFireImmuneTicks();
        this.blockPosition = BlockPos.ZERO;

        this.lastItemInMainHand = ItemStack.EMPTY;

        this.cooldowns = this.createItemCooldowns();

        this.advancements = server.getPlayerList().getPlayerAdvancements(this);

    }



    public int getMaxHeadXRot() {
        return 40;
    }

    public int getMaxHeadYRot() {
        return 75;
    }

    public int getHeadRotSpeed() {
        return 10;
    }

    protected float tickHeadTurn(float bodyRotation, float headRotation) {
        this.bodyRotationControl.clientTick();
        return headRotation;
    }

    private void updatingUsingItem() {
        if (this.isUsingItem()) {
            if (ItemStack.isSameItem(this.getItemInHand(this.getUsedItemHand()), this.useItem)) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                this.updateUsingItem(this.useItem);
            } else {
                this.stopUsingItem();
            }
        }

    }


    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
//        spawnIn(this.level());
        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {
            Bukkit.getPlayer(this.uuid).spigot().respawn();
        }, 20L);
    }

    private void setPosToBed(BlockPos pos) {
        this.setPos((double) pos.getX() + 0.5, (double) pos.getY() + 0.6875, (double) pos.getZ() + 0.5);
    }


    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25;
        this.zCloak += d2 * 0.25;
        this.yCloak += d1 * 0.25;
    }


    @Override
    public void doTick() {
        if (this.valid && !this.isSpectator() || !this.touchingUnloadedChunk()) {
            // super.tick();
            this.noPhysics = this.isSpectator();
            if (this.isSpectator()) {
                this.setOnGround(false);
            }

            if (this.takeXpDelay > 0) {
                --this.takeXpDelay;
            }

            if (this.isSleeping()) {
                ++this.sleepCounter;
                if (this.sleepCounter == 100 && (new PlayerDeepSleepEvent((org.bukkit.entity.Player) this.getBukkitEntity())).isCancelled()) {
                    this.sleepCounter = Integer.MIN_VALUE;
                }

                if (this.sleepCounter > 100) {
                    this.sleepCounter = 100;
                }

                if (!this.level().isClientSide && this.level().isDay()) {
                    this.stopSleepInBed(false, true);
                }
            }
//        } else if (this.sleepCounter > 0) {
//            ++this.sleepCounter;
//            if (this.sleepCounter >= 110) {
//                this.sleepCounter = 0;
//            }
//        }

            this.updateIsUnderwater();
            // super.tick();
            this.oAttackAnim = this.attackAnim;
            if (this.firstTick) {
                this.getSleepingPos().ifPresent(this::setPosToBed);
            }


            //super.baseTick();
            this.level().getProfiler().push("entityBaseTick");
            if (this.firstTick && this instanceof NeutralMob neutralMob) {
                neutralMob.tickInitialPersistentAnger(this.level());
            }

            this.feetBlockState = null;
            if (this.isPassenger() && this.getVehicle().isRemoved()) {
                this.stopRiding();
            }

            if (this.boardingCooldown > 0) {
                --this.boardingCooldown;
            }

            this.walkDistO = this.walkDist;
            this.xRotO = this.getXRot();


            if (this.canSpawnSprintParticle()) {
                this.spawnSprintParticle();
            }

            this.wasInPowderSnow = this.isInPowderSnow;
            this.isInPowderSnow = false;


            if (this.level().isClientSide) {
                this.clearFire();
            } else if (this.remainingFireTicks > 0) {
                if (this.fireImmune()) {
                    this.setRemainingFireTicks(this.remainingFireTicks - 4);
                    if (this.remainingFireTicks < 0) {
                        this.clearFire();
                    }
                } else {
                    if (this.remainingFireTicks % 20 == 0 && !this.isInLava()) {
                        this.hurt(this.damageSources().onFire(), 1.0F);
                    }

                    this.setRemainingFireTicks(this.remainingFireTicks - 1);
                }

                if (this.getTicksFrozen() > 0 && !this.freezeLocked) {
                    this.setTicksFrozen(0);
                    this.level().levelEvent((Player) null, 1009, this.blockPosition, 1);
                }
            }

            if (this.isInLava()) {
                this.lavaHurt();
                this.fallDistance *= 0.5F;
            } else {
                this.lastLavaContact = null;
            }

            this.checkBelowWorld();
            if (!this.level().isClientSide) {
                this.setSharedFlagOnFire(this.remainingFireTicks > 0);
            }

            this.firstTick = false;
            this.level().getProfiler().pop();

            this.level().getProfiler().push("livingEntityBaseTick");
            if (this.fireImmune() || this.level().isClientSide) {
                this.clearFire();
            }

            if (this.isAlive()) {
                boolean flag = this instanceof Player;
                if (!this.level().isClientSide) {
                    if (this.isInWall()) {
                        this.hurt(this.damageSources().inWall(), 1.0F);
                    } else if (flag && !this.level().getWorldBorder().isWithinBounds(this.getBoundingBox())) {
                        double d0 = this.level().getWorldBorder().getDistanceToBorder(this) + this.level().getWorldBorder().getDamageSafeZone();
                        if (d0 < 0.0) {
                            double d1 = this.level().getWorldBorder().getDamagePerBlock();
                            if (d1 > 0.0) {
                                this.hurt(this.damageSources().outOfBorder(), (float) Math.max(1, Mth.floor(-d0 * d1)));
                            }
                        }
                    }
                }

                if (this.isEyeInFluid(FluidTags.WATER) && !this.level().getBlockState(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
                    boolean flag1 = !this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(this) && (!flag || !((Player) this).getAbilities().invulnerable);
                    if (flag1) {
                        this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                        if (this.getAirSupply() == -20) {
                            this.setAirSupply(0);
                            Vec3 vec3d = this.getDeltaMovement();

                            for (int i = 0; i < 8; ++i) {
                                double d2 = this.random.nextDouble() - this.random.nextDouble();
                                double d3 = this.random.nextDouble() - this.random.nextDouble();
                                double d4 = this.random.nextDouble() - this.random.nextDouble();
                                this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + d2, this.getY() + d3, this.getZ() + d4, vec3d.x, vec3d.y, vec3d.z);
                            }

                            this.hurt(this.damageSources().drown(), 2.0F);
                        }
                    }

                    if (!this.level().isClientSide && this.isPassenger() && this.getVehicle() != null && this.getVehicle().dismountsUnderwater()) {
                        this.stopRiding();
                    }
                } else if (this.getAirSupply() < this.getMaxAirSupply()) {
                    this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
                }

                if (!this.level().isClientSide) {
                    BlockPos blockposition = this.blockPosition();
                    if (!Objects.equals(this.lastPos, blockposition)) {
                        this.lastPos = blockposition;
                        this.onChangedBlock(this.level().getMinecraftWorld(), blockposition);
                    }
                }
            }

            if (this.isAlive() && (this.isInPowderSnow)) {
                this.extinguishFire();
            }

            if (this.hurtTime > 0) {
                --this.hurtTime;
            }

            if (this.invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
                --this.invulnerableTime;
            }

            if (this.isDeadOrDying() && this.level().shouldTickDeath(this)) {
                this.tickDeath();
            }

            if (this.lastHurtByPlayerTime > 0) {
                --this.lastHurtByPlayerTime;
            } else {
                this.lastHurtByPlayer = null;
            }

            if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
                this.lastHurtMob = null;
            }

            if (this.lastHurtByMob != null) {
                if (!this.lastHurtByMob.isAlive()) {
                    this.setLastHurtByMob((LivingEntity) null);
                } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                    this.setLastHurtByMob((LivingEntity) null);
                }
            }

            this.tickEffects();
            this.animStepO = this.animStep;
            this.yBodyRotO = this.yBodyRot;
            this.yHeadRotO = this.yHeadRot;
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
            this.level().getProfiler().pop();


            this.updatingUsingItem();
            //this.updateSwimAmount();
            if (!this.level().isClientSide) {
                int i = this.getArrowCount();
                if (i > 0) {
                    if (this.removeArrowTime <= 0) {
                        this.removeArrowTime = 20 * (30 - i);
                    }

                    --this.removeArrowTime;
                    if (this.removeArrowTime <= 0) {
                        this.setArrowCount(i - 1);
                    }
                }

                int j = this.getStingerCount();
                if (j > 0) {
                    if (this.removeStingerTime <= 0) {
                        this.removeStingerTime = 20 * (30 - j);
                    }

                    --this.removeStingerTime;
                    if (this.removeStingerTime <= 0) {
                        this.setStingerCount(j - 1);
                    }
                }

                this.detectEquipmentUpdatesPublic();
                if (this.tickCount % 20 == 0) {
                    this.getCombatTracker().recheckStatus();
                }

            }

//        if (!this.isRemoved()) {
            if (this.jumpTriggerTime > 0) {
                --this.jumpTriggerTime;
            }

            if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                    this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN);
                }

                if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                    this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
                }
            }

            this.inventory.tick();
            this.oBob = this.bob;
            // super.aiStep();
            if (this.noJumpDelay > 0) {
                --this.noJumpDelay;
            }

            if (this.isControlledByLocalInstance()) {
                this.lerpSteps = 0;
//            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
            }

            if (this.lerpSteps > 0) {
                this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
                --this.lerpSteps;
            } else if (!this.isEffectiveAi()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
            }

            if (this.lerpHeadSteps > 0) {
                this.lerpHeadRotationStep(this.lerpHeadSteps, this.lerpYHeadRot);
                --this.lerpHeadSteps;
            }

            Vec3 vec3d = this.getDeltaMovement();
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;
            if (Math.abs(vec3d.x) < 0.003) {
                d0 = 0.0;
            }

            if (Math.abs(vec3d.y) < 0.003) {
                d1 = 0.0;
            }

            if (Math.abs(vec3d.z) < 0.003) {
                d2 = 0.0;
            }

            this.setDeltaMovement(d0, d1, d2);
            this.level().getProfiler().push("ai");
            if (this.isImmobile()) {
                this.jumping = false;
                this.xxa = 0.0F;
                this.zza = 0.0F;
            }
            //} else if (this.isEffectiveAi()) {
            this.level().getProfiler().push("newAi");
            this.level().getProfiler().push("navigation");
            this.navigation.tick();
            this.level().getProfiler().pop();

            this.level().getProfiler().push("controls");
            this.level().getProfiler().push("move");
            this.moveControl.tick();
            this.level().getProfiler().popPush("look");
            this.lookControl.tick();
            this.level().getProfiler().popPush("jump");
            this.jumpControl.tick();
            this.level().getProfiler().pop();
            this.level().getProfiler().pop();
            this.updateSwingTime();
            this.yHeadRot = this.getYRot();
            this.level().getProfiler().pop();
            //}

            this.level().getProfiler().pop();
            this.level().getProfiler().push("jump");
            if (this.jumping && this.isAffectedByFluids()) {
                double d3;
                if (this.isInLava()) {
                    d3 = this.getFluidHeight(FluidTags.LAVA);
                } else {
                    d3 = this.getFluidHeight(FluidTags.WATER);
                }

                boolean flag = this.isInWater() && d3 > 0.0;
                double d4 = this.getFluidJumpThreshold();
                if (!flag || this.onGround() && !(d3 > d4)) {
                    if (this.isInLava() && (!this.onGround() || d3 > d4)) {
                        this.jumpInLiquid(FluidTags.LAVA);
                    } else if ((this.onGround() || flag && d3 <= d4) && this.noJumpDelay == 0) {
                        if (!(new EntityJumpEvent(this.getBukkitLivingEntity())).isCancelled()) {
                            this.jumpFromGround();
                            this.noJumpDelay = 10;
                        } else {
                            this.setJumping(false);
                        }
                    }
                } else {
                    this.jumpInLiquid(FluidTags.WATER);
                }
            } else {
                this.noJumpDelay = 0;
            }

            this.level().getProfiler().pop();
            this.level().getProfiler().push("travel");
            this.xxa *= 0.98F;
            this.zza *= 0.98F;
            AABB axisalignedbb = this.getBoundingBox();
            Vec3 vec3d1 = new Vec3((double) this.xxa, (double) this.yya, (double) this.zza);
            if (this.hasEffect(MobEffects.SLOW_FALLING) || this.hasEffect(MobEffects.LEVITATION)) {
                this.resetFallDistance();
            }

            label132:
            {
                LivingEntity entityliving = this.getControllingPassenger();
                if (entityliving instanceof Player entityhuman) {
                    if (this.isAlive()) {
                        break label132;
                    }
                }

                // // Bukkit.broadcastMessage("travelling " + vec3d1.length() + " distance");
                this.travel(vec3d1);
            }

            this.level().getProfiler().pop();
            this.level().getProfiler().push("freezing");
            if (!this.level().isClientSide && !this.isDeadOrDying() && !this.freezeLocked) {
                int i = this.getTicksFrozen();
                if (this.isInPowderSnow && this.canFreeze()) {
                    this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), i + 1));
                } else {
                    this.setTicksFrozen(Math.max(0, i - 2));
                }
            }

            this.removeFrost();
            this.tryAddFrost();
            if (!this.level().isClientSide && this.tickCount % 40 == 0 && this.isFullyFrozen() && this.canFreeze()) {
                this.hurt(this.damageSources().freeze(), 1.0F);
            }

            this.level().getProfiler().pop();
            this.level().getProfiler().push("push");
            if (this.autoSpinAttackTicks > 0) {
                --this.autoSpinAttackTicks;
                this.checkAutoSpinAttack(axisalignedbb, this.getBoundingBox());
            }

            this.pushEntities();
            this.level().getProfiler().pop();
            if (((ServerLevel) this.level()).hasEntityMoveEvent && !(this instanceof Player) && (this.xo != this.getX() || this.yo != this.getY() || this.zo != this.getZ() || this.yRotO != this.getYRot() || this.xRotO != this.getXRot())) {
                Location from = new Location(this.level().getWorld(), this.xo, this.yo, this.zo, this.yRotO, this.xRotO);
                Location to = new Location(this.level().getWorld(), this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                EntityMoveEvent event = new EntityMoveEvent(this.getBukkitLivingEntity(), from, to.clone());
                if (event.isCancelled()) {
                    this.absMoveTo(from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
                } else if (!to.equals(event.getTo())) {
                    this.absMoveTo(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ(), event.getTo().getYaw(), event.getTo().getPitch());
                }
            }

            if (!this.level().isClientSide && this.isSensitiveToWater()) {
                this.hurt(this.damageSources().drown(), 1.0F);
            }

            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float f;
            if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
                f = Math.min(0.1F, (float) this.getDeltaMovement().horizontalDistance());
            } else {
                f = 0.0F;
            }

            this.bob += (f - this.bob) * 0.4F;
//        }

            double changeX = this.getX() - this.xo;
            double changeZ = this.getZ() - this.zo;
            float squareDistanceChange = (float) (changeX * changeX + changeZ * changeZ);
            float bodyRotation = this.yBodyRot;
            float headRotation = 0.0F;
            this.oRun = this.run;
            float f3 = 0.0F;
            if (squareDistanceChange > 0.0025000002F) {
                f3 = 1.0F;
                headRotation = (float) Math.sqrt((double) squareDistanceChange) * 3.0F;
                float f4 = (float) Mth.atan2(changeZ, changeX) * 57.295776F - 90.0F;
                float f5 = Mth.abs(Mth.wrapDegrees(this.getYRot()) - f4);
                if (95.0F < f5 && f5 < 265.0F) {
                    bodyRotation = f4 - 180.0F;
                } else {
                    bodyRotation = f4;
                }
            }

            if (this.attackAnim > 0.0F) {
                bodyRotation = this.getYRot();
            }

            if (!this.onGround()) {
                f3 = 0.0F;
            }

            this.run += (f3 - this.run) * 0.3F;
            this.level().getProfiler().push("headTurn");
            headRotation = this.tickHeadTurn(bodyRotation, headRotation);
            this.level().getProfiler().pop();
            this.level().getProfiler().push("rangeChecks");

            this.yRotO += (float) Math.round((this.getYRot() - this.yRotO) / 360.0F) * 360.0F;
            this.yBodyRotO += (float) Math.round((this.yBodyRot - this.yBodyRotO) / 360.0F) * 360.0F;
            this.xRotO += (float) Math.round((this.getXRot() - this.xRotO) / 360.0F) * 360.0F;
            this.yHeadRotO += (float) Math.round((this.yHeadRot - this.yHeadRotO) / 360.0F) * 360.0F;
            this.level().getProfiler().pop();
            this.animStep += headRotation;
            if (this.isFallFlying()) {
                ++this.fallFlyTicks;
            } else {
                this.fallFlyTicks = 0;
            }

            if (this.isSleeping()) {
                this.setXRot(0.0F);
            }
            if (!this.level().isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
                //  this.closeContainer(Reason.CANT_USE);
                this.closeContainer();
                this.containerMenu = this.inventoryMenu;
            }

            this.moveCloak();
            if (!this.level().isClientSide) {
                this.foodData.tick(this);
                this.awardStat(Stats.PLAY_TIME);
                this.awardStat(Stats.TOTAL_WORLD_TIME);
                if (this.isAlive()) {
                    this.awardStat(Stats.TIME_SINCE_DEATH);
                }

                if (this.isDiscrete()) {
                    this.awardStat(Stats.CROUCH_TIME);
                }

                if (!this.isSleeping()) {
                    this.awardStat(Stats.TIME_SINCE_REST);
                }
            }

            double newX = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
            double newZ = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
            if (newX != this.getX() || newZ != this.getZ()) {
                this.setPos(newX, this.getY(), newZ);
            }

            ++this.attackStrengthTicker;
            ItemStack itemstack = this.getMainHandItem();
            if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
                if (!ItemStack.isSameItem(this.lastItemInMainHand, itemstack)) {
                    this.resetAttackStrengthTicker();
                }

                this.lastItemInMainHand = itemstack.copy();
            }

            this.cooldowns.tick();
            this.updatePlayerPose();
        }
    }


    @Override
    public void tick() {

        if (on) {
            serverPlayerTick();
            doTick();
            masterGoalSelector.tick();
            targetSelector.tick();
            inventoryTracker.tick();
        }


    }


    public TargetSelector getTargetSelector() {
        return targetSelector;
    }

    public boolean canPerformAttack(LivingEntity target) {
        if (this.canAttack(target) && this.distanceToSqr(target) < 9 && this.lookControl.isLookingAtTarget()) {
            return true;
        }
        return false;
    }

    public void performAttack(LivingEntity target) {
        this.resetAttackStrengthTicker();
        this.swing(InteractionHand.MAIN_HAND);
        this.lookControl.setLookAt(target);
        this.attack(target);

    }



    public Navigation getNavigation() {
        return navigation;
    }

    public JumpControl getJumpControl() {
        return jumpControl;
    }

    public MoveControl getMoveControl() {
        return moveControl;
    }


    public void serverPlayerTick() {
        if (this.joining) {
            this.joining = false;
        }

        this.gameMode.tick();
        this.wardenSpawnTracker.tick();
        --this.spawnInvulnerableTime;
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }

        if (--this.containerUpdateDelay <= 0) {
            this.containerMenu.broadcastChanges();
            this.containerUpdateDelay = this.level().paperConfig().tickRates.containerUpdate;
        }

        if (!this.level().isClientSide && this.containerMenu != this.inventoryMenu && (this.isImmobile() || !this.containerMenu.stillValid(this))) {
            //this.closeContainer(Reason.CANT_USE);
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }

        Entity entity = this.getCamera();
        if (entity != this) {
            if (entity.isAlive()) {
                this.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                this.serverLevel().getChunkSource().move(this);
                if (this.wantsToStopRiding()) {
                    this.setCamera(this);
                }
            } else {
                this.setCamera(this);
            }
        }

        CriteriaTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }

        this.trackStartFallingPosition();
        this.trackEnteredOrExitedLavaOnVehicle();
        this.advancements.flushDirty(this);
    }




}