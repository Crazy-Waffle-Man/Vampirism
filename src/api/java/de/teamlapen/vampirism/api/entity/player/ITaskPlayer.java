package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ITaskPlayer<T extends IFactionPlayer<T> & ITaskPlayer<T>> extends IFactionPlayer<T> {

    static <T extends ITaskPlayer<T>> Optional<T> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getTaskPlayer();
    }

    /**
     * null on client & @NotNull on server
     */
    @NotNull
    ITaskManager getTaskManager();
}
