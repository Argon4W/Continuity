package me.pepperbell.continuity.client.processor;

import java.util.EnumSet;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import me.pepperbell.continuity.client.mixinterface.BlockAndTintGetterExtension;
import org.jetbrains.annotations.Nullable;

import me.pepperbell.continuity.api.client.ProcessingDataProvider;
import me.pepperbell.continuity.client.properties.BaseCtmProperties;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BaseProcessingPredicate implements ProcessingPredicate {
	@Nullable
	protected EnumSet<Direction> faces;
	@Nullable
	protected Predicate<Biome> biomePredicate;
	@Nullable
	protected IntPredicate heightPredicate;
	@Nullable
	protected Predicate<String> blockEntityNamePredicate;

	public BaseProcessingPredicate(@Nullable EnumSet<Direction> faces, @Nullable Predicate<Biome> biomePredicate, @Nullable IntPredicate heightPredicate, @Nullable Predicate<String> blockEntityNamePredicate) {
		this.faces = faces;
		this.biomePredicate = biomePredicate;
		this.heightPredicate = heightPredicate;
		this.blockEntityNamePredicate = blockEntityNamePredicate;
	}

	@Override
	public boolean shouldProcessQuad(QuadView quad, TextureAtlasSprite sprite, BlockAndTintGetter blockView, BlockState appearanceState, BlockState state, BlockPos pos, ProcessingDataProvider dataProvider) {
		if (heightPredicate != null) {
			if (!heightPredicate.test(pos.getY())) {
				return false;
			}
		}
		if (faces != null) {
			Direction face = quad.lightFace();
 			if (appearanceState.hasProperty(BlockStateProperties.AXIS)) {
 				Direction.Axis axis = appearanceState.getValue(BlockStateProperties.AXIS);
 				if (axis == Direction.Axis.X) {
 					face = face.getClockWise(Direction.Axis.Z);
				} else if (axis == Direction.Axis.Z) {
					face = face.getCounterClockWise(Direction.Axis.X);
				}
			}
			if (!faces.contains(face)) {
				return false;
			}
		}
		if (biomePredicate != null) {
			Biome biome = dataProvider.getData(ProcessingDataKeys.BIOME_CACHE).get(blockView, pos);
			if (biome == null || !biomePredicate.test(biome)) {
				return false;
			}
		}
		if (blockEntityNamePredicate != null) {
			String blockEntityName = dataProvider.getData(ProcessingDataKeys.BLOCK_ENTITY_NAME_CACHE).get(blockView, pos);
			if (blockEntityName == null || !blockEntityNamePredicate.test(blockEntityName)) {
				return false;
			}
		}
		return true;
	}

	public static BaseProcessingPredicate fromProperties(BaseCtmProperties properties) {
		return new BaseProcessingPredicate(properties.getFaces(), properties.getBiomePredicate(), properties.getHeightPredicate(), properties.getBlockEntityNamePredicate());
	}

	public static class BiomeCache {
		@Nullable
		protected Biome biome;
		protected boolean invalid = true;

		@Nullable
		public Biome get(BlockAndTintGetter blockView, BlockPos pos) {
			if (invalid) {
				/*Remove redundant fabric-block-view-api-v2 dependency.*/
				biome = ((BlockAndTintGetterExtension) blockView).continuity$hasBiome() ? ((BlockAndTintGetterExtension) blockView).continuity$getBiome(pos).value() : null;
				invalid = false;
			}
			return biome;
		}

		public void reset() {
			invalid = true;
		}
	}

	public static class BlockEntityNameCache {
		@Nullable
		protected String blockEntityName;
		protected boolean invalid = true;

		@Nullable
		public String get(BlockAndTintGetter blockView, BlockPos pos) {
			if (invalid) {
				BlockEntity blockEntity = blockView.getBlockEntity(pos);
				if (blockEntity instanceof Nameable nameable) {
					if (nameable.hasCustomName()) {
						blockEntityName = nameable.getCustomName().getString();
					} else {
						blockEntityName = null;
					}
				} else {
					blockEntityName = null;
				}
				invalid = false;
			}
			return blockEntityName;
		}

		public void reset() {
			invalid = true;
		}
	}
}
