package thaumicperiphery.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumicperiphery.ThaumicPeriphery;

public class ItemPauldron extends Item implements IBauble {

	protected static final UUID ARMOR_MODIFIER = UUID.fromString("371929FC-4CBC-11E8-842F-0ED5F89F718B");
	protected static final UUID TOUGHNESS_MODIFIER = UUID.fromString("22E6BD72-4CBD-11E8-842F-0ED5F89F718B");
	protected static final Multimap<String, AttributeModifier> attributeMap = HashMultimap.<String, AttributeModifier>create();
	
	public ItemPauldron() {
		this.setRegistryName(new ResourceLocation(ThaumicPeriphery.MODID, "pauldron"));
		this.setUnlocalizedName("pauldron");
		
		this.setMaxStackSize(1);
		this.setHasSubtypes(false);
		
		this.setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
		
		this.attributeMap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER, "pauldron armor", 2D, 0));
		this.attributeMap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(TOUGHNESS_MODIFIER, "pauldron armor toughness", 1D, 0));
	}
	
	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		player.getAttributeMap().applyAttributeModifiers(attributeMap);
	}
	
	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		player.getAttributeMap().removeAttributeModifiers(attributeMap);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("");
		tooltip.add(TextFormatting.GRAY + I18n.format("bauble.worn"));
		tooltip.add(" " + TextFormatting.BLUE + I18n.format("item.pauldron.info.1"));
		tooltip.add(" " + TextFormatting.BLUE + I18n.format("item.pauldron.info.2"));
    }
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

}
