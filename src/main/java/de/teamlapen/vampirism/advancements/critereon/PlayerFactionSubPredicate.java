package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.FactionCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PlayerFactionSubPredicate implements EntitySubPredicate {

    public static final MapCodec<PlayerFactionSubPredicate> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    FactionCodec.playable().optionalFieldOf("faction", null).forGetter(p -> p.faction),
                    Codec.INT.optionalFieldOf("level").forGetter(p -> p.level),
                    Codec.INT.optionalFieldOf("lord_level").forGetter(p -> p.lordLevel),
                    Codec.BOOL.optionalFieldOf("dracula").forGetter(p -> p.dracula)
            ).apply(inst, PlayerFactionSubPredicate::new)
    );

    @Nullable
    private final Holder<? extends IPlayableFaction<?>> faction;
    @NotNull
    private final Optional<Integer> level;
    @NotNull
    private final Optional<Integer> lordLevel;
    private final Optional<Boolean> dracula;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private PlayerFactionSubPredicate(@Nullable Holder<? extends IPlayableFaction<?>> faction, @NotNull Optional<Integer> level, @NotNull Optional<Integer> lordLevel, Optional<Boolean> dracula) {
        this.faction = faction;
        this.level = level;
        this.lordLevel = lordLevel;
        this.dracula = dracula;
    }

    private PlayerFactionSubPredicate(@Nullable Holder<? extends IPlayableFaction<?>> faction, @NotNull Optional<Integer> level, @NotNull Optional<Integer> lordLevel) {
        this(faction, level, lordLevel, Optional.empty());
    }

    public static PlayerFactionSubPredicate faction(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        return new PlayerFactionSubPredicate(faction, Optional.empty(), Optional.empty());
    }

    public static PlayerFactionSubPredicate dracula() {
        return new PlayerFactionSubPredicate(ModFactions.VAMPIRE, Optional.empty(), Optional.empty(), Optional.of(true));
    }

    public static PlayerFactionSubPredicate level(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
        return new PlayerFactionSubPredicate(faction, Optional.of(level), Optional.empty());
    }

    public static PlayerFactionSubPredicate lord(@NotNull Holder<? extends IPlayableFaction<?>> faction, int lordLevel) {
        return new PlayerFactionSubPredicate(faction, Optional.empty(), Optional.of(lordLevel));
    }

    public static PlayerFactionSubPredicate lord(int lordLevel) {
        return new PlayerFactionSubPredicate(null, Optional.empty(), Optional.of(lordLevel));
    }

    public static PlayerFactionSubPredicate lord(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        return new PlayerFactionSubPredicate(faction, Optional.empty(), Optional.of(1));
    }

    public static PlayerFactionSubPredicate level(int level) {
        return new PlayerFactionSubPredicate(null, Optional.of(level), Optional.empty());
    }

    @Override
    public boolean matches(@NotNull Entity pEntity, @NotNull ServerLevel pLevel, @Nullable Vec3 p_218830_) {
        if (pEntity instanceof Player player) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            var allowed = (faction == null || IFaction.is(fph.getFaction(), faction))
                    && (level.isEmpty() || fph.getCurrentLevel() >= level.get())
                    && (lordLevel.isEmpty() || fph.getLordLevel() >= lordLevel.get());
            allowed = allowed &&
                    (!dracula.orElse(false) || IDraculaPlayer.getDracula(player).map(IDraculaPlayer::isLord).orElse(false));
            return allowed;
        }
        return false;
    }

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }
}
