/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Delegates to contained contexts. TODO: should info be propagated proportionally to number of taskscapes?
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @author David Green bug 257977 isInteresting
 */
public class CompositeInteractionContext implements IInteractionContext {

	protected Map<String, InteractionContext> contexts = new HashMap<String, InteractionContext>();

	protected IInteractionElement activeNode = null;

	private final IInteractionContextScaling contextScaling;

	public String contentLimitedTo = null;

	public CompositeInteractionContext(IInteractionContextScaling contextScaling) {
		this.contextScaling = contextScaling;
	}

	public IInteractionElement addEvent(InteractionEvent event) {
		List<InteractionContextElement> nodes = new ArrayList<InteractionContextElement>();
		for (InteractionContext context : contexts.values()) {
			InteractionContextElement info = (InteractionContextElement) context.parseEvent(event);
			nodes.add(info);
		}
		CompositeContextElement compositeNode = new CompositeContextElement(event.getStructureHandle(), nodes,
				contextScaling);
		return compositeNode;
	}

	public IInteractionElement get(String handle) {
		if (handle == null || contexts.values().size() == 0) {
			return null;
		}
		List<InteractionContextElement> nodes = new ArrayList<InteractionContextElement>();
		for (InteractionContext taskscape : contexts.values()) {
			InteractionContextElement node = (InteractionContextElement) taskscape.get(handle);
			if (node != null) {
				nodes.add(node);
			}
		}
		CompositeContextElement composite = new CompositeContextElement(handle, nodes, contextScaling);
		return composite;
	}

	public List<IInteractionElement> getLandmarks() {
		Set<IInteractionElement> landmarks = new HashSet<IInteractionElement>();
		for (InteractionContext taskscape : contexts.values()) {
			for (IInteractionElement concreteNode : taskscape.getLandmarks()) {
				if (concreteNode != null) {
					landmarks.add(get(concreteNode.getHandleIdentifier()));
				}
			}
		}
		return new ArrayList<IInteractionElement>(landmarks);
	}

	public List<IInteractionElement> getInteresting() {
		Set<IInteractionElement> landmarks = new HashSet<IInteractionElement>();
		for (InteractionContext context : contexts.values()) {
			for (IInteractionElement concreteNode : context.getInteresting()) {
				if (concreteNode != null) {
					landmarks.add(get(concreteNode.getHandleIdentifier()));
				}
			}
		}
		return new ArrayList<IInteractionElement>(landmarks);
	}

	public boolean isInteresting(String elementHandle) {
		for (InteractionContext context : contexts.values()) {
			if (context.isInteresting(elementHandle)) {
				return true;
			}
		}
		return false;
	}

	public void setActiveElement(IInteractionElement activeElement) {
		this.activeNode = activeElement;
	}

	public IInteractionElement getActiveNode() {
		return activeNode;
	}

	public void delete(IInteractionElement node) {
		for (InteractionContext taskscape : contexts.values()) {
			taskscape.delete(node);
		}
	}

	public void delete(Collection<IInteractionElement> nodes) {
		for (InteractionContext context : contexts.values()) {
			context.delete(nodes);
		}
	}

	public void clear() {
		for (InteractionContext taskscape : contexts.values()) {
			taskscape.reset();
		}
	}

	public Map<String, InteractionContext> getContextMap() {
		return contexts;
	}

	public List<IInteractionElement> getAllElements() {
		Set<IInteractionElement> nodes = new HashSet<IInteractionElement>();
		for (InteractionContext context : contexts.values()) {
			for (IInteractionElement concreteNode : context.getAllElements()) {
				nodes.add(get(concreteNode.getHandleIdentifier()));
			}
		}
		return new ArrayList<IInteractionElement>(nodes);
	}

	/**
	 * TODO: sort by date?
	 */
	public List<InteractionEvent> getInteractionHistory() {
		Set<InteractionEvent> events = new HashSet<InteractionEvent>();
		for (InteractionContext taskscape : contexts.values()) {
			events.addAll(taskscape.getInteractionHistory());
		}
		return new ArrayList<InteractionEvent>(events);
	}

	public void updateElementHandle(IInteractionElement element, String newHandle) {
		for (InteractionContext context : contexts.values()) {
			context.updateElementHandle(element, newHandle);
		}
		element.setHandleIdentifier(newHandle);
	}

	/**
	 * Composite contexts do not have a unique handle identifier.
	 * 
	 * @return null if no unique handle
	 */
	public String getHandleIdentifier() {
		if (contexts.values().size() == 1) {
			return contexts.keySet().iterator().next();
		} else {
			return null;
		}
	}

	public IInteractionContextScaling getScaling() {
		return contextScaling;
	}

	public String getContentLimitedTo() {
		return contentLimitedTo;
	}

	public void setContentLimitedTo(String contentLimitedTo) {
		this.contentLimitedTo = contentLimitedTo;
	}

	public void addEvents(IInteractionContext otherContext) {
		for (InteractionContext context : contexts.values()) {
			context.addEvents(otherContext);
		}
	}
}
