package com.serene.avatarduels.npc;

import com.serene.avatarduels.npc.entity.BendingNPC;

import java.util.HashSet;
import java.util.Set;

public class NPCHandler {

    private static final Set<BendingNPC> npcs = new HashSet<>();


    public static void addNPC(BendingNPC basicNpc) {
        npcs.add(basicNpc);
    }

    public static Set<BendingNPC> getNpcs() {
        return npcs;
    }
}
