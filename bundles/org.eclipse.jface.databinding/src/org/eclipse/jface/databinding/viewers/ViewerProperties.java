/*******************************************************************************
 * Copyright (c) 2008, 2015 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 264286
 *     Ovidio Mallo - bug 270494
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.jface.internal.databinding.viewers.SelectionProviderMultipleSelectionProperty;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderSingleSelectionProperty;
import org.eclipse.jface.internal.databinding.viewers.StructuredViewerFiltersProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerCheckedElementsProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerInputProperty;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * A factory for creating properties of JFace {@link Viewer viewers}.
 *
 * @deprecated This class has replaced by the class
 *             {@link org.eclipse.jface.databinding.viewers.typed.ViewerProperties}.
 *             That class creates typed property objects, while this class
 *             creates raw property objects. This class has been preserved for
 *             backwards compatibility reasons.
 *
 * @since 1.3
 */
@Deprecated
@SuppressWarnings({ "rawtypes" })
public class ViewerProperties {
	/**
	 * Returns a set property for observing the checked elements of a
	 * {@link CheckboxTableViewer}, {@link CheckboxTreeViewer} or
	 * {@link ICheckable}.
	 *
	 * @param elementType
	 *            the element type of the returned property
	 *
	 * @return a set property for observing the checked elements of a
	 *         {@link CheckboxTableViewer}, {@link CheckboxTreeViewer} or
	 *         {@link ICheckable}.
	 */
	public static IViewerSetProperty checkedElements(Object elementType) {
		return new ViewerCheckedElementsProperty(elementType);
	}

	/**
	 * Returns a value property for observing the input of a
	 * {@link StructuredViewer}.
	 *
	 * @return a value property for observing the input of a
	 *         {@link StructuredViewer}.
	 */
	public static IViewerSetProperty filters() {
		return new StructuredViewerFiltersProperty();
	}

	/**
	 * Returns a value property for observing the input of a {@link Viewer}.
	 *
	 * @return a value property for observing the input of a {@link Viewer}.
	 */
	public static IViewerValueProperty input() {
		return new ViewerInputProperty();
	}

	/**
	 * Returns a list property for observing the multiple selection of an
	 * {@link ISelectionProvider}.
	 *
	 * @return a list property for observing the multiple selection of an
	 *         {@link ISelectionProvider}.
	 */
	public static IViewerListProperty multipleSelection() {
		return new SelectionProviderMultipleSelectionProperty(false);
	}

	/**
	 * Returns a list property for observing the multiple <i>post</i> selection
	 * of an {@link IPostSelectionProvider}.
	 *
	 * @return a list property for observing the multiple <i>post</i> selection
	 *         of an {@link IPostSelectionProvider}.
	 *
	 * @since 1.4
	 */
	public static IViewerListProperty multiplePostSelection() {
		return new SelectionProviderMultipleSelectionProperty(true);
	}

	/**
	 * Returns a value property for observing the single selection of a
	 * {@link ISelectionProvider}.
	 *
	 * @return a value property for observing the single selection of a
	 *         {@link ISelectionProvider}.
	 */
	public static IViewerValueProperty singleSelection() {
		return new SelectionProviderSingleSelectionProperty(false);
	}

	/**
	 * Returns a value property for observing the single <i>post</i> selection
	 * of a {@link IPostSelectionProvider}.
	 *
	 * @return a value property for observing the single <i>post</i> selection
	 *         of a {@link IPostSelectionProvider}.
	 *
	 * @since 1.4
	 */
	public static IViewerValueProperty singlePostSelection() {
		return new SelectionProviderSingleSelectionProperty(true);
	}
}
