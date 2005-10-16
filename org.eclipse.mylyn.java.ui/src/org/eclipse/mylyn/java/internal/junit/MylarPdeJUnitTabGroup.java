/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java.internal.junit;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.ui.launcher.AdvancedLauncherTab;
import org.eclipse.pde.internal.ui.launcher.ConfigurationTab;
import org.eclipse.pde.internal.ui.launcher.JUnitArgumentsTab;
import org.eclipse.pde.internal.ui.launcher.JUnitTabGroup;
import org.eclipse.pde.internal.ui.launcher.TracingLauncherTab;

/**
 * Copied from: JUnitTabGroup
 * 
 * @author Mik Kersten
 */
public class MylarPdeJUnitTabGroup extends JUnitTabGroup {
	
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = null;
		if (PDECore.getDefault().getModelManager().isOSGiRuntime()) {
			tabs = new ILaunchConfigurationTab[]{
//					new JUnitMainTab(),
					new JUnitArgumentsTab(), 
					new AdvancedLauncherTab(false),
					 new ConfigurationTab(true), new TracingLauncherTab(),
					new EnvironmentTab(), new SourceLookupTab(), 
					new CommonTab()};
		} else {
			tabs = new ILaunchConfigurationTab[]{
//					new JUnitMainTab(),
					new JUnitArgumentsTab(), 
					new AdvancedLauncherTab(false),
					new TracingLauncherTab(), new EnvironmentTab(),
					new SourceLookupTab(), new CommonTab()};
		}
		setTabs(tabs);
	}
}
