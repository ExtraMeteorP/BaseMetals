package cyano.basemetals.items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.OreDictionary;
import cyano.basemetals.init.Materials;
import cyano.basemetals.material.MetalMaterial;

public class ItemMetalHoe extends ItemHoe {

	protected final MetalMaterial metal;
	protected final Set<String> toolTypes;
	protected final String repairOreDictName;
	protected final boolean regenerates;
	protected final long regenInterval = 200; 
	
	public ItemMetalHoe(MetalMaterial metal) {
		super(Materials.getToolMaterialFor(metal));
		this.metal = metal;
		this.setMaxDamage(metal.getToolDurability());
		this.toolTypes = new HashSet<>();
		toolTypes.add("hoe");
		repairOreDictName = "ingot"+metal.getCapitalizedName();
		if(metal.equals(Materials.starsteel)){
			regenerates = true;
		} else {
			regenerates = false;
		}
	}

	



	

    @Override
    public boolean getIsRepairable(final ItemStack intputItem, final ItemStack repairMaterial) {
    	List<ItemStack> acceptableItems = OreDictionary.getOres(repairOreDictName);
    	for(ItemStack i : acceptableItems ){
    		if(ItemStack.areItemsEqual(i, repairMaterial)) return true;
    	}
    	return false;
    }
    
    @Override
    public int getHarvestLevel(final ItemStack item, final String typeRequested) {
    	if (typeRequested != null && toolTypes.contains(typeRequested)) {
            return metal.getToolHarvestLevel();
        }
        return -1;
    }
    @Override
    public Set<String> getToolClasses(final ItemStack item) {
        return toolTypes;
    }
    
    @Override
    public float getStrVsBlock(final ItemStack tool, final Block target){
    	if(this.canHarvestBlock(target,tool)){
    		return Math.max(1.0f,metal.getToolEfficiency());
    	} else {
    		return 1.0f;
    	}
    }
    
    @Override
    public boolean hitEntity(final ItemStack item, final EntityLivingBase target, final EntityLivingBase attacker) {
        super.hitEntity(item, target, attacker);
        MetalToolEffects.extraEffectsOnAttack(metal,item, target, attacker);
        return true;
    }
    
    @Override
    public void onCreated(final ItemStack item, final World world, final EntityPlayer crafter) {
    	super.onCreated(item, world, crafter);
    	MetalToolEffects.extraEffectsOnCrafting(metal,item, world, crafter);
    }
    
    @Override
    public void onUpdate(final ItemStack item, final World world, final Entity player, final int inventoryIndex, final boolean isHeld) {
    	if(regenerates && !world.isRemote && isHeld && item.getItemDamage() > 0 && world.getTotalWorldTime() % regenInterval == 0){
    		item.setItemDamage(item.getItemDamage() - 1);
    	}
    }
    
    @Override
    public boolean canHarvestBlock(final Block target) {
		if(this.toolTypes.contains(target.getHarvestTool(target.getDefaultState()))){
			return metal.getToolHarvestLevel() >= target.getHarvestLevel(target.getDefaultState());
		}
		return false;
    }
    
    public String getMaterialName() {
        return metal.getName();
    }
	
	

}
