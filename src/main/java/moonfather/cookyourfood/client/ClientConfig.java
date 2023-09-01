package moonfather.cookyourfood.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ClientConfig //extends ReflectiveConfig
{
    public boolean ShowWarningTooltipsOnItems;



    // i am supposed to use import org.quiltmc.config.api.ReflectiveConfig;
    // ... but it needs quilt loader 0.20 and this template came with 0.17 yesterday.
    // ... and raising version isn't trivial, so i'll use effect config as my own config system.

    public static ClientConfig create()
    {
        Path configPath = QuiltLoader.getConfigDir().resolve("cookyourfood-client.json");
        ConfigInner loaded = null;
        if (configPath.toFile().exists())
        {
            try
            {
                Gson gson = new Gson();
                loaded = gson.fromJson(Files.readString(configPath), ConfigInner.class);
            }
            catch (IOException ignored)
            {
            }
        }
        if (loaded == null)
        {
            loaded = new ConfigInner();
        }
        ClientConfig result = new ClientConfig();
        result.ShowWarningTooltipsOnItems = loaded.show_warning_tooltips_on_items;
        if (! configPath.toFile().exists())
        {
            try
            {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                String text = gson.toJson(loaded, ConfigInner.class);
                Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            catch (IOException ignored)
            {
            }
        }
        return result;
    }

    private ClientConfig() { }

    ////////////////////////////////////////////////////////////////

    private static class ConfigInner
    {
        public String comment = "Show warning tooltips on items that would cause food poisoning.";
        public boolean show_warning_tooltips_on_items = true;
    }
}
