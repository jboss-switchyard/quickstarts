<?xml version="1.0" encoding="UTF-8"?>
<!--
 - Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 - 
 - Licensed under the Apache License, Version 2.0 (the "License");
 - you may not use this file except in compliance with the License.
 - You may obtain a copy of the License at
 - http://www.apache.org/licenses/LICENSE-2.0
 - Unless required by applicable law or agreed to in writing, software
 - distributed under the License is distributed on an "AS IS" BASIS,
 - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 - See the License for the specific language governing permissions and
 - limitations under the License.
 -->
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:as="urn:jboss:domain:1.4"
    xmlns:inf="urn:jboss:domain:infinispan:1.4"
    xmlns:sy="urn:jboss:domain:switchyard:1.0"
    xmlns:log="urn:jboss:domain:logging:1.2"
    exclude-result-prefixes="xs xsl xsi fn xdt as inf sy log">

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="@xsi:schemaLocation">
    <xsl:attribute name="xsi:schemaLocation">
        <xsl:value-of select="."/>
        <xsl:text> urn:jboss:domain:switchyard switchyard.xsd</xsl:text>
    </xsl:attribute>
</xsl:template>

<xsl:template match="node()[name(.)='extensions']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <extension module="org.switchyard"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="//as:profile[@name='default' or @name='full']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <subsystem xmlns="urn:jboss:domain:switchyard:1.0">
            <modules>
                <module identifier="org.switchyard.component.bean" implClass="org.switchyard.component.bean.deploy.BeanComponent"/>
                <module identifier="org.switchyard.component.soap" implClass="org.switchyard.component.soap.deploy.SOAPComponent">
                    <properties>
                        <socketAddr>:18001</socketAddr>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.camel" implClass="org.switchyard.component.camel.deploy.CamelComponent">
                    <properties>
                        <socketAddr>:18001</socketAddr>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.camel.atom" implClass="org.switchyard.component.camel.atom.deploy.CamelAtomComponent"/>
                <module identifier="org.switchyard.component.camel.core" implClass="org.switchyard.component.camel.core.deploy.CamelCoreComponent"/>
                <module identifier="org.switchyard.component.camel.file" implClass="org.switchyard.component.camel.file.deploy.CamelFileComponent"/>
                <module identifier="org.switchyard.component.camel.ftp" implClass="org.switchyard.component.camel.ftp.deploy.CamelFtpComponent"/>
                <module identifier="org.switchyard.component.camel.jms" implClass="org.switchyard.component.camel.jms.deploy.CamelJmsComponent"/>
                <module identifier="org.switchyard.component.camel.jpa" implClass="org.switchyard.component.camel.jpa.deploy.CamelJpaComponent"/>
                <module identifier="org.switchyard.component.camel.mail" implClass="org.switchyard.component.camel.mail.deploy.CamelMailComponent"/>
                <module identifier="org.switchyard.component.camel.netty" implClass="org.switchyard.component.camel.netty.deploy.CamelNettyComponent"/>
                <module identifier="org.switchyard.component.camel.quartz" implClass="org.switchyard.component.camel.quartz.deploy.CamelQuartzComponent"/>
                <module identifier="org.switchyard.component.camel.rss" implClass="org.switchyard.component.camel.atom.deploy.CamelRSSComponent"/>
                <module identifier="org.switchyard.component.camel.sql" implClass="org.switchyard.component.camel.sql.deploy.CamelSqlComponent"/>
                <module identifier="org.switchyard.component.rules" implClass="org.switchyard.component.rules.deploy.RulesComponent"/>
                <module identifier="org.switchyard.component.bpm" implClass="org.switchyard.component.bpm.deploy.BPMComponent"/>
                <module identifier="org.switchyard.component.bpel" implClass="org.switchyard.component.bpel.deploy.BPELComponent"/>
                <module identifier="org.switchyard.component.http" implClass="org.switchyard.component.http.deploy.HttpComponent"/>
                <module identifier="org.switchyard.component.jca" implClass="org.switchyard.component.jca.deploy.JCAComponent"/>
                <module identifier="org.switchyard.component.sca" implClass="org.switchyard.component.sca.deploy.SCAComponent">
                    <properties>
                        <cache-name>cluster</cache-name>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.resteasy" implClass="org.switchyard.component.resteasy.deploy.RESTEasyComponent"/>
            </modules>
            <extensions>
		<extension identifier="org.apache.camel.bindy"/>
                <extension identifier="org.apache.camel.mvel"/>
                <extension identifier="org.apache.camel.ognl"/>
                <extension identifier="org.apache.camel.jaxb"/>
                <extension identifier="org.apache.camel.soap"/>
            </extensions>
        </subsystem>
    </xsl:copy>
