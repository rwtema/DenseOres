package com.rwtema.denseores.compat;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class CompatNoCompat extends Compat {
	@Override
	public boolean isV11() {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack incStackSize(@Nonnull ItemStack stack, int amount) {
		stack.grow(amount);
		return stack;
	}

	/**
	 * Make a safe copy of an itemstack
	 */
	@Override
	@Nonnull
	public ItemStack safeCopy(@Nonnull ItemStack stack) {
		return stack.copy();
	}

	/**
	 * Get the stacksize from a stack
	 */
	@Override
	public int getStackSize(@Nonnull ItemStack stack) {
		return stack.getCount();
	}

	/**
	 * Set the stacksize on a stack. Returns the same stack or null if the new
	 * amount was 0. On 1.11 it will return the 'null' itemstack
	 */
	@Override
	@Nonnull
	public ItemStack setStackSize(@Nonnull ItemStack stack, int amount) {
		if (amount <= 0) {
			return ItemStack.EMPTY;
		}
		stack.setCount(amount);
		return stack;
	}

	/**
	 * Check if this is a valid stack. Tests for null on 1.10.
	 */
	@Override
	public boolean isValid(@Nonnull ItemStack stack) {
		return !stack.isEmpty();
	}

	/**
	 * Check if this is an empty stack. Tests for null on 1.10.
	 */
	@Override
	public boolean isEmpty(@Nonnull ItemStack stack) {
		return stack.isEmpty();
	}

	@Override
	public void makeEmpty(@Nonnull ItemStack stack) {
		stack.setCount(0);
	}

	/**
	 * Load an ItemStack from NBT.
	 */
	@Override
	@Nonnull
	public ItemStack loadFromNBT(@Nonnull NBTTagCompound nbt) {
		return new ItemStack(nbt);
	}

	@Override
	@Nonnull
	public ItemStack getEmptyStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public void addChatMessage(@Nonnull ICommandSender sender, @Nonnull ITextComponent component) {
		if (sender instanceof EntityPlayer) {
			((EntityPlayer) sender).sendStatusMessage(component, false);
		} else {
			sender.sendMessage(component);
		}
	}

	@Override
	public String makeLowercase(@Nullable String string) {
		return string == null ? null : string.toLowerCase(Locale.ENGLISH);
	}
}
