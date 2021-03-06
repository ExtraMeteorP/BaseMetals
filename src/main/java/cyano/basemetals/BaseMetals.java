package cyano.basemetals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import cyano.basemetals.data.DataConstants;
import cyano.basemetals.events.VanillaOreGenDisabler;
import cyano.basemetals.registry.CrusherRecipeRegistry;


/**
 * This is the entry point for this mod. If you are writing your own mod that 
 * uses this mod, the classes of interest to you are the init classes (classes 
 * in package cyano.basemetals.init) and the CrusherRecipeRegistry class (in 
 * package cyano.basemetals.registry). Note that you should add 
 * 'dependencies = "required-after:basemetals"' to your &#64;Mod annotation 
 * (e.g. <br> 
 * &#64;Mod(modid = "moremetals", name="More Metals!", version = "1.2.3", dependencies = "required-after:basemetals")
 * <br>)
 * @author DrCyano
 *
 */
@Mod(modid = BaseMetals.MODID, name=BaseMetals.NAME, version = BaseMetals.VERSION)
public class BaseMetals
{

	/** ID of this mod */
	public static final String MODID = "basemetals";
	/** display name of this mod */
	public static final String NAME ="Base Metals";
	/** Version number, in Major.Minor.Build format. The minor number is increased whenever a change 
	 * is made that has the potential to break compatibility with other mods that depend on this one. */
	public static final String VERSION = "1.3.4";
	
	/** All ore-spawn files discovered in the ore-spawn folder */
	public static final List<Path> oreSpawnConfigFiles = new LinkedList<>();
	
	/** multiplier to increase or reduce the frequency of metal ingots appearing in treasure chests */
	public static float chestLootFactor = 0.5f;
	
	/** If true, hammers cannot crush ores that they cannot mine */
	public static boolean enforceHardness = true;
	/** If true, then crack hammers can mine on all the same blocks that their pick-axe equivalent 
	 * can mine. If false, then the hammer is 1 step weaker than the pick-axe */
	public static boolean strongHammers = true;
	/** Whether or not vanilla ore-gen has been disabled */
	public static boolean disableVanillaOreGen = false;
	/** For when the user adds specific recipies via the config file */
	public static List<String> userCrusherRecipes = new ArrayList<>();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// load config
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		chestLootFactor = config.getFloat("treasure_chest_loot_factor", "options", 0.5f, 0.0f, 1000.0f, 
				"Controls the rarity of metal ingots being found in treasure chests relative to \n"
			 +  "the frequency of other chest loot items. Set to 0 to disable metal ingots from \n"
			 +  "appearing in treasure chests.");
		
		enforceHardness = config.getBoolean("enforce_hardness", "options", enforceHardness, 
				"If true, then the crack hammer cannot crush ingots into powders if that \n"
			+	"crackhammer is not hard enough to crush the ingot's ore.");
		
		strongHammers = config.getBoolean("strong_hammers", "options", strongHammers, 
				"If true, then the crack hammer can crush ingots/ores that a pickaxe of the same \n"
			+	"material can harvest. If false, then your crack hammer must be made of a harder \n"
			+	"material than the ore you are crushing.");
		
		disableVanillaOreGen = config.getBoolean("disable_standard_ore_generation", "options", disableVanillaOreGen, 
				"If true, then ore generation will be handled exclusively by oregen .json files \n"
			+	"(vanilla ore generation will be disabled)");
		
		ConfigCategory userRecipeCat = config.getCategory("hammer recipes");
		userRecipeCat.setComment(
			  "This section allows you to add your own recipes for the Crack Hammer (and other rock \n"
			+ "crushers). Recipes are specified in semicolon (;) delimited lists of formulas in the \n"
			+ "format modid:name#y->x*modid:name#y, where x is the number of items in a stack and y \n"
			+ "is the metadata value. Note that both x and y are optional, so you can use the \n"
			+ "formula modid:name->modid:name for most items/blocks. \n\n"
			+ "All properties in this section will be parsed for formulas, regardless their name. \n"
			+ "This lets you organize your recipe lists for easier reading.");
		if(userRecipeCat.keySet().size()==0){
			Property prop = new Property("custom","",Property.Type.STRING);
			prop.comment = "Example: minecraft:stained_glass#11->minecraft:dye#4; minecraft:wool->4*minecraft:string"; 
			userRecipeCat.put("custom", prop);
		}
		for(Property p : userRecipeCat.values()){
			String[] recipes = p.getString().split(";");
			for(String r : recipes){
				String recipe = r.trim();
				if(recipe.isEmpty()) continue;
				if(recipe.contains("->") == false){
					throw new IllegalArgumentException ("Malformed hammer recipe expression '"+recipe+"'. Should be in format 'modid:itemname->modid:itemname'");
				}
				userCrusherRecipes.add(recipe);
			}
		}
		
		Path oreSpawnFolder = Paths.get(event.getSuggestedConfigurationFile().toPath().getParent().toString(),"orespawn");
		Path oreSpawnFile = Paths.get(oreSpawnFolder.toString(),MODID+".json");
		if(Files.exists(oreSpawnFile) == false){
			try {
				Files.createDirectories(oreSpawnFile.getParent());
				Files.write(oreSpawnFile, Arrays.asList(DataConstants.defaultOreSpawnJSON.split("\n")), Charset.forName("UTF-8"));
			} catch (IOException e) {
				FMLLog.severe(MODID+": Error: Failed to write file "+oreSpawnFile);
			}
		}

