package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.core.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModSkillTreeProvider extends TagsProvider<ISkillTree> {

    protected ModSkillTreeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, VampirismRegistries.Keys.SKILL_TREE, provider, REFERENCE.MODID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModSkillTreeTags.VAMPIRE_LEVEL).add(VampireSkills.Trees.LEVEL);
        this.tag(ModSkillTreeTags.VAMPIRE_LORD).add(VampireSkills.Trees.LORD);
        this.tag(ModSkillTreeTags.VAMPIRE_DRACULA).add(VampireSkills.Trees.DRACULA);
        this.tag(ModSkillTreeTags.HUNTER_LEVEL).add(HunterSkills.Trees.LEVEL);
        this.tag(ModSkillTreeTags.HUNTER_LORD).add(HunterSkills.Trees.LORD);

        this.tag(ModSkillTreeTags.HUNTER).addTags(ModSkillTreeTags.HUNTER_LEVEL, ModSkillTreeTags.HUNTER_LORD);
        this.tag(ModSkillTreeTags.VAMPIRE).addTags(ModSkillTreeTags.VAMPIRE_LEVEL, ModSkillTreeTags.VAMPIRE_LORD, ModSkillTreeTags.VAMPIRE_DRACULA);
        this.tag(ModSkillTreeTags.LEVEL).add(HunterSkills.Trees.LEVEL, VampireSkills.Trees.LEVEL);
        this.tag(ModSkillTreeTags.LORD).add(HunterSkills.Trees.LORD, VampireSkills.Trees.LORD);
        this.tag(ModSkillTreeTags.DRACULA).add(VampireSkills.Trees.DRACULA);
        this.tag(ModSkillTreeTags.DEFAULT).addTag(ModSkillTreeTags.LEVEL).addTag(ModSkillTreeTags.LORD);


    }
}
