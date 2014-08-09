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

package net.doubledoordev.autoCrafter.guis;

import net.doubledoordev.autoCrafter.guis.parts.FakeSlotCrafting;
import net.doubledoordev.autoCrafter.tile.AutoCrafterTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * The container for the autocrafter.
 *
 * @author Dries007
 */
public class AutoCrafterContainer extends Container
{
    public final AutoCrafterTile tile;
    private      ItemStack       prevOutput;

    public AutoCrafterContainer(EntityPlayer player, AutoCrafterTile te)
    {
        tile = te;
        this.addSlotToContainer(new FakeSlotCrafting(player, tile.inventoryMatrix, tile.inventoryCraftResult, AutoCrafterTile.SLOT_OUT, 124, 35));                      // Recipe output
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryMatrix, x + y * 3, 30 + x * 18, 17 + y * 18));  // The recipe matrix
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryIn, x + y * 3, 26 + x * 18, 84 + y * 18));  // Input
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryOut, x + y * 3, 98 + x * 18, 84 + y * 18));  // Output
        for (int y = 0; y < 3; y++) for (int x = 0; x < 9; x++) this.addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 149 + y * 18)); // Player inventory
        for (int x = 0; x < 9; x++) this.addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 207));          // Player hotbar
        this.onCraftMatrixChanged(tile);
        tile.players.add(player);
    }

    /**
     * Overridden to allow fake slots in the crafting matrix
     */
    @Override
    public ItemStack slotClick(int i, int mousebtn, int modifier, EntityPlayer player)
    {
        ItemStack stack = null;
        if (i >= 0 && i <= AutoCrafterTile.MATRIX) // Fake slots
        {
            if (mousebtn == 2)
            {
                getSlot(i).putStack(null);
            }
            else if (mousebtn == 0 || mousebtn == 1)
            {
                InventoryPlayer playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (stackSlot != null) stack = stackSlot.copy();

                if (stackHeld != null)
                {
                    stackHeld = stackHeld.copy();
                    stackHeld.stackSize = 1;
                    getSlot(i).putStack(stackHeld);
                }
                else getSlot(i).putStack(null);
            }
        }
        else
        {
            stack = super.slotClick(i, mousebtn, modifier, player);
        }
        onCraftMatrixChanged(tile);
        return stack;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        ItemStack output = tile.inventoryCraftResult.getStackInSlot(AutoCrafterTile.SLOT_OUT);
        if (output != prevOutput)
        {
            prevOutput = output;
            onCraftMatrixChanged(tile.inventoryMatrix);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        tile.players.remove(par1EntityPlayer);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);
        tile.updateRecipe();
        if (tile.recipe == null) tile.setInventorySlotContents(AutoCrafterTile.SLOT_OUT, null);
        else tile.setInventorySlotContents(AutoCrafterTile.SLOT_OUT, tile.recipe.getRecipeOutput().copy());
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return tile.canInteractWith(par1EntityPlayer);
    }

    /**
     * Shift click crap. I don't ever want to change this.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex < 9) return null;
            else if (slotIndex < 19)
            {
                if (!this.mergeItemStack(itemstack1, 18, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (slotIndex < 28)
            {
                if (!this.mergeItemStack(itemstack1, 27, inventorySlots.size(), true))
                {
                    return null;
                }
            }
            for (int i = 0; i < 9; i++)
            {
                if (AutoCrafterTile.canStacksMergeWithOreDict(tile.inventoryMatrix.getStackInSlot(i), itemstack1, false))
                {
                    if (this.mergeItemStack(itemstack1, 10 + i, 11 + i, false))
                    {
                        break;
                    }
                }
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    /**
     * I have no idea...
     */
    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.tile && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
