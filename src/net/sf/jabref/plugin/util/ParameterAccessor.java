package net.sf.jabref.plugin.util;

import java.util.*;

import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension.Parameter;

public abstract class ParameterAccessor {

	public abstract Plugin getDeclaringPlugin();

	public abstract Parameter getParameter(String id);

	public abstract Collection<Parameter> getParameters();

	public abstract Collection<Parameter> getParameters(String id);

	public abstract String getId();

	/**
	 * 
	 */
	public Number getNumberParameter(String id) {
		return Util.parameter2Number(id, getParameter(id));
	}

	public Collection<Number> getNumberParameters(String id) {
		Collection<Parameter> parameters = getParameters(id);
		Collection<Number> result = new ArrayList<Number>(parameters.size());

		for (Parameter para : parameters) {
			result.add(Util.parameter2Number(id, para));
		}

		return result;
	}

	public boolean getBooleanParameter(String id) {
		return Util.parameter2Boolean(id, getParameter(id));
	}

	public Collection<Boolean> getBooleanParameters(String id) {
		Collection<Parameter> parameters = getParameters(id);
		Collection<Boolean> result = new ArrayList<Boolean>(parameters.size());

		for (Parameter para : parameters) {
			result.add(Util.parameter2Boolean(id, para));
		}

		return result;
	}

	public String getStringParameter(String id) {
		return getParameter(id).rawValue();
	}

	public Collection<String> getStringParameters(String id) {
		Collection<Parameter> parameters = getParameters(id);
		Collection<String> result = new ArrayList<String>(parameters.size());

		for (Parameter para : parameters) {
			result.add(para.rawValue());
		}

		return result;
	}

	public Date getDateParameter(String id) {
		return Util.parameter2Date(id, getParameter(id));
	}

	public Collection<Date> getDateParameters(String id) {
		Collection<Parameter> parameters = getParameters(id);
		Collection<Date> result = new ArrayList<Date>(parameters.size());

		for (Parameter para : parameters) {
			result.add(Util.parameter2Date(id, para));
		}

		return result;
	}

	HashMap<String, Object> singletonMap = new HashMap<String, Object>();

	public Object getClassParameter(String id) {

		if (!singletonMap.containsKey(id)) {

			// Activate plug-in that declares extension.
			PluginManager manager = getDeclaringPlugin().getManager();

			try {
				manager.activatePlugin(getDeclaringPlugin().getDescriptor()
						.getId());

				// Get plug-in class loader.
				ClassLoader classLoader = manager
						.getPluginClassLoader(getDeclaringPlugin()
								.getDescriptor());

				// Load class.
				Class<?> classToInstantiate = classLoader
						.loadClass(getParameter(id).valueAsString());

				singletonMap.put(id, classToInstantiate.newInstance());
			} catch (Exception e) {
				return null;
			}
		}

		return singletonMap.get(id);

	}

	HashMap<String, Collection<Object>> singletonCollectionMap = new HashMap<String, Collection<Object>>();

	public Collection<Object> getClassParameters(String id) {

		if (!singletonCollectionMap.containsKey(id)) {

			// Activate plug-in that declares extension.
			PluginManager manager = getDeclaringPlugin().getManager();

			try {
				manager.activatePlugin(getDeclaringPlugin().getDescriptor()
						.getId());

				// Get plug-in class loader.
				ClassLoader classLoader = manager
						.getPluginClassLoader(getDeclaringPlugin()
								.getDescriptor());

				Collection<Object> instances = new LinkedList<Object>();

				for (String classNamesToLoad : getStringParameters(id)) {
					// Load class.
					Class<?> classToInstantiate = classLoader
							.loadClass(classNamesToLoad);
					instances.add(classToInstantiate.newInstance());
				}

				singletonCollectionMap.put(id, instances);
			} catch (Exception e) {
				return null;
			}
		}

		return singletonCollectionMap.get(id);

	}

}
