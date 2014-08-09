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

package net.doubledoordev.autoCrafter.tile;

import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import cpw.mods.fml.common.Optional;
import net.doubledoordev.autoCrafter.AutoCrafter2000;
import net.doubledoordev.autoCrafter.buildcraft.BuildcraftHelper;
import net.doubledoordev.autoCrafter.util.Constants;
import net.doubledoordev.autoCrafter.util.InventoryHelper;
import net.doubledoordev.autoCrafter.util.MultiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static net.doubledoordev.autoCrafter.util.Constants.BC_MODID;

/**
 * This is where the magic happens.
 * Thanks to Buildcraft for some handy code.
 *
 * @author Dries007
 */
@Optional.Interface(iface = "buildcraft.api.gates.IOverrideDefaultTriggers", modid = BC_MODID)
public class AutoCrafterTile extends TileEntity implements ISidedInventory, IOverrideDefaultTriggers
{
    // NBT data
    private static final String INV_RESULT = "result";
    private static final String INV_MATRIX = "matrix";
    private static final String INV_IN     = "in";
    private static final String INV_OUT    = "out";

    // Slots
    public static final  int   SLOT_OUT     = 0;
    public static final  int   MATRIX       = 3 * 3;
    private static final int[] SLOTS_MATRIX = InventoryHelper.slotArray(SLOT_OUT, MATRIX);
    private static final int   IN           = 3 * 3;
    private static final int   OUT          = 3 * 3;
    private static final int[] SLOTS_IN     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN);
    private static final int[] SLOTS_OUT    = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1 + IN, OUT);
    private static final int[] SLOTS_IO     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN + OUT);

    public static final int[] ARRAY_FOR_SHUFFEL = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    // Inventories this block is made out of, the multi one is used for the ISidedInventory
    public final  InventoryCraftResult inventoryCraftResult = new InventoryCraftResult();
    public final  InventoryCrafting    inventoryMatrix      = InventoryHelper.newCraftingMatrix(MATRIX, 1);
    public final  InventoryCrafting    inventoryIn          = InventoryHelper.newCraftingMatrix(MATRIX, 64);
    public final  InventoryBasic       inventoryOut         = new InventoryBasic("AutoCrafter_out", true, OUT);
    private final MultiInventory       multiInventory       = new MultiInventory(inventoryCraftResult, inventoryMatrix, inventoryIn, inventoryOut);

    // Other variables
    public IRecipe recipe;
    /**
     * 0 = default  => Stop crafting with redstone signal
     * 1            => Only craft with redstone signal
     * 2            => Ignore redstone signal
     */
    public  int redstoneMode = 0;
    private int tick         = 0;
    public  int debugTicks   = 0;
    private InternalPlayer internalPlayer;

    private SlotCrafting craftSlot;
    private final List<ItemStack>    overflow = new LinkedList<ItemStack>();
    public        int                crafts   = 0;
    public        List<EntityPlayer> players  = new LinkedList<EntityPlayer>();

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (worldObj.isRemote) return;
        debug(this);

        // Initialize code
        if (craftSlot == null)
        {
            internalPlayer = new InternalPlayer();
            craftSlot = new SlotCrafting(internalPlayer, inventoryIn, inventoryOut, xCoord, yCoord, zCoord);
        }

        boolean willCraft = true;
        // Lower tick rate
        tick++;
        if (AutoCrafter2000.getConfig().craftDelay != 0 && tick % AutoCrafter2000.getConfig().craftDelay != 0) willCraft = false;
        else tick = 0;
        debug("tickDelay", willCraft);

        // Redstone things
        boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (redstoneMode == 0 && powered) willCraft = false;
        if (redstoneMode == 1 && !powered) willCraft = false;
        debug("powered", powered);

        // Deal with overflow. If we couldn't empty, don't make new stuff.
        emptyOverflow();
        if (!overflow.isEmpty()) willCraft = false;
        debug("overflow.isEmpty", overflow.isEmpty());
        debug("recipe", recipe);

        ItemStack result = null;
        if (willCraft && recipe == null) willCraft = false;
        if (willCraft && !recipe.matches(inventoryIn, worldObj)) willCraft = false;
        debug("matches", willCraft);
        if (willCraft && (result = recipe.getCraftingResult(inventoryIn)) == null) willCraft = false;
        debug("result", result);
        if (willCraft && !InventoryHelper.hasSpaceFor(inventoryOut, result)) willCraft = false;
        debug("spacefor", willCraft);
        if (willCraft)
        {
            crafts++;
            result = result.copy(); // Won't be null cause then willCraft would have been false.
            if (AutoCrafter2000.getConfig().updateCraftCountLive) for (EntityPlayer player : players) ;
            // TODO: Packet crap    PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(CHANNEL_RMU, Joiner.on(";").join(this.xCoord, this.yCoord, this.zCoord, this.redstoneMode, this.crafts).getBytes()), (cpw.mods.fml.common.network.Player) player);

            // Craft!
            craftSlot.onPickupFromSlot(internalPlayer, result);

            // Overflow handling
            ItemStack stack = InventoryHelper.addToInventory(inventoryOut, result);
            if (stack != null) overflow.add(stack);
            debug("overflowFromCrafting", stack);
            for (int i = 0; i < internalPlayer.inventory.getSizeInventory(); i++)
            {
                stack = InventoryHelper.addToInventory(inventoryOut, internalPlayer.inventory.getStackInSlotOnClosing(i));
                if (stack != null) overflow.add(stack);
            }
            debug("overflow.size", overflow.size());
        }
        else reBalanceSlots(); // If we can't / won't craft, we rebalanced 1 slot.

        if (debugTicks > 0) debugTicks--;
        debug("debugTicks", debugTicks);
    }

    private void debug(Object o)
    {
        if (debugTicks > 0) AutoCrafter2000.getLogger().info(debugTicks + ": " + o.toString());
    }

    private void debug(String s, Object o)
    {
        if (debugTicks > 0) AutoCrafter2000.getLogger().info(debugTicks + ": " + s + ": " + (o == null ? "null" : o.toString()));
    }

    /**
     * Here to allow oreDict crafting
     */
    public static boolean canStacksMergeWithOreDict(ItemStack stack1, ItemStack stack2, boolean ifNull)
    {
        if (InventoryHelper.canStacksMerge(stack1, stack2, ifNull)) return true;

        int id1 = OreDictionary.getOreID(stack1);
        int id2 = OreDictionary.getOreID(stack2);
        return id1 != -1 && id1 == id2;
    }

    private void shuffleArray(int[] ar)
    {
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = worldObj.rand.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    /**
     * Done with 2 sets of loops to prioritize the filling of empty slots
     */
    private void reBalanceSlots()
    {
        shuffleArray(ARRAY_FOR_SHUFFEL);  // Randomize slot order
        debug("ARRAY_FOR_SHUFFEL", Arrays.toString(ARRAY_FOR_SHUFFEL));
        for (int i : ARRAY_FOR_SHUFFEL)
        {
            ItemStack craftStack = inventoryMatrix.getStackInSlot(i);
            if (craftStack == null) continue;

            ItemStack existingStack = inventoryIn.getStackInSlot(i);
            if (existingStack == null) continue; // If existing stack is null, move on. (Others can fill this stack later.)
            if (!canStacksMergeWithOreDict(craftStack, existingStack, false)) continue; // If existing stack doesn't match, move on.

            for (int j = 0; j < MATRIX; j++)
            {
                craftStack = inventoryMatrix.getStackInSlot(j);
                if (craftStack == null) continue;
                if (i == j) continue; // Don't try to merge with yourself...

                ItemStack otherStack = inventoryIn.getStackInSlot(j);
                if (otherStack == null)
                {
                    if (existingStack.stackSize == 1) continue; // Prevent derp
                    inventoryIn.setInventorySlotContents(i, existingStack);
                    inventoryIn.setInventorySlotContents(j, existingStack.splitStack(1));
                    debug("reBalanceSlot.FillEmptySlot");
                    return; // Do only 1 per tick
                }
            }
        }
        for (int i : ARRAY_FOR_SHUFFEL)
        {
            ItemStack craftStack = inventoryMatrix.getStackInSlot(i);
            if (craftStack == null) continue;

            ItemStack existingStack = inventoryIn.getStackInSlot(i);
            if (existingStack == null) continue; // If existing stack is null, move on. (Others can fill this stack later.)
            if (!canStacksMergeWithOreDict(craftStack, existingStack, false)) continue; // If existing stack doesn't match, move on.

            for (int j = 0; j < MATRIX; j++)
            {
                craftStack = inventoryMatrix.getStackInSlot(j);
                if (craftStack == null) continue;
                if (i == j) continue; // Don't try to merge with yourself...

                ItemStack otherStack = inventoryIn.getStackInSlot(j);
                if (!canStacksMergeWithOreDict(craftStack, otherStack, false)) continue; // If the stack we pick doesn't fit into the crafting slot, pick another one.

                if (InventoryHelper.canStacksMerge(existingStack, otherStack, false) && existingStack.stackSize > otherStack.stackSize + 1)
                {
                    existingStack.stackSize--;
                    otherStack.stackSize++;
                    debug("reBalanceSlot.ShiftOneItem");
                    return; // Do only 1 per tick
                }
            }
        }
    }

    private void emptyOverflow()
    {
        Iterator<ItemStack> iterator = overflow.iterator();

        while (iterator.hasNext())
        {
            ItemStack stack = iterator.next();
            if (InventoryHelper.hasSpaceFor(inventoryOut, stack))
            {
                InventoryHelper.addToInventory(inventoryOut, stack);
                iterator.remove();
            }
        }
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + "[" + worldObj.getWorldInfo().getWorldName() + "-" + worldObj.provider.getDimensionName() + (worldObj.isRemote ? "-remote" : "-local") + ";" + xCoord + ";" + yCoord + ";" + zCoord + "]";
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("redstoneMode", redstoneMode);
        data.setInteger("crafts", crafts);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, data);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        redstoneMode = pkt.func_148857_g().getInteger("redstoneMode");
        crafts = pkt.func_148857_g().getInteger("crafts");
    }

    public void updateRecipe()
    {
        recipe = InventoryHelper.findMatchingRecipe(inventoryMatrix, worldObj);
    }

    /**
     * If slot is an input slot, check to see if it matches the recipe.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        for (int i : SLOTS_IN) if (i == slot) return canStacksMergeWithOreDict(stack, multiInventory.getStackInSlot(i - IN), false);
        return multiInventory.isItemValidForSlot(slot, stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        return SLOTS_IO;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        for (int i : SLOTS_IN) if (i == slot) return canStacksMergeWithOreDict(stack, multiInventory.getStackInSlot(i - IN), false);
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        for (int i : SLOTS_OUT) if (i == slot) return true;
        return false;
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        //Todo: packet crap: PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(CHANNEL_RMU, Joiner.on(";").join(this.xCoord, this.yCoord, this.zCoord, this.redstoneMode, this.crafts).getBytes()), (cpw.mods.fml.common.network.Player) player);
        return true;
    }

    @Optional.Method(modid = BC_MODID)
    @Override
    public LinkedList<ITrigger> getTriggers()
    {
        return BuildcraftHelper.getAutocrafterTriggers();
    }

    public void dropAll()
    {
        InventoryHelper.dropItems(this.worldObj, inventoryIn, xCoord, yCoord, zCoord);
        InventoryHelper.dropItems(this.worldObj, inventoryOut, xCoord, yCoord, zCoord);
    }

    /**
     * I hate fake players myself but here is no better way.
     */
    private final class InternalPlayer extends EntityPlayer
    {
        public InternalPlayer()
        {
            super(AutoCrafterTile.this.worldObj, Constants.GAME_PROFILE);
            posX = AutoCrafterTile.this.xCoord;
            posY = AutoCrafterTile.this.yCoord + 1;
            posZ = AutoCrafterTile.this.zCoord;
        }

        @Override
        public void addChatMessage(IChatComponent p_145747_1_)
        {

        }

        @Override
        public boolean canCommandSenderUseCommand(int var1, String var2)
        {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates()
        {
            return null;
        }
    }


    /**
     * Start boring interface / TE code
     */
    public AutoCrafterTile()
    {}

    public AutoCrafterTile(World world)
    {
        setWorldObj(world);
    }

    @Override
    public int getSizeInventory()
    {
        return multiInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return multiInventory.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        return multiInventory.decrStackSize(i, j);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return multiInventory.getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        if (i == SLOT_OUT) updateRecipe();
        multiInventory.setInventorySlotContents(i, itemstack);
    }

    @Override
    public String getInventoryName()
    {
        return "AutoCrafter";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
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
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        InventoryHelper.readInvFromNBT(inventoryCraftResult, INV_RESULT, data);
        InventoryHelper.readInvFromNBT(inventoryMatrix, INV_MATRIX, data);
        InventoryHelper.readInvFromNBT(inventoryIn, INV_IN, data);
        InventoryHelper.readInvFromNBT(inventoryOut, INV_OUT, data);
        redstoneMode = data.getInteger("redstoneMode");
        crafts = data.getInteger("crafts");

        updateRecipe(); // Must update after load.
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        InventoryHelper.writeInvToNBT(inventoryCraftResult, INV_RESULT, data);
        InventoryHelper.writeInvToNBT(inventoryMatrix, INV_MATRIX, data);
        InventoryHelper.writeInvToNBT(inventoryIn, INV_IN, data);
        InventoryHelper.writeInvToNBT(inventoryOut, INV_OUT, data);
        data.setInteger("redstoneMode", redstoneMode);
        data.setInteger("crafts", crafts);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }
}