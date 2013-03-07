package org.batoo.jpa.community.test.i163;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class User {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;

	@Version
	@Column(nullable = false)
	protected Long timeStamp = null;
	
	@Basic
	@Column(nullable = false)
	private String login;
}
