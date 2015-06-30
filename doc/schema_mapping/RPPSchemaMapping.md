# RPP Schema Mapping #

The schema mapping for RPP follows the schema mapping in Meta-Eval. 


## Headers ##

The header section in the RPP schema is denoted ```header```:

| Field | Mapping |
| ----- | ------- |
| Title | Select the first ```<title>``` appearing in the header |
| Date | Select the first ```<date>``` appearing in the header |
| Venue | Select the first ```<journal>``` if it appears, otherwise select the first ```<booktitle>```. If neither of these appear, select the first ```<institution>``` which appears in the header | 
| Authors | Select each ```<person>``` appearing in ```<authors>```. Get the ```<person-last>```, ```<person-first>``` and ```<person-middle>``` names. Yield the formatted string ```Last, First Middle``` if the last name is defined and ```First Middle``` otherwise. | 

## References ##

Each reference is a ```<reference>``` appearing in ```<references>```

| Field | Mapping |
| ----- | ------- |
| Title | Select the first ```<title>``` appearing in the reference |
| Date | Select the first ```<date>``` appearing in the reference |
| Venue | Select the first ```<journal>``` if it appears, otherwise select the first ```<booktitle>```. If neither of these appear, select the first ```<institution>``` which appears in the reference | 
| Authors | Select each ```<person>``` appearing in ```<authors>```. Get the ```<person-last>```, ```<person-first>``` and ```<person-middle>``` names. Yield the formatted string ```Last, First Middle``` if the last name is defined and ```First Middle``` otherwise. | 
