package moonfather.cookyourfood;

import moonfather.cookyourfood.storage.RecipeCache;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

public class EventHandlers
{
    public static void onItemUse(Level world, LivingEntity user, ItemStack itemStack)
    {
        if (world.isClientSide() || (user instanceof Player player && player.isSpectator()))
        {
            return;
        }
        if (itemStack.get(DataComponents.FOOD) == null)
        {
            return;
        }
        FoodResolver.RawFoodRank rank = FoodResolver.Resolve(itemStack, world);
        if (rank.equals(FoodResolver.RawFoodRank.NotACookableFood))
        {
            return;
        }

        // have rank
        if (rank.equals(FoodResolver.RawFoodRank.OkayToEat))
        {
            return;
        }
        int effectId = GetSessionEffectId(user, itemStack); // same food equals same effects within half an hour
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
        RecipeCache.clearAndRecreate(minecraftServer);
    }

    public static void onDataPacksReloaded(MinecraftServer minecraftServer, CloseableResourceManager closeableResourceManager, boolean success)
    {
        if (! success) { return; }
        RecipeCache.clearAndRecreate(minecraftServer);
    }

    //////////////////////////////////////////////////
    private static int GetSessionEffectId(LivingEntity player, ItemStack item)
    {
        return GetSessionEffectId(player, item.getItem());
    }

    private static int GetSessionEffectId(LivingEntity player, Item item)
    {
        int pl = player.getId();
        int it = (item != null) ? Item.getId(item) : 0;
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
                Optional<Holder.Reference<MobEffect>> ref = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(ei.effect_id));
                if (ref.isEmpty()) { continue; }
                ApplyEffectInternal(player, ref.get(), ei.duration_in_sec, ei.effect_level);
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



    private static void ApplyEffectInternal(LivingEntity player, Holder<MobEffect> effect, int durationInSeconds, int level)
    {
        int duration = (int) Math.round(durationInSeconds * 20 * GetDifficultyMultiplier(player));
        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null)
        {
        // randomly reduce added duration; approx 0.5 for everything except hunger and 0.25 for hunger.
        float timeMultiplier = player.level().getRandom().nextFloat();
        if (effect.value().equals(MobEffects.HUNGER))
        {
            timeMultiplier *= 0.5f;
        }
        duration = Math.round(timeMultiplier * duration + 1f);
        //System.out.println("*********      Internal(" + timeMultiplier + "," + duration + ")");
        duration = duration + existing.getDuration();
        level = Math.max(level, existing.getAmplifier());
        }
        // back to real work; level is zero based.
        player.addEffect(new MobEffectInstance(effect, duration, level));
    }


    private static double GetDifficultyMultiplier(LivingEntity player)
    {
        if (player.level().getDifficulty() == Difficulty.EASY || player.level().getDifficulty() == Difficulty.PEACEFUL)
        {
            return CookYourFoodMod.CONFIG.EasyDifDurationMultiplier;
        }
        if (player.level().getDifficulty() == Difficulty.NORMAL)
        {
            return CookYourFoodMod.CONFIG.NormalDifDurationMultiplier;
        }
        else
        {
            return CookYourFoodMod.CONFIG.HardDifDurationMultiplier;
        }
    }

    private static final Random random = new Random();
}
