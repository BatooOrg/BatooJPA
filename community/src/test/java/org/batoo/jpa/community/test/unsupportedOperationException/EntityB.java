package org.batoo.jpa.community.test.unsupportedOperationException;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class EntityB {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id = null;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = EntityA.class, mappedBy = "entityB")
	private Set<EntityA> entitiesA;

	@Basic
	@Column(name = "CODE", nullable = false)
	private String code;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public Set<EntityA> getEntitiesA() {
		return entitiesA;
	}
	
	public void setEntitiesA(Set<EntityA> entitiesA) {
		this.entitiesA = entitiesA;
	}
}
