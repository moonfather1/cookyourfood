package moonfather.cookyourfood;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EffectPools
{
    private static EffectLevel cachedLight = null, cachedNormal = null, cachedSevere = null;
    
    public static EffectLevel getLight()
    {
        if (cachedLight != null)
        {
            return cachedLight;
        }
        readFromJson();
        return cachedLight;
    }

    public static EffectLevel getNormal()
    {
        if (cachedNormal != null)
        {
            return cachedNormal;
        }
        readFromJson();
        return cachedNormal;
    }

    public static EffectLevel getSevere()
    {
        if (cachedSevere != null)
        {
            return cachedSevere;
        }
        readFromJson();
        return cachedSevere;
    }

    private static void readFromJson()
    {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("cookyourfood-potion-effects.json");
        StoredEffects main = null;
        if (configPath.toFile().exists())
        {
            try
            {
                Gson gson = new Gson();
                main = gson.fromJson(Files.readString(configPath), StoredEffects.class);
            }
            catch (IOException ignored)
            {
            }
        }
        if (main == null)
        {
            main = new StoredEffects();
        }
        if (main.light == null || main.light.effects.length == 0)
        {
            main.light = getDefaultLightArray();
        }
        if (main.normal == null || main.normal.effects.length == 0)
        {
            main.normal = getDefaultNormalArray();
        }
        if (main.severe == null || main.severe.effects.length == 0)
        {
            main.severe = getDefaultSevereArray();
        }
        cachedLight = main.light;
        cachedNormal = main.normal;
        cachedSevere = main.severe;
        validateEffects(cachedLight, MobEffects.SLOWNESS.value());
        validateEffects(cachedNormal, MobEffects.SLOWNESS.value());
        validateEffects(cachedSevere, MobEffects.POISON.value());
        if (! configPath.toFile().exists())
        {
            try
            {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                String text = gson.toJson(main, StoredEffects.class);
                Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            catch (IOException ignored)
            {
            }
        }
    }

    private static void validateEffects(EffectLevel loadedList, MobEffect defaultForMissing)
    {
        for (Effect e: loadedList.effects)
        {
            for (EffectInternal ei: e.list)
            {
                if (! BuiltInRegistries.MOB_EFFECT.containsKey(ResourceLocation.parse(ei.effect_id)))
                {
                    ei.effect_id = BuiltInRegistries.MOB_EFFECT.getKey(defaultForMissing).toString();
                }
            }
        }
    }

    private static EffectLevel getDefaultLightArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[3];
        result.effects[0] = Effect.create(1, 30).add(MobEffects.WEAKNESS.value(), 40, 0);
        result.effects[1] = Effect.create(2, 10).add(MobEffects.MINING_FATIGUE.value(), 40, 0)
                                                             .add(MobEffects.SLOWNESS.value(), 40, 0);;
        result.effects[2] = Effect.create(1,  5).add(MobEffects.SLOWNESS.value(), 60, 0);
        result.description = "effects applied when player eats food of LIGHT severity (raw potatoes, etc.).";
        result.comment = "by default, total of 45% for those effects (30+10+5). 55% chance of no effect.";
        return result;
    }

    private static EffectLevel getDefaultNormalArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[5];
        result.effects[0] = Effect.create(3, 10).add(MobEffects.POISON.value(), 6, 0)
                                                             .add(MobEffects.WEAKNESS.value(), 60, 1)
                                                             .add(MobEffects.HUNGER.value(), 10, 0);
        result.effects[1] = Effect.create(3, 15).add(MobEffects.BLINDNESS.value(), 12, 1)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 40, 1)
                                                             .add(MobEffects.WEAKNESS.value(), 40, 0);
        result.effects[2] = Effect.create(3, 35).add(MobEffects.SLOWNESS.value(), 90, 1)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 90, 1)
                                                             .add(MobEffects.HUNGER.value(), 5, 0);
        result.effects[3] = Effect.create(3, 25).add(MobEffects.WEAKNESS.value(), 75, 1)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 75, 1)
                                                             .add(MobEffects.HUNGER.value(), 10, 0);
        result.effects[4] = Effect.create(3, 14).add(MobEffects.POISON.value(), 10, 0)
                                                             .add(MobEffects.SLOWNESS.value(), 60, 0)
                                                             .add(MobEffects.SLOW_FALLING.value(), 10, 0);
        result.description = "effects applied when player eats food of NORMAL severity (raw meat, etc.).";
        result.comment = "by default, 1% chance of no effect. btw, effect levels are zero-based, so in first group, 1 means weakness II.";
        return result;
    }


    private static EffectLevel getDefaultSevereArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[7];
        result.effects[0] = Effect.create(3, 20).add(MobEffects.NAUSEA.value(), 45, 0)
                                                             .add(MobEffects.WEAKNESS.value(), 45, 0)
                                                             .add(MobEffects.HUNGER.value(), 10, 0);
        result.effects[1] = Effect.create(2, 10).add(MobEffects.NAUSEA.value(), 45, 0)
                                                             .add(MobEffects.POISON.value(), 10, 0);
        result.effects[2] = Effect.create(3, 15).add(MobEffects.BLINDNESS.value(), 45, 0)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 45, 0)
                                                             .add(MobEffects.POISON.value(), 15, 0);
        result.effects[3] = Effect.create(3, 10).add(MobEffects.BLINDNESS.value(), 15, 1)
                                                             .add(MobEffects.POISON.value(), 15, 0)
                                                             .add(MobEffects.HUNGER.value(), 15, 0);
        result.effects[4] = Effect.create(3, 20).add(MobEffects.SLOWNESS.value(), 60, 1)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 60, 1)
                                                             .add(MobEffects.POISON.value(), 15, 0);
        result.effects[5] = Effect.create(3, 15).add(MobEffects.SLOWNESS.value(), 60, 0)
                                                             .add(MobEffects.WEAKNESS.value(), 60, 1)
                                                             .add(MobEffects.POISON.value(), 10, 1);
        result.effects[6] = Effect.create(3,  5).add(MobEffects.SLOWNESS.value(), 45, 0)
                                                             .add(MobEffects.MINING_FATIGUE.value(), 45, 0)
                                                             .add(MobEffects.HUNGER.value(), 15, 0);
        result.description = "effects applied when player eats food of SEVERE severity (zombie flesh, etc.).";
        result.comment = "by default, 5% chance of no effect. btw, durations are in seconds.";
        return result;
    }

    //////////////////////////////////////////////////////
    
    public static class StoredEffects
    {
        public EffectLevel light, normal, severe;
    }

    //////////////////////////////////////////////////////

    public static class EffectLevel
    {
        public Effect[] effects;
        public String description, comment;
    }

    //////////////////////////////////////////////////////

    public static class Effect
    {
        public EffectInternal[] list = null;
        public int weight;

        private Effect() {}
        private static Effect create(int count, int weight)
        {
            Effect result = new Effect();
            result.list = new EffectInternal[count];
            result.weight = weight;
            return result;
        }

        private Effect add(MobEffect effect, int duration, int effectLevel)
        {
            for (int i = 0; i < this.list.length; i++)
            {
                if (this.list[i] == null)
                {
                    this.list[i] = new EffectInternal(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), duration, effectLevel);
                    return this;
                }
            }
            return this;
        }
    }

    //////////////////////////////////////////////////////

    public static class EffectInternal
    {
        public String effect_id;
        public int duration_in_sec, effect_level;

        public EffectInternal(String effect, int duration, int effectLevel)
        {
            this.duration_in_sec = duration;
            this.effect_id = effect;
            this.effect_level = effectLevel;
        }
    }
}
