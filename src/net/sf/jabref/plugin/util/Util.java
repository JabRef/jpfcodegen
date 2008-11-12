package net.sf.jabref.plugin.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.java.plugin.Plugin;
import org.java.plugin.PluginClassLoader;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;


/**
 * Some useful helper methods.
 *
 */
public class Util {

	/**
	 * Concatenate all strings in the array from index 'from' to 'to' (excluding
	 * to) with the given separator.
	 * 
	 * Example:
	 * 
	 * String[] s = "ab/cd/ed".split("/"); join(s, "\\", 0, s.length) ->
	 * "ab\\cd\\ed"
	 * 
	 * @param strings
	 * @param separator
	 * @param from
	 * @param to
	 *            Excluding strings[to]
	 * @return
	 */
	public static String join(String[] strings, String separator, int from,
			int to) {
		if (strings.length == 0 || from >= to)
			return "";

		from = Math.max(from, 0);
		to = Math.min(strings.length, to);

		StringBuffer sb = new StringBuffer();
		for (int i = from; i < to - 1; i++) {
			sb.append(strings[i]).append(separator);
		}
		return sb.append(strings[to - 1]).toString();
	}

	/**
	 * Returns the given string but with the first character turned into an
	 * upper case character.
	 * 
	 * Example: testTest becomes TestTest
	 * 
	 * @param string
	 *            The string to change the first character to upper case to.
	 * @return A string has the first character turned to upper case and the
	 *         rest unchanged from the given one.
	 */
	public static String toUpperFirstLetter(String string) {
		if (string == null)
			throw new IllegalArgumentException();

		if (string.length() == 0)
			return string;

		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	public static Number parameter2Number(String id, Parameter parameter) {
		if (parameter == null) {
			return null;
		}
		try {
			return parameter.valueAsNumber();
		} catch (UnsupportedOperationException e) {
			ParameterDefinition definition = parameter.getDefinition();
			if (definition == null) {
				throw new IllegalArgumentException("Parameter " + id
						+ " is not valid.");
			}
			return Double.parseDouble(definition.getDefaultValue());
		}
	}

	public static boolean parameter2Boolean(String id, Parameter parameter) {
		if (parameter == null) {
			return false;
		}
		try {
			return parameter.valueAsBoolean();
		} catch (UnsupportedOperationException e) {
			ParameterDefinition definition = parameter.getDefinition();
			if (definition == null) {
				throw new IllegalArgumentException("Parameter " + id
						+ " is not valid.");
			}
			return Boolean.parseBoolean(definition.getDefaultValue());
		}
	}

	public static Date parameter2Date(String id, Parameter parameter) {
		if (parameter == null) {
			return null;
		}
		try {
			return parameter.valueAsDate();
		} catch (UnsupportedOperationException e) {
			return null;
		}
	}

	public static URL parameter2URL(Parameter parameter, Plugin plugin) {
		if (parameter == null || plugin == null) {
			return null;
		}
		try {
			return parameter.valueAsUrl(plugin.getManager().getPathResolver());
		} catch (UnsupportedOperationException e) {
			return null;
		}
	}

	/*
	 * This method will assume that the given parameter is a directory and
	 * append the relative string to it.
	 */
	public static URL parameter2URL(Parameter parameter, String relative,
			Plugin plugin) {
		URL baseURL = parameter2URL(parameter, plugin);
		if (relative == null)
			return baseURL;

		try {
			return new URL(joinPath(baseURL.toExternalForm(), relative));
		} catch (MalformedURLException e) {
			return baseURL;
		}
	}

	public static String joinPath(String one, String two) {
		return one.replaceFirst("/$", "") + "/"
				+ two.replaceFirst("^/", "");
	}

	public static PluginClassLoader getClassLoader(Plugin plugin) {
		if (plugin == null)
			return null;
		return plugin.getManager().getPluginClassLoader(plugin.getDescriptor());
	}

}
