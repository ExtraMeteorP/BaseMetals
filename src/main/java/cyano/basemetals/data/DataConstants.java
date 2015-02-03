package cyano.basemetals.data;

public abstract class DataConstants {

	public static final String defaultOreSpawnJSON = "{"
+ "\n	\"dimensions\":["
+ "\n		{"
+ "\n			\"dimension\":0,"
+ "\n			\"ores\":["
+ "\n				{"
+ "\n					\"__comment\":\"These settings are roughly equivalent to the "
+ "parameters of the CUSTOM world json: blockID = the unlocalized name of a "
+ "block in the format 'ModID:blockName',blockMeta = the meta data value for "
+ "the block (optional),size = number of blocks to spawn per ore deposit, "
+ "variation = vary the size of deposits by +/- this amount, frequency = number "
+ "of deposits per chunk (can be less than 1, e.g. 0.5 for 1 deposit every 2 "
+ "chunks), minHeight = minimum spawn height, maxHeight = maximum spawn height, "
+ "biomes = array of biomes to spawn this ore in (if empty or absent, the ore "
+ "will spawn in all biomes). Note that the size values in the Minecraft CUSTOM "
+ "world json do not represent the actual number of blocks spawned, where-as the "
+ "size value here does represent actual number of blocks. \","
+ "\n					\"blockID\":\"basemetals:copper_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":10,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":96"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:silver_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":4,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":32"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:tin_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":10,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":128"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:lead_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":5,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":64"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:zinc_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":10,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":96"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:mercury_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":3,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":32"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:nickel_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":10,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":96"
+ "\n				}"
+ "\n			]"
+ "\n		},"
+ "\n		{"
+ "\n			\"dimension\":-1,"
+ "\n			\"__comment\":\"the nether\","
+ "\n			\"ores\":["
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:coldiron_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":5,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":128"
+ "\n				},"
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:adamantine_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":2,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":128"
+ "\n				}"
+ "\n			]"
+ "\n		},"
+ "\n		{"
+ "\n			\"dimension\":1,"
+ "\n			\"__comment\":\"the end\","
+ "\n			\"ores\":["
+ "\n				{"
+ "\n					\"blockID\":\"basemetals:starsteel_ore\","
+ "\n					\"size\":8,"
+ "\n					\"variation\":4,"
+ "\n					\"frequency\":5,"
+ "\n					\"minHeight\":0,"
+ "\n					\"maxHeight\":255"
+ "\n				}"
+ "\n			]"
+ "\n		}"
+ "\n	]"
+ "\n}";
}
