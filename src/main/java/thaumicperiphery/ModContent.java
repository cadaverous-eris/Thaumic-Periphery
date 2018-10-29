package thaumicperiphery;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumicperiphery.compat.MysticalMechanicsCompat;
import thaumicperiphery.crafting.PhantomInkRecipe;
import thaumicperiphery.entities.EntityMagicArrow;
import thaumicperiphery.items.ItemBase;
import thaumicperiphery.items.ItemCasterElementium;
import thaumicperiphery.items.ItemCasterEmber;
import thaumicperiphery.items.ItemMagicQuiver;
import thaumicperiphery.items.ItemMalignantHeart;
import thaumicperiphery.items.ItemPauldron;
import thaumicperiphery.items.ItemPauldronRepulsion;
import thaumicperiphery.items.ItemVisPhylactery;
import thaumicperiphery.render.LayerExtraBaubles;

@Mod.EventBusSubscriber(modid = ThaumicPeriphery.MODID)
@ObjectHolder(ThaumicPeriphery.MODID)
public class ModContent {

	public static final Item caster_ember = null, caster_elementium = null, pauldron = null, pauldron_repulsion = null, malignant_heart = null, magic_quiver = null, vis_phylactery = null, gear_brass = null;
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		
		if (Config.emberCaster) registry.register(new ItemCasterEmber());
		if (Config.manaCaster) registry.register(new ItemCasterElementium());
		
		registry.register(new ItemPauldron());
		registry.register(new ItemPauldronRepulsion());
		registry.register(new ItemMalignantHeart());
		registry.register(new ItemMagicQuiver());
		registry.register(new ItemVisPhylactery());
		
		if (ThaumicPeriphery.mysticalMechanicsLoaded) registry.register(new ItemBase("gear_brass"));
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		int id = 0;
		
		event.getRegistry().register(EntityEntryBuilder.create().entity(EntityMagicArrow.class).id(new ResourceLocation(ThaumicPeriphery.MODID, "magic_arrow"), id++).name("magic_arrow").tracker(64, 1, true).build());
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistry<IRecipe> registry = event.getRegistry();
		
		registry.register(new ShapedOreRecipe(null, new ItemStack(pauldron), " PP", "PIL", "I B", 'P', "plateIron", 'I', "ingotIron", 'L', "leather", 'B', "ingotBrass").setRegistryName(ThaumicPeriphery.MODID, "pauldron"));
		
		if (ThaumicPeriphery.botaniaLoaded) registry.register(new PhantomInkRecipe().setRegistryName(ThaumicPeriphery.MODID, "phantom_ink"));
		
		if (ThaumicPeriphery.mysticalMechanicsLoaded) MysticalMechanicsCompat.initRecipes(event);
		
		
	}
	
	@SubscribeEvent
	public static void registerAspects(AspectRegistryEvent event) {
		AspectEventProxy registry = event.register;
		
		registry.registerComplexObjectTag(new ItemStack(ModContent.pauldron), new AspectList().add(Aspect.PROTECT, 10));
		registry.registerComplexObjectTag(new ItemStack(ModContent.pauldron_repulsion), new AspectList().add(Aspect.PROTECT, 15));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		if (Config.emberCaster) ModelLoader.setCustomModelResourceLocation(caster_ember, 0, new ModelResourceLocation(caster_ember.getRegistryName().toString()));
		if (Config.manaCaster) ModelLoader.setCustomModelResourceLocation(caster_elementium, 0, new ModelResourceLocation(caster_elementium.getRegistryName().toString()));
		
		ModelLoader.setCustomModelResourceLocation(pauldron, 0, new ModelResourceLocation(pauldron.getRegistryName().toString()));
		ModelLoader.setCustomModelResourceLocation(pauldron_repulsion, 0, new ModelResourceLocation(pauldron_repulsion.getRegistryName().toString()));
		ModelLoader.setCustomModelResourceLocation(malignant_heart, 0, new ModelResourceLocation(malignant_heart.getRegistryName().toString()));
		ModelLoader.setCustomModelResourceLocation(magic_quiver, 0, new ModelResourceLocation(magic_quiver.getRegistryName().toString()));
		ModelLoader.setCustomModelResourceLocation(vis_phylactery, 0, new ModelResourceLocation(vis_phylactery.getRegistryName().toString()));
		
		if (ThaumicPeriphery.mysticalMechanicsLoaded) ModelLoader.setCustomModelResourceLocation(gear_brass, 0, new ModelResourceLocation(gear_brass.getRegistryName().toString()));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		LayerExtraBaubles.visAmuletSprite = event.getMap().registerSprite(new ResourceLocation(ThaumicPeriphery.MODID, "model/vis_amulet"));
	}
	
}
