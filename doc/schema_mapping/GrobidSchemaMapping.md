# Grobid Schema Mapping #


## Grobid Schema ## 

- Generally Grobid follows the TEI-C specifications: http://www.tei-c.org/Guidelines/P5/
- The specific Grobid Schema is here: https://github.com/kermitt2/grobid/blob/master/grobid-home/schemas/doc/Grobid_doc.html

## Header -> RawCitation ##

The following defines how header information in Grobid's output is converted into a __RawCitation__.

1. Select the first instance of ```<teiHeader>``` in the xml output.
2. Select the first instance ```<title>``` appearing inside an instance of ```<titleStmt>```. Set the _title_ field of __RawCitation__ to this value.
3. Select the first instance of ```<date>``` appearing inside an instance of ```<publicationStmt>```. Set the _date_ field of __RawCitation__ to this value.
4. For each ```<persName>``` appearing in an ```author```, concatenate the values of these fields together separated by white space. This is used as an entry in the _author_ list of __RawCitation__
