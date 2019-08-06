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
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockState;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableDirectionalData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableHingeData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableOpenData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePortionData;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutablePoweredData;
import org.spongepowered.api.data.type.Hinge;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.data.ImmutableDataCachingUtil;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongeDirectionalData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongeHingeData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongeOpenData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongePortionData;
import org.spongepowered.common.data.manipulator.immutable.block.ImmutableSpongePoweredData;
import org.spongepowered.common.data.util.DirectionChecker;
import org.spongepowered.common.data.util.DirectionResolver;

import java.util.Optional;

@Mixin(DoorBlock.class)
public abstract class BlockDoorMixin extends BlockMixin {

    @SuppressWarnings("RedundantTypeArguments") // some java compilers will not calculate this generic correctly
    @Override
    public ImmutableList<ImmutableDataManipulator<?, ?>> bridge$getManipulators(final BlockState blockState) {
        return ImmutableList.<ImmutableDataManipulator<?, ?>>of(impl$getHingeFor(blockState), impl$getIsOpenFor(blockState),
                impl$getIsPoweredFor(blockState), impl$getDirectionalData(blockState), impl$getPortionData(blockState));
    }

    @Override
    public boolean bridge$supports(final Class<? extends ImmutableDataManipulator<?, ?>> immutable) {
        return ImmutableHingeData.class.isAssignableFrom(immutable) || ImmutableOpenData.class.isAssignableFrom(immutable)
                || ImmutablePoweredData.class.isAssignableFrom(immutable) || ImmutablePortionData.class.isAssignableFrom(immutable);
    }

    @Override
    public Optional<BlockState> bridge$getStateWithData(final BlockState blockState, final ImmutableDataManipulator<?, ?> manipulator) {
        if (manipulator instanceof ImmutableHingeData) {
            final DoorBlock.EnumHingePosition hinge = (DoorBlock.EnumHingePosition) (Object) ((ImmutableHingeData) manipulator).type().get();
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.HINGE, hinge));
        }
        if (manipulator instanceof ImmutableOpenData) {
            final boolean isOpen = ((ImmutableOpenData) manipulator).open().get();
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.OPEN, isOpen));
        }
        if (manipulator instanceof ImmutablePoweredData) {
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.POWERED, ((ImmutablePoweredData) manipulator).powered().get()));
        }
        if (manipulator instanceof ImmutableDirectionalData) {
            final Direction dir = DirectionChecker.checkDirectionToHorizontal(((ImmutableDirectionalData) manipulator).direction().get());
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.FACING, DirectionResolver.getFor(dir)));
        }
        if (manipulator instanceof ImmutablePortionData) {
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.HALF,
                    impl$convertPortionType(((ImmutablePortionData) manipulator).type().get())));
        }
        return super.bridge$getStateWithData(blockState, manipulator);
    }

    @Override
    public <E> Optional<BlockState> bridge$getStateWithValue(final BlockState blockState, final Key<? extends BaseValue<E>> key, final E value) {
        if (key.equals(Keys.HINGE_POSITION)) {
            final DoorBlock.EnumHingePosition hinge = (DoorBlock.EnumHingePosition) value;
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.HINGE, hinge));
        }
        if (key.equals(Keys.OPEN)) {
            final boolean isOpen = (Boolean) value;
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.OPEN, isOpen));
        }
        if (key.equals(Keys.POWERED)) {
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.POWERED, (Boolean) value));
        }
        if (key.equals(Keys.DIRECTION)) {
            final Direction dir = DirectionChecker.checkDirectionToHorizontal((Direction) value);
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.FACING, DirectionResolver.getFor(dir)));
        }
        if (key.equals(Keys.PORTION_TYPE)) {
            return Optional.of((BlockState) blockState.withProperty(DoorBlock.HALF, impl$convertPortionType((PortionType) value)));
        }
        return super.bridge$getStateWithValue(blockState, key, value);
    }

    private DoorBlock.EnumDoorHalf impl$convertPortionType(final PortionType portionType) {
        return portionType == PortionTypes.BOTTOM ? DoorBlock.EnumDoorHalf.LOWER : DoorBlock.EnumDoorHalf.UPPER;
    }

    private ImmutableHingeData impl$getHingeFor(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongeHingeData.class, (Hinge) (Object) blockState.getValue(DoorBlock.HINGE));
    }

    private ImmutableOpenData impl$getIsOpenFor(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongeOpenData.class, blockState.getValue(DoorBlock.OPEN));
    }

    private ImmutablePoweredData impl$getIsPoweredFor(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongePoweredData.class, blockState.getValue(DoorBlock.POWERED));
    }

    private ImmutableDirectionalData impl$getDirectionalData(final BlockState blockState) {
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongeDirectionalData.class,
                DirectionResolver.getFor(blockState.getValue(DoorBlock.FACING)));
    }

    private ImmutablePortionData impl$getPortionData(final BlockState blockState) {
        final EnumDoorHalf half = blockState.getValue(DoorBlock.HALF);
        return ImmutableDataCachingUtil.getManipulator(ImmutableSpongePortionData.class,
                half == EnumDoorHalf.LOWER ? PortionTypes.BOTTOM : PortionTypes.TOP);
    }

}
