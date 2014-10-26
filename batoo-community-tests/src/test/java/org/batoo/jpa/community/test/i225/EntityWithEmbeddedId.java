package org.batoo.jpa.community.test.i225;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class EntityWithEmbeddedId {

	@EmbeddedId
	private RegularEmbeddedId embeddedId;
	private String anyString;

	// get + set + hashCode + equals
}
