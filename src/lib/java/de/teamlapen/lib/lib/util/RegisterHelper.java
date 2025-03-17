package de.teamlapen.lib.lib.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.jetbrains.annotations.NotNull;

public class RegisterHelper {

    public static <T extends Block> T flammable(T block, int encouragement, int flammability) {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        fireblock.setFlammable(block, encouragement, flammability);
        return block;
    }

    public static <T extends Block> T potted(T potBlock, ResourceLocation plant) {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        pot.addPlant(plant, () -> potBlock);
        return potBlock;
    }

    public static <T extends ParticleOptions> ParticleType<T> particleType(boolean overrideLimiter, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return new ParticleType<T>(overrideLimiter) {
            @Override
            public @NotNull MapCodec<T> codec() {
                return codec;
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }
}
