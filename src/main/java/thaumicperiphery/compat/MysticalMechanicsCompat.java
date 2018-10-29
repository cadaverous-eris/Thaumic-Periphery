package thaumicperiphery.compat;

import javax.annotation.Nullable;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import thaumcraft.Thaumcraft;
import thaumicperiphery.ModContent;
import thaumicperiphery.ThaumicPeriphery;

public class MysticalMechanicsCompat {
	
	public static final ResourceLocation BRASS_GEAR_BEHAVIOR = new ResourceLocation(ThaumicPeriphery.MODID, "gear_brass");
	
	public static void init() {
		OreDictionary.registerOre("gearBrass", ModContent.gear_brass);
		
		MysticalMechanicsAPI.IMPL.registerGear(BRASS_GEAR_BEHAVIOR, new OreIngredient("gearBrass"), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
                return power;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
                //NOOP
            }
        });
	}
	
	public static void initRecipes(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(Thaumcraft.MODID, "brass_stuff"), new ItemStack(ModContent.gear_brass, 1), true, new Object[] {
                " I ",
                "INI",
                " I ",
                'N', "nuggetBrass",
                'I', "ingotBrass"
		}).setRegistryName(new ResourceLocation(ThaumicPeriphery.MODID, "gear_brass")));
		
		if (ThaumicPeriphery.embersLoaded) {
			Ingredient stampGear = Ingredient.fromItem(RegistryManager.stamp_gear);
			int gearAmount = ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT;
			
			if (FluidRegistry.isFluidRegistered("brass")) RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("brass", gearAmount), stampGear, new ItemStack(ModContent.gear_brass, 1)));
		}
	}

}
