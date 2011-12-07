/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.context.ui.ContextPopulationStrategy;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.context.RetrieveLatestContextDialog;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class ContextTasksStartupHandler implements IContextUiStartup {

	/**
	 * Adapts a {@link TaskData} or {@link ITask} to a String based on their content.
	 */
	final class TaskContentAdapter implements IAdaptable {

		private final TaskData taskData;

		private final ITask task;

		TaskContentAdapter(TaskData taskData, ITask task) {
			this.taskData = taskData;
			this.task = task;
		}

		public Object getAdapter(@SuppressWarnings("rawtypes")
		Class adapter) {
			if (adapter == ITask.class) {
				return task;
			} else if (adapter == TaskData.class) {
				return taskData;
			} else if (adapter == String.class) {
				if (taskData != null) {
					TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
					if (attribute != null) {
						String description = attribute.getValue();
						if (description != null && description.length() > 0) {
							return description;
						}
					}

					attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
					if (attribute != null) {
						String description = attribute.getValue();
						if (description != null && description.length() > 0) {
							return description;
						}
					}
				}
				if (task instanceof AbstractTask) {
					String description = ((AbstractTask) task).getNotes();
					if (description != null && description.length() > 0) {
						return description;
					}
				}
			}
			return null;
		}
	}

	private class ContextActivationListener extends AbstractContextListener {

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case PRE_ACTIVATED:
				ContextTasksStartupHandler.this.contextActivated(event);
				break;
			}
		}

	}

	private static final ITaskActivationListener TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

		@Override
		public void preTaskDeactivated(ITask task) {
			ContextUiPlugin.getEditorManager().setEnabled(!TaskMigrator.isActive());
		}

		@Override
		public void preTaskActivated(ITask task) {
			ContextUiPlugin.getEditorManager().setEnabled(!TaskMigrator.isActive());
		}

		@SuppressWarnings("restriction")
		@Override
		public void taskActivated(ITask task) {
			if (CoreUtil.TEST_MODE) {
				// avoid blocking the test suite
				return;
			}

			boolean hasLocalContext = ContextCore.getContextManager().hasContext(task.getHandleIdentifier());
			if (!hasLocalContext) {
				if (org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil.hasContextAttachment(task)) {
					RetrieveLatestContextDialog.openQuestion(WorkbenchUtil.getShell(), task);
				}
			}
		}
	};

	private final ContextPopulationStrategy contextPopulationStrategy = new ContextPopulationStrategy();

	private final ContextActivationListener contextActivationListener = new ContextActivationListener();

	public ContextTasksStartupHandler() {
		// ignore
	}

	public void lazyStartup() {
		ExternalizationManager externalizationManager = TasksUiPlugin.getExternalizationManager();
		ActiveContextExternalizationParticipant activeContextExternalizationParticipant = new ActiveContextExternalizationParticipant(
				externalizationManager);
		externalizationManager.addParticipant(activeContextExternalizationParticipant);
		activeContextExternalizationParticipant.registerListeners();

		ContextUiPlugin.getPerspectiveManager().addManagedPerspective(ITasksUiConstants.ID_PERSPECTIVE_PLANNING);
		TasksUi.getTaskActivityManager().addActivationListener(TASK_ACTIVATION_LISTENER);

		ContextUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION.equals(event.getProperty())) {
					updateAutoManageExpansionPreference();
				}
			}
		});
		updateAutoManageExpansionPreference();

		ContextCore.getContextManager().addListener(contextActivationListener);
	}

	private void lazyStop() {
		ContextCore.getContextManager().removeListener(contextActivationListener);

		ContextUiPlugin.getPerspectiveManager().removeManagedPerspective(ITasksUiConstants.ID_PERSPECTIVE_PLANNING);
		TasksUi.getTaskActivityManager().removeActivationListener(TASK_ACTIVATION_LISTENER);
	}

	private void updateAutoManageExpansionPreference() {
		boolean value = ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION);
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.AUTO_EXPAND_TASK_LIST, value);
	}

	void contextActivated(ContextChangeEvent event) {
		if (contextPopulationStrategy.isDisabled()) {
			return;
		}

		// detect empty context
		if (event.getContext().getAllElements().isEmpty()) {
			// get corresponding task
			final ITask task = TasksUi.getRepositoryModel().getTask(event.getContextHandle());
			if (task != null) {
				try {
					final TaskData taskData;
					if (TasksUiPlugin.getTaskDataManager().hasTaskData(task)) {
						taskData = TasksUiPlugin.getTaskDataManager().getWorkingCopy(task, false).getLocalData();
					} else {
						taskData = null;
					}
					IInteractionContext context = event.getContext();
					IAdaptable input = new TaskContentAdapter(taskData, task);
					contextPopulationStrategy.populateContext(context, input);
				} catch (CoreException e) {
					ContextUiPlugin.getDefault().getLog().log(e.getStatus());
				}
			}
		}
	}
}
