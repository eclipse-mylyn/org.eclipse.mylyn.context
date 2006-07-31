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

package org.eclipse.mylar.internal.context.ui.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * Applies itself automatically with context activation
 * 
 * @author Mik Kersten
 */
public abstract class AbstractAutoApplyMylarAction extends AbstractApplyMylarAction implements IMylarContextListener { // IPropertyChangeListener, 

	public AbstractAutoApplyMylarAction(InterestFilter interestFilter) {
		super(interestFilter);
//		ContextUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		ContextCorePlugin.getContextManager().addListener(this);
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(this);
	}
	
//	public void propertyChange(PropertyChangeEvent event) {
//		if (ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE.equals(event.getProperty())) {
//			configureAction();
//		}
//	}

	public void init(IAction action) {
		super.init(action);
		configureAction();
	}
//
//	@Override
//	public void update() {
//		super.update();
//		configureAction();
//	}
	
	private void configureAction() {
		// can not run this until the view has been initialized
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//
			public void run() {
				try {
					if (ContextCorePlugin.getContextManager().isContextActive() &&
							ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
							ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
						update(true);
					} 
//					else if (ContextUiPlugin.getDefault() != null) {
//						ContextUiPlugin.getDefault().getViewerManager().removeManagedAction(AbstractAutoApplyMylarAction.this);
//					}
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "could not toggle Mylar on view: " + getPartForAction(), true);
				}
			}
		});
	}

	public void contextActivated(IMylarContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
			update(true);
		} 
	}

	public void contextDeactivated(IMylarContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
			update(false);
		} 
	}

	public void edgesChanged(IMylarElement element) {
		// ignore
	}

	public void interestChanged(List<IMylarElement> elements) {
		// ignore
	}

	public void landmarkAdded(IMylarElement element) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement element) {
		// ignore
	}

	public void nodeDeleted(IMylarElement element) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore	
	}
	
}