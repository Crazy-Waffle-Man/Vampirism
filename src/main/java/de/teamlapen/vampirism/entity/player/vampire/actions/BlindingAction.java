package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BlindingAction extends DefaultVampireAction {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected IActionResult activate(IVampirePlayer player, ActivationContext context) {
        //noinspection unchecked
        List<LivingEntity> entities =  (List<LivingEntity>) (Object) player.level().getEntities(player.asEntity(), player.asEntity().getBoundingBox().inflate(10), EntitySelector.NO_SPECTATORS.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).and(x -> !Helper.isVampire(x)));
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 3));
        }
        return IActionResult.SUCCESS;
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        return 20*60;
    }
}
