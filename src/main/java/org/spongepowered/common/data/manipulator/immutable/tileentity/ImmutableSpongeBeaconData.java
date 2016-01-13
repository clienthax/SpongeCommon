package org.spongepowered.common.data.manipulator.immutable.tileentity;

import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBeaconData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.common.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.common.data.manipulator.mutable.tileentity.SpongeBeaconData;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeOptionalValue;

import java.util.Optional;

import javax.annotation.Nullable;

public class ImmutableSpongeBeaconData extends AbstractImmutableData<ImmutableBeaconData, BeaconData> implements ImmutableBeaconData {

    @Nullable
    final PotionEffectType primaryEffect;
    @Nullable
    final PotionEffectType secondaryEffect;
    final ImmutableOptionalValue<PotionEffectType> primaryEffectValue;
    final ImmutableOptionalValue<PotionEffectType> secondaryEffectValue;

    public ImmutableSpongeBeaconData(@Nullable PotionEffectType primaryEffect, @Nullable PotionEffectType secondaryEffect) {
        super(ImmutableBeaconData.class);
        this.primaryEffect = primaryEffect;
        this.secondaryEffect = secondaryEffect;
        this.primaryEffectValue = new ImmutableSpongeOptionalValue<>(Keys.BEACON_PRIMARY_EFFECT, Optional.ofNullable(this.primaryEffect));
        this.secondaryEffectValue = new ImmutableSpongeOptionalValue<>(Keys.BEACON_SECONDARY_EFFECT, Optional.ofNullable(this.secondaryEffect));
        registerGetters();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(Keys.BEACON_PRIMARY_EFFECT, this::getPrimaryEffect);
        registerKeyValue(Keys.BEACON_PRIMARY_EFFECT, this::primaryEffect);

        registerFieldGetter(Keys.BEACON_SECONDARY_EFFECT, this::getSecondaryEffect);
        registerKeyValue(Keys.BEACON_SECONDARY_EFFECT, this::secondaryEffect);
    }

    public Optional<PotionEffectType> getPrimaryEffect() {
        return Optional.ofNullable(this.primaryEffect);
    }

    @Override
    public ImmutableOptionalValue<PotionEffectType> primaryEffect() {
        return primaryEffectValue;
    }

    public Optional<PotionEffectType> getSecondaryEffect() {
        return Optional.ofNullable(this.secondaryEffect);
    }

    @Override
    public ImmutableOptionalValue<PotionEffectType> secondaryEffect() {
        return secondaryEffectValue;
    }

    @Override
    public BeaconData asMutable() {
        return new SpongeBeaconData(this.primaryEffect, this.secondaryEffect);
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        if(this.primaryEffect != null) {
            dataContainer = dataContainer.set(Keys.BEACON_PRIMARY_EFFECT.getQuery(), this.primaryEffect);
        }
        if(this.secondaryEffect != null) {
            dataContainer = dataContainer.set(Keys.BEACON_SECONDARY_EFFECT.getQuery(), this.secondaryEffect);
        }
        return dataContainer;
    }

    @Override
    public int compareTo(ImmutableBeaconData o) {
        ComparisonChain compare = ComparisonChain.start()
                .compare(this.primaryEffect().exists(), o.primaryEffect().exists())
                .compare(this.secondaryEffect().exists(), o.secondaryEffect().exists());
        if(this.primaryEffect().exists() && o.primaryEffect().exists()) {
            compare = compare.compare(this.primaryEffect().get().get().getName(), o.primaryEffect().get().get().getName())
                    .compare(this.primaryEffect().get().get().isInstant(), o.primaryEffect().get().get().isInstant());
        }
        if(this.secondaryEffect().exists() && o.secondaryEffect().exists()) {
            compare = compare.compare(this.secondaryEffect().get().get().getName(), o.secondaryEffect().get().get().getName())
                    .compare(this.secondaryEffect().get().get().isInstant(), o.secondaryEffect().get().get().isInstant());
        }
        return compare.result();
    }
}
