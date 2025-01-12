package sonar.core.helpers;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * May not be used in the end, due to making a easier styled string.
 */
public class SonarTextFormatter {

	public static final String START_SELECTION_CHAR = "\uFFEF";
	public static final String END_SELECTION_CHAR = "\uFFF0";
	private static final String CODE_STRING = "\u00a7";
	private static final Pattern START_SELECTION = Pattern.compile("(?i)" + START_SELECTION_CHAR);
	private static final Pattern END_SELECTION = Pattern.compile("(?i)" + END_SELECTION_CHAR);
	private static final Pattern ALL_FORMATS = Pattern.compile("(?i)" + CODE_STRING + "[0-9A-FK-OR]");
	private static final Pattern SPECIAL_FORMATS = Pattern.compile("(?i)" + CODE_STRING + "[K-O]");
	private static final Pattern COLOUR_FORMATS = Pattern.compile("(?i)" + CODE_STRING + "[0-9A-F]");
	private static final Pattern RESET = Pattern.compile("(?i)" + CODE_STRING + "[R]");

	public static String enableSpecialFormattingForSelections(String s, List<TextFormatting> formatting) {
		return formatSelections(s, f -> enableSpecialFormattingForString(f, formatting));
	}

	/**
	 * Enables the given special formatting for the selected string.
	 * Don't pass RESET or COLOUR values.
	 */
	public static String enableSpecialFormattingForString(String s, List<TextFormatting> formatting) {
		s = removeSelectedFormatting(s, formatting);
		s = removeAllOldResets(s);
		String formatString = createFormattingString(formatting);
		s = s.replaceAll(TextFormatting.RESET.toString(), TextFormatting.RESET + formatString);
		s = formatString + s;
		s = checkClosingResets(s);
		return s;
	}

	public static String disableSpecialFormattingForSelections(String s, List<TextFormatting> formatting) {
		return formatSelections(s, f -> disableSpecialFormattingForString(f, formatting));
	}

	/**
	 * Disables the given special formatting for the selected string.
	 * Don't pass RESET or COLOUR values.
	 */
	public static String disableSpecialFormattingForString(String s, List<TextFormatting> formatting) {
		s = removeSelectedFormatting(s, formatting);
		s = removeAllOldResets(s);
		s = checkClosingResets(s);
		return s;
	}

	public static String setTextColourForSelections(String s, TextFormatting colour) {
		return formatSelections(s, f -> setTextColourForString(f, colour));
	}

	/**
	 * Sets the Text Colour for the given string.
	 * Don't pass RESET or SPECIAL FORMATTING values.
	 * You can pass null to remove colour formatting.
	 */
	public static String setTextColourForString(String s, TextFormatting colour) {
		s = removeAllColourFormatting(s);
		s = removeAllOldResets(s);
		if (colour != null) {
			s = s.replaceAll(TextFormatting.RESET.toString(), TextFormatting.RESET.toString() + colour.toString());
		}
		s = checkClosingResets(s);
		return s;
	}

	public static String formatSelections(String s, Function<String, String> format) {
		return formatBetweenPlaceHolders(s, START_SELECTION_CHAR, END_SELECTION_CHAR, format);
	}

	public static String formatBetweenPlaceHolders(String str, final String open, final String close, Function<String, String> format) {
		str = removeAllOldResets(str);
		if (open.isEmpty() || close.isEmpty()) {
			return str;
		}
		final int strLen = str.length();
		if (strLen == 0) {
			return str;
		}
		final int closeLen = close.length();
		final int openLen = open.length();
		final List<String> list = new ArrayList<>();
		int pos = 0;
		while (pos < strLen) {
			if (!(pos < strLen + closeLen)) {
				list.add(str.substring(pos, strLen));
				break;
			} else {
				int start = str.indexOf(open, pos);
				if (start < 0) {
					list.add(str.substring(pos, strLen));
					break;
				}
				start += openLen;
				final int end = str.indexOf(close, start);
				if (end < 0) {
					list.add(str.substring(pos, strLen));
					break;
				}
				// Add previous without formatting
				list.add(str.substring(pos, start));
				list.add(format.apply(str.substring(start, end)));
				list.add(close); // Add these back for next formatting
				pos = end + closeLen;
			}
		}
		if (list.isEmpty()) {
			return str;
		}
		StringBuilder build = new StringBuilder();
		list.forEach(build::append);
		return build.toString();
	}

