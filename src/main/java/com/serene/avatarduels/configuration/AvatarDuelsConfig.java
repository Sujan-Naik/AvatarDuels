package com.serene.avatarduels.configuration;

import com.serene.avatarduels.AvatarDuels;

import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.serene.avatarduels.AvatarDuels.createAbilityConfig;

public class AvatarDuelsConfig {

	public static Config board;
	static AvatarDuels plugin;
	
	public AvatarDuelsConfig(AvatarDuels plugin) {
		AvatarDuelsConfig.plugin = plugin;
		board = new Config(new File("board.yml"));
		loadConfigBoard();
		loadConfigCore();
		addDeathMessages();
		setupElementSphereNames();
	}
	
	private void loadConfigBoard() {
		FileConfiguration config;
		config = board.getConfig();
		
		config.addDefault("Settings.Enabled", true);
		config.addDefault("Settings.Title", "&lSlots");
		config.addDefault("Settings.Pointer", "> ");
		config.addDefault("Settings.EmptySlot", "&8&o-- Slot % --");
		config.addDefault("Settings.Combos", "&fCombos:");
		config.addDefault("Settings.Toggle.Off", "&7You have hidden the bending board.");
		config.addDefault("Settings.Toggle.On", "&7You have toggled the bending board on.");
		config.addDefault("Settings.Display.DisabledWorlds", true);

		config.addDefault("Settings.OtherCooldowns.WallRun.Color", "GOLD");
		config.addDefault("Settings.OtherCooldowns.WallRun.Enabled", true);
		config.addDefault("Settings.OtherCooldowns.TorrentWave.Color", "AQUA");
		config.addDefault("Settings.OtherCooldowns.TorrentWave.Enabled", true);
		config.addDefault("Settings.OtherCooldowns.SurgeWave.Color", "AQUA");
		config.addDefault("Settings.OtherCooldowns.SurgeWave.Enabled", true);
		config.addDefault("Settings.OtherCooldowns.SurgeWall.Color", "AQUA");
		config.addDefault("Settings.OtherCooldowns.SurgeWall.Enabled", true);
		config.addDefault("Settings.OtherCooldowns.RaiseEarthPillar.Color", "GREEN");
		config.addDefault("Settings.OtherCooldowns.RaiseEarthPillar.Enabled", true);
		config.addDefault("Settings.OtherCooldowns.RaiseEarthWall.Color", "GREEN");
		config.addDefault("Settings.OtherCooldowns.RaiseEarthWall.Enabled", true);
		
		config.options().copyDefaults(true);
		board.saveConfig();
	}



		private void loadConfigCore() {
		FileConfiguration config;
		config = AvatarDuels.instance.getConfig();
		
		config.addDefault("Settings.Updater.Check", true);
		config.addDefault("Settings.Updater.Notify", true);
		config.addDefault("Properties.MobCollisions.Enabled", true);
		config.addDefault("Properties.AbilityCollisions.Enabled", true);
		config.addDefault("Properties.PerWorldConfig", true);
		config.addDefault("Properties.FireTickMethod", "larger");
		config.addDefault("Properties.LogDebug", false);

		config.addDefault("Properties.ChiRestrictor.Enabled", false);
		config.addDefault("Properties.ChiRestrictor.ResetCooldown", true);
		config.addDefault("Properties.ChiRestrictor.MeleeDistance", 7);
		config.addDefault("Properties.ChiRestrictor.Whitelist", new ArrayList<String>());

		config.addDefault("Properties.Fire.DynamicLight.Enabled", true);
		config.addDefault("Properties.Fire.DynamicLight.Brightness", 13);
		config.addDefault("Properties.Fire.DynamicLight.KeepAlive", 600);


// ElementSphere Ability
		createAbilityConfig("ElementSphere",
				new Pair<>("Abilities.Avatar.ElementSphere.Enabled", true),
				new Pair<>("Abilities.Avatar.ElementSphere.Description", "ElementSphere is a very all round ability, being "
						+ "able to shoot attacks of each element, each with a "
						+ "different effect. To use, simply Left-Click. Once active, "
						+ "Sneak (Default: Shift) to fly around. Sneak and double "
						+ "Left-Click to disable the ability! "
						+ "To use each element, simply select hotbar slots 1-4 and Left-Click. "
						+ "Each element has limited uses! Once an element is used up, "
						+ "the element's ring will disappear!"),
				new Pair<>("Abilities.Avatar.ElementSphere.Cooldown", 180000),
				new Pair<>("Abilities.Avatar.ElementSphere.Duration", 60000),
				new Pair<>("Abilities.Avatar.ElementSphere.MaxControlledHeight", 40),
				new Pair<>("Abilities.Avatar.ElementSphere.FlySpeed", 1.5),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Cooldown", 500),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Range", 40),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Uses", 20),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Damage", 3.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Knockback", 2),
				new Pair<>("Abilities.Avatar.ElementSphere.Air.Speed", 3),
				new Pair<>("Abilities.Avatar.ElementSphere.Earth.Cooldown", 500),
				new Pair<>("Abilities.Avatar.ElementSphere.Earth.Uses", 20),
				new Pair<>("Abilities.Avatar.ElementSphere.Earth.Damage", 3.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Earth.ImpactCraterSize", 3),
				new Pair<>("Abilities.Avatar.ElementSphere.Earth.ImpactRevert", 15000),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Cooldown", 500),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Range", 40),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Uses", 20),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Damage", 3.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.BurnDuration", 3000),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Speed", 3),
				new Pair<>("Abilities.Avatar.ElementSphere.Fire.Controllable", false),
				new Pair<>("Abilities.Avatar.ElementSphere.Water.Cooldown", 500),
				new Pair<>("Abilities.Avatar.ElementSphere.Water.Range", 40),
				new Pair<>("Abilities.Avatar.ElementSphere.Water.Uses", 20),
				new Pair<>("Abilities.Avatar.ElementSphere.Water.Damage", 3.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Water.Speed", 3),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.Cooldown", 500),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.Range", 40),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.Knockback", 2.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.Damage", 12.0),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.RequiredUses", 10),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.EndAbility", true),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.ImpactCraterSize", 3),
				new Pair<>("Abilities.Avatar.ElementSphere.Stream.ImpactRevert", 30000)
		);

// SpiritBeam Ability
		createAbilityConfig("SpiritBeam",
				new Pair<>("Abilities.Avatar.SpiritBeam.Enabled", true),
				new Pair<>("Abilities.Avatar.SpiritBeam.Description", "An energybending ability usable by the Avatar. "
						+ "To use, one must enter the AvatarState and hold down Sneak (Default: Shift). "
						+ "This ability lasts only for a few seconds before requiring "
						+ "another activation."),
				new Pair<>("Abilities.Avatar.SpiritBeam.Cooldown", 15000),
				new Pair<>("Abilities.Avatar.SpiritBeam.Duration", 1000),
				new Pair<>("Abilities.Avatar.SpiritBeam.Range", 40),
				new Pair<>("Abilities.Avatar.SpiritBeam.Damage", 10.0),
				new Pair<>("Abilities.Avatar.SpiritBeam.AvatarStateOnly", true),
				new Pair<>("Abilities.Avatar.SpiritBeam.BlockDamage.Enabled", true),
				new Pair<>("Abilities.Avatar.SpiritBeam.BlockDamage.Radius", 3),
				new Pair<>("Abilities.Avatar.SpiritBeam.BlockDamage.Regen", 20000)
		);

// AirBlade Ability
		createAbilityConfig("AirBlade",
				new Pair<>("Abilities.Air.AirBlade.Enabled", true),
				new Pair<>("Abilities.Air.AirBlade.Description", "With this ability bound, Left-Click to shoot "
						+ "a strong blade of air at your targets doing some damage!"),
				new Pair<>("Abilities.Air.AirBlade.Cooldown", 3000),
				new Pair<>("Abilities.Air.AirBlade.Range", 30.0),
				new Pair<>("Abilities.Air.AirBlade.Damage", 4.0),
				new Pair<>("Abilities.Air.AirBlade.EntityCollisionRadius", 1.0),
				new Pair<>("Abilities.Air.AirBlade.AbilityCollisionRadius", 1.0),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlast.Enabled", true),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlast.RemoveFirst", true),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlast.RemoveSecond", true),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlastCharged.Enabled", true),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlastCharged.RemoveFirst", true),
				new Pair<>("Abilities.Air.AirBlade.Collisions.FireBlastCharged.RemoveSecond", false)
		);

