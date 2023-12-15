package moonfather.cookyourfood;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class OptionsHolder
{
	public static class Common
	{
		private static final double defaultEasyDifDurationMultiplier = 0.8;
		private static final double defaultNormalDifDurationMultiplier = 1.0;
		private static final double defaultHardDifDurationMultiplier = 1.3;

		public final ModConfigSpec.ConfigValue<Double> EasyDifDurationMultiplier;
		public final ModConfigSpec.ConfigValue<Double> NormalDifDurationMultiplier;
		public final ModConfigSpec.ConfigValue<Double> HardDifDurationMultiplier;

		public Common(ModConfigSpec.Builder builder)
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
		public final ModConfigSpec.ConfigValue<Boolean> ShowWarningsInTooltip;
		public Client(ModConfigSpec.Builder builder)
		{
			this.ShowWarningsInTooltip = builder.comment("Show warning tooltips on items that would cause food poisoning.").worldRestart()
												.define("Show warning tooltips on items", defaultShowWarningsInTooltip);
		}
	}

	public static final Common COMMON;
	public static final ModConfigSpec COMMON_SPEC;

	public static final Client CLIENT;
	public static final ModConfigSpec CLIENT_SPEC;

	static //constructor
	{
		Pair<Common, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();

		Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT = clientSpecPair.getLeft();
		CLIENT_SPEC = clientSpecPair.getRight();
	}
}
