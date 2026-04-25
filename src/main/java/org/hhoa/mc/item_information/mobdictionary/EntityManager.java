package org.hhoa.mc.item_information.mobdictionary;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityManager {
    private final TreeSet<EntityType<? extends LivingEntity>> entityTypes =
            new TreeSet<>(Comparator.comparing(EntityType::getDescriptionId));

    public Set<EntityType<? extends LivingEntity>> getEntityTypes() {
        return entityTypes;
    }

    public String[] getEntityNames() {
        return entityTypes.stream().map((EntityType::getDescriptionId)).toArray(String[]::new);
    }

    public EntityType<?> getEntityByName(String name) {
        return entityTypes.stream()
                .filter(entityType -> entityType.getDescriptionId().endsWith(name))
                .findAny()
                .orElse(null);
    }

    public Set<EntityType<? extends LivingEntity>> getAllEntities() {
        return new HashSet<>(entityTypes);
    }

    public boolean containsName(String name) {
        return entityTypes.stream()
                .anyMatch(entityType -> entityType.getDescriptionId().endsWith(name));
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
        ServerLevel overworld = server.overworld();
        Iterable<Entity> allEntities = server.overworld().getAllEntities();
        for (Entity allEntity : allEntities) {
            System.out.println(allEntity);
        }
        for (EntityType<?> entity : ForgeRegistries.ENTITIES) {
            Entity o = entity.create(overworld);
            if (o instanceof Mob) {
                entityTypes.add((EntityType<? extends LivingEntity>) o.getType());
            }
            if (o != null) {
                o.discard();
            }
        }
    }
}
