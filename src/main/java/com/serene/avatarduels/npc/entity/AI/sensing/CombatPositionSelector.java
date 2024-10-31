package com.serene.avatarduels.npc.entity.AI.sensing;

import com.serene.avatarduels.npc.entity.BendingNPC;
import com.serene.avatarduels.npc.utils.PacketUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class CombatPositionSelector {

    private LivingEntity livingEntity;

    private BendingNPC npc;

    private Level level;


    private Set<Vec3> chunkGrid;

    private long sinceLastPathRefresh;



    public CombatPositionSelector(BendingNPC npc, LivingEntity livingEntity){
        this.npc = npc;
        this.livingEntity = livingEntity;

        this.level = npc.level();

        chunkGrid = createChunkGrid();

        sinceLastPathRefresh = npc.tickCount;
//        lastDistanceFromEntity = livingEntity.distanceToSqr(npc);
    }

    private void navigateIfChanged(Vec3 pos){
        if (npc.getNavigation().getGoalPos() != pos){
            npc.getNavigation().navigateToPos(pos);
        }
    }

    public void refreshChunkGrid(){
        chunkGrid = createChunkGrid();

//        Bukkit.broadcastMessage(String.valueOf(chunkGrid.size()));
//        chunkGrid.forEach(vec3 -> {
//            System.out.println(vec3);
//        });

    }

    private Set<Vec3> createChunkGrid(){

        Set<Vec3> bestChunkGridPositions = new HashSet<>();
        int npcChunkX = level.getChunkAt(npc.getOnPos()).locX;
        int npcChunkZ = level.getChunkAt(npc.getOnPos()).locZ;

        int targetChunkX = level.getChunkAt(livingEntity.getOnPos()).locX;
        int targetChunkZ = level.getChunkAt(livingEntity.getOnPos()).locZ;

        // Get the minimum and maximum chunk coordinates
        int minX = Math.min(npcChunkX, targetChunkX);
        int maxX = Math.max(npcChunkX, targetChunkX);
        int minZ = Math.min(npcChunkZ, targetChunkZ);
        int maxZ = Math.max(npcChunkZ, targetChunkZ);


        // Iterate through the X range of chunks
        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            // Iterate through the Z range of chunks
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                // Now iterate through the blocks within each chunk (0 to 15)

                double bestScore = Double.NEGATIVE_INFINITY;


                Vec3 bestPos = null;
                Heightmap heightmap = Heightmap.getOrCreateHeightmapUnprimed( level.getChunk(chunkX, chunkZ), Heightmap.Types.MOTION_BLOCKING_NO_BARRIERS);
                int meanHeight = calculateMeanHeight(heightmap);

                double stdDev = calculateStandardDeviation(heightmap, meanHeight);

                for (int blockX = 0; blockX < 16; blockX++) {
                    for (int blockZ = 0; blockZ < 16; blockZ++) {

                        int height = heightmap.getFirstAvailable(blockX, blockZ); // Get height at position (blockX, blockZ)

                        // Calculate score based on flatness metric
                        double score = -Math.abs(height - meanHeight) + stdDev; // You can adjust this formula
                        // Update best point
                        if (score > bestScore) {
                            bestScore = score;
                            bestPos = new Vec3(blockX + chunkX * CHUNK_SIZE, heightmap.getFirstAvailable(blockX, blockZ), blockZ + chunkZ * CHUNK_SIZE);
                        }
                    }
                }

                if (bestPos != null ) {
                    bestChunkGridPositions.add(bestPos);
                }
            }
        }
        return bestChunkGridPositions;
    }


    private static final int CHUNK_SIZE = 16;

    private static int calculateMeanHeight(Heightmap heightmap) {
        int sum = 0;
        int count = 0;

        // Iterate through all blocks in the chunk (0 to 15 for both X and Z)
        for (int blockX = 0; blockX < CHUNK_SIZE; blockX++) {
            for (int blockZ = 0; blockZ < CHUNK_SIZE; blockZ++) {
                int height = heightmap.getFirstAvailable(blockX, blockZ);
                sum += height;
                count++;
            }
        }

        return count > 0 ? sum / count : 0; // Return zero if no valid heights
    }


    private static double calculateStandardDeviation(Heightmap heightmap, int meanHeight) {
        double sumSqDiff = 0;
        int count = 0;

        // Iterate through all blocks in the chunk (0 to 15 for both X and Z)
        for (int blockX = 0; blockX < CHUNK_SIZE; blockX++) {
            for (int blockZ = 0; blockZ < CHUNK_SIZE; blockZ++) {
                int height = heightmap.getFirstAvailable(blockX, blockZ);
                sumSqDiff += Math.pow(height - meanHeight, 2);
                count++;
            }
        }

        return count > 0 ? Math.sqrt(sumSqDiff / count) : 0; // Return zero if no valid heights
    }






    private static final int PATH_REFRESH_CD = 1200;

