package de.teamlapen.vampirism.world.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnderworldEffects extends DimensionSpecialEffects {

    public UnderworldEffects() {
        super(Float.NaN, false, SkyType.NONE, true, false);
    }

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor.scale(0.15f);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public float @Nullable [] getSunriseColor(float timeOfDay, float partialTicks) {
        return null;
    }
}
