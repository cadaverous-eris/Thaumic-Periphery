package thaumicperiphery.proxy;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import teamroots.embers.RegistryManager;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.Utils;
import thaumicperiphery.Config;
import thaumicperiphery.ModContent;
import thaumicperiphery.ThaumicPeriphery;
import thaumicperiphery.compat.MysticalMechanicsCompat;
import thaumicperiphery.items.ItemPauldron;
import vazkii.botania.common.item.ModItems;

public class CommonProxy {

	public static Configuration config;

	public void preInit(FMLPreInitializationEvent event) {	
		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "thaumic_periphery.cfg"));
		Config.readConfig();
	}

	public void init(FMLInitializationEvent event) {
		
		
		ResearchCategories.registerCategory("PERIPHERY", "UNLOCKAUROMANCY",
				new AspectList(),
				new ResourceLocation("thaumcraft", "textures/misc/vortex.png"),
				new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_2.jpg"),
				new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));
		
		ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicPeriphery.MODID, "research/periphery"));
		if (Config.emberCaster) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicPeriphery.MODID, "research/ember_caster"));
		}
		if (Config.manaCaster) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicPeriphery.MODID, "research/mana_caster"));
		}
		if (ThaumicPeriphery.mysticalMechanicsLoaded) MysticalMechanicsCompat.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		if (config.hasChanged()) {
			config.save();
		}

		if (Config.copperBrassRecipe)
			replaceBrassRecipe();
		initResearch();
	}

	protected void initResearch() {
		ResearchCategory periphery = ResearchCategories.getResearchCategory("PERIPHERY");
		
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "pauldron_repulsion"),
				new InfusionRecipe("PAULDRONREPULSION", new ItemStack(ModContent.pauldron_repulsion), 1,
						new AspectList().add(Aspect.AIR, 50).add(Aspect.MOTION, 50).add(Aspect.PROTECT, 10),
						new ItemStack(ModContent.pauldron), new ItemStack(BlocksTC.pavingStoneBarrier),
						ConfigItems.AIR_CRYSTAL));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "malignant_heart"),
				new InfusionRecipe("MALIGNANTHEART", new ItemStack(ModContent.malignant_heart), 6,
						new AspectList().add(Aspect.AVERSION, 50).add(Aspect.DEATH, 50)
						.add(Aspect.UNDEAD, 25).add(Aspect.FLUX, 15).add(Aspect.ENTROPY, 10),
						new ItemStack(BlocksTC.fleshBlock), new ItemStack(ItemsTC.brain),
						new ItemStack(Items.NETHER_WART), new ItemStack(ItemsTC.tallow),
						new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ItemsTC.tallow),
						new ItemStack(Items.GHAST_TEAR)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "magic_quiver"),
				new InfusionRecipe("MAGICQUIVER", new ItemStack(ModContent.magic_quiver), 4,
						new AspectList().add(Aspect.VOID, 100).add(Aspect.ORDER, 25)
						.add(Aspect.DESIRE, 25).add(Aspect.MAGIC, 15).add(Aspect.AURA, 15),
						new ItemStack(ItemsTC.baubles, 1, 2), new ItemStack(ItemsTC.visResonator),
						new ItemStack(Items.LEATHER), new ItemStack(ItemsTC.fabric),
						new ItemStack(Items.ARROW), new ItemStack(ItemsTC.fabric),
						new ItemStack(Items.LEATHER)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "vis_phylactery"),
				new InfusionRecipe("VISPHYLACTERY", new ItemStack(ModContent.vis_phylactery), 6,
						new AspectList().add(Aspect.TRAP, 50).add(Aspect.VOID, 25).add(Aspect.DESIRE, 25),
						new ItemStack(ItemsTC.amuletVis, 1, 1), new ItemStack(ItemsTC.quicksilver),
						new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.nuggets, 1, 10),
						new ItemStack(ItemsTC.amber)));
		
		if (Config.emberCaster) {
			ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "caster_ember"),
					new InfusionRecipe("CASTEREMBER", new ItemStack(ModContent.caster_ember), 6,
							new AspectList().add(Aspect.FIRE, 50).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 25)
									.add(Aspect.MECHANISM, 25).add(Aspect.ENERGY, 50),
							new ItemStack(RegistryManager.wildfire_core), new ItemStack(RegistryManager.shard_ember),
							"ingotDawnstone", "plateIron", new ItemStack(ItemsTC.mechanismComplex), "ingotCopper",
							new ItemStack(ItemsTC.morphicResonator), "plateIron", "ingotDawnstone"));
		}

		if (Config.manaCaster) {
			ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicPeriphery.MODID, "caster_elementium"),
					new InfusionRecipe("CASTERELEMENTIUM", new ItemStack(ModContent.caster_elementium), 6,
							new AspectList().add(Aspect.PLANT, 50).add(Aspect.MAGIC, 15).add(Aspect.EXCHANGE, 25)
									.add(Aspect.ALCHEMY, 25).add(Aspect.ENERGY, 50),
							new ItemStack(ModItems.rune, 1, 8), new ItemStack(ItemsTC.morphicResonator),
							new ItemStack(ModItems.manaResource, 1, 7), "leather",
							new ItemStack(ModItems.manaResource, 1, 23), new ItemStack(ModItems.manaResource, 1, 22),
							new ItemStack(ItemsTC.salisMundus), "leather", new ItemStack(ModItems.manaResource, 1, 7)));
		}
	}

	protected void replaceBrassRecipe() {
		IThaumcraftRecipe r = ThaumcraftApi.getCraftingRecipes().get(new ResourceLocation("thaumcraft", "brassingot"));
		if (r != null && r instanceof CrucibleRecipe && OreDictionary.doesOreNameExist("ingotCopper") && (OreDictionary.getOres("ingotCopper", false).size() > 0)) {
			CrucibleRecipe recipe = (CrucibleRecipe) r;
			recipe.setCatalyst(new OreIngredient("ingotCopper"));
		}
	}

}