// AirBreath Ability
		createAbilityConfig("AirBreath",
				new Pair<>("Abilities.Air.AirBreath.Enabled", true),
				new Pair<>("Abilities.Air.AirBreath.Description", "To use, hold Sneak (Default: Shift) to release "
						+ "a strong breath of wind knocking your opponents "
						+ "back. This ability also has a longer range and "
						+ "stronger knockback while in AvatarState!"),
				new Pair<>("Abilities.Air.AirBreath.Cooldown", 3000),
				new Pair<>("Abilities.Air.AirBreath.Duration", 3000),
				new Pair<>("Abilities.Air.AirBreath.Particles", 3),
				new Pair<>("Abilities.Air.AirBreath.AffectBlocks.Lava", true),
				new Pair<>("Abilities.Air.AirBreath.AffectBlocks.Fire", true),
				new Pair<>("Abilities.Air.AirBreath.ExtinguishEntities", true),
				new Pair<>("Abilities.Air.AirBreath.Damage.Enabled", false),
				new Pair<>("Abilities.Air.AirBreath.Damage.Player", 1.0),
				new Pair<>("Abilities.Air.AirBreath.Damage.Mob", 2.0),
				new Pair<>("Abilities.Air.AirBreath.Knockback", 0.8),
				new Pair<>("Abilities.Air.AirBreath.Range", 10),
				new Pair<>("Abilities.Air.AirBreath.LaunchPower", 1.0),
				new Pair<>("Abilities.Air.AirBreath.RegenTargetOxygen", true),
				new Pair<>("Abilities.Air.AirBreath.Avatar.Enabled", true),
				new Pair<>("Abilities.Air.AirBreath.Avatar.Range", 20),
				new Pair<>("Abilities.Air.AirBreath.Avatar.Knockback", 3.5)
		);


		// AirGlide Ability
		createAbilityConfig("AirGlide",
				new Pair<>("Abilities.Air.AirGlide.Enabled", true),
				new Pair<>("Abilities.Air.AirGlide.Description", "While falling, tap Sneak for a "
						+ "slow and steady descent, tap Sneak again to stop gliding."),
				new Pair<>("Abilities.Air.AirGlide.Speed", 0.5),
				new Pair<>("Abilities.Air.AirGlide.FallSpeed", 0.1),
				new Pair<>("Abilities.Air.AirGlide.Particles", 4),
				new Pair<>("Abilities.Air.AirGlide.AllowAirSpout", false),
				new Pair<>("Abilities.Air.AirGlide.Cooldown", 0),
				new Pair<>("Abilities.Air.AirGlide.Duration", 0),
				new Pair<>("Abilities.Air.AirGlide.RequireGround", false)
		);

// AirPunch Ability
		createAbilityConfig("AirPunch",
				new Pair<>("Abilities.Air.AirPunch.Enabled", true),
				new Pair<>("Abilities.Air.AirPunch.Description", "Left-Click in rapid succession to punch high density packets of air "
						+ "at enemies to do slight damage to them. A few punches can be thrown before the ability has a cooldown."),
				new Pair<>("Abilities.Air.AirPunch.Cooldown", 5000),
				new Pair<>("Abilities.Air.AirPunch.Threshold", 500),
				new Pair<>("Abilities.Air.AirPunch.Shots", 4),
				new Pair<>("Abilities.Air.AirPunch.Range", 30),
				new Pair<>("Abilities.Air.AirPunch.Damage", 1.0),
				new Pair<>("Abilities.Air.AirPunch.EntityCollisionRadius", 1.0),
				new Pair<>("Abilities.Air.AirPunch.AbilityCollisionRadius", 1.0),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlast.Enabled", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlast.RemoveFirst", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlast.RemoveSecond", false),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlastCharged.Enabled", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlastCharged.RemoveFirst", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.FireBlastCharged.RemoveSecond", false),
				new Pair<>("Abilities.Air.AirPunch.Collisions.AirBlade.Enabled", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.AirBlade.RemoveFirst", true),
				new Pair<>("Abilities.Air.AirPunch.Collisions.AirBlade.RemoveSecond", false)
		);

// Meditate Ability
		createAbilityConfig("Meditate",
				new Pair<>("Abilities.Air.Meditate.Enabled", true),
				new Pair<>("Abilities.Air.Meditate.Description", "Hold Sneak (Default: Shift) to start meditating. "
						+ "After you have focused your energy, you will obtain several buffs."),
				new Pair<>("Abilities.Air.Meditate.UnfocusMessage", "You have become unfocused from taking damage!"),
				new Pair<>("Abilities.Air.Meditate.LossFocusMessage", true),
				new Pair<>("Abilities.Air.Meditate.ChargeTime", 5000),
				new Pair<>("Abilities.Air.Meditate.Cooldown", 60000),
				new Pair<>("Abilities.Air.Meditate.BoostDuration", 20000),
				new Pair<>("Abilities.Air.Meditate.ParticleDensity", 5),
				new Pair<>("Abilities.Air.Meditate.AbsorptionBoost", 2),
				new Pair<>("Abilities.Air.Meditate.SpeedBoost", 3),
				new Pair<>("Abilities.Air.Meditate.JumpBoost", 3)
		);


		// SonicBlast Ability
		createAbilityConfig("SonicBlast",
				new Pair<>("Abilities.Air.SonicBlast.Enabled", true),
				new Pair<>("Abilities.Air.SonicBlast.Description", "SonicBlast is a soundbending ability, known by very few airbenders. "
						+ "It allows the airbender to stun and deafen an opponent by creating a sonic blast, "
						+ "this is achieved by creating two regions of high and low pressure and bringing them together. "
						+ "To use, hold Sneak (Default: Shift) in the direction of the target. Once particles start appearing "
						+ "around you, let go of Sneak to shoot a SonicBlast at your target! The technique is very powerful, "
						+ "even if it doesn't seem it, and comes with a short cooldown."),
				new Pair<>("Abilities.Air.SonicBlast.ChargeTime", 2000),
				new Pair<>("Abilities.Air.SonicBlast.Damage", 4.0),
				new Pair<>("Abilities.Air.SonicBlast.Effects.BlindnessDuration", 5000),
				new Pair<>("Abilities.Air.SonicBlast.Effects.NauseaDuration", 5000),
				new Pair<>("Abilities.Air.SonicBlast.Cooldown", 6000),
				new Pair<>("Abilities.Air.SonicBlast.EntityCollisionRadius", 1.3),
				new Pair<>("Abilities.Air.SonicBlast.AbilityCollisionRadius", 1.3),
				new Pair<>("Abilities.Air.SonicBlast.Range", 20),
				new Pair<>("Abilities.Air.SonicBlast.ChargeSwapping", true)
		);

// AirSlam Ability
		createAbilityConfig("AirSlam",
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Enabled", true),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Description", "Kick your enemy up into the air then blast them away!"),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Cooldown", 8000),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Power", 5.0),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Range", 8),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Combination", Arrays.asList("AirSwipe:SHIFT_DOWN", "AirBlast:SHIFT_UP", "AirBlast:SHIFT_DOWN")),
				new Pair<>("Abilities.Air.AirCombo.AirSlam.Instructions", "AirSwipe (Hold sneak) > AirBlast (Release sneak) > AirBlast (Hold sneak)")
		);

// SwiftStream Ability
		createAbilityConfig("SwiftStream",
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Enabled", true),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Description", "Create a stream of air as you fly which causes nearby "
						+ "entities to be thrown in your direction."),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.DragFactor", 1.5),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Duration", 2000),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Cooldown", 6000),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Combination", Arrays.asList("Flight:SHIFT_DOWN", "Flight:SHIFT_UP", "Flight:SHIFT_DOWN", "Flight:SHIFT_UP")),
				new Pair<>("Abilities.Air.AirCombo.SwiftStream.Instructions", "Flight (Double tap sneak)")
		);

// EarthArmor Ability
		createAbilityConfig("EarthArmor",
				new Pair<>("Abilities.Earth.EarthArmor.Enabled", true),
				new Pair<>("Abilities.Earth.EarthArmor.Description", "If the block is metal, then you will get metal armor!"),
				new Pair<>("Abilities.Earth.EarthArmor.Resistance.Strength", 2),
				new Pair<>("Abilities.Earth.EarthArmor.Resistance.Duration", 4000),
				new Pair<>("Abilities.Earth.EarthArmor.UseIronArmor", false)
		);

// EarthKick Ability
		createAbilityConfig("EarthKick",
				new Pair<>("Abilities.Earth.EarthKick.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Description", "This move enables an earthbender to create a "
						+ "large earthen cover, ideal for defense. "
						+ "To use, Sneak (Default: Shift) at an earth "
						+ "source and it will raise and launch towards "
						+ "your foe!"),
				new Pair<>("Abilities.Earth.EarthKick.Cooldown", 2000),
				new Pair<>("Abilities.Earth.EarthKick.EarthBlocks", 10),
				new Pair<>("Abilities.Earth.EarthKick.Damage", 2.0),
				new Pair<>("Abilities.Earth.EarthKick.EntityCollisionRadius", 1.5),
				new Pair<>("Abilities.Earth.EarthKick.AbilityCollisionRadius", 1.5),
				new Pair<>("Abilities.Earth.EarthKick.MultipleHits", true),
				new Pair<>("Abilities.Earth.EarthKick.SourceRange", 2.0),
				new Pair<>("Abilities.Earth.EarthKick.Spread", 20),
				new Pair<>("Abilities.Earth.EarthKick.Velocity", 0.7),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.FireBlast.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.FireBlast.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.FireBlast.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.EarthBlast.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.EarthBlast.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.EarthBlast.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterManipulation.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterManipulation.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterManipulation.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSwipe.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSwipe.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSwipe.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.Combustion.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.Combustion.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.Combustion.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterSpout.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterSpout.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.WaterSpout.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSpout.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSpout.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirSpout.RemoveSecond", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirWheel.Enabled", true),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirWheel.RemoveFirst", false),
				new Pair<>("Abilities.Earth.EarthKick.Collisions.AirWheel.RemoveSecond", true)
		);

