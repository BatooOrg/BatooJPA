package org.batoo.jpa.community.test.i169;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class EntityPointToAbstractEntity {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = AbstractEntity.class)
	@JoinColumn(name = "ID_ABSTRACT_ENTITY", referencedColumnName = "ID", nullable = false)
	private AbstractEntity abstractEntity;
	
	public Long getId() {
		return id;
	}
	
	public void setAbstractEntity(AbstractEntity abstractEntity) {
		this.abstractEntity = abstractEntity;
	}
}
