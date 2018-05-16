package thaumicperiphery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.entities.EntityMagicArrow;

public class RenderMagicArrow extends RenderArrow<EntityMagicArrow> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/magic_arrow.png");
	
	public RenderMagicArrow(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicArrow entity) {
		return TEXTURE;
	}
	
	@Override
	public void doRender(EntityMagicArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;
        float f = 0.0F;
        float f1 = 0.5F;
        float f2 = 0.0F;
        float f3 = 0.15625F;
        float f4 = 0.0F;
        float f5 = 0.15625F;
        float f6 = 0.15625F;
        float f7 = 0.3125F;
        float f8 = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float f9 = (float)entity.arrowShake - partialTicks;

        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }
        
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-0.05625F, 0.0F, 0.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, 0.05625F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
            bufferbuilder.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
            bufferbuilder.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
            tessellator.draw();
        }

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        
        FXGeneric fb = new FXGeneric(entity.world, entity.posX, entity.posY, entity.posZ, entity.world.rand.nextGaussian() * 0.01D, entity.world.rand.nextFloat() * 0.075D, entity.world.rand.nextGaussian() * 0.01D);
        fb.setMaxAge(10 + entity.world.rand.nextInt(10));
        if (entity.getIsCritical()) {
        	fb.setRBGColorF(1.0F, 0.2F, 0.6F);
        } else {
        	fb.setRBGColorF(1.0F, 0.2F, 0.8F);
        }
        fb.setAlphaF(0.5F);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(new float[] { 2.0F, 1.0F });
        fb.setLayer(0);
        fb.setSlowDown(1.0D);
        fb.setGravity(0.1F);
        fb.setWind(0.001D);
        ParticleEngine.addEffect(entity.world, fb);
	}
	
	public static class Factory implements IRenderFactory<EntityMagicArrow> {

		@Override
		public Render<EntityMagicArrow> createRenderFor(RenderManager manager) {
			return new RenderMagicArrow(manager);
		}
		
	}

}
