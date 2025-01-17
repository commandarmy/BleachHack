/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import java.util.Random;

import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventRenderBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Blocks are still tesselated even if they're transparent because Minecraft's
 * rendering engine is retarded.
 */
@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {

	@Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
	private void renderBlock_head(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfoReturnable<Boolean> callback) {
		EventRenderBlock.Tesselate event = new EventRenderBlock.Tesselate(state, pos, matrices, vertexConsumer);
		BleachHack.eventBus.post(event);

		if (event.isCancelled())
			callback.cancel();
	}

	@Inject(method = "renderBlock", at = @At("RETURN"))
	private void renderBlock_return(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfoReturnable<Boolean> ci) {
		vertexConsumer.unfixColor();
	}
}
