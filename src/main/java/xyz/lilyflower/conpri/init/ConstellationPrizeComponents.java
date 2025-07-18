package xyz.lilyflower.conpri.init;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.lilyflower.conpri.entity.component.PlayerEventFlagsComponent;

public class ConstellationPrizeComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerEventFlagsComponent> EVENT_FLAGS =
            ComponentRegistry.getOrCreate(Identifier.of("conpri", "flags"), PlayerEventFlagsComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(EVENT_FLAGS, PlayerEventFlagsComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
