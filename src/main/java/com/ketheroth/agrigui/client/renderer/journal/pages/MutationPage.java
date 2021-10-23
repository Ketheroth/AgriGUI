package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

public class MutationPage extends Page {

	public static final int LIMIT = 12;

	private final List<List<IAgriPlant>> mutationsLeft;
	private final List<List<IAgriPlant>> mutationsRight;

	public MutationPage(List<List<IAgriPlant>> mutations) {
		int count = mutations.size();
		if (count <= LIMIT / 2) {
			this.mutationsLeft = mutations;
			this.mutationsRight = ImmutableList.of();
		} else {
			this.mutationsLeft = mutations.subList(0, LIMIT / 2 - 1);
			this.mutationsRight = mutations.subList(LIMIT / 2, count - 1);
		}
	}

	@Override
	public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset) {
		int posX = renderX + PAGE_LEFT_X + 10;
		int posY = renderY + PAGE_LEFT_Y + 6;
		int dy = 20;
		for (List<IAgriPlant> plants : this.mutationsLeft) {
			this.drawMutation(textureManager, matrixStack, blitOffset, posX, posY, plants);
			posY += dy;
		}
	}

	@Override
	public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset) {
		int posX = renderX + PAGE_RIGHT_X + 10;
		int posY = renderY + PAGE_RIGHT_Y + 6;
		int dy = 20;
		for (List<IAgriPlant> plants : this.mutationsRight) {
			this.drawMutation(textureManager, matrixStack, blitOffset, posX, posY, plants);
			posY += dy;
		}
	}

	@Override
	public List<ITextComponent> getTooltipList(int mouseX, int mouseY, int renderX, int renderY) {
		int startX = renderX + PAGE_LEFT_X + 10;
		int startY = renderY + PAGE_LEFT_Y + 6;
		for (int y = 0; y < this.mutationsLeft.size(); y++) {
			if (isInSquare(mouseX, mouseY, startX + 1, startX + 1 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsLeft.get(y).get(0).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startX + 35, startX + 35 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsLeft.get(y).get(1).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startX + 69, startX + 69 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsLeft.get(y).get(2).getTooltip());
			}
		}
		startX = renderX + PAGE_RIGHT_X + 10;
		startY = renderY + PAGE_RIGHT_Y + 6;
		for (int y = 0; y < this.mutationsRight.size(); y++) {
			if (isInSquare(mouseX, mouseY, startX + 1, startX + 1 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsRight.get(y).get(0).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startX + 35, startX + 35 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsRight.get(y).get(1).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startX + 69, startX + 69 + 16, startY + 1 + y*20, startY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsRight.get(y).get(2).getTooltip());
			}
		}
		return Collections.emptyList();
	}

}
