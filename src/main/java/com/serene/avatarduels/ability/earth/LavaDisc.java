package com.serene.avatarduels.ability.earth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.policies.removal.*;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.earthbending.passive.DensityShift;
import com.projectkorra.projectkorra.region.RegionProtection;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.serene.avatarduels.JCMethods;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.util.RegenTempBlock;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.firebending.util.FireDamageTimer;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

public class LavaDisc extends LavaAbility implements AddonAbility {

	private Location location;
	private int recallCount;

	private long time;

	@Attribute(Attribute.DAMAGE)
	private double damage;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.DURATION)
	private long duration;
	private int recallLimit;
	private boolean trailFlow;

	private CompositeRemovalPolicy removalPolicy;
	private DiscRenderer discRenderer;
	private State state;
	private final Set<Block> trailBlocks = new HashSet<>();

	public LavaDisc(Player player) {
		super(player);

		if (!bPlayer.canBend(this) || !bPlayer.canLavabend()) {
			return;
		}

		// Allow new LavaDisc if all existing instances for that player are in CleanupState.
		for (LavaDisc disc : CoreAbility.getAbilities(player, LavaDisc.class)) {
			if (!(disc.state instanceof CleanupState)) {
				return;
			}
		}

		state = new HoldState();
		time = System.currentTimeMillis();
		discRenderer = new DiscRenderer(this.player);

		setFields();

		if (prepare()) {
			start();
		}
	}

	public void setFields() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		
		damage = config.getDouble("Abilities.Earth.LavaDisc.Damage");
		cooldown = config.getLong("Abilities.Earth.LavaDisc.Cooldown");
		duration = config.getLong("Abilities.Earth.LavaDisc.Duration");
		recallLimit = config.getInt("Abilities.Earth.LavaDisc.RecallLimit") - 1;
		trailFlow = config.getBoolean("Abilities.Earth.LavaDisc.Destroy.TrailFlow");

		this.removalPolicy = new CompositeRemovalPolicy(this,
				new CannotBendRemovalPolicy(this.bPlayer, this, true, true),
				new IsOfflineRemovalPolicy(this.player),
				new IsDeadRemovalPolicy(this.player),
				new SwappedSlotsRemovalPolicy<>(bPlayer, LavaDisc.class)
		);

		this.removalPolicy.load(config);
	}

	private boolean prepare() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());

		long sourceRegen = config.getLong("Abilities.Earth.LavaDisc.Source.RegenTime");
		boolean lavaOnly = config.getBoolean("Abilities.Earth.LavaDisc.Source.LavaOnly");
		double sourceRange = config.getDouble("Abilities.Earth.LavaDisc.Source.Range");

		if (getLavaSourceBlock(player, sourceRange) != null) {
			Block block = getLavaSourceBlock(player, sourceRange);
			new RegenTempBlock(block, Material.LAVA, Material.LAVA.createBlockData(bd -> ((Levelled)bd).setLevel(4)), sourceRegen);
			return true;
		} else if (getEarthSourceBlock(sourceRange) != null) {
			if (lavaOnly)
				return false;
			Block block = getEarthSourceBlock(sourceRange);
			new RegenTempBlock(block, Material.LAVA, Material.LAVA.createBlockData(bd -> ((Levelled)bd).setLevel(4)), sourceRegen);
			return true;
		}

		return false;
	}

	@Override
	public void progress() {
		if (this.removalPolicy.shouldRemove()) {
			if (!player.isOnline()) {
				// Revert all of the lava blocks if the player goes offline.
				for (Block block : trailBlocks) {
					RegenTempBlock.revert(block);
				}
				bPlayer.addCooldown(this);
				remove();
				return;
			} else if (!(state instanceof CleanupState)) {
				state = new CleanupState();
			}
		}

		if (!hasAbility(player, LavaDisc.class)) {
			return;
		}

		state.update();
	}

	public static boolean canFlowFrom(Block from) {
		Material type = from.getType();
		if (type != Material.LAVA && !ElementalAbility.isAir(type)) {
			return true;
		}

		for (LavaDisc disc : CoreAbility.getAbilities(LavaDisc.class)) {
			if (disc.trailFlow) continue;

			if (disc.trailBlocks.contains(from)) {
				return false;
			}
		}

		return true;
	}

	private boolean isLocationSafe() {
		if (!isLocationSafe(location)) {
			return false;
		}

		Block block = location.getBlock();

		return isTransparent(block);
	}

	private boolean isLocationSafe(Location location) {
		if (location == null || location.getWorld() == null) {
			return false;
		}

		return  location.getY() <= (location.getWorld().getMaxHeight() - 1);
	}

	private void doDamage(Entity entity) {
		DamageHandler.damageEntity(entity, damage, this);
		entity.setFireTicks(20);
		new FireDamageTimer(entity, player, this);
		ParticleEffect.LAVA.display(entity.getLocation(), 15, Math.random(), Math.random(), Math.random(), 0.1);
	}
	
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return "LavaDisc";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public String getAuthor() {
		return AvatarDuels.dev;
	}

	@Override
	public String getVersion() {
		return AvatarDuels.version;
	}

	@Override
	public String getDescription() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return "* AvatarDuels Addon *\n" + config.getString("Abilities.Earth.LavaDisc.Description");
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getRecallCount() {
		return recallCount;
	}

	public void setRecallCount(int recallCount) {
		this.recallCount = recallCount;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRecallLimit() {
		return recallLimit;
	}

	public void setRecallLimit(int recallLimit) {
		this.recallLimit = recallLimit;
	}

	public boolean isTrailFlow() {
		return trailFlow;
	}

	public void setTrailFlow(boolean trailFlow) {
		this.trailFlow = trailFlow;
	}

	public DiscRenderer getDiscRenderer() {
		return discRenderer;
	}

	public void setDiscRenderer(DiscRenderer discRenderer) {
		this.discRenderer = discRenderer;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Set<Block> getTrailBlocks() {
		return trailBlocks;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}

	@Override
	public boolean isEnabled() {
		ConfigurationSection config = AvatarDuels.getConfig(getName());
		return config.getBoolean("Abilities.Earth.LavaDisc.Enabled");
	}

	private interface State {
		void update();
	}

	// Renders the particles showing that the player is holding lava.
	// Transitions to ForwardTravelState when the player stops sneaking.
	private class HoldState implements State {
		@Override
		public void update() {
			location = player.getEyeLocation();
			Vector dV = location.getDirection().normalize();
			location.add(new Vector(dV.getX() * 3, dV.getY() * 3, dV.getZ() * 3));

			dV = dV.multiply(0.1);

			while (!isLocationSafe() && isLocationSafe(player.getLocation())) {
				location.subtract(dV);
				if (location.distanceSquared(player.getEyeLocation()) > (3 * 3)) {
					break;
				}
			}

			discRenderer.render(location, false);

			location.setPitch(0);

			if (!player.isSneaking()) {
				time = System.currentTimeMillis();
				state = new ForwardTravelState(location.getDirection().normalize());
			}
		}
	}

	private abstract class TravelState implements State {
		private final boolean passHit;

		protected Vector direction;
		protected boolean hasHit;

		public TravelState() {
			this(player.getEyeLocation().getDirection());
		}

		public TravelState(Vector direction) {
			this.direction = direction;

			ConfigurationSection config = AvatarDuelsConfig.getConfig(player);

			passHit = config.getBoolean("Abilities.Earth.LavaDisc.ContinueAfterEntityHit");
		}

		protected void move() {
			for (int i = 0; i < 5; i++) {
				location = location.add(direction.clone().multiply(0.15));

				for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2.0D)) {
					if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId()) {
						doDamage(entity);
						if (!passHit) {
							hasHit = true;
							return;
						}
					}
				}
			}
		}
	}

	// Moves the disc forward. Makes the disc destroy blocks if enabled.
	// Transitions to ReverseTravelState if the player starts sneaking and can recall.
	// Transitions to CleanupState if it times out or hits an entity.
	private class ForwardTravelState extends TravelState {
		public ForwardTravelState() {
			this(player.getEyeLocation().getDirection());
		}

		public ForwardTravelState(Vector direction) {
			super(direction);
		}

		@Override
		public void update() {
			if (!isLocationSafe() || System.currentTimeMillis() > time + duration) {
				state = new CleanupState();
				return;
			}

			if (player.isSneaking() && recallCount <= recallLimit) {
				state = new ReverseTravelState();
				return;
			}

			alterPitch();
			move();
			discRenderer.render(location, true);

			if (hasHit) {
				state = new CleanupState();
			}
		}

		private void alterPitch() {
			Location loc = player.getLocation().clone();

			if (loc.getPitch() < -20)
				loc.setPitch(-20);
			if (loc.getPitch() > 20)
				loc.setPitch(20);

			direction = loc.getDirection().normalize();
		}
	}

	// Returns the disc to the player.
	// Transitions to ForwardTravelState if the player stops sneaking.
	// Transitions to HoldState if the disc gets close enough to the player.
	private class ReverseTravelState extends TravelState {
		@Override
		public void update() {
			if (!player.isSneaking()) {
				state = new ForwardTravelState();
				return;
			}

			Location loc = player.getEyeLocation();
			Vector dV = loc.getDirection().normalize();
			loc.add(new Vector(dV.getX() * 3, dV.getY() * 3, dV.getZ() * 3));

			Vector vector = loc.toVector().subtract(location.toVector());
			direction = loc.setDirection(vector).getDirection().normalize();

			move();
			discRenderer.render(location, true);

			double distanceAway = location.distance(loc);
			if (distanceAway < 0.5) {
				recallCount++;
				// Player is holding the disc when it gets close enough to them.
				state = new HoldState();
			}
		}
	}

	// Waits for the RegenTempBlocks to revert.
	// This exists so the instance stays alive and block flow events can stop the lava from flowing.
	private class CleanupState implements State {
		private final long startTime;
		private final long regenTime;

		public CleanupState() {
			this.startTime = System.currentTimeMillis();

			ConfigurationSection config = AvatarDuelsConfig.getConfig(player);

			regenTime = config.getLong("Abilities.Earth.LavaDisc.Destroy.RegenTime");
			bPlayer.addCooldown(LavaDisc.this);
		}

		@Override
		public void update() {
			if (System.currentTimeMillis() >= startTime + regenTime || trailBlocks.isEmpty()) {
				remove();
			}
		}
	}

	private class DiscRenderer {
		private final Player player;
		private int angle;

		private final boolean damageBlocks;
		private final List<String> meltable;
		private final long regenTime;
		private final boolean lavaTrail;

		private final int particles;


		public DiscRenderer(Player player) {
			this.player = player;
			this.angle = 0;

			ConfigurationSection config = AvatarDuels.getConfig(getName());

			damageBlocks = config.getBoolean("Abilities.Earth.LavaDisc.Destroy.BlockDamage");
			meltable = config.getStringList("Abilities.Earth.LavaDisc.Destroy.AdditionalMeltableBlocks");
			regenTime = config.getLong("Abilities.Earth.LavaDisc.Destroy.RegenTime");
			lavaTrail = config.getBoolean("Abilities.Earth.LavaDisc.Destroy.LavaTrail");
			particles = config.getInt("Abilities.Earth.LavaDisc.Particles");
		}

		void render(Location location, boolean largeLava) {
			if (largeLava)
				ParticleEffect.LAVA.display(location, particles * 2, Math.random(), Math.random(), Math.random(), 0.1);
			else
				ParticleEffect.LAVA.display(location, 1, Math.random(), Math.random(), Math.random(), 0.1);

			angle += 1;
			if (angle > 360)
				angle = 0;

			for (Location l : JCMethods.getCirclePoints(location, 20, 1, angle)) {
				ParticleEffect.REDSTONE.display(l, 0, 196, 93, 0, 0.005F, new Particle.DustOptions(Color.fromRGB(196, 93, 0), 1));
				if (largeLava && damageBlocks)
					damageBlocks(l);
			}

			for (Location l : JCMethods.getCirclePoints(location, 10, 0.5, angle)) {
				ParticleEffect.FLAME.display(l, 1, 0, 0, 0, 0.01);
				ParticleEffect.SMOKE_NORMAL.display(l, 1, 0, 0, 0, 0.05);
				if (largeLava && damageBlocks)
					damageBlocks(l);
			}
		}

		private void damageBlocks(Location l) {
			if (!RegionProtection.isRegionProtected(player, l, LavaDisc.this)) {
				if (!TempBlock.isTempBlock(l.getBlock()) && (isEarthbendable(player, l.getBlock()) || isMetal(l.getBlock()) || meltable.contains(l.getBlock().getType().name()))) {
					if (DensityShift.isPassiveSand(l.getBlock())) {
						DensityShift.revertSand(l.getBlock());
					}

					if (lavaTrail) {
						new RegenTempBlock(l.getBlock(), Material.LAVA, Material.LAVA.createBlockData(bd -> ((Levelled) bd).setLevel(4)), regenTime);

						trailBlocks.add(l.getBlock());
					} else {
						new RegenTempBlock(l.getBlock(), Material.AIR, Material.AIR.createBlockData(), regenTime);
					}

					ParticleEffect.LAVA.display(l, particles * 2, Math.random(), Math.random(), Math.random(), 0.2);
				}
			}
		}
	}
}
