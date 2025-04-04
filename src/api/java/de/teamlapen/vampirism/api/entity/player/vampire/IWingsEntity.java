package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.extensions.IEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public interface IWingsEntity extends IEntity {

    boolean wingsFunctionalOpen();

    boolean wingsVisualOpen();

    boolean showWings();

    void hideWings();

    void swingWings();

    AnimationState flyAnimation();

    AnimationState growAnimation();

    WingsState getWingsState();

    float GROW_SPEED = 0.5f;
    float GROW_SECONDS = 1f;
    float GROW_TICKS = 20 * GROW_SECONDS / GROW_SPEED;

    enum WingsState {
        CLOSED, OPENING, OPEN, FLYING, CLOSING;
    }
}
