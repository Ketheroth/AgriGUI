package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class Page {

	protected static final int PAGE_RIGHT_X = 142;
	protected static final int PAGE_RIGHT_Y = 16;
	protected static final int PAGE_LEFT_X = 8;
	protected static final int PAGE_LEFT_Y = 7;
	protected static final int PAGE_WIDTH = 115;
	protected static final int PAGE_HEIGHT = 145;

	public abstract void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset);

	public abstract void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset);

	protected int drawText(MatrixStack matrixStack, ITextComponent text, int x, int y, float scale) {
		matrixStack.push();
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		matrixStack.scale(scale, scale, scale);
		final int[] dy = {y};
		fontRenderer.trimStringToWidth(text, (int) (this.PAGE_WIDTH / scale)).forEach(line -> {
			fontRenderer.func_238422_b_(matrixStack, line, x / scale, dy[0] / scale, 0);
			dy[0] += (Math.ceil(fontRenderer.FONT_HEIGHT * scale));
		});
		matrixStack.pop();
		return dy[0];
	}

	protected void drawTexture(ResourceLocation texture, TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight) {
		textureManager.bindTexture(texture);
		AbstractGui.blit(matrixStack, x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, height, width);
	}

	protected void drawTexture(ResourceLocation texture, TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int x, int y, int width, int height) {
		this.drawTexture(texture, textureManager, matrixStack, blitOffset, x, y, width, height, 0, 0, width, height);
	}

	protected void drawMutation(TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int renderX, int renderY, List<IAgriPlant> plants) {
		this.drawTexture(Textures.MUTATION, textureManager, matrixStack, blitOffset, renderX, renderY, 86, 18);
		Renderer renderer = new Renderer(blitOffset);
		plants.get(0).getGuiRenderer().drawGrowthStage(plants.get(0), plants.get(0).getFinalStage(), renderer, matrixStack, renderX + 1, renderY + 1, 16, 16);
		plants.get(1).getGuiRenderer().drawGrowthStage(plants.get(1), plants.get(1).getFinalStage(), renderer, matrixStack, renderX + 35, renderY + 1, 16, 16);
		plants.get(2).getGuiRenderer().drawGrowthStage(plants.get(2), plants.get(2).getFinalStage(), renderer, matrixStack, renderX + 69, renderY + 1, 16, 16);
	}

	public static final class Textures {

		public static final ResourceLocation TITLE = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_title.png");
		public static final ResourceLocation GROWTH_STAGE = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_growth_stage.png");
		public static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_brightness_bar.png");
		public static final ResourceLocation BRIGHTNESS_HIGHLIGHT = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_brightness_highlight.png");
		public static final ResourceLocation HUMIDITY_EMPTY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_humidity_empty.png");
		public static final ResourceLocation HUMIDITY_FILLED = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_humidity_filled.png");
		public static final ResourceLocation ACIDITY_EMPTY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_acidity_empty.png");
		public static final ResourceLocation ACIDITY_FILLED = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_acidity_filled.png");
		public static final ResourceLocation NUTRIENTS_FILLED = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_nutrients_filled.png");
		public static final ResourceLocation NUTRIENTS_EMPTY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_nutrients_empty.png");
		public static final ResourceLocation SEASONS_FILLED = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_seasons_filled.png");
		public static final ResourceLocation SEASONS_EMPTY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_seasons_empty.png");
		public static final ResourceLocation MUTATION = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/template_mutation.png");

		public static final int[] HUMIDITY_OFFSETS = new int[]{0, 8, 16, 26, 36, 46, 53};
		public static final int[] ACIDITY_OFFSETS = new int[]{0, 7, 15, 22, 30, 38, 46, 53};
		public static final int[] NUTRIENTS_OFFSETS = new int[]{0, 6, 14, 23, 32, 43, 53};

		private Textures() {
		}

	}

	protected static class Renderer implements IAgriGrowableGuiRenderer.RenderContext, IRenderUtilities {

		private final int blitOffset;

		public Renderer(int blitOffset) {
			this.blitOffset = blitOffset;
		}

		@Override
		public void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
			this.bindTextureAtlas();
			AbstractGui.blit(transforms, (int) x, (int) y, blitOffset, (int) w, (int) h, texture);
		}

		@Override
		public TextureAtlasSprite getSprite(ResourceLocation texture) {
			return IRenderUtilities.super.getSprite(texture);
		}

	}

}
