package de.teamlapen.vampirism.entity.player.skills;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.advancements.critereon.AndSubPredicate;
import de.teamlapen.vampirism.advancements.critereon.FactionSubPredicate;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.util.FactionCodec;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name, @NotNull Optional<ResourceLocation> background) implements ISkillTree {

    public static final Codec<ISkillTree> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
            inst.group(
                    FactionCodec.playable().fieldOf("faction").forGetter(ISkillTree::faction),
                    EntityPredicate.CODEC.fieldOf("unlock_predicate").forGetter(ISkillTree::unlockPredicate),
                    ItemStack.CODEC.fieldOf("display").forGetter(ISkillTree::display),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(ISkillTree::name),
                    ResourceLocation.CODEC.optionalFieldOf("background").forGetter(ISkillTree::background)
            ).apply(inst, SkillTree::new)
    ));

    public SkillTree(@NotNull Holder<? extends IPlayableFaction<?>> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name) {
        this(faction, unlockPredicate, display, name, Optional.empty());
    }

    public static Builder create(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        return new Builder(faction).addPredicate(FactionSubPredicate.faction(faction));
    }

    public static class Builder {

        private final Holder<? extends IPlayableFaction<?>> faction;
        private @Nullable EntityPredicate unlockPredicate;
        private final @NotNull List<EntitySubPredicate> subPredicates = new ArrayList<>();
        private @NotNull ItemStack display = ItemStack.EMPTY;
        private @NotNull Component name = Component.empty();
        private @Nullable ResourceLocation background;

        private Builder(Holder<? extends IPlayableFaction<?>> faction) {
            this.faction = faction;
        }

        public static Builder create(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
            return new Builder(faction).addPredicate(FactionSubPredicate.faction(faction));
        }

        public static Builder createEmpty(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
            return new Builder(faction);
        }

        public Builder baseUnlockPredicate(@NotNull EntityPredicate unlockPredicate) {
            this.unlockPredicate = unlockPredicate;
            return this;
        }

        public Builder addPredicate(@NotNull EntitySubPredicate predicate) {
            this.subPredicates.add(predicate);
            return this;
        }

        public Builder display(@NotNull ItemStack display) {
            this.display = display;
            return this;
        }

        public Builder display(@NotNull ItemLike display) {
            this.display = display.asItem().getDefaultInstance();
            return this;
        }

        public Builder name(@NotNull Component name) {
            this.name = name;
            return this;
        }

        public Builder background(@NotNull ResourceLocation background) {
            this.background = background;
            return this;
        }

        public SkillTree build() {
            Preconditions.checkArgument(!this.display.isEmpty());
            Preconditions.checkArgument(!this.name.getString().isEmpty());
            Preconditions.checkArgument(this.unlockPredicate == null || this.subPredicates.isEmpty(), "Cannot have both a base unlock predicate and sub predicates");
            Preconditions.checkArgument(this.unlockPredicate != null || !this.subPredicates.isEmpty(), "Cannot have no unlock predicate and no sub predicates");
            if (this.unlockPredicate == null) {
                this.unlockPredicate = EntityPredicate.Builder.entity().subPredicate(AndSubPredicate.and(this.subPredicates)).build();
            }
            return new SkillTree(this.faction, this.unlockPredicate, this.display, this.name, Optional.ofNullable(this.background));
        }
    }

}
