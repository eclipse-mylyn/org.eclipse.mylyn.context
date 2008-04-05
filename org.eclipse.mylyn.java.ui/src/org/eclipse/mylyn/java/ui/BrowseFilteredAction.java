/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.ui;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.context.ui.BrowseFilteredListener;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Do not use.
 * 
 * Moved to internal package, unintentionally to API added as part of 2.1.
 * 
 * API-3.0: delete
 */
@Deprecated
public class BrowseFilteredAction extends Action implements IObjectActionDelegate, IViewActionDelegate {

	private BrowseFilteredListener browseFilteredListener;

	private TreeViewer treeViewer;

	private IStructuredSelection selection;

	@Deprecated
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof PackageExplorerPart) {
			treeViewer = ((PackageExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Deprecated
	public void init(IViewPart targetPart) {
		if (targetPart instanceof PackageExplorerPart) {
			treeViewer = ((PackageExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Deprecated
	public void run(IAction action) {
		if (selection != null) {
			browseFilteredListener.unfilterSelection(treeViewer, selection);
		}
	}

	@Deprecated
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}