// EarthLine Ability
		createAbilityConfig("EarthLine",
				new Pair<>("Abilities.Earth.EarthLine.Enabled", true),
				new Pair<>("Abilities.Earth.EarthLine.Description", "To use, place your cursor over an earth-bendable block on the ground, "
						+ "then Sneak (Default: Shift) to select the block. After selecting the block you may release Sneak. "
						+ "If you then Left-Click at an object or player, a small piece of earth will come up "
						+ "from the ground and move towards your target to deal damage and knock them back. "
						+ "Additionally, hold Sneak to control the flow of the line!"),
				new Pair<>("Abilities.Earth.EarthLine.Cooldown", 0),
				new Pair<>("Abilities.Earth.EarthLine.PrepareCooldown", 3000),
				new Pair<>("Abilities.Earth.EarthLine.Range", 30),
				new Pair<>("Abilities.Earth.EarthLine.PrepareRange", 3),
				new Pair<>("Abilities.Earth.EarthLine.SourceKeepRange", 7),
				new Pair<>("Abilities.Earth.EarthLine.AffectingRadius", 2),
				new Pair<>("Abilities.Earth.EarthLine.AllowChangeDirection", true),
				new Pair<>("Abilities.Earth.EarthLine.MaxDuration", 2500),
				new Pair<>("Abilities.Earth.EarthLine.Damage", 3.0),
				new Pair<>("Abilities.Earth.EarthLine.RemovalPolicy.SwappedSlots.Enabled", false)
		);

// EarthPillar Ability
		createAbilityConfig("EarthPillar",
				new Pair<>("Abilities.Earth.EarthPillar.Enabled", true),
				new Pair<>("Abilities.Earth.EarthPillar.Description", "With this ability bound, tap Sneak (Default: Shift) on any Earthbendable "
						+ "surface to create a pillar of earth in the direction of the block face!"),
				new Pair<>("Abilities.Earth.EarthPillar.Height", 6),
				new Pair<>("Abilities.Earth.EarthPillar.Range", 10)
		);

// EarthShard Ability
		createAbilityConfig("EarthShard",
				new Pair<>("Abilities.Earth.EarthShard.Enabled", true),
				new Pair<>("Abilities.Earth.EarthShard.Description", "EarthShard is a variation of EarthBlast "
						+ "which the earthbender may use to hit a target. This "
						+ "ability deals a fair amount of damage and is easy to "
						+ "rapid-fire. To use, simply shift at an earthbendable block, "
						+ "and it will ascend to your eye height. Then, click towards your "
						+ "target and the block will launch itself towards it."),
				new Pair<>("Abilities.Earth.EarthShard.Cooldown", 1000),
				new Pair<>("Abilities.Earth.EarthShard.Damage.Normal", 1.0),
				new Pair<>("Abilities.Earth.EarthShard.Damage.Metal", 1.5),
				new Pair<>("Abilities.Earth.EarthShard.PrepareRange", 5),
				new Pair<>("Abilities.Earth.EarthShard.AbilityRange", 30),
				new Pair<>("Abilities.Earth.EarthShard.MaxShards", 3),
				new Pair<>("Abilities.Earth.EarthShard.AbilityCollisionRadius", 2.0),
				new Pair<>("Abilities.Earth.EarthShard.EntityCollisionRadius", 1.4)
		);

// EarthSurf Ability
		createAbilityConfig("EarthSurf",
				new Pair<>("Abilities.Earth.EarthSurf.Enabled", true),
				new Pair<>("Abilities.Earth.EarthSurf.Description", "This ability allows an earth bender to "
						+ "ride up on a wave of earth, allowing them to travel a little faster than "
						+ "normal. To use, simply be in the air just above "
						+ "the ground, and Left Click! Additionally, if an entity just so happens to get caught in "
						+ "the wave, they will be moved with the wave."),
				new Pair<>("Abilities.Earth.EarthSurf.Cooldown.Cooldown", 3000),
				new Pair<>("Abilities.Earth.EarthSurf.Cooldown.MinimumCooldown", 2000),
				new Pair<>("Abilities.Earth.EarthSurf.Cooldown.Scaled", true),
				new Pair<>("Abilities.Earth.EarthSurf.Cooldown.Enabled", false),
				new Pair<>("Abilities.Earth.EarthSurf.Duration.Duration", 7000),
				new Pair<>("Abilities.Earth.EarthSurf.Duration.Enabled", false),
				new Pair<>("Abilities.Earth.EarthSurf.RelaxedCollisions", true),
				new Pair<>("Abilities.Earth.EarthSurf.RemoveOnAnyDamage", false),
				new Pair<>("Abilities.Earth.EarthSurf.Speed", 0.55),
				new Pair<>("Abilities.Earth.EarthSurf.HeightTolerance", 3),
				new Pair<>("Abilities.Earth.EarthSurf.SpringStiffness", 0.35)
		);

// Fissure Ability
		createAbilityConfig("Fissure",
				new Pair<>("Abilities.Earth.Fissure.Enabled", true),
				new Pair<>("Abilities.Earth.Fissure.Description", "Fissure is an advanced Lavabending "
						+ "ability enabling a lavabender to tear up the ground, "
						+ "swallowing up any enemies. To use, simply swing at an enemy "
						+ "and a line of lava will crack open. "
						+ "Then, tap Sneak (Default: Shift) to expand the crevice. "
						+ "The crevice has a maximum width and depth. Once the crevice has reached its maximum "
						+ "width, Sneak while looking at the crevice to close it!"),
				new Pair<>("Abilities.Earth.Fissure.Cooldown", 20000),
				new Pair<>("Abilities.Earth.Fissure.Duration", 15000),
				new Pair<>("Abilities.Earth.Fissure.MaxWidth", 3),
				new Pair<>("Abilities.Earth.Fissure.SlapRange", 12),
				new Pair<>("Abilities.Earth.Fissure.SlapDelay", 50)
		);

// LavaDisc Ability
		createAbilityConfig("LavaDisc",
				new Pair<>("Abilities.Earth.LavaDisc.Enabled", true),
				new Pair<>("Abilities.Earth.LavaDisc.Description", "Hold Sneak (Default: Shift) on a lava source "
						+ "block to generate a disc of lava at your finger tips. Releasing "
						+ "Sneak will shoot the disc off in the direction "
						+ "you are looking! If you tap or hold Sneak again, "
						+ "the disc will attempt to return to you!"),
				new Pair<>("Abilities.Earth.LavaDisc.Cooldown", 7000),
				new Pair<>("Abilities.Earth.LavaDisc.Duration", 1000),
				new Pair<>("Abilities.Earth.LavaDisc.Damage", 4.0),
				new Pair<>("Abilities.Earth.LavaDisc.Particles", 3),
				new Pair<>("Abilities.Earth.LavaDisc.ContinueAfterEntityHit", false),
				new Pair<>("Abilities.Earth.LavaDisc.RecallLimit", 3),
				new Pair<>("Abilities.Earth.LavaDisc.Destroy.RegenTime", 5000),
				new Pair<>("Abilities.Earth.LavaDisc.Destroy.BlockDamage", true),
				new Pair<>("Abilities.Earth.LavaDisc.Destroy.AdditionalMeltableBlocks", new String[] {
						Material.COBBLESTONE.name(), Material.OAK_LOG.name(), Material.SPRUCE_LOG.name(),
						Material.JUNGLE_LOG.name(), Material.DARK_OAK_LOG.name(), Material.BIRCH_LOG.name(),
						Material.ACACIA_LOG.name()
				}),
				new Pair<>("Abilities.Earth.LavaDisc.Destroy.LavaTrail", true),
				new Pair<>("Abilities.Earth.LavaDisc.Destroy.TrailFlow", false),
				new Pair<>("Abilities.Earth.LavaDisc.Source.RegenTime", 10000),
				new Pair<>("Abilities.Earth.LavaDisc.Source.LavaOnly", false),
				new Pair<>("Abilities.Earth.LavaDisc.Source.Range", 4.0),
				new Pair<>("Abilities.Earth.LavaDisc.RemovalPolicy.SwappedSlots.Enabled", true)
		);

// LavaFlux Ability
		createAbilityConfig("LavaFlux",
				new Pair<>("Abilities.Earth.LavaFlux.Enabled", true),
				new Pair<>("Abilities.Earth.LavaFlux.Description", "This offensive ability enables a Lavabender to create a wave of lava, "
						+ "swiftly progressing forward and hurting/burning anything in its way. To use, "
						+ "simply swing your arm towards a target and the ability will activate."),
				new Pair<>("Abilities.Earth.LavaFlux.Range", 12),
				new Pair<>("Abilities.Earth.LavaFlux.Cooldown", 8000),
				new Pair<>("Abilities.Earth.LavaFlux.Duration", 4000),
				new Pair<>("Abilities.Earth.LavaFlux.Cleanup", 1000),
				new Pair<>("Abilities.Earth.LavaFlux.Damage", 1.0),
				new Pair<>("Abilities.Earth.LavaFlux.Speed", 1),
				new Pair<>("Abilities.Earth.LavaFlux.Wave", true),
				new Pair<>("Abilities.Earth.LavaFlux.KnockUp", 1.0),
				new Pair<>("Abilities.Earth.LavaFlux.KnockBack", 1.0)
		);

