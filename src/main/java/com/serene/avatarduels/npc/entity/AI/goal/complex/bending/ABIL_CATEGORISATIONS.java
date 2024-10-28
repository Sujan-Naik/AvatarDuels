package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;

import java.util.ArrayList;
import java.util.List;

import static com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages.*;
import static com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages.WALLOFFIRE;

public class ABIL_CATEGORISATIONS {

    public static final List<AbilityUsages> HARMLESS = List.of(AIRBREATH, AIRBURST, AIRSHIELD, AIRSUCTION, TORNADO, EXPLODE,
            SURGE, ICEWALL);
    public static final List<AbilityUsages> CLOSE_ONLY = List.of(SUFFOCATE, AIRSWIPE, BLAZE, FROSTBREATH);
    public static final List<AbilityUsages> PUNCH_PROJECTILE = List.of(AIRPUNCH, EARTHBLAST, EARTHLINE, AIRBLADE, FIREBLAST, DISCHARGE, FIREDISC,
            FIREBALL, ICESPIKE, ICECLAWS, WATERMANIPULATION);
    public static final List<AbilityUsages> YEETER = List.of(LAVASURGE, MAGMASLAP, SANDBLAST, MUDSURGE, GALEGUST, TORRENT);
    public static final List<AbilityUsages> SHOTGUN = List.of(EARTHKICK, EARTHBLAST, LAVAFLUX, ACCRETION);
    public static final List<AbilityUsages> SNIPER = List.of(METALFRAGMENTS, EARTHSHARD, LIGHTNING, COMBUSTION);
    public static final List<AbilityUsages> DOOM = List.of(SHOCKWAVE, FIREBURST, COMBUSTBEAM, FIRECOMET);
    public static final List<AbilityUsages> CHUNKY = List.of(SONICBLAST, FIREBLASTCHARGED );
    public static final List<AbilityUsages> SHREDDER = List.of(ARCSPARK);
    public static final List<AbilityUsages> SHIELD = List.of(FIRESHIELD, WALLOFFIRE);
    public static final List<AbilityUsages> MOVEMENT = List.of(AIRBLAST, CATAPULT, FIREJET, EARTHSURF, AIRSCOOTER);


    public static List<AbilityUsages> mergeCategories(List<AbilityUsages>... categories) {
        List<AbilityUsages> mergedList = new ArrayList<>();

        for (List<AbilityUsages> list : categories) {
            if (list != null) { // Check for null to avoid NullPointerException
                mergedList.addAll(list);
            }
        }

        return mergedList;
    }
}
