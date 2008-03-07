package org.eclipse.mylyn.internal.bugzilla.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IBundleGroup;

/**
 * A container for features that map to the same name.
 * 
 * @author Steffen Pingel
 */
public class BundleGroupContainer {

	private final List<IBundleGroup> groups;

	private final String name;

	public BundleGroupContainer(String name) {
		this.name = name;
		this.groups = new ArrayList<IBundleGroup>();
	}

	public void addBundleGroup(IBundleGroup bundleGroup) {
		groups.add(bundleGroup);
	}
	
	public List<IBundleGroup> getGroups() {
		return groups;
	}

	public String getName() {
		return name;
	}
	
}