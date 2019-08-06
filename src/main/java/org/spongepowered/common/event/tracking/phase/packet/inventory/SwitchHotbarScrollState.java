/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.event.tracking.phase.packet.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.bridge.inventory.ContainerBridge;
import org.spongepowered.common.bridge.inventory.TrackedInventoryBridge;
import org.spongepowered.common.event.tracking.phase.packet.PacketPhaseUtil;
import org.spongepowered.common.item.inventory.util.ContainerUtil;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public final class SwitchHotbarScrollState extends BasicInventoryPacketState {

    public SwitchHotbarScrollState() {
    }

    @Override
    public void populateContext(ServerPlayerEntity playerMP, IPacket<?> packet, InventoryPacketContext context) {
        super.populateContext(playerMP, packet, context);
        context.setHighlightedSlotId(playerMP.inventory.currentItem);
    }

    @Override
    public ClickInventoryEvent createInventoryEvent(ServerPlayerEntity playerMP, Container openContainer, Transaction<ItemStackSnapshot> transaction,
            List<SlotTransaction> slotTransactions, List<Entity> capturedEntities, int usedButton, @Nullable org.spongepowered.api.item.inventory.Slot slot) {
        return SpongeEventFactory.createClickInventoryEventNumberPress(Sponge.getCauseStackManager().getCurrentCause(), transaction,
                Optional.ofNullable(slot), openContainer, slotTransactions, usedButton);
    }

    @Override
    public void unwind(InventoryPacketContext context) {

        final ServerPlayerEntity player = context.getPacketPlayer();
        final CHeldItemChangePacket itemChange = context.getPacket();
        final int previousSlot = context.getHighlightedSlotId();
        final net.minecraft.inventory.Container inventoryContainer = player.inventoryContainer;
        final PlayerInventory inventory = player.inventory;
        int preHotbarSize = inventory.mainInventory.size() - PlayerInventory.getHotbarSize() + inventory.armorInventory.size() + 4 + 1; // Crafting Grid & Result
        final Slot sourceSlot = inventoryContainer.getSlot(previousSlot + preHotbarSize);
        final Slot targetSlot = inventoryContainer.getSlot(itemChange.getSlotId() + preHotbarSize);

        ItemStackSnapshot sourceSnapshot = ItemStackUtil.snapshotOf(sourceSlot.getStack());
        ItemStackSnapshot targetSnapshot = ItemStackUtil.snapshotOf(targetSlot.getStack());
        org.spongepowered.api.item.inventory.Slot slotPrev = ContainerUtil.getSlot(inventoryContainer, previousSlot + preHotbarSize);
        SlotTransaction sourceTransaction = new SlotTransaction(slotPrev, sourceSnapshot, sourceSnapshot);
        org.spongepowered.api.item.inventory.Slot slotNew = ContainerUtil.getSlot(inventoryContainer, itemChange.getSlotId() + preHotbarSize);
        SlotTransaction targetTransaction = new SlotTransaction(slotNew, targetSnapshot, targetSnapshot);
        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(player);
            ImmutableList<SlotTransaction> transactions =
                new ImmutableList.Builder<SlotTransaction>().add(sourceTransaction).add(targetTransaction).build();
            final ChangeInventoryEvent.Held changeInventoryEventHeld = SpongeEventFactory
                .createChangeInventoryEventHeld(Sponge.getCauseStackManager().getCurrentCause(), slotNew, slotPrev, (Inventory) inventoryContainer, transactions);
            net.minecraft.inventory.Container openContainer = player.openContainer;
            SpongeImpl.postEvent(changeInventoryEventHeld);
            if (changeInventoryEventHeld.isCancelled() || PacketPhaseUtil.allTransactionsInvalid(changeInventoryEventHeld.getTransactions())) {
                player.connection.sendPacket(new SHeldItemChangePacket(previousSlot));
                inventory.currentItem = previousSlot;
            } else {
                PacketPhaseUtil.handleSlotRestore(player, openContainer, changeInventoryEventHeld.getTransactions(), false);
                inventory.currentItem = itemChange.getSlotId();
                player.markPlayerActive();
            }
            ((TrackedInventoryBridge) openContainer).bridge$setCaptureInventory(false);
        }
    }
}
