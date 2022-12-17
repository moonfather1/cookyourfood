package moonfather.cookyourfood;

import moonfather.cookyourfood.extra.EventForTooltips;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;


@Mod("cookyourfood")
public class ModCookYourFood
{
    //private static final Logger LOGGER = LogUtils.getLogger();

    public ModCookYourFood()
    {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
		
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
    }



    private void setup(final FMLCommonSetupEvent event)
    {
		EventForTooltips eft = new EventForTooltips();
		if (OptionsHolder.COMMON.ShowWarningsInTooltip.get())
		{
			MinecraftForge.EVENT_BUS.addListener(eft::OnItemTooltip);
		}
    }



    private void enqueueIMC(final InterModEnqueueEvent event)
    {
		InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:apple"; });
		InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:carrot"; });    
	}



    private void processIMC(final InterModProcessEvent event)
    {
	    event.getIMCStream().forEach(
	    		m -> {
				    if (m.getMethod() != null)
				    {
					    Object value = m.getMessageSupplier().get();
					    ResourceLocation itemCode = new ResourceLocation(value != null ? value.toString() : "x:x");
					    if (ForgeRegistries.ITEMS.containsKey(itemCode))
					    {
						    if (m.getMethod().equals("ok-to-eat-raw"))
						    {
							    FoodResolver.AddOkToEatRaw(ForgeRegistries.ITEMS.getValue(itemCode));
						    }
						    else if (m.getMethod().equals("raw-food-light"))
						    {
							    FoodResolver.AddCustomRawFoodLight(ForgeRegistries.ITEMS.getValue(itemCode));
						    }
						    else if (m.getMethod().equals("raw-food-normal"))
						    {
							    FoodResolver.AddCustomRawFoodNormal(ForgeRegistries.ITEMS.getValue(itemCode));
						    }
						    else if (m.getMethod().equals("raw-food-severe"))
						    {
							    FoodResolver.AddCustomRawFoodSevere(ForgeRegistries.ITEMS.getValue(itemCode));
						    }
						    else
						    {
							    System.out.println("Error: InterModComms message to mod CookYourFood was invalid. Sender: " + m.getSenderModId() + ". Valid method names are raw-food-light, raw-food-normal, raw-food-severe, ok-to-eat-raw.");
						    }
					    }
				    }
		});
    }
}
