package com.serene.avatarduels.util;

import com.serene.avatarduels.AvatarDuels;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class FireTick {
    private static Map<String, FireTickMethod> methods = new HashMap<>();
    private static FireTickMethod method = new OverwriteFireTickMethod();

    static {
        methods.put("overwrite", new OverwriteFireTickMethod());
        methods.put("larger", new LargerFireTickMethod());
        methods.put("accumulate", new AccumulateFireTickMethod());
    }

    public static void set(Entity target, int amount) {
        method.set(target, amount);
    }

    public static void loadMethod() {
        String methodName = AvatarDuels.instance.getConfig().getString("Properties.FireTickMethod");

        FireTickMethod newMethod = methods.get(methodName.toLowerCase());
        if (newMethod != null) {
            AvatarDuels.logDebug("Using " + methodName + " as FireTickMethod.");
            method = newMethod;
        } else {
            AvatarDuels.plugin.getLogger().warning(methodName + " not a known FireTickMethod. Defaulting to larger method.");
            method = new LargerFireTickMethod();
        }
    }

    private interface FireTickMethod {
        void set(Entity entity, int amount);
    }

    // Always set the target's fire tick to the new amount.
    private static class OverwriteFireTickMethod implements FireTickMethod {
        @Override
        public void set(Entity entity, int amount) {
            entity.setFireTicks(amount);
        }
    }

    // Only overwrite target's fire tick if the new fire tick is larger.
    private static class LargerFireTickMethod implements FireTickMethod {
        @Override
        public void set(Entity entity, int amount) {
            if (entity.getFireTicks() < amount) {
                entity.setFireTicks(amount);
            }
        }
    }

    // Increase the target's fire tick instead of overwriting.
    private static class AccumulateFireTickMethod implements FireTickMethod {
        @Override
        public void set(Entity entity, int amount) {
            entity.setFireTicks(entity.getFireTicks() + amount);
        }
    }
}
