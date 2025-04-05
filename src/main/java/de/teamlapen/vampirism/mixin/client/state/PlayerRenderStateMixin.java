package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.client.renderer.entity.state.extensions.IPlayerRenderStateExtension;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements IPlayerRenderStateExtension {

    @Unique
    private VampirismPlayerAttributes vampirism$vampirismAttributes;

    @Override
    public VampirismPlayerAttributes vampirism$attributes() {
        return this.vampirism$vampirismAttributes;
    }

    @Override
    public void vampirism$attributes(VampirismPlayerAttributes attributes) {
        this.vampirism$vampirismAttributes = attributes;
    }
}
