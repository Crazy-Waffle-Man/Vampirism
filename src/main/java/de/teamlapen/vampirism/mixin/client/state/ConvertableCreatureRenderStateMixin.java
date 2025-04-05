package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.client.renderer.entity.state.extensions.IConvertedOverlayRenderStateExtension;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class ConvertableCreatureRenderStateMixin implements IConvertedOverlayRenderStateExtension {

    @Unique
    public ResourceLocation vampirism$convertedOverlay;
    @Unique
    public ResourceLocation vampirism$overlay;


    @Override
    public @Nullable ResourceLocation vampirism$overlay() {
        return this.vampirism$overlay;
    }

    @Override
    public void vampirism$overlay(@Nullable ResourceLocation overlay) {
        this.vampirism$overlay = overlay;
    }

    @Override
    public @Nullable ResourceLocation vampirism$convertedOverlay() {
        return this.vampirism$convertedOverlay;
    }

    @Override
    public void vampirism$convertedOverlay(@Nullable ResourceLocation overlay) {
        this.vampirism$convertedOverlay = overlay;
    }
}
