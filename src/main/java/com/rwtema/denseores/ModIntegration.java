package com.rwtema.denseores;

import com.rwtema.denseores.compat.Compat;
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

	//    public static class TE4Integration implements ModInterface {
//        @Override
//        public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
//            addPulverizer(input, output);
//
//            if (isCanonOre(ore.baseOreDictionary) && getFurnace(ore, 1) != null) {
//                ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
//                String s = getSecondCanonOre(ore.baseOreDictionary);
//                if (s != null && getSmeltedIngot(s, ore.modOwner) != null)
//                    slag = cloneStack(getSmeltedIngot(s, ore.modOwner), 3);
//
//                addSmelter(input, new ItemStack(Blocks.sand, 4), getFurnace(ore, 8.0F), slag, 25);
//                addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "dustPyrotheum", 2), getFurnace(ore, 8.0F), slag, 75);
//                addSmelter(input, GameRegistry.findItemStack("ThermalFoundation", "crystalCinnabar", 2), getFurnace(ore, 16.0F), slag, 100);
//            }
//        }
//
//        private void addSmelter(ItemStack a, ItemStack b, ItemStack output, ItemStack altOutput, int prob) {
//            if (a == null || b == null || output == null)
//                return;
//
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setInteger("energy", 16000);
//            tag.setTag("primaryInput", getItemStackNBT(a));
//            tag.setTag("secondaryInput", getItemStackNBT(b));
//            tag.setTag("primaryOutput", getItemStackNBT(output));
//            if (altOutput != null) {
//                tag.setTag("secondaryOutput", getItemStackNBT(altOutput));
//                tag.setInteger("secondaryChance", prob);
//            }
//            FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", tag);
//        }
//
//        private void addPulverizer(ItemStack input, ItemStack output) {
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setInteger("energy", 8000);
//            tag.setTag("input", getItemStackNBT(input));
//            tag.setTag("primaryOutput", getItemStackNBT(output, 4));
//            FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", tag);
//        }
//    }
//
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
		Compat.INSTANCE.setStackSize(newitem, newStackSize);
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
			if (preferredModOwner != null && preferredModOwner.equals(preferredModOwner.equals(s.getResourceDomain()))) {
				return out;
			}
		}

		return out;
	}

	public static ItemStack getFurnace(DenseOre toSmelt, float multiplier) {
		ItemStack out = FurnaceRecipes.instance().getSmeltingResult(toSmelt.newStack(1));

		if (Compat.INSTANCE.isValid(out)) {
			out = out.copy();

			if (new ResourceLocation("minecraft:lapis_ore").equals(toSmelt.baseBlock))
				Compat.INSTANCE.setStackSize(out, 6);
			else if (new ResourceLocation("minecraft:redstone_ore").equals(toSmelt.baseBlock))
				Compat.INSTANCE.setStackSize(out, 4);

			multiplyStackSize(out, multiplier);
		}
		return out;
	}

	public static ItemStack multiplyStackSize(@Nonnull ItemStack out, float multiplier) {
		out = out.copy();
		Compat.INSTANCE.setStackSize(out, (int) Math.round(Compat.INSTANCE.getStackSize(out) * multiplier));
		if (Compat.INSTANCE.getStackSize(out) > out.getMaxStackSize())
			Compat.INSTANCE.setStackSize(out, out.getMaxStackSize());

		if (Compat.INSTANCE.getStackSize(out) < 1)
			Compat.INSTANCE.setStackSize(out, 1);
		return out;
	}

	//
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
		return Compat.INSTANCE.isV11() ? modID.toLowerCase(Locale.US) : modID;
	}

	public interface ModInterface {
		void registerOre(DenseOre ore, ItemStack input, ItemStack output);
	}

	public static class VanillaFurnace implements ModInterface {
		@Override
		public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
			ItemStack out = getFurnace(ore, 3F);
			if (!Compat.INSTANCE.isEmpty(out)) {
				GameRegistry.addSmelting(input, multiplyStackSize(out, 3), 1.0F);
			}
		}
	}

	public static class EnderIOCompat implements ModInterface {

		@Override
		public void registerOre(DenseOre ore, ItemStack input, ItemStack output) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<recipeGroup name=\"").append("DenseOres").append("\" >");
			stringBuilder.append("<recipe name=\"").append("denseores_").append(ore.name.getResourcePath()).append("\" energyCost=\"3600\" >");
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
			stringBuilder.append(nameForObject.getResourceDomain());
			stringBuilder.append("\" itemName=\"");
			stringBuilder.append(nameForObject.getResourcePath());
			stringBuilder.append("\" itemMeta=\"");
			stringBuilder.append(input.getMetadata());
			stringBuilder.append("\" number = \"");
			stringBuilder.append(Compat.INSTANCE.getStackSize(input));
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
