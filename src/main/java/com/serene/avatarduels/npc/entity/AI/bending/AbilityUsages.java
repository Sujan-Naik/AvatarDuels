package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.BendingUseAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility.ScooterAbility;
import com.serene.avatarduels.npc.entity.AI.goal.basic.bending.mobility.SourcedMovementAbility;
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

    //Mobility
    AIRBLAST("AirBlast", (player) -> {
        player.getMobilityManager().useAbility(CoreAbility.getAbility("AirBlast"), true, 500, true, true, false, true);
    }, (npc) -> new SourcedMovementAbility("AirBlast", npc, "AirBlast", PK_CONFIG.getDouble("Abilities.Air.AirBlast.Range"))),

    AIRSCOOTER("AirScooter", (player) -> {
        player.getMobilityManager().useAbility(CoreAbility.getAbility("AirScooter"), false, 0, false, false, true, false);
    }, (npc) -> new ScooterAbility("AirScooter", npc, "AirScooter", 10)),


    // AIR ABILITIES
    AIRBREATH("AirBreath", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("AirBreath"), AvatarDuels.getConfig("AirBreath").getLong("Abilities.Air.AirBreath.Duration"));
    }, (npc) -> new ChargedAbility("AirBreath", npc, "AirBreath", AvatarDuels.getConfig("AirBreath").getDouble("Abilities.Air.AirBreath.Range"), null)),



    AIRBURST("AirBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBurst"), PK_CONFIG.getLong("Abilities.Air.AirBurst.ChargeTime"));
    }, (npc) -> new ChargedAbility("AirBurst", npc, "AirBurst", PK_CONFIG.getDouble("Abilities.Air.AirBurst.Range"))),


    AIRPUNCH("AirPunch", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirPunch"), AvatarDuels.getConfig("AirPunch").getInt("Abilities.Air.AirPunch.Shots"));
    }, (npc) -> new RangedAbility("AirPunch", npc, "AirPunch", AvatarDuels.getConfig("AirPunch").getDouble("Abilities.Air.AirPunch.Range"))),

    AIRSHIELD("AirShield", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("AirShield"), (PK_CONFIG.getLong("Abilities.Air.AirShield.Duration") == 0) ? 2000 : PK_CONFIG.getLong("Abilities.Air.AirShield.Duration") );
    }, (npc) -> new ChargedAbility("AirShield", npc, "AirShield", 20)),


    AIRSUCTION("AirSuction", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSuction"), PK_CONFIG.getLong("Abilities.Air.AirSuction.ChargeTime"));
    }, (npc) -> new ChargedAbility("AirSuction", npc, "AirSuction", PK_CONFIG.getDouble("Abilities.Air.AirSuction.Range"))),


    SUFFOCATE("Suffocate", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Suffocate"), PK_CONFIG.getLong("Abilities.Air.Suffocate.ChargeTime") + 5000);
    }, (npc) -> new ChargedAbility("Suffocate", npc, "Suffocate", PK_CONFIG.getDouble("Abilities.Air.Suffocate.Range"))),

    TORNADO("Tornado", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Tornado"), PK_CONFIG.getLong("Abilities.Air.Tornado.Duration"));
    }, (npc) -> new ChargedAbility("Tornado", npc, "Tornado", PK_CONFIG.getDouble("Abilities.Air.Tornado.Range"))),


    // EARTH ABILITIES




//    EARTHARMOR("EarthArmor", (player) -> {
//        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthArmor"));
//    }, (npc) -> new SourcedAbility("EarthArmor", npc, "EarthArmor", PK_CONFIG.getDouble("Abilities.Earth.EarthArmor.Range"),
//            PK_CONFIG.getDouble("Abilities.Earth.EarthArmor.SelectRange"))),

    EARTHBLAST("EarthBlast", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthBlast"));
    }, (npc) -> new SourcedAbility("EarthBlast", npc, "EarthBlast", PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.SelectRange"))),

    EARTHKICK("EarthKick", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthKick"));
    }, (npc) -> new SourcedAbility("EarthKick", npc, "EarthKick", AvatarDuels.getConfig("EarthKick").getDouble("Abilities.Earth.EarthKick.Range"),
            AvatarDuels.getConfig("EarthKick").getDouble("Abilities.Earth.EarthKick.SelectRange"))),

