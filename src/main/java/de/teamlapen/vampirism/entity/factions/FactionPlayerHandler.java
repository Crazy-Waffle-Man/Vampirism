package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Preconditions;
import de.teamlapen.lib.lib.storage.Attachment;
import de.teamlapen.lib.lib.storage.UpdateParams;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.IRefinementPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.core.tags.ModTaskTags;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.player.ActionKeys;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.network.ClientboundPlaySoundEventPacket;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.ScoreboardUtil;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Extended entity property that handles factions and levels for the player
 */
public class FactionPlayerHandler extends Attachment implements IFactionPlayerHandler {
    private final static Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation SERIALIZER_ID = VResourceLocation.mod("faction_player_handler");

    public static @NotNull FactionPlayerHandler get(@NotNull Player player) {
        return player.getData(ModAttachments.FACTION_PLAYER_HANDLER.get());
    }

    /**
     * Resolves the FactionPlayerHandler capability (prints a warning message if not present) and returns an Optional of the current IFactionPlayer instance
     */
    public static <T extends IFactionPlayer<T>> @NotNull Optional<T> getCurrentFactionPlayer(@NotNull Player player) {
        return get(player).getCurrentFactionPlayer();
    }

    private final Player player;
    @NotNull
    private final Map<ActionKeys, Holder<IAction<?>>> boundActions = new HashMap<>();
    private @NotNull Holder<? extends IPlayableFaction<?>> currentFaction = ModFactions.NEUTRAL;
    private int currentLevel = 0;
    private int currentLordLevel = 0;
    @NotNull
    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;

    public FactionPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Player asEntity() {
        return player;
    }

    @Override
    public boolean canJoin(Holder<? extends IPlayableFaction<?>> faction) {
        PlayerFactionEvent.CanJoinFaction.Behavior behavior = VampirismEventFactory.fireCanJoinFactionEvent(this, currentFaction, faction);
        if (behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ONLY_WHEN_NO_FACTION) {
            return IFaction.is(currentFaction, ModFactions.NEUTRAL);
        }
        return behavior == PlayerFactionEvent.CanJoinFaction.Behavior.ALLOW;
    }

    @Override
    public boolean canLeaveFaction() {
        return currentFaction.value().getPlayerCapability(player).canLeaveFaction();
    }

    /**
     * @return action if bound
     */
    @Nullable
    public Holder<IAction<?>> getBoundAction(ActionKeys key) {
        return this.boundActions.get(key);
    }

