package dev.gigaherz.slimemerger;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Slime;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

@EventBusSubscriber(modid = SlimeMerger.MODID)
public class Merger
{
    private static final RandomSource rand = RandomSource.create();

    @SubscribeEvent
    public static void slimeConstruct(EntityJoinLevelEvent event)
    {
        if (!ConfigManager.enabled)
            return;

        Entity entity = event.getEntity();

        if (entity instanceof Slime slime)
        {
            slime.goalSelector.addGoal(7, new MergeWithNearbySlimesGoal(slime));
            slime.targetSelector.addGoal(5, new MoveTowardNearestSlimeGoal(slime));
        }
    }

    private static boolean isValidTarget(Slime slime, LivingEntity entity)
    {
        return entity != slime
                && entity.isAlive()
                && entity.getClass() == slime.getClass()
                && entity.tickCount >= ConfigManager.minAge
                && ((Slime) entity).getSize() == slime.getSize();
    }

    static class MergeWithNearbySlimesGoal extends Goal
    {
        private final Slime slime;
        private final Sorter sorter;

        public MergeWithNearbySlimesGoal(Slime slimeIn)
        {
            this.slime = slimeIn;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
            this.sorter = new Sorter(slimeIn);
        }

        private List<Slime> findOtherSlimes()
        {
            return slime.level()
                    .getEntities(EntityType.SLIME,
                            slime.getBoundingBox().inflate(slime.getSize() * 1.5, slime.getSize(), slime.getSize() * 1.5),
                            (other) -> isValidTarget(slime, other));
        }

        @Override
        public boolean canUse()
        {
            if (slime.getSize() >= ConfigManager.maxSize)
                return false;
            if (slime.tickCount < ConfigManager.minAge)
                return false;
            if (rand.nextFloat() > 0.5)
                return false;
            return (findOtherSlimes().size() >= 3);
        }


        @Override
        public void start()
        {
            List<Slime> list = findOtherSlimes();
            if (list.size() >= 3)
            {
                list.sort(this.sorter);

                double x = slime.getX();
                double y = slime.getY();
                double z = slime.getZ();

                int size = slime.getSize() + 1;
                for (int i = 0; i < 8 * size; i++)
                {
                    float angle = rand.nextFloat();
                    float speed = size * (1 + rand.nextFloat() * 0.5f);
                    slime.level().addParticle(ParticleTypes.ITEM_SLIME, slime.getX(), slime.getY(), slime.getZ(),
                            speed * Math.cos(angle), 1, speed * Math.sin(angle));
                }

                for (int i = 0; i < 3; i++)
                {
                    Slime target = list.get(i);
                    x += target.getX();
                    y += target.getY();
                    z += target.getZ();
                    target.remove(Entity.RemovalReason.DISCARDED);
                    for (int j = 0; j < 8 * size; j++)
                    {
                        float angle = rand.nextFloat();
                        float speed = size * (1 + rand.nextFloat() * 0.5f);
                        slime.level().addParticle(ParticleTypes.ITEM_SLIME, target.getX(), target.getY(), target.getZ(),
                                speed * Math.cos(angle), 1, speed * Math.sin(angle));
                    }
                }

                x /= 4;
                y /= 4;
                z /= 4;

                int newSize = slime.getSize() + 1;

                slime.setSize(newSize, true);
                slime.setPos(x, y, z);

                slime.playSound(SlimeMerger.SOUND_SHLOP.get(), 1, 0.8f + rand.nextFloat() * 0.4f);
            }
        }


        @Override
        public boolean canContinueToUse()
        {
            return false;
        }
    }

    public static class Sorter implements Comparator<Entity>
    {
        private final Entity entity;

        public Sorter(Entity entityIn)
        {
            this.entity = entityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_)
        {
            double d0 = this.entity.distanceToSqr(p_compare_1_);
            double d1 = this.entity.distanceToSqr(p_compare_2_);

            if (d0 < d1)
            {
                return -1;
            }
            else
            {
                return d0 > d1 ? 1 : 0;
            }
        }
    }

    public static class MoveTowardNearestSlimeGoal extends NearestAttackableTargetGoal<Slime>
    {
        private static final int EXECUTE_CHANCE = 20;

        private final Slime slime;

        public MoveTowardNearestSlimeGoal(final Slime slime)
        {
            super(slime, Slime.class, EXECUTE_CHANCE, true, true, null);
            this.slime = slime;
            this.targetConditions = TargetingConditions.forNonCombat()
                    .ignoreLineOfSight()
                    .range(this.getFollowDistance())
                    .selector(other -> isValidTarget(slime, other));
        }

        @Override
        public boolean canUse()
        {
            if (slime.getSize() >= ConfigManager.maxSize)
                return false;
            if (slime.tickCount < ConfigManager.minAge)
                return false;
            if (rand.nextFloat() > 0.05f)
                return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse()
        {
            if (rand.nextFloat() < 0.01f)
                return false;
            return super.canContinueToUse();
        }
    }
}