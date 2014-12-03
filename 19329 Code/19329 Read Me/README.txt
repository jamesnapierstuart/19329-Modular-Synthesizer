"Wavetable Modular Synthesizer"
CANDIDATE NUMBER: 19329
Advanced Computer Music: G6003
Task1: Direct Sample Level DSP Routine for Sound Synthesis/Audio Effect
University of Sussex
November 2013

BUILT ON SUPERCOLLIDER 3.6 (not 3.6.5)
MAC OSX VERSION 10.8.4

***
Important Note: 

Testing in SC 3.6.5 displays an error message for the freqscopeview.
ScopeOut2: Requested scope buffer unavailable! (index: 0, channels: 1, size: 512)
It doesn't effect the sound output or even the frequency display in any way.
It seems to throw an error but is not damaging.
Having built in 3.6 no error prevailed here, within 3.6.5 it arises but since no trouble is caused and this is a GUI system, it can be looked over.
Worth noting but it does not impact the synthesiser. (This error is not consistent across versions).
***


Wavetable Modular Synthesizer 1.0


Welcome, to get started create a new ModularSynthesizer class and call the .create method.


You'll notice two windows have been created. First is your "Synth Menu" the control centre to creating modules, available are "Wavetable Divider", "Sampler Synth" and an option to show a "Frequency Scope".
The second window is the "Modular Playground", the canvas window which, when a module is added, will show a graphical representation of the synth module.
Modules within the modular playground can be moved around, hide or show their synth controls, show their synth amplitude controls or can be deleted.
Each module has the option of routing to the main speaker output (0) or their particular Bus output starting at (16).
Also every individual module takes up to three inputs, which take the output from other modules' Bus output.
Therefore modular synthesis can be achieved by routing outputs of modules into the inputs of others.

TO QUIT:  To quit the system, either close the Modular Playground window or the Synth Menu window.

-----------------------
Wavetable Divider Synth:

This synthesiser is fundamentally a wavetable creator. The waveform plot at the bottom of the window displays the wavetable created at 1 cycle per second or 1hz.
Default settings have the wavetable points all set at 0.
You create wavetables by choosing different mathematical equations found in the pop up menus from Div.1 to Div.12.
E.g. choosing 2pi 360/0° (sine) (with divisions set to 1) will create a Sine Wave.
The divisions menu at the top of the window specifies how many divisions the waveform will be divided into.
When more than 1 division is selected, then the waveform is split into the number of divisions and we specify the mathematics for each division.
E.g. if 1…then only div1 menu mathematics will be used. If 2 divisions selected, then Div1 and Div2 menu mathematics will be used.
There is a variety of equations to choose from, but the fun starts when you alter the amount of divisions and play about with different sections of the waveform.

To see the waveforms of each individual Divisions mathematics, then click the '+' button next to the Div menu in question. 
You will see a 1hz plot of that specific equation, to help you decide whether to use it in your wavetable creation.

Frequency Knob, in the centre of the window correlates to the rate at which the waveform is played back. Scale = 10hz - 8000hz
Load and Save buttons are used to load and save pre-made presets or custom made presets.
LFO trigger button, changes the scale of the frequency knob to 0.1h - 20hz. (Turning the wave into a low frequency oscillator)

Presets loaded or saved, will have their name labelled at the top of the window. 
(Saving doesn't include the LFO parameter purposely, since found test users preferred to hear the preset at audio rate first before putting it back to 0-20 LFO mapping)

Example: As a quick tip, load up one of the presets in the 'presets' folder and play about with the different equations and divisions.

Note: All changes to the controls happen in real time.


-----------------------
Sampler Synth:

This is a sampler synthesiser. Meaning sound files (.WAV only) can be loaded up and played back.
Playback rate is changed by the main centred knob.
Playback rate scale = -200 to 200. meaning +200 will skip the buffer pointer every 200 samples going forward. -200 meaning buffer pointer will skip backwards every 200 samples.
The LFO trigger is again implemented to change the scale of playback rate knob to -20 to 20.
The Reset button rests, the playback rate to 0 (original speed).

Start and End position knobs are used to…you guessed it change the starting point of the sample and change where the sample ends.
This is particularly useful if you want to focus on a specific section of the sample.
Start position scale = 0 to numFrames of the sample.
End position = Numframes of sample to 0.

The sampler synth is constantly looping round, from the start position to the end position.
This means if a specific section of the sample was found altering the start/end pos then this section is continuously looped.

Altering the playback rate to higher speeds when tiny sections of the sample are specified can result in cyclic repetitions of the same sample points.
This means at faster rates, we can achieve pitch manipulation of these small looped samples since we start to enter audio rate.

Therefore we can use the Sampler Synth as way of creating waveforms, another form then of wavetable synthesis.


-----------------------
Frequency Scope:

A simple frequency scope the size of the computer screen to display the current frequency content.
Particularly fun/useful to see how LFO's can affect other signals.
Mostly used as entertainment graphics, to sit back and view your sound creation in all its glory.

Clicking the button will either show or hide the frequency scope depending on its current state.


-----------------------
Inputs = 1: Ring Modulation 2: Amplitude Modulation 3. Frequency Modulation
-----------------------
-----------------------
Outputs 0: Main Speaker Output (Stereo Only) 2. Bus Output
-----------------------



Notes:

* Each module created has a Bus output starting at 16 and incrementing by two each time. (e.g. module1 - Bus = 16, module2 - Bus = 18)

* WaveTable divider takes a while to load up and to load presets, since it needs time to calculate mathematics.

* When a module is deleted, the synth or buffer is freed. Also any subsequent modules created will keep the Bus ordering rather than every Bus output shifting.

* Synthesizer windows can only be hidden using their graphical representation counterparts within the modular playground view.

* Presets for the Wavetable Divider can be found in the "presets" folder.

* Recording Shortcut: (If you like what you here and what a quick recording)
s.record; // Record the sound to disk
s.stopRecord; // Stop recording

-----------------------
Sample Disclaimer:
The sample sounds folder includes samples taken from Apple's Logic Pro 9 software.
Since this project is only for educational purposes and not for commercial use, they are used just as tools of examples/experimentation.



Thanks and enjoy!

Sound examples: see https://soundcloud.com/jamesnapierstuart

