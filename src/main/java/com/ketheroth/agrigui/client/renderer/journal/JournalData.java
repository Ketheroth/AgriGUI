package com.ketheroth.agrigui.client.renderer.journal;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.ketheroth.agrigui.client.renderer.journal.pages.*;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JournalData {

	private final List<Page> pages;
	private int current;

	public JournalData(ItemStack journal) {
		this.pages = initializePages(journal);
		this.current = 0;
	}

	private static List<Page> initializePages(ItemStack journal) {
		ImmutableList.Builder<Page> builder = new ImmutableList.Builder<>();
		builder.add(FrontPage.INSTANCE);
		builder.add(IntroductionPage.INSTANCE);
		if (journal.getItem() instanceof IAgriJournalItem) {
			IAgriJournalItem journalItem = (IAgriJournalItem) journal.getItem();
			builder.addAll(getPlantPages(journalItem.getDiscoveredSeeds(journal).stream().sorted(Comparator.comparing((plant) -> plant.getPlantName().getString())).collect(Collectors.toList())));
		}
		return builder.build();
	}

	public Page getCurrentPage() {
		return this.pages.get(this.current);
	}

	public void incrementPage() {
		this.current = Math.min(this.pages.size() - 1, this.current + 1);
	}

	public void decrementPage() {
		this.current = Math.max(0, this.current - 1);
	}

	public boolean hasAfterCurrent() {
		return this.current < this.pages.size() - 1;
	}

	public boolean hasBeforeCurrent() {
		return this.current > 0;
	}

	public static List<Page> getPlantPages(List<IAgriPlant> plants) {
		ImmutableList.Builder<Page> pages = ImmutableList.builder();
		plants.forEach(plant -> {
			PlantPage page = new PlantPage(plant, plants);
			pages.add(page);
			List<List<IAgriPlant>> mutations = page.getOffPageMutations();
			int size = mutations.size();
			if (size > 0) {
				int remaining = size;
				int from = 0;
				int to = Math.min(remaining, MutationPage.LIMIT);
				while (remaining > 0) {
					pages.add(new MutationPage(mutations.subList(from, to)));
					remaining -= (to - from);
					from = to;
					to = from + Math.min(remaining, MutationPage.LIMIT);
				}
			}
		});
		return pages.build();
	}

}
