/*******************************************************************************
 * Copyright (c) 2006 Gunnar Wagenknecht, Truition and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.team;

import org.eclipse.core.resources.IResource;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * Integrates an Eclipse Team repository with Mylar.
 * 
 * @author Gunnar Wagenknecht
 * @author Mik Kersten
 */
public abstract class AbstractTeamRepositoryProvider {

	/**
	 * Return the change set collector that manages the active change set for
	 * the participant associated with this capability. A <code>null</code> is
	 * returned if active change sets are not supported. The default is to
	 * return <code>null</code>. This method must be overridden by subclasses
	 * that support active change sets.
	 * 
	 * @return the change set collector that manages the active change set for
	 *         the participant associated with this capability or
	 *         <code>null</code> if active change sets are not supported.
	 */
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return null;
	}

	/**
	 * Determines if the team provider manages at least one of the resources and
	 * at least one of the resources has an 'outgoing' state (locally changed).
	 * If this method returns <code>true</code>, the team provider may be
	 * later asked to {@link #commit} them. The set of resources may contain
	 * resources from projects that are not managed by your provider or not
	 * managed at all.
	 * 
	 * @param resources
	 * @return <code>true</code> if the team provider manages at least one of
	 *         the resources or <code>false</code> otherwise.
	 */
	public boolean hasOutgoingChanges(IResource[] resources) {
		return false;
	}

	/**
	 * Asks the team provider to commit a set of resources that may be managed
	 * by the team provider. It is up to the team provider to only operate on
	 * resources that are being managed by it. The set of resources may contain
	 * resources from projects that are not managed by your provider or not
	 * managed at all.
	 * 
	 * @param resources
	 *            Set of resources that need to be committed
	 */
	public void commit(IResource[] resources) {
	}
}