package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.api.settings.Supporter;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.client.renderer.entity.state.IVampireWingsRenderState;
import de.teamlapen.vampirism.util.SupporterManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;


public class WingsLayer<T extends LivingEntity, S extends HumanoidRenderState, Q extends EntityModel<S>> extends RenderLayer<S, Q> {

    private static final ResourceLocation TEXTURE = VResourceLocation.mod("textures/entity/wings/wings.png");
    private static final ResourceLocation TEXTURE_DEV = VResourceLocation.mod("textures/entity/wings/wings_dev.png");
    private static final ResourceLocation TEXTURE_CONTRIBUTOR = VResourceLocation.mod("textures/entity/wings/wings_contributor.png");

    private final @NotNull WingModel<S> model;
    private final BiFunction<S, Q, ModelPart> bodyPartFunction;


    /**
     * @param bodyPartFunction Should return the main body part. The returned ModelRenderer is used to adjust the wing rotation
     */
    public WingsLayer(@NotNull RenderLayerParent<S, Q> entityRendererIn, @NotNull EntityModelSet modelSet, BiFunction<S, Q, ModelPart> bodyPartFunction) {
        super(entityRendererIn);
        this.model = new WingModel<>(modelSet.bakeLayer(ModEntitiesRender.WING));
        this.bodyPartFunction = bodyPartFunction;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull S renderState, float f1, float f2) {
        if (!renderState.isInvisible && ((IVampireWingsRenderState)renderState).vampirism$getWingsState() != IWingsEntity.WingsState.CLOSED) {
            this.model.resetPose();
            translateToBack(renderState);
            poseStack.pushPose();
            poseStack.translate(0f, 0, 0.02f);
            if (!renderState.isInvisible) {
                model.setupAnim(renderState);
                ResourceLocation resourceLocation = ((IVampireWingsRenderState) renderState).vampirism$getWingsTexture();
                if (resourceLocation == null) {
                    resourceLocation = TEXTURE;
                }
                var consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(resourceLocation));
                model.renderToBuffer(poseStack, consumer, packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0), -1);
            }
            poseStack.popPose();
        }
    }

    private void translateToBack(S renderState) {
        this.model.copyRotationFromBody(this.bodyPartFunction.apply(renderState, this.getParentModel()));
        this.model.wings.z += 2f;
        this.model.wings.y += 3f;
    }

    public static final Function<Player, ResourceLocation> textureGetter = /*UtilLib.cache(*/WingsLayer::playerTextureGetter/*)*/;

    public static ResourceLocation playerTextureGetter(Player entity) {
        if (true) {
            return TEXTURE_DEV;
        }
        String name = entity.getGameProfile().getName();
        Supporter supporter = SupporterManager.getSupporter(name);
        if (supporter != null) {
            switch (supporter.status()) {
                case "Dev":
                    return TEXTURE_DEV;
                case "Contributor":
                    return TEXTURE_CONTRIBUTOR;
            }
        }
        return TEXTURE;
    }
}