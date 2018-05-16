package thaumicperiphery.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;
import thaumicperiphery.ThaumicPeriphery;

public class ItemMalignantHeart extends Item implements IBauble, IWarpingGear {

	protected static final UUID HEALTH_MODIFIER = UUID.fromString("772B1D0A-558D-11E8-9C2D-FA7AE01BBEBC");
	protected static final UUID DAMAGE_MODIFIER = UUID.fromString("7DF9C8B6-558D-11E8-9C2D-FA7AE01BBEBC");
	protected static final Multimap<String, AttributeModifier> attributeMap = HashMultimap.<String, AttributeModifier>create();
	
	public ItemMalignantHeart() {
		this.setRegistryName(new ResourceLocation(ThaumicPeriphery.MODID, "malignant_heart"));
		this.setUnlocalizedName("malignant_heart");
		
		this.setMaxStackSize(1);
		this.setHasSubtypes(false);
		
		this.setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
		
		this.attributeMap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(HEALTH_MODIFIER, "malignant heart health", -0.5D, 1));
		this.attributeMap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DAMAGE_MODIFIER, "malignant heart damage", 0.5D, 1));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("");
		tooltip.add(TextFormatting.GRAY + I18n.format("bauble.worn"));
		tooltip.add(" " + TextFormatting.BLUE + I18n.format("item.malignant_heart.info.1").replaceAll("&", "%"));
		tooltip.add(" " + TextFormatting.BLUE + I18n.format("item.malignant_heart.info.2").replaceAll("&", "%"));
    }
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
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
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.CHARM;
	}

	@Override
	public int getWarp(ItemStack stack, EntityPlayer player) {
		return 1;
	}

}
