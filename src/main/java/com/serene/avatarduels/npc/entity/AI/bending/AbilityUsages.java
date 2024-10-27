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
import static com.serene.avatarduels.AvatarDuels.JC_CONFIG;
import static com.serene.avatarduels.AvatarDuels.PK_CONFIG;

public enum AbilityUsages {


    TORRENT("Torrent", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Torrent"), 1000, true);
    }, (npc) -> new SourcedAbility("Torrent", npc, "Torrent", PK_CONFIG.getDouble("Abilities.Water.Torrent.Range"),
            PK_CONFIG.getDouble("Abilities.Water.Torrent.SelectRange"), Element.WATER)),

    ICESPIKE("IceSpike", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceSpike"));
    }, (npc) -> new SourcedAbility("IceSpike", npc, "IceSpike", PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range"),
            PK_CONFIG.getDouble("Abilities.Water.IceSpike.Range")/2, Element.WATER)),

    SURGE("Surge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Surge"));
    }, (npc) -> new SourcedAbility("Surge", npc, "Surge", PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.Range"),
            PK_CONFIG.getDouble("Abilities.Water.Surge.Wave.SelectRange"), Element.WATER)),

    WATERMANIPULATION("WaterManipulation", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("WaterManipulation"));
    }, (npc) -> new SourcedAbility("WaterManipulation", npc, "WaterManipulation", PK_CONFIG.getDouble("Abilities.Water.WaterManipulation.Range"),
            PK_CONFIG.getDouble("Abilities.Water.WaterManipulation.SelectRange"), Element.WATER)),


    EARTHBLAST("EarthBlast", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthBlast"));
    }, (npc) -> new SourcedAbility("EarthBlast", npc, "EarthBlast", PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.Range"),
            PK_CONFIG.getDouble("Abilities.Earth.EarthBlast.SelectRange"), Element.EARTH)),

    EARTHSHARD("EarthShard", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthShard"));
    }, (npc) -> new SourcedAbility("EarthShard", npc, "EarthShard", JC_CONFIG.getDouble("Abilities.Earth.EarthShard.AbilityRange"),
            JC_CONFIG.getDouble("Abilities.Earth.EarthShard.PrepareRange"), Element.EARTH)),

    MUDSURGE("MudSurge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MudSurge"));
    }, (npc) -> new SourcedAbility("MudSurge", npc, "MudSurge", 40,
            JC_CONFIG.getDouble("Abilities.Earth.MudSurge.SourceRange"), Element.EARTH)),

    EARTHLINE("EarthLine", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthLine"));
    }, (npc) -> new SourcedAbility("EarthLine", npc, "EarthLine", JC_CONFIG.getDouble("Abilities.Earth.EarthLine.Range"),
            JC_CONFIG.getDouble("Abilities.Earth.EarthLine.PrepareRange"), Element.EARTH)),

    SHOCKWAVE("Shockwave", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Shockwave"), ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Shockwave.ChargeTime"));
    }, (npc) -> new ChargedAbility("Shockwave", npc, "Shockwave", PK_CONFIG.getDouble("Abilities.Earth.Shockwave.Range"),
            null)),

    ACCRETION("Accretion", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Accretion"));
    }, (npc) -> new SourcedAbility("Accretion", npc, "Accretion", 25,
            AvatarDuels.plugin.getConfig("Accretion").getDouble("Abilities.Earth.Accretion.SelectRange"), Element.EARTH)),

    AIRBLADE("AirBlade", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBlade"));
    }, (npc) -> new RangedAbility("AirBlade", npc, "AirBlade", JC_CONFIG.getDouble("Abilities.Air.AirBlade.Range"))),


    AIRSWIPE("AirSwipe", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSwipe"));
    }, (npc) -> new RangedAbility("AirSwipe", npc, "AirSwipe", JC_CONFIG.getDouble("Abilities.Air.AirSwipe.Range"))),

    GALEGUST("GaleGust", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("GaleGust"));
    }, (npc) -> new RangedAbility("GaleGust", npc, "GaleGust", AvatarDuels.plugin.getConfig("GaleGust").getDouble("Abilities.Air.GaleGust.Range"))),

    SONICBLAST("SonicBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("SonicBlast"),  ProjectKorra.plugin.getConfig().getLong("Abilities.Air.SonicBlast.ChargeTime"));
    }, (npc) -> new ChargedAbility("SonicBlast", npc, "SonicBlast", PK_CONFIG.getDouble("Abilities.Air.SonicBlast.Range"),
            null)),

    BLAZE("Blaze", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Blaze"));
    }, (npc) -> new RangedAbility("Blaze", npc, "Blaze", PK_CONFIG.getDouble("Abilities.Fire.Blaze.Range"))),

    FIREBLAST("FireBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"));
    }, (npc) -> new RangedAbility("FireBlast", npc, "FireBlast", PK_CONFIG.getDouble("Abilities.Fire.FireBlast.Range"))),

//    FIREBLASTCHARGED("FireBlast", (player) -> {
//        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"), getConfig().getLong("Abilities.Fire.FireBlast.Charged.ChargeTime"));
//    }),


    FIRESHOTS("FireShots", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireShots"), 0, true, AvatarDuelsConfig.getConfig(Bukkit.getPlayer(player.getUUID())).getInt("Abilities.Fire.FireShots.FireBalls"));
    }, (npc) -> new RangedAbility("AirBlade", npc, "AirBlade", JC_CONFIG.getDouble("Abilities.Air.AirBlade.Range"))),

    FIREBURST("FireBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBurst"),  ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.FireBurst.ChargeTime") + 2000, true, true, 1);
    }, (npc) -> new ChargedAbility("FireBurst", npc, "FireBurst", PK_CONFIG.getDouble("Abilities.Fire.FireBurst.Range"),
            null)),
    WALLOFFIRE("WallOfFire", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("WallOfFire"));
    }, (npc) -> new RangedAbility("WallOfFire", npc, "WallOfFire", PK_CONFIG.getDouble("Abilities.Fire.WallOfFire.Range"))),


    LIGHTNING("Lightning", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Lightning"), ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.Lightning.ChargeTime"));
    }, (npc) -> new ChargedAbility("Lightning", npc, "Lightning", PK_CONFIG.getDouble("Abilities.Fire.Lightning.Range"),
            null)),

    FIREDISC("FireDisc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireDisc"));
    }, (npc) -> new RangedAbility("FireDisc", npc, "FireDisc", AvatarDuels.plugin.getConfig("FireDisc").getDouble("Abilities.Fire.FireDisc.Range"))),

    FIREBALL("FireBall", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBall"));
    }, (npc) -> new RangedAbility("FireBall", npc, "FireBall", JC_CONFIG.getDouble("Abilities.Fire.FireBall.Range"))),

    FIRECOMET("FireComet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireComet"), JC_CONFIG.getLong("Abilities.Fire.FireComet.ChargeUp"));
    }, (npc) -> new ChargedAbility("FireComet", npc, "FireComet", PK_CONFIG.getDouble("Abilities.Fire.FireComet.Range"),
            null));

//    CHARGEBOLT("ChargeBolt", (player) -> {
//        player.getBlastManager().useAbility(CoreAbility.getAbility("ChargeBolt"), 0, true, AvatarDuels.instance.getConfig("ChargeBolt").getInt("Abilities.Fire.ChargeBolt.DischargeBoltCount"));
//    });



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
