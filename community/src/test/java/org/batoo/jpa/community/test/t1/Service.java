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
package org.batoo.jpa.community.test.t1;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Table(name = "tbl_service2")
public class Service implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "service_id")
	private long serviceID;

	@Column(name = "name")
	private String name;

	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumn(name = "service_id", referencedColumnName = "service_id")
	private Set<Parameter> parameters;

	/**
	 * Returns the name of the Service.
	 * 
	 * @return the name of the Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the parameters of the Service.
	 * 
	 * @return the parameters of the Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<Parameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Returns the serviceID of the Service.
	 * 
	 * @return the serviceID of the Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public long getServiceID() {
		return this.serviceID;
	}

	/**
	 * Sets the name of the Service.
	 * 
	 * @param name
	 *            the name to set for Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the parameters of the Service.
	 * 
	 * @param parameters
	 *            the parameters to set for Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setParameters(Set<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets the serviceID of the Service.
	 * 
	 * @param serviceID
	 *            the serviceID to set for Service
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setServiceID(long serviceID) {
		this.serviceID = serviceID;
	}
}
