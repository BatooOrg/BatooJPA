package org.batoo.jpa.community.test.i225;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class RegularEmbeddedId implements Serializable {

	private int idInteger;

	private String idString;

	// get+ set + hashCode + equals
}
