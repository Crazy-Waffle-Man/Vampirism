package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AndSubPredicate implements EntitySubPredicate {

    public static MapCodec<AndSubPredicate> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            EntitySubPredicate.CODEC.listOf().fieldOf("sub_predicates").forGetter(AndSubPredicate::subPredicateList)
    ).apply(inst, AndSubPredicate::new));

    private final List<EntitySubPredicate> subPredicateList;

    private AndSubPredicate(List<EntitySubPredicate> subPredicateList) {
        this.subPredicateList = subPredicateList;
    }
    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    private List<EntitySubPredicate> subPredicateList() {
        return this.subPredicateList;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        return subPredicateList.stream().allMatch(p -> p.matches(entity, level, position));
    }

    public static AndSubPredicate and(EntitySubPredicate... subPredicates) {
        return new AndSubPredicate(List.of(subPredicates));
    }

    public static AndSubPredicate and(List<EntitySubPredicate> subPredicates) {
        return new AndSubPredicate(subPredicates);
    }
}
