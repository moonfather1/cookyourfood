package moonfather.cookyourfood;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.QuiltLoader;

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
        Path configPath = QuiltLoader.getConfigDir().resolve("cookyourfood-potion-effects.json");
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
        validateEffects(cachedLight, StatusEffects.SLOWNESS);
        validateEffects(cachedNormal, StatusEffects.SLOWNESS);
        validateEffects(cachedSevere, StatusEffects.POISON);
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

    private static void validateEffects(EffectLevel loadedList, StatusEffect defaultForMissing)
    {
        for (Effect e: loadedList.effects)
        {
            for (EffectInternal ei: e.list)
            {
                if (! Registry.STATUS_EFFECT.containsId(new Identifier(ei.effect_id)))
                {
                    ei.effect_id = Registry.STATUS_EFFECT.getKey(defaultForMissing).toString();
                }
            }
        }
    }

    private static EffectLevel getDefaultLightArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[3];
        result.effects[0] = Effect.create(1, 30).add(StatusEffects.WEAKNESS, 40, 0);
        result.effects[1] = Effect.create(2, 10).add(StatusEffects.MINING_FATIGUE, 40, 0)
                                                             .add(StatusEffects.SLOWNESS, 40, 0);;
        result.effects[2] = Effect.create(1,  5).add(StatusEffects.SLOWNESS, 60, 0);
        result.description = "effects applied when player eats food of LIGHT severity (raw potatoes, etc.).";
        result.comment = "by default, total of 45% for those effects (30+10+5). 55% chance of no effect.";
        return result;
    }

    private static EffectLevel getDefaultNormalArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[5];
        result.effects[0] = Effect.create(3, 10).add(StatusEffects.POISON, 6, 0)
                                                             .add(StatusEffects.WEAKNESS, 40, 1)
                                                             .add(StatusEffects.HUNGER, 10, 0);
        result.effects[1] = Effect.create(3, 15).add(StatusEffects.BLINDNESS, 40, 1)
                                                             .add(StatusEffects.MINING_FATIGUE, 40, 1)
                                                             .add(StatusEffects.WEAKNESS, 10, 0);
        result.effects[2] = Effect.create(3, 35).add(StatusEffects.SLOWNESS, 90, 1)
                                                             .add(StatusEffects.MINING_FATIGUE, 90, 1)
                                                             .add(StatusEffects.HUNGER, 5, 0);
        result.effects[3] = Effect.create(3, 25).add(StatusEffects.WEAKNESS, 75, 1)
                                                             .add(StatusEffects.MINING_FATIGUE, 75, 1)
                                                             .add(StatusEffects.HUNGER, 10, 0);
        result.effects[4] = Effect.create(3, 14).add(StatusEffects.POISON, 10, 0)
                                                             .add(StatusEffects.SLOWNESS, 60, 0)
                                                             .add(StatusEffects.SLOW_FALLING, 10, 0);
        result.description = "effects applied when player eats food of NORMAL severity (raw meat, etc.).";
        result.comment = "by default, 1% chance of no effect. btw, effect levels are zero-based, so in first group, 1 means weakness II.";
        return result;
    }


    private static EffectLevel getDefaultSevereArray()
    {
        EffectLevel result = new EffectLevel();
        result.effects = new Effect[7];
        result.effects[0] = Effect.create(3, 20).add(StatusEffects.NAUSEA, 45, 0)
                                                             .add(StatusEffects.WEAKNESS, 45, 0)
                                                             .add(StatusEffects.HUNGER, 10, 0);
        result.effects[1] = Effect.create(2, 10).add(StatusEffects.NAUSEA, 45, 0)
                                                             .add(StatusEffects.POISON, 10, 0);
        result.effects[2] = Effect.create(3, 15).add(StatusEffects.BLINDNESS, 45, 0)
                                                             .add(StatusEffects.MINING_FATIGUE, 45, 0)
                                                             .add(StatusEffects.POISON, 15, 0);
        result.effects[3] = Effect.create(3, 10).add(StatusEffects.BLINDNESS, 15, 1)
                                                             .add(StatusEffects.POISON, 15, 0)
                                                             .add(StatusEffects.HUNGER, 15, 0);
        result.effects[4] = Effect.create(3, 20).add(StatusEffects.SLOWNESS, 60, 1)
                                                             .add(StatusEffects.MINING_FATIGUE, 60, 1)
                                                             .add(StatusEffects.POISON, 15, 0);
        result.effects[5] = Effect.create(3, 15).add(StatusEffects.SLOWNESS, 60, 0)
                                                             .add(StatusEffects.WEAKNESS, 60, 1)
                                                             .add(StatusEffects.POISON, 10, 1);
        result.effects[6] = Effect.create(3,  5).add(StatusEffects.SLOWNESS, 45, 0)
                                                             .add(StatusEffects.MINING_FATIGUE, 45, 0)
                                                             .add(StatusEffects.HUNGER, 15, 0);
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

        private Effect add(StatusEffect effect, int duration, int effectLevel)
        {
            for (int i = 0; i < this.list.length; i++)
            {
                if (this.list[i] == null)
                {
                    this.list[i] = new EffectInternal(Registry.STATUS_EFFECT.getId(effect).toString(), duration, effectLevel);
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
