/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *   IBM Corporation - ongoing development
 *   Lars Vogel <Lars.Vogel@gmail.com> - Bug 422702
 ******************************************************************************/
package org.eclipse.e4.ui.css.core.impl.dom;

import java.util.Comparator;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A wrapper that holds a reference to the styles defined in a CSS rule block,
 * together with all the information needed to calculate a matching selector's
 * precedence.
 */
final class StyleWrapper {

	private static class StyleWrapperComparator implements Comparator<StyleWrapper> {

		@Override
		public int compare(final StyleWrapper object1, final StyleWrapper object2) {
			int result = 0;
			StyleWrapper wrapper1 = object1;
			StyleWrapper wrapper2 = object2;
			if (wrapper1.specificity > wrapper2.specificity) {
				result = 1;
			} else if (wrapper1.specificity < wrapper2.specificity) {
				result = -1;
			} else if (wrapper1.position > wrapper2.position) {
				result = 1;
			} else if (wrapper1.position < wrapper2.position) {
				result = -1;
			}
			return result;
		}
	}

	/**
	 * A comparator for {@link StyleWrapper}s.
	 */
	public static final StyleWrapperComparator COMPARATOR = new StyleWrapperComparator();

	public final CSSStyleDeclaration style;
	public final int specificity;
	public final int position;

	public StyleWrapper(CSSStyleDeclaration style, int specificity,
			int position) {
		this.style = style;
		this.specificity = specificity;
		this.position = position;
	}
}
