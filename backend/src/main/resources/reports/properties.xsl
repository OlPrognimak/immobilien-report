<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:svg="http://www.w3.org/2000/svg">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:decimal-format name="eu" decimal-separator="," grouping-separator="."/>

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
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="9pt" color="#50606e">IMMOREPORT / PROPERTY PORTFOLIO</fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block border-top="0.4pt solid #cbd5dc" padding-top="3mm" font-size="8pt" color="#66727d">
                        Generated
                        <xsl:value-of select="@generatedAt"/>
                        <fo:inline float="right">Page
                            <fo:page-number/>
                            /
                            <fo:page-number-citation-last ref-id="report-end"/>
                        </fo:inline>
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:table table-layout="fixed" width="100%" margin-bottom="8mm">
                        <fo:table-column column-width="proportional-column-width(3)"/>
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell display-align="center">
                                    <fo:block font-size="10pt" color="#8c6825" font-weight="bold" space-after="2mm">
                                        PORTFOLIO REPORT
                                    </fo:block>
                                    <fo:block font-size="24pt" line-height="29pt" font-weight="bold">
                                        <xsl:value-of select="companyName"/>
                                    </fo:block>
                                    <fo:block font-size="11pt" color="#5b6772" space-before="2mm">
                                        <xsl:value-of select="companyCity"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        <xsl:if test="string-length(normalize-space(properties/property[1]/imageUri)) &gt; 0">
                                            <fo:external-graphic src="{properties/property[1]/imageUri}"
                                                                 content-width="34mm" content-height="25mm"
                                                                 scaling="uniform"/>
                                        </xsl:if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>

                    <fo:table table-layout="fixed" width="100%" background-color="#eaf0f4" margin-bottom="9mm">
                        <fo:table-column column-width="33.33%"/>
                        <fo:table-column column-width="33.33%"/>
                        <fo:table-column column-width="33.33%"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell padding="5mm">
                                    <fo:block font-size="8pt" color="#5b6772">PROPERTIES</fo:block>
                                    <fo:block font-size="17pt" font-weight="bold">
                                        <xsl:value-of select="propertyCount"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="5mm" border-left="0.5pt solid #cbd5dc">
                                    <fo:block font-size="8pt" color="#5b6772">TOTAL AREA</fo:block>
                                    <fo:block font-size="17pt" font-weight="bold">
                                        <xsl:value-of select="format-number(totalArea, '#.##0,00', 'eu')"/> m²
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="5mm" border-left="0.5pt solid #cbd5dc">
                                    <fo:block font-size="8pt" color="#5b6772">PORTFOLIO VALUE</fo:block>
                                    <fo:block font-size="17pt" font-weight="bold">€
                                        <xsl:value-of select="format-number(totalValue, '#.##0', 'eu')"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>

                    <fo:block font-size="14pt" font-weight="bold" space-after="4mm">Property overview</fo:block>
                    <fo:table table-layout="fixed" width="100%" font-size="8.5pt">
                        <fo:table-column column-width="18%"/>
                        <fo:table-column column-width="26%"/>
                        <fo:table-column column-width="14%"/>
                        <fo:table-column column-width="13%"/>
                        <fo:table-column column-width="17%"/>
                        <fo:table-column column-width="12%"/>
                        <fo:table-header background-color="#102a43" color="#ffffff" font-weight="bold">
                            <fo:table-row>
                                <fo:table-cell padding="3mm">
                                    <fo:block>IMAGE</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="3mm">
                                    <fo:block>PROPERTY</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="3mm">
                                    <fo:block>TYPE</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="3mm" text-align="right">
                                    <fo:block>AREA</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="3mm" text-align="right">
                                    <fo:block>VALUE</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="3mm">
                                    <fo:block>STATUS</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="properties/property">
                                <fo:table-row border-bottom="0.5pt solid #d7dee3">
                                    <fo:table-cell padding="2mm">
                                        <fo:block>
                                            <xsl:if test="string-length(normalize-space(imageUri)) &gt; 0">
                                                <fo:external-graphic src="{imageUri}" content-width="22mm"
                                                                     content-height="15mm" scaling="uniform"/>
                                            </xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="3mm">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="name"/>
                                        </fo:block>
                                        <fo:block font-size="7.5pt" color="#66727d" space-before="1mm">
                                            <xsl:value-of select="address"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="3mm">
                                        <fo:block>
                                            <xsl:value-of select="type"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="3mm" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="format-number(area, '#.##0,00', 'eu')"/> m²
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="3mm" text-align="right">
                                        <fo:block>€
                                            <xsl:value-of select="format-number(value, '#.##0', 'eu')"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="3mm">
                                        <fo:block color="#315470" font-weight="bold">
                                            <xsl:value-of select="status"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block id="report-end"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
