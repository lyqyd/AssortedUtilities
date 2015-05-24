package assortedutilities.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PortalBlock extends Block {
	
	public PortalBlock() {
		super(Material.portal);
		setHardness(-1.0F);
		GameRegistry.registerBlock(this, "portal");
		setBlockName("assortedutilities.portal");
		this.setBlockTextureName("assortedutilities:portal");
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		
		if (meta == 0) {
			if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this) {
				//yz
				meta = 1;
			} else if (world.getBlock(x, y - 1, z) != this && world.getBlock(x, y + 1, z) != this) {
				//xz
				meta = 2;
			} else if (world.getBlock(x, y, z - 1) != this && world.getBlock(x, y, z + 1) != this) {
				//xy
				meta = 3;
			}
			
			if (world instanceof World && !((World)world).isRemote) {
            	//fix metadata on block.
                ((World)world).setBlockMetadataWithNotify(x, y, z, meta, 2);
            }
		}
		
		float xAdj = 0.125f;
		float yAdj = 0.125f;
		float zAdj = 0.125f;
		
		switch(meta) {
			case 1:
				yAdj = 0.5f;
				zAdj = 0.5f;
				break;
			case 2:
				xAdj = 0.5f;
				zAdj = 0.5f;
				break;
			case 3:
				xAdj = 0.5f;
				yAdj = 0.5f;
				break;
		}
		
		this.setBlockBounds(0.5f - xAdj, 0.5f - yAdj, 0.5f - zAdj, 0.5f + xAdj, 0.5f + yAdj, 0.5f + zAdj);
	}
	
	public void OldsetBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
		//mask off to get only 1 or 2 as state.
        int l = world.getBlockMetadata(x, y, z) & 3;

        if (l == 0)
        {
            if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this)
            {
            	//portal is z-axis oriented.
                l = 2;
            }
            else
            {
            	//portal is x-axis oriented.
                l = 1;
            }

            if (world instanceof World && !((World)world).isRemote)
            {
            	//fix metadata on block.
                ((World)world).setBlockMetadataWithNotify(x, y, z, l, 2);
            }
        }

        float f = 0.125F;
        float f1 = 0.125F;

        if (l == 1)
        {
            f = 0.5F;
        }

        if (l == 2)
        {
            f1 = 0.5F;
        }

        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
    }

}
