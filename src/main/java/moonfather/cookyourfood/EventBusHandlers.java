package moonfather.cookyourfood;

import java.util.*;

import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventBusHandlers  
{
	private static final Random random = new Random();


	
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



	private static void ApplyLightEffect(LivingEntity player, int sessionEffectId)
	{
		//System.out.println("*********      ApplyLightEffect(" + sessionEffectId + ")");
		int r = sessionEffectId > 0 ? sessionEffectId : random.nextInt(100) + 1;
		if (r <= 30)
		{
			ApplyEffectInternal(player, MobEffects.WEAKNESS, 40, 0);
		}
		else if (r <= 40)
		{
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 40, 0);
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 40, 0);
		}
		else if (r <= 45)
		{
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 0);
		}
	}
	
	
	
	private static void ApplyNormalEffect(LivingEntity player, int sessionEffectId)
	{
		//System.out.println("*********      ApplyNormalEffect(" + sessionEffectId + ")");
		int r = sessionEffectId > 0 ? sessionEffectId : random.nextInt(100) + 1;
		if (r <= 10)
		{
			ApplyEffectInternal(player, MobEffects.POISON, 6, 0);
			ApplyEffectInternal(player, MobEffects.WEAKNESS, 40, 1);
			ApplyEffectInternal(player, MobEffects.HUNGER, 40, 0);
		}
		else if (r <= 25)
		{
			ApplyEffectInternal(player, MobEffects.BLINDNESS, 40, 1);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 40, 1);
			ApplyEffectInternal(player, MobEffects.HUNGER, 40, 0);
		}
		else if (r <= 60)
		{
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 90, 1);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 90, 1);
			ApplyEffectInternal(player, MobEffects.HUNGER, 30, 0);
		}
		else if (r <= 85)
		{
			ApplyEffectInternal(player, MobEffects.WEAKNESS, 80, 1);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 80, 1);
			ApplyEffectInternal(player, MobEffects.HUNGER, 30, 0);
		}
		else if (r <= 99)
		{
			ApplyEffectInternal(player, MobEffects.CONFUSION, 60, 0);
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 0);
			ApplyEffectInternal(player, MobEffects.HUNGER, 40, 0);
		}
	}
	
	
	
	private static void ApplySevereEffect(LivingEntity player, int sessionEffectId)
	{
		//System.out.println("*********      ApplySevereEffect(" + sessionEffectId + ")");
		int r = sessionEffectId > 0 ? sessionEffectId : random.nextInt(100) + 1;
		if (r <= 20)
		{
			ApplyEffectInternal(player, MobEffects.CONFUSION, 45, 0);
			ApplyEffectInternal(player, MobEffects.WEAKNESS, 45, 0);
			ApplyEffectInternal(player, MobEffects.HUNGER, 45, 0);
		}
		else if (r <= 30)
		{
			ApplyEffectInternal(player, MobEffects.CONFUSION, 45, 0);
			ApplyEffectInternal(player, MobEffects.POISON, 10, 0);
		}
		else if (r <= 45)
		{
			ApplyEffectInternal(player, MobEffects.BLINDNESS, 45, 0);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 45, 0);
			ApplyEffectInternal(player, MobEffects.POISON, 20, 0);
		}
		else if (r <= 55)
		{
			ApplyEffectInternal(player, MobEffects.BLINDNESS, 15, 1);
			ApplyEffectInternal(player, MobEffects.POISON, 15, 0);
			ApplyEffectInternal(player, MobEffects.HUNGER, 45, 0);
		}
		else if (r <= 75)
		{
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 2);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 60, 2);
			ApplyEffectInternal(player, MobEffects.POISON, 15, 0);
		}
		else if (r <= 90)
		{
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 0);
			ApplyEffectInternal(player, MobEffects.WEAKNESS, 60, 1);
			ApplyEffectInternal(player, MobEffects.POISON, 15, 1);
		}
		else if (r <= 95)
		{
			ApplyEffectInternal(player, MobEffects.MOVEMENT_SLOWDOWN, 45, 0);
			ApplyEffectInternal(player, MobEffects.DIG_SLOWDOWN, 45, 0);
			ApplyEffectInternal(player, MobEffects.HUNGER, 45, 0);
		}
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
