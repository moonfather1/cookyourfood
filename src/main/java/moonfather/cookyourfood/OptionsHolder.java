package moonfather.cookyourfood;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class OptionsHolder
{
	public static class Common
	{
		private static final double defaultEasyDifDurationMultiplier = 0.8;
		private static final double defaultNormalDifDurationMultiplier = 1.0;
		private static final double defaultHardDifDurationMultiplier = 1.3;

		public final ConfigValue<Double> EasyDifDurationMultiplier;
		public final ConfigValue<Double> NormalDifDurationMultiplier;
		public final ConfigValue<Double> HardDifDurationMultiplier;

		public Common(ForgeConfigSpec.Builder builder)
		{
			this.EasyDifDurationMultiplier = builder.comment("This is the multiplier which can shorten or lengthen potion effect durations, when you earn them by eating raw food. Default value is 0.8, meaning somewhat shorter durations on these difficulties.")
					.defineInRange("Potion effect duration multiplier on easy and peaceful", defaultEasyDifDurationMultiplier, 0.1, 5);
			this.NormalDifDurationMultiplier = builder.comment("This is the multiplier which can shorten or lengthen potion effect durations, when you earn them by eating raw food. Default value is 1.0, meaning author-designed durations.")
					.defineInRange("Potion effect duration multiplier on normal difficulty", defaultNormalDifDurationMultiplier, 0.1, 5);
			this.HardDifDurationMultiplier = builder.comment("This is the multiplier which can shorten or lengthen potion effect durations, when you earn them by eating raw food. Default value is 1.3, meaning somewhat longer duration on these difficulties.").worldRestart()
					.defineInRange("Potion effect duration multiplier on hard and hardcore", defaultHardDifDurationMultiplier, 0.1, 5);
		}
	}
	public static class Client
	{
		private static final boolean defaultShowWarningsInTooltip = false;
		public final ConfigValue<Boolean> ShowWarningsInTooltip;
		public Client(ForgeConfigSpec.Builder builder)
		{
			this.ShowWarningsInTooltip = builder.comment("Show warning tooltips on items that would cause food poisoning.").worldRestart()
												.define("Show warning tooltips on items", defaultShowWarningsInTooltip);
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static //constructor
	{
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();

		Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT = clientSpecPair.getLeft();
		CLIENT_SPEC = clientSpecPair.getRight();
	}
}