// LavaThrow Ability
		createAbilityConfig("LavaThrow",
				new Pair<>("Abilities.Earth.LavaThrow.Enabled", true),
				new Pair<>("Abilities.Earth.LavaThrow.Description", "Throwing lava is a fundamental technique for the rare subskill. "
						+ "Use Sneak (Default: Shift) while looking at a pool of lava in front of you, then "
						+ "Left-Click to splash the lava at your target. "
						+ "It can be used in rapid succession to create multiple streams of lava!"),
				new Pair<>("Abilities.Earth.LavaThrow.Cooldown", 7000),
				new Pair<>("Abilities.Earth.LavaThrow.MaxShots", 6),
				new Pair<>("Abilities.Earth.LavaThrow.Range", 20),
				new Pair<>("Abilities.Earth.LavaThrow.Damage", 1.0),
				new Pair<>("Abilities.Earth.LavaThrow.SourceGrabRange", 4),
				new Pair<>("Abilities.Earth.LavaThrow.SourceRegenDelay", 10000),
				new Pair<>("Abilities.Earth.LavaThrow.FireTicks", 80)
		);

// MagnetShield Ability
		createAbilityConfig("MagnetShield",
				new Pair<>("Abilities.Earth.MagnetShield.Enabled", true),
				new Pair<>("Abilities.Earth.MagnetShield.Description", "Repel any metal projectiles using a strong magnetic shield. "
						+ "To activate, simply hold sneak with this ability bound."),
				new Pair<>("Abilities.Earth.MagnetShield.Materials", Arrays.asList(
						"IRON_INGOT", "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS",
						"IRON_BOOTS", "IRON_BLOCK", "IRON_AXE", "IRON_PICKAXE",
						"IRON_SWORD", "IRON_HOE", "IRON_SHOVEL", "IRON_DOOR",
						"IRON_NUGGET", "IRON_BARS", "IRON_HORSE_ARMOR", "IRON_TRAPDOOR",
						"HEAVY_WEIGHTED_PRESSURE_PLATE", "GOLD_INGOT", "GOLDEN_HELMET",
						"GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
						"GOLD_BLOCK", "GOLD_NUGGET", "GOLDEN_AXE", "GOLDEN_PICKAXE",
						"GOLDEN_SHOVEL", "GOLDEN_SWORD", "GOLDEN_HOE", "GOLDEN_HORSE_ARMOR",
						"LIGHT_WEIGHTED_PRESSURE_PLATE", "CLOCK", "COMPASS",
						"RAW_GOLD_BLOCK", "RAW_IRON_BLOCK", "RAW_IRON", "RAW_GOLD",
						"ANVIL", "CHIPPED_ANVIL", "DAMAGED_ANVIL", "IRON_ORE",
						"GOLD_ORE", "DEEPSLATE_IRON_ORE", "DEEPSLATE_GOLD_ORE", "SHIELD"
				)),
				new Pair<>("Abilities.Earth.MagnetShield.Duration", 6000),
				new Pair<>("Abilities.Earth.MagnetShield.Cooldowns.Shift", 5000),
				new Pair<>("Abilities.Earth.MagnetShield.Cooldowns.Click", 5000),
				new Pair<>("Abilities.Earth.MagnetShield.Range", 5.0),
				new Pair<>("Abilities.Earth.MagnetShield.RepelArrows", true),
				new Pair<>("Abilities.Earth.MagnetShield.RepelLivingEntities", true),
				new Pair<>("Abilities.Earth.MagnetShield.Velocity", 0.1)
		);

// MetalFragments Ability
		createAbilityConfig("MetalFragments",
				new Pair<>("Abilities.Earth.MetalFragments.Enabled", true),
				new Pair<>("Abilities.Earth.MetalFragments.Description", "MetalFragments allows you to select a source and shoot "
						+ "multiple fragments of metal out of that source "
						+ "block towards your target, injuring them on impact. "
						+ "To use, tap Sneak (Default: Shift) at a metal "
						+ "source block and it will float up. Then, turn around "
						+ "and click at your target to fling metal fragments at them."),
				new Pair<>("Abilities.Earth.MetalFragments.Cooldown", 5000),
				new Pair<>("Abilities.Earth.MetalFragments.MaxSources", 3),
				new Pair<>("Abilities.Earth.MetalFragments.SourceRange", 5),
				new Pair<>("Abilities.Earth.MetalFragments.MaxFragments", 10),
				new Pair<>("Abilities.Earth.MetalFragments.Damage", 4.0),
				new Pair<>("Abilities.Earth.MetalFragments.Velocity", 2.0)
		);

// MetalHook Ability
		createAbilityConfig("MetalHook",
				new Pair<>("Abilities.Earth.MetalHook.Enabled", true),
				new Pair<>("Abilities.Earth.MetalHook.Description", "This ability lets a Metalbender bend metal into "
						+ "grappling hooks, allowing them to easily maneuver terrain. "
						+ "To use this ability, the user must either have Iron in their inventory "
						+ "or be wearing an Iron/Chainmail Chestplate. Left-Click in the direction "
						+ "you are looking to fire a grappling hook, several hooks can be active at once, "
						+ "allowing the bender to 'hang' in locations. To disengage the hooks, hold Shift (Default: Sneak) or Sprint."),
				new Pair<>("Abilities.Earth.MetalHook.Cooldown", 3000),
				new Pair<>("Abilities.Earth.MetalHook.Range", 30),
				new Pair<>("Abilities.Earth.MetalHook.MaxHooks", 3),
				new Pair<>("Abilities.Earth.MetalHook.TotalHooks", 0),
				new Pair<>("Abilities.Earth.MetalHook.RequireItems", true),
				new Pair<>("Abilities.Earth.MetalHook.BarrierHooking", true)
		);

// MetalShred Ability
		createAbilityConfig("MetalShred",
				new Pair<>("Abilities.Earth.MetalShred.Enabled", true),
				new Pair<>("Abilities.Earth.MetalShred.Description", "MetalShred allows you to tear a metal surface allowing you to sneak in to the other side."
						+ "To use, you must find a flat metal surface. Then, Sneak(Default: Shift) "
						+ "at a piece of metal on that surface, and two pieces of metal "
						+ "will be pulled toward you. Finally, run alongside the surface to coil "
						+ "the metal around those two pieces. The way will be open, and the blocks "
						+ "will not reset until you either select a new source or you switch "
						+ "abilities. If you click after having torn a hole in a vertical surface, "
						+ "you can Left-Click in any direction and the metal will unfold in that "
						+ "direction. If you are fast and precise enough, the metal can bend in "
						+ "any shape. The length of this sheet of metal depends on how much was "
						+ "coiled in the first place."),
				new Pair<>("Abilities.Earth.MetalShred.SourceRange", 5),
				new Pair<>("Abilities.Earth.MetalShred.ExtendTick", 80),
				new Pair<>("Abilities.Earth.MetalShred.Damage", 6.0)
		);

// MudSurge Ability
		createAbilityConfig("MudSurge",
				new Pair<>("Abilities.Earth.MudSurge.Enabled", true),
				new Pair<>("Abilities.Earth.MudSurge.Description", "This ability lets an earthbender send a surge of mud "
						+ "in any direction, knocking back enemies and "
						+ "dealing moderate damage. This ability has a chance "
						+ "of blinding the target. To use, select "
						+ "a source of earth and click in any direction."),
				new Pair<>("Abilities.Earth.MudSurge.Cooldown", 6000),
				new Pair<>("Abilities.Earth.MudSurge.Damage", 1.0),
				new Pair<>("Abilities.Earth.MudSurge.Waves", 5),
				new Pair<>("Abilities.Earth.MudSurge.SourceRange", 7),
				new Pair<>("Abilities.Earth.MudSurge.BlindChance", 10),
				new Pair<>("Abilities.Earth.MudSurge.WetSourceOnly", false),
				new Pair<>("Abilities.Earth.MudSurge.WaterSearchRadius", 5),
				new Pair<>("Abilities.Earth.MudSurge.BlindTicks", 60),
				new Pair<>("Abilities.Earth.MudSurge.CollisionRadius", 2.0),
				new Pair<>("Abilities.Earth.MudSurge.MultipleHits", true),
				new Pair<>("Abilities.Earth.MudSurge.AllowFallDamage", false),
				new Pair<>("Abilities.Earth.MudSurge.RemovalPolicy.SwappedSlots.Enabled", true),
				new Pair<>("Abilities.Earth.MudSurge.RemovalPolicy.OutOfRange.Enabled", true),
				new Pair<>("Abilities.Earth.MudSurge.RemovalPolicy.OutOfRange.Range", 25.0)
		);

// SandBlast Ability
		createAbilityConfig("SandBlast",
				new Pair<>("Abilities.Earth.SandBlast.Enabled", true),
				new Pair<>("Abilities.Earth.SandBlast.Description", "This ability lets an earthbender blast a bunch of sand at an enemy "
						+ "damaging them and temporarily blinding them! Just Sneak (Default: Shift) "
						+ "on a sand bendable block, then Left-Click in a direction to shoot a "
						+ "blast of sand!"),
				new Pair<>("Abilities.Earth.SandBlast.Cooldown", 3000),
				new Pair<>("Abilities.Earth.SandBlast.Damage", 3.0),
				new Pair<>("Abilities.Earth.SandBlast.SourceRange", 8),
				new Pair<>("Abilities.Earth.SandBlast.Range", 30),
				new Pair<>("Abilities.Earth.SandBlast.MaxSandBlocks", 10)
		);

