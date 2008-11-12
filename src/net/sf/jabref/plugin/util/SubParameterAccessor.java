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
import org.java.plugin.registry.Extension.Parameter;

/**
 * Helper class to wrap an existing extension for use in a plugin.
 * 
 * Generated plug-in code will extend this class and put all calls through to
 * the wrapped extension instance.
 * 
 */
public class SubParameterAccessor extends ParameterAccessor {

	Plugin declaringPlugin;
	
	Parameter parameter;

	public SubParameterAccessor(Plugin declaringPlugin, Parameter parameter) {
		this.declaringPlugin = declaringPlugin;
		this.parameter = parameter;
	}

	public Plugin getDeclaringPlugin(){
		return this.declaringPlugin;
	}
	
	public Parameter getParameter(String id) {
		return parameter.getSubParameter(id);
	}

	public Collection<Parameter> getParameters() {
		return parameter.getSubParameters();
	}

	public Collection<Parameter> getParameters(String id) {
		return parameter.getSubParameters(id);
	}

	public String getId() {
		return parameter.getId();
	}
}
