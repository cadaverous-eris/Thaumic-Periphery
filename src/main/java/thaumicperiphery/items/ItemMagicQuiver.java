package thaumicperiphery.items;

import javax.annotation.Nullable;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.crafting.PhantomInkRecipe;
import thaumicperiphery.entities.EntityMagicArrow;
import thaumicperiphery.render.ModelQuiver;

public class ItemMagicQuiver extends Item implements IBauble, IRenderBauble, IRechargable {

	public static final int VIS_COST = 1;
	
	private static ModelQuiver model;
	
	public ItemMagicQuiver() {
		this.setRegistryName(new ResourceLocation(ThaumicPeriphery.MODID, "magic_quiver"));
		this.setUnlocalizedName("magic_quiver");
		
		this.setMaxStackSize(1);
		this.setHasSubtypes(false);
		
		this.setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
		
		this.addPropertyOverride(new ResourceLocation(ThaumicPeriphery.MODID, "charge"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
				if (entity instanceof EntityPlayer) return RechargeHelper.getChargePercentage(stack, (EntityPlayer) entity);
				return 0.0F;
			}
		});
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
	
	@Override
	public boolean willAutoSync(ItemStack stack, EntityLivingBase entity) {
		return true;
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BODY;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
		if (type != RenderType.BODY || PhantomInkRecipe.hasPhantomInk(stack)) return;
		
		Helper.rotateIfSneaking(player);
		
		float s = 1.00F / 16F;
		GlStateManager.scale(s, s, s);
		
		if (model == null) model = new ModelQuiver(1 / 32F);
		
		float fill = RechargeHelper.getChargePercentage(stack, player);
		model.render(player, fill, 1F);
	}
	
	@Override
	public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
		return 100;
	}
	
	@Override
	public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
		return EnumChargeDisplay.NORMAL;
	}
	
	@SubscribeEvent
	public void onArrowNock(ArrowNockEvent event) {
		if (event.hasAmmo()) return;
		
		EntityPlayer player = event.getEntityPlayer();
		if (player == null) return;
		
		ItemStack body = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
		if (body.getItem() == this && RechargeHelper.getCharge(body) >= VIS_COST) {
			player.setActiveHand(event.getHand());
			event.setAction(new ActionResult<ItemStack>(EnumActionResult.SUCCESS, event.getBow()));
		}
	}
	
	@SubscribeEvent
	public void onArrowLoose(ArrowLooseEvent event) {
		if (event.hasAmmo()) return;
		
		EntityPlayer player = event.getEntityPlayer();
		if (player == null) return;
		
		ItemStack body = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
		if (body.getItem() == this && RechargeHelper.getCharge(body) >= VIS_COST) {
			event.setCanceled(true);
			
			ItemStack bow = event.getBow();
			int charge = event.getCharge();
			float vel = ((ItemBow) (bow.getItem() instanceof ItemBow ? bow.getItem() : Items.BOW)).getArrowVelocity(charge);
			
			if (vel >= 0.1 && RechargeHelper.consumeCharge(body, player, VIS_COST)) {
				World world = event.getWorld();
				if (!world.isRemote) {
					EntityMagicArrow arrow = new EntityMagicArrow(world, player);
					arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, vel * 3F, 1.0F);
					
					int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
					int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
					if (vel == 1.0F) arrow.setIsCritical(true);
					if (power > 0) arrow.setDamage(arrow.getDamage() + power * 0.3D + 0.5D);
					if (punch > 0) arrow.setKnockbackStrength(punch);
					
					bow.damageItem(1, player);
					
					world.spawnEntity(arrow);
				}
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + vel * 0.5F);
				player.addStat(StatList.getObjectUseStats(bow.getItem()));
			}
		}
	}

}
