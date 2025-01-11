package de.teamlapen.vampirism.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class BossFeatureConfiguration implements FeatureConfiguration {
    public static final BossFeatureConfiguration INSTANCE = new BossFeatureConfiguration();
    public static final Codec<BossFeatureConfiguration> CODEC = Codec.unit(INSTANCE);
}
