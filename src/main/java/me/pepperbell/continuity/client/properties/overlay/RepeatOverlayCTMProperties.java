package me.pepperbell.continuity.client.properties.overlay;

import java.util.Properties;

import me.pepperbell.continuity.client.properties.RepeatCTMProperties;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public class RepeatOverlayCTMProperties extends RepeatCTMProperties implements OverlayPropertiesSection.Provider {
	protected OverlayPropertiesSection overlaySection;

	public RepeatOverlayCTMProperties(Properties properties, Identifier id, ResourcePack pack, int packPriority, ResourceManager resourceManager, String method) {
		super(properties, id, pack, packPriority, resourceManager, method);
		overlaySection = new OverlayPropertiesSection(properties, id, packName);
	}

	@Override
	public void init() {
		super.init();
		overlaySection.init();
	}

	@Override
	public OverlayPropertiesSection getOverlayPropertiesSection() {
		return overlaySection;
	}
}
