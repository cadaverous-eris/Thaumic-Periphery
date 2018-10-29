package thaumicperiphery.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import thaumicperiphery.ThaumicPeriphery;

public class ItemBase extends Item {
	
	public ItemBase(String name) {
		this.setRegistryName(new ResourceLocation(ThaumicPeriphery.MODID, name));
		this.setUnlocalizedName(name);
		
		this.setCreativeTab(ThaumicPeriphery.thaumicPeripheryTab);
	}

}
