/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.batoo.jpa.community.test.querydsl;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.PROPERTY)
@SuppressWarnings("javadoc")
public class BookMark implements Serializable {

	private static final long serialVersionUID = 8027009758015834551L;

	private Long page;

	private String comment;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}

		final BookMark bookMark = (BookMark) o;

		if (this.page != null ? !this.page.equals(bookMark.page) : bookMark.page != null) {
			return false;
		}

		return true;
	}

	@Basic
	public String getComment() {
		return this.comment;
	}

	@Basic
	public Long getPage() {
		return this.page;
	}

	@Override
	public int hashCode() {
		return this.page != null ? this.page.hashCode() : 0;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setPage(Long page) {
		this.page = page;
	}
}
