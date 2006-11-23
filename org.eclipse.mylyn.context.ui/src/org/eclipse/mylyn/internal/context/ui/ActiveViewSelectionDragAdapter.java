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

package org.eclipse.mylar.internal.context.ui;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Mik Kersten
 */
public class ActiveViewSelectionDragAdapter extends DragSourceAdapter implements TransferDragSourceListener {

	private ISelectionProvider fProvider;

	public ActiveViewSelectionDragAdapter(ISelectionProvider provider) {
		assert provider != null;
		fProvider = provider;
	}

	public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	public void dragStart(DragSourceEvent event) {
		ISelection selection = fProvider.getSelection();
		LocalSelectionTransfer.getTransfer().setSelection(selection);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
		event.doit = isDragable(selection);
	}

	protected boolean isDragable(ISelection selection) {
		return true;
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
	}

	public void dragFinished(DragSourceEvent event) {
		assert event.detail != DND.DROP_MOVE;
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}
}