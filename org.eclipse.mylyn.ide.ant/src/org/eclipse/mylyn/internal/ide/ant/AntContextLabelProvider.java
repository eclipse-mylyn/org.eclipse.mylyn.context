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

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class AntContextLabelProvider extends AbstractContextLabelProvider {

	public static final String LABEL_RELATION = "referenced by";
	
	@Override
	protected Image getImage(IInteractionElement node) {
		return ContextUiImages.getImage(ContextUiImages.FILE_XML);
	}

	@Override
	protected Image getImage(IInteractionRelation edge) {
		return ContextUiImages.getImage(IdeUiBridgePlugin.EDGE_REF_XML);
	}

	@Override
	protected String getText(IInteractionElement node) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(AntStructureBridge.CONTENT_TYPE);
		return bridge.getName(bridge.getObjectForHandle(node.getHandleIdentifier()));
	}

	@Override
	protected String getText(IInteractionRelation edge) {
		return LABEL_RELATION;
	}

	@Override
	protected Image getImageForObject(Object object) {
		return ContextUiImages.getImage(ContextUiImages.FILE_XML);
	}

	@Override
	protected String getTextForObject(Object object) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(AntStructureBridge.CONTENT_TYPE);
		return bridge.getName(object);
	}

}