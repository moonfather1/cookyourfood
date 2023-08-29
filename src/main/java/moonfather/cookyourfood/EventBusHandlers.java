package moonfather.cookyourfood;

import java.util.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class EventBusHandlers  
{
	private static final Random random = new Random();



	@SubscribeEvent
	public static void OnStarted(ServerStartedEvent event)
	{
		EffectPools.getNormal();  // just poke it to form a json so that users can edit it.
	}



	@SubscribeEvent
	public static void OnFood(LivingEntityUseItemEvent.Finish event)
	{
		if (event.getEntity().level.isClientSide)
		{
			return;
		}
		
		Item item = event.getItem().getItem();
		if (item.getFoodProperties(event.getItem(), event.getEntity()) == null)
		{
			return;
		}
		FoodResolver.RawFoodRank rank = FoodResolver.Resolve(event.getItem(), event.getEntity().level);
		if (rank.equals(FoodResolver.RawFoodRank.NotACookableFood))
		{
			return;
		}

		// have rank
		if (rank.equals(FoodResolver.RawFoodRank.OkayToEat))
		{
			return;
		}
		int effectId = GetSessionEffectId(event.getEntity(), item); // same food equals same effects within half an hour
		if (rank.equals(FoodResolver.RawFoodRank.Severe))
		{
			ApplySevereEffect(event.getEntity(), effectId);
		}
		else if (rank.equals(FoodResolver.RawFoodRank.Light))
		{
			ApplyLightEffect(event.getEntity(), effectId);
		}
		else if (rank.equals(FoodResolver.RawFoodRank.Normal))
		{
			ApplyNormalEffect(event.getEntity(), effectId);
		}
	}

	//////////////////////////////////////////////////

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
				ApplyEffectInternal(player, ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(ei.effect_id)), ei.duration_in_sec, ei.effect_level);
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


	
	private static void ApplyEffectInternal(LivingEntity player, MobEffect potion, int durationInSeconds, int level)
	{
		int duration = (int) Math.round(durationInSeconds * 20 * GetDifficultyMultiplier(player));
		
		MobEffectInstance existing = player.getEffect(potion);
		if (existing != null)
		{
			// randomly reduce added duration; approx 0.5 for everything except hunger and 0.25 for hunger.
			float timeMultiplier = player.level.getRandom().nextFloat();
			if (potion == MobEffects.HUNGER)
			{
				timeMultiplier *= 0.5f;
			}
			duration = Math.round(timeMultiplier * duration + 1f);
			//System.out.println("*********      Internal(" + timeMultiplier + "," + duration + ")");
			duration = duration + existing.getDuration();
			level = Math.max(level, existing.getAmplifier());
		}
		// back to real work; level is zero based.
		player.addEffect(new MobEffectInstance(potion, duration, level));
	}



	private static double GetDifficultyMultiplier(LivingEntity player)
	{
		if (player.level.getDifficulty() == Difficulty.EASY || player.level.getDifficulty() == Difficulty.PEACEFUL)
		{
			return OptionsHolder.COMMON.EasyDifDurationMultiplier.get();
		}
		if (player.level.getDifficulty() == Difficulty.NORMAL)
		{
			return OptionsHolder.COMMON.NormalDifDurationMultiplier.get();
		}
		else
		{
			return OptionsHolder.COMMON.HardDifDurationMultiplier.get();
		}
	}
}