//    FISSURE("Fissure", (player) -> {
//        player.getBlastManager().useAbility(CoreAbility.getAbility("Fissure"));
//    }, (npc) -> new RangedAbility("Fissure", npc, "Fissure", AvatarDuels.getConfig("Fissure").getDouble("Abilities.Earth.Fissure.Range"))),

//    LAVADISC("LavaDisc", (player) -> {
//        player.getSourceManager().useAbility(CoreAbility.getAbility("LavaDisc"));
//    }, (npc) -> new SourcedAbility("LavaDisc", npc, "LavaDisc", 30,
//            AvatarDuels.getConfig("LavaDisc").getDouble("Abilities.Earth.LavaDisc.Source.Range"))),

    LAVAFLUX("LavaFlux", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("LavaFlux"));
    }, (npc) -> new RangedAbility("LavaFlux", npc, "LavaFlux", AvatarDuels.getConfig("LavaFlux").getDouble("Abilities.Earth.LavaFlux.Range"))),

    LAVASURGE("LavaSurge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("LavaSurge"));
    }, (npc) -> new SourcedAbility("LavaSurge", npc, "LavaSurge", 30,
            AvatarDuels.getConfig("LavaSurge").getInt("Abilities.Earth.LavaSurge.SelectRange")) ),

//    LAVATHROW("LavaThrow", (player) -> {
//        player.getSourceManager().useAbility(CoreAbility.getAbility("LavaThrow"));
//    }, (npc) -> new SourcedAbility("LavaThrow", npc, "LavaThrow", AvatarDuels.getConfig("LavaThrow").getDouble("Abilities.Earth.LavaThrow.Range"),
//            AvatarDuels.getConfig("LavaThrow").getDouble("Abilities.Earth.LavaThrow.SelectRange")) ),

    MAGMASLAP("MagmaSlap", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("MagmaSlap"));
    }, (npc) -> new RangedAbility("MagmaSlap", npc, "MagmaSlap", AvatarDuels.getConfig("MagmaSlap").getDouble("Abilities.Earth.MagmaSlap.Range"))),


    METALFRAGMENTS("MetalFragments", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MetalFragments"), 1000L, true);
    }, (npc) -> new SourcedAbility("MetalFragments", npc, "MetalFragments", 20,
            AvatarDuels.getConfig("MetalFragments").getDouble("Abilities.Earth.MetalFragments.SourceRange")) ),

    SANDBLAST("SandBlast", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("SandBlast"));
    }, (npc) -> new SourcedAbility("SandBlast", npc, "SandBlast", AvatarDuels.getConfig("SandBlast").getDouble("Abilities.Earth.SandBlast.Range"),
            AvatarDuels.getConfig("SandBlast").getDouble("Abilities.Earth.SandBlast.SourceRange"))),

    SHOCKWAVE("Shockwave", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Shockwave"), ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Shockwave.ChargeTime"));
    }, (npc) -> new ChargedAbility("Shockwave", npc, "Shockwave", PK_CONFIG.getDouble("Abilities.Earth.Shockwave.Range"), null)),

    ACCRETION("Accretion", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Accretion"));
    }, (npc) -> new SourcedAbility("Accretion", npc, "Accretion", 25,
                                   AvatarDuels.getConfig("Accretion").getDouble("Abilities.Earth.Accretion.SelectRange"))),


    EARTHSHARD("EarthShard", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthShard"));
    }, (npc) -> new SourcedAbility("EarthShard", npc, "EarthShard", AvatarDuels.getConfig("EarthShard").getDouble("Abilities.Earth.EarthShard.AbilityRange"),
            AvatarDuels.getConfig("EarthShard").getDouble("Abilities.Earth.EarthShard.PrepareRange"))),

    MUDSURGE("MudSurge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MudSurge"));
    }, (npc) -> new SourcedAbility("MudSurge", npc, "MudSurge", 40,
            AvatarDuels.getConfig("MudSurge").getDouble("Abilities.Earth.MudSurge.SourceRange"))),

    EARTHLINE("EarthLine", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthLine"));
    }, (npc) -> new SourcedAbility("EarthLine", npc, "EarthLine", AvatarDuels.getConfig("EarthLine").getDouble("Abilities.Earth.EarthLine.Range"),
            AvatarDuels.getConfig("EarthLine").getDouble("Abilities.Earth.EarthLine.PrepareRange"))),


    AIRBLADE("AirBlade", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBlade"));
    }, (npc) -> new RangedAbility("AirBlade", npc, "AirBlade", AvatarDuels.getConfig("AirBlade").getDouble("Abilities.Air.AirBlade.Range"))),


    AIRSWIPE("AirSwipe", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSwipe"));
    }, (npc) -> new RangedAbility("AirSwipe", npc, "AirSwipe", PK_CONFIG.getDouble("Abilities.Air.AirSwipe.Range"))),

    GALEGUST("GaleGust", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("GaleGust"));
    }, (npc) -> new RangedAbility("GaleGust", npc, "GaleGust", AvatarDuels.getConfig("GaleGust").getDouble("Abilities.Air.GaleGust.Range"))),

    SONICBLAST("SonicBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("SonicBlast"),   AvatarDuels.getConfig("SonicBlast").getLong("Abilities.Air.SonicBlast.ChargeTime"));
    }, (npc) -> new ChargedAbility("SonicBlast", npc, "SonicBlast", AvatarDuels.getConfig("SonicBlast").getDouble("Abilities.Air.SonicBlast.Range"),
            null)),


    // FIRE ABILITIES
    ARCSPARK("ArcSpark", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("ArcSpark"), AvatarDuels.getConfig("ArcSpark").getLong("Abilities.Fire.ArcSpark.Duration"), true);
    }, (npc) -> new ChargedAbility("ArcSpark", npc, "ArcSpark", AvatarDuels.getConfig("ArcSpark").getDouble("Abilities.Fire.ArcSpark.Length"), null)),

    BLAZE("Blaze", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Blaze"), PK_CONFIG.getLong("Abilities.Fire.Blaze.ChargeTime"));
    }, (npc) -> new ChargedAbility("Blaze", npc, "Blaze", PK_CONFIG.getDouble("Abilities.Fire.Blaze.Range"))),
    

    FIREBLAST("FireBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"));
    }, (npc) -> new RangedAbility("FireBlast", npc, "FireBlast", PK_CONFIG.getDouble("Abilities.Fire.FireBlast.Range"))),

    FIREBLASTCHARGED("FireBlastCharged", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"), PK_CONFIG.getLong("Abilities.Fire.FireBlast.Charged.ChargeTime"));
    }, (npc) -> new ChargedAbility("FireBlastCharged", npc, "FireBlast", PK_CONFIG.getDouble("Abilities.Fire.FireBlast.Charged.Range"))),
    

    FIREBURST("FireBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBurst"), PK_CONFIG.getLong("Abilities.Fire.FireBurst.ChargeTime"));
    }, (npc) -> new ChargedAbility("FireBurst", npc, "FireBurst", PK_CONFIG.getDouble("Abilities.Fire.FireBurst.Range"))),


    FIRESHIELD("FireShield", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("FireShield"),(PK_CONFIG.getLong("Abilities.Fire.FireShield.Shield.Duration") == 0) ? 2000 : PK_CONFIG.getLong("Abilities.Fire.FireShield.Shield.Duration") );
    }, (npc) -> new ChargedAbility("FireShield", npc, "FireShield", 20)),


    WALLOFFIRE("WallOfFire", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("WallOfFire"));
    }, (npc) -> new RangedAbility("WallOfFire", npc, "WallOfFire", PK_CONFIG.getDouble("Abilities.Fire.WallOfFire.Range"))),

    COMBUSTBEAM("CombustBeam", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("CombustBeam"), AvatarDuels.getConfig("CombustBeam").getLong("Abilities.Fire.CombustBeam.Maximum.ChargeTime"));
    }, (npc) -> new ChargedAbility("CombustBeam", npc, "CombustBeam", AvatarDuels.getConfig("CombustBeam").getDouble("Abilities.Fire.CombustBeam.Range"))),

    COMBUSTION("Combustion", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Combustion"), AvatarDuels.getConfig("Combustion").getLong("Abilities.Fire.Combustion.Warmup"));
    }, (npc) -> new RangedAbility("Combustion", npc, "Combustion", AvatarDuels.getConfig("Combustion").getDouble("Abilities.Fire.Combustion.Range"))),

    DISCHARGE("Discharge", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Discharge"));
    }, (npc) -> new RangedAbility("Discharge", npc, "Discharge", AvatarDuels.getConfig("Discharge").getDouble("Abilities.Fire.Discharge.Range"))),

    EXPLODE("Explode", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("Explode"), 50);
    }, (npc) -> new RangedAbility("Explode", npc, "Explode", AvatarDuels.getConfig("Explode").getDouble("Abilities.Fire.Explode.Range"))),

    LIGHTNING("Lightning", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Lightning"), ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.Lightning.ChargeTime"));
    }, (npc) -> new ChargedAbility("Lightning", npc, "Lightning", PK_CONFIG.getDouble("Abilities.Fire.Lightning.Range"),
            null)),

    FIREDISC("FireDisc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireDisc"));
    }, (npc) -> new RangedAbility("FireDisc", npc, "FireDisc", AvatarDuels.getConfig("FireDisc").getDouble("Abilities.Fire.FireDisc.Range"))),

    FIREBALL("FireBall", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBall"));
    }, (npc) -> new RangedAbility("FireBall", npc, "FireBall", AvatarDuels.getConfig("FireBall").getDouble("Abilities.Fire.FireBall.Range"))),

    FIRECOMET("FireComet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireComet"), AvatarDuels.getConfig("FireComet").getLong("Abilities.Fire.FireComet.ChargeUp"));
    }, (npc) -> new ChargedAbility("FireComet", npc, "FireComet", AvatarDuels.getConfig("FireComet").getDouble("Abilities.Fire.FireComet.Range"),
            null)),




    // WATER ABILITIES



    ICESPIKE("IceSpike", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceSpike"));
    }, (npc) -> new SourcedAbility("IceSpike", npc, "IceSpike", PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range"),
            PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range")/2)),

    SURGE("Surge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Surge"));
    }, (npc) -> new SourcedAbility("Surge", npc, "Surge", PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.Range"),
            PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.SelectRange"))),


    FROSTBREATH("FrostBreath", (player) -> {
        player.getBreathManager().useAbility(CoreAbility.getAbility("FrostBreath"), AvatarDuels.getConfig("FrostBreath").getLong("Abilities.Water.FrostBreath.Slow.Duration"));
    }, (npc) -> new RangedAbility("FrostBreath", npc, "FrostBreath", AvatarDuels.getConfig("FrostBreath").getDouble("Abilities.Water.FrostBreath.Range"))),


    ICECLAWS("IceClaws", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("IceClaws"), 0, true, 1);
    }, (npc) -> new SourcedAbility("IceClaws", npc, "IceClaws", PK_CONFIG.getDouble("Abilities.Water.IceClaws.Range"),
            PK_CONFIG.getDouble("Abilities.Water.IceClaws.SelectRange"))),


    ICEWALL("IceWall", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceWall"));
    }, (npc) -> new SourcedAbility("IceWall", npc, "IceWall", AvatarDuels.getConfig("IceWall").getInt("Abilities.Water.IceWall.Range")
            , AvatarDuels.getConfig("IceWall").getInt("Abilities.Water.IceWall.Range"))),


    TORRENT("Torrent", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Torrent"), 1000, true);
    }, (npc) -> new SourcedAbility("Torrent", npc, "Torrent", PK_CONFIG.getDouble("Abilities.Water.Torrent.Range"),
            PK_CONFIG.getDouble("Abilities.Water.Torrent.SelectRange"))),

    WATERMANIPULATION("WaterManipulation", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("WaterManipulation"));
    }, (npc) -> new SourcedAbility("WaterManipulation", npc, "WaterManipulation", PK_CONFIG.getDouble("Abilities.Water.WaterManipulation.Range"),
            PK_CONFIG.getDouble("Abilities.Water.WaterManipulation.SelectRange")));




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
