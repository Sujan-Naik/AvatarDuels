package com.serene.avatarduels;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.common.reflect.ClassPath;
import com.projectkorra.projectkorra.airbending.AirShield;
import com.projectkorra.projectkorra.firebending.FireShield;
import com.serene.avatarduels.command.Commands;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.listener.AbilityListener;
import com.serene.avatarduels.listener.CommandListener;
import com.serene.avatarduels.listener.JCListener;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.command.SerenityCommand;
import com.serene.avatarduels.npc.listeners.SereneNPCsListener;
import com.serene.avatarduels.npc.utils.NPCUtils;
import com.serene.avatarduels.scoreboard.BendingBoard;
import com.serene.avatarduels.util.*;
import com.serene.avatarduels.util.versionadapter.ParticleAdapter;
import com.serene.avatarduels.util.versionadapter.ParticleAdapterFactory;
import com.serene.avatarduels.util.versionadapter.PotionEffectAdapter;
import com.serene.avatarduels.util.versionadapter.PotionEffectAdapterFactory;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.ElementType;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.Collision;
import com.projectkorra.projectkorra.configuration.Config;

import com.serene.avatarduels.ability.air.GaleGust;
import com.serene.avatarduels.ability.earth.Crumble;
import com.serene.avatarduels.ability.fire.CombustBeam;
import com.serene.avatarduels.ability.fire.FireDisc;
import com.serene.avatarduels.ability.water.RazorLeaf;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

public class AvatarDuels extends JavaPlugin {
	
	public static AvatarDuels instance;
	public static FileConfiguration PK_CONFIG ;
	public static FileConfiguration JC_CONFIG;

	public static final HashMap<String, Config> abilityNameConfigHashMap = new HashMap<>();
	private MainListener listener;
	private Element soundElement;

	public static AvatarDuels plugin;
	public static Logger log;
	public static String dev;
	public static String version;
	public static boolean logDebug;

	private ParticleAdapter particleAdapter;
	private PotionEffectAdapter potionEffectAdapter;

	@Override
	public void onEnable() {
		instance = this;
		plugin = this;

		PK_CONFIG = ProjectKorra.plugin.getConfig();
		this.setupConfig();


		soundElement = new SubElement("Sound", Element.AIR, ElementType.BENDING, this);

		AvatarDuels.log = this.getLogger();
		new AvatarDuelsConfig(this);

		CoreAbility.registerPluginAbilities(this, "com.serene.avatarduels.ability");
		
		this.setupCollisions();
		
		this.listener = new MainListener(this);
		
		this.getCommand("projectaddons").setExecutor(new ProjectCommand());



		logDebug = AvatarDuelsConfig.getConfig((World)null).getBoolean("Properties.LogDebug");

		dev = this.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
		version = this.getDescription().getVersion();

		JCMethods.registerDisabledWorlds();
		getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
		getServer().getPluginManager().registerEvents(new CommandListener(this), this);
		getServer().getPluginManager().registerEvents(new JCListener(this), this);
		getServer().getPluginManager().registerEvents(new ChiRestrictor(), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new JCManager(this), 0, 1);

		BendingBoard.updateOnline();
		new Commands();

		FireTick.loadMethod();

		ParticleAdapterFactory particleAdapterFactory = new ParticleAdapterFactory();
		particleAdapter = particleAdapterFactory.getAdapter();

		PotionEffectAdapterFactory potionEffectAdapterFactory = new PotionEffectAdapterFactory();
		potionEffectAdapter = potionEffectAdapterFactory.getAdapter();

		new BukkitRunnable() {
			@Override
			public void run() {
				JCMethods.registerCombos();
				BendingBoard.loadOtherCooldowns();
				initializeCollisions();
			}
		}.runTaskLater(this, 1);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
			log.info("Initialized Metrics.");
		} catch (IOException e) {
			log.info("Failed to submit statistics for MetricsLite.");
		}

		this.getServer().getPluginManager().registerEvents(new SereneNPCsListener(), this);
		this.getCommand("SereneNPCs").setExecutor(new SerenityCommand());

