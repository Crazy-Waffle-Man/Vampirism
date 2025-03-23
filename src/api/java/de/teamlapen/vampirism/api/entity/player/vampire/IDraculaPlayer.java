package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.annotations.FloatRange;
import de.teamlapen.vampirism.api.extensions.IPlayer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IDraculaPlayer extends IPlayer, IWingsEntity {

    @SuppressWarnings("unchecked")
    static Optional<IDraculaPlayer> getDracula(Player player) {
        return (Optional<IDraculaPlayer>) (Object) Optional.of(VampirismAPI.vampirePlayer(player)).filter(IDraculaPlayer::isLord);
    }

    boolean isLord();

    void awardTitle();

}
