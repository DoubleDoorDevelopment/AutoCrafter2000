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

package net.doubledoordev.autoCrafter.buildcraft;

import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import net.doubledoordev.autoCrafter.AutoCrafter2000;
import net.doubledoordev.autoCrafter.buildcraft.triggers.InventoryTrigger;
import net.doubledoordev.autoCrafter.buildcraft.triggers.RecipeSetTrigger;

import java.util.LinkedList;

/**
 * Does all BC comparability stuff.
 * Catches all errors, but isn't called if BS isn't installed.
 *
 * @author Dries007
 */
public class BuildcraftHelper
{
    private static final LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

    public static void init()
    {
        AutoCrafter2000.getLogger().info("BuildCraft compatibility init ...");
        try
        {
            triggers.add(new RecipeSetTrigger());
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Empty, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Empty, InventoryTrigger.InventoryType.Out));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Full, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Full, InventoryTrigger.InventoryType.Out));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Items, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Items, InventoryTrigger.InventoryType.Out));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Space, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Space, InventoryTrigger.InventoryType.Out));

            for (ITrigger trigger : triggers) ActionManager.registerTrigger(trigger);
            AutoCrafter2000.getLogger().info("BuildCraft compatibility done.");
        }
        catch (Exception e)
        {
            AutoCrafter2000.getLogger().warn("BuildCraft compatibility FAILED.");
            e.fillInStackTrace();
        }
    }

    /**
     * Used for overriding the default BC triggers.
     */
    public static LinkedList<ITrigger> getAutocrafterTriggers()
    {
        return triggers;
    }
}
