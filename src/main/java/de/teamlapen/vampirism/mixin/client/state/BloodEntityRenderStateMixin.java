package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.client.renderer.entity.state.extensions.ICreatureRenderStateExtension;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class BloodEntityRenderStateMixin implements ICreatureRenderStateExtension {

    @Unique
    private int vampirism$blood;
    @Unique
    private boolean vampirism$poisonousBlood;

    @Override
    public int vampirism$blood() {
        return this.vampirism$blood;
    }

    @Override
    public void vampirism$blood(int blood) {
        this.vampirism$blood = blood;
    }

    @Override
    public boolean vampirism$poisonousBlood() {
        return this.vampirism$poisonousBlood;
    }

    @Override
    public void vampirism$poisonousBlood(boolean poisonous) {
        this.vampirism$poisonousBlood = poisonous;
    }
}
