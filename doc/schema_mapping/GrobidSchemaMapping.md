# Grobid Schema Mapping #

The schema mapping used in this project follows the schema mapping used in __Meta-Eval__ to map the Grobid schema to the PaperMetaData data structure.

## Grobid Schema ##

- Generally Grobid follows the TEI-C specifications: http://www.tei-c.org/Guidelines/P5/
- The specific Grobid Schema is here: https://github.com/kermitt2/grobid/blob/master/grobid-home/schemas/doc/Grobid_doc.html

## Headers ##

The following defines how header information in Grobid's output is converted into a __RawCitation__.

Select the first instance of ```<teiHeader>``` in the xml output as the _header section_.

| Field | Mapping |
| ----- | ------- |
| Title | Select the first instance ```<title>``` appearing inside an instance of ```<titleStmt>``` in _header section_. |
| Date  | Select the value of the ```when``` attribute of the first ```<date>```appearing in a ```<publicationStmt>``` with a value of ```published``` for the ```type``` attribute in _header section_ |
| Venue | Select the first instance of ```<title>```appearing in a ```<monogr>``` in ```<sourceDesc>``` in _header section_. |
| Authors | Select each ```<author>``` appearing in _header section_. For each ```<author>```, take the ```<surname>``` field and the ```<forename>``` field with ```type```s of ```first``` and ```middle```. Format the author names as ```Last, First Middle``` or if the last name is missing is ```First Middle```. |


## References ##

The following defines how citation information in Grobid's output is converted into a __RawCitation__.

Select the first instance of ```<listBibl>``` in the xml output as the _references section_. For each reference, denoted with ```<biblStruct>```, use the following mapping:  

| Field | Mapping |
| ----- | ------- |
| Title |  Select the first instance ```<title>``` appearing in ```<analytic>``` with ```type``` equal to ```main``` if it exists, otherwise select ```<title>``` appearing in ```<monogr>```. |
| Date  | Select the value of the ```when``` attribute of the first ```<date>```appearing with a value of ```published``` for the ```type``` attribute |
| Venue | Select the first instance of ```<title>```appearing in a ```<monogr>``` in ```<sourceDesc>```. |
| Authors | First consider, each ```<author>``` appearing in ```<analytic>```. For each ```<author>```, take the ```<surname>``` field and the ```<forename>``` field with ```type```s of ```first``` and ```middle```. Format the author names as ```Last, First Middle``` or if the last name is missing is ```First Middle```. If no authors appear in ```<analytic>```, perform the same procedure on ```<monogr>```.|

