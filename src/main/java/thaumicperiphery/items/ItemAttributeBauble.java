package thaumicperiphery.items;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import baubles.api.IBauble;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemAttributeBauble extends ItemBase implements IBauble {

	protected final Multimap<String, AttributeModifier> attributeMap = HashMultimap.<String, AttributeModifier>create();
	
	public ItemAttributeBauble(String name) {
		super(name);
		
		this.setMaxStackSize(1);
	}
	
	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if (!player.world.isRemote) {
			player.getAttributeMap().applyAttributeModifiers(attributeMap);
		}
	}
	
	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		if (!player.world.isRemote) {
			player.getAttributeMap().removeAttributeModifiers(attributeMap);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("");
		tooltip.add(I18n.format("bauble.worn"));
		for (Entry<String, AttributeModifier> entry : this.attributeMap.entries()) {
			AttributeModifier modifier = entry.getValue();
			int op = modifier.getOperation();
			double amount = modifier.getAmount();
			String name = "attribute.name." + entry.getKey();
			
			if (op == 1) amount *= 100;
			
			if (modifier.getAmount() < 0) {
				tooltip.add(TextFormatting.RED + " " + I18n.format("attribute.modifier.take." + op, ItemStack.DECIMALFORMAT.format(Math.abs(amount)), I18n.format(name)));
			} else {
				tooltip.add(TextFormatting.BLUE + " " + I18n.format("attribute.modifier.plus." + op, ItemStack.DECIMALFORMAT.format(amount), I18n.format(name)));
			}
		}
    }
	
}
