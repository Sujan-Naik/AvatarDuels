package com.serene.avatarduels.npc.entity.AI.pathfinding;

import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NavigationMesh {

    private HumanEntity humanEntity;
    private Level level;

    private Set<Node> mesh = new HashSet<>();

    private Node start;

    private Node target;

    public NavigationMesh(HumanEntity humanEntity){
        this.humanEntity = humanEntity;


//        updateMesh();
//        calculateStart();
    }




    public List<Node> getPath(Vec3 targetPos){
        this.level = humanEntity.level();

        calculateStart(targetPos);

        this.target = new Node(targetPos, targetPos.distanceTo(start.getPos()), 0 );

        this.updateMesh();

        if (!mesh.isEmpty()) {
           // // Bukkit.broadcastMessage("the mesh has " + mesh.size() + " nodes");
            if (!mesh.contains(target)) {
                this.target = mesh.stream().min(Comparator.comparingDouble(o -> o.getPos().distanceTo(this.target.getPos()))).get();
               // // Bukkit.broadcastMessage("target distance to start is " + target.dist(start));
            }

            return aStar(mesh);
        }
        return new ArrayList<>();
    }

    private void calculateStart(Vec3 targetPos){
        Vec3 floorPos = humanEntity.getOnPos().getCenter();

        while (! isSolid(floorPos)){
            floorPos = floorPos.subtract(0,1,0);
        }
        this.start = new Node(floorPos, 0, targetPos.distanceTo(floorPos));
    }

    double heuristic(Node node){
        return target.dist(node);
    }

    double heuristic(Vec3 vec){
        return target.getPos().distanceTo(vec);
    }

    List<Node> reconstructPath(Map<Node, Node> cameFrom) {
        List<Node> path = new ArrayList<>();
        Node current = target;

        // If the current node (target) is not in cameFrom, it means there was no path found.
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current); // Move to the parent node
        }

        // Reverse the path since we added from target to start
        Collections.reverse(path);

        // Optionally, check if the path starts with the start node
        // and ends with the target node
        if (!path.isEmpty() && path.get(0).equals(start) && path.get(path.size() - 1).equals(target)) {
           // // Bukkit.broadcastMessage("A valid path should be being returned");
            return path;
        } else {

           // // Bukkit.broadcastMessage("Path is invalid but " + path.size());
            return new ArrayList<>(); // Return empty if the path is invalid
        }
    }


    List<Node> aStar( Set<Node> validNodes) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Double> pathCosts = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        openSet.add(start );
        pathCosts.put(start, 0D);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Check if current is the target
            if (current.equals(target)) {
               // // Bukkit.broadcastMessage("WE ARE RECONSTRUCTING THE PATH");
                return reconstructPath(cameFrom);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
//                if (!validNodes.contains(neighbor) || closedSet.contains(neighbor)) {
                if (closedSet.contains(neighbor)) {
                    continue; // Ignore if not valid or already evaluated
                }


                double tentativePathCost = pathCosts.get(current) + 1; // Assuming uniform cost
                if (!pathCosts.containsKey(neighbor) || tentativePathCost < pathCosts.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    pathCosts.put(neighbor, tentativePathCost);

                    // Create new node with updated cost values
                    openSet.add(neighbor);
                }
            }
        }

       // // Bukkit.broadcastMessage("no path found containing target");
        return new ArrayList<>(); // Return empty path if not found
    }
    
    


    public void updateMesh(){
        this.mesh = bfsToEstablishNodes();
    }

    private Node getAcceptableNextPosition( Node floorPos, Direction direction){

        Vec3 newPos = floorPos.getPos().relative(direction, 1);

        if (isAir(newPos.relative(Direction.UP, 1)) && isAir(newPos.relative(Direction.UP, 2))  && isSolid(newPos) ){
            return new Node(newPos, floorPos.pathCost + 1, heuristic(newPos) );
        } else if (isAir(newPos) && isAir(newPos.relative(Direction.UP, 1))  && isSolid(newPos.relative(Direction.DOWN, 1)) ) {
            Vec3 newPosDown = newPos.relative(Direction.DOWN, 1);
            return new Node(newPosDown, floorPos.pathCost + 2, heuristic(newPosDown) );

        } else if (isAir(newPos.relative(Direction.UP, 2 )) && isAir(newPos.relative(Direction.UP, 3))  && isSolid(newPos.relative(Direction.UP, 1 )) ){
            Vec3 newPosUp = newPos.relative(Direction.UP, 1);
            return new Node(newPosUp, floorPos.pathCost + 2, heuristic(newPosUp) );
        }
        return null;
    }

    private boolean isFrownedUponDirection(Vec3 floorPos, Direction direction){
        Vec3 newPos = floorPos.relative(direction, 1);

        return  isAir(newPos) && isAir(newPos.relative(Direction.UP, 1)); // up
    }

    private static final List<Direction> directions = List.of(Direction.NORTH,  Direction.EAST, Direction.SOUTH, Direction.WEST);

    private Set<Node> getNeighbors(Node node){
        Set<Node>neighbours = new HashSet<>();
        directions.stream().forEach(direction -> {
            if (getAcceptableNextPosition(node, direction) != null){
                neighbours.add(getAcceptableNextPosition(node, direction));
            }
        });

        if (neighbours.isEmpty()){
            directions.stream().forEach(direction -> {
                if (isFrownedUponDirection(node.getPos(), direction)){
                    neighbours.add(node.getNeighbour(direction));
                }
            });
        }
        return neighbours;
    }

    private boolean isSolid(Vec3 pos){
        return !level.getBlockState(BlockPos.containing(pos)).isAir() && level.loadedAndEntityCanStandOn(BlockPos.containing(pos), humanEntity);
    }

    private boolean isAir(Vec3 pos){
        return level.getBlockState(BlockPos.containing(pos)).isAir();
    }

    private Set<Node> bfsToEstablishNodes() {
        Queue<Node> queue = new LinkedList<>();

        Set<Node> visited = new HashSet<>();
        Set<Node> validNodes = new HashSet<>();

        queue.add(start);
        visited.add(start);

        int counter = 0;  // Initialize the counter

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            validNodes.add(current);  // Add current Node to valid nodes
            counter++;  // Increment the counter each time we process a node

            for (Node neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }

            if (counter > 10000){
                break;
            }
        }

        // Optional: Output the count of processed nodes
//        System.out.println("Total nodes processed: " + counter);

        return validNodes;
    }

    public class Node implements  Comparable<Node>{

        private Vec3 pos;

        private double pathCost;

        private double heuristic;

        private double estimatedCost;
        Node(Vec3 pos, double pathCost, double heuristic){
            this.pos = pos;

            this.pathCost = pathCost;
            this.heuristic = heuristic;
            this.estimatedCost = pathCost + heuristic;
        }

        public double getHeuristic() {
            return heuristic;
        }

        public double getEstimatedCost() {
            return estimatedCost;
        }

        private Node getNeighbour(Direction direction){
            Vec3 neighbourPos = pos.add(Vec3.atLowerCornerOf(direction.getNormal()));
            return new Node(neighbourPos, this.pathCost + 1, heuristic(target));
        }

        public Vec3 getPos() {
            return pos;
        }

        public double dist(Node node){
            return this.getPos().distanceTo(node.getPos());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node otherNode){
                return this.pos.equals(otherNode.getPos());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return pos.hashCode(); // Use the hash code of the vector for this node
        }

        @Override
        public int compareTo(@NotNull NavigationMesh.Node o) {

             return Double.compare(this.getEstimatedCost(), o.getEstimatedCost());
        }
    }
}
