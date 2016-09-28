### Experiments with the webaudio api

Trying out the components of the webaudio api and how they work together

[click here to see how it works. http://entelijan.net/webaudio/](http://entelijan.net/webaudio/)

#### build and run doctus-webaudio-tryout

##### Prerequisites
* Have [SBT](http://www.scala-sbt.org/) 0.13.x installed on your computer
* Clone doctus on your computer and publish it locally on your computer
  * Call 'git clone https://github.com/wwagner4/doctus1.git' anywhere on your computer.
  * Run 'sbt publish-local' in the base directory of doctus.

##### Build
* Clone doctus-webaudio-tryout on your computer
  * Call 'git clone https://github.com/wwagner4/doctus-webaudio-tryout.git'
* Run 'sbt' in the base directory of doctus-webaudio-tryout
  * Use the following sbt commands to build a running version of doctus-webaudio-tryout. 'test', 'fastOptJS'.
  * Use the following sbt command to be able to import doctus-webaudio-tryout into eclipse. 'eclipse'

##### Run
* When successfully built open doctuswebaudiotryout-scalajs/index.html in chrome.


#### Ideas
* [Modal Synthesis](https://ccrma.stanford.edu/~bilbao/booktop/node14.html)
* [Digital waveguide synthesis](https://en.wikipedia.org/wiki/Digital_waveguide_synthesis)
* Tryout some modulated delay. Do this differently on the left and right output to widen the sound.
* Make some waveforms using AudioBuffers. (shall we really implement waveforms)
* Make the ADSR ramping and decay logarithmic


#### Implemented Ideas
* [Karplus-Strong-Algorithmus](https://de.wikipedia.org/wiki/Karplus-Strong-Algorithmus)
* Added some wha wha effect
* [Ring modulation synthesi](https://en.wikibooks.org/wiki/Sound_Synthesis_Theory/Modulation_Synthesis)
* Make an Oscilator with a square wave. Filter works with square wave
* FM-Synth
* Make an example using delay
* Synchronize a new sequence of of notes with another already plying sequence (melody)
* Make a panning Example
* Implement some sound filters where the cutoff frequency is controlled by the pitch, an envelope and/or a modulator
* Make a metallic sound using inharmonic partials. 'metallic'
* Design a sound with a reusable ADSR envelope. 'ADSR'
* Make a reusable noise AudioNode. 'noise'
* Create some kind of 'Tremolo Controller'. 'noise's

#### TODOs
* Check why noise has delay on portable devices
* Fix the 'mouse leave canvas' bug in doctus.

#### Implemented TODOs
* Check why ADSR sounds unstable during sustain
* Check why melody does not work any longer
* Switch to latest version of scalaJS
* Fix the 'wrong point when scaled' bug in doctus. On mobile devices if the screen is zoomed in, the points
created during point events are not correct. To fix this make an automatic deployment of the doctus showcase.
