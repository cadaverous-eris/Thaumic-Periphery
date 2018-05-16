package thaumicperiphery.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import teamroots.embers.item.IEmberItem;
import teamroots.embers.item.IHeldEmberCell;
import teamroots.embers.item.IInventoryEmberCell;

public class EmberUtil {

	public static double getEmberCapacityTotal(EntityPlayer player) {
		double amount = 0.0D;
		for (int i = 0; i < 36; i++) {
			if ((player.inventory.getStackInSlot(i) != ItemStack.EMPTY)
					&& ((player.inventory.getStackInSlot(i).getItem() instanceof IInventoryEmberCell))
					&& ((player.inventory.getStackInSlot(i).getItem() instanceof IEmberItem))) {
				amount += ((IEmberItem) player.inventory.getStackInSlot(i).getItem())
						.getEmberCapacity(player.inventory.getStackInSlot(i));
			}
		}

		if ((player.getHeldItem(EnumHand.OFF_HAND) != ItemStack.EMPTY)
				&& ((player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof IHeldEmberCell))
				&& ((player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof IEmberItem))) {
			amount += ((IEmberItem) player.getHeldItem(EnumHand.OFF_HAND).getItem())
					.getEmberCapacity(player.getHeldItem(EnumHand.OFF_HAND));
		}

		if ((player.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY)
				&& ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof IHeldEmberCell))
				&& ((player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof IEmberItem))) {
			amount += ((IEmberItem) player.getHeldItem(EnumHand.MAIN_HAND).getItem())
					.getEmberCapacity(player.getHeldItem(EnumHand.MAIN_HAND));
		}

		return amount;
	}

}
