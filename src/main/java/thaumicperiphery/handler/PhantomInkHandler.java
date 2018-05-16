package thaumicperiphery.handler;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.crafting.PhantomInkRecipe;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;

@Mod.EventBusSubscriber(modid = ThaumicPeriphery.MODID, value = Side.CLIENT)
public class PhantomInkHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (ThaumicPeriphery.botaniaLoaded && stack.hasTagCompound() && stack.getTagCompound().hasKey(PhantomInkRecipe.TAG_PHANTOM_INK)) {
			if (stack.getItem() instanceof ItemBauble) return;
			event.getToolTip().add(1, I18n.format("botaniamisc.hasPhantomInk").replaceAll("&", "\u00a7"));
		}
	}

}
