package de.teamlapen.vampirism.world.gen.structure.underworld_portal;

import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class UnderWorldPortalPieces {

    public static final ResourceKey<StructureTemplatePool> START = ModStructures.createTemplatePool("underworld_portal");

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> holderGetter = context.lookup(Registries.TEMPLATE_POOL);

    }
}
