package com.serene.avatarduels.npc.entity.AI.goal.basic.movement;

import com.serene.avatarduels.npc.entity.AI.goal.interfaces.EntityInteraction;
import com.serene.avatarduels.npc.entity.BendingNPC;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CircleEntity extends Movement implements EntityInteraction {

    private LivingEntity targetEntity;
    private double radius; // The distance to maintain from the target
    private double angularSpeed; // Speed of circling in radians
    private double angle; // Current angle in the circular path

    public CircleEntity(String name, BendingNPC npc, int priority, double requiredDistance, double angularSpeed, LivingEntity targetEntity) {
        super(name, npc, priority, null, requiredDistance); // Initialize with null for goalPos
        this.targetEntity = targetEntity;
        this.radius = requiredDistance; // Associate radius with requiredDistance for position updates
        this.angularSpeed = angularSpeed;
        this.angle = 0; // Initialize angle to 0
    }

    private void updatePos() {
        if (targetEntity == null || !targetEntity.isAlive()) {
            // If the target entity is not alive or null, do not update the position
            return; // Leave the last valid goalPos set
        }

        // Calculate the new position in a circular path around the target
        angle += angularSpeed; // Update the angle

        // Use trigonometric functions to calculate the new position on the circle
        double targetX = targetEntity.getX() + radius * Math.cos(angle);
        double targetZ = targetEntity.getZ() + radius * Math.sin(angle);
        double targetY = targetEntity.getY(); // Maintain the same vertical position

        // Create the new goal position Vec3
        Vec3 newGoalPos = new Vec3(targetX, targetY, targetZ);

        // Set the goal position for the NPC to move toward
        this.setGoalPos(newGoalPos); // Continuously set the new goal position
    }

    @Override
    public LivingEntity getEntity() {
        return targetEntity; // Return the target entity being circled
    }

    @Override
    public void tick() {
        this.updatePos(); // Update position continuously

        // Call superclass tick to handle movement logic
        super.tick(); // The superclass will manage the goal's persistence
    }
}