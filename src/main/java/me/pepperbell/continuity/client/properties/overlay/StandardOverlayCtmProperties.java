package me.pepperbell.continuity.client.properties.overlay;

import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import me.pepperbell.continuity.client.properties.BasicConnectingCtmProperties;
import me.pepperbell.continuity.client.properties.PropertiesParsingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public class StandardOverlayCtmProperties extends BasicConnectingCtmProperties implements OverlayPropertiesSection.Provider {
	protected OverlayPropertiesSection overlaySection;
	@Nullable
	protected Set<Identifier> connectTilesSet;
	@Nullable
	protected Predicate<BlockState> connectBlocksPredicate;

	public StandardOverlayCtmProperties(Properties properties, Identifier resourceId, ResourcePack pack, int packPriority, ResourceManager resourceManager, String method) {
		super(properties, resourceId, pack, packPriority, resourceManager, method);
		overlaySection = new OverlayPropertiesSection(properties, resourceId, packId);
	}

	@Override
	public void init() {
		super.init();
		overlaySection.init();
		parseConnectTiles();
		parseConnectBlocks();
	}

	@Override
	public OverlayPropertiesSection getOverlayPropertiesSection() {
		return overlaySection;
	}

	protected void parseConnectTiles() {
		connectTilesSet = PropertiesParsingHelper.parseMatchTiles(properties, "connectTiles", resourceId, packId);
	}

	protected void parseConnectBlocks() {
		connectBlocksPredicate = PropertiesParsingHelper.parseBlockStates(properties, "connectBlocks", resourceId, packId);
	}

	@Nullable
	public Set<Identifier> getConnectTilesSet() {
		return connectTilesSet;
	}

	@Nullable
	public Predicate<BlockState> getConnectBlocksPredicate() {
		return connectBlocksPredicate;
	}
}
