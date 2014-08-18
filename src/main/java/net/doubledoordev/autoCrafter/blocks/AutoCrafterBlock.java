/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.autoCrafter.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.doubledoordev.autoCrafter.AutoCrafter2000;
import net.doubledoordev.autoCrafter.tile.AutoCrafterTile;
import net.doubledoordev.autoCrafter.util.Constants;
import net.doubledoordev.d3core.D3Core;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * The block class, contains instance and registers name, recipes and tile entity.
 *
 * @author Dries007
 */
public class AutoCrafterBlock extends BlockContainer
{
    public static AutoCrafterBlock instance;
    private final IIcon[]  icons      = new IIcon[6];
    private final String[] icon_names = {"_bottom", "_top", "_side", "_side1", "_side2", "_side3"};

    public AutoCrafterBlock()
    {
        super(Material.iron);
        setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName(Constants.MODID + ":autocrafter").setCreativeTab(CreativeTabs.tabMisc);

        GameRegistry.registerBlock(this, "AutoCrafter");
        GameRegistry.registerTileEntity(AutoCrafterTile.class, "AutoCrafterTile");
        CraftingManager.getInstance().addRecipe(new ItemStack(this), " c ", "iwi", " t ", 'c', Blocks.chest, 'i', Items.iron_ingot, 'w', Blocks.crafting_table, 't', Blocks.redstone_torch);

        instance = this;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof AutoCrafterTile) ((AutoCrafterTile) te).dropAll();

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
    {
        super.onBlockActivated(world, x, y, z, entityplayer, side, hitX, hitY, hitZ);
        if (world.isRemote) return true;

        if (D3Core.debug() && entityplayer.getHeldItem() != null && entityplayer.getHeldItem().getItem() == Items.stick) ((AutoCrafterTile) world.getTileEntity(x, y, z)).debugTicks = 100;
        else entityplayer.openGui(AutoCrafter2000.instance, Constants.GuiID_AutoCrafter, world, x, y, z);

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new AutoCrafterTile(world);
    }

    @Override
    public IIcon getIcon(int i, int j)
    {
        return icons[i];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < icons.length; i++) icons[i] = iconRegister.registerIcon(Constants.MODID.toLowerCase() + ":autoCrafter" + icon_names[i]);
    }

    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }
}
