package com.ketheroth.agrigui;

import com.infinityraider.agricraft.AgriCraft;
import com.ketheroth.agrigui.common.ClientProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AgriGUI.MODID)
public class AgriGUI {

	public static final String MODID = "agrigui";
	public static final Logger LOGGER = LogManager.getLogger();

	public AgriGUI() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::clientSetup);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBookRightClick(PlayerInteractEvent.RightClickItem event) {
		if (!event.getPlayer().world.isRemote || event.getPlayer().isSneaking()) {
			return;
		}
		if (event.getItemStack().getItem() != AgriCraft.instance.getModItemRegistry().journal.toItem()) {
			return;
		}
		event.setResult(Event.Result.DENY);
		event.setCanceled(true);

		ClientProxy.openScreen(event.getItemStack());
	}

}
