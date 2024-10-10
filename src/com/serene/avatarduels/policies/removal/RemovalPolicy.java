package com.serene.avatarduels.policies.removal;

import org.bukkit.configuration.ConfigurationSection;

public interface RemovalPolicy {
    boolean shouldRemove();

    default void load(ConfigurationSection config) { }
    String getName();
}

