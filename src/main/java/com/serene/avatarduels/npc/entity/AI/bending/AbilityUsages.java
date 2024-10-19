package com.serene.avatarduels.npc.entity.AI.bending;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.configuration.AvatarDuelsConfig;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

import static com.projectkorra.projectkorra.ability.CoreAbility.getConfig;
import static com.projectkorra.projectkorra.object.Preset.config;

public enum AbilityUsages {

    TORRENT("Torrent", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Torrent"), 1000, true);
    }),

    ICESPIKE("IceSpike", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("IceSpike"));
    }),

    SURGE("Surge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Surge"));
    }),

    WATERMANIPULATION("WaterManipulation", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("WaterManipulation"));
    }),


    EARTHBLAST("EarthBlast", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthBlast"));
    }),

    EARTHSHARD("EarthShard", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthShard"));
    }),

    MUDSURGE("MudSurge", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("MudSurge"));
    }),

    EARTHLINE("EarthLine", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("EarthLine"));
    }),

    SHOCKWAVE("Shockwave", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Shockwave"), ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Shockwave.ChargeTime"));
    }),

    ACCRETION("Accretion", (player) -> {
        player.getSourceManager().useAbility(CoreAbility.getAbility("Accretion"));
    }),

    AIRBLADE("AirBlade", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirBlade"));
    }),


    AIRSWIPE("AirSwipe", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("AirSwipe"));
    }),

    GALEGUST("GaleGust", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("GaleGust"));
    }),

    SONICBLAST("SonicBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("SonicBlast"),  ProjectKorra.plugin.getConfig().getLong("Abilities.Air.SonicBlast.ChargeTime"));
    }),


    BLAZE("Blaze", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Blaze"));
    }),

    FIREBLAST("FireBlast", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"));
    }),

//    FIREBLASTCHARGED("FireBlast", (player) -> {
//        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBlast"), getConfig().getLong("Abilities.Fire.FireBlast.Charged.ChargeTime"));
//    }),


    FIRESHOTS("FireShots", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireShots"), 0, true, AvatarDuelsConfig.getConfig(Bukkit.getPlayer(player.getUUID())).getInt("Abilities.Fire.FireShots.FireBalls"));
    }),

    FIREBURST("FireBurst", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBurst"),  ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.FireBurst.ChargeTime") + 2000, true, true, 1);
    }),

    WALLOFFIRE("WallOfFire", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("WallOfFire"));
    }),


    LIGHTNING("Lightning", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("Lightning"), ProjectKorra.plugin.getConfig().getLong("Abilities.Fire.Lightning.ChargeTime"));
    }),

    FIREDISC("FireDisc", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireDisc"));
    }),

    FIREBALL("FireBall", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireBall"));
    }),

    FIRECOMET("FireComet", (player) -> {
        player.getBlastManager().useAbility(CoreAbility.getAbility("FireComet"), AvatarDuelsConfig.getConfig(Bukkit.getPlayer(player.getUUID())).getLong("Abilities.Fire.FireComet.ChargeUp"));
    });

//    CHARGEBOLT("ChargeBolt", (player) -> {
//        player.getBlastManager().useAbility(CoreAbility.getAbility("ChargeBolt"), 0, true, AvatarDuels.instance.getConfig("ChargeBolt").getInt("Abilities.Fire.ChargeBolt.DischargeBoltCount"));
//    });

    private static BukkitScheduler scheduler = Bukkit.getScheduler();

    private String name;

    private AbilityUsageInterface function;


    AbilityUsages(String name, AbilityUsageInterface function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void doFunction(BendingNPC player){
        function.performFunction(player);
    }

    public static AbilityUsages fromName(String string){
        return Arrays.stream(values()).filter(abilityUsages -> abilityUsages.name.equals(string)).findAny().get();
    }

}
