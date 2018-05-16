package thaumicperiphery;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumicperiphery.proxy.CommonProxy;

public class Config {
	
	public static final String CATEGORY_TWEAKS = "tweaks";
	public static final String CATEGORY_COMPAT = "compat";
	
	private final static List<String> PROPERTY_ORDER_TWEAKS = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_COMPAT = new ArrayList<String>();
	
	public static boolean tcBaubleRenderer;
	public static boolean copperBrassRecipe;
	
	public static boolean emberCaster;
	public static boolean manaCaster;
	
	public static void readConfig() {
		Configuration cfg = CommonProxy.config;
		try {
			cfg.load();
			initGeneralConfig(cfg);
		} catch (Exception e1) {

		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}
	
	private static void initGeneralConfig(Configuration cfg) {
		cfg.addCustomCategoryComment(CATEGORY_TWEAKS, "Tweaks to existing Thaumcraft features");
		cfg.addCustomCategoryComment(CATEGORY_COMPAT, "Mod compatability options");
		
		// tweaks
		tcBaubleRenderer = cfg.getBoolean("Thaumcraft Bauble Renderer", CATEGORY_TWEAKS, true, "Adds renderers to Thaumcraft's belts and amulets");
		copperBrassRecipe = cfg.getBoolean("Copper Brass Recipe", CATEGORY_TWEAKS, true, "Replaces iron with copper in the crucible recipe for alchemical brass");
		// compat
		emberCaster = cfg.getBoolean("Ember Caster", CATEGORY_COMPAT, true, "Enables the caster's gauntlet that uses ember instead of vis") && Loader.isModLoaded("embers");
		manaCaster = cfg.getBoolean("Mana Caster", CATEGORY_COMPAT, true, "Enables the caster's gauntlet that uses mana (from Botania) instead of vis") && Loader.isModLoaded("botania");
		
		// ordering
		PROPERTY_ORDER_TWEAKS.add("Thaumcraft Bauble Renderer");
		PROPERTY_ORDER_TWEAKS.add("Copper Brass Recipe");
		
		PROPERTY_ORDER_COMPAT.add("Ember Caster");
		PROPERTY_ORDER_COMPAT.add("Mana Caster");
		
		cfg.setCategoryPropertyOrder(CATEGORY_TWEAKS, PROPERTY_ORDER_TWEAKS);
		cfg.setCategoryPropertyOrder(CATEGORY_COMPAT, PROPERTY_ORDER_COMPAT);
	}

}
