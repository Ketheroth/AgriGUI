package com.ketheroth.agrigui.common;

import com.ketheroth.agrigui.client.gui.screen.JournalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientProxy {

	public static void openScreen(ItemStack journal) {
		Minecraft.getInstance().displayGuiScreen(new JournalScreen(new TranslationTextComponent("journalgui"), journal));
	}

}
