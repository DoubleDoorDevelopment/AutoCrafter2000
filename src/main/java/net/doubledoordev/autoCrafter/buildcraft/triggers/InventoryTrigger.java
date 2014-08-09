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

package net.doubledoordev.autoCrafter.buildcraft.triggers;

import buildcraft.api.gates.ITileTrigger;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.doubledoordev.autoCrafter.tile.AutoCrafterTile;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import static net.doubledoordev.autoCrafter.util.Constants.MODID;

/**
 * Largely same as the default BC ones, except we differentiate between input and output
 *
 * @author Dries007
 */
public class InventoryTrigger implements ITileTrigger
{
    @Override
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
    {
        return tile instanceof AutoCrafterTile && isTriggerActive((AutoCrafterTile) tile);
    }

    public enum State
    {
        Empty, Full, Has_Items, Has_Space
    }

    public enum InventoryType
    {
        In, Out;

        public IInventory getRightInventory(AutoCrafterTile tile)
        {
            return this == In ? tile.inventoryIn : tile.inventoryOut;
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon         icon;
    private State         state;
    private InventoryType inventoryType;

    public InventoryTrigger(State state, InventoryType inventoryType)
    {
        this.state = state;
        this.inventoryType = inventoryType;
    }

    public boolean isTriggerActive(AutoCrafterTile tile)
    {
        IInventory inventory = inventoryType.getRightInventory(tile);
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            if (tile.inventoryMatrix.getStackInSlot(i) == null) continue;

            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && state == State.Has_Items) return true;
            if (itemStack == null && state == State.Full) return false;
            if (itemStack == null && state == State.Has_Space) return true;
            if (itemStack != null && state == State.Empty) return false;
            if (itemStack != null && itemStack.stackSize != itemStack.getMaxStackSize() && state == State.Full) return false;
            if (itemStack != null && itemStack.stackSize < itemStack.getMaxStackSize() && state == State.Has_Space) return true;
        }
        switch (state)
        {
            case Has_Items:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getUniqueTag()
    {
        return MODID + ":" + inventoryType.name().toLowerCase() + "_" + state.name().toLowerCase();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon()
    {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(MODID.toLowerCase() + ":triggers/" + inventoryType.name().toLowerCase() + "_" + state.name().toLowerCase());
    }

    @Override
    public boolean hasParameter()
    {
        return false;
    }

    @Override
    public boolean requiresParameter()
    {
        return false;
    }

    @Override
    public String getDescription()
    {
        return inventoryType.name() + "put buffer " + state.name().toLowerCase().replace('_', ' ');
    }

    @Override
    public ITriggerParameter createParameter()
    {
        return null;
    }

    @Override
    public ITrigger rotateLeft()
    {
        return null;
    }
}
