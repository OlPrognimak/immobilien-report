<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:svg="http://www.w3.org/2000/svg"
  exclude-result-prefixes="svg">
    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:template match="/report">
        <fo:root font-family="Helvetica" color="#17212b">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="a4" page-width="210mm" page-height="297mm" margin="15mm">
                    <fo:region-body margin-top="14mm" margin-bottom="12mm"/>
                    <fo:region-before extent="10mm"/>
                    <fo:region-after extent="8mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="a4">
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block border-top="0.4pt solid #cbd5dc" padding-top="3mm">
                        <fo:table table-layout="fixed" width="100%" font-size="8pt" color="#66727d">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>Generated <xsl:value-of select="@generatedAt"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right">
                                        <fo:block>Page <fo:page-number/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="10pt" color="#8c6825" font-weight="bold" space-after="2mm">
                        REGIONAL SALES REPORT
                    </fo:block>
                    <fo:block font-size="18pt" line-height="24pt" font-weight="bold" space-after="3mm">
                        Regional sales comparison
                    </fo:block>
                    <fo:block font-size="10pt" line-height="14pt" color="#5b6772" space-after="10mm">
                        Regional sales are grouped by company and region so the current companies can be compared independently of the selected property portfolio.
                    </fo:block>
                    <fo:block font-size="13pt" line-height="18pt" font-weight="bold" space-after="2mm">
                        Sales by region
                    </fo:block>
                    <xsl:choose>
                        <xsl:when test="regionalSalesChart/svg:svg">
                            <fo:block>
                                <fo:instream-foreign-object content-width="510pt" content-height="400pt" scaling="uniform">
                                    <xsl:copy-of select="regionalSalesChart/svg:svg"/>
                                </fo:instream-foreign-object>
                            </fo:block>
                        </xsl:when>
                        <xsl:otherwise>
                            <fo:block font-size="12pt" color="#66727d">No regional sales data available</fo:block>
                        </xsl:otherwise>
                    </xsl:choose>
                    <fo:block space-before="28mm" font-size="9pt" color="#50606e">IMMOREPORT / REGIONAL SALES</fo:block>
                    <fo:block id="report-end"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
