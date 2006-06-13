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
/*
 * Created on Apr 13, 2005
  */
package org.eclipse.mylar.java.ui.actions;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.ui.MembersFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;


public class FilterMembersAction extends Action implements IViewActionDelegate {

    public static final String PREF_ID = "org.eclipse.mylar.java.ui.explorer.filter.members";
    
    public FilterMembersAction() {
        super();
//        setChecked(true);
//        try {
//            boolean checked= MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID); 
//            valueChanged(true, true);
//        } catch (Exception e) {
//            
//        }
    } 

    public void run(IAction action) {
        valueChanged(isChecked(), true);
         
    }
    
    private void valueChanged(final boolean on, boolean store) {
//        setChecked(on);
        if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
        
        setChecked(true);
        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        ViewerFilter existingFilter = null;
        for (int i = 0; i < packageExplorer.getTreeViewer().getFilters().length; i++) {
            ViewerFilter filter = packageExplorer.getTreeViewer().getFilters()[i];
            if (filter instanceof MembersFilter) existingFilter = filter;
        }
        if (existingFilter != null) {
            packageExplorer.getTreeViewer().removeFilter(existingFilter);
        } else {
            packageExplorer.getTreeViewer().addFilter(new MembersFilter());
        }
    }
        
    public void init(IViewPart view) {
    	// don't need to do anything on init
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

}
