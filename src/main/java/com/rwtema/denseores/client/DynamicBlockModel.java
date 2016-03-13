package com.rwtema.denseores.client;

import com.rwtema.denseores.DenseOre;
import com.rwtema.denseores.modelbuilder.ModelBuilder;
import com.rwtema.denseores.blockaccess.BlockAccessSingleOverride;
import com.rwtema.denseores.blockstates.DenseOreBlockState;
import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public class DynamicBlockModel extends EmptyBakedModel implements ISmartBlockModel, ISmartItemModel {
    public final DenseOreBlockState denseBlockState;
    private final DenseOre denseOre;
    private final OreType type;
    public static BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    public static ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();


    public DynamicBlockModel(DenseOre denseOre, IBlockState denseBlockState, OreType type) {
        this.denseOre = denseOre;
        this.type = type;

        this.denseBlockState = ((DenseOreBlockState) denseBlockState);
        this.denseBlockState.model = this;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return denseOre.sprites.get(type);
    }

    ThreadLocal<IBakedModel> result = new ThreadLocal<IBakedModel>(){
        @Override
        protected IBakedModel initialValue() {
            return new EmptyBakedModel();
        }
    };

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return result.get();
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        IBakedModel itemModel = itemModelMesher.getItemModel(denseOre.newStack(1));
        IBakedModel iBakedModel = ModelBuilder.changeIcon(itemModel, denseOre.sprites.get(type));
        return iBakedModel;
    }

    public void handleState(IBlockState baseBlockState, IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBakedModel model = dispatcher.getModelFromBlockState(baseBlockState, new BlockAccessSingleOverride(worldIn, baseBlockState, pos), pos);
        result.set(ModelBuilder.changeIcon(model, denseOre.sprites.get(type)));
    }
}
