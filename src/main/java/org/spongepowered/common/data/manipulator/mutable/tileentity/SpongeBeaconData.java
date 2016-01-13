package org.spongepowered.common.data.manipulator.mutable.tileentity;

import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBeaconData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.common.data.manipulator.immutable.tileentity.ImmutableSpongeBeaconData;
import org.spongepowered.common.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.common.data.value.mutable.SpongeOptionalValue;

import java.util.Optional;

import javax.annotation.Nullable;

public class SpongeBeaconData extends AbstractData<BeaconData, ImmutableBeaconData> implements BeaconData {

    @Nullable
    PotionEffectType primaryEffect;
    @Nullable
    PotionEffectType secondaryEffect;

    public SpongeBeaconData() {
        this(null, null);
    }

    public SpongeBeaconData(@Nullable PotionEffectType primaryEffect, @Nullable PotionEffectType secondaryEffect) {
        super(BeaconData.class);
        this.primaryEffect = primaryEffect;
        this.secondaryEffect = secondaryEffect;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(Keys.BEACON_PRIMARY_EFFECT, this::getPrimaryEffect);
        registerFieldSetter(Keys.BEACON_PRIMARY_EFFECT, this::setPrimaryEffect);
        registerKeyValue(Keys.BEACON_PRIMARY_EFFECT, this::primaryEffect);

        registerFieldGetter(Keys.BEACON_SECONDARY_EFFECT, this::getSecondaryEffect);
        registerFieldSetter(Keys.BEACON_SECONDARY_EFFECT, this::setSecondaryEffect);
        registerKeyValue(Keys.BEACON_SECONDARY_EFFECT, this::secondaryEffect);
    }

    public Optional<PotionEffectType> getPrimaryEffect() {
        return Optional.ofNullable(this.primaryEffect);
    }

    public void setPrimaryEffect(Optional<PotionEffectType> primaryEffect) {
        this.primaryEffect = primaryEffect.orElse(null);
    }

    @Override
    public OptionalValue<PotionEffectType> primaryEffect() {
        return new SpongeOptionalValue<>(Keys.BEACON_PRIMARY_EFFECT, Optional.ofNullable(this.primaryEffect));
    }

    public Optional<PotionEffectType> getSecondaryEffect() {
        return Optional.ofNullable(this.secondaryEffect);
    }

    public void setSecondaryEffect(Optional<PotionEffectType> secondaryEffect) {
        this.secondaryEffect = secondaryEffect.orElse(null);
    }

    @Override
    public OptionalValue<PotionEffectType> secondaryEffect() {
        return new SpongeOptionalValue<>(Keys.BEACON_SECONDARY_EFFECT, Optional.ofNullable(this.secondaryEffect));
    }

    @Override
    public BeaconData clearEffects() {
        this.primaryEffect = null;
        this.secondaryEffect = null;
        return this;
    }

    @Override
    public BeaconData copy() {
        return new SpongeBeaconData(this.primaryEffect, this.secondaryEffect);
    }

    @Override
    public ImmutableBeaconData asImmutable() {
        return new ImmutableSpongeBeaconData(this.primaryEffect, this.secondaryEffect);
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
    public int compareTo(BeaconData o) {
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