// Crevice Ability
		createAbilityConfig("Crevice",
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Enabled", true),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Description", "Create a Crevice in the ground! Once opened, "
						+ "anyone can Tap Sneak with Shockwave to close the Crevice!"),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Range", 50),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.RevertDelay", 7500),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Depth", 5),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.AvatarStateDepth", 8),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Cooldown", 10000),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Combination", Arrays.asList("Collapse:RIGHT_CLICK_BLOCK", "Shockwave:SHIFT_DOWN", "Shockwave:SHIFT_UP", "Shockwave:SHIFT_DOWN")),
				new Pair<>("Abilities.Earth.EarthCombo.Crevice.Instructions", "Collapse (Right-click a block) > Shockwave (Tap sneak) > Shockwave (Tap sneak)")
		);

// MagmaBlast Ability
		createAbilityConfig("MagmaBlast",
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.Enabled", true),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.Description", "Fire balls of magma at your enemy!"),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.MaxShots", 3),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.ImpactDamage", 2.0),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.SearchRange", 4),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.Cooldown", 6000),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.ShotCooldown", 1500),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.RequireLavaFlow", false),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.PlayerCollisions", true),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.EntitySelection", true),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.SelectRange", 30.0),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.ExplosionRadius", 2.0),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.FireSpeed", 1.5),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.MaxDuration", 15000),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.MaxDistanceFromSources", 15),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.Combination", Arrays.asList("EarthBlast:SHIFT_DOWN", "LavaFlow:SHIFT_UP", "LavaFlow:SHIFT_DOWN", "LavaFlow:RIGHT_CLICK_BLOCK")),
				new Pair<>("Abilities.Earth.EarthCombo.MagmaBlast.Instructions", "EarthBlast (Hold sneak) > LavaFlow (Release sneak) > LavaFlow (Hold sneak) > LavaFlow (Right-click a block) > LavaFlow (Left-click multiple times)")
		);

// Combustion Ability
		createAbilityConfig("Combustion",
				new Pair<>("Abilities.Fire.Combustion.Enabled", true),
				new Pair<>("Abilities.Fire.Combustion.Description", "Hold Shift to focus large amounts of energy into your body, "
						+ "Release Shift to fire Combustion. Move your mouse to "
						+ "direct where the beam travels. Left-Click to detonate "
						+ "the beam manually"),
				new Pair<>("Abilities.Fire.Combustion.Damage", 4.0),
				new Pair<>("Abilities.Fire.Combustion.FireTick", 100),
				new Pair<>("Abilities.Fire.Combustion.MisfireModifier", -1),
				new Pair<>("Abilities.Fire.Combustion.Power", 3),
				new Pair<>("Abilities.Fire.Combustion.Range", 100),
				new Pair<>("Abilities.Fire.Combustion.Speed", 0.65),
				new Pair<>("Abilities.Fire.Combustion.Warmup", 1500),
				new Pair<>("Abilities.Fire.Combustion.Cooldown", 5000),
				new Pair<>("Abilities.Fire.Combustion.RegenTime", 10000),
				new Pair<>("Abilities.Fire.Combustion.EntityCollisionRadius", 1.3),
				new Pair<>("Abilities.Fire.Combustion.AbilityCollisionRadius", 1.3),
				new Pair<>("Abilities.Fire.Combustion.DamageBlocks", true),
				new Pair<>("Abilities.Fire.Combustion.RegenBlocks", true),
				new Pair<>("Abilities.Fire.Combustion.WaitForRegen", true),
				new Pair<>("Abilities.Fire.Combustion.InstantExplodeIfHit", true),
				new Pair<>("Abilities.Fire.Combustion.ExplodeOnDeath", true),
				new Pair<>("Abilities.Fire.Combustion.RemovalPolicy.SwappedSlots.Enabled", false)
		);

// Discharge Ability
		createAbilityConfig("Discharge",
				new Pair<>("Abilities.Fire.Discharge.Enabled", true),
				new Pair<>("Abilities.Fire.Discharge.Description", "Left-Click to shoot bolts of electricity out "
						+ "of your fingertips zapping what ever it hits!"),
				new Pair<>("Abilities.Fire.Discharge.Damage", 3.0),
				new Pair<>("Abilities.Fire.Discharge.Cooldown", 5000),
				new Pair<>("Abilities.Fire.Discharge.AvatarCooldown", 500),
				new Pair<>("Abilities.Fire.Discharge.Duration", 1000),
				new Pair<>("Abilities.Fire.Discharge.SlotSwapping", false),
				new Pair<>("Abilities.Fire.Discharge.EntityCollisionRadius", 1.0),
				new Pair<>("Abilities.Fire.Discharge.AbilityCollisionRadius", 1.0)
		);

// FireBall Ability
		createAbilityConfig("FireBall",
				new Pair<>("Abilities.Fire.FireBall.Enabled", true),
				new Pair<>("Abilities.Fire.FireBall.Description", "To use, simply Left-Click to shoot a fireball at your target!"),
				new Pair<>("Abilities.Fire.FireBall.Cooldown", 3000),
				new Pair<>("Abilities.Fire.FireBall.Range", 50),
				new Pair<>("Abilities.Fire.FireBall.Damage", 3.0),
				new Pair<>("Abilities.Fire.FireBall.FireDuration", 2000),
				new Pair<>("Abilities.Fire.FireBall.Controllable", false),
				new Pair<>("Abilities.Fire.FireBall.FireTrail", true),
				new Pair<>("Abilities.Fire.FireBall.CollisionRadius", 1.1),
				new Pair<>("Abilities.Fire.FireBall.Collisions.FireShield.Enabled", true),
				new Pair<>("Abilities.Fire.FireBall.Collisions.FireShield.RemoveFirst", true),
				new Pair<>("Abilities.Fire.FireBall.Collisions.FireShield.RemoveSecond", false),
				new Pair<>("Abilities.Fire.FireBall.Collisions.AirShield.Enabled", true),
				new Pair<>("Abilities.Fire.FireBall.Collisions.AirShield.RemoveFirst", false),
				new Pair<>("Abilities.Fire.FireBall.Collisions.AirShield.RemoveSecond", false),
				new Pair<>("Abilities.Fire.FireBall.Collisions.AirShield.Reflect", true)
		);

// FireBreath Ability
		createAbilityConfig("FireBreath",
				new Pair<>("Abilities.Fire.FireBreath.Enabled", true),
				new Pair<>("Abilities.Fire.FireBreath.Description", "To use, hold Sneak (Default: Shift) to start breathing "
						+ "fire! Some Firebenders possess the power to infuse color "
						+ "when they breathe, it's unclear how they do this, but some suggest "
						+ "it can be obtained by saying \"Bring fire and light together as one and allow the breath of color\" "
						+ "and can be brought back to normal by saying \"Split the bond of fire "
						+ "and light and set the color free\"."),
				new Pair<>("Abilities.Fire.FireBreath.Cooldown", 5000),
				new Pair<>("Abilities.Fire.FireBreath.Duration", 3000),
				new Pair<>("Abilities.Fire.FireBreath.Particles", 3),
				new Pair<>("Abilities.Fire.FireBreath.Damage.Player", 1.0),
				new Pair<>("Abilities.Fire.FireBreath.Damage.Mob", 2.0),
				new Pair<>("Abilities.Fire.FireBreath.FireDuration", 3000),
				new Pair<>("Abilities.Fire.FireBreath.Range", 10),
				new Pair<>("Abilities.Fire.FireBreath.Avatar.FireEnabled", true),
				new Pair<>("Abilities.Fire.FireBreath.Melt.Enabled", true),
				new Pair<>("Abilities.Fire.FireBreath.Melt.Chance", 3),
				new Pair<>("Abilities.Fire.FireBreath.RainbowBreath.Enabled", true),
				new Pair<>("Abilities.Fire.FireBreath.RainbowBreath.EnabledMessage", "You have bonded fire with light and can now breathe pure color."),
				new Pair<>("Abilities.Fire.FireBreath.RainbowBreath.DisabledMessage", "You have split your bond of color and light."),
				new Pair<>("Abilities.Fire.FireBreath.RainbowBreath.NoAccess", "You don't possess the power to bond light with fire.")
		);

// FireComet Ability
		createAbilityConfig("FireComet",
				new Pair<>("Abilities.Fire.FireComet.Enabled", true),
				new Pair<>("Abilities.Fire.FireComet.Description", "Harnessing the power of Sozin's Comet, a firebender can create a great "
						+ "ball of fire, with much destructive power. Only useable during Sozin's Comet or while in the AvatarState, hold Sneak (Default: Shift) "
						+ "to start charging the ability up. Once the ability is charged, a large mass of particles will follow your cursor, until you release sneak, "
						+ "launching the great ball of fire in the direction you are looking."),
				new Pair<>("Abilities.Fire.FireComet.Cooldown", 45000),
				new Pair<>("Abilities.Fire.FireComet.ChargeUp", 7000),
				new Pair<>("Abilities.Fire.FireComet.Damage", 6.0),
				new Pair<>("Abilities.Fire.FireComet.BlastRadius", 3.0),
				new Pair<>("Abilities.Fire.FireComet.SozinsComet.Cooldown", 30000),
				new Pair<>("Abilities.Fire.FireComet.SozinsComet.ChargeUp", 5000),
				new Pair<>("Abilities.Fire.FireComet.SozinsComet.Damage", 12.0),
				new Pair<>("Abilities.Fire.FireComet.SozinsComet.BlastRadius", 5.0),
				new Pair<>("Abilities.Fire.FireComet.Range", 50),
				new Pair<>("Abilities.Fire.FireComet.RegenDelay", 15000),
				new Pair<>("Abilities.Fire.FireComet.SozinsCometOnly", true),
				new Pair<>("Abilities.Fire.FireComet.AvatarStateBypassComet", true)
		);

