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

package org.eclipse.mylar.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * @author Mik Kersten
 */
public abstract class AbstractInterestManipulationAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

	public static final String SOURCE_ID = "org.eclipse.mylar.ui.interest.user";

	protected IViewPart view;

	public void init(IWorkbenchWindow window) {
		// don't have anything to initialize
	}

	public void init(IViewPart view) {
		this.view = view;
	}

	protected abstract boolean isIncrement();

	public void run(IAction action) {
		boolean increment = isIncrement();
		ISelection currentSelection = null;
		if (action instanceof ObjectPluginAction) {
			ObjectPluginAction objectAction = (ObjectPluginAction) action;
			currentSelection = objectAction.getSelection();
		} else {
			try {
				currentSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
			} catch (Exception e) {
				// ignore
			}
		}

		if (currentSelection instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) currentSelection;
			for (Object object : selection.toList()) {
				IMylarElement node = null;
				if (object instanceof IMylarElement) {
					node = (IMylarElement) object;
				} else {
					IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
					String handle = bridge.getHandleIdentifier(object);
					node = MylarPlugin.getContextManager().getElement(handle);
				}
				if (node != null) {
					MylarPlugin.getContextManager().manipulateInterestForNode(node, increment, false, SOURCE_ID);
				} else {
					MylarStatusHandler.log("no element for interest manipulation", this);
				}
			}
		} else {
			IMylarElement node = MylarPlugin.getContextManager().getActiveElement();
			if (node != null) {
				MylarPlugin.getContextManager().manipulateInterestForNode(node, increment, false, SOURCE_ID);
			} else {
				MylarStatusHandler.log("no active element for interest manipulation", this);
			}
		}
	}

	public void dispose() {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}