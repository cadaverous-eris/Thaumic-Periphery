package thaumicperiphery.proxy;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumicperiphery.Config;
import thaumicperiphery.ModContent;
import thaumicperiphery.entities.EntityMagicArrow;
import thaumicperiphery.render.LayerExtraBaubles;
import thaumicperiphery.render.RenderMagicArrow;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityMagicArrow.class, new RenderMagicArrow.Factory());
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		registerColorHandlers();
        
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render;
		render = skinMap.get("default");
		render.addLayer(new LayerExtraBaubles(render));

		render = skinMap.get("slim");
		render.addLayer(new LayerExtraBaubles(render));
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	protected void registerColorHandlers() {
		final IItemColor itemCasterColorHandler = (stack, tintIndex) -> {
            final ItemCaster item = (ItemCaster) stack.getItem();
            final ItemFocus focus = item.getFocus(stack);
            return (tintIndex > 0 && focus != null) ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
        };
        if (Config.emberCaster) Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemCasterColorHandler, ModContent.caster_ember);
        if (Config.manaCaster) Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemCasterColorHandler, ModContent.caster_elementium);
	}

}
