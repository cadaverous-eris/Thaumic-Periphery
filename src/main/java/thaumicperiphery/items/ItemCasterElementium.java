package thaumicperiphery.items;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumicperiphery.ThaumicPeriphery;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemCasterElementium extends ItemCaster implements IManaUsingItem {
	
	public static final float MANA_RATIO = 50f;
	
	public ItemCasterElementium() {
		super("caster_elementium", 0);
		setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
	}
	
	@Override
	public boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean sim) {
		amount *= getConsumptionModifier(is, player, crafting);

		return ManaItemHandler.requestManaExact(is, player, (int) amount, !sim);
	}
	
	@Override
	public float getConsumptionModifier(ItemStack is, EntityPlayer player, boolean crafting) {
		float consumptionModifier = 1.0F;
		if (player != null)
			consumptionModifier -= ManaItemHandler.getFullDiscountForTools(player, is);
		return Math.max(consumptionModifier, 0.1F) * MANA_RATIO;
	}
	
	@Override
	public void onUpdate(ItemStack is, World w, Entity e, int slot, boolean currentItem) {

	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if ((tab == ThaumicPeriphery.thaumicPeripheryTab) || (tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
		}
	}
	
	DecimalFormat myFormatter = new DecimalFormat("#######.#");

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (getFocus(stack) != null) {
			tooltip.add(TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.GREEN
					+ getFocus(stack).getItemStackDisplayName(getFocusStack(stack)));
			getFocus(stack).addFocusInformation(getFocusStack(stack), worldIn, tooltip, flagIn);
		}
	}

	@Override
	public boolean usesMana(ItemStack arg0) {
		return true;
	}

}
