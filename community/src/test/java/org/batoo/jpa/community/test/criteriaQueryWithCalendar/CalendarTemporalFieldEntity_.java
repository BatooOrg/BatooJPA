package org.batoo.jpa.community.test.criteriaQueryWithCalendar;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(value = CalendarTemporalFieldEntity.class)
public class CalendarTemporalFieldEntity_ {
	public static volatile SingularAttribute<CalendarTemporalFieldEntity, Long> id;
	public static volatile SingularAttribute<CalendarTemporalFieldEntity, Calendar> calendar;
	public static volatile SingularAttribute<CalendarTemporalFieldEntity, Date> date;
}
