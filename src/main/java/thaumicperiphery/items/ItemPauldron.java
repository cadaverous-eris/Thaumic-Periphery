package thaumicperiphery.items;

import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

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

public class ItemPauldron extends ItemAttributeBauble {

	protected static final UUID ARMOR_MODIFIER = UUID.fromString("371929FC-4CBC-11E8-842F-0ED5F89F718B");
	protected static final UUID TOUGHNESS_MODIFIER = UUID.fromString("22E6BD72-4CBD-11E8-842F-0ED5F89F718B");
	
	public ItemPauldron() {
		super("pauldron");
		
		this.setMaxStackSize(1);
		this.setHasSubtypes(false);
		
		this.attributeMap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER, "pauldron armor", 2D, 0));
		this.attributeMap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(TOUGHNESS_MODIFIER, "pauldron armor toughness", 1D, 0));
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

}
