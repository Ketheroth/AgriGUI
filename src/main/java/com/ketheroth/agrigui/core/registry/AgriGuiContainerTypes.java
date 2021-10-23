package com.ketheroth.agrigui.core.registry;

import com.ketheroth.agrigui.AgriGUI;
import com.ketheroth.agrigui.common.inventory.container.SeedAnalyzerContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AgriGuiContainerTypes {

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, AgriGUI.MODID);

	public static final RegistryObject<ContainerType<SeedAnalyzerContainer>> SEED_ANALYZER_CONTAINER = CONTAINERS.register("seed_analyzer_container",
			() -> IForgeContainerType.create((id, inv, data) -> new SeedAnalyzerContainer(id, inv.player.getEntityWorld(), inv, data.readBlockPos())));

}