		NPCUtils.initUUID(0, this);

	}

	public void initializeCollisions() {
		boolean enabled = this.getConfig().getBoolean("Properties.AbilityCollisions.Enabled");

		if (!enabled) {
			getLogger().info("Collisions disabled.");
			return;
		}

		try {
			ClassPath cp = ClassPath.from(this.getClassLoader());

			for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive("com.serene.avatarduels.ability")) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends CoreAbility> abilityClass = (Class<? extends CoreAbility>)Class.forName(info.getName());

					if (abilityClass == null) continue;

					CollisionInitializer initializer = new CollisionInitializer<>(abilityClass);
					initializer.initialize();
				} catch (Exception e) {

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logDebug(String message) {
		if (logDebug) {
			plugin.getLogger().info(message);
		}
	}

	public ParticleAdapter getParticleAdapter() {
		return this.particleAdapter;
	}

	public PotionEffectAdapter getPotionEffectAdapter() {
		return this.potionEffectAdapter;
	}
	
	@Override
	public void onDisable() {
		listener.revertSwappedBinds();

		NPCHandler.getNpcs().forEach(bendingNPC -> Bukkit.getPlayer(bendingNPC.getUUID()).kickPlayer("Suck less"));

		if (CoreAbility.getAbility(Crumble.class) != null) {
			for (Crumble c : CoreAbility.getAbilities(Crumble.class)) {
				c.revert();
			}
		}

		RegenTempBlock.revertAll();

	}

	
	public String prefix() {
		return ChatColor.GRAY + "[" + ChatColor.GREEN + "AvatarDuels" + ChatColor.GRAY + "]";
	}
	
	public String version() {
		return prefix() + " v." + this.getDescription().getVersion();
	}
	
	public Element getSoundElement() {
		return soundElement;
	}

	public static void createAbilityConfig(String abilityName, Pair<String, Object>... pathValuePairs){
		Config newConfig = new Config(new File("abilities/" + abilityName));
		FileConfiguration c = newConfig.get();
		Arrays.stream(pathValuePairs).forEach(stringObjectPair -> {
			c.addDefault(stringObjectPair.getA(), stringObjectPair.getB());
		});
		newConfig.save();
		abilityNameConfigHashMap.put(abilityName, newConfig);
	}

	public void reloadConfigs(){
		abilityNameConfigHashMap.values().forEach(Config::reload);
	}

	public static FileConfiguration getConfig(String name){
		return abilityNameConfigHashMap.get(name).get();
	}
	
	private void setupConfig() {
		
//		new Pair<>("Chat.Colors.Sound", "#3e4d52"); //Make soundbending have a color

//		new Pair<>("Properties.MetallicBlocks", Arrays.asList("GOLD_BLOCK", "IRON_BLOCK", "NETHERITE_BLOCK"));

		// ---- Avatar ----
		// EnergyBeam
		createAbilityConfig("EnergyBeam", 
				new Pair<>("Abilities.Avatar.EnergyBeam.Enabled", true),
				new Pair<>("Abilities.Avatar.EnergyBeam.Cooldown", 12000),
		new Pair<>("Abilities.Avatar.EnergyBeam.Duration", 10000),
		new Pair<>("Abilities.Avatar.EnergyBeam.Damage", 3),
		new Pair<>("Abilities.Avatar.EnergyBeam.Range", 40),
		new Pair<>("Abilities.Avatar.EnergyBeam.EasterEgg", true));
		
		// ---- Airbending ----
		// Flying
		createAbilityConfig("Flying",

				new Pair<>("Passives.Air.Flying.Enabled", true),
		new Pair<>("Passives.Air.Flying.FlySpeed", 0.05),
		new Pair<>("Passives.Air.Flying.Glide.StartSpeed", 0.8),
		new Pair<>("Passives.Air.Flying.Glide.MaxSpeed", 1.6),
		new Pair<>("Passives.Air.Flying.Acceleration", 0.001),
		new Pair<>("Passives.Air.Flying.AbilityBlacklist", Arrays.asList("Tornado", "EarthSmash", "Surge", "Lightning")));
		
		// Deafen
		createAbilityConfig("Deafen",
				new Pair<>("Abilities.Air.Deafen.Enabled", true),
		new Pair<>("Abilities.Air.Deafen.Cooldown", 10000),
		new Pair<>("Abilities.Air.Deafen.Duration", 6000));
		
		// GaleGust
		createAbilityConfig("GaleGust",
				new Pair<>("Abilities.Air.GaleGust.Enabled", true),
		new Pair<>("Abilities.Air.GaleGust.Cooldown", 9000),
		new Pair<>("Abilities.Air.GaleGust.Damage", 4),
		new Pair<>("Abilities.Air.GaleGust.Radius", 1),
		new Pair<>("Abilities.Air.GaleGust.Range", 18),
		new Pair<>("Abilities.Air.GaleGust.Knockback", 0.67));
		
		// SonicWave
		createAbilityConfig("SonicWave",

				new Pair<>("Abilities.Air.SonicWave.Enabled", true),
		new Pair<>("Abilities.Air.SonicWave.Cooldown", 4000),
		new Pair<>("Abilities.Air.SonicWave.Range", 25),
		new Pair<>("Abilities.Air.SonicWave.Width", 10),
		new Pair<>("Abilities.Air.SonicWave.Nausea.Duration", 120),
		new Pair<>("Abilities.Air.SonicWave.Nausea.Power", 2));
		
		// VocalMimicry
		createAbilityConfig("VocalMimicry",

				new Pair<>("Abilities.Air.VocalMimicry.Enabled", true),
		new Pair<>("Abilities.Air.VocalMimicry.Volume", 0.7),
		new Pair<>("Abilities.Air.VocalMimicry.Pitch", 1),
		new Pair<>("Abilities.Air.VocalMimicry.SoundBlacklist", Arrays.asList("SOUND_NAME_HERE")));
		
		// Zephyr
		createAbilityConfig("Zephyr",

				new Pair<>("Abilities.Air.Zephyr.Enabled", true),
		new Pair<>("Abilities.Air.Zephyr.Cooldown", 1000),
		new Pair<>("Abilities.Air.Zephyr.Radius", 4));
		
		// Tailwind
		createAbilityConfig("Tailwind",

				new Pair<>("Combos.Air.Tailwind.Enabled", true),
		new Pair<>("Combos.Air.Tailwind.Cooldown", 7000),
		new Pair<>("Combos.Air.Tailwind.Duration", 22000),
		new Pair<>("Combos.Air.Tailwind.Speed", 9));
		
		// ---- Earthbending ----
		// LandLaunch
		createAbilityConfig("LandLaunch",

				new Pair<>("Passives.Earth.LandLaunch.Enabled", true),
		new Pair<>("Passives.Earth.LandLaunch.Power", 3));

		// Accretion
		createAbilityConfig("Accretion",

				new Pair<>("Abilities.Earth.Accretion.Enabled", true),
		new Pair<>("Abilities.Earth.Accretion.Cooldown", 10000),
		new Pair<>("Abilities.Earth.Accretion.Damage", 1),
		new Pair<>("Abilities.Earth.Accretion.Blocks", 8),
		new Pair<>("Abilities.Earth.Accretion.SelectRange", 6),
		new Pair<>("Abilities.Earth.Accretion.RevertTime", 20000),
		new Pair<>("Abilities.Earth.Accretion.ThrowSpeed", 1.6));

		// Bulwark
		createAbilityConfig("Bulwark",

				new Pair<>("Abilities.Earth.Bulwark.Enabled", true),
		new Pair<>("Abilities.Earth.Bulwark.Cooldown", 6000),
		new Pair<>("Abilities.Earth.Bulwark.Damage", 1),
		new Pair<>("Abilities.Earth.Bulwark.ThrowSpeed", 0.94),
		new Pair<>("Abilities.Earth.Bulwark.Height", 2));
		
		// Crumble
		createAbilityConfig("Crumble",

				new Pair<>("Abilities.Earth.Crumble.Enabled", true),
		new Pair<>("Abilities.Earth.Crumble.Cooldown", 3000),
		new Pair<>("Abilities.Earth.Crumble.Radius", 6),
		new Pair<>("Abilities.Earth.Crumble.SelectRange", 9),
		new Pair<>("Abilities.Earth.Crumble.RevertTime", 60));
		
		// Dig
		createAbilityConfig("Dig",

				new Pair<>("Abilities.Earth.Dig.Enabled", true),
		new Pair<>("Abilities.Earth.Dig.Cooldown", 3000),
		new Pair<>("Abilities.Earth.Dig.Duration", -1),
		new Pair<>("Abilities.Earth.Dig.RevertTime", 3500),
		new Pair<>("Abilities.Earth.Dig.Speed", 0.51));
		
		// EarthKick
		createAbilityConfig("EarthKick",

				new Pair<>("Abilities.Earth.EarthKick.Enabled", true),
		new Pair<>("Abilities.Earth.EarthKick.Cooldown", 4000),
		new Pair<>("Abilities.Earth.EarthKick.Damage", 0.5),
		new Pair<>("Abilities.Earth.EarthKick.MaxBlocks", 9),
		new Pair<>("Abilities.Earth.EarthKick.LavaMultiplier", 1.5));
		
		// LavaSurge
		createAbilityConfig("LavaSurge",

				new Pair<>("Abilities.Earth.LavaSurge.Enabled", true),
		new Pair<>("Abilities.Earth.LavaSurge.Cooldown", 4000),
		new Pair<>("Abilities.Earth.LavaSurge.Damage", 0.5),
		new Pair<>("Abilities.Earth.LavaSurge.Speed", 1.14),
		new Pair<>("Abilities.Earth.LavaSurge.SelectRange", 5),
		new Pair<>("Abilities.Earth.LavaSurge.SourceRadius", 3),
		new Pair<>("Abilities.Earth.LavaSurge.MaxBlocks", 10),
		new Pair<>("Abilities.Earth.LavaSurge.Burn.Enabled", true),
		new Pair<>("Abilities.Earth.LavaSurge.Burn.Duration", 3000));

		// MagmaSlap
		createAbilityConfig("MagmaSlap",

				new Pair<>("Abilities.Earth.MagmaSlap.Enabled", true),
		new Pair<>("Abilities.Earth.MagmaSlap.Cooldown", 4000),
		new Pair<>("Abilities.Earth.MagmaSlap.Offset", 1.5),
		new Pair<>("Abilities.Earth.MagmaSlap.Damage", 2),
		new Pair<>("Abilities.Earth.MagmaSlap.Length", 13),
		new Pair<>("Abilities.Earth.MagmaSlap.Width", 1),
		new Pair<>("Abilities.Earth.MagmaSlap.RevertTime", 7000));

		// QuickWeld
		createAbilityConfig("QuickWeld",

				new Pair<>("Abilities.Earth.QuickWeld.Enabled", true),
		new Pair<>("Abilities.Earth.QuickWeld.Cooldown", 1000),
		new Pair<>("Abilities.Earth.QuickWeld.RepairAmount", 25),
		new Pair<>("Abilities.Earth.QuickWeld.RepairInterval", 1250));
		
		// Shrapnel
		createAbilityConfig("Shrapnel",

				new Pair<>("Abilities.Earth.Shrapnel.Enabled", true),
		new Pair<>("Abilities.Earth.Shrapnel.Shot.Cooldown", 2000),
		new Pair<>("Abilities.Earth.Shrapnel.Shot.Damage", 2),
		new Pair<>("Abilities.Earth.Shrapnel.Shot.Speed", 2.3),
		new Pair<>("Abilities.Earth.Shrapnel.Blast.Cooldown", 8000),
		new Pair<>("Abilities.Earth.Shrapnel.Blast.Shots", 9),
		new Pair<>("Abilities.Earth.Shrapnel.Blast.Spread", 24),
		new Pair<>("Abilities.Earth.Shrapnel.Blast.Speed", 1.7));
		
		// RockSlide
		createAbilityConfig("RockSlide",

				new Pair<>("Combos.Earth.RockSlide.Enabled", true),
		new Pair<>("Combos.Earth.RockSlide.Cooldown", 7000),
		new Pair<>("Combos.Earth.RockSlide.Damage", 1),
		new Pair<>("Combos.Earth.RockSlide.Knockback", 0.9),
		new Pair<>("Combos.Earth.RockSlide.Knockup", 0.4),
		new Pair<>("Combos.Earth.RockSlide.Speed", 0.68),
		new Pair<>("Combos.Earth.RockSlide.RequiredRockCount", 6),
		new Pair<>("Combos.Earth.RockSlide.TurningSpeed", 0.086),
		new Pair<>("Combos.Earth.RockSlide.Duration", -1));
		
		// ---- Firebending ----
		// ArcSpark
		createAbilityConfig("ArcSpark",

				new Pair<>("Abilities.Fire.ArcSpark.Enabled", true),
		new Pair<>("Abilities.Fire.ArcSpark.Speed", 6),
		new Pair<>("Abilities.Fire.ArcSpark.Length", 7),
		new Pair<>("Abilities.Fire.ArcSpark.Damage", 1),
		new Pair<>("Abilities.Fire.ArcSpark.Cooldown", 6500),
		new Pair<>("Abilities.Fire.ArcSpark.Duration", 4000),
		new Pair<>("Abilities.Fire.ArcSpark.ChargeTime", 500));
		
		// CombustBeam
		createAbilityConfig("CombustBeam",

				new Pair<>("Abilities.Fire.CombustBeam.Enabled", true),
		new Pair<>("Abilities.Fire.CombustBeam.Range", 50),
		new Pair<>("Abilities.Fire.CombustBeam.Cooldown", 5000),
		new Pair<>("Abilities.Fire.CombustBeam.Minimum.Power", 0.6),
		new Pair<>("Abilities.Fire.CombustBeam.Minimum.Angle", 0.2),
		new Pair<>("Abilities.Fire.CombustBeam.Minimum.ChargeTime", 1000),
		new Pair<>("Abilities.Fire.CombustBeam.Minimum.Damage", 2),
		new Pair<>("Abilities.Fire.CombustBeam.Maximum.Power", 2.7),
		new Pair<>("Abilities.Fire.CombustBeam.Maximum.Angle", 40),
		new Pair<>("Abilities.Fire.CombustBeam.Maximum.ChargeTime", 5000),
		new Pair<>("Abilities.Fire.CombustBeam.Maximum.Damage", 10),
		new Pair<>("Abilities.Fire.CombustBeam.InterruptedDamage", 10),
		new Pair<>("Abilities.Fire.CombustBeam.RevertTime", 13000));
		
		// ChargeBolt
		createAbilityConfig("ChargeBolt",

				new Pair<>("Abilities.Fire.ChargeBolt.Enabled", true),
		new Pair<>("Abilities.Fire.ChargeBolt.Damage", 2),
		new Pair<>("Abilities.Fire.ChargeBolt.Cooldown", 8000),
		new Pair<>("Abilities.Fire.ChargeBolt.Speed", 6),
		new Pair<>("Abilities.Fire.ChargeBolt.ChargeTime", 3000),
		new Pair<>("Abilities.Fire.ChargeBolt.BoltRange", 26),
		new Pair<>("Abilities.Fire.ChargeBolt.BlastRadius", 13),
		new Pair<>("Abilities.Fire.ChargeBolt.DischargeBoltCount", 6));
		
		// Electrify
		createAbilityConfig("Electrify",

				new Pair<>("Abilities.Fire.Electrify.Enabled", true),
		new Pair<>("Abilities.Fire.Electrify.Cooldown", 4000),
		new Pair<>("Abilities.Fire.Electrify.Duration", 7000),
		new Pair<>("Abilities.Fire.Electrify.DamageInWater", 2),
		new Pair<>("Abilities.Fire.Electrify.Slowness", 2),
		new Pair<>("Abilities.Fire.Electrify.Weakness", 1));
		
		// Explode
		createAbilityConfig("Explode",

				new Pair<>("Abilities.Fire.Explode.Enabled", true),
		new Pair<>("Abilities.Fire.Explode.Cooldown", 4500),
		new Pair<>("Abilities.Fire.Explode.Damage", 2),
		new Pair<>("Abilities.Fire.Explode.Radius", 2.4),
		new Pair<>("Abilities.Fire.Explode.Knockback", 1.94),
		new Pair<>("Abilities.Fire.Explode.Range", 7.4));
		
		// FireDisc
		createAbilityConfig("FireDisc",

				new Pair<>("Abilities.Fire.FireDisc.Enabled", true),
		new Pair<>("Abilities.Fire.FireDisc.Cooldown", 1700),
		new Pair<>("Abilities.Fire.FireDisc.Damage", 1.5),
		new Pair<>("Abilities.Fire.FireDisc.Range", 32),
		new Pair<>("Abilities.Fire.FireDisc.Knockback", 0.84),
		new Pair<>("Abilities.Fire.FireDisc.Controllable", true),
		new Pair<>("Abilities.Fire.FireDisc.RevertCutBlocks", true),
		new Pair<>("Abilities.Fire.FireDisc.DropCutBlocks", false),
		new Pair<>("Abilities.Fire.FireDisc.CuttableBlocks", Arrays.asList("ACACIA_LOG", "OAK_LOG", "JUNGLE_LOG", "BIRCH_LOG", "DARK_OAK_LOG", "SPRUCE_LOG")));

		// Jets
		createAbilityConfig("Jets",

				new Pair<>("Abilities.Fire.Jets.Enabled", true),
		new Pair<>("Abilities.Fire.Jets.Cooldown.Minimum", 4000),
		new Pair<>("Abilities.Fire.Jets.Cooldown.Maximum", 12000),
		new Pair<>("Abilities.Fire.Jets.Duration", 20000),
		new Pair<>("Abilities.Fire.Jets.FlySpeed", 0.65),
		new Pair<>("Abilities.Fire.Jets.HoverSpeed", 0.065),
		new Pair<>("Abilities.Fire.Jets.SpeedThreshold", 2.4),
		new Pair<>("Abilities.Fire.Jets.DamageThreshold", 4),
		new Pair<>("Abilities.Fire.Jets.MaxHeight", -1));
		
		// FlameBreath
		createAbilityConfig("FlameBreath",

				new Pair<>("Combos.Fire.FlameBreath.Enabled", true),
		new Pair<>("Combos.Fire.FlameBreath.Cooldown", 8000),
		new Pair<>("Combos.Fire.FlameBreath.Damage", 0.2),
		new Pair<>("Combos.Fire.FlameBreath.FireTick", 30),
		new Pair<>("Combos.Fire.FlameBreath.Range", 5),
		new Pair<>("Combos.Fire.FlameBreath.Speed", 0.65),
		new Pair<>("Combos.Fire.FlameBreath.Duration", 4000),
		new Pair<>("Combos.Fire.FlameBreath.Burn.Ground", true),
		new Pair<>("Combos.Fire.FlameBreath.Burn.Entities", true),
		new Pair<>("Combos.Fire.FlameBreath.Rainbow", true));

		// TurboJet
		createAbilityConfig("TurboJet",

				new Pair<>("Combos.Fire.TurboJet.Enabled", true),
		new Pair<>("Combos.Fire.TurboJet.Cooldown", 12000),
		new Pair<>("Combos.Fire.TurboJet.Speed", 1.95));
		
		// ---- Waterbending ----
		// Hydrojet
		createAbilityConfig("Hydrojet",

				new Pair<>("Passives.Water.Hydrojet.Enabled", true),
		new Pair<>("Passives.Water.Hydrojet.Speed", 8));
		
		// BloodGrip
		/*
		new Pair<>("Abilities.Water.BloodGrip.Enabled", true),
		new Pair<>("Abilities.Water.BloodGrip.Cooldown", 6000),
		new Pair<>("Abilities.Water.BloodGrip.Range", 8),
		new Pair<>("Abilities.Water.BloodGrip.DragSpeed", 0.32),
		new Pair<>("Abilities.Water.BloodGrip.ThrowPower", 1.3),
		new Pair<>("Abilities.Water.BloodGrip.MangleDamage", 3),
		new Pair<>("Abilities.Water.BloodGrip.SlamSpeed", 2),
		new Pair<>("Abilities.Water.BloodGrip.DamageThreshold", 4),
		new Pair<>("Abilities.Water.BloodGrip.EntityFilter", Arrays.asList(EntityType.ENDER_CRYSTAL.toString(), EntityType.ENDER_DRAGON.toString(), EntityType.ARMOR_STAND.toString(), EntityType.BLAZE.toString(), EntityType.WITHER.toString()));
		new Pair<>("Abilities.Water.BloodGrip.BasicAbilities", Arrays.asList("AirBlast", "AirSwipe", "EarthBlast", "FireBlast", "FireDisc", "WaterManipulation"));
		*/

		// RazorLeaf
		createAbilityConfig("RazorLeaf",

				new Pair<>("Abilities.Water.RazorLeaf.Enabled", true),
		new Pair<>("Abilities.Water.RazorLeaf.Cooldown", 3000),
		new Pair<>("Abilities.Water.RazorLeaf.Damage", 2),
		new Pair<>("Abilities.Water.RazorLeaf.Radius", 0.7),
		new Pair<>("Abilities.Water.RazorLeaf.Range", 24),
		new Pair<>("Abilities.Water.RazorLeaf.MaxRecalls", 3),
		new Pair<>("Abilities.Water.RazorLeaf.Particles", 300));
		
		// PlantArmor
		createAbilityConfig("PlantArmor",

				new Pair<>("Abilities.Water.PlantArmor.Enabled", true),
		new Pair<>("Abilities.Water.PlantArmor.Cooldown", 10000),
		new Pair<>("Abilities.Water.PlantArmor.Duration", -1),
		new Pair<>("Abilities.Water.PlantArmor.Durability", 2000),
		new Pair<>("Abilities.Water.PlantArmor.SelectRange", 9),
		new Pair<>("Abilities.Water.PlantArmor.RequiredPlants", 14),
		new Pair<>("Abilities.Water.PlantArmor.Boost.Swim", 3),
		new Pair<>("Abilities.Water.PlantArmor.Boost.Speed", 2),
		new Pair<>("Abilities.Water.PlantArmor.Boost.Jump", 2),
		
		// PlantArmor - VineWhip

		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.VineWhip.Cost", 50),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.VineWhip.Cooldown", 2000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.VineWhip.Damage", 2),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.VineWhip.Range", 18),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.VineWhip.Speed", 3),
		
		// PlantArmor - RazorLeaf
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.RazorLeaf.Cost", 150),
		
		// PlantArmor - LeafShield
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafShield.Cost", 100),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafShield.Cooldown", 1500),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafShield.Radius", 2),
		
		// PlantArmor - Tangle
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Tangle.Cost", 200),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Tangle.Cooldown", 7000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Tangle.Radius", 0.45),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Tangle.Duration", 3000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Tangle.Range", 18),
		
		// PlantArmor - Leap
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Leap.Cost", 100),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Leap.Cooldown", 2500),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Leap.Power", 1.4),
		
		// PlantArmor - Grapple
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Grapple.Cost", 100),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Grapple.Cooldown", 2000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Grapple.Range", 25),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Grapple.Speed", 1.24),
		
		// PlantArmor - LeafDome
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafDome.Cost", 400),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafDome.Cooldown", 5000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.LeafDome.Radius", 3),
		
		// PlantArmor - Regenerate
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Regenerate.Cooldown", 10000),
		new Pair<>("Abilities.Water.PlantArmor.SubAbilities.Regenerate.RegenAmount", 150));
		
		// LeafStorm
				createAbilityConfig("LeafStorm",

						new Pair<>("Combos.Water.LeafStorm.Enabled", true),
		new Pair<>("Combos.Water.LeafStorm.Cooldown", 7000),
		new Pair<>("Combos.Water.LeafStorm.PlantArmorCost", 800),
		new Pair<>("Combos.Water.LeafStorm.LeafCount", 10),
		new Pair<>("Combos.Water.LeafStorm.LeafSpeed", 14),
		new Pair<>("Combos.Water.LeafStorm.Damage", 0.5),
		new Pair<>("Combos.Water.LeafStorm.Radius", 6));
		
		// MistShards
				createAbilityConfig("MistShards",

						new Pair<>("Combos.Water.MistShards.Enabled", true),
		new Pair<>("Combos.Water.MistShards.Cooldown", 7000),
		new Pair<>("Combos.Water.MistShards.Damage", 1),
		new Pair<>("Combos.Water.MistShards.Range", 20),
		new Pair<>("Combos.Water.MistShards.IcicleCount", 8));
		
		// ---- Chiblocking ----
		// Dodging
		createAbilityConfig("Dodging",

				new Pair<>("Passives.Chi.Dodging.Enabled", true),
		new Pair<>("Passives.Chi.Dodging.Chance", 18));
		
		// Camouflage
		createAbilityConfig("Camouflage",

				new Pair<>("Passives.Chi.Camouflage.Enabled", true));
		
		// Jab
		createAbilityConfig("Jab",

				new Pair<>("Abilities.Chi.Jab.Enabled", true),
		new Pair<>("Abilities.Chi.Jab.Cooldown", 3000),
		new Pair<>("Abilities.Chi.Jab.MaxUses", 4));
		
		// NinjaStance
		createAbilityConfig("NinjaStance",

				new Pair<>("Abilities.Chi.NinjaStance.Enabled", true),
		new Pair<>("Abilities.Chi.NinjaStance.Cooldown", 0),
		new Pair<>("Abilities.Chi.NinjaStance.Stealth.Duration", 5000),
		new Pair<>("Abilities.Chi.NinjaStance.Stealth.ChargeTime", 2000),
		new Pair<>("Abilities.Chi.NinjaStance.Stealth.Cooldown", 8000),
		new Pair<>("Abilities.Chi.NinjaStance.SpeedAmplifier", 5),
		new Pair<>("Abilities.Chi.NinjaStance.JumpAmplifier", 5),
		new Pair<>("Abilities.Chi.NinjaStance.DamageModifier", 0.75));
		
		// ChiblockJab
		createAbilityConfig("ChiblockJab",

				new Pair<>("Combos.Chi.ChiblockJab.Enabled", true),
		new Pair<>("Combos.Chi.ChiblockJab.Cooldown", 5000),
		new Pair<>("Combos.Chi.ChiblockJab.Duration", 2000));
		
		// FlyingKick
		createAbilityConfig("FlyingKick",

				new Pair<>("Combos.Chi.FlyingKick.Enabled", true),
		new Pair<>("Combos.Chi.FlyingKick.Cooldown", 4000),
		new Pair<>("Combos.Chi.FlyingKick.Damage", 2.0),
		new Pair<>("Combos.Chi.FlyingKick.LaunchPower", 1.85));
		
		// WeakeningJab
		createAbilityConfig("WeakeningJab",

				new Pair<>("Combos.Chi.WeakeningJab.Enabled", true),
		new Pair<>("Combos.Chi.WeakeningJab.Cooldown", 6000),
		new Pair<>("Combos.Chi.WeakeningJab.Duration", 4000),
		new Pair<>("Combos.Chi.WeakeningJab.Modifier", 1.5));

	}
	
	private void setupCollisions() {
		if (CoreAbility.getAbility(FireDisc.class) != null) {
			ProjectKorra.getCollisionInitializer().addSmallAbility(CoreAbility.getAbility(FireDisc.class));
		}
		
		if (CoreAbility.getAbility(RazorLeaf.class) != null) {
			ProjectKorra.getCollisionInitializer().addSmallAbility(CoreAbility.getAbility(RazorLeaf.class));
		}
		
		if (CoreAbility.getAbility(GaleGust.class) != null) {
			ProjectKorra.getCollisionInitializer().addSmallAbility(CoreAbility.getAbility(GaleGust.class));
		}
		
		if (CoreAbility.getAbility(CombustBeam.class) != null) {
			ProjectKorra.getCollisionInitializer().addLargeAbility(CoreAbility.getAbility(CombustBeam.class));
			ProjectKorra.getCollisionManager().addCollision(new Collision(CoreAbility.getAbility(FireShield.class), CoreAbility.getAbility(CombustBeam.class), false, true));
			ProjectKorra.getCollisionManager().addCollision(new Collision(CoreAbility.getAbility(AirShield.class), CoreAbility.getAbility(CombustBeam.class), false, true));
		}
	}
}
