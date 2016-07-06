### Experiments with the webaudio api

Trying out the components of the webaudio api and how they work together

[click here to see how it works. http://entelijan.net/webaudio/](http://entelijan.net/webaudio/)

#### Ideas
* Designe a sound with a reusable ADSR envelope
* Implemet some sound filters where the cutoff frequency is controlled by the pitch, an envelope and/or a modulator
* Make a metallic sound using inharmonic partials
* Synchronize a new sequence of of notes with another already plying sequence
* Tryout some modulated delay. Do this differently on the left and right output to widen the sound.
* Make some waveforms using AudioBuffers.

#### Implemented Ideas
* Make a reusable noise AudioNode. Implemented in 'noise'
* Create some kind of 'TremoloNode'. Implemented in 'noise'

#### TODOs
* Switch to latest version of scalaJS
* Fix the 'mouse leave canvas' bug in doctus.

#### Implemented TODOs
