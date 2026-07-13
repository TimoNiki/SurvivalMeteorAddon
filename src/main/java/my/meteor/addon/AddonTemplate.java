// Файл сделан ии. потом LightLight01 мне его поможет до делать чтобы без ии.
package my.meteor.addon;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import my.meteor.addon.modules.AntiMobRun;
import my.meteor.addon.modules.AntiTntRun;
import my.meteor.addon.modules.LavaGrief;
import my.meteor.addon.modules.TntGrief;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    
    // Создаем кастомную вкладку "Local World" в чите
    public static final Category CATEGORY = new Category("Local World");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Local World Utils Addon for 1.21.11!");

        // Регистрируем ваши модули
        Modules.get().add(new TntGrief());
        Modules.get().add(new LavaGrief());
        Modules.get().add(new AntiTntRun());
        Modules.get().add(new AntiMobRun());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "my.meteor.addon";
    }
}
