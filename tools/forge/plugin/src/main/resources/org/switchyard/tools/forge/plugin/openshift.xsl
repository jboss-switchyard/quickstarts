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
    xmlns="urn:jboss:domain:1.1"
    exclude-result-prefixes="xs xsl xsi fn xdt">
    <!--
    https://issues.jboss.org/browse/SWITCHYARD-548
    xmlns:jpa="urn:jboss:domain:jpa:1.0"
    -->

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

<xsl:template match="node()[name(.)='socket-binding-group']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <socket-binding name="switchyard-ws" port="18001"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="node()[name(.)='profile']">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <subsystem xmlns="urn:jboss:domain:switchyard:1.0">
            <socket-binding names="switchyard-ws"/>
            <modules>
                <module identifier="org.switchyard.component.bean" implClass="org.switchyard.component.bean.deploy.BeanComponent"/>
                <module identifier="org.switchyard.component.soap" implClass="org.switchyard.component.soap.deploy.SOAPComponent">
                    <properties>
                        <socketAddr>$switchyard-ws</socketAddr>
                        <contextPath>swydws</contextPath>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.camel" implClass="org.switchyard.component.camel.deploy.CamelComponent">
                    <properties>
                        <socketAddr>$switchyard-ws</socketAddr>
                        <contextPath>swydws</contextPath>
                    </properties>
                </module>
                <module identifier="org.switchyard.component.rules" implClass="org.switchyard.component.rules.deploy.RulesComponent"/>
                <module identifier="org.switchyard.component.bpm" implClass="org.switchyard.component.bpm.deploy.BPMComponent"/>
                <module identifier="org.switchyard.component.bpel" implClass="org.switchyard.component.bpel.deploy.BPELComponent"/>
            </modules>
        </subsystem>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
