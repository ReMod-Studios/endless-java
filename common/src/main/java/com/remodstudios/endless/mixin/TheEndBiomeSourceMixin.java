package com.remodstudios.endless.mixin;

import com.remodstudios.endless.world.biome.BiomeRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin {
    @Unique private boolean dumpedTestBiomeInfo = false;

    @Inject(method = "getBiomeForNoiseGen", at = @At("HEAD"), cancellable = true)
    private void addOurOwnEvilBiomes(int x, int y, int z, CallbackInfoReturnable<Biome> cir) {
        long l = x >> 2;
        long m = z >> 2;
        if (l * l + m * m <= 256L) {
            Biome testBiome = BiomeRegistry.INSTANCE.getTEST_BIOME();
            if (!dumpedTestBiomeInfo) {
                dumpedTestBiomeInfo = true;
                System.out.println("Hey. So...");
                System.out.println("BiomeRegistry.TEST_BIOME = " + testBiome);
                RegistryKey<Biome> key = BuiltinRegistries.BIOME.getKey(testBiome).orElse(null);
                if (key == null)
                    System.out.println("Biome isn't registered! Shit's about to get *fucky*!");
                else {
                    System.out.println("Key: " + key);
                    System.out.println("Raw ID (according to the registry itself): " + BuiltinRegistries.BIOME.getRawId(testBiome));
                    int bbRawId = -1;
                    for (Int2ObjectMap.Entry<RegistryKey<Biome>> entry : BuiltinBiomesAccessor.getIdMap().int2ObjectEntrySet()) {
                        if (key.equals(entry.getValue())) {
                            bbRawId = entry.getIntKey();
                            break;
                        }
                    }
                    System.out.println("Raw ID (according to BuiltinBiomes - if this is -1, expect problems!): " + bbRawId);
                }
            }
            cir.setReturnValue(testBiome);
        }
    }
}
