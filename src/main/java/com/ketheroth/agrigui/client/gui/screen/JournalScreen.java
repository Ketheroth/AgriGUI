package com.ketheroth.agrigui.client.gui.screen;

import com.ketheroth.agrigui.AgriGUI;
import com.ketheroth.agrigui.client.renderer.journal.JournalData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JournalScreen extends Screen {

	private static final ResourceLocation PAGE_BACKGROUND = new ResourceLocation(AgriGUI.MODID, "textures/gui/journal_background.png");
	private static final int PAGE_WIDTH = 272;
	private static final int PAGE_HEIGHT = 180;
	//where to place the arrow on the book
	private static final int ARROW_LEFT_X = 29;
	private static final int ARROW_LEFT_Y = 158;
	private static final int ARROW_RIGHT_X = 225;
	private static final int ARROW_RIGHT_Y = 158;
	private PageButton buttonNextPage;
	private PageButton buttonPreviousPage;

	private final JournalData journalData;

	public JournalScreen(ITextComponent name, ItemStack journal) {
		super(name);
		this.journalData = new JournalData(journal);
	}

	@Override
	protected void init() {
		super.init();
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		this.buttonNextPage = this.addButton(new PageButton(renderX + ARROW_RIGHT_X, renderY + ARROW_RIGHT_Y, false, button -> this.nextPage()));
		this.buttonPreviousPage = this.addButton(new PageButton(renderX + ARROW_LEFT_X, renderY + ARROW_LEFT_Y, true, button -> this.previousPage()));
		this.updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(PAGE_BACKGROUND);
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		blit(matrixStack, renderX, renderY, this.getBlitOffset(), 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		journalData.getCurrentPage().drawRightSheet(this.minecraft.getTextureManager(), matrixStack, renderX, renderY, this.getBlitOffset());
		journalData.getCurrentPage().drawLeftSheet(this.minecraft.getTextureManager(), matrixStack, renderX, renderY, this.getBlitOffset());
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.func_243308_b(matrixStack, journalData.getCurrentPage().getTooltipList(mouseX, mouseY, renderX, renderY), mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta > 0) {
			this.previousPage();
		} else if (delta < 0) {
			this.nextPage();
		}
		return true;
	}

	private void previousPage() {
		this.journalData.decrementPage();
		this.updateButtons();
	}

	private void nextPage() {
		this.journalData.incrementPage();
		this.updateButtons();
	}

	private void updateButtons() {
		this.buttonNextPage.visible = journalData.hasAfterCurrent();
		this.buttonNextPage.active = this.buttonNextPage.visible;
		this.buttonPreviousPage.visible = journalData.hasBeforeCurrent();
		this.buttonPreviousPage.active = this.buttonPreviousPage.visible;
	}

	private static class PageButton extends Button {

		private static final int ARROW_WIDTH = 18;
		private static final int ARROW_HEIGHT = 10;

		private final boolean isPrevious;

		public PageButton(int x, int y, boolean isPrevious, Button.IPressable onPress) {
			super(x, y, ARROW_WIDTH, ARROW_HEIGHT, StringTextComponent.EMPTY, onPress);
			this.isPrevious = isPrevious;
		}

		@Override
		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getInstance().getTextureManager().bindTexture(PAGE_BACKGROUND);
			int xOffset = 0;
			if (this.isHovered()) {
				xOffset += 18;
			}
			int yOffset = 180;
			if (this.isPrevious) {
				yOffset += 10;
			}
			blit(matrixStack, this.x, this.y, this.getBlitOffset(), xOffset, yOffset, ARROW_WIDTH, ARROW_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		}

		@Override
		public void playDownSound(SoundHandler handler) {
			handler.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
		}

	}

}
