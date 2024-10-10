package com.serene.avatarduels.collision;

import org.bukkit.util.Vector;

public interface Collider {
    boolean intersects(AABB aabb);
    boolean intersects(Sphere sphere);
    Vector getPosition();
    Vector getHalfExtents();

    boolean contains(Vector point);
}
