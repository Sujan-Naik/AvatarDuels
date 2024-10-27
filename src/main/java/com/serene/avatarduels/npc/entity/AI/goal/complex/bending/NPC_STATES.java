package com.serene.avatarduels.npc.entity.AI.goal.complex.bending;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.serene.avatarduels.npc.entity.AI.bending.AbilityUsages.*;
import static com.serene.avatarduels.npc.entity.AI.goal.complex.bending.ABIL_CATEGORISATIONS.*;

public enum NPC_STATES {




    KEEP_AWAY(25, 0, mergeCategories(SNIPER, DOOM)),

    NEUTRAL(20, 10, mergeCategories(CHUNKY, DOOM, SNIPER) ),

    RUSHDOWN(15, 15, mergeCategories(SHIELD, SHREDDER, YEETER, PUNCH_PROJECTILE, HARMLESS) ),

    POINT_BLANK(5, 20, mergeCategories(SHOTGUN, PUNCH_PROJECTILE, CLOSE_ONLY));

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

    private static final double RANGE_WEIGHT = 1.2, HEALTH_WEIGHT = 1;

    public static NPC_STATES getBestState(double currentRange, double currentHealth){
        return Arrays.stream(NPC_STATES.values()).min(Comparator.comparingDouble(npcStates -> {
            return Math.abs(currentRange - npcStates.getIdealRange()) * RANGE_WEIGHT +
                    Math.abs(currentHealth - npcStates.getIdealHealth()) * HEALTH_WEIGHT;
        })).get();
    }
}



















