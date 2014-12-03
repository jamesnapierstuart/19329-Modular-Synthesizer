/*
Task1: Direct Sample Level DSP Routine for sound synthesis/audio effect:

Wavetable Modular Synthesizer project:
A GUI environment, that allows creation of multiple synthesizers to be patched together.

Includes Wavetable Divider synthesizer, a tool for wavetable creation/manipulation.
Mathematical functions are used to define the values of individual floats within an array.
These floats correspond to amplitude levels, and form the shape of a wave.
The wave can be divided up into sections and the user can choose specific mathematical equations
for each division.

Includes Sampler synthesizer, which manipulates audio loaded in by user on a sample by sample level.
The playback rates, start and end positions are controllable and reversing the sample is possible.

Both synthesizers have one output to a defined bus, and three possible inputs.
Input1 = Ring Modulation, Input2 = Amplitude Modualtion. Input3 = Frequency Modulation.
Both have the ability of becoming LFO's low frequency oscillators.

The combination of the wavetable divider creation, the sampling manipulation and the ability to
patch outputs of synths into multiple inputs of others, creates vast possibility for sound creation.
Therefore deep sound design is acheivable.

See READ ME (for more details)

Part of the Wavetable Modular Synthesizer Project.
(c) 2013-2014, Candidate Number 19329
Advanced Computer Music, University of Sussex, Autumn 2013.
*/


// To start, create a new ModularSynthesizer, and call the .create method.
(
a = ModularSynthesizer();
a.create;
)