	public static String removeAllSelections(String s) {
		return s.replaceAll(START_SELECTION_CHAR, "").replaceAll(END_SELECTION_CHAR, "");
	}

	public static String removeAllFormatting(String s) {
		return ALL_FORMATS.matcher(s).replaceAll("");
	}

	public static String removeAllSpecialFormatting(String s) {
		return SPECIAL_FORMATS.matcher(s).replaceAll("");
	}

	public static String removeAllColourFormatting(String s) {
		return COLOUR_FORMATS.matcher(s).replaceAll("");
	}

	public static String removeSelectedFormatting(String s, List<TextFormatting> format) {
		return createFormattingPattern(format).matcher(s).replaceAll("");
	}

	public static String checkClosingResets(String s) {
		return checkFirstReset(checkFinalReset(s));
	}

	public static String removeClosingResets(String s) {
		return removeFirstReset(removeFinalReset(s));
	}

	public static String checkFinalReset(String s) {
		if (!s.endsWith(TextFormatting.RESET.toString())) {
			return s + TextFormatting.RESET.toString();
		}
		return s;
	}

	public static String checkFirstReset(String s) {
		if (!s.startsWith(TextFormatting.RESET.toString())) {
			return TextFormatting.RESET.toString() + s;
		}
		return s;
	}

	public static String removeFinalReset(String s) {
		if (s.endsWith(TextFormatting.RESET.toString())) {
			return s.substring(0, s.length() - TextFormatting.RESET.toString().length());
		}
		return s;
	}

	public static String removeFirstReset(String s) {
		if (s.startsWith(TextFormatting.RESET.toString())) {
			return s.replaceFirst(TextFormatting.RESET.toString(), "");
		}
		return s;
	}

	/**
	 * Removes all resets which have no formatting before them.
	 * You will still need to add back the start and end reset.
	 */
	public static String removeAllOldResets(String s) {
		StringBuilder newS = new StringBuilder();
		String[] resetStrings = RESET.split(s);
		for (String subS : resetStrings) {
			newS.append(subS);
			if (subS.contains(CODE_STRING)) { // If there is some formatting add a reset after
				newS.append(TextFormatting.RESET.toString());
			}
		}
		return newS.toString();
	}

	public static Pattern createFormattingPattern(List<TextFormatting> format) {
		StringBuilder s = new StringBuilder();
		for (TextFormatting text : format) {
			String formatCode = text.toString().replace(CODE_STRING, "");
			if (!s.toString().contains(formatCode)) {
				s.append(formatCode);
			}
		}
		return Pattern.compile("(?i)" + CODE_STRING + "[" + s + "]");
	}

	public static String createFormattingString(List<TextFormatting> format) {
		StringBuilder s = new StringBuilder();
		for (TextFormatting text : format) {
			String formatCode = text.toString();
			if (!s.toString().contains(formatCode)) {
				s.append(formatCode);
			}
		}
		return s.toString();
	}

	public static List<TextFormatting> readFormattingFromNBT(NBTTagCompound nbt) {
		List<TextFormatting> formatting = new ArrayList<>();
		int[] ordinals = nbt.getIntArray("tf");
		for (int i : ordinals) {
			formatting.add(TextFormatting.values()[i]);
		}
		return formatting;
	}

	public static NBTTagCompound writeFormattingToNBT(NBTTagCompound nbt, List<TextFormatting> format) {
		int[] ordinals = ListHelper.getOrdinals((TextFormatting[]) format.toArray());
		if (ordinals.length > 0) {
			nbt.setIntArray("tf", ordinals);
		}
		return nbt;
	}
}
