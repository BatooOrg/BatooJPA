/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.batoo.jpa.community.test.i103;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Administrator
 */
@Entity
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
	@NamedQuery(name = "Users.findByUserName", query = "SELECT u FROM Users u WHERE u.userName = :userName"),
	@NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
	@NamedQuery(name = "Users.findByTheme", query = "SELECT u FROM Users u WHERE u.theme = :theme"),
	@NamedQuery(name = "Users.findByActive", query = "SELECT u FROM Users u WHERE u.active = :active"),
	@NamedQuery(name = "Users.findByActiveStatusChangedDt", query = "SELECT u FROM Users u WHERE u.activeStatusChangedDt = :activeStatusChangedDt"),
	@NamedQuery(name = "Users.findByCreatedDt", query = "SELECT u FROM Users u WHERE u.createdDt = :createdDt"),
	@NamedQuery(name = "Users.findByLastLoginDt", query = "SELECT u FROM Users u WHERE u.lastLoginDt = :lastLoginDt"),
	@NamedQuery(name = "Users.findByLastLogoutDt", query = "SELECT u FROM Users u WHERE u.lastLogoutDt = :lastLogoutDt"),
	@NamedQuery(name = "Users.findByLoggedInViaMobile", query = "SELECT u FROM Users u WHERE u.loggedInViaMobile = :loggedInViaMobile"),
	@NamedQuery(name = "Users.findByPasswordLastChangedDt", query = "SELECT u FROM Users u WHERE u.passwordLastChangedDt = :passwordLastChangedDt") })
@SuppressWarnings("javadoc")
public class Users implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "USER_NAME")
	private String userName;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "PASSWORD")
	private String password;

	@Size(min = 1, max = 25)
	@Column(name = "THEME")
	private String theme;

	@Column(name = "ACTIVE")
	private Character active;

	@Column(name = "ACTIVE_STATUS_CHANGED_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeStatusChangedDt;

	@Column(name = "CREATED_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDt;

	@Column(name = "LAST_LOGIN_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginDt;

	@Column(name = "LAST_LOGOUT_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogoutDt;

	@Column(name = "LOGGED_IN_VIA_MOBILE")
	private Character loggedInViaMobile;

	@Column(name = "PASSWORD_LAST_CHANGED_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date passwordLastChangedDt;

	public Users() {
		super();
	}

	public Users(String userName) {
		super();

		this.userName = userName;
	}

	public Users(String userName, String password) {
		super();

		this.userName = userName;
		this.password = password;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Users)) {
			return false;
		}
		final Users other = (Users) object;
		if (((this.userName == null) && (other.userName != null)) || ((this.userName != null) && !this.userName.equals(other.userName))) {
			return false;
		}
		return true;
	}

	public Character getActive() {
		return this.active;
	}

	public Date getActiveStatusChangedDt() {
		return this.activeStatusChangedDt;
	}

	public Date getCreatedDt() {
		return this.createdDt;
	}

	public Date getLastLoginDt() {
		return this.lastLoginDt;
	}

	public Date getLastLogoutDt() {
		return this.lastLogoutDt;
	}

	public Character getLoggedInViaMobile() {
		return this.loggedInViaMobile;
	}

	public String getPassword() {
		return this.password;
	}

	public Date getPasswordLastChangedDt() {
		return this.passwordLastChangedDt;
	}

	public String getTheme() {
		return this.theme;
	}

	public String getUserName() {
		return this.userName;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.userName != null ? this.userName.hashCode() : 0);
		return hash;
	}

	public void setActive(Character active) {
		this.active = active;
	}

	public void setActiveStatusChangedDt(Date activeStatusChangedDt) {
		this.activeStatusChangedDt = activeStatusChangedDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public void setLastLoginDt(Date lastLoginDt) {
		this.lastLoginDt = lastLoginDt;
	}

	public void setLastLogoutDt(Date lastLogoutDt) {
		this.lastLogoutDt = lastLogoutDt;
	}

	public void setLoggedInViaMobile(Character loggedInViaMobile) {
		this.loggedInViaMobile = loggedInViaMobile;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordLastChangedDt(Date passwordLastChangedDt) {
		this.passwordLastChangedDt = passwordLastChangedDt;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "jpa.entities.Users[ userName=" + this.userName + " ]";
	}
}
