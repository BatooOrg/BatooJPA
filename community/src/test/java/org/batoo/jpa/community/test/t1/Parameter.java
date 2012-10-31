/*
 * Copyright (c) 2012 - Batoo Software import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
Software Foundation.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Table(name = "tbl_service_parameters2")
public class Parameter implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "param_id")
	private long parameterID;

	@Column(name = "name")
	private String name;

	/**
	 * Returns the name of the Parameter.
	 * 
	 * @return the name of the Parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the parameterID of the Parameter.
	 * 
	 * @return the parameterID of the Parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public long getParameterID() {
		return this.parameterID;
	}

	/**
	 * Sets the name of the Parameter.
	 * 
	 * @param name
	 *            the name to set for Parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the parameterID of the Parameter.
	 * 
	 * @param parameterID
	 *            the parameterID to set for Parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setParameterID(long parameterID) {
		this.parameterID = parameterID;
	}
}
