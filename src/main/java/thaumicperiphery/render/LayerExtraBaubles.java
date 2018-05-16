package thaumicperiphery.render;

import org.lwjgl.opengl.GL11;

import baubles.api.BaublesApi;
import baubles.api.render.IRenderBauble.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.lib.UtilsFX;
import thaumicperiphery.Config;
import thaumicperiphery.ModContent;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.crafting.PhantomInkRecipe;

public class LayerExtraBaubles implements LayerRenderer<EntityPlayer> {

	protected final RenderPlayer renderer;
	
	protected static final ResourceLocation girdle_mundane = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/girdle_mundane.png");
	protected static final ResourceLocation girdle_fancy = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/girdle_fancy.png");
	
	public static final ResourceLocation amulet_mundane = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/amulet_mundane.png");
	public static final ResourceLocation amulet_fancy = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/amulet_fancy.png");
	public static final ResourceLocation amulet_vis_stone = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/amulet_vis_stone.png");
	public static final ResourceLocation amulet_vis = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/amulet_vis.png");
	
	public static TextureAtlasSprite visAmuletSprite;
	
	public static final ResourceLocation pauldron = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/pauldron.png");
	public static final ResourceLocation pauldron_repulsion = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/pauldron_repulsion.png");
	
	public static final ResourceLocation focus_pouch = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/focus_pouch.png");
	
	protected static ModelBiped model;
	protected static ModelPauldron modelPauldron;
	protected static ModelRenderer pouchModel;
	
	public LayerExtraBaubles(RenderPlayer renderPlayer) {
		this.renderer = renderPlayer;
	}
	
