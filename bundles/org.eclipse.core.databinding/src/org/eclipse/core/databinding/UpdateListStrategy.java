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
 *     IBM Corporation - initial API and implementation
 *     Tom Schindl<tom.schindl@bestsolution.at> - bugfix for 217940
 *     Matthew Hall - bug 262221, 270461
 *******************************************************************************/

package org.eclipse.core.databinding;

import java.util.Objects;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Customizes a {@link Binding} between two {@link IObservableList observable
 * lists}. The following behaviors can be customized via the strategy:
 * <ul>
 * <li>Conversion</li>
 * <li>Automatic processing</li>
 * </ul>
 * <p>
 * Conversion:<br>
 * When elements are added they can be {@link #convert(Object) converted} to the
 * destination element type.
 * </p>
 * <p>
 * Automatic processing:<br>
 * The processing to perform when the source observable changes. This behavior
 * is configured via policies provided on construction of the strategy (e.g.
 * {@link #POLICY_NEVER}, {@link #POLICY_ON_REQUEST}, {@link #POLICY_UPDATE}).
 * </p>
 *
 * @param <S> the type of the elements on the source side (i.e. the model side
 *            if this is a model-to-target update and the target side if this is
 *            a target-to-model update)
 * @param <D> the type of the elements on the destination side (i.e. the target
 *            side if this is a model-to-target update and the model side if
 *            this is a target-to-model update)
 * @see DataBindingContext#bindList(IObservableList, IObservableList,
 *      UpdateListStrategy, UpdateListStrategy)
 * @see IConverter
 * @since 1.0
 */
public class UpdateListStrategy<S, D> extends UpdateStrategy<S, D> {

	/**
	 * Policy constant denoting that the source observable's state should not be
	 * tracked and that the destination observable's state should never be
	 * updated.
	 */
	public static int POLICY_NEVER = notInlined(1);

	/**
	 * Policy constant denoting that the source observable's state should not be
	 * tracked, but that conversion and updating the destination observable's
	 * state should be performed when explicitly requested.
	 */
	public static int POLICY_ON_REQUEST = notInlined(2);

	/**
	 * Policy constant denoting that the source observable's state should be
	 * tracked, and that conversion and updating the destination observable's
	 * state should be performed automatically on every change of the source
	 * observable state.
	 */
	public static int POLICY_UPDATE = notInlined(8);

	/**
	 * Helper method allowing API evolution of the above constant values. The
	 * compiler will not inline constant values into client code if values are
	 * "computed" using this helper.
	 *
	 * @param i
	 *            an integer
	 * @return the same integer
	 */
	private static int notInlined(int i) {
		return i;
	}

	private int updatePolicy;

	protected boolean provideDefaults;

	/**
	 * Creates a new update list strategy for automatically updating the
	 * destination observable list whenever the source observable list changes.
	 * A default converter will be provided. The defaults can be changed by
	 * calling one of the setter methods.
	 */
	public UpdateListStrategy() {
		this(true, POLICY_UPDATE);
	}

	/**
	 * Creates a new update list strategy with a configurable update policy. A
	 * default converter will be provided. The defaults can be changed by
	 * calling one of the setter methods.
	 *
	 * @param updatePolicy
	 *            one of {@link #POLICY_NEVER}, {@link #POLICY_ON_REQUEST}, or
	 *            {@link #POLICY_UPDATE}
	 */
	public UpdateListStrategy(int updatePolicy) {
		this(true, updatePolicy);
	}

	/**
	 * Creates a new update list strategy with a configurable update policy. A
	 * default converter will be provided if <code>provideDefaults</code> is
	 * <code>true</code>. The defaults can be changed by calling one of the
	 * setter methods.
	 *
	 * @param provideDefaults
	 *            if <code>true</code>, default validators and a default
	 *            converter will be provided based on the observable list's
	 *            type.
	 * @param updatePolicy
	 *            one of {@link #POLICY_NEVER}, {@link #POLICY_ON_REQUEST}, or
	 *            {@link #POLICY_UPDATE}
	 */
	public UpdateListStrategy(boolean provideDefaults, int updatePolicy) {
		this.provideDefaults = provideDefaults;
		this.updatePolicy = updatePolicy;
	}

	/**
	 *
	 * @param source      source observable, to be used for its type
	 * @param destination destination observable, to be used for its type
	 */
	@SuppressWarnings("unchecked")
	protected void fillDefaults(IObservableList<? extends S> source, IObservableList<? super D> destination) {
		Object sourceType = source.getElementType();
		Object destinationType = destination.getElementType();
		if (provideDefaults && sourceType != null && destinationType != null) {
			if (converter == null) {
				setConverter((IConverter<S, D>) createConverter(sourceType, destinationType));
			}
		}
		if (converter != null) {
			if (sourceType != null) {
				checkAssignable(converter.getFromType(), sourceType,
						"converter does not convert from type " + sourceType); //$NON-NLS-1$
			}
			if (destinationType != null) {
				checkAssignable(destinationType, converter.getToType(),
						"converter does not convert to type " + destinationType); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @return the update policy
	 */
	public int getUpdatePolicy() {
		return updatePolicy;
	}

	/**
	 * Sets the converter to be invoked when converting added elements from the
	 * source element type to the destination element type.
	 * <p>
	 * If the converter throws any exceptions they are reported as validation
	 * errors, using the exception message.
	 *
	 * @param converter the new converter
	 * @return the receiver, to enable method call chaining
	 */
	public UpdateListStrategy<S, D> setConverter(IConverter<? super S, ? extends D> converter) {
		this.converter = converter;
		return this;
	}

	/**
	 * Adds the given element at the given index to the given observable list.
	 * Clients may extend but must call the super implementation.
	 *
	 * @param observableList the list to add to
	 * @param element        element to add
	 * @param index          insertion index
	 * @return a status
	 */
	protected IStatus doAdd(IObservableList<? super D> observableList, D element, int index) {
		try {
			observableList.add(index, element);
		} catch (Exception ex) {
			return logErrorWhileSettingValue(ex);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Removes the element at the given index from the given observable list.
	 * Clients may extend but must call the super implementation.
	 *
	 * @param observableList the list to remove from
	 * @param index          element index to remove
	 * @return a status
	 */
	protected IStatus doRemove(IObservableList<? super D> observableList, int index) {
		try {
			observableList.remove(index);
		} catch (Exception ex) {
			return logErrorWhileSettingValue(ex);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Returns whether ListBinding should call
	 * {@link #doMove(IObservableList, int, int)} and
	 * {@link #doReplace(IObservableList, int, Object)} instead of paired calls
	 * to {@link #doRemove(IObservableList, int)} and
	 * {@link #doAdd(IObservableList, Object, int)}. The default implementation
	 * returns true for this class and false for subclasses.
	 * <p>
	 * This method is provided for the benefit of subclasses which extend the
	 * {@link #doAdd(IObservableList, Object, int)} and
	 * {@link #doRemove(IObservableList, int)} methods. The
	 * {@link #doMove(IObservableList, int, int)} and
	 * {@link #doReplace(IObservableList, int, Object)} methods were added in
	 * version 1.2 so that logically related remove() and add() operations could
	 * be combined into a single method call. However to ensure that custom
	 * behavior is not lost in existing code, {@link ListBinding} is advised by
	 * this method whether these new methods may be used.
	 * <p>
	 * To enable use of these methods in subclasses, override this method to
	 * return true.
	 *
	 * @return whether ListBinding should call doMove() and doReplace() instead
	 *         of paired calls of doRemove() and doAdd()
	 * @since 1.2
	 * @noreference This method is not intended to be referenced by clients.
	 */
	protected boolean useMoveAndReplace() {
		return getClass() == UpdateListStrategy.class;
	}

	/**
	 * Moves the element in the observable list located at the given old index to
	 * the given new index.
	 *
	 * @param observableList the list to manipulate
	 * @param oldIndex       current element index
	 * @param newIndex       new element index
	 * @return a status
	 * @since 1.2
	 */
	protected IStatus doMove(IObservableList<? super D> observableList, int oldIndex, int newIndex) {
		try {
			observableList.move(oldIndex, newIndex);
		} catch (Exception ex) {
			return logErrorWhileSettingValue(ex);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Replaces the element in the observable list located at the given index to
	 * with the given element.
	 *
	 * @param observableList the list to manipulate
	 * @param index          index of the existing element
	 * @param element        new element for the given index
	 * @return a status
	 * @since 1.2
	 */
	protected IStatus doReplace(IObservableList<? super D> observableList, int index, D element) {
		try {
			observableList.set(index, element);
		} catch (Exception ex) {
			return logErrorWhileSettingValue(ex);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Convenience method that creates an {@link UpdateValueStrategy} with the given
	 * converter. It uses {@link #POLICY_UPDATE}.
	 *
	 * @param converter the converter
	 * @return the update strategy
	 * @since 1.8
	 */
	public static <S, D> UpdateListStrategy<S, D> create(IConverter<S, D> converter) {
		Objects.requireNonNull(converter);
		return new UpdateListStrategy<S, D>().setConverter(converter);
	}

	/**
	 * Convenience method that creates an update strategy that never updates its
	 * observables, using {@link #POLICY_NEVER} and no defaults.
	 *
	 * @return the update strategy
	 * @since 1.8
	 */
	public static <S, D> UpdateListStrategy<S, D> never() {
		return new UpdateListStrategy<>(false, POLICY_NEVER);
	}
}
