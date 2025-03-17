package de.teamlapen.vampirism.api.entity.player.vampire;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IVampireVisionUser {

    /**
     * Force enables the vision
     * Does NOT unlock the vision
     *
     * @param vision Null to disable all
     */
    void activateVision(@Nullable IVampireVision vision);

    /**
     * @return The currently active vision. May be null
     */
    @Nullable
    IVampireVision getActiveVision();

    /**
     * Locks the vision again, preventing the player from using it
     */
    void unUnlockVision(@NotNull IVampireVision vision);

    /**
     * Unlocks the given vision, so the player can activate it.
     * Is not saved to nbt
     */
    void unlockVision(@NotNull IVampireVision vision);
}