    @Override
    public @NotNull ResourceLocation getAttachedKey() {
        return SERIALIZER_ID;
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return currentFaction;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends IFactionPlayer<T>> T factionPlayer() {
        return (T) currentFaction.value().getPlayerCapability(player);
    }

    @Override
    public <T extends IFactionPlayer<T>> Optional<T> factionPlayer(Holder<IFaction<T>> faction) {
        if (IFaction.is(currentFaction, faction)) {
            return Optional.of(factionPlayer());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends IFactionPlayer<T>> Optional<T> getCurrentFactionPlayer() {
        return Optional.of(factionPlayer());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ISkillPlayer<T>> Optional<T> getCurrentSkillPlayer() {
        return this.getCurrentFactionPlayer().filter(s -> s instanceof ISkillPlayer<?>).map(s -> (T) s);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IRefinementPlayer<T>> Optional<T> getCurrentRefinementPlayer() {
        return this.getCurrentFactionPlayer().filter(s -> s instanceof IRefinementPlayer<?>).map(s -> (T) s);
    }

    @Override
    public <T extends ISkillPlayer<T>> Optional<ISkillHandler<T>> getSkillHandler() {
        return this.<T>getCurrentSkillPlayer().map(ISkillPlayer::getSkillHandler);
    }

    @Override
    public <T extends ISkillPlayer<T>> Optional<IActionHandler<T>> getActionHandler() {
        return this.<T>getCurrentSkillPlayer().map(ISkillPlayer::getActionHandler);
    }

    @Override
    public <T extends IRefinementPlayer<T>> Optional<IRefinementHandler<T>> getRefinementHandler() {
        return this.<T>getCurrentRefinementPlayer().map(IRefinementPlayer::getRefinementHandler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ITaskPlayer<T>> Optional<T> getTaskPlayer() {
        return getCurrentFactionPlayer().filter(s -> s instanceof ITaskPlayer<?>).map(s -> (T) s);
    }

    @Override
    public Optional<ITaskManager> getTaskManager() {
        return getTaskPlayer().map(ITaskPlayer::getTaskManager);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getCurrentLevel(Holder<? extends IPlayableFaction<?>> f) {
        return isInFaction(f) ? currentLevel : 0;
    }

    @Override
    public float getCurrentLevelRelative() {
        return currentLevel / (float) currentFaction.value().getHighestReachableLevel();
    }

    @Override
    public @NotNull Optional<Holder<? extends IPlayableFaction<?>>> getLordFaction() {
        return currentLordLevel > 0 ? Optional.of(currentFaction) : Optional.of(ModFactions.NEUTRAL);
    }

    @Override
    public int getLordLevel() {
        return currentLordLevel;
    }

    @Nullable
    @Override
    public Component getLordTitle() {
        return lordTitles().map(titles -> titles.getLordTitle(currentLordLevel, titleGender)).orElse(null);
    }

    @Override
    public @Nullable Component getLordTitleShort() {
        return lordTitles().map(titles -> titles.getShort(currentLordLevel, titleGender)).orElse(null);
    }

    public @NotNull Optional<ILordTitleProvider> lordTitles() {
        return Optional.of(currentFaction).map(Holder::value).map(IPlayableFaction::lordTiles);
    }

    public int getMaxMinions() {
        return currentLordLevel * VampirismConfig.BALANCE.miMinionPerLordLevel.get();
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return this.titleGender;
    }

    @Override
    public <T extends IFaction<?>> boolean isInFaction(@Nullable Holder<T> f) {
        return IFaction.is(currentFaction, f);
    }

    @Override
    public <T extends IFaction<?>> boolean isInFaction(@Nullable TagKey<T> f) {
        return IFaction.is(currentFaction, f);
    }

    @Override
    public void joinFaction(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        if (canJoin(faction)) {
            setFaction(LevelingChange.builder().faction(faction).level(1).build());
        }
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("faction", Tag.TAG_STRING)) {
            String f = nbt.getString("faction");
            // check for backwards compatibility
            if ("null".equals(f)) {
                currentFaction = ModFactions.NEUTRAL;
                currentLevel = 0;
                currentLordLevel = 0;
            } else {
                currentFaction = getFactionFromKey(ResourceLocation.parse(f));
                currentLevel = nbt.getInt("level");
                currentLordLevel = nbt.getInt("lord_level");
            }
        }
        if (nbt.contains("title_gender", Tag.TAG_STRING)) {
            this.titleGender = IPlayableFaction.TitleGender.valueOf(nbt.getString("title_gender"));
        }
        this.loadBoundActions(nbt);
        this.factionPlayer().levelChanged(LevelingChange.builder().faction(this.currentFaction).level(this.currentLevel).lordLevel(this.currentLordLevel).build());
        updateCache();
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        if (VampirismConfig.SERVER.pvpOnlyBetweenFactions.get() && src.getEntity() instanceof Player) {
            Holder<? extends IPlayableFaction<?>> otherFaction = get((Player) src.getEntity()).getFaction();
            return !IFaction.is(this.currentFaction, otherFaction);
        }
        return true;
    }

    /**
     * Reset all lord task that should be available for players at the given lord level
     */
    public void resetLordTasks() {
        getTaskManager().ifPresent(manager -> {
            this.player.level().registryAccess().lookupOrThrow(VampirismRegistries.Keys.TASK).getTagOrEmpty(ModTaskTags.AWARDS_LORD_LEVEL).forEach(holder -> {
                holder.unwrapKey().ifPresent(manager::resetUniqueTask);
            });
        });
    }

    public void setBoundAction(ActionKeys key, @Nullable Holder<IAction<?>> boundAction, boolean sync) {
        if (boundAction == null) {
            this.boundActions.remove(key);
        } else {
            this.boundActions.put(key, boundAction);
        }
        if (sync) {
            sync();
        }
    }

    @Override
    public void sync() {
        this.sync(UpdateParams.ignoreChanged());
    }

    @SuppressWarnings("removal")
    @Override
    public boolean setFactionAndLevel(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
        return setFaction(LevelingChange.builder().faction(faction).level(level).build());
    }

    @SuppressWarnings("removal")
    @Override
    public boolean setFactionLevel(@NotNull Holder<? extends IPlayableFaction<?>> faction, int level) {
        if (IFaction.is(currentFaction, faction)) {
            return setFaction(LevelingChange.builder().faction(faction).level(level).build());
        } else {
            return false;
        }
    }

    @SuppressWarnings("removal")
    @Override
    public boolean setLordLevel(int level) {
        return setFaction(LevelingChange.maxLevel(this.currentFaction).lordLevel(level).build());
    }

    public boolean setTitleGender(boolean female) {
        var gender = female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE;
        return this.setTitleGender(gender);
    }

    public boolean setTitleGender(IPlayableFaction.TitleGender female) {
        this.titleGender = female;
        player.refreshDisplayName();
        sync(UpdateParams.all());
        return true;
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBTInternal(HolderLookup.@NotNull Provider provider, UpdateParams params) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("faction", Optional.of(this.currentFaction).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).orElseThrow());
        nbt.putInt("level", currentLevel);
        nbt.putInt("lord_level", currentLordLevel);
        nbt.putString("title_gender", titleGender.name());
        this.writeBoundActions(nbt);
        return nbt;
    }

    @Override
    public void leaveFaction(boolean die) {
        Holder<? extends IFaction<?>> oldFaction = currentFaction;
        setFaction(LevelingChange.neutral());
        player.displayClientMessage(Component.translatable("command.vampirism.base.level.successful", player.getName(), oldFaction.value().getName(), 0), true);
        if (die) {
            DamageHandler.kill((ServerLevel) this.player.level(), player, 10000);
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private @NotNull Holder<? extends IPlayableFaction<?>> getFactionFromKey(ResourceLocation key) {
        Holder<IFaction<?>> faction = ModRegistries.FACTIONS.get(key).orElse(null);
        if (faction != null && faction.value() instanceof IPlayableFaction<?>) {
            return (Holder<? extends IPlayableFaction<?>>) (Object) faction;
        }
        return ModFactions.NEUTRAL;
    }

    private void loadBoundActions(@NotNull CompoundTag nbt) {
        CompoundTag boundActions = nbt.getCompound("action_bindings");
        for (String s : boundActions.getAllKeys()) {
            try {
                ActionKeys actionKey = ActionKeys.valueOf(s);
                ModRegistries.ACTIONS.get(ResourceLocation.parse(boundActions.getString(s))).ifPresentOrElse(h -> this.boundActions.put(actionKey, h), () -> LOGGER.warn("Cannot find bound action {}", boundActions.getString(s)));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid action key {}", s);
            }
        }
    }

    @Override
    public void checkSkillTreeLocks() {
        if (this.player.level() instanceof ServerLevel level) {
            Registry<ISkillTree> registryAccess = this.player.level().registryAccess().lookupOrThrow(VampirismRegistries.Keys.SKILL_TREE);
            getSkillHandler().ifPresent(handler -> handler.updateUnlockedSkillTrees(registryAccess.listElements().filter(s -> s.value().unlockPredicate().matches(level, null, this.player)).collect(Collectors.toList())));
        }
    }

    @Override
    public boolean setFaction(LevelingChange param) {
        var oldFaction = this.currentFaction;
        var oldLevel = this.currentLevel;
        var oldLordLevel = this.currentLordLevel;
        var newFaction = param.getNewFaction(oldFaction);
        boolean changedFaction = !IFaction.is(currentFaction, newFaction);
        int newLevel = oldLevel;
        int newLordLevel = oldLordLevel;

        if (changedFaction && (!param.hasLevelChange() && !param.hasLordLevelChange())) {
            newLevel = 1;
            newLordLevel = 0;
        }

        if (param.hasLevelChange() && !param.hasLordLevelChange()) {
            newLevel = param.getNewLevel();
            if (newLevel < newFaction.value().getHighestReachableLevel()) {
                newLordLevel = 0;
            }
        }
        if (param.hasLordLevelChange()) {
            newLordLevel = param.getNewLordLevel();
            newLevel = newFaction.value().getHighestReachableLevel();
        }

        if (changedFaction) {
            if (!this.currentFaction.value().getPlayerCapability(player).canLeaveFaction()) {
                LOGGER.info("You cannot leave faction {}, it is prevented by respective mod", currentFaction.getRegisteredName());
                return false;
            }
        }

        if (VampirismEventFactory.fireChangeLevelOrFactionEvent(this, oldFaction, oldLevel, newFaction, newLevel)) {
            LOGGER.debug("Faction or Level change event canceled");
            return false;
        }

        if (changedFaction && factionPlayer() instanceof ITaskPlayer<?> taskPlayer) {
            taskPlayer.getTaskManager().reset();
        }

        if (changedFaction || newLordLevel < oldLordLevel) {
            resetLordTasks();
        }


        this.currentFaction = newFaction;
        this.currentLevel = newLevel;
        this.currentLordLevel = newLordLevel;

        param = param.copy()
                .level(this.currentLevel)
                .lordLevel(this.currentLordLevel)
                .faction(this.currentFaction).build();

        if (changedFaction) {
            oldFaction.value().getPlayerCapability(this.player).leaveFaction();
        }
        newFaction.value().getPlayerCapability(this.player).levelChanged(param);

        this.checkSkillTreeLocks();
        this.updateCache();

        ScoreboardUtil.updateScoreboard(this.player, ScoreboardUtil.FACTION_CRITERIA, this.currentFaction.value().hashCode());

        MinionWorldData.getData(this.player.level()).ifPresent(data -> {
            PlayerMinionController c = data.getController(this.player.getUUID());
            if (c != null) {
                c.setMaxMinions(this.currentFaction, this.getMaxMinions());
            }
        });

        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(ModSounds.LEVEL_UP));
            VampirismLogger.info(VampirismLogger.FACTION, param.toJson());
        }

        if (changedFaction || oldLevel != newLevel) {
            VampirismEventFactory.fireFactionLevelChangedEvent(this, oldFaction, oldLevel, currentFaction, currentLevel);
        }

        VampirismEventFactory.fireLevelChangedEvent(this, param);

        sync(changedFaction ? UpdateParams.all() : UpdateParams.ignoreChanged());
        if (player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, currentFaction, currentLevel, currentLordLevel);
        }
        return true;
    }

    private void updateCache() {
        player.refreshDisplayName();
        VampirismPlayerAttributes atts = ((IVampirismPlayer) player).getVampAtts();
        atts.hunterLevel = getCurrentLevel(ModFactions.HUNTER);
        atts.vampireLevel = getCurrentLevel(ModFactions.VAMPIRE);
        atts.lordLevel = this.currentLordLevel;
        atts.faction = this.currentFaction;
    }

    private void writeBoundActions(@NotNull CompoundTag nbt) {
        CompoundTag bounds = new CompoundTag();
        for (Map.Entry<ActionKeys, Holder<IAction<?>>> entry : this.boundActions.entrySet()) {
            entry.getValue().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).ifPresent(id -> {
                bounds.putString(entry.getKey().name(), id);
            });
        }
        nbt.put("action_bindings", bounds);
    }


    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        Optional.of(this.currentFaction).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).ifPresent(faction -> {
            nbt.putString("faction", faction);
            nbt.putInt("level", this.currentLevel);
            nbt.putInt("lord_level", this.currentLordLevel);
        });
        nbt.putString("title_gender", this.titleGender.name());

        writeBoundActions(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("faction")) {
            this.currentFaction = getFactionFromKey(ResourceLocation.parse(nbt.getString("faction")));
            this.currentLevel = Math.min(nbt.getInt("level"), this.currentFaction.value().getHighestReachableLevel());
            this.currentLordLevel = Math.min(nbt.getInt("lord_level"), this.currentFaction.value().getHighestLordLevel());
            this.currentFaction.value().getPlayerCapability(player).levelChanged(LevelingChange.builder().level(currentLevel).faction(currentFaction).lordLevel(currentLordLevel).build());
        }
        if (nbt.contains("title_gender")) {
            this.titleGender = IPlayableFaction.TitleGender.valueOf(nbt.getString("title_gender"));
        }
        loadBoundActions(nbt);
        updateCache();
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, FactionPlayerHandler> {

        @Override
        public @NotNull FactionPlayerHandler read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof Player player) {
                FactionPlayerHandler handler = new FactionPlayerHandler(player);
                handler.deserializeNBT(provider, tag);
                return handler;
            }
            throw new IllegalStateException("Cannot deserialize FactionPlayerHandler for non player entity");
        }

        @Override
        public CompoundTag write(FactionPlayerHandler attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, FactionPlayerHandler> {

        @Override
        public FactionPlayerHandler apply(IAttachmentHolder holder) {
            if (holder instanceof Player player) {
                return new FactionPlayerHandler(player);
            }
            throw new IllegalArgumentException("Cannot create faction player handler attachment for holder " + holder.getClass() + ". Expected Player");
        }
    }


}
