package com.rwtema.denseores.compat;

import com.google.common.base.Throwables;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ModAPIManager;

import javax.annotation.Nonnull;

public abstract class Compat {
	public final static Compat INSTANCE;

	static {
		Compat instance;
		if (ModAPIManager.INSTANCE.hasAPI("compatlayer")) {
			try {
				instance = (Compat) Class.forName("com.rwtema.denseores.compat.CompatLayer").newInstance();
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		} else {
			try {
				instance = (Compat) Class.forName("com.rwtema.denseores.compat.CompatNoCompat").newInstance();
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		}

		INSTANCE = instance;
	}

	public abstract boolean isV11();

	@Nonnull
	public abstract ItemStack incStackSize(@Nonnull ItemStack stack, int amount);

	@Nonnull
	public abstract ItemStack safeCopy(@Nonnull ItemStack stack);

	public abstract int getStackSize(@Nonnull ItemStack stack);

	@Nonnull
	public abstract ItemStack setStackSize(@Nonnull ItemStack stack, int amount);

	public abstract boolean isValid(@Nonnull ItemStack stack);

	public abstract boolean isEmpty(@Nonnull ItemStack stack);

	public abstract void makeEmpty(@Nonnull ItemStack stack);

	@Nonnull
	public abstract ItemStack loadFromNBT(@Nonnull NBTTagCompound nbt);

	@Nonnull
	public abstract ItemStack getEmptyStack();

	public abstract void addChatMessage(@Nonnull ICommandSender sender, @Nonnull ITextComponent component) ;
}
