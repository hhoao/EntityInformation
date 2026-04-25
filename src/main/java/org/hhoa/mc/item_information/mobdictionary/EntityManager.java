package org.hhoa.mc.item_information.mobdictionary;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import org.hhoa.mc.item_information.utils.Worlds;

public class EntityManager {
    private final TreeSet<EntityType<? extends LivingEntity>> entityTypes =
            new TreeSet<>(Comparator.comparing(EntityType::getTranslationKey));

    public Set<EntityType<? extends LivingEntity>> getEntityTypes() {
        return entityTypes;
    }

    public String[] getEntityNames() {
        return entityTypes.stream().map((EntityType::getTranslationKey)).toArray(String[]::new);
    }

    public EntityType<?> getEntityByName(String name) {
        return entityTypes.stream()
                .filter(entityType -> entityType.getTranslationKey().endsWith(name))
                .findAny()
                .orElse(null);
    }

    public Set<EntityType<? extends LivingEntity>> getAllEntities() {
        return new HashSet<>(entityTypes);
    }

    public boolean containsName(String name) {
        return entityTypes.stream()
                .anyMatch(entityType -> entityType.getTranslationKey().endsWith(name));
    }

    public int getAllMobCount() {
        return entityTypes.size();
    }

    public void addEntityType(EntityType<? extends LivingEntity> entityType) {
        if (entityType.getRegistryName() != null) {
            entityTypes.add(entityType);
        }
    }

    public void loadAllMob(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(Worlds.overworld);
        for (EntityType<?> entity : ForgeRegistries.ENTITIES) {
            Entity o = entity.create(overworld);
            if (o instanceof MobEntity) {
                entityTypes.add((EntityType<? extends LivingEntity>) o.getType());
            }
            if (o != null) {
                o.remove();
            }
        }
    }
}
