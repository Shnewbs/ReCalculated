package sonar.core.client.renderers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.systems.RenderSystem;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import sonar.core.common.block.properties.IBlockRotated;
import sonar.core.helpers.RenderHelper;

@EventBusSubscriber
public class BlockRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> implements IModel {

	private final ISonarCustomRenderer renderer;

	public BlockRenderer(ISonarCustomRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return renderer.getAllTextures();
	}

	@Override
	public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedBlockModel<>(format, renderer, bakedTextureGetter, true);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Level level = blockEntity.getLevel();
		BlockPos pos = blockEntity.getBlockPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		BlockState extendedState = block.getStateForPlacement(state, level, pos);

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bind(TextureMap.LOCATION_BLOCKS_TEXTURE);

		RenderSystem.pushMatrix();
		RenderSystem.translate(poseStack.last().pose());

		VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.solid());
		renderer.renderWorldBlock(vertexBuilder, level, pos, partialTicks, state, block, blockEntity);

		RenderSystem.popMatrix();
	}

	public static class BakedBlockModel<T extends BlockEntity> implements IBakedModel {

		private final VertexFormat format;
		private final ISonarCustomRenderer renderer;
		private final Function<ResourceLocation, TextureAtlasSprite> textures;

		public BakedBlockModel(VertexFormat format, ISonarCustomRenderer renderer, Function<ResourceLocation, TextureAtlasSprite> textures, boolean inventory) {
			this.format = format;
			this.renderer = renderer;
			this.textures = textures;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
			List<BakedQuad> list;
			if (side == null && renderer instanceof ISonarModelRenderer) {
				ModelTechne model = ((ISonarModelRenderer) renderer).getModel();
				TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(renderer.getAllTextures().get(0));
				list = model.getBakedQuads(format, sprite, 1.0F);
				Direction face = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
				if (face == Direction.UP) {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(-90, 1, 0, 0, RenderHelper.getOffsetForFace(face)));
				} else if (face == Direction.DOWN) {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(90, 1, 0, 0, RenderHelper.getOffsetForFace(face)));
				} else {
					list = RenderHelper.transformQuads(list, new TransformationMatrix(-face.toYRot(), 0, 1, 0, RenderHelper.getOffsetForFace(face)));
				}
			} else {
				list = ImmutableList.of();
			}
			return list;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean usesBlockLight() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return renderer.getIcon();
		}

		@Override
		public ItemTransforms getTransforms() {
			return ItemTransforms.NO_TRANSFORMS;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}
}
