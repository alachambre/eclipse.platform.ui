/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.viewers;

import java.util.EventObject;

import org.eclipse.jface.util.Assert;

/**
 * Event object describing an open which may be generated from a 
 * selection or default selection event. The source of these
 * events is a viewer.
 *
 * @see IOpenListener
 */
public class OpenEvent extends EventObject {

	/**
	 * The selection.
	 */
	protected ISelection selection;
/**
 * Creates a new event for the given source and selection.
 *
 * @param source the viewer
 * @param selection the selection
 */
public OpenEvent(Viewer source, ISelection selection) {
	super(source);
	Assert.isNotNull(selection);
	this.selection = selection;
}
/**
 * Returns the selection.
 *
 * @return the selection
 */
public ISelection getSelection() {
	return selection;
}
/**
 * Returns the viewer that is the source of this event.
 *
 * @return the originating viewer
 */
public Viewer getViewer() {
	return (Viewer) getSource();
}
}
