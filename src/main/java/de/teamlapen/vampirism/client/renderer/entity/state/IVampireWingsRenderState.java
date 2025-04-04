package de.teamlapen.vampirism.client.renderer.entity.state;

import de.teamlapen.vampirism.api.entity.player.vampire.IWingsEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public interface IVampireWingsRenderState {

    AnimationState vampirism$getFlyAnimationState();

    AnimationState vampirism$getGrowingWingsAnimationState();

    void vampirism$setWingsState(IWingsEntity.WingsState wingsState);

    IWingsEntity.WingsState vampirism$getWingsState();

    default void vampirism$setWingsTexture(@Nullable ResourceLocation texture) {

    }

    @Nullable
    default ResourceLocation vampirism$getWingsTexture() {
        return null;
    }
}
