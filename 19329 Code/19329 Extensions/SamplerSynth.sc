/*
Sampler Class,
A Sampler Synthesizer that loads in a waveform and plays it back.
Rate of playback can be changed, as well as looping the waveform.
Start and end position can be manipulated.
Reversing samples is also achievable, with rate manipulation.

Part of the Wavetable Modular Synthesizer Project.
(c) 2013-2014, Candidate Number 19329
Advanced Computer Music, University of Sussex, Autumn 2013.
*/

SamplerSynth {

	var w, v, cvp, titlecv, titletxt, scope;
	var wNo;
	var <>winNo = 1;
	var font, font2;
	var array;
	var fknob;
	var cspec;
	var cspec2;
	var cspec3;
	var lfob;
	var lfoText, loopText;
	var lfoOn = false;
	var lfoKnob = false;
	var loopOn = true;
	var <>synth;
	var loopbs;
	var textFreq;
	var myPath;
	var loadButton;
	var buff;
	var constantBufNum;
	var resetb;
	var presetText;
	var playbackbs;
	var bufTest;
	var posText;
	var sampleText;
	var loadGUI;
	var syncUp;
	var s;
	var bufLoad = 0;
	var <>busIn = 128;

	*new {
		^super.newCopyArgs();
	}

	setSynth { arg synth;
		this.synth = synth;
	}

	setWindowNumber { arg wn;
		this.winNo = wn;
	}

	setBus { arg busNo;
		this.busIn = busNo;
	}

	setScope {arg bus;
		scope.inBus = bus;
	}

	create {

		"Loading Sampler Synth...please wait...".postln;
		s = Server.local;

		font = Font("Chalkduster", 35, true);
		font2 = Font("Chalkduster", 16);

		// make sure bounds are all ok here!
		w = Window("Sampler", Rect(500, 100, 360, 540), false);
		v = UserView(w, Rect(0, 0, 500, 580));
		v.background = Color.grey;
		titlecv = CompositeView(v, Rect(0, 0, 500, 80));
		titlecv.background = Color.grey(0.3);
		titletxt = StaticText(titlecv, Rect(20, -10, 500, 80));
		titletxt.string = "SAMPLER";
		titletxt.font_(font);
		titletxt.stringColor = Color.white;

		cvp = CompositeView(v, Rect(0, 80, 360, 240));
		scope = FreqScopeView(cvp);
		scope.active_(true);
		// In Bus
		scope.inBus = 0;

		// Preset Text ---- delete!! )replace with buffer name if possible)
		presetText = StaticText(titlecv, Rect(20, 50, 460, 25));
		presetText.string = "SAMPLE:";
		presetText.font_(font2);
		presetText.stringColor = Color.white;

		// textfield for keeping track:
		sampleText = TextField(titlecv, Rect(100, 50, 250, 25));
		sampleText.backColor_(Color.grey);
		sampleText.font_(font2);
		sampleText.stringColor = [Color.white, Color.black].choose;

		// Start/End pos text:
		posText = Array.fill(2, {arg i; StaticText(v, Rect(103 + (i*103), 505, 100, 30))});
		posText[0].string = "Start Pos.";
		posText[1].string = "End Pos.";

		// Window Number
		wNo = StaticText(titlecv, Rect(300, 20, 200, 25));
		wNo.string = "S:" + winNo;
		wNo.font_(font2);
		wNo.stringColor = Color.white;

		// Reset button:
		resetb = Button(v, Rect(20, 370, 80, 35));
		resetb.states_([["RESET", Color.white, Color.grey(0.3, 0.7)]]);
		resetb.mouseDownAction_({
			resetb.states_([["RESET", Color.white, Color.red(0.3, 0.7)]]);
			fknob.value= 0.5025;
			synth.set(\rate, 1);
			lfob.states_([["OFF", Color.white, Color.grey(0.3, 0.7)]]);
			if(lfoOn == true, {
				lfoOn = false;
				lfoKnob = false;
			});
		});

		// Reset up action for colour flash
		resetb.mouseUpAction_({
			resetb.states_([["RESET", Color.white, Color.grey(0.3, 0.7)]]);
		});

		// Sample Load
		loadButton = Button(v, Rect(100, 90, 140, 30));
		loadButton.states_([["LOAD SAMPLE...", Color.white, Color.grey(0.3, 0.7)]]);
		loadButton.action_({arg i;
			var b;

			b = Buffer.loadDialog(s, action: { arg buffer;
				var a;
				bufTest = buffer;
				synth.set(\bufnum, (buffer.bufnum), \rate, 1, \start, 0, \end, buffer.numFrames);
			});

		});

		// LFO button:
		lfob = Button(v, Rect(260, 370, 40, 40));
		lfob.states_([["OFF", Color.white, Color.grey(0.3, 0.7)], ["ON", Color.white, Color.red(0.5, 0.7)]]);
		lfob.action_({arg i;
			if(lfoOn == false, {
				lfoOn = true;
				lfoKnob = true;
				lfob.states_([["ON", Color.white, Color.red(0.5, 0.7)]]);
				synth.set(\rate, cspec2.map(fknob.value));
				},
				{
					lfoOn = false;
					lfoKnob = false;
					lfob.states_([["OFF", Color.white, Color.grey(0.3, 0.7)]]);
					synth.set(\rate, cspec.map(fknob.value));
			});
		});

		// Lfo text
		lfoText = StaticText(v, Rect(265, 340, 100, 30));
		lfoText.string = "LFO Trigger";
		lfoText.stringColor = Color.white;

		// Loop text:
		loopText = StaticText(v, Rect(20, 340, 100, 30));
		loopText.string = "Reset Trigger";
		loopText.stringColor = Color.white;

		// Playback rate text:
		textFreq = StaticText(v, Rect(130, 420, 200, 20));
		textFreq.string = "PLAYBACK RATE";

		// Frequency/Rate knob
		fknob = Knob(v, Rect(140, 340, 80, 80));
		fknob.mode = \vert;
		fknob.value= 0.5025;
		cspec= ControlSpec(-200, 200, 'linear', 0, 0);
		cspec2= ControlSpec(-20, 20, 'linear', 0, 0);
		fknob.action_({
			if(lfoOn == false, {
				synth.set(\rate, cspec.map(fknob.value));
				},
				{
					synth.set(\rate, cspec2.map(fknob.value));
			});
		});

		// Start and finish buffer: (changing the start and end position)
		playbackbs = Array.fill(2, {arg i; Knob(v, Rect(100 + (i*100), 450, 60, 60))});
		// Every time the start knob is changed, change start.
		playbackbs.do{arg i;
			i.mode = \vert;
		};
		playbackbs[0].value = 0;
		playbackbs[1].value = 1;

		// Start position knob
		playbackbs[0].action_({arg i;
			if(bufTest != nil) {
				cspec3= ControlSpec(0, bufTest.numFrames, 'linear', 0, 0);   // map values
				synth.set(\start, cspec3.map(playbackbs[0].value));
			};
		});
		// End position knob
		playbackbs[1].action_({arg i;
			if(bufTest != nil) {
				cspec3= ControlSpec(0, bufTest.numFrames, 'linear', 0, 0);   // map values
				synth.set(\end, cspec3.map(playbackbs[1].value));
			};
		});

		// Show window
		w.front;
		w.userCanClose_(false);
		w.onClose_({scope.kill});
		w.onClose_({synth.free; bufLoad.free; bufTest.free});
		"Loaded Sampler".postln;
	}

	getSynth {
		^synth.value;
	}

	getKnobValue {
		^fknob.value;
	}

	closeWindow {
		w.userCanClose_(true);
		w.close;
	}

	hideWindow {
		w.visible = false;
	}

	showWindow {
		w.visible = true;
	}

}