// FirePunch Ability
		createAbilityConfig("FirePunch",
				new Pair<>("Abilities.Fire.FirePunch.Enabled", true),
				new Pair<>("Abilities.Fire.FirePunch.Description", "This basic ability allows a Firebender to channel their energies into a "
						+ "single punch, igniting and damaging the victim."),
				new Pair<>("Abilities.Fire.FirePunch.Cooldown", 4000),
				new Pair<>("Abilities.Fire.FirePunch.FireTicks", 2000),
				new Pair<>("Abilities.Fire.FirePunch.Damage", 2.0)
		);

// FireShots Ability
		createAbilityConfig("FireShots",
				new Pair<>("Abilities.Fire.FireShots.Enabled", true),
				new Pair<>("Abilities.Fire.FireShots.Description", "To use, tap Sneak (Default: Shift) to summon a "
						+ "FireBalls at your hand, then Left Click to shoot off each ball! "
						+ "Each shot will follow the cursor until it runs out or hits something! "
						+ "Tap Sneak again to switch your main hand."),
				new Pair<>("Abilities.Fire.FireShots.Cooldown", 3000),
				new Pair<>("Abilities.Fire.FireShots.Range", 50),
				new Pair<>("Abilities.Fire.FireShots.FireBalls", 4),
				new Pair<>("Abilities.Fire.FireShots.FireDuration", 3000),
				new Pair<>("Abilities.Fire.FireShots.Damage", 2.0),
				new Pair<>("Abilities.Fire.FireShots.CollisionRadius", 0.9),
				new Pair<>("Abilities.Fire.FireShots.Collisions.FireShield.Enabled", true),
				new Pair<>("Abilities.Fire.FireShots.Collisions.FireShield.RemoveFirst", true),
				new Pair<>("Abilities.Fire.FireShots.Collisions.FireShield.RemoveSecond", false),
				new Pair<>("Abilities.Fire.FireShots.Collisions.AirShield.Enabled", true),
				new Pair<>("Abilities.Fire.FireShots.Collisions.AirShield.RemoveFirst", false),
				new Pair<>("Abilities.Fire.FireShots.Collisions.AirShield.RemoveSecond", false),
				new Pair<>("Abilities.Fire.FireShots.Collisions.AirShield.Reflect", true)
		);

// FireSki Ability
		createAbilityConfig("FireSki",
				new Pair<>("Abilities.Fire.FireSki.Enabled", true),
				new Pair<>("Abilities.Fire.FireSki.Cooldown", 6000),
				new Pair<>("Abilities.Fire.FireSki.Duration", 6000),
				new Pair<>("Abilities.Fire.FireSki.Speed", 0.7),
				new Pair<>("Abilities.Fire.FireSki.IgniteEntities", true),
				new Pair<>("Abilities.Fire.FireSki.FireTicks", 60),
				new Pair<>("Abilities.Fire.FireSki.RequiredHeight", 0.7),
				new Pair<>("Abilities.Fire.FireSki.PunchActivated", false)
		);

// LightningBurst Ability
		createAbilityConfig("LightningBurst",
				new Pair<>("Abilities.Fire.LightningBurst.Enabled", true),
				new Pair<>("Abilities.Fire.LightningBurst.Description", "To use the most explosive lightning move available to a firebender, hold "
						+ "Sneak (Default: Shift) until blue sparks appear in front of you. Upon releasing, "
						+ "you will unleash an electrical sphere, shocking anyone who gets too close"),
				new Pair<>("Abilities.Fire.LightningBurst.Cooldown", 25000),
				new Pair<>("Abilities.Fire.LightningBurst.ChargeUp", 4000),
				new Pair<>("Abilities.Fire.LightningBurst.AvatarCooldown", 1000),
				new Pair<>("Abilities.Fire.LightningBurst.AvatarChargeUp", 1000),
				new Pair<>("Abilities.Fire.LightningBurst.Radius", 12),
				new Pair<>("Abilities.Fire.LightningBurst.Damage", 9.0)
		);

// Blood Ability
		createAbilityConfig("Blood",
				new Pair<>("Abilities.Water.Blood.Enabled", true),
				new Pair<>("Abilities.Water.Blood.Description", "This ability allows a skilled waterbender "
						+ "to bend the water within an enemy's blood, granting them full "
						+ "control over the enemy's limbs. This ability is extremely dangerous "
						+ "and is to be used carefully. To use, sneak while looking at an entity "
						+ "and its body will follow your movement. If you click, you will launch "
						+ "the entity towards whatever you were looking at when you clicked. The "
						+ "entity may collide with others, injuring them and the other one further."),
				new Pair<>("Abilities.Water.Blood.NightOnly", false),
				new Pair<>("Abilities.Water.Blood.FullMoonOnly", false),
				new Pair<>("Abilities.Water.Blood.UndeadMobs", true),
				new Pair<>("Abilities.Water.Blood.IgnoreWalls", false),
				new Pair<>("Abilities.Water.Blood.RequireBound", false),
				new Pair<>("Abilities.Water.Blood.Distance", 6),
				new Pair<>("Abilities.Water.Blood.HoldTime", 10000),
				new Pair<>("Abilities.Water.Blood.Cooldown", 4000)
		);

// BloodPuppet Ability
		createAbilityConfig("BloodPuppet",
				new Pair<>("Abilities.Water.BloodPuppet.Enabled", true),
				new Pair<>("Abilities.Water.BloodPuppet.Description", "This very high-level bloodbending ability lets "
						+ "a master control entities' limbs, forcing them to "
						+ "attack the master's target. To use this ability, you must "
						+ "be a bloodbender. Next, sneak while targeting "
						+ "a mob or player and you will start controlling them. To "
						+ "make the entity hit another, click. To release your "
						+ "target, stop sneaking. This ability has NO cooldown, but "
						+ "may only be usable during the night depending on the "
						+ "server configuration."),
				new Pair<>("Abilities.Water.BloodPuppet.NightOnly", false),
				new Pair<>("Abilities.Water.BloodPuppet.FullMoonOnly", false),
				new Pair<>("Abilities.Water.BloodPuppet.UndeadMobs", true),
				new Pair<>("Abilities.Water.BloodPuppet.IgnoreWalls", false),
				new Pair<>("Abilities.Water.BloodPuppet.RequireBound", false),
				new Pair<>("Abilities.Water.BloodPuppet.Distance", 6),
				new Pair<>("Abilities.Water.BloodPuppet.HoldTime", 10000),
				new Pair<>("Abilities.Water.BloodPuppet.Cooldown", 4000)
		);

// Drain Ability
		createAbilityConfig("Drain",
				new Pair<>("Abilities.Water.Drain.Enabled", true),
				new Pair<>("Abilities.Water.Drain.Description", "Inspired by how Hama drained water from the fire lilies, many benders "
						+ "have practiced in the skill of draining water from plants! With this ability bound, "
						+ "Sneak (Default: Shift) near/around plant sources to drain the water out of them to fill up any "
						+ "bottles/buckets in your inventory! Alternatively, if you have nothing to fill"
						+ " and blasts are enabled in the config, you will be able to create mini blasts "
						+ "of water to shoot at your targets! Alternatively, this ability can also be used to quickly fill up "
						+ "bottles from straight water sources or from falling rain!"),
				new Pair<>("Abilities.Water.Drain.RegenDelay", 15000),
				new Pair<>("Abilities.Water.Drain.Duration", 2000),
				new Pair<>("Abilities.Water.Drain.Cooldown", 2000),
				new Pair<>("Abilities.Water.Drain.AbsorbSpeed", 0.1),
				new Pair<>("Abilities.Water.Drain.AbsorbChance", 20),
				new Pair<>("Abilities.Water.Drain.AbsorbRate", 6),
				new Pair<>("Abilities.Water.Drain.Radius", 6),
				new Pair<>("Abilities.Water.Drain.HoldRange", 2),
				new Pair<>("Abilities.Water.Drain.AllowRainSource", true),
				new Pair<>("Abilities.Water.Drain.BlastsEnabled", true),
				new Pair<>("Abilities.Water.Drain.KeepSource", false),
				new Pair<>("Abilities.Water.Drain.BlastSpeed", 1),
				new Pair<>("Abilities.Water.Drain.BlastDamage", 1.5),
				new Pair<>("Abilities.Water.Drain.BlastRange", 20),
				new Pair<>("Abilities.Water.Drain.MaxBlasts", 4),
				new Pair<>("Abilities.Water.Drain.DrainTempBlocks", true)
		);

