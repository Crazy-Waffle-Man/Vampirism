package de.teamlapen.vampirism.client.renderer.entity.state.extensions;

import de.teamlapen.vampirism.client.renderer.entity.state.IVampireWingsRenderState;
import net.minecraft.world.entity.ambient.Bat;

public interface IVampirePlayerRenderStateExtension extends IVampireWingsRenderState {

    Bat vampirism$bat();

    void vampirism$bat(Bat bat);
}
