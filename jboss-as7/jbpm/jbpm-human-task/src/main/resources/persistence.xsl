<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ JBoss, Home of Professional Open Source.
  ~ Copyright 2011, Red Hat, Inc., and individual contributors
  ~ as indicated by the @author tags. See the copyright.txt file in the
  ~ distribution for a full listing of individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  -->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="xs xsl xsi fn xdt">

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="node()[name(.)='persistence-unit']">
    <persistence-unit name="org.jbpm.task" transaction-type="RESOURCE_LOCAL">
        <xsl:apply-templates select="@*|node()"/>
    </persistence-unit>
</xsl:template>

<xsl:template match="node()[name(.)='provider']">
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
    <non-jta-data-source>java:jboss/datasources/jbpmDS</non-jta-data-source>
    <mapping-file>META-INF/jbpm-human-task-orm.xml</mapping-file>
</xsl:template>

<xsl:template match="node()[name(.)='properties']">
    <properties>
        <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
        <property name="hibernate.connection.driver_class" value="org.h2.Driver"/>
        <property name="hibernate.connection.url" value="jdbc:h2:~/jbpmDS"/>
        <property name="hibernate.connection.username" value="sa"/>
        <property name="hibernate.connection.password" value="sa"/>
        <property name="hibernate.connection.autocommit" value="false"/>
        <property name="hibernate.connection.pool_size" value="1"/>
        <property name="hibernate.max_fetch_depth" value="3"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
        <property name="hibernate.current_session_context_class" value="thread"/>
        <property name="hibernate.cache.provider_class" value="org.hibernate.cache.NoCacheProvider"/>
        <property name="hibernate.show_sql" value="false"/>
        <property name="hibernate.transaction.manager_lookup_class" value="org.switchyard.component.bpm.jta.hibernate.AS7TransactionManagerLookup"/>
    </properties>
</xsl:template>

</xsl:stylesheet>
