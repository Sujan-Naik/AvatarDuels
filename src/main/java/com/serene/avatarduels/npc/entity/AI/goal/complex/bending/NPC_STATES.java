package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum NPC_STATES {


    KEEP_AWAY(20, 0, List.of(AbilityUsages.MUDSURGE, AbilityUsages.FIREBALL, AbilityUsages.FIRECOMET)),

    NEUTRAL(10, 10,  List.of(AbilityUsages.FIREBURST, AbilityUsages.LIGHTNING)),

    RUSHDOWN(5, 15, List.of(AbilityUsages.GALEGUST, AbilityUsages.SONICBLAST, AbilityUsages.SHOCKWAVE, AbilityUsages.AIRSWIPE)),

    POINT_BLANK(2, 20, List.of(AbilityUsages.BLAZE, AbilityUsages.EARTHLINE, AbilityUsages.ACCRETION, AbilityUsages.WALLOFFIRE));

    private double idealRange;

    private double idealHealth;

    private List<AbilityUsages> abilityUsagesList;

    NPC_STATES(double idealRange, double idealHealth,  List<AbilityUsages> abilityUsagesList){
        this.idealRange = idealRange;
        this.idealHealth = idealHealth;
        this.abilityUsagesList = abilityUsagesList;

    }

    public double getIdealRange() {
        return idealRange;
    }

    public double getIdealHealth() {
        return idealHealth;
    }

    public List<AbilityUsages> getAbilityUsagesList() {
        return abilityUsagesList;
    }

    private static final double RANGE_WEIGHT = 1, HEALTH_WEIGHT = 1;

    public static NPC_STATES getBestState(double currentRange, double currentHealth){
        return Arrays.stream(NPC_STATES.values()).min(Comparator.comparingDouble(npcStates -> {
            return Math.abs(currentRange - npcStates.getIdealRange()) * RANGE_WEIGHT +
                    Math.abs(currentHealth - npcStates.getIdealHealth()) * HEALTH_WEIGHT;
        })).get();
    }
}



















