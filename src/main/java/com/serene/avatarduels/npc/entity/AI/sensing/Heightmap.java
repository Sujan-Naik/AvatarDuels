package com.serene.avatarduels.npc.entity.AI.sensing;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;

public class Heightmap {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Predicate<BlockState> NOT_AIR = state -> !state.isAir();
    static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = BlockBehaviour.BlockStateBase::blocksMotion;
    private final BitStorage data;
    private final Predicate<BlockState> isOpaque;
    private final ChunkAccess chunk;

    public static final Map<Types, Heightmap> heightmaps = Maps.newEnumMap(Types.class);



    public Heightmap(ChunkAccess chunk, Heightmap.Types type) {
        this.isOpaque = type.isOpaque();
        this.chunk = chunk;
        int i = Mth.ceillog2(chunk.getHeight() + 1);
        this.data = new SimpleBitStorage(i, 256);
    }

    public static Heightmap getOrCreateHeightmapUnprimed(ChunkAccess chunkAccess, Types type) {
        return heightmaps.computeIfAbsent(type, (heightmap_type1) -> new Heightmap(chunkAccess, heightmap_type1));
    }


    public static void primeHeightmaps(ChunkAccess chunk, Set<Heightmap.Types> types) {
        int i = types.size();
        ObjectList<Heightmap> objectList = new ObjectArrayList<>(i);
        ObjectListIterator<Heightmap> objectListIterator = objectList.iterator();


        int j = chunk.getHighestFilledSectionIndex();

        if (j == -1) {
            j = chunk.getMinBuildHeight() + 16;
        } else {
            j = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(j)) + 16;
        }


        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int k = 0; k < 16; k++) {
            for (int l = 0; l < 16; l++) {
                for (Heightmap.Types types2 : types) {
                    objectList.add(getOrCreateHeightmapUnprimed(chunk, types2));
                }

                for (int m = j - 1; m >= chunk.getMinBuildHeight(); m--) {
                    mutableBlockPos.set(k, m, l);
                    BlockState blockState = chunk.getBlockState(mutableBlockPos);
                    if (!blockState.is(Blocks.AIR)) {
                        while (objectListIterator.hasNext()) {
                            Heightmap heightmap = objectListIterator.next();
                            if (heightmap.isOpaque.test(blockState)) {
                                heightmap.setHeight(k, l, m + 1);
                                objectListIterator.remove();
                            }
                        }

                        if (objectList.isEmpty()) {
                            break;
                        }

                        objectListIterator.back(i);
                    }
                }
            }
        }
    }

    public boolean update(int x, int y, int z, BlockState state) {
        int i = this.getFirstAvailable(x, z);
        if (y <= i - 2) {
            return false;
        } else {
            if (this.isOpaque.test(state)) {
                if (y >= i) {
                    this.setHeight(x, z, y + 1);
                    return true;
                }
            } else if (i - 1 == y) {
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                for (int j = y - 1; j >= this.chunk.getMinBuildHeight(); j--) {
                    mutableBlockPos.set(x, j, z);
                    if (this.isOpaque.test(this.chunk.getBlockState(mutableBlockPos))) {
                        this.setHeight(x, z, j + 1);
                        return true;
                    }
                }

                this.setHeight(x, z, this.chunk.getMinBuildHeight());
                return true;
            }

            return false;
        }
    }

    public int getFirstAvailable(int x, int z) {
        return this.getFirstAvailable(getIndex(x, z));
    }

    public int getHighestTaken(int x, int z) {
        return this.getFirstAvailable(getIndex(x, z)) - 1;
    }

    private int getFirstAvailable(int index) {
        return this.data.get(index) + this.chunk.getMinBuildHeight();
    }

    private void setHeight(int x, int z, int height) {
        this.data.set(getIndex(x, z), height - this.chunk.getMinBuildHeight());
    }

    public void setRawData(ChunkAccess chunk, Heightmap.Types type, long[] values) {
        long[] ls = this.data.getRaw();
        if (ls.length == values.length) {
            System.arraycopy(values, 0, ls, 0, values.length);
        } else {
            LOGGER.warn("Ignoring heightmap data for chunk " + chunk.getPos() + ", size does not match; expected: " + ls.length + ", got: " + values.length);
            primeHeightmaps(chunk, EnumSet.of(type));
        }
    }

    public long[] getRawData() {
        return this.data.getRaw();
    }

    private static int getIndex(int x, int z) {
        return x + z * 16;
    }

    public enum Types implements StringRepresentable {
        MOTION_BLOCKING("MOTION_BLOCKING", state -> state.blocksMotion() || !state.getFluidState().isEmpty()),
        MOTION_BLOCKING_NO_BARRIERS_OR_WATER(
                "MOTION_BLOCKING_IGNORE_BARRIERS_AND_WATER",
                state -> (state.blocksMotion() || !state.getFluidState().isEmpty()) && !(state.getBlock() instanceof BarrierBlock )),
        MOTION_BLOCKING_NO_BARRIERS(
                "MOTION_BLOCKING_IGNORE_BARRIERS",
                state -> (state.blocksMotion() &&  !(state.getBlock() instanceof BarrierBlock )
        ));

        private final String serializationKey;
        private final Predicate<BlockState> isOpaque;

        Types(final String name, final Predicate<BlockState> blockPredicate) {
            this.serializationKey = name;
            this.isOpaque = blockPredicate;
        }

        public String getSerializationKey() {
            return this.serializationKey;
        }

        public Predicate<BlockState> isOpaque() {
            return this.isOpaque;
        }

        @Override
        public String getSerializedName() {
            return this.serializationKey;
        }
    }


}
