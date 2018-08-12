package com.rwtema.denseores;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;

public class ModIntegration {
	public static final String[] canonOres = {"oreIron", "oreGold", "oreCopper", "oreTin", "oreSilver", "oreLead", "oreNickel", "orePlatinum"};
	public static final String[] canonSecondaryOres = {"oreNickel", null, "oreGold", "oreIron", "oreLead", "oreSilver", "orePlatinum", null};
	public static ModInterface[] mods = {new VanillaFurnace(), new ExtraUtilsCompat(), new EnderIOCompat()};

	public static boolean isCanonOre(String ore) {
		return Arrays.stream(canonOres).anyMatch(s -> s.equals(ore));
	}

	public static String getSecondCanonOre(String ore) {
		for (int i = 0; i < canonOres.length; i++)
			if (canonOres[i].equals(ore))
				return canonSecondaryOres[i];
		return null;
	}

	public static NBTTagCompound getItemStackNBT(ItemStack item, int newStackSize) {
		NBTTagCompound tag = getItemStackNBT(item);
		tag.setByte("Count", (byte) newStackSize);
		return tag;
	}

	public static NBTTagCompound getItemStackNBT(ItemStack item) {
		NBTTagCompound tag = new NBTTagCompound();
		item.writeToNBT(tag);
		return tag;
	}

	public static ItemStack cloneStack(ItemStack item, int newStackSize) {
		ItemStack newitem = item.copy();
		newitem.setCount(newStackSize);
		return newitem;
	}

	public static boolean isOreSmeltsToIngot(String oreDict) {
		if ("".equals(oreDict))
			return false;
		String ingotName = "ingot" + oreDict.substring("ore".length());

		return !OreDictionary.getOres(ingotName).isEmpty();
	}

	public static ItemStack getSmeltedIngot(String oreDict, String preferredModOwner) {
		if ("".equals(oreDict))
			return null;
		String ingotName = "ingot" + oreDict.substring("ore".length());

		ItemStack out = null;

		for (ItemStack ingot : OreDictionary.getOres(ingotName)) {
			out = ingot;

			ResourceLocation s = Item.REGISTRY.getNameForObject(ingot.getItem());
			if (preferredModOwner != null && preferredModOwner.equals(s.getNamespace())) {
				return out;
			}
		}

		return out;
	}

	public static ItemStack getFurnace(DenseOre toSmelt, float multiplier) {
		ItemStack out = FurnaceRecipes.instance().getSmeltingResult(toSmelt.newStack(1));

		if (out.isEmpty()) {
			out = out.copy();

			if (new ResourceLocation("minecraft:lapis_ore").equals(toSmelt.baseBlock))
				out.setCount(6);
			else if (new ResourceLocation("minecraft:redstone_ore").equals(toSmelt.baseBlock))
				out.setCount(4);

			multiplyStackSize(out, multiplier);
		}
		return out;
	}

	public static ItemStack multiplyStackSize(@Nonnull ItemStack out, float multiplier) {
		out = out.copy();
		out.setCount((int) Math.round(out.getCount() * multiplier));
		if (out.getCount() > out.getMaxStackSize())
			out.setCount(out.getMaxStackSize());

		if (out.getCount() < 1)
			out.setCount(1);
		return out;
	}

	public static void addModIntegration() {
		for (DenseOre ore : DenseOresRegistry.ores.values()) {
			ItemStack output = new ItemStack(ore.getBaseBlock(), 1, ore.metadata);
			ItemStack input = new ItemStack(ore.block, 1, 0);

			for (ModInterface mod : mods) {
				mod.registerOre(ore, input, output);
			}

		}
	}

	@Nonnull
	private static String getModID(String modID) {
		return modID.toLowerCase(Locale.US);
	}

	public interface ModInterface {
		void registerOre(DenseOre ore, ItemStack input, ItemStack output);
	}

	public static class VanillaFurnace implements ModInterface {
		@Override
		public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
			ItemStack out = getFurnace(ore, 3F);
			if (out.getItem() != null) {
				GameRegistry.addSmelting(input, multiplyStackSize(out, 3), 1.0F);
			}
		}
	}

	public static class EnderIOCompat implements ModInterface {

		@Override
		public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<recipeGroup name=\"").append("DenseOres").append("\" >");
			stringBuilder.append("<recipe name=\"").append("denseores_").append(ore.name.getPath()).append("\" energyCost=\"3600\" >");
			stringBuilder.append("<input>");
			addEnderIOXMLEntryItemStack(input, stringBuilder);
			stringBuilder.append("</input>");
			stringBuilder.append("<output>");
			addEnderIOXMLEntryItemStack(multiplyStackSize(output, 4), stringBuilder);
			stringBuilder.append("</output>");
			stringBuilder.append("</recipe>");
			stringBuilder.append("</recipeGroup>");

			String s = stringBuilder.toString();
			FMLInterModComms.sendMessage(getModID("EnderIO"), "recipe:sagmill", s);
		}

		private void addEnderIOXMLEntryItemStack(ItemStack input, StringBuilder stringBuilder) {
			ResourceLocation nameForObject = Validate.notNull(Item.REGISTRY.getNameForObject(input.getItem()));
			stringBuilder.append("<itemStack modID=\"");
			stringBuilder.append(nameForObject.getNamespace());
			stringBuilder.append("\" itemName=\"");
			stringBuilder.append(nameForObject.getPath());
			stringBuilder.append("\" itemMeta=\"");
			stringBuilder.append(input.getMetadata());
			stringBuilder.append("\" number = \"");
			stringBuilder.append(input.getCount());
			stringBuilder.append("\" />");
		}
	}

	public static class ExtraUtilsCompat implements ModInterface {
		@Override
		public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
			NBTTagCompound compound = new NBTTagCompound();

			compound.setString("machine", "extrautils2:crusher");
			compound.setTag("input", input.writeToNBT(new NBTTagCompound()));
			compound.setTag("output", multiplyStackSize(output, 4).writeToNBT(new NBTTagCompound()));
			FMLInterModComms.sendMessage(getModID("ExtraUtils2"), "addMachineRecipe", compound);
		}
	}
}
