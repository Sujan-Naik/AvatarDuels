package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.RangedAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.charged.ChargedAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.ranged.sourced.SourcedAbility;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

import static com.projectkorra.projectkorra.ability.CoreAbility.getConfig;
import static com.projectkorra.projectkorra.object.Preset.config;
import static com.serene.avatarduels.AvatarDuels.PK_CONFIG;

public enum AbilityUsages {


    AIRBREATH("AirBreath", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("AirBreath"));
    }, (npc) -> new ChargedAbility("AirBreath", npc, "AirBreath", AvatarDuels.getConfig("AirBreath").getDouble("Abilities.Air.AirBreath.Range"), null)),

    AIRBLAST("AirBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBlast"));
    }, (npc) -> new ChargedAbility("AirBlast", npc, "AirBlast", PK_CONFIG.getDouble("Abilities.Air.AirBlast.Range"))),

    AIRBURST("AirBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBurst"));
    }, (npc) -> new ChargedAbility("AirBurst", npc, "AirBurst", PK_CONFIG.getDouble("Abilities.Air.AirBurst.Range"))),

    AIRGLIDE("AirGlide", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("AirGlide"));
    }, (npc) -> new ChargedAbility("AirGlide", npc, "AirGlide", PK_CONFIG.getDouble("Abilities.Air.AirGlide.Range"))),

    AIRPUNCH("AirPunch", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirPunch"));
    }, (npc) -> new RangedAbility("AirPunch", npc, "AirPunch", AvatarDuels.getConfig("AirPunch").getDouble("Abilities.Air.AirPunch.Range"))),

    AIRSCOOTER("AirScooter", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("AirScooter"));
    }, (npc) -> new SourcedAbility("AirScooter", npc, "AirScooter", PK_CONFIG.getDouble("Abilities.Air.AirScooter.Range"),
            PK_CONFIG.getDouble("Abilities.Air.AirScooter.SelectRange"), Element.AIR)),

    AIRSHIELD("AirShield", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirShield"));
    }, (npc) -> new ChargedAbility("AirShield", npc, "AirShield", PK_CONFIG.getDouble("Abilities.Air.AirShield.Range"))),

    AIRSPOUT("AirSpout", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("AirSpout"));
    }, (npc) -> new SourcedAbility("AirSpout", npc, "AirSpout", PK_CONFIG.getDouble("Abilities.Air.AirSpout.Range"),
            PK_CONFIG.getDouble("Abilities.Air.AirSpout.SelectRange"), Element.AIR)),

    AIRSUCTION("AirSuction", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSuction"));
    }, (npc) -> new ChargedAbility("AirSuction", npc, "AirSuction", PK_CONFIG.getDouble("Abilities.Air.AirSuction.Range"))),


    DEAFEN("Deafen", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Deafen"));
    }, (npc) -> new ChargedAbility("Deafen", npc, "Deafen", AvatarDuels.getConfig("Deafen").getDouble("Abilities.Air.Deafen.Range"))),

    TAILWIND("Tailwind", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Tailwind"));
    }, (npc) -> new ChargedAbility("Tailwind", npc, "Tailwind", AvatarDuels.getConfig("Tailwind").getDouble("Abilities.Air.Tailwind.Range"))),

    VOCALMIMICRY("VocalMimicry", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("VocalMimicry"));
    }, (npc) -> new ChargedAbility("VocalMimicry", npc, "VocalMimicry", AvatarDuels.getConfig("VocalMimicry").getDouble("Abilities.Air.VocalMimicry.Range"))),

    ZEPHYR("Zephyr", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Zephyr"));
    }, (npc) -> new ChargedAbility("Zephyr", npc, "Zephyr", AvatarDuels.getConfig("Zephyr").getDouble("Abilities.Air.Zephyr.Range"))),

    SUFFOCATE("Suffocate", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Suffocate"));
    }, (npc) -> new ChargedAbility("Suffocate", npc, "Suffocate", AvatarDuels.getConfig("Suffocate").getDouble("Abilities.Air.Suffocate.Range"))),

    TORNADO("Tornado", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Tornado"));
    }, (npc) -> new ChargedAbility("Tornado", npc, "Tornado", PK_CONFIG.getDouble("Abilities.Air.Tornado.Range"))),

    // EARTH ABILITIES
    CRUMBLE("Crumble", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Crumble"));
    }, (npc) -> new SourcedAbility("Crumble", npc, "Crumble", PK_CONFIG.getDouble("Abilities.Earth.Crumble.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.Crumble.SelectRange"), Element.EARTH)),

    CATAPULT("Catapult", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Catapult"));
    }, (npc) -> new ChargedAbility("Catapult", npc, "Catapult", PK_CONFIG.getDouble("Abilities.Earth.Catapult.Range"))),

    DIG("Dig", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Dig"));
    }, (npc) -> new SourcedAbility("Dig", npc, "Dig", PK_CONFIG.getDouble("Abilities.Earth.Dig.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.Dig.SelectRange"), Element.EARTH.EARTH)),

    EARTHARMOR("EarthArmor", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthArmor"));
    }, (npc) -> new SourcedAbility("EarthArmor", npc, "EarthArmor", PK_CONFIG.getDouble("Abilities.Earth.EarthArmor.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.EarthArmor.SelectRange"), Element.EARTH)),

    EARTHBLAST("EarthBlast", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthBlast"));
    }, (npc) -> new SourcedAbility("EarthBlast", npc, "EarthBlast", PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.SelectRange"), Element.EARTH)),

    EARTHKICK("EarthKick", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthKick"));
    }, (npc) -> new SourcedAbility("EarthKick", npc, "EarthKick", PK_CONFIG.getDouble("Abilities.Earth.EarthKick.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.EarthKick.SelectRange"), Element.EARTH)),

    FISSURE("Fissure", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Fissure"));
    }, (npc) -> new SourcedAbility("Fissure", npc, "Fissure", PK_CONFIG.getDouble("Abilities.Earth.Fissure.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.Fissure.SelectRange"), Element.EARTH)),

    LANDLAUNCH("LandLaunch", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("LandLaunch"));
    }, (npc) -> new RangedAbility("LandLaunch", npc, "LandLaunch", AvatarDuels.getConfig("LandLaunch").getDouble("Abilities.Earth.LandLaunch.Range"))),

    LAVADISC("LavaDisc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("LavaDisc"));
    }, (npc) -> new RangedAbility("LavaDisc", npc, "LavaDisc", AvatarDuels.getConfig("LavaDisc").getDouble("Abilities.Earth.LavaDisc.Range"))),

    LAVAFLUX("LavaFlux", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("LavaFlux"));
    }, (npc) -> new SourcedAbility("LavaFlux", npc, "LavaFlux", PK_CONFIG.getDouble("Abilities.Earth.LavaFlux.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.LavaFlux.SelectRange"), Element.EARTH)),

    LAVASURGE("LavaSurge", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("LavaSurge"));
    }, (npc) -> new RangedAbility("LavaSurge", npc, "LavaSurge", AvatarDuels.getConfig("LavaSurge").getDouble("Abilities.Earth.LavaSurge.Range"))),

    LAVATHROW("LavaThrow", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("LavaThrow"));
    }, (npc) -> new RangedAbility("LavaThrow", npc, "LavaThrow", AvatarDuels.getConfig("LavaThrow").getDouble("Abilities.Earth.LavaThrow.Range"))),

    MAGMASLAP("MagmaSlap", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MagmaSlap"));
    }, (npc) -> new SourcedAbility("MagmaSlap", npc, "MagmaSlap", PK_CONFIG.getDouble("Abilities.Earth.MagmaSlap.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.MagmaSlap.SelectRange"), Element.EARTH)),

    METALARMOR("MetalArmor", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MetalArmor"));
    }, (npc) -> new SourcedAbility("MetalArmor", npc, "MetalArmor", PK_CONFIG.getDouble("Abilities.Earth.MetalArmor.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.MetalArmor.SelectRange"), Element.EARTH)),

    METALFRAGMENTS("MetalFragments", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("MetalFragments"));
    }, (npc) -> new RangedAbility("MetalFragments", npc, "MetalFragments", AvatarDuels.getConfig("MetalFragments").getDouble("Abilities.Earth.MetalFragments.Range"))),

    METALHOOK("MetalHook", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MetalHook"));
    }, (npc) -> new SourcedAbility("MetalHook", npc, "MetalHook", PK_CONFIG.getDouble("Abilities.Earth.MetalHook.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.MetalHook.SelectRange"), Element.EARTH)),

    METALSHRED("MetalShred", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("MetalShred"));
    }, (npc) -> new RangedAbility("MetalShred", npc, "MetalShred", AvatarDuels.getConfig("MetalShred").getDouble("Abilities.Earth.MetalShred.Range"))),

    QUICKWELD("QuickWeld", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("QuickWeld"));
    }, (npc) -> new SourcedAbility("QuickWeld", npc, "QuickWeld", PK_CONFIG.getDouble("Abilities.Earth.QuickWeld.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.QuickWeld.SelectRange"), Element.EARTH)),

    ROCKSLIDE("RockSlide", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("RockSlide"));
    }, (npc) -> new RangedAbility("RockSlide", npc, "RockSlide", AvatarDuels.getConfig("RockSlide").getDouble("Abilities.Earth.RockSlide.Range"))),

    SANDBLAST("SandBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("SandBlast"));
    }, (npc) -> new RangedAbility("SandBlast", npc, "SandBlast", AvatarDuels.getConfig("SandBlast").getDouble("Abilities.Earth.SandBlast.Range"))),

    SHOCKWAVE("Shockwave", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Shockwave"), ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Shockwave.ChargeTime"));
    }, (npc) -> new ChargedAbility("Shockwave", npc, "Shockwave", PK_CONFIG.getDouble("Abilities.Earth.Shockwave.Range"), null)),

    ACCRETION("Accretion", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Accretion"));
    }, (npc) -> new SourcedAbility("Accretion", npc, "Accretion", 25,
                                   AvatarDuels.getConfig("Accretion").getDouble("Abilities.Earth.Accretion.SelectRange"), Element.EARTH)),


    EARTHSHARD("EarthShard", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthShard"));
    }, (npc) -> new SourcedAbility("EarthShard", npc, "EarthShard", AvatarDuels.getConfig("EarthShard").getDouble("Abilities.Earth.EarthShard.AbilityRange"),
            AvatarDuels.getConfig("EarthShard").getDouble("Abilities.Earth.EarthShard.PrepareRange"), Element.EARTH)),

    MUDSURGE("MudSurge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MudSurge"));
    }, (npc) -> new SourcedAbility("MudSurge", npc, "MudSurge", 40,
            AvatarDuels.getConfig("MudSurge").getDouble("Abilities.Earth.MudSurge.SourceRange"), Element.EARTH)),

    EARTHLINE("EarthLine", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthLine"));
    }, (npc) -> new SourcedAbility("EarthLine", npc, "EarthLine", AvatarDuels.getConfig("EarthLine").getDouble("Abilities.Earth.EarthLine.Range"),
            AvatarDuels.getConfig("EarthLine").getDouble("Abilities.Earth.EarthLine.PrepareRange"), Element.EARTH)),


    AIRBLADE("AirBlade", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBlade"));
    }, (npc) -> new RangedAbility("AirBlade", npc, "AirBlade", AvatarDuels.getConfig("AirBlade").getDouble("Abilities.Air.AirBlade.Range"))),


    AIRSWIPE("AirSwipe", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSwipe"));
    }, (npc) -> new RangedAbility("AirSwipe", npc, "AirSwipe", AvatarDuels.getConfig("AirSwipe").getDouble("Abilities.Air.AirSwipe.Range"))),

    GALEGUST("GaleGust", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("GaleGust"));
    }, (npc) -> new RangedAbility("GaleGust", npc, "GaleGust", AvatarDuels.getConfig("GaleGust").getDouble("Abilities.Air.GaleGust.Range"))),

    SONICBLAST("SonicBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("SonicBlast"),  ProjectKorra.plugin.getConfig().getLong("Abilities.Air.SonicBlast.ChargeTime"));
    }, (npc) -> new ChargedAbility("SonicBlast", npc, "SonicBlast", PK_CONFIG.getDouble("Abilities.Air.SonicBlast.Range"),
            null)),



    // FIRE ABILITIES
    ARCSPARK("ArcSpark", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("ArcSpark"));
    }, (npc) -> new RangedAbility("ArcSpark", npc, "ArcSpark", AvatarDuels.getConfig("ArcSpark").getDouble("Abilities.Fire.ArcSpark.Range"))),

    BLAZE("Blaze", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Blaze"));
    }, (npc) -> new ChargedAbility("Blaze", npc, "Blaze", PK_CONFIG.getDouble("Abilities.Fire.Blaze.Range"))),

    BLAZEARC("BlazeArc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("BlazeArc"));
    }, (npc) -> new ChargedAbility("BlazeArc", npc, "BlazeArc", PK_CONFIG.getDouble("Abilities.Fire.BlazeArc.Range"))),

    BLAZERING("BlazeRing", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("BlazeRing"));
    }, (npc) -> new ChargedAbility("BlazeRing", npc, "BlazeRing", PK_CONFIG.getDouble("Abilities.Fire.BlazeRing.Range"))),

    FIREBLAST("FireBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"));
    }, (npc) -> new ChargedAbility("FireBlast", npc, "FireBlast", PK_CONFIG.getDouble("Abilities.Fire.FireBlast.Range"))),

    FIREBLASTCHARGED("FireBlastCharged", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlastCharged"));
    }, (npc) -> new ChargedAbility("FireBlastCharged", npc, "FireBlastCharged", PK_CONFIG.getDouble("Abilities.Fire.FireBlastCharged.Range"))),

    FIREBURST("FireBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBurst"));
    }, (npc) -> new ChargedAbility("FireBurst", npc, "FireBurst", PK_CONFIG.getDouble("Abilities.Fire.FireBurst.Range"))),

    FIREJET("FireJet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireJet"));
    }, (npc) -> new RangedAbility("FireJet", npc, "FireJet", AvatarDuels.getConfig("FireJet").getDouble("Abilities.Fire.FireJet.Range"))),

    FIREMANIPULATION("FireManipulation", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("FireManipulation"));
    }, (npc) -> new SourcedAbility("FireManipulation", npc, "FireManipulation", PK_CONFIG.getDouble("Abilities.Fire.FireManipulation.Range"),
            PK_CONFIG.getDouble("Abilities.Fire.FireManipulation.SelectRange"), Element.FIRE)),

    FIRESHIELD("FireShield", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireShield"));
    }, (npc) -> new ChargedAbility("FireShield", npc, "FireShield", PK_CONFIG.getDouble("Abilities.Fire.FireShield.Range"))),

    HEATCONTROL("HeatControl", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("HeatControl"));
    }, (npc) -> new SourcedAbility("HeatControl", npc, "HeatControl", AvatarDuels.getConfig("HeatControl").getDouble("Abilities.Fire.HeatControl.Range"),
            AvatarDuels.getConfig("HeatControl").getDouble("Abilities.Fire.HeatControl.SelectRange"), Element.FIRE)),

    ILLUMINATION("Illumination", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Illumination"));
    }, (npc) -> new RangedAbility("Illumination", npc, "Illumination", AvatarDuels.getConfig("Illumination").getDouble("Abilities.Fire.Illumination.Range"))),

    WALLOFFIRE("WallOfFire", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("WallOfFire"));
    }, (npc) -> new SourcedAbility("WallOfFire", npc, "WallOfFire", PK_CONFIG.getDouble("Abilities.Fire.WallOfFire.Range"),
            PK_CONFIG.getDouble("Abilities.Fire.WallOfFire.SelectRange"), Element.FIRE)),

    COMBUSTBEAM("CombustBeam", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("CombustBeam"));
    }, (npc) -> new RangedAbility("CombustBeam", npc, "CombustBeam", AvatarDuels.getConfig("CombustBeam").getDouble("Abilities.Fire.CombustBeam.Range"))),

    COMBUSTION("Combustion", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Combustion"));
    }, (npc) -> new RangedAbility("Combustion", npc, "Combustion", AvatarDuels.getConfig("Combustion").getDouble("Abilities.Fire.Combustion.Range"))),

    DISCHARGE("Discharge", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Discharge"));
    }, (npc) -> new RangedAbility("Discharge", npc, "Discharge", AvatarDuels.getConfig("Discharge").getDouble("Abilities.Fire.Discharge.Range"))),

    EXPLODE("Explode", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Explode"));
    }, (npc) -> new RangedAbility("Explode", npc, "Explode", AvatarDuels.getConfig("Explode").getDouble("Abilities.Fire.Explode.Range"))),

    // WATER ABILITIES



    ICESPIKE("IceSpike", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceSpike"));
    }, (npc) -> new SourcedAbility("IceSpike", npc, "IceSpike", PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range"),
            PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range")/2, Element.WATER)),

    SURGE("Surge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Surge"));
    }, (npc) -> new SourcedAbility("Surge", npc, "Surge", PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.Range"),
            PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.SelectRange"), Element.WATER)),


    FROSTBREATH("FrostBreath", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FrostBreath"));
    }, (npc) -> new RangedAbility("FrostBreath", npc, "FrostBreath", AvatarDuels.getConfig("FrostBreath").getDouble("Abilities.Water.FrostBreath.Range"))),

    HEALINGWATERS("HealingWaters", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("HealingWaters"));
    }, (npc) -> new SourcedAbility("HealingWaters", npc, "HealingWaters", PK_CONFIG.getDouble("Abilities.Water.HealingWaters.Range"),
            PK_CONFIG.getDouble("Abilities.Water.HealingWaters.SelectRange"), Element.WATER)),

    HYDROJET("Hydrojet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Hydrojet"));
    }, (npc) -> new RangedAbility("Hydrojet", npc, "Hydrojet", AvatarDuels.getConfig("Hydrojet").getDouble("Abilities.Water.Hydrojet.Range"))),

    ICECLAWS("IceClaws", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceClaws"));
    }, (npc) -> new SourcedAbility("IceClaws", npc, "IceClaws", PK_CONFIG.getDouble("Abilities.Water.IceClaws.Range"),
            PK_CONFIG.getDouble("Abilities.Water.IceClaws.SelectRange"), Element.WATER)),

    ICEPASSIVE("IcePassive", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IcePassive"));
    }, (npc) -> new ChargedAbility("IcePassive", npc, "IcePassive", PK_CONFIG.getDouble("Abilities.Water.IcePassive.Range"))),

    ICEWALL("IceWall", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("IceWall"));
    }, (npc) -> new RangedAbility("IceWall", npc, "IceWall", AvatarDuels.getConfig("IceWall").getDouble("Abilities.Water.IceWall.Range"))),

    LEAFSTORM("LeafStorm", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("LeafStorm"));
    }, (npc) -> new SourcedAbility("LeafStorm", npc, "LeafStorm", PK_CONFIG.getDouble("Abilities.Water.LeafStorm.Range"),
            PK_CONFIG.getDouble("Abilities.Water.LeafStorm.SelectRange"), Element.WATER)),

    LIGHTNING("Lightning", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Lightning"), ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.Lightning.ChargeTime"));
    }, (npc) -> new ChargedAbility("Lightning", npc, "Lightning", PK_CONFIG.getDouble("Abilities.Fire.Lightning.Range"),
            null)),

    FIREDISC("FireDisc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireDisc"));
    }, (npc) -> new RangedAbility("FireDisc", npc, "FireDisc", AvatarDuels.getConfig("FireDisc").getDouble("Abilities.Fire.FireDisc.Range"))),

    FIREBALL("FireBall", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBall"));
    }, (npc) -> new RangedAbility("FireBall", npc, "FireBall", AvatarDuels.getConfig("").getDouble("Abilities.Fire.FireBall.Range"))),

    FIRECOMET("FireComet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireComet"), AvatarDuels.getConfig("").getLong("Abilities.Fire.FireComet.ChargeUp"));
    }, (npc) -> new ChargedAbility("FireComet", npc, "FireComet", PK_CONFIG.getDouble("Abilities.Fire.FireComet.Range"),
            null));





    private static BukkitScheduler scheduler = Bukkit.getScheduler();

    private String name;

    private AbilityUsageInterface function;

    private AbilityGoalInterface goalInterface;


    AbilityUsages(String name, AbilityUsageInterface function, AbilityGoalInterface goalInterface) {
        this.name = name;
        this.function = function;
        this.goalInterface = goalInterface;
    }

    public String getName() {
        return name;
    }

    public void doFunction(BendingNPC player){
        function.performFunction(player);
    }

    public BendingUseAbility makeGoal(BendingNPC npc){
        return goalInterface.makeGoal(npc);
    }

    public static AbilityUsages fromName(String string){
        return Arrays.stream(values()).filter(abilityUsages -> abilityUsages.name.equals(string)).findAny().get();
    }

}
