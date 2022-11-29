/*******************************************************************************
 * Copyright (c) 2022 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import org.eclipse.mylyn.common.context.IContextCallBack;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

public class ContextCallBack implements IContextCallBack {

	@Override
	public void processActivityMetaContextEvent(InteractionEvent event) {
		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(event);
	}

	@Override
	public String getActiveContextHandleIdentifier() {
		if (ContextCore.getContextManager().getActiveContext().getHandleIdentifier() != null) {
			return ContextCore.getContextManager().getActiveContext().getHandleIdentifier();
		}
		return null;
	}

}
