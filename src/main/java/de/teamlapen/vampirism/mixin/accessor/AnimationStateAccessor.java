package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimationState.class)
public interface AnimationStateAccessor {

    @Accessor("startTick")
    int getStartTick();
}
