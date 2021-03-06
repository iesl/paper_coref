# RPP #

## Setup Prerequisites ##

### Bibie ###

Clone and install the project bibie:

```
git clone https://github.com/iesl/bibie
cd bibie
mvn install
```

### Paper Header ###

Clone and install the project paper_header:

```
git clone https://github.com/iesl/paper-header
cd paper-header
mvn install
```


## Clone \& Build ##

```
git clone https://github.com/iesl/rpp
```

We need to build the project and jar the classes with the dependencies

```
mvn clean package -Pjar-with-dependencies
```

## Running the project ##

Unzip the models in the rpp repo:

```
tar -xvf models.tgz
```

Obtain the lexicon files (if necessarily). You can email me if you do not have these files.

See [this script](../../scripts/rpp/batchrun.sh) for an example of running RPP.



