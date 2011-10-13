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

package org.eclipse.mylyn.internal.resources.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class ResourceContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	public Image getImage(IInteractionElement node) {
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(ResourceStructureBridge.CONTENT_TYPE);
		Object object = bridge.getObjectForHandle(node.getHandleIdentifier());
		return getImageForObject(object);
	}

	@Override
	protected Image getImageForObject(Object object) {
		if (object instanceof IFile) {
			return CommonImages.getImage(ContextUiImages.FILE_GENERIC);
		} else if (object instanceof IContainer) {
			return CommonImages.getImage(ContextUiImages.FOLDER_GENERIC);
		}
		return null;
	}

	@Override
	protected String getTextForObject(Object object) {
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(object);
		return bridge.getLabel(object);
	}

	/**
	 * TODO: slow?
	 */
	@Override
	public String getText(IInteractionElement node) {
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(ResourceStructureBridge.CONTENT_TYPE);
		return bridge.getLabel(bridge.getObjectForHandle(node.getHandleIdentifier()));
	}

	@Override
	protected Image getImage(IInteractionRelation edge) {
		return null;
	}

	@Override
	protected String getText(IInteractionRelation edge) {
		return null;
	}
}
