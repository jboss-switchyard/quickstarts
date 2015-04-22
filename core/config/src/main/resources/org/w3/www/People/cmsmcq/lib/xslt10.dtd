<!--*
    * XSLT DTD.  Mostly copied from appendix C of the XSLT spec
    * (http://www.w3.org/TR/xslt#dtd), but with some minor changes by
    * CMSMcQ, mostly added commentary.
    * Revisions:
    * 2006-12-31 : MSM : move to /People/cmsmcq/lib
    * 2006-12-30 : MSM : move to www.w3.org/XML/Public
    * 2001-10-27 : MSM : add pe to allow adding attributes to top level,
    *   to allow use of Saxon-specific elements
    * 2001-07-06 : MSM : copy to SGML/Public/W3C, add to .emacs
    * 2001-02-01/02 : MSM : made file (Bergen) as part of schemas for specs TF
    *-->
<!--* PEs intended for modification by user: *-->
<!ENTITY % result-elements "">
<!ENTITY % non-xsl-instructions "">

<!--*
    * From here, copied without change except for adding comments on
    * MSM's test schema for XSLT.
    *-->

<!--* make char-instruction an abstract element, head of a 
    * substitution group, and member of substitution group of 'instruction'.
    *-->
<!ENTITY % char-instructions "
  | xsl:apply-templates
  | xsl:call-template
  | xsl:apply-imports
  | xsl:for-each
  | xsl:value-of
  | xsl:copy-of
  | xsl:number
  | xsl:choose
  | xsl:if
  | xsl:text
  | xsl:copy
  | xsl:variable
  | xsl:message
  | xsl:fallback
">

<!--* make instruction an abstract element, head of a 
    * substitution group
    *-->
<!ENTITY % instructions "
  %char-instructions;
  | xsl:processing-instruction
  | xsl:comment
  | xsl:element
  | xsl:attribute
  %non-xsl-instructions;
">

<!--* make char-template a complex type *-->
<!ENTITY % char-template "
 (#PCDATA
  %char-instructions;)*
">

<!--* make template a complex type *-->
<!ENTITY % template "
 (#PCDATA
  %instructions;
  %result-elements;)*
">

<!--* use simple type xsd:uriReference *-->
<!-- Used for the type of an attribute value that is a URI reference.-->
<!ENTITY % URI "CDATA">

<!--* make simple type xslt:Pattern *-->
<!-- Used for the type of an attribute value that is a pattern.-->
<!ENTITY % pattern "CDATA">

<!--* make simple type xslt:avt *-->
<!-- Used for the type of an attribute value that is an
     attribute value template.-->
<!ENTITY % avt "CDATA">

<!--* make simple type xslt:QName from simple type xsd:QName *-->
<!-- Used for the type of an attribute value that is a QName; the prefix
     gets expanded by the XSLT processor. -->
<!ENTITY % qname "NMTOKEN">

<!--* make simple type xslt:QNameList *-->
<!-- Like qname but a whitespace-separated list of QNames. -->
<!ENTITY % qnames "NMTOKENS">

<!--* make simple type xslt:Expression from simple type XPath:Expr *-->
<!-- Used for the type of an attribute value that is an expression.-->
<!ENTITY % expr "CDATA">

<!--* make simple type xslt:Char from simple type xsd:string *-->
<!-- Used for the type of an attribute value that consists
     of a single character.-->
<!ENTITY % char "CDATA">

<!--* make simple type xslt:Priority from simple type xsd:float *-->
<!-- Used for the type of an attribute value that is a priority. -->
<!ENTITY % priority "NMTOKEN">

<!--* do nothing with this.  Just refer in appropriate places to
    * xml:space
    *-->
<!ENTITY % space-att "xml:space (default|preserve) #IMPLIED">

<!--* make named model group xslt:non-xsl-top-level (empty choice) *-->
<!-- This may be overridden to customize the set of elements allowed
at the top-level. -->

<!ENTITY % non-xsl-top-level "">

<!--* make complex type top-level, for stylesheet and transform *-->
<!ENTITY % top-level "
 (xsl:import*,
  (xsl:include
  | xsl:strip-space
  | xsl:preserve-space
  | xsl:output
  | xsl:key
  | xsl:decimal-format
  | xsl:attribute-set
  | xsl:variable
  | xsl:param
  | xsl:template
  | xsl:namespace-alias
  %non-xsl-top-level;)*)
">

<!ENTITY % x-top-level-atts "">
<!ENTITY % top-level-atts '
  extension-element-prefixes CDATA #IMPLIED
  exclude-result-prefixes CDATA #IMPLIED
  id ID #IMPLIED
  version NMTOKEN #REQUIRED
  xmlns:xsl CDATA #FIXED "http://www.w3.org/1999/XSL/Transform"
  %x-top-level-atts;
  %space-att;
'>

<!--* make attribute group, for target-NS elements *-->
<!-- This entity is defined for use in the ATTLIST declaration
for result elements. -->

<!ENTITY % result-element-atts '
  xsl:extension-element-prefixes CDATA #IMPLIED
  xsl:exclude-result-prefixes CDATA #IMPLIED
  xsl:use-attribute-sets %qnames; #IMPLIED
  xsl:version NMTOKEN #IMPLIED
'>

<!--* make top level elements *-->
<!ELEMENT xsl:stylesheet %top-level;>
<!ATTLIST xsl:stylesheet %top-level-atts;>

<!ELEMENT xsl:transform %top-level;>
<!ATTLIST xsl:transform %top-level-atts;>

<!ELEMENT xsl:import EMPTY>
<!ATTLIST xsl:import href %URI; #REQUIRED>

<!ELEMENT xsl:include EMPTY>
<!ATTLIST xsl:include href %URI; #REQUIRED>

<!ELEMENT xsl:strip-space EMPTY>
<!ATTLIST xsl:strip-space elements CDATA #REQUIRED>

<!ELEMENT xsl:preserve-space EMPTY>
<!ATTLIST xsl:preserve-space elements CDATA #REQUIRED>

<!ELEMENT xsl:output EMPTY>
<!ATTLIST xsl:output
  method %qname; #IMPLIED
  version NMTOKEN #IMPLIED
  encoding CDATA #IMPLIED
  omit-xml-declaration (yes|no) #IMPLIED
  standalone (yes|no) #IMPLIED
  doctype-public CDATA #IMPLIED
  doctype-system CDATA #IMPLIED
  cdata-section-elements %qnames; #IMPLIED
  indent (yes|no) #IMPLIED
  media-type CDATA #IMPLIED
>

<!ELEMENT xsl:key EMPTY>
<!ATTLIST xsl:key
  name %qname; #REQUIRED
  match %pattern; #REQUIRED
  use %expr; #REQUIRED
>

<!ELEMENT xsl:decimal-format EMPTY>
<!ATTLIST xsl:decimal-format
  name %qname; #IMPLIED
  decimal-separator %char; "."
  grouping-separator %char; ","
  infinity CDATA "Infinity"
  minus-sign %char; "-"
  NaN CDATA "NaN"
  percent %char; "&#x25;"
  per-mille %char; "&#x2030;"
  zero-digit %char; "0"
  digit %char; "#"
  pattern-separator %char; ";"
>
<!--* default value of percent was "%", indirection added to get
    * it past Saxon *-->

<!ELEMENT xsl:namespace-alias EMPTY>
<!ATTLIST xsl:namespace-alias
  stylesheet-prefix CDATA #REQUIRED
  result-prefix CDATA #REQUIRED
>

<!ELEMENT xsl:template
 (#PCDATA
  %instructions;
  %result-elements;
  | xsl:param)*
>

<!ATTLIST xsl:template
  match %pattern; #IMPLIED
  name %qname; #IMPLIED
  priority %priority; #IMPLIED
  mode %qname; #IMPLIED
  %space-att;
>

<!ELEMENT xsl:value-of EMPTY>
<!ATTLIST xsl:value-of
  select %expr; #REQUIRED
  disable-output-escaping (yes|no) "no"
>

<!ELEMENT xsl:copy-of EMPTY>
<!ATTLIST xsl:copy-of select %expr; #REQUIRED>

<!ELEMENT xsl:number EMPTY>
<!ATTLIST xsl:number
   level (single|multiple|any) "single"
   count %pattern; #IMPLIED
   from %pattern; #IMPLIED
   value %expr; #IMPLIED
   format %avt; '1'
   lang %avt; #IMPLIED
   letter-value %avt; #IMPLIED
   grouping-separator %avt; #IMPLIED
   grouping-size %avt; #IMPLIED
>

<!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
<!ATTLIST xsl:apply-templates
  select %expr; "node()"
  mode %qname; #IMPLIED
>

<!ELEMENT xsl:apply-imports EMPTY>

<!-- xsl:sort cannot occur after any other elements or
any non-whitespace character -->

<!ELEMENT xsl:for-each
 (#PCDATA
  %instructions;
  %result-elements;
  | xsl:sort)*
>

<!ATTLIST xsl:for-each
  select %expr; #REQUIRED
  %space-att;
>

<!ELEMENT xsl:sort EMPTY>
<!ATTLIST xsl:sort
  select %expr; "."
  lang %avt; #IMPLIED
  data-type %avt; "text"
  order %avt; "ascending"
  case-order %avt; #IMPLIED
>

<!ELEMENT xsl:if %template;>
<!ATTLIST xsl:if
  test %expr; #REQUIRED
  %space-att;
>

<!ELEMENT xsl:choose (xsl:when+, xsl:otherwise?)>
<!ATTLIST xsl:choose %space-att;>

<!ELEMENT xsl:when %template;>
<!ATTLIST xsl:when
  test %expr; #REQUIRED
  %space-att;
>

<!ELEMENT xsl:otherwise %template;>
<!ATTLIST xsl:otherwise %space-att;>

<!ELEMENT xsl:attribute-set (xsl:attribute)*>
<!ATTLIST xsl:attribute-set
  name %qname; #REQUIRED
  use-attribute-sets %qnames; #IMPLIED
>

<!ELEMENT xsl:call-template (xsl:with-param)*>
<!ATTLIST xsl:call-template
  name %qname; #REQUIRED
>

<!ELEMENT xsl:with-param %template;>
<!ATTLIST xsl:with-param
  name %qname; #REQUIRED
  select %expr; #IMPLIED
>

<!ELEMENT xsl:variable %template;>
<!ATTLIST xsl:variable 
  name %qname; #REQUIRED
  select %expr; #IMPLIED
>

<!ELEMENT xsl:param %template;>
<!ATTLIST xsl:param 
  name %qname; #REQUIRED
  select %expr; #IMPLIED
>

<!ELEMENT xsl:text (#PCDATA)>
<!ATTLIST xsl:text
  disable-output-escaping (yes|no) "no"
>

<!ELEMENT xsl:processing-instruction %char-template;>
<!ATTLIST xsl:processing-instruction 
  name %avt; #REQUIRED
  %space-att;
>

<!ELEMENT xsl:element %template;>
<!ATTLIST xsl:element 
  name %avt; #REQUIRED
  namespace %avt; #IMPLIED
  use-attribute-sets %qnames; #IMPLIED
  %space-att;
>

<!ELEMENT xsl:attribute %char-template;>
<!ATTLIST xsl:attribute 
  name %avt; #REQUIRED
  namespace %avt; #IMPLIED
  %space-att;
>

<!ELEMENT xsl:comment %char-template;>
<!ATTLIST xsl:comment %space-att;>

<!ELEMENT xsl:copy %template;>
<!ATTLIST xsl:copy
  %space-att;
  use-attribute-sets %qnames; #IMPLIED
>

<!ELEMENT xsl:message %template;>
<!ATTLIST xsl:message
  %space-att;
  terminate (yes|no) "no"
>

<!ELEMENT xsl:fallback %template;>
<!ATTLIST xsl:fallback %space-att;>
