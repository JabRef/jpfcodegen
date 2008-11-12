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

import java.util.Collection;

import org.java.plugin.Plugin;
import org.java.plugin.registry.Documentation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginFragment;

/**
 * Helper class to wrap an existing extension for use in a plugin.
 * 
 * Generated plug-in code will extend this class and put all calls through to
 * the wrapped extension instance.
 * 
 */
public class RuntimeExtension extends ParameterAccessor implements Extension  {

	Plugin declaringPlugin;
	Extension wrapped;

	public RuntimeExtension(Plugin declaringPlugin, Extension wrapped) {
		this.declaringPlugin = declaringPlugin;
		this.wrapped = wrapped;
	}

	public Plugin getDeclaringPlugin() {
		return this.declaringPlugin;
	}

	public String getExtendedPluginId() {
		return wrapped.getExtendedPluginId();
	}

	public String getExtendedPointId() {
		return wrapped.getExtendedPointId();
	}

	public Parameter getParameter(String id) {
		return wrapped.getParameter(id);
	}

	public Collection<Parameter> getParameters() {
		return wrapped.getParameters();
	}

	public Collection<Parameter> getParameters(String id) {
		return wrapped.getParameters(id);
	}

	public boolean isValid() {
		return wrapped.isValid();
	}

	public String getUniqueId() {
		return wrapped.getUniqueId();
	}

	public String getId() {
		return wrapped.getId();
	}

	public PluginDescriptor getDeclaringPluginDescriptor() {
		return wrapped.getDeclaringPluginDescriptor();
	}

	public PluginFragment getDeclaringPluginFragment() {
		return wrapped.getDeclaringPluginFragment();
	}

	public String getDocsPath() {
		return wrapped.getDocsPath();
	}

	public Documentation<Extension> getDocumentation() {
		return wrapped.getDocumentation();
	}

}
