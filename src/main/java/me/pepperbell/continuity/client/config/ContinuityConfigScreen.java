package me.pepperbell.continuity.client.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ContinuityConfigScreen extends Screen {
	private final Screen parent;
	private final ContinuityConfig config;

	public ContinuityConfigScreen(Screen parent, ContinuityConfig config) {
		super(Text.translatable(getTranslationKey("title")));
		this.parent = parent;
		this.config = config;
	}

	@Override
	protected void init() {
		addDrawableChild(startBooleanOptionButton(config.connectedTextures)
				.dimensions(width / 2 - 100 - 110, height / 2 - 10 - 12, 200, 20)
				.build());
		addDrawableChild(startBooleanOptionButton(config.emissiveTextures)
				.dimensions(width / 2 - 100 + 110, height / 2 - 10 - 12, 200, 20)
				.build());
		addDrawableChild(startBooleanOptionButton(config.customBlockLayers)
				.dimensions(width / 2 - 100 - 110, height / 2 - 10 + 12, 200, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
				.dimensions(width / 2 - 100, height - 40, 200, 20)
				.build());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawCenteredText(matrices, textRenderer, title, width / 2, 30, 0xFFFFFF);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}

	@Override
	public void removed() {
		config.save();
		config.onChange();
	}

	private static String getTranslationKey(String optionKey) {
		return "options.continuity." + optionKey;
	}

	private static String getTooltipKey(String translationKey) {
		return translationKey + ".tooltip";
	}

	private ButtonWidget.Builder startBooleanOptionButton(Option<Boolean> option) {
		String translationKey = getTranslationKey(option.getKey());
		Text text = Text.translatable(translationKey);
		Text tooltipText = Text.translatable(getTooltipKey(translationKey));
		return ButtonWidget.builder(ScreenTexts.composeToggleText(text, option.get()),
				button -> {
					boolean newValue = !option.get();
					button.setMessage(ScreenTexts.composeToggleText(text, newValue));
					option.set(newValue);
				})
				.tooltip(Tooltip.of(tooltipText));
	}
}
