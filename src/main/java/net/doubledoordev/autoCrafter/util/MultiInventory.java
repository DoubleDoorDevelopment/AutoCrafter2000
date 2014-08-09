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

package net.doubledoordev.autoCrafter.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Thanks for this Buildcraft.
 * Makes managing multiple inventories in block easy.
 *
 * @author Dries007
 */
public class MultiInventory implements IInventory
{
    private final ArrayList<Integer>    slotList = new ArrayList<Integer>();
    private final ArrayList<IInventory> invList  = new ArrayList<IInventory>();

    public MultiInventory(IInventory... inventories)
    {
        for (IInventory iInventory : inventories) add(iInventory);
    }

    private void add(IInventory iInventory)
    {
        for (int slot = 0; slot < iInventory.getSizeInventory(); slot++)
        {
            slotList.add(slot);
            invList.add(iInventory);
        }
    }

    @Override
    public int getSizeInventory()
    {
        return slotList.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return invList.get(slot).getStackInSlot(slotList.get(slot));
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        return invList.get(slot).decrStackSize(slotList.get(slot), amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return invList.get(slot).getStackInSlotOnClosing(slotList.get(slot));
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        invList.get(slot).setInventorySlotContents(slotList.get(slot), stack);
    }

    @Override
    public String getInventoryName()
    {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return invList.get(slot).isItemValidForSlot(slotList.get(slot), stack);
    }
}
