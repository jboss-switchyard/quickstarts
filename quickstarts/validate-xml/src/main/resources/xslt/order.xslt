<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<orders:orderAck xmlns:orders="urn:switchyard-quickstart:validate-xml:0.1.0">
			<xsl:for-each select="orders:order">
				<orderId>
					<xsl:value-of select="orderId" />
				</orderId>
				<accepted>
					<xsl:choose>
						<xsl:when test="(itemId = 'BUTTER') and (quantity &lt;= 200)">
							true
						</xsl:when>
						<xsl:otherwise>
							false
						</xsl:otherwise>
					</xsl:choose>
				</accepted>
				<status>
					<xsl:choose>
						<xsl:when test="(itemId = 'BUTTER') and (quantity &lt;= 200)">
							Order Accepted
						</xsl:when>
						<xsl:when test="itemId != 'BUTTER'">
							No Such Item:
							<xsl:value-of select="itemId" />
						</xsl:when>
						<xsl:when test="quantity &gt; 200">
							Not Enough Stock
						</xsl:when>
						<xsl:otherwise>
							UNKNOWN
						</xsl:otherwise>
					</xsl:choose>
				</status>
			</xsl:for-each>
		</orders:orderAck>
	</xsl:template>
</xsl:stylesheet>
