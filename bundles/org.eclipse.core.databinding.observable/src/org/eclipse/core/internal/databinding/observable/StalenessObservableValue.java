/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boris Bokowski, IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 212468
 *     Stefan Xenos <sxenos@gmail.com> - Bug 335792
 *******************************************************************************/
package org.eclipse.core.internal.databinding.observable;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;

/**
 * An observable value that tracks the staleness of an {@link IObservable}.
 *
 * @since 1.1
 */
public class StalenessObservableValue extends AbstractObservableValue<Boolean> {

	private class MyListener implements IChangeListener, IStaleListener {
		@Override
		public void handleChange(ChangeEvent event) {
			if (stale && !event.getObservable().isStale()) {
				stale = false;
				fireValueChange(Diffs.createValueDiff(Boolean.TRUE,
						Boolean.FALSE));
			}
		}

		@Override
		public void handleStale(StaleEvent staleEvent) {
			if (!stale) {
				stale = true;
				fireValueChange(Diffs.createValueDiff(Boolean.FALSE,
						Boolean.TRUE));
			}
		}
	}

	private IObservable tracked;
	private boolean stale;
	private MyListener listener = new MyListener();

	/**
	 * Constructs a StalenessObservableValue that tracks the staleness of the
	 * given {@link IObservable}.
	 *
	 * @param observable
	 *            the observable to track
	 */
	public StalenessObservableValue(IObservable observable) {
		super(observable.getRealm());
		this.tracked = observable;
		this.stale = observable.isStale();
		tracked.addChangeListener(listener);
		tracked.addStaleListener(listener);
	}

	@Override
	protected Boolean doGetValue() {
		return tracked.isStale();
	}

	@Override
	public Object getValueType() {
		return Boolean.TYPE;
	}

	@Override
	public synchronized void dispose() {
		if (tracked != null) {
			tracked.removeChangeListener(listener);
			tracked.removeStaleListener(listener);
			tracked = null;
			listener = null;
		}
		super.dispose();
	}

}
