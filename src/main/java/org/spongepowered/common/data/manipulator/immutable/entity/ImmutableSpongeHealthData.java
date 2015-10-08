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
package org.spongepowered.common.data.manipulator.immutable.entity;

import static org.spongepowered.common.data.util.ComparatorUtil.doubleComparator;

import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.common.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeHealthData;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeBoundedValue;
import org.spongepowered.common.util.GetterFunction;

public class ImmutableSpongeHealthData extends AbstractImmutableData<ImmutableHealthData, HealthData> implements ImmutableHealthData {

    private final double health;
    private final double maxHealth;

    public ImmutableSpongeHealthData(double health, double maxHealth) {
        super(ImmutableHealthData.class);
        this.health = health;
        this.maxHealth = maxHealth;
        registerGetters();
    }

    @Override
    public ImmutableBoundedValue<Double> health() {
        return ImmutableSpongeBoundedValue.cachedOf(Keys.HEALTH, this.health, this.maxHealth, doubleComparator(), 0D, this.maxHealth);
    }

    @Override
    public ImmutableBoundedValue<Double> maxHealth() {
        return ImmutableSpongeBoundedValue.cachedOf(Keys.HEALTH, this.maxHealth, this.maxHealth, doubleComparator(), 0D, (double) Float.MAX_VALUE);
    }

    @Override
    public HealthData asMutable() {
        return new SpongeHealthData(this.health, this.maxHealth);
    }

    @Override
    public int compareTo(ImmutableHealthData o) {
        return ComparisonChain.start()
            .compare(o.health().get().doubleValue(), this.health)
            .compare(o.maxHealth().get().doubleValue(), this.maxHealth)
            .result();
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
            .set(Keys.HEALTH.getQuery(), this.health)
            .set(Keys.MAX_HEALTH.getQuery(), this.maxHealth);
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(Keys.HEALTH, ImmutableSpongeHealthData.this::getHealth);
        registerKeyValue(Keys.HEALTH, ImmutableSpongeHealthData.this::health);

        registerFieldGetter(Keys.MAX_HEALTH, ImmutableSpongeHealthData.this::getMaxHealth);
        registerKeyValue(Keys.MAX_HEALTH, ImmutableSpongeHealthData.this::maxHealth);
    }

    public double getHealth() {
        return this.health;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }
}
