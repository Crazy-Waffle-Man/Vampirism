package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.client.renderer.entity.state.extensions.IFactionRenderStateExtension;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class FactionLivingEntityRenderStateMixin implements IFactionRenderStateExtension {

    @Unique
    private boolean vampirism$isHunter;

    @Override
    public boolean vampirism$hunter() {
        return this.vampirism$isHunter;
    }

    @Override
    public void vampirism$hunter(boolean hunter) {
        this.vampirism$isHunter = hunter;
    }
}
