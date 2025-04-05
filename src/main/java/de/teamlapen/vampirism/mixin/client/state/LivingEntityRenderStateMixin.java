package de.teamlapen.vampirism.mixin.client.state;

import de.teamlapen.vampirism.client.renderer.entity.state.extensions.IFactionRenderStateExtension;
import de.teamlapen.vampirism.client.renderer.entity.state.extensions.ILivingEntityRenderStateExtension;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements ILivingEntityRenderStateExtension {


    @Unique
    private boolean vampirism$sleepingInCoffin;
    @Unique
    private float vampirism$attackTime;
    @Unique
    private HumanoidArm vampirism$attackArm = HumanoidArm.RIGHT;

    @Override
    public boolean vampirism$sleepingInCoffin() {
        return this.vampirism$sleepingInCoffin;
    }

    @Override
    public void vampirism$sleepingInCoffin(boolean sleepingInCoffin) {
        this.vampirism$sleepingInCoffin = sleepingInCoffin;
    }

    @Override
    public HumanoidArm vampirism$attackArm() {
        return this.vampirism$attackArm;
    }

    @Override
    public void vampirism$attackArm(HumanoidArm arm) {
        this.vampirism$attackArm = arm;
    }

    @Override
    public float vampirism$attackTime() {
        return this.vampirism$attackTime;
    }

    @Override
    public void vampirism$attackTime(float attackTime) {
        this.vampirism$attackTime = attackTime;
    }
}
