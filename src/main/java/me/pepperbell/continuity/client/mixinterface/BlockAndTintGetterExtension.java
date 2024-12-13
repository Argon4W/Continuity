package me.pepperbell.continuity.client.mixinterface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

/*Remove redundant fabric-block-view-api-v2 dependency.*/
public interface BlockAndTintGetterExtension {

    default boolean continuity$hasBiome() {
        return false;
    }

    default Holder<Biome> continuity$getBiome(BlockPos pos) {
        return null;
    }
}
