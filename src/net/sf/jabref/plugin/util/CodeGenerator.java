/* 
 * JPFCodeGenerator 
 * Copyright (C) 2007 Christopher Oezbek - oezi[at]oezi.de
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.sf.jabref.plugin.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.boot.DefaultPluginsCollector;
import org.java.plugin.registry.*;
import org.java.plugin.registry.ExtensionPoint.ParameterDefinition;
import org.java.plugin.util.ExtendedProperties;

/**
 * Generate plug-in code from plugin.xml.
 * 
 */
public class CodeGenerator {

	/**
	 * Generate Plug-ins from plugin.xml files.
	 * 
	 * @param args
	 *            Pass a comma separated list of plug-in directories and a
	 *            boolean indicating whether to overwrite existing Plugin files
	 *            or not. Defaults are ./plugins and false
	 */
	public static void main(String args[]) {

		String directory = (args.length < 1 ? "./plugins" : args[0]);

		boolean overwrite = (args.length < 2 ? false : Boolean
				.parseBoolean(args[1]));

		generatePlugins(directory, overwrite);
	}

	/**
	 * 
	 * @param pluginLocations
	 *            Comma separated list of plug-in location.
	 * 
	 * @param overwrite
	 *            Whether to overwrite existing main plug-in classes.
	 *            _Plugin-class will be overwritten any way.
	 */
	public static void generatePlugins(String pluginLocations, boolean overwrite) {

		ObjectFactory objectFactory = ObjectFactory.newInstance();
		PluginManager manager = objectFactory.createManager();
		try {
			DefaultPluginsCollector collector = new DefaultPluginsCollector();
			ExtendedProperties ep = new ExtendedProperties();
			ep.setProperty("org.java.plugin.boot.pluginsRepositories",
					pluginLocations);
			collector.configure(ep);

			Collection<PluginLocation> plugins = collector
					.collectPluginLocations();

			if (plugins.size() <= 0) {
				System.out.println("No plugins found.");
				System.exit(0);
			}

			manager.publishPlugins(collector.collectPluginLocations().toArray(
					new PluginLocation[] {}));

			for (PluginDescriptor desc : manager.getRegistry()
					.getPluginDescriptors()) {

				boolean doPlugin = false;

				System.out.println("\nCreating Classes for " + desc.getId());

				File pluginBaseFolder = new File(desc.getLocation().getFile())
						.getParentFile();

				String targetDir = "src/";
				String helperClassName = null;

				PluginAttribute jpfCodeGenAttribute = desc
						.getAttribute("jpfcodegen");
				if (jpfCodeGenAttribute != null) {
					PluginAttribute helperAttribute = jpfCodeGenAttribute
							.getSubAttribute("helperClassName");
					if (helperAttribute != null) {
						helperClassName = helperAttribute.getValue();
					}
					PluginAttribute targetDirAttribute = jpfCodeGenAttribute
							.getSubAttribute("targetDir");
					if (targetDirAttribute != null) {
						targetDir = targetDirAttribute.getValue();
						if (targetDir.length() == 0) {
							System.err.println("targetDir attribute in "
									+ desc.getId()
									+ " is invalid. Proceeding with 'src/'");
							targetDir = "src/";
						}
					}
				}

				String className = null;

				if (helperClassName == null) {
					String pluginClassName = desc.getPluginClassName();
					if (pluginClassName != null) {
						doPlugin = true;
						className = pluginClassName;
					}
				} else {
					doPlugin = false;
					className = helperClassName;
				}

				if (className == null) {
					System.out
							.println("  Code Generator can only generate code for plugins that either have...\n"
									+ "    ...the class attribute set in the plugin or\n"
									+ "    ...the plugin-attribute 'jpfcodegen' -> 'helperClassName' set to the classname to generate\n" +
									  "    See the documentation for details.");
					continue; // Next Plugin
				}
				int lastDot = className.lastIndexOf('.');
				String pluginClassName = className.substring(lastDot + 1);
				String pluginPackageName = className.substring(0, lastDot);

				String[] parts = pluginPackageName.split("\\.");

				StringBuilder path = new StringBuilder();
				for (String part : parts) {
					path.append(part).append("/");
				}

				File pluginFolder = new File(new File(pluginBaseFolder,
						targetDir), path.toString());

				File pluginClassFile = new File(pluginFolder, pluginClassName
						+ ".java");

				Properties p = new Properties();
				p.setProperty("velocimacro.library",
						"resources/templates/macro.vm");
				p.setProperty("resource.loader", "class, file");
				p
						.setProperty("class.resource.loader.class",
								"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
				Velocity.init(p);

				VelocityContext context = new VelocityContext();

				context.put("packageName", pluginPackageName);
				context.put("className", pluginClassName);
				context.put("id", desc.getId());
				context.put("doPlugin", doPlugin);

				// Create Plugin file if it does not already exist
				if (!pluginClassFile.exists() || overwrite) {

					pluginClassFile.getParentFile().mkdirs();

					Template template = Velocity
							.getTemplate("resources/templates/Plugin.vm");

					PrintWriter writer = new PrintWriter(pluginClassFile);

					template.merge(context, writer);

					writer.close();
				}

				// Now do the main work
				File generatedFile = new File(pluginFolder, "generated/_"
						+ pluginClassName + ".java");
				generatedFile.getParentFile().mkdirs();
				PrintWriter writer = new PrintWriter(generatedFile);

				Set<String> imports = new TreeSet<String>();
				context.put("imports", imports);
				imports.add("org.java.plugin.Plugin");
				if (!doPlugin) {
					imports.add("org.java.plugin.PluginManager");
					imports.add("org.java.plugin.PluginLifecycleException");
				}

				LinkedList<HashMap<String, Object>> exts = new LinkedList<HashMap<String, Object>>();

				for (ExtensionPoint ext : desc.getExtensionPoints()) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					exts.add(map);

					map.put("id", ext.getId());
					map.put("name", Util.toUpperFirstLetter(ext.getId()));

					recurseParameters(map, ext.getParameterDefinitions(),
							imports);
				}
				context.put("extensions", exts);

				Template template = Velocity
						.getTemplate("resources/templates/_Plugin.vm");

				template.merge(context, writer);

				writer.close();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void recurseParameters(HashMap<String, Object> map,
			Collection<ParameterDefinition> parameterDefinitions,
			Set<String> imports) {

		LinkedList<HashMap<String, Object>> parameters = new LinkedList<HashMap<String, Object>>();
		map.put("paras", parameters);

		if (parameterDefinitions.size() > 0) {
			imports.add("java.util.ArrayList");
			imports.add("java.util.List");
			imports.add("net.sf.jabref.plugin.util.RuntimeExtension");
			imports.add("org.java.plugin.registry.Extension");
			imports.add("org.java.plugin.registry.ExtensionPoint");
			imports.add("org.java.plugin.PluginLifecycleException");
		}

		for (ExtensionPoint.ParameterDefinition pd : parameterDefinitions) {

			HashMap<String, Object> para = new HashMap<String, Object>();

			String pid = pd.getId();
			String cleanedId = Util.toUpperFirstLetter(pid);
			if (pid.equals("class")) {
				cleanedId += "_";
			}

			para.put("id", pid);
			para.put("name", cleanedId);
			para.put("type", pd.getType().toCode());

			Collection<ExtensionPoint.ParameterDefinition> subDefs = pd
					.getSubDefinitions();

			// Is this a parameter with nested sub parameters?
			if (subDefs.size() > 0) {
				// if so, then create a container class for the parameters
				// and...
				para.put("subclass", true);
				para.put("subclassname", cleanedId);
				para.put("name", "Value");

				// add imports
				imports.add("net.sf.jabref.plugin.util.SubParameterAccessor");

				// populate the sub field with information about these:
				recurseParameters(para, subDefs, imports);
			} else {
				para.put("subclass", false);
			}

			if (pd.getMultiplicity() == ParameterMultiplicity.ANY
					|| pd.getMultiplicity() == ParameterMultiplicity.ONE_OR_MORE) {
				imports.add("java.util.Collection");
				if (pd.getType() == ParameterType.FIXED || (subDefs.size() > 0)) {
					imports.add("java.util.ArrayList");
				}
				para.put("multiplicity", true);
			} else {
				para.put("multiplicity", false);
			}

			String customData;
			switch (pd.getType()) {
			case STRING:
				if ((customData = pd.getCustomData()) != null) {
					para.put("type", "class");
					para.put("className", customData);
				}
				break;
			case FIXED:
				if ((customData = pd.getCustomData()) != null) {
					para.put("enumValues", Util.join(customData.toUpperCase()
							.split("\\|"), ", ", 0, Integer.MAX_VALUE));
				} else {
					continue;
				}
				break;
			case RESOURCE:
				imports.add("java.net.URL");
				break;
			case DATE:
			case DATE_TIME:
			case TIME:
				imports.add("java.util.Date");
				break;
			}
			parameters.add(para);
		}
	}

	enum Bla {
		A, B, C;
	}

}
