/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.junit;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.java.ui.junit.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String TaskContextJUnitLaunchConfiguration_Context_Test_Suite;

	public static String TaskContextJUnitLaunchConfiguration_No_test_types_found_in_the_active_task_context;

	public static String TaskContextJUnitMainTab_SUBCLASSES_OF_TESTCASE_AUTOMATICALLY_ADD_TO_SUITE;
}
