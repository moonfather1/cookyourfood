package moonfather.cookyourfood;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.Calendar;
import java.util.Random;

public class EventHandlers
{
    public static void onItemUse(World world, LivingEntity user, Item item)
    {
        if (world.isClient() || (user instanceof PlayerEntity player && player.isSpectator()))
        {
            return;
        }

        if (world.isClient)
        {
            return;
        }
        if (item.getFoodComponent() == null)
        {
            return;
        }
        FoodResolver.RawFoodRank rank = FoodResolver.Resolve(item.getDefaultStack(), world);
        if (rank.equals(FoodResolver.RawFoodRank.NotACookableFood))
        {
            return;
        }

        // have rank
        if (rank.equals(FoodResolver.RawFoodRank.OkayToEat))
        {
            return;
        }
        int effectId = GetSessionEffectId(user, item); // same food equals same effects within half an hour
        if (rank.equals(FoodResolver.RawFoodRank.Severe))
        {
            ApplySevereEffect(user, effectId);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Light))
        {
            ApplyLightEffect(user, effectId);
        }
        else if (rank.equals(FoodResolver.RawFoodRank.Normal))
        {
            ApplyNormalEffect(user, effectId);
        }
    }

    public static void onServerStarted(MinecraftServer minecraftServer)
    {
        EffectPools.getLight();  // poke the config to create a file
    }

    //////////////////////////////////////////////////

    private static int GetSessionEffectId(LivingEntity player, Item item)
    {
        int pl = player.getId();
        int it = (item != null) ? Item.getRawId(item) : 0;
        int ho = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int mi = Calendar.getInstance().get(Calendar.MINUTE);

        return Math.abs(1500450271 * pl + 15487457 * it + 15485917 * ho + 71 * (mi < 30 ? 1 : 0)) % 100 + 1;
        ////  http://www.bigprimes.net/archive/prime/10001/
    }



    private static void ApplyLoadedEffect(LivingEntity player, int sessionEffectId, EffectPools.EffectLevel loaded)
    {
        int r = sessionEffectId > 0 ? sessionEffectId : random.nextInt(100) + 1;
        int index = 0;
        while (index < loaded.effects.length && r > loaded.effects[index].weight)
        {
            r -= loaded.effects[index].weight;
            index += 1;
        }
        if (index < loaded.effects.length)
        {
            // if not, we went through all effect and there should be nothing applied
            for (EffectPools.EffectInternal ei: loaded.effects[index].list)
            {
                ApplyEffectInternal(player, Registries.STATUS_EFFECT.get(new Identifier(ei.effect_id)), ei.duration_in_sec, ei.effect_level);
            }
        }
    }

    private static void ApplyLightEffect(LivingEntity player, int sessionEffectId)
    {
        ApplyLoadedEffect(player, sessionEffectId, EffectPools.getLight());
        //System.out.println("*********      ApplyLightEffect(" + sessionEffectId + ")");
    }

    private static void ApplyNormalEffect(LivingEntity player, int sessionEffectId)
    {
        //System.out.println("*********      ApplyNormalEffect(" + sessionEffectId + ")");
        ApplyLoadedEffect(player, sessionEffectId, EffectPools.getNormal());
    }

    private static void ApplySevereEffect(LivingEntity player, int sessionEffectId)
    {
        //System.out.println("*********      ApplySevereEffect(" + sessionEffectId + ")");
        ApplyLoadedEffect(player, sessionEffectId, EffectPools.getSevere());
    }



    private static void ApplyEffectInternal(LivingEntity player, StatusEffect potion, int durationInSeconds, int level)
    {
        int duration = (int) Math.round(durationInSeconds * 20 * GetDifficultyMultiplier(player));
        StatusEffectInstance existing = player.getStatusEffect(potion);
        if (existing != null)
        {
        // randomly reduce added duration; approx 0.5 for everything except hunger and 0.25 for hunger.
        float timeMultiplier = player.getWorld().getRandom().nextFloat();
        if (potion == StatusEffects.HUNGER)
        {
        timeMultiplier *= 0.5f;
        }
        duration = Math.round(timeMultiplier * duration + 1f);
        //System.out.println("*********      Internal(" + timeMultiplier + "," + duration + ")");
        duration = duration + existing.getDuration();
        level = Math.max(level, existing.getAmplifier());
        }
        // back to real work; level is zero based.
        player.addStatusEffect(new StatusEffectInstance(potion, duration, level));
    }



    private static double GetDifficultyMultiplier(LivingEntity player)
    {
        if (player.getWorld().getDifficulty() == Difficulty.EASY || player.getWorld().getDifficulty() == Difficulty.PEACEFUL)
        {
            return ModCookYourFood.CONFIG.EasyDifDurationMultiplier;
        }
        if (player.getWorld().getDifficulty() == Difficulty.NORMAL)
        {
            return ModCookYourFood.CONFIG.NormalDifDurationMultiplier;
        }
        else
        {
            return ModCookYourFood.CONFIG.HardDifDurationMultiplier;
        }
    }
    private static final Random random = new Random();
}
