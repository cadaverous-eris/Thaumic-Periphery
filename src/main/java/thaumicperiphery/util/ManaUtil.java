package thaumicperiphery.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;

public class ManaUtil {
	
	public static double getStoredMana(EntityPlayer player) {
		int totalMana = 0;
		int totalManaCapacity = 0;
		
		List<ItemStack> items = ManaItemHandler.getManaItems(player);
		for (ItemStack stack : items) {
			Item item = stack.getItem();
			if (!((IManaItem) item).isNoExport(stack)) {
				totalMana += ((IManaItem) item).getMana(stack);
				totalManaCapacity += ((IManaItem) item).getMaxMana(stack);
			}
			if (item instanceof ICreativeManaProvider && ((ICreativeManaProvider) item).isCreative(stack))
				return 1d;
		}

		Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
		for (ItemStack stack : baubles.values()) {
			Item item = stack.getItem();
			if (!((IManaItem) item).isNoExport(stack)) {
				totalMana += ((IManaItem) item).getMana(stack);
				totalManaCapacity += ((IManaItem) item).getMaxMana(stack);
			}
			if (item instanceof ICreativeManaProvider && ((ICreativeManaProvider) item).isCreative(stack))
				return 1d;
		}
		
		if (totalManaCapacity <= 0 || totalMana <= 0) return 0;
		
		return ((double) totalMana) / ((double) totalManaCapacity);
	}

}
