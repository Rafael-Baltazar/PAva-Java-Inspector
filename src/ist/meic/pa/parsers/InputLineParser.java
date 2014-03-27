package ist.meic.pa.parsers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InputLineParser {
	/**
	 * Parses the input line into a String[]. The input line must be a set of
	 * values separated by white space.
	 * 
	 * @param line
	 *            the input line
	 * @return the array with the set of parsed values
	 * @throws ParseException
	 *             the parse exception
	 */
	public static String[] parseInputLine(String line) throws ParseException {
		int length = line.length();
		boolean canParseAValue = true;
		List<String> cmd = new ArrayList<String>();

		for (int i = 0; i < length; i++) {
			char c = line.charAt(i);

			if (Character.isWhitespace(c)) {
				canParseAValue = true;
			} else {
				if (canParseAValue) {
					i = parseValue(line, cmd, i);
					canParseAValue = false;
				} else {
					throw new ParseException(
							"Must have white spaces between values in the input line",
							i);
				}
			}
		}
		return cmd.toArray(new String[0]);
	}

	/**
	 * Parse a value from line. The value could be a string, a char or a normal
	 * value without white spaces.
	 * 
	 * @param line
	 * @param cmd
	 *            list to add the value.
	 * @param i
	 * @return the index of the end of the value.
	 * @throws ParseException
	 */
	private static int parseValue(String line, List<String> cmd, int i)
			throws ParseException {
		char c = line.charAt(i);

		if (c == '"') {
			return parseString(line, cmd, i);
		} else if (c == '\'') {
			return parseChar(line, cmd, i);
		} else {
			return parseOther(line, cmd, i);
		}
	}

	/**
	 * Parse a normal value from line. The value must not include white spaces
	 * nor any of the banned characters for normal values.
	 * 
	 * @param line
	 * @param cmd
	 *            list to add the value.
	 * @param i
	 * @return the index of the end of the normal value.
	 * @throws ParseException
	 */
	private static int parseOther(String line, List<String> cmd, int i)
			throws ParseException {
		String result = "";
		List<Character> bannedChars = initBannedCharacters();

		for (; i < line.length(); i++) {
			char c = line.charAt(i);

			if (bannedChars.contains(c)) {
				throw new ParseException(
						getBannedCharacterExceptionMessage(bannedChars), i);
			} else if (Character.isWhitespace(c)) {
				break;
			} else {
				result += c;
			}
		}
		cmd.add(result);
		return i - 1;
	}

	/**
	 * Creates the message to be sent in the ParseException in the method
	 * parseOther.
	 * 
	 * @param bannedChars
	 * @return the message to be sent in the ParseException in the method
	 *         parseOther.
	 */
	private static String getBannedCharacterExceptionMessage(
			List<Character> bannedChars) {
		String exceptionMessage = "Cannot have character";

		if (bannedChars.size() > 1) {
			exceptionMessage += "s ";
		} else {
			exceptionMessage += " ";
		}

		for (Character c : bannedChars) {
			exceptionMessage += "<" + c + "> ";
		}

		return exceptionMessage + "in a normal value.";
	}

	private static List<Character> initBannedCharacters() {
		List<Character> bannedCharacters = new ArrayList<Character>();
		bannedCharacters.add('\'');
		bannedCharacters.add('"');
		return bannedCharacters;
	}

	/**
	 * Parse a char value from line. Use escape character '\' to add a ' and \\
	 * to add '\' to the char value.
	 * 
	 * @param line
	 * @param cmd
	 *            list to add the value.
	 * @param i
	 * @return the index of the end of the string value.
	 * @throws ParseException
	 */
	private static int parseChar(String line, List<String> cmd, int i)
			throws ParseException {
		boolean escapeCharacter = false;
		String result = "'";

		for (i += 1; i < line.length(); i++) {
			char c = line.charAt(i);

			if (escapeCharacter) {
				// Needed to add ' and \
				result += c;
				escapeCharacter = false;
			} else {
				if (c == '\\') {
					// Needed to add ' and \
					escapeCharacter = true;
				} else if (c == '\'') {
					// Close char value
					result += c;
					cmd.add(result);
					return i;
				} else {
					result += c;
				}
			}
		}
		throw new ParseException("Didn't close char value.", i);
	}

	/**
	 * Parse a string value from line. Use escape character '\' to add '"' and
	 * \\ to add '\' to the string value.
	 * 
	 * @param line
	 * @param cmd
	 *            list to add the value.
	 * @param i
	 * @return the index of the end of the string value.
	 * @throws ParseException
	 */
	private static int parseString(String line, List<String> cmd, int i)
			throws ParseException {
		boolean escapeCharacter = false;
		String result = "\"";

		for (i += 1; i < line.length(); i++) {
			char c = line.charAt(i);

			if (escapeCharacter) {
				// Needed to add " and \
				result += c;
				escapeCharacter = false;
			} else {
				if (c == '\\') {
					// Needed to add " and \
					escapeCharacter = true;
				} else if (c == '"') {
					// Close string value
					result += c;
					cmd.add(result);
					return i;
				} else {
					result += c;
				}
			}
		}
		throw new ParseException("Didn't close string value.", i);
	}
}
