/*******************************************************************************
 * Copyright (c) 2004, 2011 Mylyn project committers and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.team.ui.ContextChangeSet;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.AbstractTaskReference;
import org.eclipse.mylyn.team.ui.IContextChangeSet;
import org.eclipse.mylyn.team.ui.TeamUiUtil;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.internal.ccvs.core.mapping.CVSActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.osgi.service.prefs.Preferences;

/**
 * Copied from {@link ContextChangeSet}
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class CvsContextChangeSet extends CVSActiveChangeSet implements IAdaptable, IContextChangeSet {

	// HACK: copied from super
	private static final String CTX_TITLE = "title"; //$NON-NLS-1$

	public static final String SOURCE_ID = "org.eclipse.mylyn.java.context.changeset.add"; //$NON-NLS-1$

	private final ITask task;

	public CvsContextChangeSet(ITask task, ActiveChangeSetManager manager) {
		super(manager, task.getSummary());
		this.task = task;
		updateLabel();
	}

	@Override
	public boolean isUserCreated() {
		return true;
	}

	public void updateLabel() {
		super.setName(task.getSummary());
		super.setTitle(task.getSummary());
	}

	/**
	 * Encodes the handle in the title, since init won't get called on this class.
	 */
	@Override
	public void save(Preferences prefs) {
		super.save(prefs);
		prefs.put(CTX_TITLE, getTitleForPersistance());
	}

	private String getTitleForPersistance() {
		return getTitle() + " (" + task.getHandleIdentifier() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getHandleFromPersistedTitle(String title) {
		int delimStart = title.lastIndexOf('(');
		int delimEnd = title.lastIndexOf(')');
		if (delimStart != -1 && delimEnd != -1) {
			return title.substring(delimStart + 1, delimEnd);
		} else {
			return null;
		}
	}

	@Override
	public String getComment() {
		return getComment(true);
	}

	public String getComment(boolean checkTaskRepository) {
		return TeamUiUtil.getComment(checkTaskRepository, task, getChangedResources());
	}

	@Override
	public void remove(IResource resource) {
		super.remove(resource);
	}

	@Override
	public void remove(IResource[] newResources) {
		super.remove(newResources);
	}

	@Override
	public void add(IDiff diff) {
		super.add(diff);
	}

	@Override
	public void add(IDiff[] diffs) {
		super.add(diffs);
	}

	@Override
	public void add(IResource[] newResources) throws CoreException {
		super.add(newResources);
	}

	public void restoreResources(IResource[] newResources) throws CoreException {
		super.add(newResources);
		setComment(getComment(false));
	}

	@Override
	public IResource[] getResources() {
		return super.getResources();
//		List<IResource> allResources = getAllResourcesInChangeContext();
//		return allResources.toArray(new IResource[allResources.size()]);
	}

	public IResource[] getChangedResources() {
		return super.getResources();
	}

	public List<IResource> getAllResourcesInChangeContext() {
		Set<IResource> allResources = new HashSet<IResource>();
		allResources.addAll(Arrays.asList(super.getResources()));
		if (Platform.isRunning() && ResourcesUiBridgePlugin.getDefault() != null && task.isActive()) {
			// TODO: if super is always managed correctly should remove
			// following line
			allResources.addAll(ResourcesUiBridgePlugin.getDefault().getInterestingResources(
					ContextCore.getContextManager().getActiveContext()));
		}
		return new ArrayList<IResource>(allResources);
	}

	/**
	 * TODO: unnessary check context?
	 */
	@Override
	public boolean contains(IResource local) {
		return super.contains(local); //getAllResourcesInChangeContext().contains(local);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CvsContextChangeSet && task != null) {
			CvsContextChangeSet changeSet = (CvsContextChangeSet) object;
			return task.equals(changeSet.getTask());
		} else {
			return super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		if (task != null) {
			return task.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public ITask getTask() {
		return task;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
//		if (adapter == ResourceMapping.class) {
//			return null;
//			return new ChangeSetResourceMapping(this);
//		}
		if (adapter == AbstractTask.class) {
			return task;
		} else if (adapter == AbstractTaskReference.class) {
			return new LinkedTaskInfo(getTask(), this);
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
