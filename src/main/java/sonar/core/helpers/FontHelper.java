package sonar.core.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sonar.core.api.energy.EnergyType;
import sonar.core.utils.CustomColour;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FontHelper {

	public static int text(String info, int x, int y, CustomColour colour) {
		return text(info, x, y, colour.getRGB());
	}

	/** @param colour 0 = grey, 1 = black, 2 = white */
	public static int text(String info, int x, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		switch (colour) {
			case 0:
				return render.drawString(info, x, y, 4210752);
			case 1:
				return render.drawString(info, x, y, 1);
			case 2:
				return render.drawString(info, x, y, -1);
			default:
				return render.drawString(info, x, y, colour);
		}
	}

	public static int text(List<String> info, int gap, int x, int y, int colour) {
		return text(info, gap, 0, info.size(), x, y, colour);
	}

	public static int text(List<String> info, int start, int stop, int gap, int x, int y, int colour) {
		int textPos = 0;
		for (int i = start; i < Math.min(stop, info.size()); i++) {
			String s = info.get(i);
			textPos += text(s, x, y + i * gap, colour);
		}
		return textPos;
	}

	public static int textCentre(String info, int xSize, int y, CustomColour colour) {
		return textCentre(info, xSize, y, colour.getRGB());
	}

	/** @param colour 0 = grey, 1 = black, 2 = white */
	public static int textCentre(String info, int xSize, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		int xPosition = xSize / 2 - width(info) / 2;
		switch (colour) {
			case 0:
				return render.drawString(info, xPosition, y, 4210752);
			case 1:
				return render.drawString(info, xPosition, y, 1);
			case 2:
				return render.drawString(info, xPosition, y, -1);
			default:
				return render.drawString(info, xPosition, y, colour);
		}
	}

	public static int width(String info) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		return render.getStringWidth(info);
	}

	public static int textOffsetCentre(String info, int xCentre, int y, CustomColour colour) {
		return textOffsetCentre(info, xCentre, y, colour.getRGB());
	}

	/** @param info string information
	 * @param xCentre where you want txt to be centred
	 * @param y y coordinate
	 * @param colour 0 = Gray, 1= Black, 2 = White */
	public static int textOffsetCentre(String info, int xCentre, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		int xPosition = xCentre - width(info) / 2;
		switch (colour) {
			case 0:
				return render.drawString(info, xPosition, y, 4210752);
			case 1:
				return render.drawString(info, xPosition, y, 1);
			case 2:
				return render.drawString(info, xPosition, y, -1);
			default:
				return render.drawString(info, xPosition, y, colour);
		}
	}

	/** sends a chat message to the Player & translates it */
	public static void sendMessage(String string, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.sendMessage(new TextComponentTranslation(string));
		}
	}

	public static void sendMessage(ITextComponent component, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.sendMessage(component);
		}
	}

	public static String formatStorage(long power) {
		return formatStorage(EnergyType.RF, power);
	}

	public static String formatStorage(EnergyType type, long power) {
		if (power < 1000) {
			return power + " " + type.getStorageSuffix();
		} else if (power < 1_000_000) {
			return roundValue(1, (float) power / 1000) + " K" + type.getStorageSuffix();
		} else if (power < 1_000_000_000) {
			return roundValue(1, (float) power / 1_000_000) + " M" + type.getStorageSuffix();
		}
		return roundValue(2, (float) power / 1_000_000_000) + " B" + type.getStorageSuffix();
	}

	public static String formatOutput(long power) {
		return formatOutput(EnergyType.RF, power);
	}

	public static String formatOutput(EnergyType type, long power) {
		if (power < 1000) {
			return power + " " + type.getUsageSuffix();
		} else if (power < 1_000_000) {
			return roundValue(1, (float) power / 1000) + " K" + type.getUsageSuffix();
		} else if (power < 1_000_000_000) {
			return roundValue(1, (float) power / 1_000_000) + " M" + type.getUsageSuffix();
		}
		return roundValue(2, (float) power / 1_000_000_000) + " B" + type.getUsageSuffix();
	}

	public static String formatStackSize(long stackSize) {
		if (stackSize < 10_000) {
			return " " + stackSize;
		} else if (stackSize < 1_000_000) {
			return roundValue(1, (float) stackSize / 1000) + " K";
		} else if (stackSize < 1_000_000_000) {
			return roundValue(1, (float) stackSize / 1_000_000) + " M";
		}
		return roundValue(2, (float) stackSize / 1_000_000_000) + " B";
	}

	public static String formatFluidSize(long fluidSize) {
		if (fluidSize < 10_000) {
			return " " + fluidSize + " mB";
		} else if (fluidSize < 1_000_000) {
			return roundValue(1, (float) fluidSize / 1000) + " KmB";
		} else if (fluidSize < 1_000_000_000) {
			return roundValue(1, (float) fluidSize / 1_000_000) + " MmB";
		}
		return roundValue(2, (float) fluidSize / 1_000_000_000) + " BmB";
	}

	public static Float roundValue(int decimalPlace, Float d) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
		return bd.floatValue();
	}

	public static boolean addDigitsToString(GuiTextField box, char c, int i) {
		switch (c) {
			case '\001':
			case '\003':
			case '\030':
				return box.textboxKeyTyped(c, i);
			case '\026':
				return false;
		}
		switch (i) {
			case 14:
			case 199:
			case 203:
			case 205:
			case 207:
			case 211:
				return box.textboxKeyTyped(c, i);
		}
		return Character.isDigit(c) && box.textboxKeyTyped(c, i);
	}

	public static String translate(String string) {
		return new TextComponentTranslation(string).getFormattedText();
	}

	public static int getIntFromColor(int red, int green, int blue) {
		red = red << 16 & 0x00FF0000;
		green = green << 8 & 0x0000FF00;
		blue = blue & 0x000000FF;

		return 0xFF000000 | red | green | blue;
	}

	public static int getColourFromFormatting(TextFormatting colour) {
		int formattingColour = RenderHelper.getTextFormattingColour(colour);
		int r = formattingColour >> 16 & 255;
		int g = formattingColour >> 8 & 255;
		int b = formattingColour & 255;
		return getIntFromColor(r, g, b);
	}

	public static String getStringListToText(List<String> strings) {
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < strings.size(); i++) {
			String string = strings.get(i);
			list.append(i != 0 ? ", " : "").append(string);
		}
		return list.toString();
	}

	public static ArrayList<String> breakUpStringIntoLines(String multiline, int charLimit) {
		ArrayList<String> lines = new ArrayList<>();
		char[] chars = multiline.toCharArray();
		boolean endOfString = false;
		int start = 0;
		int end = start;

		while (start < chars.length - 1) {
			int charCount = 0;
			int lastSpace = 0;
			while (charCount < charLimit) {
				if (chars[charCount + start] == ' ') {
					lastSpace = charCount;
				}
				charCount++;
				if (charCount + start == multiline.length()) {
					endOfString = true;
					break;
				}
			}
			end = endOfString ? multiline.length() : (lastSpace > 0 ? lastSpace + start : charCount + start);
			lines.add(multiline.substring(start, end));
			start = end + 1;
		}
		return lines;
	}

	public static ArrayList<String> breakLines(ArrayList<String> lines, String str, int wrapWidth) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		int i = sizeStringToWidth(render, str, wrapWidth);
		if (str.length() <= i) {
			lines.add(str);
			return lines;
		} else {
			String s = str.substring(0, i);
			char c0 = str.charAt(i);
			boolean flag = c0 == 32 || c0 == 10;
			String s1 = FontRenderer.getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
			lines.add(s);
			return breakLines(lines, s1, wrapWidth);
		}
	}

	// Clone of private method in FontRenderer
	public static int sizeStringToWidth(FontRenderer render, String str, int wrapWidth) {
		int i = str.length();
		int j = 0;
		int k = 0;
		int l = -1;

		for (boolean flag = false; k < i; ++k) {
			char c0 = str.charAt(k);

			switch (c0) {
				case '\u00a3': // FOR PAGE NUMBERS!!!! DON'T USE THIS OTHERWISE
					break;
				case '\n':
					--k;
					break;
				case ' ':
					l = k;
				default:
					j += render.getCharWidth(c0);

					if (flag) {
						++j;
					}
					break;
				case '\u00a7':
					if (k < i - 1) {
						++k;
						char c1 = str.charAt(k);

						if (c1 != 108 && c1 != 76) {
							if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
								flag = false;
							}
						} else {
							flag = true;
						}
					}
			}

			if (c0 == 10) {
				++k;
				l = k;
				break;
			}

			if (j > wrapWidth) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	// Clone of private method in FontRenderer
	public static boolean isFormatColor(char colorChar) {
		return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
	}

	public static String trimToWidthWithParentheses(String s, String parentheses, int width, double scale) {
		int specialWidth = RenderHelper.getStringWidth(parentheses);
		int fullWidth = (int) (((width - specialWidth)) / scale);
		String trimmed = RenderHelper.trimStringToWidth(s, fullWidth);
		if (s.length() != trimmed.length()) {
			trimmed = trimmed + parentheses;
		}
		return trimmed;
	}

}
