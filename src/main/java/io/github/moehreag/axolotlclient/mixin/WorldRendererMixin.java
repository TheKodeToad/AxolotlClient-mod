package io.github.moehreag.axolotlclient.mixin;


import com.mojang.blaze3d.platform.GlStateManager;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Final private static Identifier SUN;

    @Shadow @Final private static Identifier MOON_PHASES;

    @Shadow private boolean field_10817;

    @Shadow private AdvancedVertexBuffer starsBuffer;

    @Shadow private int field_1923;

    @Shadow private ClientWorld world;

    @Inject(method = "method_9891", at=@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;disableTexture()V"), cancellable = true)
    public void sky(float f, int ih, CallbackInfo ci){
        if(Axolotlclient.features && Axolotlclient.CONFIG.General.customSky){

            GlStateManager.disableFog();
            GlStateManager.depthMask(false);
            GlStateManager.enableTexture();


            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            SkyboxManager.getInstance().renderSkyboxes();


            GlStateManager.pushMatrix();
            float n = 1.0F - this.world.getRainGradient(f);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, n);
            GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(this.world.getSkyAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);


            if(!Axolotlclient.CONFIG.Cursed.rotateWorld)GlStateManager.popMatrix();
            GlStateManager.disableTexture();
            Vec3d vec3d = this.world.method_3631(MinecraftClient.getInstance().getCameraEntity(), f);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float j = (float)vec3d.z;


            GlStateManager.color3f(g, h, j);
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color3f(g, h, j);

            GlStateManager.disableFog();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GuiLighting.disable();
            float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(f), f);
            if (fs != null) {
                GlStateManager.disableTexture();
                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(MathHelper.sin(this.world.getSkyAngleRadians(f)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
                float o = fs[1];
                float p = fs[2];
                n = 1.0F - this.world.getRainGradient(f);

                bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(0.0, 100.0, 0.0).color(n, o, p, fs[3]).next();

                for(int u = 0; u <= 16; ++u) {
                    float s = (float)u * (float) Math.PI * 2.0F / 16.0F;
                    float v = MathHelper.sin(s);
                    float w = MathHelper.cos(s);
                    bufferBuilder.vertex(v * 120.0F, w * 120.0F, -w * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }

            GlStateManager.enableTexture();
            GlStateManager.blendFuncSeparate(770, 1, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, n);
            GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(this.world.getSkyAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);
            float o = 30.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(SUN);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex((-o), 100.0, (-o)).texture(0.0, 0.0).next();
            bufferBuilder.vertex(o, 100.0, (-o)).texture(1.0, 0.0).next();
            bufferBuilder.vertex(o, 100.0, o).texture(1.0, 1.0).next();
            bufferBuilder.vertex(-o, 100.0, o).texture(0.0, 1.0).next();
            tessellator.draw();
            o = 20.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(MOON_PHASES);
            int x = this.world.getMoonPhase();
            int t = x % 4;
            int u = x / 4 % 2;
            float s = (float)(t) / 4.0F;
            float v = (float)(u) / 2.0F;
            float w = (float)(t + 1) / 4.0F;
            float y = (float)(u + 1) / 2.0F;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex((-o), -100.0, o).texture(w, y).next();
            bufferBuilder.vertex(o, -100.0, o).texture(s, y).next();
            bufferBuilder.vertex(o, -100.0, (-o)).texture(s, v).next();
            bufferBuilder.vertex((-o), -100.0, (-o)).texture(w, v).next();
            tessellator.draw();
            GlStateManager.disableTexture();
            float z = this.world.method_3707(f) * n;
            if (z > 0.0F) {
                GlStateManager.color4f(z, z, z, z);
                if (this.field_10817) {
                    this.starsBuffer.method_10327();
                    GL11.glEnableClientState(32884);
                    GL11.glVertexPointer(3, 5126, 12, 0L);
                    this.starsBuffer.method_10328(7);
                    this.starsBuffer.method_10330();
                    GL11.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.field_1923);
                }
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture();
            GlStateManager.color3f(0.0F, 0.0F, 0.0F);
            double d = MinecraftClient.getInstance().player.getCameraPosVec(f).y - this.world.getHorizonHeight();
            if (d < 0.0) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0F, 12.0F, 0.0F);

                GlStateManager.popMatrix();
                float q = -((float)(d + 65.0));
                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(-1.0, q, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, q, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, q, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, q, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, q, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, q, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, q, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, q, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
                tessellator.draw();
            }

            GlStateManager.enableTexture();
            GlStateManager.depthMask(true);

            ci.cancel();
        }
    }

    @Inject(method = "method_9924", at = @At("TAIL"))
    public void decurse(CallbackInfo ci){
        if(Axolotlclient.CONFIG.Cursed.rotateWorld)GlStateManager.popMatrix();
    }

}
