package com.serene.avatarduels.npc.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.mojang.authlib.GameProfile;
import com.serene.avatarduels.npc.entity.AI.bending.SourceManager;
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
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;


import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.*;

import javax.annotation.Nullable;
import java.util.*;

/***
 * Represents a player
 * Lot of useless code here kept for posterity (this is heavily inspired by Minecraft code itself)
 */
public class SereneHumanEntity extends ServerPlayer {

    private final Inventory inventory = new Inventory(this);
    private final Set<TagKey<Fluid>> fluidOnEyes;
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



    public SereneHumanEntity(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions) {
        super(server, world, profile, clientOptions);


        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.lookControl = new LookControl(this);
        this.bodyRotationControl = new BodyRotationControl(this);

//        this.navigation = new GroundPathNavigation(this, world);
        this.navigation = new Navigation(this);

        this.masterGoalSelector = new MasterGoalSelector();
        this.targetSelector = new TargetSelector(this);
        this.inventoryTracker = new InventoryTracker(inventory, this);

        this.feetBlockState = null;
        this.remainingFireTicks = -this.getFireImmuneTicks();
        this.blockPosition = BlockPos.ZERO;
        this.fluidOnEyes = new HashSet();

        this.lastItemInMainHand = ItemStack.EMPTY;

        this.cooldowns = this.createItemCooldowns();

        this.advancements = server.getPlayerList().getPlayerAdvancements(this);



//        this.setItemSlot(EquipmentSlot.HEAD, net.minecraft.world.item.ItemStack.fromBukkitCopy(new org.bukkit.inventory.ItemStack(Material.IRON_HELMET)));
//        this.setItemSlot(EquipmentSlot.CHEST, net.minecraft.world.item.ItemStack.fromBukkitCopy(new org.bukkit.inventory.ItemStack(Material.IRON_CHESTPLATE)));
//        this.setItemSlot(EquipmentSlot.LEGS, net.minecraft.world.item.ItemStack.fromBukkitCopy(new org.bukkit.inventory.ItemStack(Material.IRON_LEGGINGS)));
//        this.setItemSlot(EquipmentSlot.FEET, net.minecraft.world.item.ItemStack.fromBukkitCopy(new org.bukkit.inventory.ItemStack(Material.IRON_BOOTS)));

        // Player player = this.getBukkitEntity().getPlayer();

//         player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
//         player.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

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

    public void livingEntityAiStep() {
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
        this.serverAiStep();
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

            // Bukkit.broadcastMessage("travelling " + vec3d1.length() + " distance");
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

    }

    @Override
    public void travel(Vec3 movementInput) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        double d3;
        if (this.isSwimming() && !this.isPassenger()) {
            d3 = this.getLookAngle().y;
            double d4 = d3 < -0.2 ? 0.085 : 0.06;
            if (d3 <= 0.0 || this.jumping || !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
                Vec3 vec3d1 = this.getDeltaMovement();
                this.setDeltaMovement(vec3d1.add(0.0, (d3 - vec3d1.y) * d4, 0.0));
            }
        }

        super.travel(movementInput);

    }


    public void playerAiStep() {
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
        livingEntityAiStep();
        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float f;
        if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
            f = Math.min(0.1F, (float) this.getDeltaMovement().horizontalDistance());
        } else {
            f = 0.0F;
        }

