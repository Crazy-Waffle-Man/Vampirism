package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.teamlapen.vampirism.api.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.WingModel;
import de.teamlapen.vampirism.client.renderer.entity.VampireBaronRenderer;
import de.teamlapen.vampirism.client.renderer.entity.state.IDraculaPlayerRenderState;
import de.teamlapen.vampirism.client.renderer.entity.state.IVampireWingsRenderState;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class WingsLayer<T extends LivingEntity, S extends HumanoidRenderState, Q extends EntityModel<S>> extends RenderLayer<S, Q> {

    private static final ResourceLocation TEXTURE = VResourceLocation.mod("textures/entity/wings/wings.png");
    private final @NotNull WingModel<S> model;
    private final BiFunction<S, Q, ModelPart> bodyPartFunction;
    private final Supplier<ResourceLocation> texture;

    public WingsLayer(@NotNull RenderLayerParent<S, Q> entityRendererIn, @NotNull EntityModelSet modelSet, BiFunction<S, Q, ModelPart> bodyPartFunction) {
        this(entityRendererIn, modelSet, bodyPartFunction, () -> TEXTURE);
    }

    /**
     * @param bodyPartFunction Should return the main body part. The returned ModelRenderer is used to adjust the wing rotation
     */
    public WingsLayer(@NotNull RenderLayerParent<S, Q> entityRendererIn, @NotNull EntityModelSet modelSet, BiFunction<S, Q, ModelPart> bodyPartFunction, Supplier<ResourceLocation> location) {
        super(entityRendererIn);
        this.model = new WingModel<>(modelSet.bakeLayer(ModEntitiesRender.WING));
        this.bodyPartFunction = bodyPartFunction;
        this.texture = location;
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
                var consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.texture.get()));
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

}