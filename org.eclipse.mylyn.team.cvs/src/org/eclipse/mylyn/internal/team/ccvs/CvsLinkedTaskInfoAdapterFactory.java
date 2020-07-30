/*******************************************************************************
 * Copyright (c) 2004, 2010 Mylyn project committers and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.team.ui.AbstractTaskReference;
import org.eclipse.mylyn.team.ui.IContextChangeSet;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileRevision;
import org.eclipse.team.internal.ccvs.core.mapping.CVSCheckedInChangeSet;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.RemoteResource;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;

/**
 * @author Eugene Kuleshov
 */
public class CvsLinkedTaskInfoAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_TYPES = new Class[] { AbstractTaskReference.class };

	@SuppressWarnings({ "rawtypes" })
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@SuppressWarnings({ "rawtypes" })
	public Object getAdapter(Object object, Class adapterType) {
		if (!AbstractTaskReference.class.equals(adapterType)) {
			return null;
		}

		return adaptFromComment(object);
	}

	private AbstractTaskReference adaptFromComment(Object object) {
		String comment = getCommentForElement(object);
		if (comment == null) {
			return null;
		}

		long timestamp = 0;
		if (object instanceof CVSFileRevision) {
			timestamp = ((CVSFileRevision) object).getTimestamp();
		}

		IResource resource = getResourceForElement(object);
		if (resource != null) {
			TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(resource);
			if (repository != null) {
				return new LinkedTaskInfo(repository.getRepositoryUrl(), null, null, comment, timestamp);
			}
		}

		return new LinkedTaskInfo(null, null, null, comment);
	}

	private static String getCommentForElement(Object element) {
		if (element instanceof IContextChangeSet) {
			return ((IContextChangeSet) element).getComment(false);
		} else if (element instanceof DiffChangeSet) {
			return ((DiffChangeSet) element).getComment();
		} else if (element instanceof LogEntry) {
			return ((LogEntry) element).getComment();
		} else if (element instanceof IFileRevision) {
			return ((IFileRevision) element).getComment();
		} else if (element instanceof CVSCheckedInChangeSet) {
			return ((CVSCheckedInChangeSet) element).getComment();
		}
		return null;
	}

	private static IResource getResourceForElement(Object element) {
		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			IResourceVariant resourceVariant = (IResourceVariant) adaptable.getAdapter(IResourceVariant.class);
			if (resourceVariant != null && resourceVariant instanceof RemoteResource) {
				RemoteResource remoteResource = (RemoteResource) resourceVariant;
				// TODO is there a better way then iterating trough all projects?
				String path = remoteResource.getRepositoryRelativePath();
				if (path != null) {
					for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
						if (project.isAccessible()) {
							ICVSResource cvsResource = CVSWorkspaceRoot.getCVSFolderFor(project);
							try {
								String repositoryRelativePath = cvsResource.getRepositoryRelativePath();
								if (repositoryRelativePath != null && path.startsWith(repositoryRelativePath)) {
									return project;
								}
							} catch (CVSException ex) {
								// ignore
							}
						}
					}
				}
			}
		}

		// TODO any other resource types?

		return null;
	}

}
