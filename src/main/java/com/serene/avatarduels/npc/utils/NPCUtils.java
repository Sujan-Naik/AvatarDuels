package com.serene.avatarduels.npc.utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.npc.NPCHandler;
import com.serene.avatarduels.npc.entity.BendingNPC;

import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.flow.FlowControlHandler;
import net.datafaker.Faker;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.UnconfiguredPipelineHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Triplet;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class NPCUtils {

    private static final Stack<Triplet<String, String, String>> NAME_VALUE_SIGNATURE = new Stack<>();



    public static BendingNPC spawnNPC(Location location, Player player, String name) {
        //ServerPlayer player = ((CraftPlayer)p).getHandle();

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();

        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, name);

        GameProfile skinGameProfile = setSkin(gameProfile);

        serverLevel.getCraftServer().getOfflinePlayer(gameProfile);

        BendingNPC serverPlayer = new BendingNPC(minecraftServer, serverLevel, skinGameProfile, ClientInformation.createDefault());

        Bukkit.getScheduler().runTaskLater(AvatarDuels.plugin, () -> {serverPlayer.enableBending();}, 100L);

        serverPlayer.setPos(location.getX(), location.getY(), location.getZ());

        SynchedEntityData synchedEntityData = serverPlayer.getEntityData();
        synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

        Connection serverPlayerConnection = new Connection(PacketFlow.SERVERBOUND);

//        serverPlayerConnection.channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        serverPlayerConnection.channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                // Ignore the incoming message
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                // Handle exceptions here to avoid them getting suppressed
            }

        });

        CommonListenerCookie commonListenerCookie = CommonListenerCookie.createInitial(gameProfile, true);
        ServerGamePacketListenerImpl serverGamePacketListener = new ServerGamePacketListenerImpl(minecraftServer, serverPlayerConnection, serverPlayer, commonListenerCookie);
        serverPlayer.connection = serverGamePacketListener;

        addNPC(player, serverPlayer);

//        serverLevel.addFreshEntity(serverPlayer);

//        serverLevel.addNewPlayer(serverPlayer);
        serverLevel.getServer().getPlayerList().placeNewPlayer(serverPlayerConnection, serverPlayer, commonListenerCookie);
        serverLevel.getServer().getPlayerList().players.forEach(serverPlayer1 -> Bukkit.broadcastMessage(serverPlayer1.displayName));

        NPCHandler.addNPC(serverPlayer);

        return serverPlayer;
    }

