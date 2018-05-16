package thaumicperiphery.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPauldron extends ModelBiped {
	
	public ModelPauldron() {
		this(0.0F);
	}
	
	public ModelPauldron(float modelSize) {
        super(modelSize);
        
        ModelRenderer bipedShoulderPlate = new ModelRenderer(this, 0, 0);
        bipedShoulderPlate.addBox(-4.5F, -3.5F, -2.5F, 6, 3, 5, modelSize);
        bipedShoulderPlate.rotateAngleZ = (float) (-30.0F * Math.PI / 180);
        
        ModelRenderer platePart1 = new ModelRenderer(this, 22, 0);
        platePart1.addBox(0.0F, -5F, -3.5F, 1, 3, 7, modelSize);
        bipedShoulderPlate.addChild(platePart1);
        
        this.bipedRightArm.addChild(bipedShoulderPlate);
    }
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        
        if (this.isChild) {
            float f = 2.0F;
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
        } else {
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0F, 3.2F, 0.0F);
            }
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
        }

        GlStateManager.popMatrix();
    }
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
	}

}