	@Override
	public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float age, float netHeadYaw, float headPitch, float scale) {
		if (!baubles.common.Config.renderBaubles || player.getActivePotionEffect(MobEffects.INVISIBILITY) != null) return;
		
		int i = player.getBrightnessForRender();
        int j = i % 65536;
        int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		
		if (Config.tcBaubleRenderer) {
			ItemStack belt = BaublesApi.getBaublesHandler(player).getStackInSlot(3);
			if (!belt.isEmpty()) {
				if (belt.getItem() == ItemsTC.baubles && !hasPhantomInk(belt)) {
					renderBelt(player, partialTicks, belt.getMetadata() == 6 ? girdle_fancy : girdle_mundane);
				} else if (belt.getItem() == ItemsTC.focusPouch && !hasPhantomInk(belt)) {
					renderPouch(player, partialTicks, focus_pouch);
				}
			}
			ItemStack amulet = BaublesApi.getBaublesHandler(player).getStackInSlot(0);
			if (!amulet.isEmpty()) {
				if (amulet.getItem() == ItemsTC.baubles && !hasPhantomInk(amulet)) {
					renderAmulet(player, partialTicks, 5, 5, amulet.getMetadata() == 4 ? amulet_fancy : amulet_mundane, null);
				} else if (amulet.getItem() == ItemsTC.amuletVis && !hasPhantomInk(amulet)) {
					boolean found = amulet.getMetadata() == 0;
					renderAmulet(player, partialTicks, found ? 5 : 6, 6, found ? amulet_vis_stone : amulet_vis, found ? null : visAmuletSprite);
				}
			}
		}
		ItemStack body = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
		if (!body.isEmpty()) {
			if (body.getItem() == ModContent.pauldron_repulsion && !hasPhantomInk(body)) {
				renderPauldron(player, limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch, scale, pauldron_repulsion);
			} else if (body.getItem() == ModContent.pauldron && !hasPhantomInk(body)) {
				renderPauldron(player, limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch, scale, pauldron);
			}
		}
	}
	
	protected void renderBelt(EntityPlayer player, float partialTicks, ResourceLocation belt) {
		GlStateManager.pushMatrix();
		GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(belt);
		Helper.rotateIfSneaking(player);
		
		GlStateManager.translate(0F, 0.2F, 0F);

		float s = 1.05F / 16F;
		GlStateManager.scale(s, s, s);
		
		if (model == null) model = new ModelBiped();
		model.bipedBody.render(1F);
		
		GlStateManager.popMatrix();
	}
	
	protected void renderAmulet(EntityPlayer player, float partialTicks, int width, int height, ResourceLocation amulet, TextureAtlasSprite amuletSprite) {
		GlStateManager.pushMatrix();
		GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(amulet);
		Helper.rotateIfSneaking(player);
		
		GlStateManager.translate(0F, -0.0005F, 0F);

		float s = 1.05F / 16F;
		GlStateManager.scale(s, s, s);
		
		if (model == null) model = new ModelBiped();
		model.bipedBody.render(1F);
		
		GlStateManager.scale(0.5f * width, -0.5f * height, 2.5f);
		GlStateManager.translate(-0.5F, -1 - ((14 - height) / 16F), -0.725F);
		
		if (amuletSprite != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			float minU = amuletSprite.getMinU();
			float maxU = amuletSprite.getMaxU();
			float minV = amuletSprite.getMinV();
			float maxV = amuletSprite.getMaxV();
			
			float diffU = maxU - minU;
			float diffV = maxV - minV;
			
			maxU = minU + (diffU * (width / 16F));
			maxV = minV + (diffV * (height / 16F));
			
			renderIconIn3D(Tessellator.getInstance(), minU, minV, maxU, maxV, width, height, 0.25F);
		} else {
			UtilsFX.renderTextureIn3D(1 - (width / 128F), 0.0F, 1.0F, height / 64F, width, height, 0.25F);
		}
		GlStateManager.popMatrix();
	}
	
	protected void renderPauldron(EntityPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float age, float netHeadYaw, float headPitch, float scale, ResourceLocation texture) {
		GlStateManager.pushMatrix();
		GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		float s = 1F / 16F;
		GlStateManager.scale(s, s, s);
		
		if (modelPauldron == null) modelPauldron = new ModelPauldron(0.125F);
		
		modelPauldron.setModelAttributes(this.renderer.getMainModel());
		modelPauldron.render(player, limbSwing, limbSwingAmount, age, netHeadYaw, headPitch, 1F);
		
		GlStateManager.popMatrix();
	}
	
	protected void renderPouch(EntityPlayer player, float partialTicks, ResourceLocation texture) {
		GlStateManager.pushMatrix();
		GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		Helper.rotateIfSneaking(player);
		
		GlStateManager.translate(0F, 0.2F, 0F);
		
		float s = 1.05F / 16F;
		GlStateManager.scale(s, s, s);
		
		if (model == null) model = new ModelBiped();
		if (pouchModel == null) {
			pouchModel = new ModelRenderer(model, 0, 11);
			pouchModel.addBox(1.25F, 6.5F, -3F, 3, 3, 1);
		}
		
		model.bipedBody.render(1.0F);
		pouchModel.render(1.0F);
		
		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	public static boolean hasPhantomInk(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(PhantomInkRecipe.TAG_PHANTOM_INK);
	}
	
	public static void renderIconIn3D(Tessellator tess, float minU, float minV, float maxU, float maxV, int width, int height, float thickness) {
		BufferBuilder wr = tess.getBuffer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		wr.pos(0.0D, 0.0D, 0.0D).tex(minU, maxV).normal(0, 0, 1).endVertex();
		wr.pos(1.0D, 0.0D, 0.0D).tex(maxU, maxV).normal(0, 0, 1).endVertex();
		wr.pos(1.0D, 1.0D, 0.0D).tex(maxU, minV).normal(0, 0, 1).endVertex();
		wr.pos(0.0D, 1.0D, 0.0D).tex(minU, minV).normal(0, 0, 1).endVertex();
		tess.draw();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		wr.pos(0.0D, 1.0D, 0.0F - thickness).tex(minU, minV).normal(0, 0, -1).endVertex();
		wr.pos(1.0D, 1.0D, 0.0F - thickness).tex(maxU, minV).normal(0, 0, -1).endVertex();
		wr.pos(1.0D, 0.0D, 0.0F - thickness).tex(maxU, maxV).normal(0, 0, -1).endVertex();
		wr.pos(0.0D, 0.0D, 0.0F - thickness).tex(minU, maxV).normal(0, 0, -1).endVertex();
		tess.draw();
		float f5 = 0.5F * (minU - maxU) / width;
		float f6 = 0.5F * (maxV - minV) / height;
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		int k;
		float f7;
		float f8;

		for (k = 0; k < width; ++k) {
			f7 = (float) k / (float) width;
			f8 = minU + (maxU - minU) * f7 - f5;
			wr.pos(f7, 0.0D, 0.0F - thickness).tex(f8, maxV).normal(-1, 0, 0).endVertex();
			wr.pos(f7, 0.0D, 0.0D).tex(f8, maxV).normal(-1, 0, 0).endVertex();
			wr.pos(f7, 1.0D, 0.0D).tex(f8, minV).normal(-1, 0, 0).endVertex();
			wr.pos(f7, 1.0D, 0.0F - thickness).tex(f8, minV).normal(-1, 0, 0).endVertex();
		}

		tess.draw();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		float f9;

		for (k = 0; k < width; ++k) {
			f7 = (float) k / (float) width;
			f8 = minU + (maxU - minU) * f7 - f5;
			f9 = f7 + 1.0F / width;
			wr.pos(f9, 1.0D, 0.0F - thickness).tex(f8, minV).normal(1, 0, 0).endVertex();
			wr.pos(f9, 1.0D, 0.0D).tex(f8, minV).normal(1, 0, 0).endVertex();
			wr.pos(f9, 0.0D, 0.0D).tex(f8, maxV).normal(1, 0, 0).endVertex();
			wr.pos(f9, 0.0D, 0.0F - thickness).tex(f8, maxV).normal(1, 0, 0).endVertex();
		}

		tess.draw();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

		for (k = 0; k < height; ++k) {
			f7 = (float)k / (float)height;
			f8 = maxV + (minV - maxV) * f7 - f6;
			f9 = f7 + 1.0F / height;
			wr.pos(0.0D, f9, 0.0D).tex(minU, f8).normal(0, 1, 0).endVertex();
			wr.pos(1.0D, f9, 0.0D).tex(maxU, f8).normal(0, 1, 0).endVertex();
			wr.pos(1.0D, f9, 0.0F - thickness).tex(maxU, f8).normal(0, 1, 0).endVertex();
			wr.pos(0.0D, f9, 0.0F - thickness).tex(minU, f8).normal(0, 1, 0).endVertex();
		}

		tess.draw();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

		for (k = 0; k < height; ++k) {
			f7 = (float)k / (float)height;
			f8 = maxV + (minV - maxV) * f7 - f6;
			wr.pos(1.0D, f7, 0.0D).tex(maxU, f8).normal(0, -1, 0).endVertex();
			wr.pos(0.0D, f7, 0.0D).tex(minU, f8).normal(0, -1, 0).endVertex();
			wr.pos(0.0D, f7, 0.0F - thickness).tex(minU, f8).normal(0, -1, 0).endVertex();
			wr.pos(1.0D, f7, 0.0F - thickness).tex(maxU, f8).normal(0, -1, 0).endVertex();
		}

		tess.draw();
	}

}
