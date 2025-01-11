package com.serene.avatarduels.spirits.ability.spirit;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.ability.api.SpiritAbility;
import com.serene.avatarduels.spirits.utilities.Methods;
import com.serene.avatarduels.spirits.utilities.PossessRecoil;
import com.serene.avatarduels.spirits.utilities.TempSpectator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Possess extends SpiritAbility {

    //TODO: Test how it interacts with sudden change in pathway (like the spawning of a RaiseEarth that obstructs it's path)
    //TODO: Test how it interacts with abilities like AirShield and Shelter.
    //TODO: Add configurable speed for the armor stand/blast feature.

    private static final Map<UUID, Possess> VICTIMS = new HashMap<>();
    private static final Map<Entity, Possess> ENTITY_VICTIMS = new HashMap<>();
    //Sound effect
    private final float pitch = 0F;
    private final float volume = 0.1F;
    private double minDamage;
    @Attribute("SelfDamage")
    private double selfDamage;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.RANGE)
    private double range;
    @Attribute("Durability")
    private int durability;
    @Attribute(Attribute.SPEED)
    private double speed;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.DURATION)
    private long duration;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargeTime;
    private int breakingDurability; //The counter used to determine when the possession is broken.
    private State state = State.CHARGING;
    private long possessStartTime;
    private boolean releasedSneak;
    private ArmorStand armorStand;
    private LivingEntity target;
    private TempSpectator spectator;
    private double distanceTraveled = 0;
    private String possessString;
    private String possessEnd;
    private String possessBreak;
    private String durabilityString;
    private String durabilityChar;
    public Possess(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) {
            return;
        }

        setFields();
        start();
    }

    /**
     * Makes the player punch any players currently possessing them
     *
     * @param player The player punching
     * @return True if the player managed to punch a possessor
     */
    public static boolean punchPossessing(Player player) {
        Possess instance = VICTIMS.get(player.getUniqueId());
        if (instance != null) {
            instance.breakDurability();
            return true;
        }
        return false;
    }

    public static boolean stopSpectating(Player player) {
        Possess possess = CoreAbility.getAbility(player, Possess.class);
        if (possess != null) {
            if (possess.state == State.TRAVELING) return true;
            if (possess.state == State.POSSESSING) {
                if (!possess.releasedSneak) {
                    possess.releasedSneak = true;
                    return true; //Stop them dismounting still
                } else {
                    possess.finalBlow();
                    return false;
                }
            }
        }
        return false;
    }

    public static Possess getPossessed(Entity entity) {
        return ENTITY_VICTIMS.get(entity);
    }

    public void setFields() {
        ConfigurationSection config = AvatarDuels.getConfig(getName());
        this.cooldown = config.getLong("Abilities.Spirits.Neutral.Possess.Cooldown");
        this.range = config.getDouble("Abilities.Spirits.Neutral.Possess.Range");
        this.damage = config.getDouble("Abilities.Spirits.Neutral.Possess.MaxDamage");
        this.minDamage = config.getDouble("Abilities.Spirits.Neutral.Possess.MinDamage");
        this.selfDamage = config.getDouble("Abilities.Spirits.Neutral.Possess.FailureSelfDamage");
        this.duration = config.getLong("Abilities.Spirits.Neutral.Possess.Duration");
        this.chargeTime = config.getLong("Abilities.Spirits.Neutral.Possess.ChargeTime");
        this.durability = config.getInt("Abilities.Spirits.Neutral.Possess.Durability", 8);
        this.speed = config.getDouble("Abilities.Spirits.Neutral.Possess.Speed");
        this.possessString = ChatUtil.color(config.getString("Language.Abilities.Spirit.Possess.Possessed"));
        this.possessEnd = ChatUtil.color(config.getString("Language.Abilities.Spirit.Possess.PossessionEnd"));
        this.possessBreak = ChatUtil.color(config.getString("Language.Abilities.Spirit.Possess.PossessionBreak"));
        this.durabilityString = ChatUtil.color(config.getString("Language.Abilities.Spirit.Possess.Durability"));
        this.durabilityChar = ChatUtil.color(config.getString("Language.Abilities.Spirit.Possess.DurabilityChar"));
        this.spectator = TempSpectator.create(player);
    }

    @Override
    public void progress() {
        if (!spectator.canBend(this)) {
            remove();
        } else if (state == State.CHARGING) {
            chargeDisplay();
        } else if (state == State.TRAVELING) {
            travelDisplay();
        } else {
            possessionTick();
        }
    }

    /**
     * Called every tick while charging. Displays particles and plays sounds.
     */
    private void chargeDisplay() {
        long charge = System.currentTimeMillis() - this.getStartTime();
        if (charge >= chargeTime) {
            if (player.isSneaking()) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.3F, -1);
                player.getWorld().spawnParticle(Particle.FIREWORK, player.getEyeLocation(), 1, 0.3F, 0.3F, 0.3F, 0);

            } else {
                this.armorStand = this.createArmorStand();
                spectator.spectator();
                player.setFlySpeed(0F);
                this.state = State.TRAVELING;
            }
        } else {
            player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 2, 0.3F, 0.3F, 0.3F, 0);
        }
    }

    /**
     * Called every tick while traveling. Progresses the location and displays particles.
     */
    private void travelDisplay() {
        this.distanceTraveled += speed;

        if (player.getEyeLocation().getBlock().getType().isSolid()) {
            //player.sendMessage("Removed from solid");
            remove();
            return;
        }

        player.setVelocity(player.getEyeLocation().getDirection().multiply(speed));
        armorStand.teleport(player.getLocation());

        if (getRunningTicks() % 5 == 0) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
        }
        player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getEyeLocation(), 5, 0.4, 0.4, 0.4, 0);

        if (this.distanceTraveled >= range) {
            //player.sendMessage("Removed from range");
            remove();
            return;
        }

        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getEyeLocation(), 0.4,
                entity -> entity instanceof LivingEntity && entity != this.player && !(entity instanceof ArmorStand))) {
            possess((LivingEntity) entity);
            break;
        }

    }

    /**
     * Called every tick when the target has successfully been possessed.
     */
    private void possessionTick() {
        if (target.getWorld() != player.getWorld()
                || (target instanceof Player && ((Player) target).getGameMode() == GameMode.SPECTATOR)) {
            remove();
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 1));

        double yy = target.getHeight() / 2D;
        double xx = (target.getWidth() * 1.2) / 2D;

        target.getWorld().spawnParticle(Particle.WITCH, target.getLocation().add(0, yy, 0), 1, xx, yy, xx, 0);
        Methods.playSpiritParticles(player, target.getLocation().add(0, yy, 0), xx, yy, xx, 0, 1);

        if (getRunningTicks() % 5 == 0) {
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, this.volume, this.pitch);
        }

        if (player.isSneaking() || player.getSpectatorTarget() != target) {
            this.finalBlow();
            return;
        }

        if (System.currentTimeMillis() > possessStartTime + duration) {
            this.finalBlow();
            return;
        }

        ActionBar.sendActionBar(getDurabilityString(), player);

        if (!(target instanceof Player) && target instanceof Monster) {
            if (target instanceof Creeper) {
                ((Creeper) target).ignite();
                return;
            }

            int hp = (int) target.getMaxHealth() + 1;

            for (int i = 0; i < hp; i++) {
                if (Math.random() < 0.004) {
                    breakDurability();
                    break;
                }
            }
        }
    }

    public String getDurabilityString() {
        double percentage = 1 - ((double) breakingDurability) / ((double) durability);
        double bigPercentage = percentage * 100;
        ChatColor color = ChatColor.DARK_RED;
        if (bigPercentage >= 90) color = ChatColor.DARK_GREEN;
        else if (bigPercentage >= 60) color = ChatColor.GREEN;
        else if (bigPercentage >= 40) color = ChatColor.YELLOW;
        else if (bigPercentage > 25) color = ChatColor.GOLD;
        else if (bigPercentage > 5) color = ChatColor.RED; //5% or lower will be dark red since that is the default

        String completed = "";
        for (int i = 1; i <= durability; i++) {
            if (i <= breakingDurability) {
                completed = ChatColor.GRAY + durabilityChar + completed;
            } else {
                completed = color + durabilityChar + completed;
            }
        }

        return durabilityString.replace("{durability}", completed);
    }

    /**
     * Possess the target
     *
     * @param entity The target to possess
     */
    private void possess(LivingEntity entity) {
        this.target = entity;
        this.possessStartTime = System.currentTimeMillis();
        player.setSpectatorTarget(this.target);
        this.state = State.POSSESSING;
        if (!player.isSneaking()) releasedSneak = true;
        this.armorStand.remove();
        if (target instanceof Player) {
            if (VICTIMS.containsKey(target.getUniqueId())) { //From another player
                Possess other = VICTIMS.get(target.getUniqueId());
                other.remove(); //Force them out, damage the player
            }
            VICTIMS.put(target.getUniqueId(), this);
            ActionBar.sendActionBar(this.possessString, (Player) target);
        } else {
            if (ENTITY_VICTIMS.containsKey(target)) { //From another player
                Possess other = ENTITY_VICTIMS.get(target);
                other.remove(); //Force them out, damage the player
            }
            ENTITY_VICTIMS.put(target, this);
        }
    }

    /**
     * Called when the target needs to be damaged
     */
    private void finalBlow() {
        long currentDuration = System.currentTimeMillis() - possessStartTime;
        currentDuration = Math.min(currentDuration, duration); //Make sure the duration is maxed at 100%
        double multiplier = (double) currentDuration / (double) duration;
        double extraDamage = (damage - minDamage) * multiplier; //Calculate the extra damage to give based on the duration possessed

        DamageHandler.damageEntity(target, minDamage + extraDamage, this);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_HURT, 0.2F, 0F);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getEyeLocation(), 1, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.CRIT, target.getEyeLocation(), 20, 0.3, 1, 0.3, 0);

        remove();
    }

    private ArmorStand createArmorStand() {
        ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setBasePlate(false);
        stand.setMarker(true);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(this.player);
        skull.setItemMeta(skullMeta);
        stand.getEquipment().setHelmet(skull);
        return stand;
    }

    /**
     * Call to damage the possessor. If they do this enough, the target can break possession
     *
     * @return True if the target broke out
     */
    public boolean breakDurability() {
        breakingDurability++;

        if (breakingDurability >= durability) {
            remove();
            CoreAbility recoil = new PossessRecoil(this);
            DamageHandler.damageEntity(player, (Player) target, selfDamage, recoil, true);
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_VEX_DEATH, 1F, 1.5F);
            ActionBar.sendActionBar(possessBreak, (Player) target);
            return true;
        } else {
            player.getWorld().playSound(target.getEyeLocation(), Sound.ENTITY_PANDA_BITE, 1F, 1.5F);
            return false;
        }
    }

    @Override
    public void remove() {
        if (spectator.isSpectator()) {
            spectator.revert();
        }

        player.setNoDamageTicks(0);

        if (this.armorStand != null && !this.armorStand.isDead()) this.armorStand.remove();

        if (state == State.TRAVELING) {
            bPlayer.addCooldown(this);
        } else if (state == State.POSSESSING) {
            if (!target.isDead() && target.getWorld() == player.getWorld()) {
                //The location behind them by 3 blocks
                Vector vec = target.getEyeLocation().getDirection().clone().setY(0).normalize().multiply(-1);
                for (int i = 3; i >= 0; i--) { //Try teleport at least 3 blocks back
                    if (i == 0) {
                        player.teleport(target.getEyeLocation());
                        break;
                    }

                    Location newLoc = target.getLocation().add(vec.clone().multiply(i));

                    if (!newLoc.getBlock().getType().isSolid()) {
                        player.teleport(newLoc);
                        break;
                    }
                }
            }

            bPlayer.addCooldown(this);
        }

        for (int i = 0; i < 2; i++) {
            if (player.getLocation().getBlock().getType().isSolid()) {
                player.teleport(player.getLocation().add(0, 1, 0));
            }
        }

        if (target instanceof Player) {
            VICTIMS.remove(target.getUniqueId());
            ActionBar.sendActionBar(possessEnd, (Player) target);
        } else {
            ENTITY_VICTIMS.remove(target);
        }

        super.remove();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return this.player.getLocation();
    }

    @Override
    public String getName() {
        return "Possess";
    }

    @Override
    public String getAbilityType() {
        return OFFENSE;
    }

    @Override
    public boolean isExplosiveAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isIgniteAbility() {
        return false;
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public double getCollisionRadius() {
        return 0.5;
    }

    public enum State {
        CHARGING, TRAVELING, POSSESSING
    }
}