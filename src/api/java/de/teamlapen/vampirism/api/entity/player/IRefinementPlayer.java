package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IRefinementPlayer<T extends IFactionPlayer<T> & IRefinementPlayer<T>> extends IFactionPlayer<T> {

    static <T extends IRefinementPlayer<T>> Optional<T> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getCurrentRefinementPlayer();
    }

    IRefinementHandler<T> getRefinementHandler();
}