		File[] files = oreSpawnFolder.toFile().listFiles(); // sigh, java 8 does this so much better
		for(File f : files){
			if(f.getName().toLowerCase().endsWith(".json")){
				oreSpawnConfigFiles.add(f.toPath());
			}
		}
		cyano.basemetals.init.Materials.init();
		cyano.basemetals.init.Blocks.init();
		cyano.basemetals.init.Items.init();
		
		

		config.save();

		if(event.getSide() == Side.CLIENT){
			clientPreInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverPreInit(event);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void clientPreInit(FMLPreInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPreInit(FMLPreInitializationEvent event){
		// client-only code
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
		cyano.basemetals.init.Recipes.init();
		cyano.basemetals.init.DungeonLoot.init();
		cyano.basemetals.init.VillagerTrades.init();
		
		cyano.basemetals.init.Achievements.init();
		
		if(disableVanillaOreGen){
			MinecraftForge.ORE_GEN_BUS.register(VanillaOreGenDisabler.getInstance());
		}

		if(event.getSide() == Side.CLIENT){
			clientInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverInit(event);
		}
	}
	

	@SideOnly(Side.CLIENT)
	private void clientInit(FMLInitializationEvent event){
		// client-only code
		cyano.basemetals.init.Items.registerItemRenders(event);
		cyano.basemetals.init.Blocks.registerItemRenders(event);
	}
	@SideOnly(Side.SERVER)
	private void serverInit(FMLInitializationEvent event){
		// client-only code
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		// parse orespawn data
		for(Path oreSpawnFile : oreSpawnConfigFiles){
			try {
				cyano.basemetals.init.WorldGen.loadConfig(oreSpawnFile);

			} catch (IOException e) {
				FMLLog.log(Level.ERROR, e,MODID+": Error parsing ore-spawn config file "+oreSpawnFile);
			}
		}
		
		cyano.basemetals.init.WorldGen.init();

		
		// parse user crusher recipes
		for(String recipe : userCrusherRecipes){
			FMLLog.info(MODID+": adding custom crusher recipe '"+recipe+"'");
			int i = recipe.indexOf("->");
			String inputStr = recipe.substring(0,i);
			String outputStr = recipe.substring(i+2,recipe.length());
			ItemStack input = parseStringAsItemStack(inputStr,true);
			ItemStack output = parseStringAsItemStack(outputStr,false);
			if(input == null || output == null){
				FMLLog.severe("Failed to add recipe formula '"+recipe+"' because the blocks/items could not be found");
			} else {
				CrusherRecipeRegistry.addNewCrusherRecipe(input, output);
			}
		}
		
		if(event.getSide() == Side.CLIENT){
			clientPostInit(event);
		}
		if(event.getSide() == Side.SERVER){
			serverPostInit(event);
		}
		
		CrusherRecipeRegistry.getInstance().clearCache();
	}
	

	@SideOnly(Side.CLIENT)
	private void clientPostInit(FMLPostInitializationEvent event){
		// client-only code
	}
	@SideOnly(Side.SERVER)
	private void serverPostInit(FMLPostInitializationEvent event){
		// client-only code
	}
	

	/**
	 * Parses a String in the format (stack-size)*(modid):(item/block name)#(metadata value). The 
	 * stacksize and metadata value parameters are optional.
	 * @param str A String describing an itemstack (e.g. "4*minecraft:dye#15" or "minecraft:bow")
	 * @param allowWildcard If true, then item strings that do not specify a metadata value will use 
	 * the OreDictionary wildcard value. If false, then the default meta value is 0 instead.
	 * @return An ItemStack representing the item, or null if the item is not found
	 */
	public static ItemStack parseStringAsItemStack(String str, boolean allowWildcard){
		str = str.trim();
		int count = 1;
		int meta;
		if(allowWildcard){
			meta = OreDictionary.WILDCARD_VALUE;
		} else {
			meta = 0;
		}
		int nameStart = 0;
		int nameEnd = str.length();
		if(str.contains("*")){
			count = Integer.parseInt(str.substring(0,str.indexOf("*")).trim());
			nameStart = str.indexOf("*")+1;
		}
		if(str.contains("#")){
			meta = Integer.parseInt(str.substring(str.indexOf("#")+1,str.length()).trim());
			nameEnd = str.indexOf("#");
		}
		String id = str.substring(nameStart,nameEnd).trim();
		String mod = id.substring(0,id.indexOf(":")).trim();
		String name = id.substring(id.indexOf(":")+1,id.length()).trim();
		if(GameRegistry.findBlock(mod, name) != null){
			// is a block
			return new ItemStack(GameRegistry.findBlock(mod, name),count,meta);
		} else if(GameRegistry.findItem(mod, name) != null){
			// is an item
			return new ItemStack(GameRegistry.findItem(mod, name),count,meta);
		} else {
			// item not found
			FMLLog.severe("Failed to find item or block for ID '"+id+"'");
			return null;
		}
	}
}
