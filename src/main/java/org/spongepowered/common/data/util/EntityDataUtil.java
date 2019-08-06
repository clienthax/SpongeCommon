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
package org.spongepowered.common.data.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

public class EntityDataUtil {

    public static Optional<ItemStackSnapshot> getRepresentedItemFrom(Entity entity) {
        if (entity instanceof ItemFrameEntity) {
            final ItemStack itemStack = ((ItemFrameEntity) entity).getDisplayedItem();
            if (!itemStack.isEmpty()) {
                return Optional.of(((org.spongepowered.api.item.inventory.ItemStack) itemStack).createSnapshot());
            }
        } else if (entity instanceof ItemEntity) {
            return Optional.of(((org.spongepowered.api.item.inventory.ItemStack) ((ItemEntity) entity).getItem()).createSnapshot());
        }
        return Optional.empty();
    }

}
