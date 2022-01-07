package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class IntroductionPages extends Page {

	public static final Page INTRODUCTION = new Introduction();
	public static final Page GENETICS = new Genetics();
	public static final Page STATS = new Stats();
	public static final Page STATS_REQ = new StatsAndReq();
	public static final Page GROWTH_REQS = new GrowthReqs();
	public static final Page GROWTH_REQS_2 = new GrowthReqs2();

	public static final Page[] PAGES = {INTRODUCTION, GENETICS, STATS, STATS_REQ, GROWTH_REQS, GROWTH_REQS_2};

	protected static final int SPACING = 4;

	private static final class Introduction extends IntroductionPages {

		private static final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
		private static final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
		private static final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
		private static final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");
		private static final ITextComponent DISCOVERED = new TranslationTextComponent("agricraft.journal.introduction.discovered");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y - 2;
			int dx = renderX + PAGE_RIGHT_X;
			int spacing = 3;
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
			dy = this.drawText(matrixStack, PARAGRAPH_3, dx, dy, 1.0F);
			dy += spacing;
			// Final paragraph:
			ITextComponent discovered = new StringTextComponent("")
					.appendSibling(DISCOVERED)
					.appendString(": " + journal.getDiscoveredSeeds(stack).size() + " / " + AgriApi.getPlantRegistry().count());
			this.drawText(matrixStack, discovered, dx, dy, 1.0F);
		}

	}

	private static final class Genetics extends IntroductionPages {

		private static final ITextComponent CROP_BREEDING = new TranslationTextComponent("agricraft.journal.crop_breeding");
		private static final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_1");
		private static final ITextComponent PARAGRAPH_L_2 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_2");
		private static final ITextComponent PARAGRAPH_L_3 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_3");

		private static final ResourceLocation DNA_SCHEMATIC = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "textures/journal/dna_schematic.png");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_LEFT_Y + 4;
			int dx = renderX + PAGE_LEFT_X + 8;
			// Title
			dy = this.drawText(matrixStack, CROP_BREEDING, dx, dy, 1.3F);
			dy += SPACING;
			// First paragraph
			dy = this.drawText(matrixStack, PARAGRAPH_L_1, dx, dy, 1.0F);
			dy += SPACING;
			// Second paragraph
			dy = this.drawText(matrixStack, PARAGRAPH_L_2, dx, dy, 1.0F);
			dy += SPACING;
			dy += SPACING;
			// Illustration
			this.drawTexture(DNA_SCHEMATIC, textureManager, matrixStack, blitOffset, dx, dy, 96, 32);

		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y + 13;
			int dx = renderX + PAGE_RIGHT_X;
			// Third paragraph
			this.drawText(matrixStack, PARAGRAPH_L_3, dx, dy, 1.0F);
		}

	}

	private static final class Stats extends IntroductionPages {

		private static final ITextComponent STATS = new TranslationTextComponent("agricraft.journal.stats");
		private static final ITextComponent PARAGRAPH_R_1 = new TranslationTextComponent("agricraft.journal.stats.paragraph_1");
		private static final ITextComponent PARAGRAPH_GROWTH = new TranslationTextComponent("agricraft.journal.stats.growth");
		private static final ITextComponent PARAGRAPH_GAIN = new TranslationTextComponent("agricraft.journal.stats.gain");
		private static final ITextComponent PARAGRAPH_STRENGTH = new TranslationTextComponent("agricraft.journal.stats.strength");
		private static final ITextComponent PARAGRAPH_RESISTANCE = new TranslationTextComponent("agricraft.journal.stats.resistance");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_LEFT_Y + 4;
			int dx = renderX + PAGE_LEFT_X + 8;
			// Title
			dy = this.drawText(matrixStack, STATS, dx, dy, 1.3F);
			dy += SPACING;
			// First paragraph
			dy = this.drawText(matrixStack, PARAGRAPH_R_1, dx, dy, 1.0F);
			dy += SPACING;
			dy += SPACING;
			// Growth
			if (!AgriCraft.instance.getConfig().isGrowthStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().growthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				dy = this.drawText(matrixStack, PARAGRAPH_GROWTH, dx, dy + 1, 1.0F);
				dy += SPACING;
				dy += SPACING;
			}
			// Gain
			if (!AgriCraft.instance.getConfig().isGainStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().gainStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				this.drawText(matrixStack, PARAGRAPH_GAIN, dx, dy + 1, 1.0F);
			}
		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y + 17;
			int dx = renderX + PAGE_RIGHT_X;
			int spacing = 4;
			// Strength
			if (!AgriCraft.instance.getConfig().isStrengthStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().strengthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				dy = this.drawText(matrixStack, PARAGRAPH_STRENGTH, dx, dy + 1, 1.0F);
				dy += spacing;
				dy += spacing;
				dy += spacing;
			}
			// Resistance
			if (!AgriCraft.instance.getConfig().isResistanceStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().resistanceStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				this.drawText(matrixStack, PARAGRAPH_RESISTANCE, dx, dy + 1, 1.0F);
			}
		}

	}

	private static final class StatsAndReq extends IntroductionPages {

		private static final ITextComponent PARAGRAPH_FERTILITY = new TranslationTextComponent("agricraft.journal.stats.fertility");
		private static final ITextComponent PARAGRAPH_MUTATIVITY = new TranslationTextComponent("agricraft.journal.stats.mutativity");

		private static final ITextComponent GROWTH_REQS = new TranslationTextComponent("agricraft.journal.growth_reqs");
		private static final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.growth_reqs.paragraph_1");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_LEFT_Y + 17;
			int dx = renderX + PAGE_LEFT_X + 8;
			// Fertility
			if (!AgriCraft.instance.getConfig().isFertilityStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().fertilityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				dy = this.drawText(matrixStack, PARAGRAPH_FERTILITY, dx, dy + 1, 1.0F);
				dy += SPACING*3;
			}
			// Mutativity
			if (!AgriCraft.instance.getConfig().isMutativityStatHidden()) {
				dy = this.drawText(matrixStack, AgriStatRegistry.getInstance().mutativityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
						dx, dy, 1.1F);
				this.drawText(matrixStack, PARAGRAPH_MUTATIVITY, dx, dy + 1, 1.0F);
			}
		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y + 10;
			int dx = renderX + PAGE_RIGHT_X;
			// Title
			dy = this.drawText(matrixStack, GROWTH_REQS, dx, dy, 1.3F);
			dy += SPACING*3;
			// First paragraph
			this.drawText(matrixStack, PARAGRAPH_L_1, dx, dy, 1.0F);
		}

	}

	private static final class GrowthReqs extends IntroductionPages {

		private static final ITextComponent BRIGHTNESS = new TranslationTextComponent("agricraft.journal.growth_reqs.brightness");
		private static final ITextComponent PARAGRAPH_BRIGHTNESS = new TranslationTextComponent("agricraft.journal.growth_reqs.brightness.desc");
		private static final ITextComponent HUMIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.humidity");
		private static final ITextComponent PARAGRAPH_HUMIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.humidity.desc");
		private static final ITextComponent ACIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.acidity");
		private static final ITextComponent PARAGRAPH_ACIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.acidity.desc");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_LEFT_Y + 4;
			int dx = renderX + PAGE_LEFT_X + 8;

			// Brightness
			dy = this.drawText(matrixStack, BRIGHTNESS, dx, dy, 1.1F);
			this.drawTexture(Textures.BRIGHTNESS_BAR, textureManager, matrixStack, blitOffset, dx, dy + 2, 66, 8);
			dy += (7 + SPACING);
			dy = this.drawText(matrixStack, PARAGRAPH_BRIGHTNESS, dx, dy, 1.0F);
			dy += SPACING;
			dy += SPACING;

			// Humidity
			dy = this.drawText(matrixStack, HUMIDITY, dx, dy, 1.1F);
			dy = this.drawText(matrixStack, PARAGRAPH_HUMIDITY, dx, dy + 1, 1.0F);
			this.drawSoilProperties(textureManager, matrixStack, blitOffset, dx, dy - 1, IAgriSoil.Humidity.values(),
					Textures.HUMIDITY_OFFSETS, Textures.HUMIDITY_FILLED);
		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y + 14;
			int dx = renderX + PAGE_RIGHT_X;

			// Acidity
			dy = this.drawText(matrixStack, ACIDITY, dx, dy, 1.1F);
			dy = this.drawText(matrixStack, PARAGRAPH_ACIDITY, dx, dy + 1, 1.0F);
			this.drawSoilProperties(textureManager, matrixStack, blitOffset, dx, dy + 5, IAgriSoil.Acidity.values(),
					Textures.ACIDITY_OFFSETS, Textures.ACIDITY_FILLED);

		}

	}

	private static final class GrowthReqs2 extends IntroductionPages {

		private static final ITextComponent NUTRIENTS = new TranslationTextComponent("agricraft.journal.growth_reqs.nutrients");
		private static final ITextComponent PARAGRAPH_NUTRIENTS = new TranslationTextComponent("agricraft.journal.growth_reqs.nutrients.desc");
		private static final ITextComponent SEASONS = new TranslationTextComponent("agricraft.journal.growth_reqs.seasons");
		private static final ITextComponent PARAGRAPH_SEASONS = new TranslationTextComponent("agricraft.journal.growth_reqs.seasons.desc");

		@Override
		public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_LEFT_Y + 24;
			int dx = renderX + PAGE_LEFT_X + 8;

			// Nutrients
			dy = this.drawText(matrixStack, NUTRIENTS, dx, dy, 1.1F);
			dy = this.drawText(matrixStack, PARAGRAPH_NUTRIENTS, dx, dy, 1.0F);
			this.drawSoilProperties(textureManager, matrixStack, blitOffset, dx, dy, IAgriSoil.Nutrients.values(),
					Textures.NUTRIENTS_OFFSETS, Textures.NUTRIENTS_FILLED);

		}

		@Override
		public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
			int dy = renderY + PAGE_RIGHT_Y + 19;
			int dx = renderX + PAGE_RIGHT_X + 2;
			int spacing = 4;

			// Seasons
			if (AgriApi.getSeasonLogic().isActive()) {
				dy = this.drawText(matrixStack, SEASONS, dx, dy, 1.1F);
				dy = this.drawText(matrixStack, PARAGRAPH_SEASONS, dx, dy, 1.0F);
				dy += spacing;
				dx += 10;
				for (int i = 0; i < AgriSeason.values().length - 1; i++) {
					int w = 10;
					int h = 12;
					this.drawTexture(Textures.SEASONS_FILLED, textureManager, matrixStack, blitOffset, dx, dy - spacing / 2, 10, 48, 0, h * i, w, h);
					dy = this.drawText(matrixStack, AgriSeason.values()[i].getDisplayName(), dx + 12, dy, 1.0F);
					dy += spacing + 1;
				}
			}
		}

	}

	protected int drawSoilProperties(TextureManager textureManager, MatrixStack transforms, int blitOffset, int dx, int dy,
	                                 IAgriSoil.SoilProperty[] property, int[] offsets, ResourceLocation texture) {
		dx += 10;
		dy += SPACING;
		for (int i = 0; i < property.length - 1; i++) {
			int w = offsets[i + 1] - offsets[i];
			float uOffset = offsets[i];
			this.drawTexture(texture, textureManager, transforms, blitOffset, dx, dy - SPACING / 2, 53, 12, uOffset, 0, w, 12);
			dy = this.drawText(transforms, property[i].getDescription(), dx + 10, dy, 1.0F);
			dy += SPACING;
		}
		return dy + SPACING;
	}

}
