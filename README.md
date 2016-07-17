### Experiments with the webaudio api

Trying out the components of the webaudio api and how they work together

[click here to see how it works. http://entelijan.net/webaudio/](http://entelijan.net/webaudio/)

#### Ideas
* Implemet some sound filters where the cutoff frequency is controlled by the pitch, an envelope and/or a modulator
* Synchronize a new sequence of of notes with another already plying sequence
* Tryout some modulated delay. Do this differently on the left and right output to widen the sound.
* Make some waveforms using AudioBuffers.
* Make some FM-Synth

#### Implemented Ideas
* Make a metallic sound using inharmonic partials. 'metallic'
* Designe a sound with a reusable ADSR envelope. 'ADSR'
* Make a reusable noise AudioNode. 'noise'
* Create some kind of 'TremoloNode'. 'noise's

#### TODOs
* Fix the 'mouse leave canvas' bug in doctus.
* Fix the 'wrong point when scaled' bug in doctus. On mobile devices if the screen is zoomed in, the points 
created during point events are not correct. To fix this make an automatic deployment of the doctus showcase.

#### Implemented TODOs
* Switch to latest version of scalaJS
