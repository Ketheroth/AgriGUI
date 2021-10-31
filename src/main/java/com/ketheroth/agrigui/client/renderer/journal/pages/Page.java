package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.ketheroth.agrigui.AgriGUI;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.util.Collections;
import java.util.List;

public abstract class Page {

	protected static final int PAGE_RIGHT_X = 142;
	protected static final int PAGE_RIGHT_Y = 12;
	protected static final int PAGE_LEFT_X = 8;
	protected static final int PAGE_LEFT_Y = 7;
	protected static final int PAGE_WIDTH = 115;
	protected static final int PAGE_HEIGHT = 145;
	protected final ResourceLocation FONT = new ResourceLocation(AgriGUI.MODID, "unicode_font");

	public abstract void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset);

	public abstract void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset);

	protected int drawText(MatrixStack matrixStack, ITextComponent text, int x, int y, float scale) {
		matrixStack.push();
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		matrixStack.scale(scale, scale, scale);
		final int[] dy = {y};
		fontRenderer.trimStringToWidth(text.deepCopy().mergeStyle(Style.EMPTY.setFontId(FONT)), (int) (PAGE_WIDTH / scale)).forEach(line -> {
			fontRenderer.func_238422_b_(matrixStack, line, x / scale, dy[0] / scale, 0);
			dy[0] += (Math.ceil(7 * scale));
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

	public List<ITextComponent> getTooltipList(int mouseX, int mouseY, int renderX, int renderY) {
		return Collections.emptyList();
	}

	protected boolean isInSquare(int x, int y, int startX, int endX, int startY, int endY) {
		return x >= startX && x <= endX && y >= startY && y <= endY;
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

		public void drawColoredTexture(MatrixStack matrixStack, float x1, float x2, float y1, float y2, int blitOffset, float minU, float maxU, float minV, float maxV, float r, float g, float b, float a) {
			BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			Matrix4f matrix4f = matrixStack.getLast().getMatrix();
			bufferbuilder.pos(matrix4f, x1, y2, blitOffset).tex(minU, maxV).color(r, g, b, a).endVertex();
			bufferbuilder.pos(matrix4f, x2, y2, blitOffset).tex(maxU, maxV).color(r, g, b, a).endVertex();
			bufferbuilder.pos(matrix4f, x2, y1, blitOffset).tex(maxU, minV).color(r, g, b, a).endVertex();
			bufferbuilder.pos(matrix4f, x1, y1, blitOffset).tex(minU, minV).color(r, g, b, a).endVertex();
			bufferbuilder.finishDrawing();
			RenderSystem.enableAlphaTest();
			WorldVertexBufferUploader.draw(bufferbuilder);
		}

		@Override
		public void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
			this.bindTextureAtlas();
			this.drawColoredTexture(transforms, x, x + w, y, y + h, blitOffset, texture.getMinU(), texture.getMaxU(), texture.getMinV(), texture.getMaxV(), r, g, b, a);
		}

		@Override
		public TextureAtlasSprite getSprite(ResourceLocation texture) {
			return IRenderUtilities.super.getSprite(texture);
		}

	}

}
