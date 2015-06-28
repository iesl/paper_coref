# Serialization #

The __LocatedCitation__ and __PaperMetadata__ data structures can be serialized and de-serialized to and from JSON. To convert XML ACL extractions to JSON in these structures, use: ```WriteExtractionsToLocatedCitationJSON``` amd ```WriteExtractionsToPaperMetadataJSON```. 

The input to these programs is a config file in a similar format to the config files for the [ACL Coreference experiment](coreference/acl_paper_coreference.md)```. 

To use ```WriteExtractionsToLocatedCitationJSON```, run:

```
java -Xmx4G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.process.WriteExtractionsToLocatedCitationJSON --config=<config-file>
```

where the config file has the following fields:

```
--format-type = The format of the input, RPP, ParsCit, Grobid.
--input = Either a directory of files, a filename of files, or a list of files
--headers = Either a directory of files, a filename of files, or a list of files containing header information
--references = Either a directory of files, a filename of files, or a list of files containing reference information
--input-type = Directory, filenames, file
--output = The output directory or file
--output-encoding = the encoding of output file(s)
--single-output-file = Whether to write the output to a single file or to separate file
```

To use ```WriteExtractionsToPaperMetadataJSON```, run:


```
java -Xmx4G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.process.WriteExtractionsToPaperMetadataJSON --config=<config-file>
```

where the config file has the following fields:


```
--format-type = The format of the input, RPP, ParsCit, Grobid.
--input = Either a directory of files, a filename of files, or a list of files
--headers = Either a directory of files, a filename of files, or a list of files containing header information
--references = Either a directory of files, a filename of files, or a list of files containing reference information
--input-type = Directory, filenames, file
--output = The output directory or file
--output-encoding = the encoding of output file(s)
```