//    private double lastDistanceFromEntity;


    private List<Vec3> path = new ArrayList<>();

    private Vec3 currentNavigatingPos;
    public boolean tick(){
        if (!shouldContinue()){
            Bukkit.broadcastMessage("no continue");
            return false;
        } else {

            if (npc.tickCount - sinceLastPathRefresh > PATH_REFRESH_CD){
                this.refreshChunkGrid();
                sinceLastPathRefresh = npc.tickCount;
//                path = computePath();

//                if (lastDistanceFromEntity < npc.distanceTo(livingEntity)){
//                    if (!path.isEmpty()){
//                        currentNavigatingPos = path.removeFirst();
//                        navigateIfChanged(currentNavigatingPos);
//                        lastDistanceFromEntity = npc.distanceTo(livingEntity);
//                    }
//                }

            }
            if (!path.isEmpty()) {
                if (currentNavigatingPos != null){
                    Vec3 currentNavigatingPosY0 = currentNavigatingPos.multiply(1,0,1);
                    Vec3 npcPosY0 = npc.getPosition(0).multiply(1,0,1);
                    Vec3 targetPosY0 = livingEntity.getPosition(0).multiply(1,0,1);

                    double npcDistSqrToCurrentPos = npcPosY0.distanceToSqr(currentNavigatingPosY0);
                    double npcDistSqrToEnemy = npcPosY0.distanceToSqr(targetPosY0);
                    double currentPosDistSqrToEnemy = currentNavigatingPosY0.distanceToSqr(targetPosY0);

                    if (npcDistSqrToCurrentPos < CHUNK_SIZE * CHUNK_SIZE || npcDistSqrToEnemy < currentPosDistSqrToEnemy){

                        currentNavigatingPos = path.removeFirst();
                        Vec3 relative = new Vec3(currentNavigatingPos.x()-npcPosY0.x(), 69 , currentNavigatingPos.z()-npcPosY0.z() );
                        if (!npc.hasClearRayRelative(relative)){
                            currentNavigatingPos = npc.getBlockingPos(currentNavigatingPos);
                        }

                        navigateIfChanged(currentNavigatingPos);
                    }
                } else {
                    currentNavigatingPos = path.removeFirst();

                }
//                navigateIfChanged(currentNavigatingPos);

//                Bukkit.broadcastMessage("Dijkstra'ing off into oblivion");
            } else {
                this.refreshChunkGrid();
                path = computePath();

                Bukkit.broadcastMessage("no Dijkce");
                return false;
            }

            return true;
        }
    }

    public List<Vec3> computePath() {
        Vec3 target = livingEntity.getPosition(0);
        // Implement Dijkstra's Algorithm
        return dijkstra(npc.getPosition(0), target);
    }

    private Vec3 getLowestYAdjustedGoalPos(Vec3 startPos, Vec3 goalPos){
        Vec3 adjustedGoalPos = goalPos;

        int maximumIterations = (int) (384 - goalPos.y());

        do {
            adjustedGoalPos = adjustedGoalPos.add(0, 1, 0);
            maximumIterations--;
        } while (!npc.hasClearRay(startPos.add(0,2,0), adjustedGoalPos) && maximumIterations > 0);

        if (maximumIterations < 0) {
//            Bukkit.broadcastMessage("no clear goalPos");
            return null;
        }

        return adjustedGoalPos;
    }


    private Set<Vec3> getNeighbours(Vec3 current){

//        return chunkGrid.stream().filter(vec3 -> npc.hasClearRay(vec3, current) ).collect(Collectors.toSet());
        return chunkGrid.stream().map(vec3 -> getLowestYAdjustedGoalPos(vec3, current)).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private List<Vec3> dijkstra(Vec3 start, Vec3 target) {
        // Priority queue to store (cost, Vec3)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost));
        Map<Vec3, Double> costMap = new HashMap<>();
        Map<Vec3, Vec3> cameFrom = new HashMap<>();
        List<Vec3> path = new ArrayList<>();
//        Vec3 target = chunkGrid.stream().min(Comparator.comparingDouble(o -> o(actualTarget)).get();


        costMap.put(start, 0.0);
        openSet.add(new Node(start, 0));


        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.position.multiply(1,0,1).distanceToSqr(target.multiply(1,0,1)) < CHUNK_SIZE * CHUNK_SIZE) {
                // Rebuild path
                return reconstructPath(cameFrom, current.position);
            }

            // Visit each neighbor
//            for (Vec3 neighbor : getNeighbours(current.position)) {
            for (Vec3 neighbor : chunkGrid) {
//                if (isSolid(npc.getBlockingPos(neighbor.add(0,50,0), neighbor.subtract(0,50,0)))) {
                    double newCost = costMap.get(current.position) + current.position.distanceTo(neighbor);
                    costMap.putIfAbsent(neighbor, Double.POSITIVE_INFINITY);
                    if (newCost < costMap.get(neighbor)) {
                        costMap.put(neighbor, newCost);
                        cameFrom.put(neighbor, current.position);
                        openSet.add(new Node(neighbor, newCost));
                    }
//                }
            }

            if (openSet.isEmpty()){
                Bukkit.broadcastMessage("this isn't the best path but it gets you " + current.position.multiply(1,0,1).distanceToSqr(target.multiply(1,0,1)) );
                return reconstructPath(cameFrom, current.position);

            }
        }
        return path; // Return empty if no path is found
    }

    private List<Vec3> reconstructPath(Map<Vec3, Vec3> cameFrom, Vec3 target) {
        List<Vec3> path = new ArrayList<>();
        Vec3 current = target;

        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }

        Collections.reverse(path); // Reverse path to get from start to target
        return path;
    }

    // Internal node class to store position and cost
    private static class Node {
        Vec3 position;
        double cost;

        Node(Vec3 position, double cost) {
            this.position = position;
            this.cost = cost;
        }
    }



    private Vec3 bypassObstacle(){
        if (findFirstObstacleInPath() != null){
            Vec3 bypassPos = findNewPositionAroundObstacle(findFirstObstacleInPath());
            return bypassPos;
        } else {
            Bukkit.broadcastMessage("first obstacle is null");
            return null;
        }
    }

    private Vec3 findNewPositionAroundObstacle(Vec3 obstaclePos) {
        Vec3 currentPos = npc.getPosition(0);  // Get the current position of the NPC
        float[] directions = {0, 90, 180, 270}; // Degrees to check
        float searchRadius = 0.1f;  // Initial search radius
        float maxSearchRadius = 2.0f; // Define a maximum search radius to avoid infinite loops

        // Variable to store the potential new position
        Vec3 trialNewPos;

        for (float angle : directions) {
            int iteration = 0; // Count iterations to limit the radial search
            do {
                // Calculate the trial position around the obstacle
                trialNewPos = obstaclePos.add(npc.getForward().yRot((float) Math.toRadians(angle)).normalize().scale(searchRadius));

                // Check for a clear path from the current position to the new position
                if (npc.hasClearRay(trialNewPos)) {
                    return trialNewPos; // Return the first valid position found
                }

                // Increase the radius for the next iteration
                searchRadius += 0.1f; // Increment the search radius
                iteration++;
            } while (searchRadius <= maxSearchRadius && iteration < 10);
        }

        // If no clear positions are found, return the original position or handle accordingly
        return currentPos; // Fallback to returning the current position
    }

    private Vec3 findFirstObstacleInPath(){
        return npc.getBlockingPos(livingEntity.getPosition(0));
    }


    private boolean shouldContinue(){
        // Assume npc and livingEntity are already defined and initialized

// Check if npc is null
        if (npc == null) {
            Bukkit.broadcastMessage("NPC is null");
        }

// Check if livingEntity is null
        if (livingEntity == null) {
            Bukkit.broadcastMessage("LivingEntity is null");
        }

// Check if npc is alive
        if (!npc.isAlive()) {
            Bukkit.broadcastMessage("NPC is not alive");
        }

// Check if livingEntity is alive
        if (!livingEntity.isAlive()) {
            Bukkit.broadcastMessage("LivingEntity is not alive");
        }

// Check if the current target of the npc is not the livingEntity
        if (npc.getTargetSelector().getCurrentTarget() != livingEntity) {
            Bukkit.broadcastMessage("NPC's current target is not the LivingEntity");
        }

// Finally, if all checks pass, you can return the result
        boolean result = (npc != null && livingEntity != null && npc.isAlive() && livingEntity.isAlive()
                && npc.getTargetSelector().getCurrentTarget() == livingEntity);
        return result;


//        return (npc == null || livingEntity == null || !npc.isAlive() || !livingEntity.isAlive()
//        || npc.getTargetSelector().getCurrentTarget() != livingEntity);
    }

//    private boolean isSolid(Vec3 pos){
//        return !npc.level().getBlockState(BlockPos.containing(pos)).isAir();
//    }
//
//    private boolean isAir(Vec3 pos){
//        return npc.level().getBlockState(BlockPos.containing(pos)).isAir();
//    }

}
