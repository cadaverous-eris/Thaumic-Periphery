package thaumicperiphery;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;
import thaumicperiphery.proxy.CommonProxy;

@Mod(modid = ThaumicPeriphery.MODID, name = ThaumicPeriphery.NAME, version = ThaumicPeriphery.VERSION, dependencies = ThaumicPeriphery.DEPENDENCIES)
public class ThaumicPeriphery {
	
	public static final String MODID = "thaumicperiphery";
	public static final String NAME = "Thaumic Periphery";
	public static final String VERSION = "0.3.0";
	public static final String DEPENDENCIES = "required-after:thaumcraft;after:botania;after:embers;after:mysticalmechanics";
	
	public static boolean botaniaLoaded = Loader.isModLoaded("botania");
	public static boolean embersLoaded = Loader.isModLoaded("embers");
	public static boolean mysticalMechanicsLoaded = Loader.isModLoaded("mysticalmechanics");
	
	@SidedProxy(clientSide = "thaumicperiphery.proxy.ClientProxy", serverSide = "thaumicperiphery.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static ThaumicPeriphery instance;
	
	public static CreativeTabs thaumicPeripheryTab = new CreativeTabs("thaumicperiphery") {
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			ItemStack stack;
			if (Config.emberCaster) {
				stack = new ItemStack(ModContent.caster_ember);
			} else if (Config.manaCaster) {
				stack = new ItemStack(ModContent.caster_elementium);
			} else {
				stack = new ItemStack(ItemsTC.casterBasic);
			}
			return stack;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
}
