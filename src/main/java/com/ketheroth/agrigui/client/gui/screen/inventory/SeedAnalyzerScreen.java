package com.ketheroth.agrigui.client.gui.screen.inventory;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.ketheroth.agrigui.AgriGUI;
import com.ketheroth.agrigui.common.inventory.container.SeedAnalyzerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SeedAnalyzerScreen extends ContainerScreen<SeedAnalyzerContainer> {
	private final ResourceLocation GUI = new ResourceLocation(AgriGUI.MODID, "textures/gui/seed_analyzer_gui.png");
	private final ITextComponent TEXT_SEPARATOR = new StringTextComponent("-");

	public SeedAnalyzerScreen(SeedAnalyzerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.ySize = 186;
		this.playerInventoryTitleY = this.ySize - 94;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI);
		int relX = (this.width - this.xSize) / 2;
		int relY = (this.height - this.ySize) / 2;
		// background
		this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
		//journal slot
		this.blit(matrixStack, relX + 20 , relY + 70, 176, 73, 18, 18);
		// magnifying glass
		this.blit(matrixStack, relX + 8 , relY + 25, 195, 0, 56, 56);
		List<IAgriGenePair<?>> genes = container.getGeneToRender();
		if (!genes.isEmpty()) {
			int DNA_X = relX + 80;
			// dna
			this.blit(matrixStack, DNA_X , relY + 26, 176, 0, 19, 73);
			int yy = relY + 26;
			for (IAgriGenePair<?> pair : genes) {
				ITextComponent geneText = pair.getGene().getGeneDescription();
				ITextComponent domText = pair.getDominant().getTooltip();
				ITextComponent recText = pair.getRecessive().getTooltip();
				int domw = this.font.getStringWidth(domText.getString());
				if (!(pair.getGene() instanceof GeneSpecies)) { // stats genes
					this.font.drawText(matrixStack, geneText, DNA_X + 36, yy, 0);
					this.font.drawText(matrixStack, domText, DNA_X - domw - 1, yy, 0);
					this.font.drawText(matrixStack, recText, DNA_X + 21, yy, 0);
					yy+=this.font.FONT_HEIGHT + 4;
				} else { // species genes
					int middle = relX + this.xSize/2;
					int sepLength = this.font.getStringWidth(TEXT_SEPARATOR.getString());
					this.font.drawText(matrixStack, TEXT_SEPARATOR, middle - sepLength / 2F, relY + 16, 0);
					this.font.drawText(matrixStack, domText, middle - domw - sepLength / 2F - 1, relY + 16, 0);
					this.font.drawText(matrixStack, recText, middle + sepLength / 2F + 1, relY + 16, 0);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		super.drawGuiContainerForegroundLayer(matrixStack, x, y);
		this.font.drawText(matrixStack, this.title, (float) this.titleX, (float) this.titleY, 4210752);
		this.font.drawText(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, (float) this.playerInventoryTitleY, 4210752);
	}

}
