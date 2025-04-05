package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.api.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.client.renderer.entity.state.extensions.IVampirePlayerRenderStateExtension;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class VampirePlayerRenderStateMixin implements IVampirePlayerRenderStateExtension {

    @Unique
    private Bat vampirism$bat;
    @Unique
    private final AnimationState vampirism$flyAnimationState = new AnimationState();
    @Unique
    private final AnimationState vampirism$growingWingsAnimationState = new AnimationState();
    @Unique
    private IWingsEntity.WingsState vampirism$wingsState;
    @Nullable
    @Unique
    private ResourceLocation vampirism$wingsTexture;

    @Override
    public Bat vampirism$bat() {
        return this.vampirism$bat;
    }

    @Override
    public void vampirism$bat(Bat bat) {
        this.vampirism$bat = bat;
    }

    @Override
    public @NotNull AnimationState vampirism$getFlyAnimationState() {
        return this.vampirism$flyAnimationState;
    }

    @Override
    public @NotNull AnimationState vampirism$getGrowingWingsAnimationState() {
        return this.vampirism$growingWingsAnimationState;
    }

    @Override
    public void vampirism$setWingsState(IWingsEntity.@NotNull WingsState wingsState) {
        this.vampirism$wingsState = wingsState;
    }

    @Override
    public IWingsEntity.@NotNull WingsState vampirism$getWingsState() {
        return this.vampirism$wingsState;
    }

    @Override
    public void vampirism$setWingsTexture(@Nullable ResourceLocation texture) {
        this.vampirism$wingsTexture = texture;
    }

    @Override
    public @Nullable ResourceLocation vampirism$getWingsTexture() {
        return this.vampirism$wingsTexture;
    }
}
