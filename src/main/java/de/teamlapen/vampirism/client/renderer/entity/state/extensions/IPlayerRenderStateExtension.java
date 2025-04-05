package de.teamlapen.vampirism.client.renderer.entity.state.extensions;

import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;

public interface IPlayerRenderStateExtension {

    VampirismPlayerAttributes vampirism$attributes();

    void vampirism$attributes(VampirismPlayerAttributes attributes);

}
