package xyz.lilyflower.epsilon.init;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import xyz.lilyflower.epsilon.entity.component.PlayerEventFlagsComponent;

public class EPSILONComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerEventFlagsComponent> EVENT_FLAGS =
            ComponentRegistry.getOrCreate(Identifier.of("epsilon", "flags"), PlayerEventFlagsComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(EVENT_FLAGS, PlayerEventFlagsComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
