[![Build Status](https://secure.travis-ci.org/BatooOrg/BatooJPA.png)](http://travis-ci.org/BatooOrg/BatooJPA) Welcome to Batoo JPA - [http://batoo.org](http://batoo.org)
___
# FASTEST JPA IMPLEMENTATION #

Batoo JPA is the fastest JPA Implementation by far.

It is 10 ~ 20 times faster then the leading JPA providers.

## LICENSE ##
Copyright (c) 2012-2013, Batu Alp Ceylan

This copyrighted material is made available to anyone wishing to use, modify,
copy, or redistribute it subject to the terms and conditions of the GNU
Lesser General Public License, as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
for more details.

You should have received a copy of the GNU Lesser General Public License
along with this distribution; if not, write to:
Free Software Foundation, Inc.
51 Franklin Street, Fifth Floor
Boston, MA  02110-1301  USA

## FEATURES ##
- Full implementation of JPA 2.0 Spec (except for the L2 Cache for now)
- Minimum deviation from the specification
- Full JTA Support
- Built in connection pool for Java SE
- Prepared Statement cache support for Java SE

## COMPATIBILITY ##

Batoo JPA has been tested with the following databases:

- Derby
- HypersonicDB
- H2
- MySql
- PostgreSQL
- MsSQL
- Oracle
- Sybase SQLAnyWhere

Batoo JPA has been tested with the following platforms:

- Java SE 1.6
- Java SE 1.7
- JBoss AS 6.1.0.Final
- JBoss AS 7.1.1.Final
- GlassFish 3.1.2.1
- Apache TomEE 5.0
- Apache Tomcat 7.0.x


## BUILDING ##
1. Install Maven 3.0.X if you haven't installed it previously.
2. Install JDK 1.7 if you haven't installed it previously.
3. check the project using

    `$ git clone git://github.com/BatooOrg/BatooJPA.git`

    `$ cd BatooJPA`

    `$ mvn install`
	
Note: The initial build may take a long time.


## BENCHMARK ##

You can use [BatooJPABenchmark](https://github.com/BatooOrg/BatooJPABenchmark) for benchmarking.
