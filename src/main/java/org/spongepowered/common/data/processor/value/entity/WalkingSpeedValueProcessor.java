package org.spongepowered.common.data.processor.value.entity;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.data.DataTransactionBuilder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.processor.common.AbstractSpongeValueProcessor;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeBoundedValue;

import java.util.Optional;

import static org.spongepowered.common.data.util.ComparatorUtil.doubleComparator;

public class WalkingSpeedValueProcessor extends AbstractSpongeValueProcessor<Double, MutableBoundedValue<Double>> {

    public WalkingSpeedValueProcessor() {
        super(Keys.WALKING_SPEED);
    }

    @Override
    protected MutableBoundedValue<Double> constructValue(Double defaultValue) {
        return new SpongeBoundedValue<>(this.getKey(), 0.1d, doubleComparator(), 0d, Double.MAX_VALUE, defaultValue);
    }

    @Override
    public Optional<Double> getValueFromContainer(ValueContainer<?> container) {
        if (supports(container)) {
            return Optional.of((double) ((EntityPlayer) container).capabilities.getWalkSpeed());
        }
        return Optional.empty();
    }

    @Override
    public boolean supports(ValueContainer<?> container) {
        return container instanceof EntityPlayer;
    }

    @Override
    public DataTransactionResult offerToStore(ValueContainer<?> container, Double value) {
        final ImmutableValue<Double> proposedValue = new ImmutableSpongeValue<>(Keys.WALKING_SPEED, value);
        if (supports(container)) {
            final ImmutableValue<Double> newWalkingSpeedValue = new ImmutableSpongeValue<>(Keys.WALKING_SPEED, value);
            final ImmutableValue<Double> oldWalkingSpeedValue = getApiValueFromContainer(container).get().asImmutable();
            ((EntityPlayer) (container)).capabilities.walkSpeed = value.floatValue();
            ((EntityPlayer) container).sendPlayerAbilities();
            return DataTransactionBuilder.successReplaceResult(oldWalkingSpeedValue, newWalkingSpeedValue);
        }
        return DataTransactionBuilder.failResult(proposedValue);
    }

    @Override
    public DataTransactionResult removeFrom(ValueContainer<?> container) {
        if (supports(container)) {
            final DataTransactionBuilder builder = DataTransactionBuilder.builder();
            builder.replace(getApiValueFromContainer(container).get().asImmutable());
            builder.replace(container.getValue(Keys.WALKING_SPEED).get().asImmutable());
            ((EntityPlayer) container).capabilities.walkSpeed = 0.1F;
            ((EntityPlayer) container).sendPlayerAbilities();
            return builder.result(DataTransactionResult.Type.SUCCESS).build();

        }
        return DataTransactionBuilder.failNoData();
    }
}
