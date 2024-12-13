package me.pepperbell.continuity.client;

import com.google.common.collect.ImmutableSet;
import me.pepperbell.continuity.client.mixinterface.ModelLoaderExtension;
import me.pepperbell.continuity.client.resource.CustomBlockLayers;
import me.pepperbell.continuity.client.resource.ModelWrappingHandler;
import me.pepperbell.continuity.client.util.RenderUtil;
import me.pepperbell.continuity.impl.client.ProcessingDataKeyRegistryImpl;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = ContinuityClient.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ContinuityClientModEvents {

    //Replacement for ProcessingDataKeyRegistryImpl.INSTANCE.init(), use FMLClientSetupEvent to freeze data key registry. Remove redundant fabric-lifecycle-events-v1 dependency.
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ProcessingDataKeyRegistryImpl.INSTANCE.setFrozen();
    }

    //Replacement for ResourceManagerHelper.registerBuiltinResourcePack, use AddPackFindersEvent to add builtin resource packs. Partially remove fabric-loader dependency. (fabric-renderer-indigo still bounds to fabric-loader)
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(ContinuityClient.asId("resourcepacks/default"), PackType.CLIENT_RESOURCES, Component.translatable("resourcePack.continuity.default.name"), PackSource.BUILT_IN, false, Pack.Position.TOP);
        event.addPackFinders(ContinuityClient.asId("resourcepacks/glass_pane_culling_fix"), PackType.CLIENT_RESOURCES, Component.translatable("resourcePack.continuity.glass_pane_culling_fix.name"), PackSource.BUILT_IN, false, Pack.Position.TOP);
    }

    //Replacement for RenderUtil.ReloadListener.init() and CustomBlockLayers.ReloadListener.init(), use RegisterClientReloadListenersEvent to register client resource reload listeners. Remove redundant fabric-resource-loader-v0 dependency.
    @SubscribeEvent
    public static void onRegisterResourceReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(RenderUtil.ReloadListener.INSTANCE);
        event.registerReloadListener(CustomBlockLayers.ReloadListener.INSTANCE);
    }

    //Replacement for ModelWrappingHandler.init(), use ModelEvent.ModifyBakingResult to wrap baked models. Remove redundant fabric-model-loading-api-v1 dependency.
    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ModelWrappingHandler wrappingHandler = ((ModelLoaderExtension) event.getModelBakery()).continuity$getModelWrappingHandler();

        if (wrappingHandler == null) {
            return;
        }

        Map<ModelResourceLocation, BakedModel> bakedModels = event.getModels();
        Set<ModelResourceLocation> keys = ImmutableSet.copyOf(event.getModels().keySet());

        for (ModelResourceLocation modelResourceLocation : keys) {
            bakedModels.put(modelResourceLocation, wrappingHandler.wrap(bakedModels.get(modelResourceLocation), null, modelResourceLocation));
        }
    }
}
