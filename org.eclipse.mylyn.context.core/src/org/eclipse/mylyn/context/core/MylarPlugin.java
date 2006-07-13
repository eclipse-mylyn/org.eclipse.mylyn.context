/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.context.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.core";

	public static final String CONTENT_TYPE_ANY = "*";

	private Map<String, IMylarStructureBridge> bridges = new HashMap<String, IMylarStructureBridge>();

	private IMylarStructureBridge defaultBridge = null;

	private static MylarPlugin INSTANCE;

	private static MylarContextManager contextManager;

	private static IMylarContextStore contextStore;
	
	private static final IMylarStructureBridge DEFAULT_BRIDGE = new IMylarStructureBridge() {

		public String getContentType() {
			return null;
		}

		public String getHandleIdentifier(Object object) {
			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		public Object getObjectForHandle(String handle) {
			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		public String getParentHandle(String handle) {
			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		public String getName(Object object) {
			MylarStatusHandler.log("null bridge for object: " + object.getClass(), this);
			return "";
		}

		public boolean canBeLandmark(String handle) {
			return false;
		}

		public boolean acceptsObject(Object object) {
			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		public boolean canFilter(Object element) {
			return true;
		}

		public boolean isDocument(String handle) {
			// return false;
			throw new RuntimeException("null adapter for handle: " + handle);
		}

		public String getContentType(String elementHandle) {
			return getContentType();
		}

		public List<AbstractRelationProvider> getRelationshipProviders() {
			return Collections.emptyList();
		}

		public List<IDegreeOfSeparation> getDegreesOfSeparation() {
			return Collections.emptyList();
		}

		public String getHandleForOffsetInObject(Object resource, int offset) {
			MylarStatusHandler.log("null bridge for marker: " + resource.getClass(), this);
			return null;
		}

		public void setParentBridge(IMylarStructureBridge bridge) {
			// ignore
		}

		public List<String> getChildHandles(String handle) {
			return Collections.emptyList();
		}
	};

	public MylarPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		CoreExtensionPointReader.initExtensions();
		contextManager = new MylarContextManager();
		
		for (IMylarStructureBridge bridge : bridges.values()) {
			if (bridge.getRelationshipProviders() != null) {
				for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
					getContextManager().addListener(provider);
				}
			}	
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
//			resourceBundle = null;

			// Stop all running jobs when we exit if the plugin didn't do it
			Map<String, IMylarStructureBridge> bridges = getStructureBridges();
			for (Entry<String, IMylarStructureBridge> entry : bridges.entrySet()) {
				IMylarStructureBridge bridge = entry.getValue();// bridges.get(extension);
				List<AbstractRelationProvider> providers = bridge.getRelationshipProviders();
				if (providers == null)
					continue;
				for (AbstractRelationProvider provider : providers) {
					provider.stopAllRunningJobs();
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Core stop failed", false);
		}
	}

	public static MylarPlugin getDefault() {
		return INSTANCE;
	}

	public static MylarContextManager getContextManager() {
		return contextManager;
	}

	public Map<String, IMylarStructureBridge> getStructureBridges() {
		return bridges;
	}

	public IMylarStructureBridge getStructureBridge(String contentType) {
		IMylarStructureBridge bridge = bridges.get(contentType);
		if (bridge != null) {
			return bridge;
		}
		return (defaultBridge == null) ? DEFAULT_BRIDGE : defaultBridge;
	}

	public Set<String> getKnownContentTypes() {
		return bridges.keySet();
	}

	/**
	 * TODO: cache this to improve performance?
	 * 
	 * @return null if there are no bridges loaded, null bridge otherwise
	 */
	public IMylarStructureBridge getStructureBridge(Object object) {
		for (IMylarStructureBridge structureBridge : bridges.values()) {
			if (structureBridge.acceptsObject(object)) {
				return structureBridge;
			}
		}

		// use the default if not finding
		return (defaultBridge != null && defaultBridge.acceptsObject(object)) ? defaultBridge : DEFAULT_BRIDGE;
	}

	private void internalAddBridge(IMylarStructureBridge bridge) {
		if (bridge.getContentType().equals(CONTENT_TYPE_ANY)) {
			defaultBridge = bridge;
		} else {
			bridges.put(bridge.getContentType(), bridge);
		}
	}

//	/**
//	 * Returns the string from the plugin's resource bundle, or 'key' if not
//	 * found.
//	 */
//	public static String getResourceString(String key) {
//		ResourceBundle bundle = MylarPlugin.getDefault().getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}

//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		try {
//			if (resourceBundle == null)
//				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.core.MylarPluginResources");
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
//		return resourceBundle;
//	}

	public IMylarContextStore getContextStore() {
		return contextStore;
	}
	
	public static void setContextStore(IMylarContextStore contextStore) {
		MylarPlugin.contextStore = contextStore;
	}

	public boolean suppressWizardsOnStartup() {
		List<String> commandLineArgs = Arrays.asList(Platform.getCommandLineArgs());
		if (commandLineArgs.contains("-showmylarwizards")) {
			return false;
		} else {
			return commandLineArgs.contains("-pdelaunch");
		}
	}

	/**
	 * TODO: remove
	 */
	public void setDefaultBridge(IMylarStructureBridge defaultBridge) {
		this.defaultBridge = defaultBridge;
	}

	static class CoreExtensionPointReader {

		public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.context.core.context";

		public static final String ELEMENT_STRUCTURE_BRIDGE = "structureBridge";

		public static final String ELEMENT_CONTEXT_STORE = "contextStore";
		
		public static final String ELEMENT_CLASS = "class";

		public static final String ELEMENT_STRUCTURE_BRIDGE_PARENT = "parent";
		
		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(CoreExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE) == 0) {
							readBridge(elements[j]);
						}  
						if (elements[j].getName().compareTo(CoreExtensionPointReader.ELEMENT_CONTEXT_STORE) == 0) {
							readStore(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readStore(IConfigurationElement element) {
			try {
				Object object = element
						.createExecutableExtension(CoreExtensionPointReader.ELEMENT_CLASS);

				if (!(object instanceof IMylarContextStore)) {
					MylarStatusHandler.log("Could not load bridge: " + object.getClass().getCanonicalName()
							+ " must implement " + IMylarStructureBridge.class.getCanonicalName(), null);
					return;
				} else {
					contextStore = (IMylarContextStore)object;
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}
		
		@SuppressWarnings("deprecation")
		private static void readBridge(IConfigurationElement element) {
			try {
				Object object = element
						.createExecutableExtension(CoreExtensionPointReader.ELEMENT_CLASS);

				if (!(object instanceof IMylarStructureBridge)) {
					MylarStatusHandler.log("Could not load bridge: " + object.getClass().getCanonicalName()
							+ " must implement " + IMylarStructureBridge.class.getCanonicalName(), null);
					return;
				}

				IMylarStructureBridge bridge = (IMylarStructureBridge) object;
				MylarPlugin.getDefault().internalAddBridge(bridge);
				if (element.getAttribute(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT) != null) {
					Object parent = element
							.createExecutableExtension(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT);
					if (parent instanceof IMylarStructureBridge) {
						((IMylarStructureBridge) bridge).setParentBridge(((IMylarStructureBridge) parent));
					} else {
						MylarStatusHandler.log("Could not load parent bridge: " + parent.getClass().getCanonicalName()
								+ " must implement " + IMylarStructureBridge.class.getCanonicalName(), null);
					}
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}
	}
}
