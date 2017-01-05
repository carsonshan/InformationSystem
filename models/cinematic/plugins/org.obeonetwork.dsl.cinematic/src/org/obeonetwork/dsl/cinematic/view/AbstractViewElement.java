/*******************************************************************************
 * Copyright (c) 2012, 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.cinematic.view;

import org.eclipse.emf.common.util.EList;
import org.obeonetwork.dsl.cinematic.NamedElement;
import org.obeonetwork.dsl.cinematic.toolkits.Widget;
import org.obeonetwork.dsl.environment.BoundableElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract View Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getWidget <em>Widget</em>}</li>
 *   <li>{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getActions <em>Actions</em>}</li>
 *   <li>{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getEvents <em>Events</em>}</li>
 *   <li>{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getLabel <em>Label</em>}</li>
 * </ul>
 *
 * @see org.obeonetwork.dsl.cinematic.view.ViewPackage#getAbstractViewElement()
 * @model abstract="true"
 * @generated
 */
public interface AbstractViewElement extends NamedElement, BoundableElement {
	/**
	 * Returns the value of the '<em><b>Widget</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Widget</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Widget</em>' reference.
	 * @see #setWidget(Widget)
	 * @see org.obeonetwork.dsl.cinematic.view.ViewPackage#getAbstractViewElement_Widget()
	 * @model
	 * @generated
	 */
	Widget getWidget();

	/**
	 * Sets the value of the '{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getWidget <em>Widget</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Widget</em>' reference.
	 * @see #getWidget()
	 * @generated
	 */
	void setWidget(Widget value);

	/**
	 * Returns the value of the '<em><b>Actions</b></em>' containment reference list.
	 * The list contents are of type {@link org.obeonetwork.dsl.cinematic.view.ViewAction}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Actions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Actions</em>' containment reference list.
	 * @see org.obeonetwork.dsl.cinematic.view.ViewPackage#getAbstractViewElement_Actions()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<ViewAction> getActions();

	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link org.obeonetwork.dsl.cinematic.view.ViewEvent}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see org.obeonetwork.dsl.cinematic.view.ViewPackage#getAbstractViewElement_Events()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<ViewEvent> getEvents();

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see org.obeonetwork.dsl.cinematic.view.ViewPackage#getAbstractViewElement_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link org.obeonetwork.dsl.cinematic.view.AbstractViewElement#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

} // AbstractViewElement