</xsl:template>

<xsl:template match="//as:profile[@name='ha' or @name='full-ha']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <subsystem xmlns="urn:jboss:domain:switchyard:1.0">
            <modules>
                <module identifier="org.switchyard.component.bean" implClass="org.switchyard.component.bean.deploy.BeanComponent"/>
                <module identifier="org.switchyard.component.soap" implClass="org.switchyard.component.soap.deploy.SOAPComponent">
                    <properties>
                        <socketAddr>:18001</socketAddr>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.camel" implClass="org.switchyard.component.camel.deploy.CamelComponent">
                    <properties>
                        <socketAddr>:18001</socketAddr>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.camel.atom" implClass="org.switchyard.component.camel.atom.deploy.CamelAtomComponent"/>
                <module identifier="org.switchyard.component.camel.core" implClass="org.switchyard.component.camel.core.deploy.CamelCoreComponent"/>
                <module identifier="org.switchyard.component.camel.file" implClass="org.switchyard.component.camel.file.deploy.CamelFileComponent"/>
                <module identifier="org.switchyard.component.camel.ftp" implClass="org.switchyard.component.camel.ftp.deploy.CamelFtpComponent"/>
                <module identifier="org.switchyard.component.camel.jms" implClass="org.switchyard.component.camel.jms.deploy.CamelJmsComponent"/>
                <module identifier="org.switchyard.component.camel.jpa" implClass="org.switchyard.component.camel.jpa.deploy.CamelJpaComponent"/>
                <module identifier="org.switchyard.component.camel.mail" implClass="org.switchyard.component.camel.mail.deploy.CamelMailComponent"/>
                <module identifier="org.switchyard.component.camel.netty" implClass="org.switchyard.component.camel.netty.deploy.CamelNettyComponent"/>
                <module identifier="org.switchyard.component.camel.quartz" implClass="org.switchyard.component.camel.quartz.deploy.CamelQuartzComponent"/>
                <module identifier="org.switchyard.component.camel.sql" implClass="org.switchyard.component.camel.sql.deploy.CamelSqlComponent"/>
                <module identifier="org.switchyard.component.rules" implClass="org.switchyard.component.rules.deploy.RulesComponent"/>
                <module identifier="org.switchyard.component.bpm" implClass="org.switchyard.component.bpm.deploy.BPMComponent"/>
                <module identifier="org.switchyard.component.bpel" implClass="org.switchyard.component.bpel.deploy.BPELComponent"/>
                <module identifier="org.switchyard.component.http" implClass="org.switchyard.component.http.deploy.HttpComponent"/>
                <module identifier="org.switchyard.component.jca" implClass="org.switchyard.component.jca.deploy.JCAComponent"/>
                <module identifier="org.switchyard.component.sca" implClass="org.switchyard.component.sca.deploy.SCAComponent">
                    <properties>
                        <cache-name>switchyard</cache-name>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.resteasy" implClass="org.switchyard.component.resteasy.deploy.RESTEasyComponent"/>
            </modules>
            <extensions>
		<extension identifier="org.apache.camel.bindy"/>
                <extension identifier="org.apache.camel.mvel"/>
                <extension identifier="org.apache.camel.ognl"/>
                <extension identifier="org.apache.camel.jaxb"/>
                <extension identifier="org.apache.camel.soap"/>
            </extensions>
        </subsystem>
    </xsl:copy>
</xsl:template>

<xsl:template match="log:subsystem">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <logger category="org.switchyard">
            <level name="INFO"/>
        </logger>
        <logger category="org.apache.deltaspike.core.api.provider.BeanManagerProvider">
            <level name="ERROR"/>
        </logger>
    </xsl:copy>
</xsl:template>

<xsl:template match="node()[name(.)='security-domains']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <security-domain name="bpel-console" cache-type="default">
            <authentication>
                <login-module code="UsersRoles" flag="required"/>
            </authentication>
        </security-domain>
    </xsl:copy>
</xsl:template>

<xsl:template match="//as:profile[@name='ha' or @name='full-ha']//sy:cache-name">
   <xsl:copy>switchyard</xsl:copy>
</xsl:template>

<xsl:template match="//as:profile[@name='ha' or @name='full-ha']/inf:subsystem">
   <xsl:copy>
   <xsl:apply-templates select="@*|node()"/>
        <cache-container name="switchyard" default-cache="default" start="EAGER">
           <transport lock-timeout="60000"/>
           <replicated-cache name="default" mode="SYNC" batching="true" start="EAGER">
               <locking isolation="REPEATABLE_READ"/>
           </replicated-cache>
       </cache-container>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
