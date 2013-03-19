package org.batoo.jpa.community.test.unsupportedOperationException;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

@Entity
public class EntityA {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id = null;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = EntityB.class)
	@JoinColumn(name = "ID_ENTITYB", referencedColumnName = "ID")
	private EntityB entityB;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ENTITYA_NAME", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyEnumerated(EnumType.STRING)
	@MapKeyColumn(name = "LOCALE", nullable = false)
	@Column(name = "TRANSLATION", nullable = false)
	private Map<FieldLocale, String> name;
	
	public void setEntityB(EntityB entityB) {
		this.entityB = entityB;
	}
	
	public EntityB getEntityB() {
		return entityB;
	}
	
	public void setName(Map<FieldLocale, String> name) {
		this.name = name;
	}
	
	public Map<FieldLocale, String> getName() {
		return name;
	}
}
