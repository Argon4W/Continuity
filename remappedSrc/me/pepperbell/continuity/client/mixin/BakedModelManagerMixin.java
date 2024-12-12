package me.pepperbell.continuity.client.mixin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import me.pepperbell.continuity.client.resource.BakedModelManagerReloadExtension;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

@Mixin(ModelManager.class)
abstract class BakedModelManagerMixin {
	@Unique
	@Nullable
	private volatile BakedModelManagerReloadExtension continuity$reloadExtension;

	@Inject(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
	private void continuity$onHeadReload(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager resourceManager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		continuity$reloadExtension = new BakedModelManagerReloadExtension(resourceManager, prepareExecutor);

		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.setContext();
		}
	}

	@Inject(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
	private void continuity$onReturnReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.clearContext();
		}
	}

	@ModifyReturnValue(method = "reload(Lnet/minecraft/resource/ResourceReloader$Synchronizer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/util/profiler/Profiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
	private CompletableFuture<Void> continuity$modifyReturnReload(CompletableFuture<Void> original) {
		return original.thenRun(() -> continuity$reloadExtension = null);
	}

	@Inject(method = "bake(Lnet/minecraft/util/profiler/Profiler;Ljava/util/Map;Lnet/minecraft/client/render/model/ModelLoader;)Lnet/minecraft/client/render/model/BakedModelManager$BakingResult;", at = @At("HEAD"))
	private void continuity$onHeadBake(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> preparations, ModelBakery modelLoader, CallbackInfoReturnable<?> cir) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.beforeBaking(preparations, modelLoader);
		}
	}

	@Inject(method = "upload(Lnet/minecraft/client/render/model/BakedModelManager$BakingResult;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
	private void continuity$onReturnUpload(CallbackInfo ci) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.apply();
		}
	}
}
