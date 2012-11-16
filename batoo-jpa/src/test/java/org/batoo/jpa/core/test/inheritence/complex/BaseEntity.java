package org.batoo.jpa.core.test.inheritence.complex;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@MappedSuperclass
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedOn;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final BaseEntity other = (BaseEntity) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the createdOn of the BaseEntity.
	 * 
	 * @return the createdOn of the BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public Date getCreatedOn() {
		return this.createdOn;
	}

	/**
	 * @return the display text
	 * 
	 * @since 2.0.0
	 */
	@Transient
	@Access(AccessType.PROPERTY)
	public abstract String getDisplayText();

	/**
	 * Returns the id of the BaseEntity.
	 * 
	 * @return the id of the BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Returns the modifiedOn of the BaseEntity.
	 * 
	 * @return the modifiedOn of the BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public Date getModifiedOn() {
		return this.modifiedOn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.getDisplayText() == null) ? 0 : this.getDisplayText().hashCode());
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	@PrePersist
	public void initTimeStamps() {
		if (this.createdOn == null) {
			this.createdOn = new Date();
		}

		this.modifiedOn = this.createdOn;
	}

	/**
	 * Sets the createdOn of the BaseEntity.
	 * 
	 * @param createdOn
	 *            the createdOn to set for BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * Sets the id of the BaseEntity.
	 * 
	 * @param id
	 *            the id to set for BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the modifiedOn of the BaseEntity.
	 * 
	 * @param modifiedOn
	 *            the modifiedOn to set for BaseEntity
	 * 
	 * @since 2.0.0
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}
