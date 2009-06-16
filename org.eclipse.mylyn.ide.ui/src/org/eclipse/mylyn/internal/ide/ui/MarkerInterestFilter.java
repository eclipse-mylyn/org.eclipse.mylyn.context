/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 */
public class MarkerInterestFilter extends InterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (!(element instanceof ConcreteMarker)) {
			return true;
			// NOTE: code commented out below did a look-down the children, which may be too expensive
//			if (element instanceof MarkerNode) {
//				MarkerNode markerNode = (MarkerNode) element;
//				MarkerNode[] children = markerNode.getChildren();
//				for (int i = 0; i < children.length; i++) {
//					MarkerNode node = children[i];
//					if (node instanceof ConcreteMarker) {
//						return isInteresting((ConcreteMarker) node, viewer, parent);
//					} else {
//						return true;
//					}
//				}
//			} 
		} else {
			return isInteresting((ConcreteMarker) element, viewer, parent);
		}
	}

	private boolean isImplicitlyInteresting(ConcreteMarker marker) {
		return (marker instanceof ProblemMarker) && ((ProblemMarker) marker).getSeverity() == IMarker.SEVERITY_ERROR;
	}

	private boolean isInteresting(ConcreteMarker marker, Viewer viewer, Object parent) {
		if (isImplicitlyInteresting(marker)) {
			return true;
		} else {
			String handle = ContextCorePlugin.getDefault()
					.getStructureBridge(marker.getResource().getFileExtension())
					.getHandleForOffsetInObject(marker, 0);
			if (handle == null) {
				return false;
			} else {
				return super.select(viewer, parent, ContextCorePlugin.getContextManager().getElement(handle));
			}
		}

	}
}
