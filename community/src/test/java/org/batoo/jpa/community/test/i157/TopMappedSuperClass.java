package org.batoo.jpa.community.test.i157;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class TopMappedSuperClass {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;

	@Version
	@Column(nullable = false)
	protected Long timeStamp = null;
}
