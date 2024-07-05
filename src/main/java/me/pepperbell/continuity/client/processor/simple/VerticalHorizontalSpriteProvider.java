package me.pepperbell.continuity.client.processor.simple;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import me.pepperbell.continuity.api.client.ProcessingDataProvider;
import me.pepperbell.continuity.client.processor.ConnectionPredicate;
import me.pepperbell.continuity.client.processor.DirectionMaps;
import me.pepperbell.continuity.client.processor.OrientationMode;
import me.pepperbell.continuity.client.processor.ProcessingDataKeys;
import me.pepperbell.continuity.client.properties.OrientedConnectingCtmProperties;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public class VerticalHorizontalSpriteProvider extends VerticalSpriteProvider {
	// Indices for this array are formed from these bit values:
	// 32     16
	// 1   *   8
	// 2       4
	protected static final int[] SECONDARY_SPRITE_INDEX_MAP = new int[] {
			3, 6, 3, 3, 3, 6, 3, 3, 4, 5, 4, 4, 3, 6, 3, 3,
			3, 6, 3, 3, 3, 6, 3, 3, 3, 6, 3, 3, 3, 6, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
	};

	public VerticalHorizontalSpriteProvider(Sprite[] sprites, ConnectionPredicate connectionPredicate, boolean innerSeams, OrientationMode orientationMode) {
		super(sprites, connectionPredicate, innerSeams, orientationMode);
	}

	@Override
	@Nullable
	public Sprite getSprite(QuadView quad, Sprite sprite, BlockRenderView blockView, BlockState appearanceState, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, ProcessingDataProvider dataProvider) {
		Direction[] directions = DirectionMaps.getDirections(orientationMode, quad, appearanceState);
		BlockPos.Mutable mutablePos = dataProvider.getData(ProcessingDataKeys.MUTABLE_POS);
		int connections = getConnections(directions, mutablePos, blockView, appearanceState, state, pos, quad.lightFace(), sprite);
		if (connections != 0) {
			return sprites[SPRITE_INDEX_MAP[connections]];
		} else {
			int secondaryConnections = getSecondaryConnections(directions, mutablePos, blockView, appearanceState, state, pos, quad.lightFace(), sprite);
			return sprites[SECONDARY_SPRITE_INDEX_MAP[secondaryConnections]];
		}
	}

	protected int getSecondaryConnections(Direction[] directions, BlockPos.Mutable mutablePos, BlockRenderView blockView, BlockState appearanceState, BlockState state, BlockPos pos, Direction face, Sprite quadSprite) {
		int connections = 0;
		for (int i = 0; i < 2; i++) {
			Direction direction = directions[i * 2];
			mutablePos.set(pos, direction);
			if (connectionPredicate.shouldConnect(blockView, state, appearanceState, pos, mutablePos, face, quadSprite, innerSeams)) {
				connections |= 1 << (i * 3);
				for (int j = 0; j < 2; j++) {
					mutablePos.set(pos, direction).move(directions[((i + j) % 2) * 2 + 1]);
					if (connectionPredicate.shouldConnect(blockView, appearanceState, state, pos, mutablePos, face, quadSprite, innerSeams)) {
						connections |= 1 << ((i * 3 + j * 2 + 5) % 6);
					}
				}
			}
		}
		return connections;
	}

	public static class Factory implements SpriteProvider.Factory<OrientedConnectingCtmProperties> {
		@Override
		public SpriteProvider createSpriteProvider(Sprite[] sprites, OrientedConnectingCtmProperties properties) {
			return new VerticalHorizontalSpriteProvider(sprites, properties.getConnectionPredicate(), properties.getInnerSeams(), properties.getOrientationMode());
		}

		@Override
		public int getTextureAmount(OrientedConnectingCtmProperties properties) {
			return 7;
		}
	}
}
