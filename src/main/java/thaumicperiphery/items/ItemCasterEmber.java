package thaumicperiphery.items;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.util.EmberInventoryUtil;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.world.aura.AuraHandler;
import thaumicperiphery.ThaumicPeriphery;

public class ItemCasterEmber extends ItemCaster {

	public static final float EMBER_RATIO = 7f;

	public ItemCasterEmber() {
		super("caster_ember", 0);
		setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
	}

	@Override
	public boolean consumeVis(ItemStack is, EntityPlayer player, float amount, boolean crafting, boolean sim) {
		amount *= getConsumptionModifier(is, player, crafting);
		
		amount = ((int) (amount * 10)) / 10F;
		 
		if (EmberInventoryUtil.getEmberTotal(player) < amount)
			return false;

		if (!sim)
			EmberInventoryUtil.removeEmber(player, amount);

		return true;
	}

	@Override
	public float getConsumptionModifier(ItemStack is, EntityPlayer player, boolean crafting) {
		float consumptionModifier = 1.0F;
		if (player != null)
			consumptionModifier -= CasterManager.getTotalVisDiscount(player);
		return Math.max(consumptionModifier, 0.1F) * EMBER_RATIO;
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
		if (stack.hasTagCompound()) {
			String text = "";
			ItemStack focus = getFocusStack(stack);
			if (focus != null && (!focus.isEmpty())) {
				float amt = ((ItemFocus) focus.getItem()).getVisCost(focus) * EMBER_RATIO;
				if (amt > 0.0F) {
					text = "\u00A7r" + myFormatter.format(amt) + " " + I18n.translateToLocal("item.Focus.cost_ember");
				}
			}
			tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.RED
					+ I18n.translateToLocal("thaumicperiphery.ember.cost") + " " + text);
		}

		if (getFocus(stack) != null) {
			tooltip.add(TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.GREEN
					+ getFocus(stack).getItemStackDisplayName(getFocusStack(stack)));
			getFocus(stack).addFocusInformation(getFocusStack(stack), worldIn, tooltip, flagIn);
		}
	}

}
