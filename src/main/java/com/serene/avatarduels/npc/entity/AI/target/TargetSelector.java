package com.serene.avatarduels.npc.entity.AI.target;

import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.PriorityQueue;

public class TargetSelector {

    private PriorityQueue<Entity> hostileEntityStack;

    private PriorityQueue<Entity> peacefulEntityStack;

    private PriorityQueue<Player> players;

    private LivingEntity currentTarget;

    public LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(LivingEntity currentTarget) {
        this.currentTarget = currentTarget;
    }

    private HumanEntity npc;
    private long time = 0;

    public TargetSelector(HumanEntity owner) {
        this.npc = owner;
        this.hostileEntityStack = new PriorityQueue<>((a, b) -> (int) (npc.distanceToSqr(a) - npc.distanceToSqr(b)));
        this.peacefulEntityStack = new PriorityQueue<>((a, b) -> (int) (npc.distanceToSqr(a) - npc.distanceToSqr(b)));
        this.players = new PriorityQueue<>((a, b) -> (int) (npc.distanceToSqr(a) - npc.distanceToSqr(b)));
    }

    public boolean noHostile() {
        return hostileEntityStack.isEmpty();
    }

    public Entity retrieveTopHostile() {
        if (noHostile()) {
            return null;
        }
        return hostileEntityStack.poll();
    }

    public boolean noPeaceful() {
        return peacefulEntityStack.isEmpty();

    }

    public Entity retrieveTopPeaceful() {
        if (noPeaceful()) {
            return null;
        }
        return peacefulEntityStack.poll();
    }

    public boolean noPlayer() {
        return players.isEmpty();
    }

    public Player retrieveTopPlayer() {
        if (noPlayer()) {
            return null;
        }
        return players.poll();
    }

    public void tick() {
        if (System.currentTimeMillis() - time > 500) {
            time = System.currentTimeMillis();
            for (Entity entity : npc.level().getEntities(npc, new AABB(npc.getOnPos()).inflate(100))) {

                if (entity instanceof Player player) {
                    players.add(player);
                } else if (entity instanceof Monster monster) {
                    hostileEntityStack.add(monster);
                } else if (entity instanceof Animal animal) {
                    peacefulEntityStack.add(animal);
                }
            }
        }

    }
}
