<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (c) 2010-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:index="http://sonatype.com/xsd/litmus-testsupport/index/1.0">

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="index.css"/>
      </head>
      <body>
        <table class="pane">
          <tr>
            <td class="pane-header">Index</td>
            <td class="pane-header">Class</td>
            <td class="pane-header">Method</td>
            <td class="pane-header">Duration</td>
            <td class="pane-header">Info</td>
          </tr>
          <xsl:for-each select="index:index/index:test">
            <tr>
              <td class="pane right">
                <a>
                  <xsl:attribute name="href">
                    <xsl:value-of select="index"/>
                  </xsl:attribute>
                  <xsl:value-of select="index"/>
                </a>
              </td>
              <td>
                <xsl:attribute name="class">
                  pane success-<xsl:value-of select="success"/>
                </xsl:attribute>
                <xsl:if test="throwableMessage">
                  <xsl:attribute name="title">
                    <xsl:value-of select="throwableMessage"/>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="className"/>
              </td>
              <td class="pane">
                <xsl:value-of select="methodName"/>
              </td>
              <td class="pane">
                <xsl:value-of select="duration"/> sec
              </td>
              <td class="pane">
                <table>
                  <xsl:for-each select="index:testInfo">
                    <tr>
                      <td>
                        <xsl:value-of select="key"/>:
                      </td>
                      <xsl:if test="link='true'">
                        <td>
                          <a>
                            <xsl:attribute name="href">
                              <xsl:value-of select="value"/>
                            </xsl:attribute>
                            View
                          </a>
                        </td>
                      </xsl:if>
                      <xsl:if test="link='false'">
                        <td>
                          <xsl:value-of select="value"/>
                        </td>
                      </xsl:if>
                    </tr>
                  </xsl:for-each>
                </table>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>