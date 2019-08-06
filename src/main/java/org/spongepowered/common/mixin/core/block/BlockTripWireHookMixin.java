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
package org.spongepowered.common.mixin.core.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.TripWireHookBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableAttachedData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDirectionalData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePoweredData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.data.ImmutableDataCachingUtil;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongeAttachedData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongeDirectionalData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongePoweredData;
import org.spongepowered.common.data.util.DirectionChecker;
import org.spongepowered.common.data.util.DirectionResolver;

import java.util.Optional;

@Mixin(TripWireHookBlock.class)
public abstract class BlockTripWireHookMixin extends BlockMixin {

    @SuppressWarnings("RedundantTypeArguments") // some JDK's can fail to compile without the explicit type generics
    @Override
    public ImmutableList<ImmutableDataManipulator<?, ?>> bridge$getManipulators(final BlockState blockState) {
        return ImmutableList.<ImmutableDataManipulator<?, ?>>of(impl$getIsAttachedFor(blockState),
                impl$getIsPoweredFor(blockState), impl$getDirectionalData(blockState));
    }

    @Override
    public boolean bridge$supports(final Class<? extends ImmutableDataManipulator<?, ?>> immutable) {
        return ImmutableAttachedData.class.isAssignableFrom(immutable)
                || ImmutablePoweredData.class.isAssignableFrom(immutable) || ImmutableDirectionalData.class.isAssignableFrom(immutable);
    }

    @Override
    public Optional<BlockState> bridge$getStateWithData(final BlockState blockState, final ImmutableDataManipulator<?, ?> manipulator) {
        if (manipulator instanceof ImmutableAttachedData) {
            return Optional.of((BlockState) blockState);
        }
        if (manipulator instanceof ImmutablePoweredData) {
            return Optional.of((BlockState) blockState.withProperty(TripWireHookBlock.POWERED, ((ImmutablePoweredData) manipulator).powered().get()));
        }
        if (manipulator instanceof ImmutableDirectionalData) {
            final Direction dir = DirectionChecker.checkDirectionToHorizontal(((ImmutableDirectionalData) manipulator).direction().get());
            return Optional.of((BlockState) blockState.withProperty(TripWireHookBlock.FACING, DirectionResolver.getFor(dir)));
        }
        return super.bridge$getStateWithData(blockState, manipulator);
    }

    @Override
    public <E> Optional<BlockState> bridge$getStateWithValue(final BlockState blockState, final Key<? extends BaseValue<E>> key, final E value) {
        if (key.equals(Keys.SUSPENDED)) {
            return Optional.of((BlockState) blockState);
        }
        if (key.equals(Keys.ATTACHED)) {
            return Optional.of((BlockState) blockState);
        }
        if (key.equals(Keys.POWERED)) {
            return Optional.of((BlockState) blockState.withProperty(TripWireHookBlock.POWERED, (Boolean) value));
        }
        if (key.equals(Keys.DIRECTION)) {
            final Direction dir = DirectionChecker.checkDirectionToHorizontal((Direction) value);
            return Optional.of((BlockState) blockState.withProperty(TripWireHookBlock.FACING, DirectionResolver.getFor(dir)));
        }
        return super.bridge$getStateWithValue(blockState, key, value);
    }

    private ImmutableAttachedData impl$getIsAttachedFor(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongeAttachedData.class, blockState.getValue(TripWireHookBlock.ATTACHED));
    }

    private ImmutablePoweredData impl$getIsPoweredFor(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongePoweredData.class, blockState.getValue(TripWireHookBlock.POWERED));
    }

    private ImmutableDirectionalData impl$getDirectionalData(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongeDirectionalData.class,
                DirectionResolver.getFor(blockState.getValue(TripWireHookBlock.FACING)));
    }
}
