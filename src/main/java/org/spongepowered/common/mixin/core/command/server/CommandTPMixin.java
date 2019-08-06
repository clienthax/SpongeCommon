/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.command.server;

import com.flowpowered.math.vector.Vector3d;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandTP;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.common.entity.EntityUtil;

import java.util.EnumSet;
import java.util.Set;

@Mixin(CommandTP.class)
public abstract class CommandTPMixin extends CommandBase {

    // This boolean is added in order to make minimal changes to 'execute'.
    // It is set to true if the events fired in 'teleportEntityToCoordinates' are not cancelled.
    // This allows us to prevent calling 'notifyCommandListener' if the event is cancelled.
    private static boolean impl$shouldNotifyCommandListener = false;

    /**
     * @author blood - May 31st, 2016
     * @author gabizou - May 31st, 2016 - Update to 1.9.4
     * @author Aaron1011 - August 15, 2016 - Update to 1.10.2
     * @reason to fix LVT errors with SpongeForge
     *
     * @param sender The command source
     * @param args The command arguments
     */
    @Override
    @Overwrite
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            int i = 0;
            final Entity entity;

            if (args.length != 2 && args.length != 4 && args.length != 6)
            {
                entity = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entity = getEntity(server, sender, args[0]);
                i = 1;
            }

