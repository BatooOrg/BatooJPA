package org.batoo.jpa.common.impl.log;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringStyle;

public class MultiLineToStringStyle extends ToStringStyle {

	public MultiLineToStringStyle() {
		super();
		this.setContentStart("[");
		this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
		this.setFieldSeparatorAtStart(true);
		this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
	}
}
