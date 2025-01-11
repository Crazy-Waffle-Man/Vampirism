package de.teamlapen.vampirism.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.world.dimension.UnderworldBiomeSource;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.OptionalLong;

public class ModDimensions {

    public static final ResourceKey<DimensionType> UNDERWORLD = ResourceKey.create(Registries.DIMENSION_TYPE, VResourceLocation.mod("underworld"));
    public static final ResourceKey<LevelStem> UNDERWORLD_STEM = ResourceKey.create(Registries.LEVEL_STEM, VResourceLocation.mod("underworld"));
    public static final ResourceKey<Level> UNDERWORLD_LEVEL = ResourceKey.create(Registries.DIMENSION, UNDERWORLD_STEM.location());
    public static final ResourceKey<NoiseGeneratorSettings> RULES = ResourceKey.create(Registries.NOISE_SETTINGS, VResourceLocation.mod("noise"));
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(Registries.BIOME_SOURCE, REFERENCE.MODID);
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace("end/sloped_cheese"));
    private static final ResourceKey<DensityFunction> SHIFT_X = ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace("shift_x"));
    private static final ResourceKey<DensityFunction> SHIFT_Z = ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace("shift_z"));


    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<? extends BiomeSource>> UNDERWORLD_BIOME_SOURCE = BIOME_SOURCES.register("underworld", () -> UnderworldBiomeSource.CODEC);

    static void register(IEventBus bus) {
        BIOME_SOURCES.register(bus);
    }
    static void bootstrapTypes(BootstrapContext<DimensionType> context) {
        context.register(UNDERWORLD, new DimensionType(OptionalLong.of(6000), false, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_END, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 5f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0,7), 0)));
    }

    static void bootstrapLevels(BootstrapContext<LevelStem> context) {
        var dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        var noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        var biomes = context.lookup(Registries.BIOME);
        context.register(UNDERWORLD_STEM, new LevelStem(dimensionTypes.getOrThrow(UNDERWORLD), new NoiseBasedChunkGenerator(new UnderworldBiomeSource(biomes), noiseSettings.getOrThrow(RULES))));
    }

    static void bootstrapSource(BootstrapContext<NoiseGeneratorSettings> context) {
        context.register(RULES, new NoiseGeneratorSettings(new NoiseSettings(0,128,1,2), ModBlocks.DARK_STONE.get().defaultBlockState(), Blocks.AIR.defaultBlockState(), rute(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)), rule(), List.of(), 0, false, false, false, true));
    }

    private static SurfaceRules.RuleSource rule() {
        SurfaceRules.RuleSource cursed_earth = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_EARTH.get().defaultBlockState());
        SurfaceRules.RuleSource grass = new SurfaceRules.BlockRuleSource(ModBlocks.CURSED_GRASS.get().defaultBlockState());
        SurfaceRules.ConditionSource inVampireBiome = SurfaceRules.isBiome(ModBiomes.UNDERWORLD_VAMPIRE_FOREST);
        SurfaceRules.RuleSource vampireForestTopLayer = SurfaceRules.ifTrue(inVampireBiome, grass);
        SurfaceRules.RuleSource vampireForestBaseLayer = SurfaceRules.ifTrue(inVampireBiome, cursed_earth);
        return SurfaceRules.sequence(
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestTopLayer))),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(vampireForestBaseLayer)))
                        )
        );
    }

    private static NoiseRouter rute(HolderGetter<DensityFunction> density, HolderGetter<NormalNoise.NoiseParameters> noise) {
        DensityFunction densityfunction = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
        DensityFunction densityfunction1 = postProcess(slideEnd(getFunction(density, SLOPED_CHEESE_END)));

        DensityFunction densityfunction4 = getFunction(density, SHIFT_X);
        DensityFunction densityfunction5 = getFunction(density, SHIFT_Z);
        DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(
                densityfunction4, densityfunction5, 0.25, noise.getOrThrow(Noises.VEGETATION));
        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                densityfunction7,
                DensityFunctions.zero(),
                densityfunction,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                slideEnd(DensityFunctions.add(densityfunction, DensityFunctions.constant(-0.703125))),
                densityfunction1,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );
    }

    private static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction densityfunction = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction), DensityFunctions.constant(0.64)).squeeze();
    }

    private static DensityFunction slideEnd(DensityFunction densityFunction) {
        return slideEndLike(densityFunction, 0, 128);
    }

    private static DensityFunction slideEndLike(DensityFunction densityFunction, int minY, int maxY) {
        return slide(densityFunction, minY, maxY, 72, -184, -23.4375, 4, 32, -0.234375);
    }

    private static DensityFunction slide(
            DensityFunction input, int minY, int maxY, int p_224447_, int p_224448_, double p_224449_, int p_224450_, int p_224451_, double p_224452_
    ) {
        DensityFunction densityfunction1 = DensityFunctions.yClampedGradient(minY + maxY - p_224447_, minY + maxY - p_224448_, 1.0, 0.0);
        DensityFunction $$9 = DensityFunctions.lerp(densityfunction1, p_224449_, input);
        DensityFunction densityfunction2 = DensityFunctions.yClampedGradient(minY + p_224450_, minY + p_224451_, 0.0, 1.0);
        return DensityFunctions.lerp(densityfunction2, p_224452_, $$9);
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }

}
