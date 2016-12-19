package elec332.core.client.model.loading.handler;

/**
 * Created by Elec332 on 19-9-2016.
 */
//@ModelHandler
public class MultiPartModelHandler{}/* implements IModelHandler {

    public MultiPartModelHandler(){
        multipartModelHandlers = Lists.newArrayList();
        multipartResourceLocations = Maps.newHashMap();
    }

    private List<IMultipartModelHandler> multipartModelHandlers;
    private Map<Pair<IBlockState, IMultipart>, ModelResourceLocation> multipartResourceLocations;

    @Override
    public boolean enabled() {
        return Loader.isModLoaded("mcmultipart");
    }

    @Override
    public void getModelHandlers(List<?> list) {
        for (Object o : list) {
            if (o instanceof IMultipartModelHandler){
                multipartModelHandlers.add((IMultipartModelHandler) o);
            }
        }
    }

    @Override
    public void registerModels() {
        ElecCore.logger.info("Prehandling MultiPart Models");
        for (final ResourceLocation part : MultipartRegistry.getRegisteredParts()) {
            for (final IMultipartModelHandler handler : multipartModelHandlers) {
                final IMultipart mPart = MultipartRegistry.createPart(part, new NBTTagCompound());
                if (handler.handlePart(mPart)) {
                    MultipartRegistryClient.registerSpecialPartStateMapper(part, new StateMapperBase() {

                        @Override
                        @Nonnull
                        public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
                            for (IBlockState iblockstate : MultipartRegistry.getDefaultState(part).getValidStates()) {
                                this.mapStateModelLocations.put(iblockstate, this.getModelResourceLocation(iblockstate));
                            }
                            return this.mapStateModelLocations;
                        }

                        @Override
                        @Nonnull
                        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
                            ModelResourceLocation mrl = new ModelResourceLocation(part, handler.getIdentifier(state, mPart));
                            multipartResourceLocations.put(Pair.of(state, mPart), mrl);
                            return mrl;
                        }

                    });
                }
            }
        }
    }

    @Override
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> modelGetter) {
        Map<ModelResourceLocation, IBakedModel> ret = Maps.newHashMap();
        for (Map.Entry<Pair<IBlockState, IMultipart>, ModelResourceLocation> entry : multipartResourceLocations.entrySet()) {
            ModelResourceLocation mrl = entry.getValue();
            for (IMultipartModelHandler handler : multipartModelHandlers) {
                Pair<IBlockState, IMultipart> pair = entry.getKey();
                IMultipart multipart = pair.getRight();
                if (handler.handlePart(multipart)) {
                    ret.put(mrl, handler.getModelFor(multipart, pair.getLeft(), mrl.getVariant(), mrl));
                    break;
                }
            }
        }
        return ret;
    }

} */