package de.teamlapen.vampirism.client.renderer.entity.state.extensions;

import net.minecraft.world.entity.HumanoidArm;

public interface ILivingEntityRenderStateExtension {

    boolean vampirism$sleepingInCoffin();

    void vampirism$sleepingInCoffin(boolean sleepingInCoffin);

    HumanoidArm vampirism$attackArm();

    void vampirism$attackArm(HumanoidArm arm);

    float vampirism$attackTime();

    void vampirism$attackTime(float attackTime);
}
