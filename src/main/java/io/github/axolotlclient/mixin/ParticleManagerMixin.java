package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.particles.Particles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    private ParticleType cachedType;

    @Inject(method = "addParticle(IDDDDDD[I)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"), cancellable = true)
    public void afterCreation(int i, double d, double e, double f, double g, double h, double j, int[] is, CallbackInfoReturnable<Particle> cir){
        cachedType = ParticleType.getById(i);

        if(!Particles.getInstance().getShowParticle(cachedType)){
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At(value = "HEAD"))
    public void afterCreation(Particle particle, CallbackInfo ci){
        if(cachedType!=null){
            Particles.getInstance().particleMap.put(particle, cachedType);
            cachedType=null;
        }
    }

    @Redirect(method = "renderParticles", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    public <E> E applyOptions(List<E> instance, int i){
        E particle = instance.get(i);
        if(Particles.getInstance().particleMap.containsKey((Particle) particle)) {
            Particles.getInstance().applyOptions((Particle) particle);
        }
        return particle;
    }
}
