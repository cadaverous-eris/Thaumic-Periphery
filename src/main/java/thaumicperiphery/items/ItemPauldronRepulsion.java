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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;
import thaumicperiphery.ThaumicPeriphery;

public class ItemPauldronRepulsion extends ItemAttributeBauble {

	public static final String TAG_COOLDOWN = "cooldown";

	protected static final UUID ARMOR_MODIFIER = UUID.fromString("371929FC-4CBC-11E8-842F-0ED5F89F718B");
	protected static final UUID TOUGHNESS_MODIFIER = UUID.fromString("22E6BD72-4CBD-11E8-842F-0ED5F89F718B");

	public ItemPauldronRepulsion() {
		super("pauldron_repulsion");
		
		this.attributeMap.put(SharedMonsterAttributes.ARMOR.getName(),
				new AttributeModifier(ARMOR_MODIFIER, "pauldron armor", 2D, 0));
		this.attributeMap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
				new AttributeModifier(TOUGHNESS_MODIFIER, "pauldron armor toughness", 1D, 0));

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		super.onWornTick(stack, player);
		
		int cooldown = getCooldown(stack);
		if (cooldown > 0) setCooldown(stack, cooldown - 1);
	}

	protected int getCooldown(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(TAG_COOLDOWN, Constants.NBT.TAG_INT)) {
				return tag.getInteger(TAG_COOLDOWN);
			}
		}
		return 0;
	}

	protected void setCooldown(ItemStack stack, int cooldown) {
		if (cooldown < 0)
			cooldown = 0;
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();

		if (cooldown <= 0) {
			tag.removeTag(TAG_COOLDOWN);
			if (tag.hasNoTags())
				tag = null;
		} else {
			tag.setInteger(TAG_COOLDOWN, cooldown);
		}

		stack.setTagCompound(tag);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}

	@Override
	public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		Entity source = event.getSource().getImmediateSource();
		if (source != null && source instanceof EntityLivingBase && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack body = BaublesApi.getBaublesHandler(player).getStackInSlot(5);

			if (!body.isEmpty() && body.getItem() == this && getCooldown(body) <= 0) {
				if (!player.world.isRemote) {
					player.world.playSound(null, player.posX, player.posY, player.posZ,
							SoundsTC.poof, SoundCategory.PLAYERS, 1F,
							1.0F + (float) player.getEntityWorld().rand.nextGaussian() * 0.05F);
					
					List<Entity> entities = player.world.getEntitiesWithinAABBExcludingEntity(player,
							player.getEntityBoundingBox().grow(4.5, 2, 4.5));
					
					for (Entity e : entities) {
						if (e instanceof EntityLivingBase) {
							EntityLivingBase mob = (EntityLivingBase) e;
							mob.knockBack(player, 2F, player.posX - mob.posX, player.posZ - mob.posZ);
						}
					}
				}
				setCooldown(body, 50);
			}
		}
	}

}
