/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.BookmarkView;
import org.eclipse.ui.views.markers.internal.TableView;

public class FocusBookmarkMarkersViewAction extends AbstractFocusMarkerViewAction {

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (cachedViewer == null) {
			try {
				IViewPart viewPart = super.getPartForAction();
				if (viewPart instanceof BookmarkView) {
					Class<?> infoClass = TableView.class;
					Method method = infoClass.getDeclaredMethod("getViewer", new Class[] {});
					method.setAccessible(true);
					cachedViewer = (StructuredViewer) method.invoke(viewPart, new Object[] {});
					updateMarkerViewLabelProvider(cachedViewer);
				}
			} catch (Exception e) {
				StatusHandler.fail(new Status(IStatus.ERROR, IdeUiBridgePlugin.PLUGIN_ID,
						"Could not get bookmarks view viewer", e));
			}
		}
		if (cachedViewer != null) {
			viewers.add(cachedViewer);
		}
		return viewers;
	}
}
