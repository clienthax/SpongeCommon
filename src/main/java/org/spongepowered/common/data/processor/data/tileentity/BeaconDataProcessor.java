package org.spongepowered.common.data.processor.data.tileentity;

import com.google.common.collect.Maps;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBeaconData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.common.data.manipulator.mutable.tileentity.SpongeBeaconData;
import org.spongepowered.common.data.processor.common.AbstractTileEntityDataProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BeaconDataProcessor extends AbstractTileEntityDataProcessor<TileEntityBeacon, BeaconData, ImmutableBeaconData> {

    public BeaconDataProcessor() {
        super(TileEntityBeacon.class);
    }

    @Override
    protected boolean doesDataExist(TileEntityBeacon dataHolder) {
        return true;
    }

    @Override
    protected boolean set(TileEntityBeacon dataHolder, Map<Key<?>, Object> keyValues) {
        Object o = keyValues.get(Keys.BEACON_PRIMARY_EFFECT);
        System.out.println(o);//TODO wtfffffff
        return true;
    }

    @Override
    protected Map<Key<?>, ?> getValues(TileEntityBeacon dataHolder) {
        HashMap<Key<?>, PotionEffectType> values = Maps.newHashMapWithExpectedSize(2);
        int primaryID = dataHolder.getField(1);
        int secondaryID = dataHolder.getField(2);
        if(primaryID != 0) {
            values.put(Keys.BEACON_PRIMARY_EFFECT, (PotionEffectType) Potion.potionTypes[primaryID]);
        }
        if(secondaryID != 0) {
            values.put(Keys.BEACON_SECONDARY_EFFECT, (PotionEffectType) Potion.potionTypes[secondaryID]);
        }

        return values;
    }

    @Override
    protected BeaconData createManipulator() {
        return new SpongeBeaconData();
    }

    @Override
    public Optional<BeaconData> fill(DataContainer container, BeaconData beaconData) {
        if(!container.contains(Keys.BEACON_PRIMARY_EFFECT.getQuery()) && !container.contains(Keys.BEACON_SECONDARY_EFFECT.getQuery())) {
            return Optional.empty();
        }
        if(container.contains(Keys.BEACON_PRIMARY_EFFECT.getQuery())) {
            beaconData.set(Keys.BEACON_PRIMARY_EFFECT, PotionEffectTypes.BLINDNESS);
        }

        return null;
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        return DataTransactionResult.failNoData();
    }
}
