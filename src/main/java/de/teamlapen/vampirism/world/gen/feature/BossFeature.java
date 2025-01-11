package de.teamlapen.vampirism.world.gen.feature;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class BossFeature extends Feature<BossFeatureConfiguration> {

    public BossFeature(Codec<BossFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<BossFeatureConfiguration> context) {

        for (int i = 0; i < 10; i++) {
            setBlock(context.level(), new BlockPos(0,i + context.level().getMinBuildHeight(),0), Blocks.OBSIDIAN.defaultBlockState());
        }
        return true;
    }
}
