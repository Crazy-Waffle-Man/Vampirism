package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ISkillPlayer<T extends ISkillPlayer<T>> extends IFactionPlayer<T> {

    static <T extends ISkillPlayer<T>> Optional<T> get(Player player) {
        return VampirismAPI.factionPlayerHandler(player).getCurrentSkillPlayer();
    }

    /**
     * @return The skill handler for this player
     */
    @NotNull
    ISkillHandler<T> getSkillHandler();

    @NotNull
    IActionHandler<T> getActionHandler();
}