            if (args.length != 1 && args.length != 2)
            {
                if (args.length < i + 3)
                {
                    throw new WrongUsageException("commands.tp.usage", new Object[0]);
                }
                else if (entity.world != null)
                {
                    // int j = 4096;
                    int lvt_6_2_ = i + 1;
                    final CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(entity.posX, args[i], true);
                    final CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(entity.posY, args[lvt_6_2_++], -4096, 4096, false);
                    final CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(entity.posZ, args[lvt_6_2_++], true);
                    final CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate((double)entity.rotationYaw, args.length > lvt_6_2_ ? args[lvt_6_2_++] : "~", false);
                    final CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate((double)entity.rotationPitch, args.length > lvt_6_2_ ? args[lvt_6_2_] : "~", false);
                    // Sponge start - check impl$shouldNotifyCommandListener before calling 'notifyCommandListener'

                    // Guard against any possible re-entrance
                    final boolean shouldNotify = impl$shouldNotifyCommandListener;

                    teleportEntityToCoordinates(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                    if (impl$shouldNotifyCommandListener) {
                        notifyCommandListener(sender, this, "commands.tp.success.coordinates", new Object[] {entity.getName(), Double.valueOf(commandbase$coordinatearg.getResult()), Double.valueOf(commandbase$coordinatearg1.getResult()), Double.valueOf(commandbase$coordinatearg2.getResult())});
                    }
                    impl$shouldNotifyCommandListener = shouldNotify;
                    // Sponge end
                }
            }
            else
            {
                final Entity entity1 = getEntity(server, sender, args[args.length - 1]);

                if (entity1.world != entity.world)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                else
                {
                    entity.dismountRidingEntity();

                    if (entity instanceof ServerPlayerEntity)
                    {
                        // Sponge start
                        final ServerPlayerEntity player = (ServerPlayerEntity) entity;
                        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                            frame.addContext(EventContextKeys.TELEPORT_TYPE, TeleportTypes.COMMAND);
                            final MoveEntityEvent.Teleport event = EntityUtil.handleDisplaceEntityTeleportEvent(entity, entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
                            if (event.isCancelled()) {
                                return;
                            }

                            final Vector3d position = event.getToTransform().getPosition();
                            player.connection.setPlayerLocation(position.getX(), position.getY(), position.getZ(), (float) event.getToTransform().getYaw(), (float) event.getToTransform().getPitch());
                        }
                        // Sponge end
                    }
                    else
                    {
                        // Sponge Start - Events
                        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                            frame.addContext(EventContextKeys.TELEPORT_TYPE, TeleportTypes.COMMAND);
                            final MoveEntityEvent.Teleport event = EntityUtil.handleDisplaceEntityTeleportEvent(entity, entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
                            if (event.isCancelled()) {
                                return;
                            }

                            final Vector3d position = event.getToTransform().getPosition();
                            entity.setLocationAndAngles(position.getX(), position.getY(), position.getZ(), (float) event.getToTransform().getYaw(), (float) event.getToTransform().getPitch());
                        }
                        // Sponge End
                    }

                    notifyCommandListener(sender, this, "commands.tp.success", new Object[] {entity.getName(), entity1.getName()});
                }
            }
        }
    }

    /**
     * @author Aaron1011 - August 15, 2016
     * @reason Muliple modification points are needed, so an overwrite is easier
     */
    @Overwrite
    private static void teleportEntityToCoordinates(final Entity p_189863_0_, final CommandBase.CoordinateArg p_189863_1_, final CommandBase.CoordinateArg p_189863_2_, final CommandBase.CoordinateArg p_189863_3_, final CommandBase.CoordinateArg p_189863_4_, final CommandBase.CoordinateArg p_189863_5_)
    {
        if (p_189863_0_ instanceof ServerPlayerEntity)
        {
            final Set<SPlayerPositionLookPacket.EnumFlags> set = EnumSet.<SPlayerPositionLookPacket.EnumFlags>noneOf(SPlayerPositionLookPacket.EnumFlags.class);

            if (p_189863_1_.isRelative())
            {
                set.add(SPlayerPositionLookPacket.EnumFlags.X);
            }

            if (p_189863_2_.isRelative())
            {
                set.add(SPlayerPositionLookPacket.EnumFlags.Y);
            }

            if (p_189863_3_.isRelative())
            {
                set.add(SPlayerPositionLookPacket.EnumFlags.Z);
            }

            if (p_189863_5_.isRelative())
            {
                set.add(SPlayerPositionLookPacket.EnumFlags.X_ROT);
            }

            if (p_189863_4_.isRelative())
            {
                set.add(SPlayerPositionLookPacket.EnumFlags.Y_ROT);
            }

            float f = (float)p_189863_4_.getAmount();

            if (!p_189863_4_.isRelative())
            {
                f = MathHelper.wrapDegrees(f);
            }

            float f1 = (float)p_189863_5_.getAmount();

            if (!p_189863_5_.isRelative())
            {
                f1 = MathHelper.wrapDegrees(f1);
            }

            // Sponge start
            final ServerPlayerEntity player = (ServerPlayerEntity) p_189863_0_;
            final double x = p_189863_1_.getAmount();
            final double y = p_189863_2_.getAmount();
            final double z = p_189863_3_.getAmount();
            final MoveEntityEvent.Teleport event = EntityUtil.handleDisplaceEntityTeleportEvent(player, x, y, z, f, f1);
            if (event.isCancelled()) {
                return;
            }

            p_189863_0_.dismountRidingEntity();
            final Vector3d position = event.getToTransform().getPosition();
            ((ServerPlayerEntity)p_189863_0_).connection.setPlayerLocation(position.getX(), position.getY(), position.getZ(), (float) event.getToTransform().getYaw(), (float) event.getToTransform().getPitch(), set);
            p_189863_0_.setRotationYawHead((float) event.getToTransform().getYaw());
            // Sponge end
        }
        else
        {
            final float f2 = (float)MathHelper.wrapDegrees(p_189863_4_.getResult());
            float f3 = (float)MathHelper.wrapDegrees(p_189863_5_.getResult());
            f3 = MathHelper.clamp(f3, -90.0F, 90.0F);

            // Sponge start
            final double x = p_189863_1_.getResult();
            final double y = p_189863_2_.getResult();
            final double z = p_189863_3_.getResult();
            final MoveEntityEvent.Teleport event = EntityUtil.handleDisplaceEntityTeleportEvent(p_189863_0_, x, y, z, f2, f3);
            if (event.isCancelled()) {
                return;
            }

            final Vector3d position = event.getToTransform().getPosition();
            p_189863_0_.setLocationAndAngles(position.getX(), position.getY(), position.getZ(), (float) event.getToTransform().getYaw(), (float) event.getToTransform().getPitch());
            p_189863_0_.setRotationYawHead((float) event.getToTransform().getYaw());
            // Sponge end
        }

        if (!(p_189863_0_ instanceof LivingEntity) || !((LivingEntity)p_189863_0_).isElytraFlying())
        {
            p_189863_0_.motionY = 0.0D;
            p_189863_0_.onGround = true;
        }

        // Sponge start - set 'impl$shouldNotifyCommandListener' to 'true' if we make it to the end of the method (the event wasn't cancelled)
        impl$shouldNotifyCommandListener = true;
        // Sponge end
    }
}
