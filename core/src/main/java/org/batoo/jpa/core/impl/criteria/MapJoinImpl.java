package org.batoo.jpa.core.impl.criteria;
///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.batoo.jpa.core.impl.criteria;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.persistence.criteria.Expression;
//import javax.persistence.criteria.MapJoin;
//import javax.persistence.criteria.Path;
//import javax.persistence.metamodel.MapAttribute;
//
///**
// * The type of the result of joining to a collection over an association or element collection that has been specified as a
// * <code>java.util.Map</code>.
// * 
// * @param <Z>
// *            the source type of the join
// * @param <K>
// *            the type of the target Map key
// * @param <V>
// *            the type of the target Map value * @author hceylan
// * @since $version
// */
//public class MapJoinImpl<Z, K, V> extends PluralJoinImpl<Z, Map<K, V>, V> implements MapJoin<Z, K, V> {
//
//	/**
//	 * {@inheritDoc}
//	 * 
//	 */
//	@Override
//	public Expression<Entry<K, V>> entry() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * 
//	 */
//	@Override
//	public MapAttribute<? super Z, K, V> getModel() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * 
//	 */
//	@Override
//	public Path<K> key() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * 
//	 */
//	@Override
//	public Path<V> value() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
// }