//    public void placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
//
//        ServerLevel serverLevel = player.serverLevel();
//
//
//        player.isRealPlayer = true; // Paper
//        player.loginTime = System.currentTimeMillis(); // Paper - Replace OfflinePlayer#getLastPlayed
//        GameProfile gameprofile = player.getGameProfile();
//        GameProfileCache usercache = ((CraftServer) Bukkit.getServer()).getServer().getProfileCache();
//        // Optional optional; // CraftBukkit - decompile error
//        String s;
//
//        if (usercache != null) {
//            Optional<GameProfile> optional = usercache.get(gameprofile.getId()); // CraftBukkit - decompile error
//            s = (String) optional.map(GameProfile::getName).orElse(gameprofile.getName());
//            usercache.add(gameprofile);
//        } else {
//            s = gameprofile.getName();
//        }
//
//        Optional<CompoundTag> optional = serverLevel.getServer().getPlayerList().load(player); // CraftBukkit - decompile error
//        ResourceKey<Level> resourcekey = null; // Paper
//        // CraftBukkit start - Better rename detection
//        if (optional.isPresent()) {
//            CompoundTag nbttagcompound = optional.get();
//            if (nbttagcompound.contains("bukkit")) {
//                CompoundTag bukkit = nbttagcompound.getCompound("bukkit");
//                s = bukkit.contains("lastKnownName", 8) ? bukkit.getString("lastKnownName") : s;
//            }
//        }
//        // CraftBukkit end
//        // Paper start - move logic in Entity to here, to use bukkit supplied world UUID & reset to main world spawn if no valid world is found
//        boolean[] invalidPlayerWorld = {false};
//        bukkitData: if (optional.isPresent()) {
//            // The main way for bukkit worlds to store the world is the world UUID despite mojang adding custom worlds
//            final org.bukkit.World bWorld;
//            if (optional.get().contains("WorldUUIDMost") && optional.get().contains("WorldUUIDLeast")) {
//                bWorld = org.bukkit.Bukkit.getServer().getWorld(new UUID(optional.get().getLong("WorldUUIDMost"), optional.get().getLong("WorldUUIDLeast")));
//            } else if (optional.get().contains("world", net.minecraft.nbt.Tag.TAG_STRING)) { // Paper - legacy bukkit world name
//                bWorld = org.bukkit.Bukkit.getServer().getWorld(optional.get().getString("world"));
//            } else {
//                break bukkitData; // if neither of the bukkit data points exist, proceed to the vanilla migration section
//            }
//            if (bWorld != null) {
//                resourcekey = ((CraftWorld) bWorld).getHandle().dimension();
//            } else {
//                resourcekey = Level.OVERWORLD;
//                invalidPlayerWorld[0] = true;
//            }
//        }
//        if (resourcekey == null) { // only run the vanilla logic if we haven't found a world from the bukkit data
//            // Below is the vanilla way of getting the dimension, serverLevel.getServer().getPlayerList() is for migration from vanilla servers
//            resourcekey = optional.flatMap((nbttagcompound) -> {
//                // Paper end
//                DataResult<ResourceKey<Level>> dataresult = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, nbttagcompound.get("Dimension"))); // CraftBukkit - decompile error
////                Logger logger = PlayerList.LOGGER;
////
////                Objects.requireNonNull(logger);
//                // Paper start - reset to main world spawn if no valid world is found
////                final Optional<ResourceKey<Level>> result = dataresult.resultOrPartial(logger::error);
////                invalidPlayerWorld[0] = result.isEmpty();
////                return result;
//            }).orElse(Level.OVERWORLD); // Paper - revert to vanilla default main world, serverLevel.getServer().getPlayerList() isn't an "invalid world" since no player data existed
//        }
//        // Paper end
//        ServerLevel worldserver = ((CraftServer) Bukkit.getServer()).getServer().getLevel(resourcekey);
//        ServerLevel worldserver1;
//
//        if (worldserver == null) {
////            PlayerList.LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourcekey);
//            worldserver1 = ((CraftServer) Bukkit.getServer()).getServer().overworld();
//            invalidPlayerWorld[0] = true; // Paper - reset to main world if no world with parsed value is found
//        } else {
//            worldserver1 = worldserver;
//        }
//
//        // Paper start - Entity#getEntitySpawnReason
//        if (optional.isEmpty()) {
//            player.spawnReason = org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT; // set Player SpawnReason to DEFAULT on first login
//            // Paper start - reset to main world spawn if first spawn or invalid world
//        }
//        if (optional.isEmpty() || invalidPlayerWorld[0]) {
//            // Paper end - reset to main world spawn if first spawn or invalid world
//            player.moveTo(player.adjustSpawnLocation(worldserver1, worldserver1.getSharedSpawnPos()).getBottomCenter(), worldserver1.getSharedSpawnAngle(), 0.0F); // Paper - MC-200092 - fix first spawn pos yaw being ignored
//        }
//        // Paper end - Entity#getEntitySpawnReason
//        player.setServerLevel(worldserver1);
//        String s1 = connection.getLoggableAddress(((CraftServer) Bukkit.getServer()).getServer().logIPs());
//
//        // Spigot start - spawn location event
//        Player spawnPlayer = player.getBukkitEntity();
//        org.spigotmc.event.player.PlayerSpawnLocationEvent ev = new org.spigotmc.event.player.PlayerSpawnLocationEvent(spawnPlayer, spawnPlayer.getLocation());
//
//        serverLevel.getServer().getPlayerList().getServer().server.getPluginManager().callEvent(ev);
//
//        Location loc = ev.getSpawnLocation();
//        worldserver1 = ((CraftWorld) loc.getWorld()).getHandle();
//
//        player.spawnIn(worldserver1);
//        player.gameMode.setLevel((ServerLevel) player.level());
//        // Paper start - set raw so we aren't fully joined to the world (not added to chunk or world)
//        player.setPosRaw(loc.getX(), loc.getY(), loc.getZ());
//        player.setRot(loc.getYaw(), loc.getPitch());
//        // Paper end - set raw so we aren't fully joined to the world
//        // Spigot end
//
//        // CraftBukkit - Moved message to after join
//        // PlayerList.LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{entityplayer.getName().getString(), s1, entityplayer.getId(), entityplayer.getX(), entityplayer.getY(), entityplayer.getZ()});
//        LevelData worlddata = worldserver1.getLevelData();
//
//        player.loadGameTypes((CompoundTag) optional.orElse(null)); // CraftBukkit - decompile error
//        ServerGamePacketListenerImpl playerconnection = new ServerGamePacketListenerImpl(((CraftServer) Bukkit.getServer()).getServer(), connection, player, clientData);
//
//        connection.setupInboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(((CraftServer) Bukkit.getServer()).getServer().registryAccess())), playerconnection);
//        GameRules gamerules = worldserver1.getGameRules();
//        boolean flag = gamerules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
//        boolean flag1 = gamerules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
//        boolean flag2 = gamerules.getBoolean(GameRules.RULE_LIMITED_CRAFTING);
//
//        // Spigot - view distance
//        playerconnection.send(new ClientboundLoginPacket(player.getId(), worlddata.isHardcore(), ((CraftServer) Bukkit.getServer()).getServer().levelKeys(), serverLevel.getServer().getPlayerList().getMaxPlayers(), worldserver1.spigotConfig.viewDistance, worldserver1.spigotConfig.simulationDistance, flag1, !flag, flag2, player.createCommonSpawnInfo(worldserver1), ((CraftServer) Bukkit.getServer()).getServer().enforceSecureProfile()));
//        player.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
//        playerconnection.send(new ClientboundChangeDifficultyPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
//        playerconnection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
//        playerconnection.send(new ClientboundSetCarriedItemPacket(player.getInventory().selected));
//        playerconnection.send(new ClientboundUpdateRecipesPacket(((CraftServer) Bukkit.getServer()).getServer().getRecipeManager().getOrderedRecipes()));
//        serverLevel.getServer().getPlayerList().sendPlayerPermissionLevel(player);
//        player.getStats().markAllDirty();
//        player.getRecipeBook().sendInitialRecipeBook(player);
//        serverLevel.getServer().getPlayerList().updateEntireScoreboard(worldserver1.getScoreboard(), player);
//        ((CraftServer) Bukkit.getServer()).getServer().invalidateStatus();
//        MutableComponent ichatmutablecomponent;
//
//        if (player.getGameProfile().getName().equalsIgnoreCase(s)) {
//            ichatmutablecomponent = Component.translatable("multiplayer.player.joined", player.getDisplayName());
//        } else {
//            ichatmutablecomponent = Component.translatable("multiplayer.player.joined.renamed", player.getDisplayName(), s);
//        }
//        // CraftBukkit start
//        ichatmutablecomponent.withStyle(ChatFormatting.YELLOW);
//        Component joinMessage = ichatmutablecomponent; // Paper - Adventure
//
//        playerconnection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
//        ServerStatus serverping = ((CraftServer) Bukkit.getServer()).getServer().getStatus();
//
//        if (serverping != null && !clientData.transferred()) {
//            player.sendServerStatus(serverping);
//        }
//
//        // entityplayer.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(serverLevel.getServer().getPlayerList().players)); // CraftBukkit - replaced with loop below
//        serverLevel.getServer().getPlayerList().players.add(player);
//
//        serverLevel.getServer().getPlayerList().playersByName.put(player.getScoreboardName().toLowerCase(java.util.Locale.ROOT), player); // Spigot
//        serverLevel.getServer().getPlayerList().playersByUUID.put(player.getUUID(), player);
//        // serverLevel.getServer().getPlayerList().broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityplayer))); // CraftBukkit - replaced with loop below
//
//        // Paper start - Fire PlayerJoinEvent when Player is actually ready; correctly register player BEFORE PlayerJoinEvent, so the entity is valid and doesn't require tick delay hacks
//        player.supressTrackerForLogin = true;
//        worldserver1.addNewPlayer(player);
//        ((CraftServer) Bukkit.getServer()).getServer().getCustomBossEvents().onPlayerConnect(player); // see commented out section below worldserver.addPlayerJoin(entityplayer);
//        serverLevel.getServer().getPlayerList().mountSavedVehicle(player, worldserver1, optional);
//        // Paper end - Fire PlayerJoinEvent when Player is actually ready
//        // CraftBukkit start
//        CraftPlayer bukkitPlayer = player.getBukkitEntity();
//
//        // Ensure that player inventory is populated with its viewer
//        player.containerMenu.transferTo(player.containerMenu, bukkitPlayer);
//
//        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(bukkitPlayer, io.papermc.paper.adventure.PaperAdventure.asAdventure(ichatmutablecomponent)); // Paper - Adventure
//        serverLevel.getServer().getPlayerList().cserver.getPluginManager().callEvent(playerJoinEvent);
//
//        if (!player.connection.isAcceptingMessages()) {
//            return;
//        }
//
//        final net.kyori.adventure.text.Component jm = playerJoinEvent.joinMessage();
//
//        if (jm != null && !jm.equals(net.kyori.adventure.text.Component.empty())) { // Paper - Adventure
//            joinMessage = io.papermc.paper.adventure.PaperAdventure.asVanilla(jm); // Paper - Adventure
//            ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().broadcastSystemMessage(joinMessage, false); // Paper - Adventure
//        }
//        // CraftBukkit end
//
//        // CraftBukkit start - sendAll above replaced with serverLevel.getServer().getPlayerList() loop
//        ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player)); // Paper - Add Listing API for Player
//
//        final List<ServerPlayer> onlinePlayers = Lists.newArrayListWithExpectedSize(serverLevel.getServer().getPlayerList().players.size() - 1); // Paper - Use single player info update packet on join
//        for (int i = 0; i < serverLevel.getServer().getPlayerList().players.size(); ++i) {
//            ServerPlayer entityplayer1 = (ServerPlayer) serverLevel.getServer().getPlayerList().players.get(i);
//
//            if (entityplayer1.getBukkitEntity().canSee(bukkitPlayer)) {
//                // Paper start - Add Listing API for Player
//                if (entityplayer1.getBukkitEntity().isListed(bukkitPlayer)) {
//                    // Paper end - Add Listing API for Player
//                    entityplayer1.connection.send(packet);
//                    // Paper start - Add Listing API for Player
//                } else {
//                    entityplayer1.connection.send(ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(player, false));
//                }
//                // Paper end - Add Listing API for Player
//            }
//
//            if (entityplayer1 == player || !bukkitPlayer.canSee(entityplayer1.getBukkitEntity())) { // Paper - Use single player info update packet on join; Don't include joining player
//                continue;
//            }
//
//            onlinePlayers.add(entityplayer1); // Paper - Use single player info update packet on join
//        }
//        // Paper start - Use single player info update packet on join
//        if (!onlinePlayers.isEmpty()) {
//            player.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(onlinePlayers, player)); // Paper - Add Listing API for Player
//        }
//        // Paper end - Use single player info update packet on join
//        player.sentListPacket = true;
//        player.supressTrackerForLogin = false; // Paper - Fire PlayerJoinEvent when Player is actually ready
//        ((ServerLevel)player.level()).getChunkSource().chunkMap.addEntity(player); // Paper - Fire PlayerJoinEvent when Player is actually ready; track entity now
//        // CraftBukkit end
//
//        //player.refreshEntityData(player); // CraftBukkit - BungeeCord#2321, send complete data to self on spawn // Paper - THIS IS NOT NEEDED ANYMORE
//
//        serverLevel.getServer().getPlayerList().sendLevelInfo(player, worldserver1);
//
//        // CraftBukkit start - Only add if the player wasn't moved in the event
//        if (player.level() == worldserver1 && !worldserver1.players().contains(player)) {
//            worldserver1.addNewPlayer(player);
//            ((CraftServer) Bukkit.getServer()).getServer().getCustomBossEvents().onPlayerConnect(player);
//        }
//
//        worldserver1 = player.serverLevel(); // CraftBukkit - Update in case join event changed it
//        // CraftBukkit end
//        serverLevel.getServer().getPlayerList().sendActivePlayerEffects(player);
//        // Paper start - Fire PlayerJoinEvent when Player is actually ready; move vehicle into method so it can be called above - short circuit around that code
//        serverLevel.getServer().getPlayerList().onPlayerJoinFinish(player, worldserver1, s1);
//        // Paper start - Send empty chunk, so players aren't stuck in the world loading screen with our chunk system not sending chunks when dead
//        if (player.isDeadOrDying()) {
//            net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> plains = worldserver1.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
//                    .getHolderOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS);
//            player.connection.send(new net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket(
//                    new net.minecraft.world.level.chunk.EmptyLevelChunk(worldserver1, player.chunkPosition(), plains),
//                    worldserver1.getLightEngine(), (java.util.BitSet)null, (java.util.BitSet) null, true)
//            );
//        }
//        // Paper end - Send empty chunk
//    }

    public static void updateEquipment(BendingNPC npc, Player player) {
        List<Pair<EquipmentSlot, ItemStack>> equipment = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            equipment.add(new Pair<EquipmentSlot, ItemStack>(slot, npc.getItemBySlot(slot)));
        }
        ClientboundSetEquipmentPacket clientboundSetEquipmentPacket =
                new ClientboundSetEquipmentPacket(npc.getId(), equipment);
        PacketUtils.sendPacket(clientboundSetEquipmentPacket, player);
    }

    public static String getJson(String url) {
        String json = getStringFromURL(url);
        return json;
    }

    public static GameProfile setSkin(GameProfile gameProfile) {

        Triplet<String, String, String> triplet = NAME_VALUE_SIGNATURE.peek();
        if (NAME_VALUE_SIGNATURE.size() > 1) {
            NAME_VALUE_SIGNATURE.pop();
        }
        String name = triplet.getValue0();
        String value = triplet.getValue1();
        String signature = triplet.getValue2();

        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("name", new Property("name", name));
        propertyMap.put("textures", new Property("textures", value, signature));
        return gameProfile;
    }

    public static void changeSkin(String value, String signature, GameProfile gameProfile) {
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
    }

    public static void initUUID(int counter, JavaPlugin plugin) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            Faker faker = new Faker();
            String newUrl = "https://api.mojang.com/users/profiles/minecraft/" + faker.name().firstName();
            String json = getJson(newUrl);
            if (!json.isEmpty()) {
                Gson gson = new Gson();
                String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();
                initProperties(faker.name().firstName(), uuid);
            }
            if (counter < 100) {
                initUUID(counter + 1, plugin);
            }

        }, 100L);
    }

    private static void initProperties(String name, String uuid) {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        String json = getStringFromURL(url);
        Gson gson = new Gson();
        JsonObject mainObject = gson.fromJson(json, JsonObject.class);
        JsonObject jsonObject = mainObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String value = jsonObject.get("value").getAsString();
        String signature = jsonObject.get("signature").getAsString();
        Triplet<String, String, String> triplet = new Triplet<>(name, value, signature);
        NAME_VALUE_SIGNATURE.add(triplet);

    }


    private static String getStringFromURL(String url) {
        StringBuilder text = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new URL(url).openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                while (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                text.append(line);
            }
            scanner.close();
        } catch (IOException exception) {
//            exception.printStackTrace();
        }
        return text.toString();
    }

    public static void addNPC(Player player, BendingNPC serverPlayer) {
//        ClientboundPlayerInfoUpdatePacketWrapper playerInfoPacket = new ClientboundPlayerInfoUpdatePacketWrapper(
//                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY),
//                serverPlayer,
//                180,
//                true
//        );
//        PacketUtils.sendPacket(playerInfoPacket.getPacket(), player);

        ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer);
        PacketUtils.sendPacket(playerInfoUpdatePacket, player);
    }


}