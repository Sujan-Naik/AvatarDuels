package com.serene.avatarduels.duel;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.moros.gaia.api.Gaia;
import me.moros.gaia.api.arena.Arena;
import me.moros.gaia.api.service.ArenaService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class DuelManager {

    private Gaia gaiaApi;
    private ArenaService arenaService;

    private Set<Arena> arenas = new HashSet<>();
    public DuelManager(){
        RegisteredServiceProvider<Gaia> provider = Bukkit.getServicesManager().getRegistration(Gaia.class);
        if (provider != null) {
            gaiaApi = provider.getProvider();
            arenaService = gaiaApi.arenaService();
            arenaService.stream().forEach(arena -> arenas.add(arena));
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "FUCK");
        }
    }

    public void reload() {
        arenaService.stream().forEach(arena -> arenas.add(arena));
    }

    public void listArenas(){
        arenas.forEach(arena -> {

            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append("World: " + arena.level().asMinimalString() + " Arena: " + arena.name());

            arena.streamPoints().forEach(point ->
                    messageBuilder.append("Point: ").append(point.toVector3i().toString())
            );

            String message = messageBuilder.toString();
            // Bukkit.broadcastMessage(message);
        });
    }


    // Assuming this method is part of a class; you need to specify your return type instead of 'Void' based on your requirements.
    public Arena goToArena(String name, Player player, int slot) {
        // Stream through the arenas and filter by name
        Optional<Arena> optionalArena = arenas.stream()
                .filter(arena -> arena.name().equalsIgnoreCase(name)) // Filter by name
                .findAny(); // Find any matching arena

        // If an arena was found, perform the teleport
        optionalArena.ifPresent(arena -> {
            MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
            MVWorldManager worldManager = core.getMVWorldManager();

            // Check if the world is an MV world
            if (worldManager.isMVWorld(name)) {
                World world = worldManager.getMVWorld(name).getCBWorld();
                if (world != null) {
                    // Ensure the slot is within bounds
                    if (slot >= 0 && slot < arena.points().size()) {
                        // Create a new location and teleport the player
                        Location loc = new Location(world,
                                arena.points().get(slot).x(),
                                arena.points().get(slot).y(),
                                arena.points().get(slot).z());
                        player.teleport(loc); // Teleport the player
                        player.sendMessage("Teleported to " + arena.name()); // Inform the player
                    } else {
                        player.sendMessage("Invalid slot number."); // Handle invalid slot case
                    }
                } else {
                    player.sendMessage("World not found."); // Handle world not found case
                }
            } else {
                player.sendMessage("Not a Multiverse world."); // Handle not a Multiverse world case
            }
        });

        // Return the arena or null if no arena was found
        return optionalArena.orElse(null);
    }



}
