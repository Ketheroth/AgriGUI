package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FrontPage extends Page {

	public static final Page INSTANCE = new FrontPage();

	private static final ResourceLocation BACKGROUND_FRONT_RIGHT = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/front_page.png");
	private static final int TEXTURE_WIDTH = 128;
	private static final int TEXTURE_HEIGHT = 192;

	private FrontPage() {
	}

	@Override
	public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
	}

	@Override
	public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
		this.drawTexture(BACKGROUND_FRONT_RIGHT, textureManager, matrixStack, blitOffset, renderX + PAGE_RIGHT_X - 5, renderY + PAGE_RIGHT_Y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

}
