<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="html" encoding="UTF-8"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />

  <xsl:template match="/">
    <html>
      <head>
        <title>NVList Manual</title>
        <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
        <link id="main_style" rel="stylesheet" type="text/css" href="style/default.css"/>

        <link rel="stylesheet" href="codemirror/lib/codemirror.css" />
        <link rel="stylesheet" href="codemirror/theme/gedit.css" />
        <script src="codemirror/lib/codemirror.js" type="text/javascript"></script>
        <script src="codemirror/lib/util/runmode.js" type="text/javascript"></script>
        <script src="codemirror/mode/lvn/lvn.js" type="text/javascript"></script>
        <script src="codemirror/mode/lua/lua.js" type="text/javascript"></script>
        <script src="codemirror/mode/xml/xml.js" type="text/javascript"></script>
      </head>
      <body>
        <div id="header">
          <a href="index.xml" class="image-link">
            <img src="img/main/logo.png" alt="NVList logo" style="margin: 19px 10px;"/>
          </a>
        </div>
        <div id="content">
          <xsl:apply-templates select="*" />
        </div>
        <div id="footer"></div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="section">
    <div class="section">
      <div class="section-header{@level}">
        <xsl:if test="@id">
          <xsl:attribute name="id">
            <xsl:value-of select="@id" />
          </xsl:attribute>
        </xsl:if>

        <xsl:value-of select="@title" />
      </div>
      <br/>
      <xsl:apply-templates select="node()" />
      <div style="clear: both;"></div>
    </div>
  </xsl:template>

  <xsl:template match="sourcecode">
    <textarea id="{generate-id(.)}-source"><xsl:value-of select="."/></textarea>
    <pre id="{generate-id(.)}" class="code-block cm-s-gedit" style="display: none;"></pre>
    <script>
      var id = "<xsl:value-of select="generate-id(.)"/>";
      var inputElement = document.getElementById(id + "-source");
      var outputElement = document.getElementById(id);
      CodeMirror.runMode(inputElement.value, {name: "<xsl:value-of select="@lang"/>"}, outputElement);
      inputElement.style.display = "none";
      outputElement.style.display = "block";
    </script>
  </xsl:template>

  <xsl:template match="figure">
    <div class="figure">
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="@float">
            float: <xsl:value-of select="@float" />;
          </xsl:when>
          <xsl:otherwise>
            text-align: center;
          </xsl:otherwise>
        </xsl:choose>

        <xsl:value-of select="@style" />
      </xsl:attribute>

      <img src="{@src}" alt="{@alt}" />
      <div class="figure-desc">
        <xsl:apply-templates select="node()" />
      </div>
    </div>
  </xsl:template>

  <xsl:template match="chapter-index">
    <xsl:for-each select="chapter-group">
      <xsl:variable name="ch" select="position()" />

      <div class="chapter-group">
        <xsl:value-of select="$ch" />.
        <span class="chapter-group"><xsl:value-of select="@title"/></span>
        <xsl:for-each select="chapter">
          <div>
            <span class="chapter-number"><xsl:value-of select="concat('ch', $ch, position()-1, ' ')" /></span>
            <span class="chapter"><a href="{@url}"><xsl:value-of select="."/></a></span>
          </div>
        </xsl:for-each>
      </div>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="javadoc-link">
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="concat('../engine-internals/', translate(@class, '.', '/'), '.html')" />
      </xsl:attribute>
      <xsl:apply-templates select="node()" />
    </a>
  </xsl:template>

  <!-- html-copy.xsl -->

  <xsl:template match="script">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*"/>
      <xsl:value-of select="node()" disable-output-escaping="yes" />
    </xsl:element>
  </xsl:template>

  <xsl:template match="*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{local-name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

</xsl:stylesheet>
