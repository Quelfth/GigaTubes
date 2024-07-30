package net.quelfth.gigatubes.block.render;

import java.util.List;

import javax.annotation.Nonnull;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import net.quelfth.gigatubes.LazyCache;
import net.quelfth.gigatubes.GigaModels.Model;
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;
import net.quelfth.gigatubes.block.tube_parts.FilterModule;
import net.quelfth.gigatubes.block.tube_parts.TubeModule;

public class TubeRenderer implements BlockEntityRenderer<TubeBlockEntity> {

    

    protected final LazyCache<BakedModel> intakeCache;
    protected final LazyCache<BakedModel> outputCache;
    protected final LazyCache<BakedModel> ioCache;
    protected final LazyCache<BakedModel> filterCache;

    public TubeRenderer(BlockEntityRendererProvider.Context context) {
        intakeCache = Model.TUBE_INTAKE.cache;
        outputCache = Model.TUBE_OUTPUT.cache;
        ioCache = Model.TUBE_IO.cache;
        filterCache = Model.TUBE_FILTER.cache;
    }

    
    @Override
    public void render(final TubeBlockEntity tube, final float partialTick, final PoseStack pose, final MultiBufferSource buffers, final int light, final int overlay) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null)
            return;
        final Level level = minecraft.level;
        if (level == null)
            return;
        final @Nonnull RandomSource random = level.random;
        final @Nonnull List<BakedQuad> intake = getQuads(intakeCache, random);
        final @Nonnull List<BakedQuad> output = getQuads(outputCache, random);
        final @Nonnull List<BakedQuad> io = getQuads(ioCache, random);
        final @Nonnull List<BakedQuad> filter = getQuads(filterCache, random);

        final @Nonnull VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        for (final Direction dir : Direction.values()) {
            if (tube.getIO(dir, true)) {
                if (tube.getIO(dir, false)) {
                    renderIO(dir, pose, buffer, io, light, overlay);
                } else 
                     renderIO(dir, pose, buffer, intake, light, overlay);

            } else if (tube.getIO(dir, false)) {
                renderIO(dir, pose, buffer, output, light, overlay);
            }

            final TubeModule module = tube.getModule(dir);
            if (module instanceof FilterModule) {
                renderModule(dir, pose, buffer, filter, light, overlay, tube.anyIO(dir) ? 5 : 6);
            }
        }
    }

    @SuppressWarnings("null")
    private static List<BakedQuad> getQuads(final LazyCache<BakedModel> cache, final RandomSource random) {
        return cache.get().getQuads(null, null, random, ModelData.EMPTY, RenderType.cutout());
    }

    // private int getLightLevel(Level level, BlockPos pos) {
    //     int b = level.getBrightness(LightLayer.BLOCK, pos);
    //     int s = level.getBrightness(LightLayer.SKY, pos);
    //     return LightTexture.pack(b, s);
    // }

    private void renderIO(final Direction dir, final PoseStack pose, final VertexConsumer buffer, final List<BakedQuad> quads, final int light, final int overlay) {
        pose.pushPose();
        pose.translate(dir.getStepX() * 0.001, dir.getStepY() * 0.001, dir.getStepZ() * 0.001);
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(getIORotation(dir));
        pose.translate(-0.5, -0.5, -0.5);

        for (final BakedQuad quad : quads) {
            buffer.putBulkData(pose.last(), quad, 1.f, 1.f, 1.f, light, overlay);
        }

        pose.popPose();
    }

    private void renderModule(final Direction dir, final PoseStack pose, final VertexConsumer buffer, final List<BakedQuad> quads, final int light, final int overlay, final int distance) {
        pose.pushPose();
        pose.scale(1.001f, 1.001f, 1.001f);
        final double d = distance / 16. + 0.03125;
        pose.translate(dir.getStepX() * d, dir.getStepY() * d, dir.getStepZ() * d);
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(getModuleRotation(dir));
        pose.translate(-0.5, -0.5, -0.5);

        for (final BakedQuad quad : quads) {
            buffer.putBulkData(pose.last(), quad, 1.f, 1.f, 1.f, light, overlay);
        }

        pose.popPose();
    }

    private static Quaternionf getModuleRotation(final Direction dir) {
        Quaternionf q = new Quaternionf();
        switch (dir) {
            case SOUTH -> { }
            case NORTH -> {
                q.mul(Axis.XP.rotationDegrees(180.f));
            }
            case UP -> {
                q.mul(Axis.XP.rotationDegrees(90.f));
            }
            case DOWN -> {
                q.mul(Axis.XP.rotationDegrees(270.f));
            }
            case EAST -> {
                q.mul(Axis.YP.rotationDegrees(270.f));
            }
            case WEST -> {
                q.mul(Axis.YP.rotationDegrees(90.f));
            }
        }
        return q;
    }

    private static Quaternionf getIORotation(final Direction dir) {
        Quaternionf q = new Quaternionf();
        switch (dir) {
            case DOWN -> { }
            case UP -> {
                q.mul(Axis.XP.rotationDegrees(180.f));
            }
            case SOUTH -> {
                q.mul(Axis.YP.rotationDegrees(180.f));
                q.mul(Axis.XP.rotationDegrees(90.f));
            }
            case NORTH -> {
                q.mul(Axis.XP.rotationDegrees(90.f));
            }
            case EAST -> {
                q.mul(Axis.YP.rotationDegrees(270.f));
                q.mul(Axis.XP.rotationDegrees(90.f));
            }
            case WEST -> {
                q.mul(Axis.YP.rotationDegrees(90.f));
                q.mul(Axis.XP.rotationDegrees(90.f));
            }
        }
        return q;
    }
    
}
