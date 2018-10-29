package thaumicperiphery.items;

import java.util.List;

import javax.annotation.Nullable;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.lib.UtilsFX;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.crafting.PhantomInkRecipe;

public class ItemVisPhylactery extends ItemBase implements IBauble, IRenderBauble, IRechargable {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/vis_phylactery.png");
	private static ModelBiped model;
	
	public ItemVisPhylactery() {
		super("vis_phylactery");
		
		this.setMaxStackSize(1);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.RARE;
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.AMULET;
	}

	@Override
	public int getMaxCharge(ItemStack arg0, EntityLivingBase arg1) {
		return 300;
	}

	@Override
	public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase arg1) {
		return EnumChargeDisplay.NORMAL;
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		if ((entity instanceof EntityPlayer) && !entity.world.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.ticksExisted % 5 == 0) {
				if (AuraHelper.getVis(player.world, player.getPosition()) > 50) {
					RechargeHelper.rechargeItem(player.world, stack, player.getPosition(), player, 1);
				}
				
				if (RechargeHelper.getCharge(stack) > 0) {
					NonNullList<ItemStack> inv = player.inventory.mainInventory;
					for (int a = 0; a < InventoryPlayer.getHotbarSize(); a++) {
						if (inv.get(a).getItem() != this && RechargeHelper.rechargeItemBlindly(inv.get(a), player, 1) > 0) {
							RechargeHelper.consumeCharge(stack, player, 1);
							return;
						}
					}
					
					IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
					for (int a = 1; a < baubles.getSlots(); a++) {
						if (inv.get(a).getItem() != this && RechargeHelper.rechargeItemBlindly(baubles.getStackInSlot(a), player, 1) > 0) {
							RechargeHelper.consumeCharge(stack, player, 1);
							return;
						}
					}
					
					inv = player.inventory.armorInventory;
					for (int a = 0; a < inv.size(); a++) {
						if (RechargeHelper.rechargeItemBlindly(inv.get(a), player, 1) > 0.0F) {
							RechargeHelper.consumeCharge(stack, player, 1);
							return;
						}
					}
				}
			}
		}
	}
	  
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.amulet_vis.text"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
		if (type == RenderType.BODY && !PhantomInkRecipe.hasPhantomInk(stack)) {
			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
			Helper.rotateIfSneaking(player);
			
			GlStateManager.translate(0F, -0.0005F, 0F);
			
			float s = 1.05F / 16F;
			GlStateManager.scale(s, s, s);
			
			if (model == null) model = new ModelBiped();
			model.bipedBody.render(1F);
			
			GlStateManager.scale(0.5f * 5, -0.5f * 6, 2.5f);
			GlStateManager.translate(-0.5F, -1.5F, -0.725F);
			
			UtilsFX.renderTextureIn3D(1 - (5 / 128F), 0.0F, 1.0F, 6 / 64F, 5, 6, 0.25F);
		}
	}

}
