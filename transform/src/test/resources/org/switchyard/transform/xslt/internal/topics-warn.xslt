<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output encoding="unsupported"/>
  	<xsl:template match="/">
		<index>
			<head>
				<title>JBoss Project's'</title>
			</head>
			<body>
				<table border="1">
					<tr>
						<th>Title</th>
						<th>URL</th>
					</tr>
					<xsl:for-each select="project/topic">
						<tr>
							<td>
								<xsl:value-of select="title" />
							</td>
							<td>
								<xsl:value-of select="url" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</index>
	</xsl:template>
</xsl:stylesheet>

