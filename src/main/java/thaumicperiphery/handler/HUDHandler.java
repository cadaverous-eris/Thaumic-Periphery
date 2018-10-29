package thaumicperiphery.handler;

import java.awt.Color;
import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.util.EmberInventoryUtil;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.events.HudHandler;
import thaumicperiphery.Config;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.items.ItemCasterElementium;
import thaumicperiphery.items.ItemCasterEmber;
import thaumicperiphery.util.ManaUtil;

@Mod.EventBusSubscriber(modid = ThaumicPeriphery.MODID, value = Side.CLIENT)
public class HUDHandler {

	public static final ResourceLocation TC_HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");	
	private static final DecimalFormat smallFormatter = new DecimalFormat("####");
	private static final DecimalFormat largeFormatter = new DecimalFormat("#.#k");
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public static void renderTick(final TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			final Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.getRenderViewEntity() instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();
				final long time = System.currentTimeMillis();
				if (player != null && mc.inGameHasFocus && Minecraft.isGuiEnabled()) {
					ItemStack stack = player.getHeldItemMainhand();
					for (int i = 0; i < 2; i++) {
						if (Config.emberCaster && stack.getItem() != null && stack.getItem() instanceof ItemCasterEmber) {
							renderEmberCasterHUD(mc, event.renderTickTime, player, time);
							break;
						}
						if (Config.manaCaster && stack.getItem() != null && stack.getItem() instanceof ItemCasterElementium) {
							renderManaCasterHUD(mc, event.renderTickTime, player, time);
							break;
						}
						stack = player.getHeldItemOffhand();
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void renderEmberCasterHUD(Minecraft mc, float renderTickTime, EntityPlayer player, long time) {
		short short1 = 240;
	    short short2 = 240;
	    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
	    net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	    
	    GL11.glPushMatrix();
	    
	    ScaledResolution sr = new ScaledResolution(mc);
	    GL11.glClear(256);
	    GL11.glMatrixMode(5889);
	    GL11.glLoadIdentity();
	    GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
	    GL11.glMatrixMode(5888);
	    GL11.glLoadIdentity();
	    int l = sr.getScaledHeight();
	    
	    int dialLocation = thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS.dialBottom ? l - 32 : 0;
	    
	    GL11.glTranslatef(0.0F, dialLocation, -2000.0F);
	    
	    GL11.glEnable(3042);
	    GL11.glBlendFunc(770, 771);
	    
	    mc.renderEngine.bindTexture(TC_HUD);
	    
	    GL11.glTranslatef(16.0F, 16.0F, 0.0F);
	    
	    double max = EmberInventoryUtil.getEmberCapacityTotal(player);
	    double amount = EmberInventoryUtil.getEmberTotal(player);
	    
	    GL11.glPushMatrix();

	    GL11.glTranslatef(16.0F, -10.0F, 0.0F);
	    GL11.glScaled(0.5D, 0.5D, 0.5D);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    
	    int loc = (int) (30 * amount / max);
	    
	    GL11.glPushMatrix();
	    Color ac = new Color(Aspect.FIRE.getColor());
	    
	    UtilsFX.drawTexturedQuad(-4.0F, 5F, 104.0F, 0.0F, 8.0F, 30F, -90.0D);
	    GL11.glColor4f(ac.getRed() / 255.0F, ac.getGreen() / 255.0F, ac.getBlue() / 255.0F, 0.8F);
	    UtilsFX.drawTexturedQuad(-4.0F, 35 - loc, 104.0F, 0.0F, 8.0F, loc, -90.0D);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glPopMatrix();
	    
	    GL11.glPushMatrix();
	    UtilsFX.drawTexturedQuad(-8.0F, -3.0F, 72.0F, 0.0F, 16.0F, 42.0F, -90.0D);
	    GL11.glPopMatrix();
	    
	    int sh = 0;

	    if (player.isSneaking()) {
	    	GL11.glPushMatrix();
	    	GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
	    	String msg;
	    	if (MathHelper.floor(amount) > 9999) msg = largeFormatter.format(amount / 1000);
	    	else msg = smallFormatter.format(amount);
	    	mc.ingameGUI.drawString(mc.fontRenderer, msg, -32, -4, 16777215);
	    	GL11.glPopMatrix();
	      
	      mc.renderEngine.bindTexture(TC_HUD);
	    }
	    
	    GL11.glPopMatrix();
	    
	    GL11.glDisable(3042);
	    
	    GL11.glPopMatrix();
	}
	
	@SideOnly(Side.CLIENT)
	private static void renderManaCasterHUD(Minecraft mc, float renderTickTime, EntityPlayer player, long time) {
		short short1 = 240;
	    short short2 = 240;
	    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
	    net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	    
	    GL11.glPushMatrix();
	    
	    ScaledResolution sr = new ScaledResolution(mc);
	    GL11.glClear(256);
	    GL11.glMatrixMode(5889);
	    GL11.glLoadIdentity();
	    GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
	    GL11.glMatrixMode(5888);
	    GL11.glLoadIdentity();
	    int l = sr.getScaledHeight();
	    
	    int dialLocation = thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS.dialBottom ? l - 32 : 0;
	    
	    GL11.glTranslatef(0.0F, dialLocation, -2000.0F);
	    
	    GL11.glEnable(3042);
	    GL11.glBlendFunc(770, 771);
	    
	    mc.renderEngine.bindTexture(TC_HUD);
	    
	    GL11.glTranslatef(16.0F, 16.0F, 0.0F);
	    
	    double manaPercentage = ManaUtil.getStoredMana(player);
	    
	    GL11.glPushMatrix();

	    GL11.glTranslatef(16.0F, -10.0F, 0.0F);
	    GL11.glScaled(0.5D, 0.5D, 0.5D);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    
	    int loc = (int) (30 * manaPercentage);
	    
	    GL11.glPushMatrix();
	    Color ac = new Color(Aspect.ALCHEMY.getColor());
	    
	    UtilsFX.drawTexturedQuad(-4.0F, 5F, 104.0F, 0.0F, 8.0F, 30F, -90.0D);
	    GL11.glColor4f(ac.getRed() / 255.0F, ac.getGreen() / 255.0F, ac.getBlue() / 255.0F, 0.8F);
	    UtilsFX.drawTexturedQuad(-4.0F, 35 - loc, 104.0F, 0.0F, 8.0F, loc, -90.0D);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glPopMatrix();
	    
	    GL11.glPushMatrix();
	    UtilsFX.drawTexturedQuad(-8.0F, -3.0F, 72.0F, 0.0F, 16.0F, 42.0F, -90.0D);
	    GL11.glPopMatrix();
	    
	    GL11.glPopMatrix();
	    
	    GL11.glDisable(3042);
	    
	    GL11.glPopMatrix();
	}

}