// FrostBreath Ability
		createAbilityConfig("FrostBreath",
				new Pair<>("Abilities.Water.FrostBreath.Enabled", true),
				new Pair<>("Abilities.Water.FrostBreath.Description", "As demonstrated by Katara, a Waterbender is able to freeze their breath, "
						+ "causing anything it touches to be frozen! With this ability bound, simply hold "
						+ "Sneak (Default: Shift) to start breathing frost!"),
				new Pair<>("Abilities.Water.FrostBreath.Cooldown", 15000),
				new Pair<>("Abilities.Water.FrostBreath.Duration", 3000),
				new Pair<>("Abilities.Water.FrostBreath.Particles", 3),
				new Pair<>("Abilities.Water.FrostBreath.FrostDuration", 5000),
				new Pair<>("Abilities.Water.FrostBreath.FrozenWaterDuration", 10000),
				new Pair<>("Abilities.Water.FrostBreath.Range", 10),
				new Pair<>("Abilities.Water.FrostBreath.Snow", true),
				new Pair<>("Abilities.Water.FrostBreath.SnowDuration", 5000),
				new Pair<>("Abilities.Water.FrostBreath.BendableSnow", false),
				new Pair<>("Abilities.Water.FrostBreath.Damage.Enabled", false),
				new Pair<>("Abilities.Water.FrostBreath.Damage.Player", 1.0),
				new Pair<>("Abilities.Water.FrostBreath.Damage.Mob", 2.0),
				new Pair<>("Abilities.Water.FrostBreath.Slow.Enabled", true),
				new Pair<>("Abilities.Water.FrostBreath.Slow.Duration", 4000),
				new Pair<>("Abilities.Water.FrostBreath.RestrictBiomes", true)
		);

// HealingWaters Ability
		createAbilityConfig("HealingWaters",
				new Pair<>("Abilities.Water.HealingWaters.Enabled", true),
				new Pair<>("Abilities.Water.HealingWaters.Description", "To use this ability, the bender has to be partially submerged "
						+ "in water, OR be holding either a bottle of water or a water bucket."
						+ " This move will heal the player automatically if they have it equipped "
						+ "and are standing in water. If the player sneaks while in water and is targeting"
						+ " another entity, the bender will heal the targeted entity. The alternate "
						+ "healing method requires the bender to be holding a bottle of water or a water"
						+ " bucket. To start healing simply sneak, however if the bender is targeting "
						+ "a mob while sneaking, the bender will heal the targeted mob."),
				new Pair<>("Abilities.Water.HealingWaters.Power", 1),
				new Pair<>("Abilities.Water.HealingWaters.Range", 5),
				new Pair<>("Abilities.Water.HealingWaters.DrainChance", 5),
				new Pair<>("Abilities.Water.HealingWaters.DynamicLight.Enabled", true),
				new Pair<>("Abilities.Water.HealingWaters.DynamicLight.Brightness", 10),
				new Pair<>("Abilities.Water.HealingWaters.DynamicLight.KeepAlive", 1500)
		);

// IceClaws Ability
		createAbilityConfig("IceClaws",
				new Pair<>("Abilities.Water.IceClaws.Enabled", true),
				new Pair<>("Abilities.Water.IceClaws.Description", "As demonstrated by Hama, a Waterbender can pull water out of thin air to create claws "
						+ "at the tips of their fingers. With IceClaws bound, hold Sneak (Default: Shift) to "
						+ "start pulling water out the air until you form claws at your finger "
						+ "tips, then attack an enemy to slow them down and do a bit of damage!"),
				new Pair<>("Abilities.Water.IceClaws.Cooldown", 6000),
				new Pair<>("Abilities.Water.IceClaws.ChargeTime", 1000),
				new Pair<>("Abilities.Water.IceClaws.SlowDuration", 5000),
				new Pair<>("Abilities.Water.IceClaws.Damage", 3.0),
				new Pair<>("Abilities.Water.IceClaws.Range", 10),
				new Pair<>("Abilities.Water.IceClaws.Throwable", true)
		);

// IceWall Ability
		createAbilityConfig("IceWall",
				new Pair<>("Abilities.Water.IceWall.Enabled", true),
				new Pair<>("Abilities.Water.IceWall.Description", "IceWall allows an icebender to create a wall of ice, similar to "
						+ "raiseearth. To use, simply sneak while targeting either water, ice, or snow. "
						+ "To break the wall, you must sneak again while targeting it. Be aware that "
						+ "other icebenders can break your own shields, and if you are too close you "
						+ "can get hurt by the shards."),
				new Pair<>("Abilities.Water.IceWall.Cooldown", 4000),
				new Pair<>("Abilities.Water.IceWall.Width", 6),
				new Pair<>("Abilities.Water.IceWall.MaxHeight", 5),
				new Pair<>("Abilities.Water.IceWall.MinHeight", 3),
				new Pair<>("Abilities.Water.IceWall.MaxWallHealth", 12),
				new Pair<>("Abilities.Water.IceWall.MinWallHealth", 8),
				new Pair<>("Abilities.Water.IceWall.Range", 8),
				new Pair<>("Abilities.Water.IceWall.Damage", 4.0),
				new Pair<>("Abilities.Water.IceWall.CanBreak", true),
				new Pair<>("Abilities.Water.IceWall.Stackable", false),
				new Pair<>("Abilities.Water.IceWall.LifeTime.Enabled", false),
				new Pair<>("Abilities.Water.IceWall.LifeTime.Duration", 10000),
				new Pair<>("Abilities.Water.IceWall.WallDamage", true),
				new Pair<>("Abilities.Water.IceWall.WallDamage.Torrent", 5),
				new Pair<>("Abilities.Water.IceWall.WallDamage.TorrentFreeze", 9),
				new Pair<>("Abilities.Water.IceWall.WallDamage.IceBlast", 8),
				new Pair<>("Abilities.Water.IceWall.WallDamage.Fireblast", 3),
				new Pair<>("Abilities.Water.IceWall.WallDamage.FireblastCharged", 5),
				new Pair<>("Abilities.Water.IceWall.WallDamage.Lightning", 12),
				new Pair<>("Abilities.Water.IceWall.WallDamage.Combustion", 12),
				new Pair<>("Abilities.Water.IceWall.WallDamage.EarthSmash", 8),
				new Pair<>("Abilities.Water.IceWall.WallDamage.AirBlast", 2)
		);

// WakeFishing Ability
		createAbilityConfig("WakeFishing",
				new Pair<>("Abilities.Water.WakeFishing.Enabled", true),
				new Pair<>("Abilities.Water.WakeFishing.Description", "With this ability bound, hold Sneak (Default: Shift) at a water block and "
						+ "don't lose focus of that block. Eventually, some fish will investigate "
						+ "the wake and swim out at you!"),
				new Pair<>("Abilities.Water.WakeFishing.Cooldown", 10000),
				new Pair<>("Abilities.Water.WakeFishing.Duration", 20000),
				new Pair<>("Abilities.Water.WakeFishing.Range", 5)
		);

// Maelstrom Ability
		createAbilityConfig("Maelstrom",
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Enabled", true),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Description", "Create a swirling mass of water that drags any entity that enters it to the bottom "
						+ "of the whirlpool."),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Cooldown", 25000),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Duration", 15000),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.MaxDepth", 5),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Range", 10),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Combination", Arrays.asList("PhaseChange:SHIFT_DOWN", "Torrent:LEFT_CLICK", "Torrent:LEFT_CLICK")),
				new Pair<>("Abilities.Water.WaterCombo.Maelstrom.Instructions", "PhaseChange (Hold sneak) > Torrent (Left-click) > Torrent (Left-click)")
		);

// WaterFlow Ability
		createAbilityConfig("WaterFlow",
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Enabled", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Description", "Some Waterbenders have managed to create torrents of water much stronger than a regular torrent, "
						+ "that can carry them selves and others, as well as being able to freeze the entire stream whenever. The bender must stay focused on the flow or else the flow will stop."
						+ " If you Sneak (Default: Shift) while controlling the stream, the stream will return to you."),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Cooldown", 8000),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Duration", 8000),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.MeltDelay", 5000),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.SourceRange", 10),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.MaxRange", 40),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.MinRange", 8),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Trail", 80),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.BottleSource", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.PlantSource", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.RemoveOnAnyDamage", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Size.Normal", 1),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Size.AvatarState", 3),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Size.FullmoonSmall", 2),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Size.FullmoonLarge", 3),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.IsAvatarStateToggle", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.AvatarStateDuration", 60000),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.PlayerStayNearSource", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.MaxDistanceFromSource", 100),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.FullMoon.Enabled", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.FullMoon.Modifier.Cooldown", 3),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.FullMoon.Modifier.Duration", 2),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.PlayerRideOwnFlow", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Combination", Arrays.asList("WaterManipulation:SHIFT_DOWN", "WaterManipulation:SHIFT_UP", "Torrent:SHIFT_DOWN", "Torrent:SHIFT_UP", "Torrent:SHIFT_DOWN", "WaterManipulation:SHIFT_UP")),
				new Pair<>("Abilities.Water.WaterCombo.WaterFlow.Instructions", "WaterManipulation (Tap sneak) > Torrent (Tap sneak) > Torrent (Hold sneak) > WaterManipulation (Release sneak)")
		);

