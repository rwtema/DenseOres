package com.rwtema.denseores.compat;

import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class CompatLayer extends Compat {

	@Override
	public boolean isV11() {
		return mcjty.lib.CompatLayer.isV11();
	}

	@Nonnull
	@Override
	public ItemStack incStackSize(@Nonnull ItemStack stack, int amount) {
		return ItemStackTools.incStackSize(stack, amount);
	}

	@Nonnull
	@Override
	public ItemStack safeCopy(@Nonnull ItemStack stack) {
		return ItemStackTools.safeCopy(stack);
	}

	@Override
	public int getStackSize(@Nonnull ItemStack stack) {
		return ItemStackTools.getStackSize(stack);
	}

	@Nonnull
	@Override
	public ItemStack setStackSize(@Nonnull ItemStack stack, int amount) {
		return ItemStackTools.setStackSize(stack, amount);
	}

	@Override
	public boolean isValid(@Nonnull ItemStack stack) {
		return ItemStackTools.isValid(stack);
	}

	@Override
	public boolean isEmpty(@Nonnull ItemStack stack) {
		return ItemStackTools.isEmpty(stack);
	}

	@Override
	public void makeEmpty(@Nonnull ItemStack stack) {
		ItemStackTools.makeEmpty(stack);
	}

	@Nonnull
	@Override
	public ItemStack loadFromNBT(@Nonnull NBTTagCompound nbt) {
		return ItemStackTools.loadFromNBT(nbt);
	}

	@Nonnull
	@Override
	public ItemStack getEmptyStack() {
		return ItemStackTools.getEmptyStack();
	}

	@Override
	public void addChatMessage(@Nonnull ICommandSender sender, @Nonnull ITextComponent component) {
		ChatTools.addChatMessage(sender, component);
	}

	@Override
	public String makeLowercase(@Nullable String string) {
		if(string != null && isV11()){
			return string.toLowerCase(Locale.ENGLISH);
		}
		return string;
	}
}
