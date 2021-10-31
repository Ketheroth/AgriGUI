package com.ketheroth.agrigui;

import com.infinityraider.agricraft.AgriCraft;
import com.ketheroth.agrigui.client.gui.screen.inventory.SeedAnalyzerScreen;
import com.ketheroth.agrigui.common.ClientProxy;
import com.ketheroth.agrigui.common.inventory.container.SeedAnalyzerContainer;
import com.ketheroth.agrigui.core.registry.AgriGuiContainerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(AgriGUI.MODID)
public class AgriGUI {

	public static final String MODID = "agrigui";
	public static final Logger LOGGER = LogManager.getLogger();

	public AgriGUI() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::clientSetup);

		AgriGuiContainerTypes.CONTAINERS.register(modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ScreenManager.registerFactory(AgriGuiContainerTypes.SEED_ANALYZER_CONTAINER.get(), SeedAnalyzerScreen::new);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBookRightClick(PlayerInteractEvent.RightClickItem event) {
		if (!event.getPlayer().world.isRemote || event.getPlayer().isSneaking()) {
			return;
		}
		if (event.getItemStack().getItem() != AgriCraft.instance.getModItemRegistry().journal.toItem()) {
			return;
		}
		event.setCancellationResult(ActionResultType.SUCCESS);
		event.setCanceled(true);

		ClientProxy.openScreen(event.getItemStack());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onSeedAnalyzerRightClick(PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos =event.getPos();
		BlockState state = event.getWorld().getBlockState(pos);

		if (event.getPlayer().isSneaking() || event.getPlayer().isInLava()) {
			return;
		}
		if (state.getBlock() != AgriCraft.instance.getModBlockRegistry().seed_analyzer.getBlock()) {
			return;
		}
		if (event.getPlayer().world.isRemote) {
			if (state.getBlock() == AgriCraft.instance.getModBlockRegistry().seed_analyzer.getBlock()) {
				event.setCancellationResult(ActionResultType.SUCCESS);
				event.setCanceled(true);
			}
			return;
		}
		event.setCancellationResult(ActionResultType.SUCCESS);
		event.setCanceled(true);
		INamedContainerProvider containerProvider = new INamedContainerProvider() {
			@Nonnull
			@Override
			public ITextComponent getDisplayName() {
				return new TranslationTextComponent("screen.agrigui.seed_analyzer_inventory");
			}

			@Override
			public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
				return new SeedAnalyzerContainer(id, event.getWorld(), playerInventory, pos);
			}
		};
		NetworkHooks.openGui((ServerPlayerEntity) event.getPlayer(), containerProvider, pos);
		System.out.println("hello");
	}

}
