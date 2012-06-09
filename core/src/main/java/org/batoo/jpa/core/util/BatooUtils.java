/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class BatooUtils {

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 * 
	 * @param str
	 *            string to indent
	 * @return the indented string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static String indent(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "\t" + str.replaceAll("\n", "\n\t");
	}

}
