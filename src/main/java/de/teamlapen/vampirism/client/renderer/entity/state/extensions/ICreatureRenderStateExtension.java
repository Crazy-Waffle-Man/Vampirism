package de.teamlapen.vampirism.client.renderer.entity.state.extensions;

public interface ICreatureRenderStateExtension {

    int vampirism$blood();

    void vampirism$blood(int blood);

    boolean vampirism$poisonousBlood();

    void vampirism$poisonousBlood(boolean poisonous);
}
