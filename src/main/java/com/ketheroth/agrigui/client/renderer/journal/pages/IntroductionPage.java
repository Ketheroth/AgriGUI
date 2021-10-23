package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IntroductionPage extends Page {

	public static final Page INSTANCE = new IntroductionPage();

	private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
	private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
	private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
	private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");

	private IntroductionPage() {
	}

	@Override
	public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset) {
	}

	@Override
	public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset) {
		int dy = renderY + PAGE_RIGHT_Y;
		int dx = renderX + PAGE_RIGHT_X;
		float spacing = 6;
		// Title
		dy = this.drawText(matrixStack, INTRODUCTION, dx, dy, 1.3F);
		dy += spacing - 1;
		// First paragraph
		dy = this.drawText(matrixStack, PARAGRAPH_1, dx, dy, 1.0F);
		dy += spacing;
		// Second paragraph
		dy = this.drawText(matrixStack, PARAGRAPH_2, dx, dy, 1.0F);
		dy += spacing;
		// Third paragraph
		this.drawText(matrixStack, PARAGRAPH_3, dx, dy, 1.0F);
	}

}
