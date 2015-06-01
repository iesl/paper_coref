#Installing ParsCit#

This is meant to be used along with provided documentation.

## The code ##

Cloned repo: ``https://github.com/knmnyn/ParsCit.git``

## Installing CRF Library ##

### Building the Shared Objects ##

Unzip the code

```
tar -xvzf crf++-0.51.tar.gz
```

Move into the newly created directory

```
cd CRF++-0.51
```

Use ```make``` to build the code:

```
./configure
make
```

_Trouble shooting_: If ```make``` fails, try running it again by doing: ```make clean``` and then ```make```


### Copy the Shared Objects into Place for ParsCit ###

ParsCit expects that the shared objects are in a particular location. You move them by doing the following.
 
```
(from the CRF++-0.51 directory)
cp crf_learn crf_test ..
```

```
(from the CRF++-0.51 directory)
cd .libs
cp -Rf * ../../.libs
cp crf_learn ../../.libs/lt-crf_learn
cp crf_test ../../.libs/lt-crf_test
```

The result of these commands should be a directory ```.libs``` in the ```<parscit-root>/crfpp``` directory with the following files: ```crf_learn,crf_test,encoder.o,feature_cache.o,feature_index.o,feature.o,lbfgs.o,libcrfpp.a,libcrfpp.la,libcrfpp.lai,libcrfpp.o,libcrfpp.so,libcrfpp.so.0,libcrfpp.so.0.0.0,lt-crf_learn,lt-crf_test,node.o,param.o,path.o,tagger.o```

### Adding Shared Objects to Library Path ###

You need to add the shared objects to library path. For single session use you can do:

```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/crfpp/.libs/
```

Or add it to your ```.bashrc```

```
echo export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/crfpp/.libs/ >> ~/.bashrc
```


## Installing perl libraries ##

The version of perl on blake does not have all of the dependencies necessary to run ParsCit and so we need to install them. However, since we do not have root priviledges, we need to use the perl package ``local::lib`` which allows us to install dependencies to your account. 

### Perl Version ###

The version of perl we recommend using is installed on blake at: ```/opt/perl/bin/perl5.14.2```

### local::lib ##

Download the package, unzip and move into the folder

```
wget http://search.cpan.org/CPAN/authors/id/H/HA/HAARG/local-lib-2.000015.tar.gz
tar -xvzf local-lib-2.000015.tar.gz
cd local-lib-2.000015
```

Install the package in the following way. In my below example, I install this package and all subsequent dependecies to the directory ```~/perl```. If you would like to install elsewhere, just change this path in all occurrences.

```
/opt/perl/bin/perl5.14.2 Makefile.PL --bootstrap=~/perl
make test && make install
```

Add the following line to your ```~/.bashrc```

```
echo '[ $SHLVL -eq 1 ] && eval "$(perl -I$HOME/perl/lib/perl5 -Mlocal::lib=$HOME/perl)"' >>~/.bashrc
```
 
You can check that local::lib is installed correctly, by checking that the documentation exists. From outside of the local-lib-2.000015 directory, run:

```
/opt/perl/bin/perldoc -l local::lib
```

If a path to the documentation is given the installation was a success.

For additional details on installing local::lib see: http://search.cpan.org/~haarg/local-lib-2.000015/lib/local/lib.pm


### Dependencies ###

We must have the following dependencies installed:
  - Class::Struct
  - Getopt::Long
  - Getopt::Std
  - File::Basename
  - File::Spec
  - FindBin
  - HTML::Entities
  - IO::File
  - POSIX
  - __XML::Parser__
  - __XML::Twig__
  - __XML::Writer__
  - __XML::Writer::String__
 
It appears that the non-__bold__ face dependencies are already installed on blake, we need to install the __bolded__ dependencies. You can double check that they are installed by doing 

```
/opt/perl/bin/perldoc -l <name>
```

### XML::Parser ###

To install the package use the following commands. If you used a different library path in the above step, remember to make proper edits.

```
wget http://search.cpan.org/CPAN/authors/id/M/MS/MSERGEANT/XML-Parser-2.36.tar.gz
tar -xvzf XML-Parser-2.36.tar.gz
cd XML-Parser-2.36
/opt/perl/bin/perl5.14.2 Makefile.PL LIB=~/perl/lib/perl5/
make
make test
make install
```

Check that it was installed correctly:

```
cd ..
/opt/perl/bin/perldoc -l XML::Parser
```

### XML::Twig ###
The package XML::Parser is a prereq. to this package, please make sure it is installed.

To install the Twig package, do the following:

```
wget http://search.cpan.org/CPAN/authors/id/M/MI/MIROD/XML-Twig-3.49.tar.gz
tar -xvzf XML-Twig-3.49.tar.gz
cd XML-Twig-3.49
/opt/perl/bin/perl5.14.2 Makefile.PL -y LIB=~/perl/lib/perl5
make 
make test
make install
```

To check that it is installed:


```
cd ..
/opt/perl/bin/perldoc -l XML::Twig
```

### XML::Writer ###

To install the package, do the following:

```
XML::Writer
wget http://search.cpan.org/CPAN/authors/id/J/JO/JOSEPHW/XML-Writer-0.625.tar.gz
cd XML-Writer-0.625
tar -xvzf XML-Writer-0.625.tar.gz
/opt/perl/bin/perl5.14.2 Makefile.PL  LIB=~/perl/lib/perl5
make
make test
make install
```

To check that it installed:

```
cd ..
/opt/perl/bin/perldoc -l XML::Writer
```

### XML::Writer::String ###

To install do:

```
wget http://search.cpan.org/CPAN/authors/id/S/SO/SOLIVER/XML-Writer-String-0.1.tar.gz
tar -xvzf XML-Writer-String-0.1.tar.gz
cd XML-Writer-String-0.1
/opt/perl/bin/perl5.14.2 Makefile.PL  LIB=~/perl/lib/perl5
make
make test
make install
```

To check that it installed:

```
cd ..
/opt/perl/bin/perldoc -l XML::Writer::String
```

### Setting path to perl in scripts ###

The ```bin``` directory contains scripts to run the ParsCit code. You need to change the shebang lines to point the scripts to the version of perl given above. The following converts each of the versions of perl given in the scripts to the correct version:

```
sed -i "s/\#\!\/usr\/bin\/perl/\#\!\/opt\/perl\/bin\/perl5.14.2/g" *.pl
sed -i "s/\#\!\/usr\/bin\/env perl/\#\!\/opt\/perl\/bin\/perl5.14.2/g" *.pl 
sed -i "s/\#\!\/opt\/ActivePerl-5.8\/bin\/perl/\#\!\/opt\/perl\/bin\/perl5.14.2/g" *.pl
```



