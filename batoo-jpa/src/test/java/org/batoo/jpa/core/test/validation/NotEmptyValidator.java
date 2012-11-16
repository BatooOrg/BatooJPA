/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.test.validation;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check a credit card number through the Luhn algorithm.
 * 
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Collection<?>> {

	/**
	 * 
	 * @since $version
	 */
	public NotEmptyValidator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize(NotEmpty constraintAnnotation) {

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
		return ((value != null) && (value.size() > 0));
	}
}