// WaterGimbal Ability
		createAbilityConfig("WaterGimbal",
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Enabled", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Description", "Skilled Waterbenders are able to create two spinning rings of water around their bodies, "
						+ "which can be used as a defensive ability or for an offensive attack."),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Cooldown", 7000),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Damage", 3.0),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.RingSize", 3.5),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Range", 40),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.SourceRange", 10),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Speed", 2),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.AnimationSpeed", 3),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.PlantSource", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.SnowSource", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.RequireAdjacentPlants", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.BottleSource", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.AbilityCollisionRadius", 1.6),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.EntityCollisionRadius", 1.6),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Collisions.FireShield.Enabled", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Collisions.FireShield.RemoveFirst", true),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Collisions.FireShield.RemoveSecond", false),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Combination", Arrays.asList("Torrent:SHIFT_DOWN", "Torrent:SHIFT_UP", "Torrent:SHIFT_DOWN", "Torrent:SHIFT_UP", "WaterManipulation:SHIFT_DOWN")),
				new Pair<>("Abilities.Water.WaterCombo.WaterGimbal.Instructions", "Torrent (Tap sneak) > Torrent (Tap sneak) > WaterManipulation (Hold sneak) > WaterManipulation (Left-click multiple times)")
		);

// Skate Passive
		createAbilityConfig("Skate",
				new Pair<>("Abilities.Water.Ice.Passive.Skate.Enabled", true),
				new Pair<>("Abilities.Water.Ice.Passive.Skate.SpeedFactor", 4)
		);

// Backstab Ability
		createAbilityConfig("Backstab",
				new Pair<>("Abilities.Chi.Backstab.Enabled", true),
				new Pair<>("Abilities.Chi.Backstab.Description", "Strike your foe in the back with a hard jab, temporariliy blocking their Chi, and "
						+ "inflicting a lot of damage! This ability has a long cooldown. You must hit the target in the back or this ability won't work!"),
				new Pair<>("Abilities.Chi.Backstab.Cooldown", 8500),
				new Pair<>("Abilities.Chi.Backstab.Damage", 6.0),
				new Pair<>("Abilities.Chi.Backstab.MaxActivationAngle", 90)
		);

// DaggerThrow Ability
		createAbilityConfig("DaggerThrow",
				new Pair<>("Abilities.Chi.DaggerThrow.Enabled", true),
				new Pair<>("Abilities.Chi.DaggerThrow.Description", "With this ability bound, Left-Click in "
						+ "rapid succession to shoot arrows out of your inventory at your target!"),
				new Pair<>("Abilities.Chi.DaggerThrow.Cooldown", 3000),
				new Pair<>("Abilities.Chi.DaggerThrow.MaxDaggers.Enabled", true),
				new Pair<>("Abilities.Chi.DaggerThrow.MaxDaggers.Amount", 6),
				new Pair<>("Abilities.Chi.DaggerThrow.Damage", 1.0),
				new Pair<>("Abilities.Chi.DaggerThrow.ParticleTrail", true),
				new Pair<>("Abilities.Chi.DaggerThrow.AbilityCollisionRadius", 0.5),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.WaterSpout.Enabled", true),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.WaterSpout.Cooldown", 1000),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.WaterSpout.HitsRequired", 1),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.AirSpout.Enabled", true),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.AirSpout.Cooldown", 1000),
				new Pair<>("Abilities.Chi.DaggerThrow.Interactions.AirSpout.HitsRequired", 1)
		);

// WallRun Passive
		String[] invalidWallRun = {Material.BARRIER.name()};
		createAbilityConfig("WallRun",
				new Pair<>("Abilities.Passives.WallRun.Enabled", true),
				new Pair<>("Abilities.Passives.WallRun.Cooldown", 6000),
				new Pair<>("Abilities.Passives.WallRun.Duration", 20000),
				new Pair<>("Abilities.Passives.WallRun.Particles", true),
				new Pair<>("Abilities.Passives.WallRun.Air", true),
				new Pair<>("Abilities.Passives.WallRun.Earth", false),
				new Pair<>("Abilities.Passives.WallRun.Water", false),
				new Pair<>("Abilities.Passives.WallRun.Fire", true),
				new Pair<>("Abilities.Passives.WallRun.Chi", true),
				new Pair<>("Abilities.Passives.WallRun.InvalidBlocks", invalidWallRun)
		);
		
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

	private void addDeathMessages() {
		FileConfiguration lang = ConfigManager.languageConfig.get();

		//Fire
		lang.addDefault("Abilities.Fire.FireComet.DeathMessage", "{victim} was squashed under the pressure of {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.FireBall.DeathMessage", "{victim} burst from {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.FireBreath.DeathMessage", "{victim} was consumed {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.Discharge.DeathMessage", "{victim} couldn't take {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.FirePunch.DeathMessage", "{victim} punched out from {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.FireShots.DeathMessage", "{victim} was shot by {attacker}'s {ability}");
		lang.addDefault("Abilities.Fire.LightningBurst.DeathMessage", "{victim} crackled out of existence from {attacker}'s {ability}");

		//Water
		lang.addDefault("Abilities.Water.Drain.DeathMessage", "{victim} was blasted by {attacker}'s {ability}");
		lang.addDefault("Abilities.Water.FrostBreath.DeathMessage", "{victim} shattered from {attacker}'s {ability}");
		lang.addDefault("Abilities.Water.IceClaws.DeathMessage", "{victim} was ripped to shreds by {attacker}'s {ability}");
		lang.addDefault("Abilities.Water.IceWall.DeathMessage", "{victim} was collateral to {attacker}'s exploding {ability}");
		lang.addDefault("Abilities.Water.WaterBlast.DeathMessage", "{victim} was blasted by {attacker}'s {ability}");
		lang.addDefault("Abilities.Water.Combo.WaterGimbal.DeathMessage", "{victim} was ripped apart by {attacker}'s {ability}");

		//Earth
		lang.addDefault("Abilities.Earth.EarthKick.DeathMessage", "{victim} got too much dirt in their eye from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.EarthLine.DeathMessage", "{victim} lost their footing from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.EarthShard.DeathMessage", "{victim} got blasted apart {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.LavaDisc.DeathMessage", "{victim} sliced in half by {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.LavaFlux.DeathMessage", "{victim} couldn't take the heat from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.LavaThrow.DeathMessage", "{victim} melted from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.MetalFragments.DeathMessage", "{victim} was shredded apart from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.MetalShred.DeathMessage", "{victim} was in the way of {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.MudSurge.DeathMessage", "{victim} drowned in mud from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.SandBlast.DeathMessage", "{victim} was sandblasted to oblivion from {attacker}'s {ability}");
		lang.addDefault("Abilities.Earth.Combo.MagmaBlast.DeathMessage", "{victim} was obliterated by {attacker}'s {ability}");

		//Air
		lang.addDefault("Abilities.Air.AirBlade.DeathMessage", "{victim} was sliced in two by {attacker}'s {ability}");
		lang.addDefault("Abilities.Air.AirPunch.DeathMessage", "{victim} was exploded from {attacker}'s {ability}");
		lang.addDefault("Abilities.Air.SonicBlast.DeathMessage", "{victim}'s ears burst from {attacker}'s {ability}");

		//Chi
		lang.addDefault("Abilities.Chi.DaggerThrow.DeathMessage", "{victim} got stabbed too many times from {attacker}'s {ability}");
		lang.addDefault("Abilities.Chi.Backstab.DeathMessage", "{victim} fell victim to {attacker}'s {ability}");

		//Avatar
		lang.addDefault("Abilities.Avatar.SpiritBeam.DeathMessage", "{victim} was erased from existence by {attacker}'s {ability}");
		lang.addDefault("Abilities.Avatar.ElementSphereAir.DeathMessage", "{victim} was blasted apart by {attacker}'s \u00A75ElementSphere");
		lang.addDefault("Abilities.Avatar.ElementSphereFire.DeathMessage", "{victim} was burnt to cinders by {attacker}'s \u00A75ElementSphere");
		lang.addDefault("Abilities.Avatar.ElementSphereEarth.DeathMessage", "{victim} was crushed by {attacker}'s \u00A75ElementSphere");
		lang.addDefault("Abilities.Avatar.ElementSphereWater.DeathMessage", "{victim} was sliced apart by {attacker}'s \u00A75ElementSphere");
		lang.addDefault("Abilities.Avatar.ElementSphereStream.DeathMessage", "{victim} took the full force of {attacker}'s \u00A75ElementSphere");

		ConfigManager.languageConfig.save();
	}

	private void setupElementSphereNames() {
		FileConfiguration lang = ConfigManager.languageConfig.get();

		lang.addDefault("Abilities.Avatar.ElementSphereAir.Name", "Air");
		lang.addDefault("Abilities.Avatar.ElementSphereFire.Name", "Fire");
		lang.addDefault("Abilities.Avatar.ElementSphereEarth.Name", "Earth");
		lang.addDefault("Abilities.Avatar.ElementSphereWater.Name", "Water");
		lang.addDefault("Abilities.Avatar.ElementSphereStream.Name", "Stream");

		ConfigManager.languageConfig.save();
	}

	public static ConfigurationSection getConfig(Player player) {
		if (player == null) {
			return getConfig((World) null);
		}
		return getConfig(player.getWorld());
	}

	public static ConfigurationSection getConfig(World world) {
		boolean perWorldConfig = plugin.getConfig().getBoolean("Properties.PerWorldConfig");

		if (world == null || !perWorldConfig) {
			return plugin.getConfig();
		}

		String prefix = "Worlds." + world.getName();
		return new SubsectionConfigurationDecorator(plugin.getConfig(), prefix);
	}
}
