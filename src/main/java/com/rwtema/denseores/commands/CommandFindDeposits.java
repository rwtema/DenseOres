package com.rwtema.denseores.commands;

import com.rwtema.denseores.blockstates.OreType;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import java.util.LinkedList;

public class CommandFindDeposits extends CommandBase {
    public CommandFindDeposits() {
        synchronized (OreType.deposit_positions) {
            OreType.deposit_positions.clear();
        }
    }

    @Override
    public String getCommandName() {
        return "denseores_find_deposits";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "denseores_find_deposits";
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) throws CommandException {

        synchronized (OreType.deposit_positions) {
            final LinkedList<BlockPos> list = OreType.deposit_positions;
            if (list.isEmpty()) {
                sender.addChatMessage(new ChatComponentTranslation("denseores.commandFindDeposits.noPos"));
            } else {

                sender.addChatMessage(new ChatComponentTranslation("denseores.commandFindDeposits.lastPos", list.size()));
                for (BlockPos pos : list) {
                    sender.addChatMessage(new ChatComponentText(pos.toString()));
                }
            }
        }
    }
}
