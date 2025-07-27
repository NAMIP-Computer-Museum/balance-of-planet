# Balance of the Planet

## Introduction

Balance of the Planet is a simulation video game created by Chris Crawford in 1990, 
designed to educate players about the complex relationships between economic activity and environmental sustainability

About Chris Crawford: https://en.wikipedia.org/wiki/Chris_Crawford_(game_designer)

Original game dates was written in Pascal for Macintosh and then ported to IBM compatible PC
https://en.wikipedia.org/wiki/Balance_of_the_Planet

Quoting Chris Crawford from his website: https://www.erasmatazz.com/library/software-projects-for-volun/balance-of-the-planet.html
*"I self-published the first version of Balance of the Planet in 1990. It was quite advanced for the time, 
but I was never satisfied with it. In 2012 (if memory serves), I decided to build a new and better version in Java. 
I spent about 16 months working on the project, completing it in the fall of 2013. 
I then announced it to the world as a free download. Of course, not many people had Java installed on their computers, 
and I made no effort to publicize it. I had hoped that perhaps some organization might decide to popularize it. 
Nothing came of it. I’m sure that, had I been a more aggressive salesman, I could have found a nice home for the software. 
But I hate selling myself, so the whole thing quietly died."*

There was also a crowdfunding attempt to make it freely available online.

Note the original game can actually be played online through emulation on internet archive
* Original MacOSe: https://archive.org/details/BalanceOfThePlanet
* MSDOS (DOS BOX): https://archive.org/details/msdos_Balance_of_the_Planet_1990

This repo explores the 2013 Java version which actually compiles without problems and how to make it available online in 2025.
The approach selected so far is to translate to webassembly using https://cheerpj.com

## Content of repository

This repository contains a slightly reworked version of 2013 Java version released by Chris Crawford on his web site 
https://www.erasmatazz.com/library/source-code/index.html

* original branch: unmodified Java source code bundled in an Eclipse project (build using JDK1.8 but more recent should work)
* main branch: 
** reworked version to include all resource in a single jar file
** docs directory: contains executable resuling jar file and index.html that uses cheerPJ ( https://cheerpj.com/ ) which is the modern way to run Java inside a browser

## Trying online (proof-of-concept)

This is just a trial to show the jar can be run online. It is available from this URL
https://namip-computer-museum.github.io/balance-of-planet

Known limitations
* limitations from code itself, e.g. simulation seems to run directly from 2013 to 2072, not yet investigated
* URL links will not work (external references blocked)
* no logging (code not modified but no way to write to target log file)
* not sure how long cheerjs will be available (no "low level" executable is available)
