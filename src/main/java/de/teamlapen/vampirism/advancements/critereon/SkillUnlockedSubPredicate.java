package de.teamlapen.vampirism.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillUnlockedSubPredicate implements EntitySubPredicate {

    public static final MapCodec<SkillUnlockedSubPredicate> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    VampirismRegistries.SKILL.get().holderByNameCodec().fieldOf("skill").forGetter(SkillUnlockedSubPredicate::skill)
            ).apply(inst, SkillUnlockedSubPredicate::new));

    private final Holder<ISkill<?>> skill;

    public static SkillUnlockedSubPredicate skill(Holder<ISkill<?>> skill) {
        return new SkillUnlockedSubPredicate(skill);
    }

    private SkillUnlockedSubPredicate(Holder<ISkill<?>> skill) {
        this.skill = skill;
    }

    public Holder<ISkill<?>> skill() {
        return skill;
    }

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof Player player && ISkillHandler.get(player).map(x -> x.isSkillEnabled(skill)).orElse(false);
    }
}
