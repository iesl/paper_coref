# ParsCit Schema Mapping #

The ParsCit XML output provides a confidence score for the value of each field. In each of the following cases, if two values have the same confidence, the value appearing earlier in the XML document is selected.

## Headers ##

| Field | Mapping |
| ----- | ------- |
| Title | Select the ```<title>``` with the highest confidence value appearing in an ```<algorithm>``` block which is not a ```<citationList>```. |
| Date | Select the ```<date>``` with the highest confidence value appearing in an ```<algorithm>``` block which is not a ```<citationList>```. |
| Venue | No ```<venue>``` information is given in the header. |
| Author | Find the ``<author>``` with the highest confidence value appearing in an ```<algorithm>```. Select all ``<author>```s with this confidence value appearing in an ```<algorithm>``` block which is not a ```<citationList>```. |

## References ##

Each reference mention is a ```<citation>``` appearing in a ```<citationList>``` appearing in an ```<algorithm>```. For each reference, use the following schema mapping:

| Field | Mapping |
| ----- | ------- |
| Title | Select the ```<title>``` with the highest confidence value. |
| Date | Select the ```<date>``` with the highest confidence value. |
| Venue | Select the first ```<booktitle>``` appearing in the reference | 
| Author |  Find the ```<author>``` with the highest confidence value appearing in an ```<algorithm>```. Select all ```<author>```s with this confidence value appearing in the reference |
