package cyano.basemetals.blocks;

import cyano.basemetals.material.MetalMaterial;
import cyano.basemetals.registry.IOreDictionaryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockMetalBars extends net.minecraft.block.BlockPane implements IOreDictionaryEntry {

	final MetalMaterial metal;
	public BlockMetalBars(MetalMaterial metal) {
		super(Material.iron, true);
		this.stepSound = Block.soundTypeMetal;
		this.metal = metal;
		this.blockHardness = metal.getMetalBlockHardness();
		this.blockResistance = metal.getBlastResistance();
		this.setHarvestLevel("pickaxe", metal.getRequiredHarvestLevel());
	}
	@Override
	public String getOreDictionaryName() {
		return "bars"+metal.getCapitalizedName();
	}
}
