# Balance of the Planet

This repository contains a slightly reworked version of 2013 Java version released by Chris Crawford on his web site 
https://www.erasmatazz.com/library/source-code/index.html

Original game dates from 1990 and was written in Pascal for Macinthosh and then ported to IBM compatible PC
https://en.wikipedia.org/wiki/Balance_of_the_Planet

It can be played online here: https://archive.org/details/BalanceOfThePlanet

About Chris Crawford: https://en.wikipedia.org/wiki/Chris_Crawford_(game_designer)

# Content of repository

* original branch: unmodified Java source code bundled in an Eclipse project (build using JDK1.8 but more recent should work)
* main branch: 
** reworked version to include all resource in a single jar file
** docs directory: contains executable resulint jar file and index.html that uses cheerPJ ( https://cheerpj.com/ ) which is the modern way to run Java inside a browser

# trying online (proof-of-concept)

This is just a trial to show the jar can be run online. It is available from this URL
https://namip-computer-museum.github.io/balance-of-planet

Known limitations
* URL links will not work (external references blocked)
* no logging (code not modified but no way to write to target log file)
* limitations from code itself, e.g. simulation seems to run directly from 2013 to 2072, not yet investigated