        this.bob += (f - this.bob) * 0.4F;


    }




    public void livingEntityTick() {
        // super.tick();
        livingEntityBaseTick();
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
            this.playerAiStep();
//        }

        double d0 = this.getX() - this.xo;
        double d1 = this.getZ() - this.zo;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.yBodyRot;
        float f2 = 0.0F;
        this.oRun = this.run;
        float f3 = 0.0F;
        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) Mth.atan2(d1, d0) * 57.295776F - 90.0F;
            float f5 = Mth.abs(Mth.wrapDegrees(this.getYRot()) - f4);
            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.attackAnim > 0.0F) {
            f1 = this.getYRot();
        }

        if (!this.onGround()) {
            f3 = 0.0F;
        }

        this.run += (f3 - this.run) * 0.3F;
        this.level().getProfiler().push("headTurn");
        f2 = this.tickHeadTurn(f1, f2);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("rangeChecks");

        this.yRotO += (float) Math.round((this.getYRot() - this.yRotO) / 360.0F) * 360.0F;
        this.yBodyRotO += (float) Math.round((this.yBodyRot - this.yBodyRotO) / 360.0F) * 360.0F;
        this.xRotO += (float) Math.round((this.getXRot() - this.xRotO) / 360.0F) * 360.0F;
        this.yHeadRotO += (float) Math.round((this.yHeadRot - this.yHeadRotO) / 360.0F) * 360.0F;
        this.level().getProfiler().pop();
        this.animStep += f2;
        if (this.isFallFlying()) {
            ++this.fallFlyTicks;
        } else {
            this.fallFlyTicks = 0;
        }

        if (this.isSleeping()) {
            this.setXRot(0.0F);
        }

        //this.refreshDirtyAttributes();
    }

    public void entityBaseTick() {


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
    }

    public void livingEntityBaseTick() {
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getSleepingPos().ifPresent(this::setPosToBed);
        }


        //super.baseTick();
        entityBaseTick();
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
    }

    private void setPosToBed(BlockPos pos) {
        this.setPos((double) pos.getX() + 0.5, (double) pos.getY() + 0.6875, (double) pos.getZ() + 0.5);
    }

    public void playerTick() {
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
        livingEntityTick();
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

        int i = 29999999;
        double d0 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double d1 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if (d0 != this.getX() || d1 != this.getZ()) {
            this.setPos(d0, this.getY(), d1);
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


    public void mobServerAiStep() {
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
    }

    protected void serverAiStep() {
        //super.serverAiStep();
        this.mobServerAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public void doTick() {
        serverPlayerDoTick();
    }

    public void toggleOn() {
        on = true;
    }

    public void toggleOff() {
        on = false;
    }

    @Override
    public void tick() {
        // tick() calls serverPlayerTick() and doTick()
        // doTick() calls serverPlayerDoTick()
        // serverPlayerDoTick() calls playerTick()
        // playerTick() calls livingEntityTick()
        // livingEntityTick() calls livingEntityBaseTick() and playerAiStep()
        // livingEntityBaseTick() calls entityBaseTick()
        // playerAiStep() calls livingEntityAiStep()
        // livingEntityAiStep() calls serverAiStep()

        if (on) {
            serverPlayerTick();
            doTick();
//            org.bukkit.entity.Player player = Bukkit.getPlayer("Sakrajin");
//            net.minecraft.world.entity.player.Player nmsPlayer = ((CraftPlayer) player).getHandle();
//            this.lookAt(EntityAnchorArgument.Anchor.EYES, nmsPlayer.getEyePosition().subtract(0,5,0));

            masterGoalSelector.tick();
            targetSelector.tick();
            inventoryTracker.tick();
//            sourceManager.tick();
        }

//        if (!masterGoalSelector.doingGoal("break wood")) {
//            masterGoalSelector.addMasterGoal(new GatherBlocks("break wood", this, Blocks.OAK_WOOD, 1));
//        }

//        if (! masterGoalSelector.doingGoal("kill hostile entity")) {
//            if (targetSelector.retrieveTopHostile() instanceof LivingEntity hostile &&  (!Vec3Utils.isObstructed(this.getPosition(0), hostile.getPosition(0), this.level()))){
//                masterGoalSelector.addMasterGoal(new KillTargetEntity("kill hostile entity", this, hostile));
//            }
//            else {
//                if (!masterGoalSelector.doingGoal("roam")){
//                    masterGoalSelector.addMasterGoal(new RandomExploration("roam", this, null));
//                }
//                if (! inventoryTracker.hasEnoughFood()){
//                    if (! masterGoalSelector.doingGoal("kill food entity")) {
//                        if (targetSelector.retrieveTopPeaceful() instanceof LivingEntity peaceful){
//                            masterGoalSelector.addMasterGoal(new KillTargetEntity("kill food entity", this, peaceful));
//                        }
//                    }
//                } else if (inventoryTracker.hasFood()){
//                    this.eat(this.level(), inventoryTracker.getMostAppropriateFood());
//                }
//            }
//        }


//        if (owner != null) {
//            if (this.distanceToSqr(this.owner) <= 144.0) {
//                //this.navigation.moveTo(this.owner, 10);
//            }
//            else {
//                if (! masterGoalSelector.hasGoal()) {
//                    masterGoalSelector.addMasterGoal(new KillTargetEntity("kill", this));
//
//                }
//            }
//        }


        /*        if (this.isUsingItem()) {
                    int drawingTime = this.server.getTickCount() - timeSinceBowDraw;
                    if (drawingTime >= 20) {
                        this.stopUsingItem();
                        this.performRangedAttack(owner, BowItem.getPowerForTime(drawingTime));
                        this.attackTime = this.attackIntervalMin;
                        this.timeSinceBowDraw = -1;
                    }
                } else if (--this.attackTime <= 0) {
                    this.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
                    timeSinceBowDraw = this.server.getTickCount();
                }
*/
        // checkAndPerformAttack(owner);


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

    public void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            performAttack(target);
        }
    }

    public void performAttack(LivingEntity target) {
        this.resetAttackStrengthTicker();
        this.swing(InteractionHand.MAIN_HAND);
        this.lookControl.setLookAt(target);
        this.attack(target);

        //this.indicateDamage();
    }

    public boolean livingEntityHurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.level().isClientSide) {
            return false;
        } else if (!this.isRemoved() && !this.dead && !(this.getHealth() <= 0.0F)) {
            if (source.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                return false;
            } else {
                if (this.isSleeping() && !this.level().isClientSide) {
                    this.stopSleeping();
                }

                this.noActionTime = 0;
                float f1 = amount;
                boolean flag = amount > 0.0F && this.isDamageSourceBlocked(source);
                float f2 = 0.0F;
                if (source.is(DamageTypeTags.IS_FREEZING) && this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    amount *= 5.0F;
                }

                this.walkAnimation.setSpeed(1.5F);
                boolean flag1 = true;
                if ((float)this.invulnerableTime > (float)this.invulnerableDuration / 2.0F && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                    if (amount <= this.lastHurt) {
                        return false;
                    }


                    this.lastHurt = amount;
                    flag1 = false;
                } else {


                    this.lastHurt = amount;
                    this.invulnerableTime = this.invulnerableDuration;
                    this.hurtDuration = 10;
                    this.hurtTime = this.hurtDuration;
                }

                Entity entity1 = source.getEntity();
                if (entity1 != null) {
                    if (entity1 instanceof LivingEntity) {
                        LivingEntity entityliving1 = (LivingEntity)entity1;
                        if (!source.is(DamageTypeTags.NO_ANGER)) {
                            this.setLastHurtByMob(entityliving1);
                        }
                    }

                    if (entity1 instanceof Player) {
                        Player entityhuman = (Player)entity1;
                        this.lastHurtByPlayerTime = 100;
                        this.lastHurtByPlayer = entityhuman;
                    } else if (entity1 instanceof Wolf) {
                        Wolf entitywolf = (Wolf)entity1;
                        if (entitywolf.isTame()) {
                            this.lastHurtByPlayerTime = 100;
                            LivingEntity entityliving2 = entitywolf.getOwner();
                            if (entityliving2 instanceof Player) {
                                Player entityhuman1 = (Player)entityliving2;
                                this.lastHurtByPlayer = entityhuman1;
                            } else {
                                this.lastHurtByPlayer = null;
                            }
                        }
                    }
                }

                boolean flag2;
                if (flag1) {
                    if (flag) {
                        this.level().broadcastEntityEvent(this, (byte)29);
                    } else {
                        this.level().broadcastDamageEvent(this, source);
                    }

                    if (!source.is(DamageTypeTags.NO_IMPACT) && (!flag || amount > 0.0F)) {
                        this.markHurt();
                    }

                    if (entity1 != null && !source.is(DamageTypeTags.NO_KNOCKBACK)) {
                        flag2 = entity1.distanceToSqr(this) > 40000.0;
                        double d0 = flag2 ? Math.random() - Math.random() : entity1.getX() - this.getX();

                        double d1;
                        for(d1 = flag2 ? Math.random() - Math.random() : entity1.getZ() - this.getZ(); d0 * d0 + d1 * d1 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01) {
                            d0 = (Math.random() - Math.random()) * 0.01;
                        }

                        if (!flag) {
                            this.indicateDamage(d0, d1);
                        }
                    }
                }

                if (this.isDeadOrDying()) {
//                    if (!this.checkTotemDeathProtection(source)) {
//                        this.silentDeath = !flag1;
//                        this.die(source);
//                        this.silentDeath = false;
//                    }
                } else if (flag1) {
                    this.playHurtSound(source);
                }

                flag2 = !flag || amount > 0.0F;
//                if (flag2) {
//                    this.lastDamageSource = source;
//                    this.lastDamageStamp = this.level().getGameTime();
//                }

                if (this instanceof ServerPlayer) {
                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer)this, source, f1, amount, flag);
                    if (f2 > 0.0F && f2 < 3.4028235E37F) {
                        ((ServerPlayer)this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f2 * 10.0F));
                    }
                }

                if (entity1 instanceof ServerPlayer) {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity1, this, source, f1, amount, flag);
                }

                return flag2;
            }
        } else {
            return false;
        }
    }

    public boolean playerHurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.getAbilities().invulnerable && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                if (!this.level().isClientSide) {
                }

                if (source.scalesWithDifficulty()) {
                    if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                        return false;
                    }

                    if (this.level().getDifficulty() == Difficulty.EASY) {
                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                    }

                    if (this.level().getDifficulty() == Difficulty.HARD) {
                        amount = amount * 3.0F / 2.0F;
                    }
                }

                // boolean damaged = super.hurt(source, amount);
                boolean damaged = livingEntityHurt(source, amount);
                if (damaged) {
                    this.removeEntitiesOnShoulder();
                }

                return damaged;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean flag = this.server.isDedicatedServer() && source.is(DamageTypeTags.IS_FALL);
            if (!flag && this.spawnInvulnerableTime > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                Entity entity = source.getEntity();
                if (entity instanceof Player) {
                    Player entityhuman = (Player) entity;
                    if (!this.canHarmPlayer(entityhuman)) {
                        return false;
                    }
                }

                if (entity instanceof AbstractArrow) {
                    AbstractArrow entityarrow = (AbstractArrow) entity;
                    Entity entity1 = entityarrow.getOwner();
                    if (entity1 instanceof Player) {
                        Player entityhuman1 = (Player) entity1;
                        if (!this.canHarmPlayer(entityhuman1)) {
                            return false;
                        }
                    }
                }


                //boolean damaged = super.hurt(source, amount);
                boolean damaged = playerHurt(source, amount);
                return damaged;
            }
        }
    }

    public void attack(Entity target) {
        boolean willAttack = target.isAttackable() && !target.skipAttackInteraction(this);
        PrePlayerAttackEntityEvent playerAttackEntityEvent = new PrePlayerAttackEntityEvent((org.bukkit.entity.Player) this.getBukkitEntity(), target.getBukkitEntity(), willAttack);
        if (!playerAttackEntityEvent.isCancelled() && willAttack) {
            float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = 0;

            float f2 = this.getAttackStrengthScale(0.5F);
            f *= 0.2F + f2 * f2 * 0.8F;
            f1 *= f2;
            if (f > 0.0F || f1 > 0.0F) {
                boolean flag = f2 > 0.9F;
                boolean flag1 = false;

                if (this.isSprinting() && flag) {
                    // sendSoundEffect(this, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);

                    flag1 = true;
                }

                boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && target instanceof LivingEntity;
                flag2 = flag2 && !this.level().paperConfig().entities.behavior.disablePlayerCrits;
                flag2 = flag2 && !this.isSprinting();
                if (flag2) {
                    f *= 1.5F;
                }

                f += f1;
                boolean flag3 = false;
                double d0 = (double) (this.walkDist - this.walkDistO);
                if (flag && !flag2 && !flag1 && this.onGround() && d0 < (double) this.getSpeed()) {
                    ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
                    if (itemstack.getItem() instanceof SwordItem) {
                        flag3 = true;
                    }
                }

                Vec3 vec3d = target.getDeltaMovement();
                target.hurt(this.damageSources().playerAttack(this).critical(flag2), f);

            }
        }

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

    //    protected void findTarget() {
//        if (this.targetType != net.minecraft.world.entity.player.Player.class && this.targetType != ServerPlayer.class) {
//            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (entityliving) -> {
//                return true;
//            }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
//        } else {
//            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
//        }
//
//    }

//    protected AABB getTargetSearchArea(double distance) {
//        return this.mob.getBoundingBox().inflate(distance, 4.0, distance);
//    }

//    private Vec3 getPosition() {
//
//    }

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

    public void serverPlayerDoTick() {
        //try {
        if (this.valid && !this.isSpectator() || !this.touchingUnloadedChunk()) {
            // super.tick();
            playerTick();
        }


    }

    @Override
    public void onEnterCombat() {

    }

    @Override
    public void onLeaveCombat() {

    }

    @Override
    public void die(DamageSource damageSource) {
        this.gameEvent(GameEvent.ENTITY_DIE);
        boolean flag = this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if (!this.isRemoved()) {
            List<DefaultDrop> loot = new ArrayList(this.getInventory().getContainerSize());
            boolean keepInventory = this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || this.isSpectator();
            if (!keepInventory) {
                Iterator var5 = this.getInventory().getContents().iterator();

                while (var5.hasNext()) {
                    ItemStack item = (ItemStack) var5.next();
                        loot.add(new DefaultDrop(item, (stack) -> {
                            this.drop(stack, true, false);
                        }));
                }
            }

            if (this.shouldDropLoot() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.dropFromLootTable(damageSource, this.lastHurtByPlayerTime > 0);
                loot.addAll(this.drops);
                this.drops.clear();
            }
            net.minecraft.network.chat.Component defaultMessage = this.getCombatTracker().getDeathMessage();
            this.keepLevel = keepInventory;


            this.removeEntitiesOnShoulder();

            this.setCamera(this);
            LivingEntity entityliving = this.getKillCredit();
            if (entityliving != null) {
                this.awardStat(Stats.ENTITY_KILLED_BY.get(entityliving.getType()));
                entityliving.awardKillScore(this, this.deathScore, damageSource);
                this.createWitherRose(entityliving);
            }

            this.level().broadcastEntityEvent(this, (byte) 3);
            this.awardStat(Stats.DEATHS);
            this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
            this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
            this.clearFire();
            this.setTicksFrozen(0);
            this.setSharedFlagOnFire(false);
            this.getCombatTracker().recheckStatus();
            this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
            // }
        }
    }



    protected void livingEntityOnEffectUpdated(MobEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
        this.effectsDirty = true;
        if (reapplyEffect && !this.level().isClientSide) {
            MobEffect mobeffectlist = effect.getEffect().value();
            mobeffectlist.removeAttributeModifiers(this.getAttributes());
            mobeffectlist.addAttributeModifiers(this.getAttributes(), effect.getAmplifier());
            // this.refreshDirtyAttributes();
        }

        if (!this.level().isClientSide) {
            //     this.sendEffectToPassengers(effect);
        }

    }

    @Override
    protected void onEffectUpdated(MobEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
        livingEntityOnEffectUpdated(effect, reapplyEffect, source);
        //      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), effect));
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, source);
    }

    protected void livingEntityOnEffectRemoved(MobEffectInstance effect) {
        this.effectsDirty = true;
        if (!this.level().isClientSide) {
            effect.getEffect().value().removeAttributeModifiers(this.getAttributes());
            //  this.refreshDirtyAttributes();
            Iterator iterator = this.getPassengers().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                if (entity instanceof ServerPlayer) {
                    ServerPlayer entityplayer = (ServerPlayer) entity;
                    //         entityplayer.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), effect.getEffect()));
                }
            }
        }

    }

    @Override
    protected void onEffectRemoved(MobEffectInstance effect) {
        livingEntityOnEffectRemoved(effect);
        //  this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), effect.getEffect()));
        if (effect.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartPos = null;
        }

        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, (Entity) null);
    }

    @Override
    public void indicateDamage(double deltaX, double deltaZ) {
        this.hurtDir = (float) (Mth.atan2(deltaZ, deltaX) * 57.2957763671875 - (double) this.getYRot());
        // this.connection.send(new ClientboundHurtAnimationPacket(this));
    }

}