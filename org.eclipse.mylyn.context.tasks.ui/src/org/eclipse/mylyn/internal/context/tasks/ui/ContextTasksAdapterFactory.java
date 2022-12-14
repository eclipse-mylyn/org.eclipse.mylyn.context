/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastian Schmidt - bug 387156
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.ui.ContextAwareEditorInput;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;

/**
 * Adapts the active task to the active context.
 *
 * @author Steffen Pingel
 * @author Sebastian Schmidt
 */
public class ContextTasksAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IInteractionContext.class };

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType == IInteractionContext.class) {
			if (adaptableObject == TasksUi.getTaskActivityManager().getActiveTask()) {
				return ContextCore.getContextManager().getActiveContext();
			} else if (adaptableObject instanceof TaskEditorInput) {
				TaskEditorInput editorInput = (TaskEditorInput) adaptableObject;
				ITask task = editorInput.getTask();
				return ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier());
			}
		} else if (adapterType == ContextAwareEditorInput.class) {
			if (adaptableObject instanceof TaskEditorInput) {
				// forces closing of task editors that do not show the active task
				final TaskEditorInput input = (TaskEditorInput) adaptableObject;
				return new ContextAwareEditorInput() {
					@Override
					public boolean forceClose(String contextHandle) {
						return !input.getTask().getHandleIdentifier().equals(contextHandle);
					}
				};
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
