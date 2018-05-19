package thaumicperiphery.crafting;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.items.ItemsTC;
import thaumicperiphery.ModContent;
import vazkii.botania.common.item.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PhantomInkRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	public static final String TAG_PHANTOM_INK = "phantomInk";
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean ink = false;
		boolean inkable = false;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			
			if (!ink && stack.getItem() == ModItems.phantomInk) {
				ink = true;
			} else if (!inkable && isInkable(stack)) {
				inkable = true;
			} else {
				return false;
			}
		}
		
		return ink && inkable;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack result = ItemStack.EMPTY;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			
			if (isInkable(stack)) {
				result = stack.copy();
				break;
			}
		}
		
		if (!result.isEmpty()) {
			NBTTagCompound tag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
			if (tag.hasKey(TAG_PHANTOM_INK)) {
				tag.removeTag(TAG_PHANTOM_INK);
				if (tag.hasNoTags()) tag = null;
			} else {
				tag.setBoolean(TAG_PHANTOM_INK, true);
			}
			result.setTagCompound(tag);
		}
		
		return result;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	protected boolean isInkable(ItemStack stack) {
		Item item = stack.getItem();
		int meta = stack.getMetadata();
		
		boolean inkable = false;
		
		if (item == ItemsTC.amuletVis) inkable = true;
		
		if (item == ItemsTC.focusPouch) inkable = true;
		
		if (item == ModContent.pauldron || item == ModContent.pauldron_repulsion || item == ModContent.magic_quiver || item == ModContent.vis_phylactery) inkable = true;
		
		if (item == ItemsTC.baubles) {
			if (meta == 0 || meta == 2 || meta == 4 || meta == 6) inkable = true;
		}
		
		return inkable;
	}
	
	public static boolean hasPhantomInk(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(PhantomInkRecipe.TAG_PHANTOM_INK);
	}

}
