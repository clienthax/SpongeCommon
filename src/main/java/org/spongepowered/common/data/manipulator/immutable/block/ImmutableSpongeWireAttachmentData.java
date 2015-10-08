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
package org.spongepowered.common.data.manipulator.immutable.block;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableWireAttachmentData;
import org.spongepowered.api.data.manipulator.mutable.block.WireAttachmentData;
import org.spongepowered.api.data.type.WireAttachmentType;
import org.spongepowered.api.data.type.WireAttachmentTypes;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Direction;
import org.spongepowered.common.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.common.data.manipulator.mutable.block.SpongeWireAttachementData;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeMapValue;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;

import java.util.Map;

public class ImmutableSpongeWireAttachmentData extends AbstractImmutableData<ImmutableWireAttachmentData, WireAttachmentData> implements ImmutableWireAttachmentData {

    private final ImmutableMap<Direction, WireAttachmentType> wireAttachmentMap;

    public ImmutableSpongeWireAttachmentData(Map<Direction, WireAttachmentType> wireAttachmentMap) {
        super(ImmutableWireAttachmentData.class);
        this.wireAttachmentMap = ImmutableMap.copyOf(wireAttachmentMap);
    }

    @Override
    public ImmutableMapValue<Direction, WireAttachmentType> wireAttachments() {
        return new ImmutableSpongeMapValue<>(Keys.WIRE_ATTACHMENTS, this.wireAttachmentMap);
    }

    @Override
    public ImmutableValue<WireAttachmentType> wireAttachmentNorth() {
        return ImmutableSpongeValue.cachedOf(Keys.WIRE_ATTACHMENT_NORTH, WireAttachmentTypes.NONE, this.wireAttachmentMap.get(Direction.NORTH));
    }

    @Override
    public ImmutableValue<WireAttachmentType> wireAttachmentSouth() {
        return ImmutableSpongeValue.cachedOf(Keys.WIRE_ATTACHMENT_SOUTH, WireAttachmentTypes.NONE, this.wireAttachmentMap.get(Direction.SOUTH));
    }

    @Override
    public ImmutableValue<WireAttachmentType> wireAttachmentEast() {
        return ImmutableSpongeValue.cachedOf(Keys.WIRE_ATTACHMENT_EAST, WireAttachmentTypes.NONE, this.wireAttachmentMap.get(Direction.EAST));
    }

    @Override
    public ImmutableValue<WireAttachmentType> wireAttachmentWest() {
        return ImmutableSpongeValue.cachedOf(Keys.WIRE_ATTACHMENT_WEST, WireAttachmentTypes.NONE, this.wireAttachmentMap.get(Direction.WEST));
    }

    @Override
    public WireAttachmentData asMutable() {
        return new SpongeWireAttachementData(this.wireAttachmentMap);
    }

    @Override
    public int compareTo(ImmutableWireAttachmentData o) {
        return ComparisonChain.start()
            .compare(o.wireAttachmentNorth().get().getId(), this.wireAttachmentMap.get(Direction.NORTH).getId())
            .compare(o.wireAttachmentSouth().get().getId(), this.wireAttachmentMap.get(Direction.SOUTH).getId())
            .compare(o.wireAttachmentEast().get().getId(), this.wireAttachmentMap.get(Direction.EAST).getId())
            .compare(o.wireAttachmentWest().get().getId(), this.wireAttachmentMap.get(Direction.WEST).getId())
            .result();
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
            .set(Keys.WIRE_ATTACHMENTS.getQuery(), this.wireAttachmentMap)
            .set(Keys.WIRE_ATTACHMENT_NORTH.getQuery(), this.wireAttachmentMap.get(Direction.NORTH).getId())
            .set(Keys.WIRE_ATTACHMENT_EAST.getQuery(), this.wireAttachmentMap.get(Direction.EAST).getId())
            .set(Keys.WIRE_ATTACHMENT_SOUTH.getQuery(), this.wireAttachmentMap.get(Direction.SOUTH).getId())
            .set(Keys.WIRE_ATTACHMENT_WEST.getQuery(), this.wireAttachmentMap.get(Direction.WEST).getId());
    }

    @Override
    protected void registerGetters() {
        // TODO
    }
}
