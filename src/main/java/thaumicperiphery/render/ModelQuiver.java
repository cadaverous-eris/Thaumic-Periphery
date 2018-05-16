package thaumicperiphery.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumicperiphery.ThaumicPeriphery;

public class ModelQuiver extends ModelBiped {
	
	private static final ResourceLocation QUIVER_TEXTURE = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/magic_quiver.png");
	private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(ThaumicPeriphery.MODID, "textures/model/magic_arrow.png");
	
	protected final ModelRendererQuiver quiver;
	
	protected final ModelRendererArrow[] arrows;
	
	public ModelQuiver() {
		this(0.0F);
	}
	
	public ModelQuiver(float modelSize) {
		super(modelSize);
		
		quiver = new ModelRendererQuiver(this, 0, 0);
		arrows = new ModelRendererArrow[] {
				new ModelRendererArrow(this, 0.5F, 9F, 0.666F),
				new ModelRendererArrow(this, 1.5F, 9F, -0.666F),
				new ModelRendererArrow(this, -1.5F, 9F, 0.666F),
				new ModelRendererArrow(this, -0.5F, 9F, -0.666F),
		};
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		quiver.showModel = visible;
	}
	
	public void render(EntityPlayer player, float fill, float scale) {
		Minecraft.getMinecraft().renderEngine.bindTexture(QUIVER_TEXTURE);
		this.bipedBody.render(scale);
		this.quiver.render(scale);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(ARROW_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
		GlStateManager.enableBlend();
		int numArrows = MathHelper.ceil(fill * arrows.length);
		for (int i = 0; i < numArrows; i++) {
			arrows[i].render(scale);
		}
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	protected static class ModelRendererQuiver extends ModelRenderer {

		public ModelRendererQuiver(ModelBase model, int texOffX, int texOffY) {
			super(model, texOffX, texOffY);
			
			setRotationPoint(1.5F, 1.5F, 4.125F);
			rotateAngleZ = 3.1415F / 8;
			
			addBox(-2.5F, -1F, -2F, 5, 8, 4);
			cubeList.add(new ModelBox(this, 0, 12, -2.5F, 7F, -2F, 5, 3, 4, 0.0F) {
				private PositionTextureVertex[] vertexPositions = {
						new PositionTextureVertex(2.5F, 7F, -2F, 0.0F, 8.0F),
						new PositionTextureVertex(1.5F, 10F, -1F, 8.0F, 8.0F),
						new PositionTextureVertex(-1.5F, 10F, -1F, 8.0F, 0.0F),
						new PositionTextureVertex(-2.5F, 7F, 2F, 0.0F, 0.0F),
						new PositionTextureVertex(2.5F, 7F, 2F, 0.0F, 8.0F),
						new PositionTextureVertex(1.5F, 10F, 1F, 8.0F, 8.0F),
						new PositionTextureVertex(-1.5F, 10F, 1F, 8.0F, 0.0F),
						new PositionTextureVertex(-2.5F, 7F, -2F, 0.0F, 0.0F),
				};
				private TexturedQuad[] quadList = {
						new TexturedQuad(new PositionTextureVertex[] {vertexPositions[4], vertexPositions[0], vertexPositions[1], vertexPositions[5]}, 9, 16, 13, 19, textureWidth, textureHeight),
						new TexturedQuad(new PositionTextureVertex[] {vertexPositions[7], vertexPositions[3], vertexPositions[6], vertexPositions[2]}, 0, 16, 4, 19, textureWidth, textureHeight),
						//new TexturedQuad(new PositionTextureVertex[] {vertexPositions[4], vertexPositions[3], vertexPositions[7], vertexPositions[0]}, 4, 12, 9, 16, textureWidth, textureHeight),
						new TexturedQuad(new PositionTextureVertex[] {vertexPositions[1], vertexPositions[2], vertexPositions[6], vertexPositions[5]}, 9, 16, 14, 12, textureWidth, textureHeight),
						new TexturedQuad(new PositionTextureVertex[] {vertexPositions[0], vertexPositions[7], vertexPositions[2], vertexPositions[1]}, 4, 16, 9, 19, textureWidth, textureHeight),
						new TexturedQuad(new PositionTextureVertex[] {vertexPositions[3], vertexPositions[4], vertexPositions[5], vertexPositions[6]}, 13, 16, 18, 19, textureWidth, textureHeight),
				};
				
				@Override
				@SideOnly(Side.CLIENT)
				public void render(BufferBuilder renderer, float scale) {
					for (TexturedQuad texturedquad : this.quadList) {
			            texturedquad.draw(renderer, scale);
			        }
				}
			});
		}
		
	}
	
	protected static class ModelRendererArrow extends ModelRenderer {
			
		public ModelRendererArrow(ModelBase model, float x, float y, float z) {
			super(model, 0, 0);
			
			setRotationPoint(1.5F, 1.5F, 4.125F);
			rotateAngleZ = 3.1415F / 8;
			
			cubeList.add(new ModelArrow(this, x, y, z));
		}
		
	}
	
	protected static class ModelArrow extends ModelBox {

		private static final float c = MathHelper.sqrt(3.125F);
		private static final float a = 2 * c;
		
		private final TexturedQuad[] quadList;
		
		public ModelArrow(ModelRenderer renderer, float x, float y, float z) {
			super(renderer, 0, 0, 0, 0, 0, 0, 0, 0, 0);
			
			quadList = new TexturedQuad[] {
					new TexturedQuad(new PositionTextureVertex[] {
							new PositionTextureVertex(x + c, y - 16, z + c, 0.0F, 0.0F),
							new PositionTextureVertex(x - c, y - 16, z - c, 0.0F, 0.15625F),
							new PositionTextureVertex(x - c, y - 10, z - c, 0.1875F, 0.15625F),
							new PositionTextureVertex(x + c, y - 10, z + c, 0.1875F, 0.0F),
					}),
					new TexturedQuad(new PositionTextureVertex[] {
							new PositionTextureVertex(x + c, y - 16, z - c, 0.0F, 0.0F),
							new PositionTextureVertex(x - c, y - 16, z + c, 0.0F, 0.15625F),
							new PositionTextureVertex(x - c, y - 10, z + c, 0.1875F, 0.15625F),
							new PositionTextureVertex(x + c, y - 10, z - c, 0.1875F, 0.0F),
					}),
					new TexturedQuad(new PositionTextureVertex[] {
							new PositionTextureVertex(x, y - 15, z - a, 0.0F, 8.0F),
							new PositionTextureVertex(x + a, y - 15, z, 0.0F, 0.0F),
							new PositionTextureVertex(x, y - 15, z + a, 0.8F, 0.0F),
							new PositionTextureVertex(x - a, y - 15, z, 8.0F, 8.0F),
					}, 0, 5, 5, 10, 32, 32),
			};
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void render(BufferBuilder renderer, float scale) {
			for (TexturedQuad texturedquad : this.quadList) {
	            texturedquad.draw(renderer, scale);
	        }
		}
		
	}

}
