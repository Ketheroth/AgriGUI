package com.ketheroth.agrigui.client.renderer.journal.pages;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlantPage extends Page {

	private static final ITextComponent GROWTH_STAGES = new TranslationTextComponent("agricraft.tooltip.growth_stages");
	private static final ITextComponent GROWTH_REQUIREMENTS = new TranslationTextComponent("agricraft.tooltip.growth_requirements");
	private static final ITextComponent PRODUCTS = new TranslationTextComponent("agricraft.tooltip.products");
	private static final ITextComponent MUTATIONS = new TranslationTextComponent("agricraft.tooltip.mutations");

	private final IAgriPlant plant;
	private final List<IAgriPlant> all;
	private final List<IAgriGrowthStage> stages;

	private final boolean[] brightnessMask;
	private final boolean[] humidityMask;
	private final boolean[] acidityMask;
	private final boolean[] nutrientsMask;
	private final boolean[] seasonMask;

	private final List<ItemStack> drops;
	private final List<List<IAgriPlant>> mutationsOnPage;
	private final List<List<IAgriPlant>> mutationsOffPage;

	public PlantPage(IAgriPlant plant, List<IAgriPlant> all) {
		this.plant = plant;
		this.all = all;
		this.stages = plant.getGrowthStages().stream().sorted(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage)).collect(Collectors.toList());
		this.brightnessMask = new boolean[16];
		IAgriGrowthRequirement req = this.plant.getGrowthRequirement(this.plant.getInitialGrowthStage());
		for (int light = 0; light < this.brightnessMask.length; light++) {
			this.brightnessMask[light] = req.getLightLevelResponse(light, 1).isFertile();
		}
		this.humidityMask = new boolean[IAgriSoil.Humidity.values().length - 1];
		for (int humidity = 0; humidity < this.humidityMask.length; humidity++) {
			this.humidityMask[humidity] = req.getSoilHumidityResponse(IAgriSoil.Humidity.values()[humidity], 1).isFertile();
		}
		this.acidityMask = new boolean[IAgriSoil.Acidity.values().length - 1];
		for (int acidity = 0; acidity < this.acidityMask.length; acidity++) {
			this.acidityMask[acidity] = req.getSoilAcidityResponse(IAgriSoil.Acidity.values()[acidity], 1).isFertile();
		}
		this.nutrientsMask = new boolean[IAgriSoil.Nutrients.values().length - 1];
		for (int nutrients = 0; nutrients < this.nutrientsMask.length; nutrients++) {
			this.nutrientsMask[nutrients] = req.getSoilNutrientsResponse(IAgriSoil.Nutrients.values()[nutrients], 1).isFertile();
		}
		this.seasonMask = new boolean[AgriSeason.values().length - 1];
		for (int season = 0; season < this.seasonMask.length; season++) {
			this.seasonMask[season] = req.getSeasonResponse(AgriSeason.values()[season], 1).isFertile();
		}
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		this.plant.getAllPossibleProducts(builder::add);
		this.drops = builder.build();
		List<List<IAgriPlant>> mutations = Stream.concat(
				this.gatherMutationSprites(mutation -> mutation.hasParent(this.plant)),
				this.gatherMutationSprites(mutation -> mutation.hasChild(this.plant))
		).collect(Collectors.toList());
		int count = mutations.size();
		if (count <= 4) {
			this.mutationsOnPage = mutations.subList(0, count);
			this.mutationsOffPage = ImmutableList.of();
		} else {
			this.mutationsOnPage = mutations.subList(0, 4);
			this.mutationsOffPage = mutations.subList(4, count);
		}
	}

	protected Stream<List<IAgriPlant>> gatherMutationSprites(Predicate<IAgriMutation> filter) {
		return AgriApi.getMutationRegistry().stream()
				.filter(filter).map(mutation ->
						Stream.of(mutation.getParents().get(0), mutation.getParents().get(1), mutation.getChild())
								.map(plant -> {
									if (this.isPlantKnown(plant)) {
										return plant;
									} else {
										return NoPlant.getInstance();
									}
								})
								.collect(Collectors.toList()));
	}

	protected boolean isPlantKnown(IAgriPlant plant) {
		if (AgriCraft.instance.getConfig().progressiveJEI()) {
			return all.contains(plant) || CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), plant);
		}
		return true;
	}

	public List<List<IAgriPlant>> getOffPageMutations() {
		return this.mutationsOffPage;
	}

	int cacheTopY = -1;
	@Override
	public void drawLeftSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
		// Title
		this.drawTexture(Textures.TITLE, textureManager, matrixStack, blitOffset, renderX + PAGE_LEFT_X, renderY + 2 + PAGE_LEFT_Y, 128, 20);
		this.drawText(matrixStack, this.plant.getSeedName(), renderX + PAGE_LEFT_X + 30, renderY + PAGE_LEFT_Y + 6, 1.3F);
		// Description
		int topY = this.drawText(matrixStack, this.plant.getInformation(), renderX + PAGE_LEFT_X + 10, renderY + PAGE_LEFT_Y + 30, 1.0F);
		// Growth requirements
		topY = Math.max(topY, renderY + PAGE_LEFT_Y + 70);
		topY = this.drawGrowthRequirements(textureManager, matrixStack, blitOffset, renderX + PAGE_LEFT_X, topY);
		this.cacheTopY = topY;
		// Seed
		this.plant.getGuiRenderer().drawSeed(this.plant, new Renderer(blitOffset), matrixStack, renderX + PAGE_LEFT_X + 4, renderY + PAGE_LEFT_Y + 4, 16, 16);
		// Products
		this.drawProducts(textureManager, matrixStack, blitOffset, renderX + PAGE_LEFT_X, topY);
	}

	@Override
	public void drawRightSheet(TextureManager textureManager, MatrixStack matrixStack, int renderX, int renderY, int blitOffset, ItemStack stack, IAgriJournalItem journal) {
		// Growth stages
		int topY = this.drawGrowthStages(textureManager, matrixStack, blitOffset, renderX + PAGE_RIGHT_X + 5, renderY + PAGE_RIGHT_Y);
		// Mutations
		this.drawMutations(textureManager, matrixStack, blitOffset, renderX + PAGE_RIGHT_X + 5, topY);
	}

	protected int drawGrowthRequirements(TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int renderX, int renderY) {
		int dy = this.drawText(matrixStack, GROWTH_REQUIREMENTS, renderX + 10, renderY, 1.2F) + 1;
		dy = (int) Math.ceil(dy);
		// Light level
		this.drawTexture(Textures.BRIGHTNESS_BAR, textureManager, matrixStack, blitOffset, renderX + 10, dy, 66, 8);
		for (int i = 0; i < this.brightnessMask.length; i++) {
			boolean current = this.brightnessMask[i];
			if (current) {
				boolean prev = i > 0 && this.brightnessMask[i - 1];
				boolean next = i < (this.brightnessMask.length - 1) && this.brightnessMask[i + 1];
				this.drawTexture(Textures.BRIGHTNESS_HIGHLIGHT, textureManager, matrixStack, blitOffset, renderX + 10 + 4 * i + 1, dy, 4, 8, 1, 0, 2, 8);
				this.drawTexture(Textures.BRIGHTNESS_HIGHLIGHT, textureManager, matrixStack, blitOffset, renderX + 10 + 4 * i + 3, dy, 4, 8, 1, 0, 2, 8);
				if (!prev) {
					this.drawTexture(Textures.BRIGHTNESS_HIGHLIGHT, textureManager, matrixStack, blitOffset, renderX + 10 + 4 * i, dy, 4, 8, 0, 0, 1, 8);
				}
				if (!next) {
					this.drawTexture(Textures.BRIGHTNESS_HIGHLIGHT, textureManager, matrixStack, blitOffset, renderX + 10 + 4 * i + 5, dy, 4, 8, 3, 0, 1, 8);
				}
			}
		}
		dy += 9;
		// Seasons
		if (AgriApi.getSeasonLogic().isActive()) {
			int dx = 70;
			int w = 10;
			int h = 12;
			for (int i = 0; i < this.seasonMask.length; i++) {
				int x = (i % 2) * (w + 2) + 5;
				int y = (i / 2) * (h + 2) + 6;
				if (this.seasonMask[i]) {
					this.drawTexture(Textures.SEASONS_FILLED, textureManager, matrixStack, blitOffset, renderX + x + dx, y + dy, 10, 48, 0, i * h, 10, 12);
				} else {
					this.drawTexture(Textures.SEASONS_EMPTY, textureManager, matrixStack, blitOffset, renderX + x + dx, y + dy, 10, 48, 0, i * h, 10, 12);
				}
			}
		}
		// Humidity
		for (int i = 0; i < this.humidityMask.length; i++) {
			int dx = Textures.HUMIDITY_OFFSETS[i];
			int w = Textures.HUMIDITY_OFFSETS[i + 1] - Textures.HUMIDITY_OFFSETS[i];
			if (this.humidityMask[i]) {
				this.drawTexture(Textures.HUMIDITY_FILLED, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			} else {
				this.drawTexture(Textures.HUMIDITY_EMPTY, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			}
		}
		dy += 13;
		// Acidity
		for (int i = 0; i < this.acidityMask.length; i++) {
			int dx = Textures.ACIDITY_OFFSETS[i];
			int w = Textures.ACIDITY_OFFSETS[i + 1] - Textures.ACIDITY_OFFSETS[i];
			if (this.acidityMask[i]) {
				this.drawTexture(Textures.ACIDITY_FILLED, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			} else {
				this.drawTexture(Textures.ACIDITY_EMPTY, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			}
		}
		dy += 13;
		// Nutrients
		for (int i = 0; i < this.nutrientsMask.length; i++) {
			int dx = Textures.NUTRIENTS_OFFSETS[i];
			int w = Textures.NUTRIENTS_OFFSETS[i + 1] - Textures.NUTRIENTS_OFFSETS[i];
			if (this.nutrientsMask[i]) {
				this.drawTexture(Textures.NUTRIENTS_FILLED, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			} else {
				this.drawTexture(Textures.NUTRIENTS_EMPTY, textureManager, matrixStack, blitOffset, renderX + 10 + dx, dy, 53, 12, dx, 0, w, 12);
			}
		}
		return dy + 5;
	}

	protected int drawGrowthStages(TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int renderX, int renderY) {
		int delta = 20;
		// draw text
		int topY = this.drawText(matrixStack, GROWTH_STAGES, renderX, renderY, 1.2F) + 2;
		// draw stages
		for (int i = 0; i < this.stages.size(); i++) {
			int dx = delta * (i % 4);
			int dy = delta * (i / 4);

			this.drawTexture(Textures.GROWTH_STAGE, textureManager, matrixStack, blitOffset, renderX + dx, topY + dy, 18, 18);
			this.plant.getGuiRenderer().drawGrowthStage(this.plant, this.stages.get(i), new Renderer(blitOffset), matrixStack, renderX + dx + 1, topY + dy + 1, 16, 16);
		}
		return topY + delta * this.stages.size() / 4;
	}

	protected void drawProducts(TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int renderX, int renderY) {
		this.drawText(matrixStack, PRODUCTS, renderX + 10, renderY + 15, 1.2F);
		for (int i = 0; i < this.drops.size(); i++) {
			this.drawTexture(Textures.MUTATION, textureManager, matrixStack, blitOffset, renderX + 12 + i * 20 + PAGE_WIDTH / 2, renderY + 9, 83, 18, 0, 0, 18, 18);
			Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(this.drops.get(i), renderX + 13 + i * 20 + PAGE_WIDTH / 2, renderY + 10);
		}
	}

	protected void drawMutations(TextureManager textureManager, MatrixStack matrixStack, int blitOffset, int renderX, int renderY) {
		this.drawText(matrixStack, MUTATIONS, renderX, renderY + 2, 1.2F);
		int posY = renderY + 12;
		int dy = 20;
		for (List<IAgriPlant> plants : this.mutationsOnPage) {
			this.drawMutation(textureManager, matrixStack, blitOffset, renderX, posY, plants);
			posY += dy;
		}
	}

	@Override
	public List<ITextComponent> getTooltipList(int mouseX, int mouseY, int renderX, int renderY) {
		// seed item
		if (isInSquare(mouseX, mouseY, renderX + PAGE_LEFT_X + 4, renderX + PAGE_LEFT_X + 4 + 16, renderY + 4 + PAGE_LEFT_Y, renderY + 4 + PAGE_LEFT_Y + 16)) {
			return Collections.singletonList(this.plant.getTooltip());
		}
		int startReqX = renderX + PAGE_LEFT_X;
		int startReqY = Math.max(fakeDrawText(this.plant.getInformation(), renderY + PAGE_LEFT_Y + 30, 1.0F), renderY + PAGE_LEFT_Y + 70);
		startReqY = fakeDrawText(GROWTH_REQUIREMENTS, startReqY, 1.2F);
		// brightness requirement
		for (int i = 0; i < this.brightnessMask.length; i++) {
			if (isInSquare(mouseX, mouseY, startReqX + 10 + 4*i, startReqX + 10 + 4 + 4*i, startReqY, startReqY + 8)) {
				return Collections.singletonList(new TranslationTextComponent("agricraft.tooltip.light").appendString(" " + i));
			}
		}
		startReqY+=9;
		// seasons requirement
		if (AgriApi.getSeasonLogic().isActive()) {
			int dx = 70;
			int w = 10;
			int h = 12;
			for (int i = 0; i < this.seasonMask.length; i++) {
				int x = (i % 2) * (w + 2) + 5;
				int y = (i / 2) * (h + 2) + 6;
				int beginX = startReqX + x + dx;
				int beginY = startReqY + y;
				if (isInSquare(mouseX, mouseY, beginX, beginX + 10, beginY, beginY + 12)) {
					if (this.seasonMask[i]) {
						return Collections.singletonList(AgriSeason.values()[i].getDisplayName());
					}
				}
			}
		}
		// humidity requirement
		for (int i = 0; i < this.humidityMask.length; i++) {
			int dx = Textures.HUMIDITY_OFFSETS[i];
			int w = Textures.HUMIDITY_OFFSETS[i + 1] - Textures.HUMIDITY_OFFSETS[i];
			if (isInSquare(mouseX, mouseY, startReqX + 10 + dx, startReqX + 10 + dx + w, startReqY, startReqY + 12)) {
				if (this.humidityMask[i]) {
					return Collections.singletonList(IAgriSoil.Humidity.values()[i].getDescription());
				} else {
					return Collections.emptyList();
				}
			}
		}
		startReqY+=13;
		// acidity requirement
		for (int i = 0; i < this.acidityMask.length; i++) {
			int dx = Textures.ACIDITY_OFFSETS[i];
			int w = Textures.ACIDITY_OFFSETS[i + 1] - Textures.ACIDITY_OFFSETS[i];
			if (isInSquare(mouseX, mouseY, startReqX + 10 + dx, startReqX + 10 + dx + w, startReqY, startReqY + 12)) {
				if (this.acidityMask[i]) {
					return Collections.singletonList(IAgriSoil.Acidity.values()[i].getDescription());
				} else {
					return Collections.emptyList();
				}
			}
		}
		startReqY+=13;
		// nutrients requirement
		for (int i = 0; i < this.nutrientsMask.length; i++) {
			int dx = Textures.NUTRIENTS_OFFSETS[i];
			int w = Textures.NUTRIENTS_OFFSETS[i + 1] - Textures.NUTRIENTS_OFFSETS[i];
			if (isInSquare(mouseX, mouseY, startReqX + 10 + dx, startReqX + 10 + dx + w, startReqY, startReqY + 12)) {
				if (this.nutrientsMask[i]) {
					return Collections.singletonList(IAgriSoil.Nutrients.values()[i].getDescription());
				} else {
					return Collections.emptyList();
				}
			}
		}
		//products items
		for (int i = 0; i < this.drops.size(); i++) {
			int startX = renderX + 13 + i * 20 + PAGE_WIDTH / 2 + PAGE_LEFT_X;
			int startY = cacheTopY;
			if (isInSquare(mouseX, mouseY, startX, startX + 16, startY + 10, startY + 10 + 16)) {
				return getTooltipFromItem(this.drops.get(i));
			}
		}
		// mutations plants
		int startMutX = renderX + PAGE_RIGHT_X + 5;
		int startMutY = fakeDrawText(GROWTH_STAGES, renderY + PAGE_RIGHT_Y, 1.2F) + 2 + 20 * this.stages.size() / 4 + 12;
		for (int y = 0; y < this.mutationsOnPage.size(); y++) {
			if (isInSquare(mouseX, mouseY, startMutX + 1, startMutX + 1 + 16, startMutY + 1 + y*20, startMutY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsOnPage.get(y).get(0).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startMutX + 35, startMutX + 35 + 16, startMutY + 1 + y*20, startMutY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsOnPage.get(y).get(1).getTooltip());
			} else if (isInSquare(mouseX, mouseY, startMutX + 69, startMutX + 69 + 16, startMutY + 1 + y*20, startMutY + 1 + 16 + y*20)) {
				return Collections.singletonList(this.mutationsOnPage.get(y).get(2).getTooltip());
			}
		}
		return Collections.emptyList();
	}

	private List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
		return itemStack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
	}

	private int fakeDrawText(ITextComponent text, int y, float scale) {
		final int[] dy = {y};
		Minecraft.getInstance().fontRenderer.trimStringToWidth(text.deepCopy().mergeStyle(Style.EMPTY.setFontId(FONT)), (int) (PAGE_WIDTH / scale)).forEach(line -> dy[0] += (Math.ceil(7 * scale)));
		return dy[0];
	}

}
