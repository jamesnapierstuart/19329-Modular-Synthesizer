/*
Modular Synthesizer Class,
The core of the modular synthesizer project.

An environment that utilises the Wavetable Divider and Sampler Synth classes.
Allows user to create multiple instances of these synths.
And patch them together i.e. plug outputs to inputs.
Therefore allowing for additive synthesis, ring, amplitude and frequency modulation.
And combinations of these three types of modulation too.

Limited only by the amount of busses available, usually projects don't exceed max.
Possible combinations of ins/outs are vast, allowing deep sound creation.

Part of the Wavetable Modular Synthesizer Project.
(c) 2013-2014, Candidate Number 19329
Advanced Computer Music, University of Sussex, Autumn 2013.
*/

ModularSynthesizer {

	var scrollView, w, v, b, b2, b3, view, cv, cv2, cv3;
	var menuCV, plusB;
	var oscText, oscillatorCV, oscModText, oscB;
	var oscOuts, oscOutText;
	var waveTableDividers;
	var loadSynth;
	var loadGui;
	var oscIns;
	var oscDelete;
	var oscMute;
	var oscillatorNo = 1;
	var oscNo = 0;
	var busNo = 16;
	var textN = 0;
	var hideN = 0;
	var tN = 0;
	var samtN = 0;
	var inTrues;
	var oscOutBs;
	var reboot;
	var oscAmp;
	var busArray;
	var bt = 16;
	var scopeV;
	var scope;
	var samplerCV, samText, samModText, samB, samDelete, samMute;
	var samplers, samIns, samTrues, samOutBs, samAmp;
	var samplerNo = 1;
	var samNo = 0;
	var samTextN = 0;
	var samHideN = 0;
	var samOuts, samOutText;
	var s;

	*new {
		^super.newCopyArgs();
	}


	create {


		// Load up the synthDef:
		loadSynth = {

			SynthDef("WaveTableDividerSynth", {arg out = 0, in1 = 0, in2 = 0, in3 = 0, sound = 0, bufnum, rate2 = 1, rate = 1, amAmp = 0.8, amp = 0.1;

				var input1, input2, input3, drySignal, rmSignal, amSignal, fmSignal, signals, outs, output;
				var dryRm, dryAm, dryFm;
				var rmAm, rmFm, amFm;
				var rmAmFm;

				// In1 - RM, In2 - AM, In3 - FM.
				input1 = InFeedback.ar(in1, 1);
				input2 = InFeedback.ar(in2, 1);
				input3 = InFeedback.ar(in3, 1);

				// dry signal
				drySignal = PlayBuf.ar(1, bufnum, rate, 0, 0, 1);
				// rm modulation with in
				rmSignal = PlayBuf.ar(1, bufnum, rate, 0, 0, 1) * input1;
				// am modulation with in2
				amSignal = PlayBuf.ar(1, bufnum, rate, 0, 0, 1) * input2 + drySignal;
				// fm modulation with in3
				fmSignal = PlayBuf.ar(1, bufnum, rate * input3, 0, 0, 1);

				// rm with am
				rmAm = rmSignal + amSignal;
				// rm with fm
				rmFm = rmSignal + fmSignal;
				// am with fm
				amFm = amSignal + fmSignal;
				// rm + am + fm (all three)
				rmAmFm = rmSignal + amSignal + fmSignal;

				// dry with rm modulation
				dryRm = drySignal + rmSignal;
				// dry with am modulation
				dryAm = drySignal + amSignal;
				// dry with fm modulation
				dryFm = drySignal + fmSignal;

				// Array of all possible signal combinations
				signals = [
					drySignal,
					rmSignal,
					amSignal,
					fmSignal,
					rmAm,
					rmFm,
					amFm,
					rmAmFm,
					dryRm,
					dryAm,
					dryFm
				];

				// Set output
				output = Select.ar(sound, signals);

				// Output
				Out.ar(out, Pan2.ar(output*amp));
			}).store;

			SynthDef("SamplerSynth", {arg out = 0, in1 = 0, in2 = 0, in3 = 0, sound = 0, bufnum = 1000, rate = 1, loop = 1, start = 0, end = 0, trig = 1, amp = 0.2, amAmp = 0.8;

				var input1, input2, input3, drySignal, rmSignal, amSignal, fmSignal, signals, outs, output;
				var dryRm, dryAm, dryFm;
				var rmAm, rmFm, amFm;
				var rmAmFm;

				// In1 - RM, In2 - AM, In3 - FM.
				input1 = InFeedback.ar(in1, 1);
				input2 = InFeedback.ar(in2, 1);
				input3 = InFeedback.ar(in3, 1);

				// dry signal
				drySignal = BufRd.ar(1, bufnum, Phasor.ar(start, rate, start, end));
				// rm modulation with in
				rmSignal = BufRd.ar(1, bufnum, Phasor.ar(start, rate, start, end)) * input1;
				// am modulation with in2
				amSignal = BufRd.ar(1, bufnum, Phasor.ar(start, rate, start, end)) * input2 + drySignal;
				// fm modulation with in3
				fmSignal = BufRd.ar(1, bufnum, Phasor.ar(start, rate*input3, start, end));

				// rm with am
				rmAm = rmSignal + amSignal;
				// rm with fm
				rmFm = rmSignal + fmSignal;
				// am with fm
				amFm = amSignal + fmSignal;
				// rm + am + fm (all three)
				rmAmFm = rmSignal + amSignal + fmSignal;

				// Array of all possible signal combinations
				signals = [
					drySignal,
					rmSignal,
					amSignal,
					fmSignal,
					rmAm,
					rmFm,
					amFm,
					rmAmFm,
				];

				// Set output
				output = Select.ar(sound, signals);

				Out.ar(out, Pan2.ar(output)*amp);

			}).store;

			"Loaded Synth".postln;
		};

		// Load up the GUI:
		loadGui = {

			var left = 0@0;
			var right = 0@0;
			var downPos, childDrag, childStartPos = 0@0;

			/*
			Set Up the Initial GUI Objects:
			*/
			// Main Window Menu
			w = Window("Synth Menu", Rect((Window.availableBounds.left+200), (Window.availableBounds.top+600), 300, 230), false, scroll:true);

			// Set up views
			scrollView = ScrollView(bounds:Rect(0,0,1000,500).center_(Window.availableBounds.center)).hasBorder = true;
			v = CompositeView(scrollView, scrollView.bounds);
			view = UserView(v, Rect(0, 0, v.bounds));

			scrollView.name = "Modular Playground";
			scrollView.backColor = Color.defaultArgs;
			// scrollView.backColor_(Color.black);
			// scrollView.backColor = Color.grey(0.8, 1);

			// Menu items for window
			menuCV = Array.fill(3, {arg i; CompositeView(w, Rect(10, 10 + (i*70), 280, 60))});
			// Colour array of menu composite views
			menuCV.do { arg cv;
				cv.background = Color.grey;
			};

			// Array of buttons for opening modules
			plusB = Array.fill(5, {arg i; Button((menuCV[i]), Rect(5, 5, 270, 50))});
			// Labels for the buttons
			plusB[0].states_([["WAVETABLE DIVIDER +", Color.black, Color.white]]);
			plusB[1].states_([["SAMPLER SYNTH +", Color.black, Color.white]]);
			plusB[2].states_([["FREQUENCY SCOPE +/-", Color.black, Color.white]]);

			// Colour array of the plus buttons
			plusB.do { arg b;
				b.background = Color.white;
			};

			/*
			View Actions:
			*/
			scrollView.mouseDownAction_({ |view, x, y|
				downPos = x@y;
				if (childDrag.notNil) {
					childStartPos = childDrag.bounds.origin;
				};
			});
			// make the childDrag co-ordinates nil
			scrollView.mouseUpAction_({ |view, x, y|
				childDrag = nil;
				//tmp= tmp.add([src, dst]);
			});
			// Work only if childDrag isn't nil
			scrollView.mouseMoveAction_({ |view, x, y|
				if (childDrag.notNil) {
					var pt = x@y;
					var diff = pt - downPos;
					var newPos = diff + childStartPos;
					childDrag.moveTo(newPos.x, newPos.y)
				};
			});

			/*
			Set arrays up for the wavetable divider modules:
			*/
			// List for all composite views inside the scrollView
			oscillatorCV = List();
			// List for all the text within composite views
			oscText = List();
			oscModText = List();
			// List for all the buttons within composite views
			oscB = List();
			// delete button list
			oscDelete = List();
			// mute button
			oscMute = List();
			// List for the waveTable dividers!
			waveTableDividers = List();
			// List for the text inputs
			oscIns = List();
			// List of inTrues
			inTrues = List();
			// List of output buttons:
			oscOutBs = List();
			// List of amp buttons:
			oscAmp = List();
			// List of busses:
			busArray = List();

			/*
			Set arrays up for the sampler modules:
			*/
			// List for all composite views inside the scrollView
			samplerCV = List();
			// List for all the text within composite views
			samText = List();
			samModText = List();
			// List for all the buttons within composite views
			samB = List();
			// delete button list
			samDelete = List();
			// mute button
			samMute = List();
			// List for the waveTable dividers!
			samplers = List();
			// List for the text inputs
			samIns = List();
			// List of inTrues
			samTrues = List();
			// List of output buttons:
			samOutBs = List();
			// List of amp buttons:
			samAmp = List();

			/*
			Wavetable Divider Actions:
			*/
			plusB[0].action_({arg b, window, a;

				var oscN = oscNo;
				var hd1 = hideN;
				var tn = textN, tn2 = textN+1, tn3 = textN+2;
				var bn = busNo;
				var oscillatorN = oscillatorNo;
				var busTest = bt;

				// Set up wavetable divider synthesizer:
				waveTableDividers.add(WavetableDivider.new);
				waveTableDividers[oscNo].setSynth("WaveTableDividerSynth");
				waveTableDividers[oscNo].setXbuf(Array.fill(44100, {arg i; 0; }));
				waveTableDividers[oscNo].setSynth(Synth("WaveTableDividerSynth", [\bufnum, waveTableDividers[oscNo].getXbuf.bufnum]));
				waveTableDividers[oscNo].setWindowNumber(oscillatorNo);
				waveTableDividers[oscNo].new2;

				// Set the Bus output
				waveTableDividers[oscNo].getSynth.set(\out, 0);

				// Start with the dry carrier signal
				waveTableDividers[oscNo].getSynth.set(\sound, 0);

				// Add GUI representation of synths to the view
				oscillatorCV.add(CompositeView(scrollView, Rect(rrand(20, 400), rrand(10, 300), 220, 120)));
				oscillatorCV[oscNo].visible = true;
				oscillatorCV[oscNo].background = Color.grey(0.3);
				oscillatorCV[oscNo].mouseDownAction_({ |view| childDrag = view; false });

				// Add wavetable text to view
				oscText.add(StaticText(oscillatorCV[oscNo], Rect(10, 50, 200, 20)));
				oscText[oscNo].string = "    WT" + oscillatorNo;
				oscText[oscNo].stringColor = Color.white;

				// Add text fields for inputs
				oscIns.add(TextField(oscillatorCV[oscNo], Rect(20, -3, 40, 30)));
				oscIns.add(TextField(oscillatorCV[oscNo], Rect(80, -3, 40, 30)));
				oscIns.add(TextField(oscillatorCV[oscNo], Rect(140, -3, 40, 30)));

				// Input 1 methods
				oscIns[tn].canReceiveDragHandler = false;
				oscIns[tn].background = Color.black;
				oscIns[tn].stringColor = Color.white;
				oscIns[tn].value = "0";

				// Input 2 methods
				oscIns[tn2].canReceiveDragHandler = false;
				oscIns[tn2].background = Color.black;
				oscIns[tn2].stringColor = Color.white;
				oscIns[tn2].value = "0";

				// Input 3 methods
				oscIns[tn3].canReceiveDragHandler = false;
				oscIns[tn3].background = Color.black;
				oscIns[tn3].stringColor = Color.white;
				oscIns[tn3].value = "0";

				// Action for input 1:
				oscIns[tn].action = {arg field;
					case
					// In2 and In3 are 0 i.e. not plugged in -- DRY or RM
					{(oscIns[tn2].value == "0") && (oscIns[tn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in1, 0, \sound, 0, \amp, 0.05);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 16, \sound, 1);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 18, \sound, 1);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 20, \sound, 1);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 22, \sound, 1);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 24, \sound, 1);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 26, \sound, 1);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 28, \sound, 1);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 30, \sound, 1);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 32, \sound, 1);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 34, \sound, 1);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 36, \sound, 1);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 38, \sound, 1);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 40, \sound, 1);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 42, \sound, 1);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 44, \sound, 1);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 46, \sound, 1);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 48, \sound, 1);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 50, \sound, 1);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 52, \sound, 1);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 54, \sound, 1);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 56, \sound, 1);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 58, \sound, 1);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 60, \sound, 1);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 62, \sound, 1);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 64, \sound, 1);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 66, \sound, 1);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 68, \sound, 1);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 70, \sound, 1);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 72, \sound, 1);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 74, \sound, 1);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 76, \sound, 1);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 78, \sound, 1);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 80, \sound, 1);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 82, \sound, 1);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 84, \sound, 1);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 86, \sound, 1);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 88, \sound, 1);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 90, \sound, 1);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 92, \sound, 1);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 94, \sound, 1);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 96, \sound, 1);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 98, \sound, 1);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 100, \sound, 1);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 102, \sound, 1);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 104, \sound, 1);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 106, \sound, 1);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 108, \sound, 1);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 110, \sound, 1);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 112, \sound, 1);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 114, \sound, 1);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 116, \sound, 1);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 118, \sound, 1);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 120, \sound, 1);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 122, \sound, 1);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 124, \sound, 1);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 126, \sound, 1);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 128, \sound, 1);};
					)}

					// In2 is plugged, In3 is not plugged in -- RM and AM
					{(oscIns[tn2].value != "0") && (oscIns[tn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in1, 0, \sound, 2);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 16, \sound, 4);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 18, \sound, 4);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 20, \sound, 4);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 22, \sound, 4);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 24, \sound, 4);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 26, \sound, 4);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 28, \sound, 4);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 30, \sound, 4);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 32, \sound, 4);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 34, \sound, 4);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 36, \sound, 4);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 38, \sound, 4);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 40, \sound, 4);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 42, \sound, 4);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 44, \sound, 4);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 46, \sound, 4);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 48, \sound, 4);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 50, \sound, 4);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 52, \sound, 4);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 54, \sound, 4);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 56, \sound, 4);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 58, \sound, 4);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 60, \sound, 4);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 62, \sound, 4);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 64, \sound, 4);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 66, \sound, 4);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 68, \sound, 4);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 70, \sound, 4);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 72, \sound, 4);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 74, \sound, 4);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 76, \sound, 4);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 78, \sound, 4);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 80, \sound, 4);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 82, \sound, 4);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 84, \sound, 4);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 86, \sound, 4);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 88, \sound, 4);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 90, \sound, 4);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 92, \sound, 4);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 94, \sound, 4);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 96, \sound, 4);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 98, \sound, 4);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 100, \sound, 4);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 102, \sound, 4);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 104, \sound, 4);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 106, \sound, 4);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 108, \sound, 4);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 110, \sound, 4);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 112, \sound, 4);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 114, \sound, 4);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 116, \sound, 4);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 118, \sound, 4);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 120, \sound, 4);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 122, \sound, 4);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 124, \sound, 4);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 126, \sound, 4);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 128, \sound, 4);};
					)}

					// In2 is not plugged, In3 is plugged in -- RM and FM
					{(oscIns[tn2].value == "0") && (oscIns[tn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in1, 0, \sound, 3);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 16, \sound, 5);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 18, \sound, 5);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 20, \sound, 5);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 22, \sound, 5);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 24, \sound, 5);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 26, \sound, 5);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 28, \sound, 5);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 30, \sound, 5);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 32, \sound, 5);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 34, \sound, 5);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 36, \sound, 5);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 38, \sound, 5);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 40, \sound, 5);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 42, \sound, 5);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 44, \sound, 5);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 46, \sound, 5);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 48, \sound, 5);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 50, \sound, 5);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 52, \sound, 5);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 54, \sound, 5);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 56, \sound, 5);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 58, \sound, 5);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 60, \sound, 5);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 62, \sound, 5);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 64, \sound, 5);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 66, \sound, 5);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 68, \sound, 5);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 70, \sound, 5);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 72, \sound, 5);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 74, \sound, 5);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 76, \sound, 5);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 78, \sound, 5);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 80, \sound, 5);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 82, \sound, 5);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 84, \sound, 5);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 86, \sound, 5);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 88, \sound, 5);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 90, \sound, 5);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 92, \sound, 5);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 94, \sound, 5);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 96, \sound, 5);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 98, \sound, 5);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 100, \sound, 5);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 102, \sound, 5);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 104, \sound, 5);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 106, \sound, 5);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 108, \sound, 5);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 110, \sound, 5);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 112, \sound, 5);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 114, \sound, 5);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 116, \sound, 5);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 118, \sound, 5);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 120, \sound, 5);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 122, \sound, 5);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 124, \sound, 5);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 126, \sound, 5);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 128, \sound, 5);};
					)}

					// In2 is plugged in and In3 is plugged in -- RM, AM, FM
					{(oscIns[tn2].value != "0") && (oscIns[tn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in1, 0, \sound, 6);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in1, 128, \sound, 7);};
					)};
				};

				// Actions for input 2:
				oscIns[tn2].action = {arg field;
					case
					// In1 and In3 are 0 i.e. not plugged in -- AM only
					{(oscIns[tn].value == "0") && (oscIns[tn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in2, 0, \sound, 0, \amp, 0.05);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 16, \sound, 2);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 18, \sound, 2);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 20, \sound, 2);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 22, \sound, 2);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 24, \sound, 2);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 26, \sound, 2);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 28, \sound, 2);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 30, \sound, 2);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 32, \sound, 2);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 34, \sound, 2);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 36, \sound, 2);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 38, \sound, 2);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 40, \sound, 2);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 42, \sound, 2);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 44, \sound, 2);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 46, \sound, 2);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 48, \sound, 2);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 50, \sound, 2);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 52, \sound, 2);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 54, \sound, 2);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 56, \sound, 2);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 58, \sound, 2);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 60, \sound, 2);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 62, \sound, 2);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 64, \sound, 2);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 66, \sound, 2);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 68, \sound, 2);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 70, \sound, 2);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 72, \sound, 2);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 74, \sound, 2);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 76, \sound, 2);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 78, \sound, 2);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 80, \sound, 2);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 82, \sound, 2);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 84, \sound, 2);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 86, \sound, 2);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 88, \sound, 2);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 90, \sound, 2);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 92, \sound, 2);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 94, \sound, 2);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 96, \sound, 2);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 98, \sound, 2);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 100, \sound, 2);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 102, \sound, 2);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 104, \sound, 2);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 106, \sound, 2);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 108, \sound, 2);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 110, \sound, 2);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 112, \sound, 2);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 114, \sound, 2);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 116, \sound, 2);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 118, \sound, 2);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 120, \sound, 2);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 122, \sound, 2);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 124, \sound, 2);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 126, \sound, 2);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 128, \sound, 2);};
					)}

					// In1 is plugged, In3 is not plugged in -- RM and AM
					{(oscIns[tn].value != "0") && (oscIns[tn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in2, 0, \sound, 1);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 16, \sound, 4);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 18, \sound, 4);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 20, \sound, 4);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 22, \sound, 4);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 24, \sound, 4);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 26, \sound, 4);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 28, \sound, 4);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 30, \sound, 4);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 32, \sound, 4);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 34, \sound, 4);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 36, \sound, 4);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 38, \sound, 4);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 40, \sound, 4);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 42, \sound, 4);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 44, \sound, 4);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 46, \sound, 4);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 48, \sound, 4);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 50, \sound, 4);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 52, \sound, 4);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 54, \sound, 4);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 56, \sound, 4);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 58, \sound, 4);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 60, \sound, 4);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 62, \sound, 4);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 64, \sound, 4);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 66, \sound, 4);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 68, \sound, 4);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 70, \sound, 4);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 72, \sound, 4);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 74, \sound, 4);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 76, \sound, 4);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 78, \sound, 4);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 80, \sound, 4);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 82, \sound, 4);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 84, \sound, 4);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 86, \sound, 4);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 88, \sound, 4);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 90, \sound, 4);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 92, \sound, 4);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 94, \sound, 4);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 96, \sound, 4);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 98, \sound, 4);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 100, \sound, 4);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 102, \sound, 4);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 104, \sound, 4);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 106, \sound, 4);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 108, \sound, 4);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 110, \sound, 4);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 112, \sound, 4);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 114, \sound, 4);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 116, \sound, 4);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 118, \sound, 4);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 120, \sound, 4);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 122, \sound, 4);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 124, \sound, 4);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 126, \sound, 4);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 128, \sound, 4);};
					)}

					// In1 is not plugged, In3 is plugged in -- AM and FM
					{(oscIns[tn].value == "0") && (oscIns[tn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in2, 0, \sound, 3);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 16, \sound, 6);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 18, \sound, 6);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 20, \sound, 6);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 22, \sound, 6);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 24, \sound, 6);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 26, \sound, 6);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 28, \sound, 6);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 30, \sound, 6);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 32, \sound, 6);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 34, \sound, 6);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 36, \sound, 6);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 38, \sound, 6);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 40, \sound, 6);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 42, \sound, 6);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 44, \sound, 6);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 46, \sound, 6);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 48, \sound, 6);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 50, \sound, 6);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 52, \sound, 6);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 54, \sound, 6);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 56, \sound, 6);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 58, \sound, 6);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 60, \sound, 6);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 62, \sound, 6);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 64, \sound, 6);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 66, \sound, 6);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 68, \sound, 6);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 70, \sound, 6);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 72, \sound, 6);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 74, \sound, 6);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 76, \sound, 6);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 78, \sound, 6);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 80, \sound, 6);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 82, \sound, 6);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 84, \sound, 6);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 86, \sound, 6);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 88, \sound, 6);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 90, \sound, 6);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 92, \sound, 6);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 94, \sound, 6);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 96, \sound, 6);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 98, \sound, 6);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 100, \sound, 6);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 102, \sound, 6);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 104, \sound, 6);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 106, \sound, 6);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 108, \sound, 6);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 110, \sound, 6);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 112, \sound, 6);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 114, \sound, 6);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 116, \sound, 6);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 118, \sound, 6);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 120, \sound, 6);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 122, \sound, 6);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 124, \sound, 6);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 126, \sound, 6);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 128, \sound, 6);};
					)}

					// In1 is plugged in and In3 is plugged in -- AM RM, FM
					{(oscIns[tn].value != "0") && (oscIns[tn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in2, 0, \sound, 5);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in2, 128, \sound, 7);};
					)};
				};

				// Actions for input 3:
				oscIns[tn3].action = {arg field;
					case
					// In1 and In2 are 0 i.e. not plugged in -- FM only
					{(oscIns[tn].value == "0") && (oscIns[tn2].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in3, 0, \sound, 0, \amp, 0.05);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 16, \sound, 3);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 18, \sound, 3);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 20, \sound, 3);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 22, \sound, 3);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 24, \sound, 3);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 26, \sound, 3);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 28, \sound, 3);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 30, \sound, 3);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 32, \sound, 3);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 34, \sound, 3);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 36, \sound, 3);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 38, \sound, 3);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 40, \sound, 3);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 42, \sound, 3);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 44, \sound, 3);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 46, \sound, 3);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 48, \sound, 3);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 50, \sound, 3);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 52, \sound, 3);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 54, \sound, 3);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 56, \sound, 3);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 58, \sound, 3);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 60, \sound, 3);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 62, \sound, 3);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 64, \sound, 3);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 66, \sound, 3);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 68, \sound, 3);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 70, \sound, 3);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 72, \sound, 3);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 74, \sound, 3);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 76, \sound, 3);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 78, \sound, 3);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 80, \sound, 3);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 82, \sound, 3);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 84, \sound, 3);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 86, \sound, 3);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 88, \sound, 3);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 90, \sound, 3);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 92, \sound, 3);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 94, \sound, 3);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 96, \sound, 3);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 98, \sound, 3);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 100, \sound, 3);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 102, \sound, 3);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 104, \sound, 3);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 106, \sound, 3);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 108, \sound, 3);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 110, \sound, 3);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 112, \sound, 3);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 114, \sound, 3);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 116, \sound, 3);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 118, \sound, 3);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 120, \sound, 3);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 122, \sound, 3);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 124, \sound, 3);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 126, \sound, 3);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 128, \sound, 3);};
					)}
					// In1 is plugged, In2 is not plugged in -- RM and FM
					{(oscIns[tn].value != "0") && (oscIns[tn2].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in3, 0, \sound, 1);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 16, \sound, 5);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 18, \sound, 5);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 20, \sound, 5);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 22, \sound, 5);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 24, \sound, 5);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 26, \sound, 5);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 28, \sound, 5);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 30, \sound, 5);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 32, \sound, 5);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 34, \sound, 5);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 36, \sound, 5);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 38, \sound, 5);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 40, \sound, 5);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 42, \sound, 5);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 44, \sound, 5);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 46, \sound, 5);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 48, \sound, 5);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 50, \sound, 5);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 52, \sound, 5);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 54, \sound, 5);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 56, \sound, 5);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 58, \sound, 5);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 60, \sound, 5);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 62, \sound, 5);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 64, \sound, 5);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 66, \sound, 5);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 68, \sound, 5);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 70, \sound, 5);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 72, \sound, 5);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 74, \sound, 5);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 76, \sound, 5);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 78, \sound, 5);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 80, \sound, 5);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 82, \sound, 5);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 84, \sound, 5);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 86, \sound, 5);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 88, \sound, 5);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 90, \sound, 5);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 92, \sound, 5);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 94, \sound, 5);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 96, \sound, 5);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 98, \sound, 5);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 100, \sound, 5);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 102, \sound, 5);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 104, \sound, 5);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 106, \sound, 5);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 108, \sound, 5);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 110, \sound, 5);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 112, \sound, 5);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 114, \sound, 5);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 116, \sound, 5);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 118, \sound, 5);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 120, \sound, 5);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 122, \sound, 5);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 124, \sound, 5);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 126, \sound, 5);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 128, \sound, 5);};
					)}

					// In1 is not plugged, In2 is plugged in -- AM and FM
					{(oscIns[tn].value == "0") && (oscIns[tn2].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in3, 0, \sound, 2);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 16, \sound, 6);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 18, \sound, 6);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 20, \sound, 6);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 22, \sound, 6);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 24, \sound, 6);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 26, \sound, 6);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 28, \sound, 6);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 30, \sound, 6);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 32, \sound, 6);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 34, \sound, 6);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 36, \sound, 6);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 38, \sound, 6);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 40, \sound, 6);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 42, \sound, 6);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 44, \sound, 6);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 46, \sound, 6);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 48, \sound, 6);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 50, \sound, 6);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 52, \sound, 6);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 54, \sound, 6);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 56, \sound, 6);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 58, \sound, 6);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 60, \sound, 6);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 62, \sound, 6);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 64, \sound, 6);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 66, \sound, 6);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 68, \sound, 6);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 70, \sound, 6);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 72, \sound, 6);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 74, \sound, 6);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 76, \sound, 6);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 78, \sound, 6);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 80, \sound, 6);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 82, \sound, 6);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 84, \sound, 6);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 86, \sound, 6);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 88, \sound, 6);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 90, \sound, 6);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 92, \sound, 6);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 94, \sound, 6);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 96, \sound, 6);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 98, \sound, 6);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 100, \sound, 6);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 102, \sound, 6);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 104, \sound, 6);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 106, \sound, 6);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 108, \sound, 6);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 110, \sound, 6);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 112, \sound, 6);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 114, \sound, 6);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 116, \sound, 6);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 118, \sound, 6);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 120, \sound, 6);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 122, \sound, 6);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 124, \sound, 6);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 126, \sound, 6);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 128, \sound, 6);};
					)}

					// In1 is plugged in and In2 is plugged in -- FM RM, AM
					{(oscIns[tn].value != "0") && (oscIns[tn2].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								waveTableDividers[oscN].getSynth.set(\in3, 0, \sound, 4);},
							"16", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								waveTableDividers[oscN].getSynth.set(\in3, 128, \sound, 7);};
					)};
				};

				// Output Buttons
				2.do{arg i; oscOutBs.add(Button(oscillatorCV[oscNo], Rect(20 + (i*50), 93, 40, 30)))};
				oscOutBs[hideN].states_([[busTest, Color.white, Color.black], [busTest, Color.white, Color.red] ]);
				oscOutBs[hideN+1].states_([["0", Color.white, Color.red], ["0", Color.white, Color.black]]);

				// Toggle Output Button
				oscOutBs[hd1].action_({arg i;
					waveTableDividers[oscN].getSynth.set(\out, busTest);
					oscOutBs[hd1].value = 1;
					oscOutBs[hd1+1].value = 1;
				});
				oscOutBs[hd1+1].action_({arg i;
					waveTableDividers[oscN].getSynth.set(\out, 0);
					oscOutBs[hd1].value = 0;
					oscOutBs[hd1+1].value = 0;
				});

				// Show/Hide Buttons
				2.do{arg i; oscB.add(Button(oscillatorCV[oscNo], Rect(120 + (i*48), 50, 45, 25)))};
				oscB[hideN].states_([["Show", Color.black, Color.grey]]);
				oscB[hideN+1].states_([["Hide", Color.black, Color.grey]]);
				// Show and Hide actions
				oscB[hideN].action_({arg i;
					waveTableDividers[oscN].showWindow;
				});
				oscB[hideN+1].action_({arg i;
					waveTableDividers[oscN].hideWindow;
				});

				// Delete Button
				oscDelete.add(Button(oscillatorCV[oscNo], Rect(168, 80, 45, 25)));
				oscDelete[oscNo].states_([["Delete", Color.black, Color.grey]]);
				oscDelete[oscNo].action_({arg i;
					waveTableDividers[oscN].closeWindow;
					oscillatorCV[oscN].remove;
				});

				// Osc amp button launches a small window
				oscAmp.add(Button(oscillatorCV[oscNo], Rect(120, 80, 45, 25)));
				oscAmp[oscNo].states_([["AMP", Color.black, Color.grey]]);
				oscAmp[oscNo].action_({arg i, w;
					var win, title, text, slid;
					win = UserView(nil, Rect(300, 400, 90, 250));
					win.background = Color.grey(0.3);
					title = CompositeView(win, Rect(0, 0, 90, 30));
					text = StaticText(title, Rect(10, 5, 90, 30));
					text.string = "WT" + oscillatorN + "AMP";
					text.stringColor = Color.white;
					slid = Slider(win, Rect(30, 60, 30, 150));
					slid.alwaysOnTop = true;
					slid.knobColor_(Color.grey(0.2));
					slid.value = 0.4;
					slid.action_({
						waveTableDividers[oscN].getSynth.set(\amp, slid.value);
					});
					title.background = Color.grey;
					win.front;
					win.alwaysOnTop = true;
					win.mouseLeaveAction_({arg view, x, y;
						view.close;
					});
				});

				// Rm, Am, FM text:
				oscModText = Array.fill(3, {arg i; StaticText(oscillatorCV[oscNo], Rect(30 + (i*60), 20, 200, 30))});
				oscModText[0].string = "RM";
				oscModText[1].string = "AM";
				oscModText[2].string = "FM";

				// Output Bus text
				oscOutText = StaticText(oscillatorCV[oscNo], Rect(25, 70, 30, 30));
				oscOutText.string = "OUT";

				// Increment oscNo.
				oscNo = oscNo +1;
				oscillatorNo = oscillatorNo +1;
				// Increment BusNo.
				busNo = busNo + 1;
				// Increment textN
				textN = textN + 3;
				// Incremenent hideN.
				hideN = hideN + 2;
				// Increment true number
				tN = tN + 3;
				// Increment bus test
				bt = bt + 2;
			});

			/*
			SAMPLER Actions:
			*/
			plusB[1].action_({arg b, window, a;

				var samN = samNo;
				var samhd1 = samHideN;
				var samtn = samTextN;
				var samtn2 = samTextN+1;
				var samtn3 = samTextN+2;
				var bn = busNo;
				var samplerN = samplerNo;
				var busTest = bt;


				// Set up wavetable divider synthesizer:
				samplers.add(SamplerSynth.new);
				samplers[samNo].setSynth(Synth("SamplerSynth"));
				samplers[samNo].setWindowNumber(samplerNo);
				samplers[samNo].create;
				samplers[samNo].setBus(busTest);

				// Set the Bus output
				samplers[samNo].getSynth.set(\out, 0);

				// Start with the dry carrier signal
				samplers[samNo].getSynth.set(\sound, 0);

				// Add GUI representation of synths to the view
				samplerCV.add(CompositeView(scrollView, Rect(rrand(20, 400), rrand(10, 300), 220, 120)));
				samplerCV[samNo].visible = true;
				samplerCV[samNo].background = Color.blue(0.4);
				samplerCV[samNo].mouseDownAction_({ |view| childDrag = view; false });

				// Add wavetable text to view
				samText.add(StaticText(samplerCV[samNo], Rect(10, 50, 200, 20)));
				samText[samNo].string = "    S" + samplerNo;
				samText[samNo].stringColor = Color.white;

				// Add text fields for inputs
				samIns.add(TextField(samplerCV[samNo], Rect(20, -3, 40, 30)));
				samIns.add(TextField(samplerCV[samNo], Rect(80, -3, 40, 30)));
				samIns.add(TextField(samplerCV[samNo], Rect(140, -3, 40, 30)));

				// Input 1 methods
				samIns[samtn].canReceiveDragHandler = false;
				samIns[samtn].background = Color.black;
				samIns[samtn].stringColor = Color.white;
				samIns[samtn].value = "0";

				// Input 2 methods
				samIns[samtn2].canReceiveDragHandler = false;
				samIns[samtn2].background = Color.black;
				samIns[samtn2].stringColor = Color.white;
				samIns[samtn2].value = "0";

				// Input 3 methods
				samIns[samtn3].canReceiveDragHandler = false;
				samIns[samtn3].background = Color.black;
				samIns[samtn3].stringColor = Color.white;
				samIns[samtn3].value = "0";

				// Action for input 1:
				samIns[samtn].action = {arg field;
					case
					// In2 and In3 are 0 i.e. not plugged in -- DRY or RM
					{(samIns[samtn2].value == "0") && (samIns[samtn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in1, 0, \sound, 0);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 16, \sound, 1);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 18, \sound, 1);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 20, \sound, 1);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 22, \sound, 1);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 24, \sound, 1);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 26, \sound, 1);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 28, \sound, 1);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 30, \sound, 1);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 32, \sound, 1);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 34, \sound, 1);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 36, \sound, 1);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 38, \sound, 1);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 40, \sound, 1);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 42, \sound, 1);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 44, \sound, 1);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 46, \sound, 1);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 48, \sound, 1);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 50, \sound, 1);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 52, \sound, 1);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 54, \sound, 1);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 56, \sound, 1);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 58, \sound, 1);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 60, \sound, 1);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 62, \sound, 1);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 64, \sound, 1);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 66, \sound, 1);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 68, \sound, 1);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 70, \sound, 1);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 72, \sound, 1);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 74, \sound, 1);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 76, \sound, 1);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 78, \sound, 1);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 80, \sound, 1);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 82, \sound, 1);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 84, \sound, 1);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 86, \sound, 1);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 88, \sound, 1);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 90, \sound, 1);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 92, \sound, 1);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 94, \sound, 1);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 96, \sound, 1);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 98, \sound, 1);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 100, \sound, 1);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 102, \sound, 1);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 104, \sound, 1);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 106, \sound, 1);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 108, \sound, 1);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 110, \sound, 1);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 112, \sound, 1);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 114, \sound, 1);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 116, \sound, 1);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 118, \sound, 1);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 120, \sound, 1);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 122, \sound, 1);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 124, \sound, 1);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 126, \sound, 1);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 128, \sound, 1);};
					)}

					// In2 is plugged, In3 is not plugged in -- RM and AM
					{(samIns[samtn2].value != "0") && (samIns[samtn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in1, 0, \sound, 2);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 16, \sound, 4);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 18, \sound, 4);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 20, \sound, 4);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 22, \sound, 4);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 24, \sound, 4);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 26, \sound, 4);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 28, \sound, 4);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 30, \sound, 4);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 32, \sound, 4);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 34, \sound, 4);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 36, \sound, 4);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 38, \sound, 4);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 40, \sound, 4);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 42, \sound, 4);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 44, \sound, 4);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 46, \sound, 4);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 48, \sound, 4);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 50, \sound, 4);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 52, \sound, 4);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 54, \sound, 4);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 56, \sound, 4);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 58, \sound, 4);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 60, \sound, 4);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 62, \sound, 4);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 64, \sound, 4);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 66, \sound, 4);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 68, \sound, 4);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 70, \sound, 4);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 72, \sound, 4);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 74, \sound, 4);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 76, \sound, 4);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 78, \sound, 4);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 80, \sound, 4);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 82, \sound, 4);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 84, \sound, 4);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 86, \sound, 4);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 88, \sound, 4);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 90, \sound, 4);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 92, \sound, 4);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 94, \sound, 4);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 96, \sound, 4);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 98, \sound, 4);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 100, \sound, 4);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 102, \sound, 4);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 104, \sound, 4);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 106, \sound, 4);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 108, \sound, 4);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 110, \sound, 4);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 112, \sound, 4);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 114, \sound, 4);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 116, \sound, 4);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 118, \sound, 4);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 120, \sound, 4);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 122, \sound, 4);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 124, \sound, 4);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 126, \sound, 4);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 128, \sound, 4);};
					)}

					// In2 is not plugged, In3 is plugged in -- RM and FM
					{(samIns[samtn2].value == "0") && (samIns[samtn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in1, 0, \sound, 3);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 16, \sound, 5);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 18, \sound, 5);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 20, \sound, 5);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 22, \sound, 5);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 24, \sound, 5);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 26, \sound, 5);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 28, \sound, 5);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 30, \sound, 5);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 32, \sound, 5);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 34, \sound, 5);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 36, \sound, 5);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 38, \sound, 5);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 40, \sound, 5);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 42, \sound, 5);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 44, \sound, 5);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 46, \sound, 5);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 48, \sound, 5);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 50, \sound, 5);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 52, \sound, 5);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 54, \sound, 5);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 56, \sound, 5);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 58, \sound, 5);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 60, \sound, 5);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 62, \sound, 5);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 64, \sound, 5);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 66, \sound, 5);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 68, \sound, 5);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 70, \sound, 5);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 72, \sound, 5);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 74, \sound, 5);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 76, \sound, 5);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 78, \sound, 5);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 80, \sound, 5);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 82, \sound, 5);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 84, \sound, 5);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 86, \sound, 5);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 88, \sound, 5);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 90, \sound, 5);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 92, \sound, 5);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 94, \sound, 5);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 96, \sound, 5);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 98, \sound, 5);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 100, \sound, 5);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 102, \sound, 5);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 104, \sound, 5);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 106, \sound, 5);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 108, \sound, 5);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 110, \sound, 5);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 112, \sound, 5);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 114, \sound, 5);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 116, \sound, 5);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 118, \sound, 5);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 120, \sound, 5);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 122, \sound, 5);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 124, \sound, 5);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 126, \sound, 5);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 128, \sound, 5);};
					)}

					// In2 is plugged in and In3 is plugged in -- RM, AM, FM
					{(samIns[samtn2].value != "0") && (samIns[samtn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in1, 0, \sound, 6);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in1, 128, \sound, 7);};
					)};
				};

				// Actions for input 2:
				samIns[samtn2].action = {arg field;
					case
					// In1 and In3 are 0 i.e. not plugged in -- AM only
					{(samIns[samtn].value == "0") && (samIns[samtn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in2, 0, \sound, 0);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 16, \sound, 2);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 18, \sound, 2);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 20, \sound, 2);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 22, \sound, 2);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 24, \sound, 2);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 26, \sound, 2);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 28, \sound, 2);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 30, \sound, 2);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 32, \sound, 2);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 34, \sound, 2);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 36, \sound, 2);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 38, \sound, 2);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 40, \sound, 2);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 42, \sound, 2);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 44, \sound, 2);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 46, \sound, 2);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 48, \sound, 2);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 50, \sound, 2);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 52, \sound, 2);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 54, \sound, 2);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 56, \sound, 2);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 58, \sound, 2);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 60, \sound, 2);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 62, \sound, 2);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 64, \sound, 2);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 66, \sound, 2);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 68, \sound, 2);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 70, \sound, 2);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 72, \sound, 2);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 74, \sound, 2);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 76, \sound, 2);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 78, \sound, 2);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 80, \sound, 2);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 82, \sound, 2);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 84, \sound, 2);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 86, \sound, 2);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 88, \sound, 2);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 90, \sound, 2);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 92, \sound, 2);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 94, \sound, 2);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 96, \sound, 2);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 98, \sound, 2);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 100, \sound, 2);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 102, \sound, 2);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 104, \sound, 2);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 106, \sound, 2);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 108, \sound, 2);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 110, \sound, 2);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 112, \sound, 2);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 114, \sound, 2);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 116, \sound, 2);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 118, \sound, 2);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 120, \sound, 2);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 122, \sound, 2);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 124, \sound, 2);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 126, \sound, 2);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 128, \sound, 2);};
					)}

					// In1 is plugged, In3 is not plugged in -- RM and AM
					{(samIns[samtn].value != "0") && (samIns[samtn3].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in2, 0, \sound, 1);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 16, \sound, 4);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 18, \sound, 4);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 20, \sound, 4);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 22, \sound, 4);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 24, \sound, 4);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 26, \sound, 4);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 28, \sound, 4);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 30, \sound, 4);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 32, \sound, 4);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 34, \sound, 4);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 36, \sound, 4);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 38, \sound, 4);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 40, \sound, 4);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 42, \sound, 4);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 44, \sound, 4);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 46, \sound, 4);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 48, \sound, 4);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 50, \sound, 4);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 52, \sound, 4);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 54, \sound, 4);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 56, \sound, 4);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 58, \sound, 4);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 60, \sound, 4);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 62, \sound, 4);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 64, \sound, 4);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 66, \sound, 4);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 68, \sound, 4);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 70, \sound, 4);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 72, \sound, 4);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 74, \sound, 4);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 76, \sound, 4);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 78, \sound, 4);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 80, \sound, 4);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 82, \sound, 4);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 84, \sound, 4);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 86, \sound, 4);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 88, \sound, 4);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 90, \sound, 4);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 92, \sound, 4);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 94, \sound, 4);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 96, \sound, 4);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 98, \sound, 4);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 100, \sound, 4);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 102, \sound, 4);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 104, \sound, 4);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 106, \sound, 4);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 108, \sound, 4);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 110, \sound, 4);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 112, \sound, 4);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 114, \sound, 4);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 116, \sound, 4);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 118, \sound, 4);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 120, \sound, 4);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 122, \sound, 4);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 124, \sound, 4);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 126, \sound, 4);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 128, \sound, 4);};
					)}

					// In1 is not plugged, In3 is plugged in -- AM and FM
					{(samIns[samtn].value == "0") && (samIns[samtn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in2, 0, \sound, 3);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 16, \sound, 6);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 18, \sound, 6);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 20, \sound, 6);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 22, \sound, 6);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 24, \sound, 6);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 26, \sound, 6);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 28, \sound, 6);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 30, \sound, 6);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 32, \sound, 6);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 34, \sound, 6);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 36, \sound, 6);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 38, \sound, 6);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 40, \sound, 6);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 42, \sound, 6);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 44, \sound, 6);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 46, \sound, 6);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 48, \sound, 6);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 50, \sound, 6);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 52, \sound, 6);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 54, \sound, 6);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 56, \sound, 6);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 58, \sound, 6);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 60, \sound, 6);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 62, \sound, 6);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 64, \sound, 6);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 66, \sound, 6);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 68, \sound, 6);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 70, \sound, 6);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 72, \sound, 6);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 74, \sound, 6);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 76, \sound, 6);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 78, \sound, 6);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 80, \sound, 6);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 82, \sound, 6);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 84, \sound, 6);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 86, \sound, 6);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 88, \sound, 6);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 90, \sound, 6);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 92, \sound, 6);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 94, \sound, 6);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 96, \sound, 6);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 98, \sound, 6);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 100, \sound, 6);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 102, \sound, 6);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 104, \sound, 6);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 106, \sound, 6);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 108, \sound, 6);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 110, \sound, 6);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 112, \sound, 6);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 114, \sound, 6);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 116, \sound, 6);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 118, \sound, 6);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 120, \sound, 6);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 122, \sound, 6);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 124, \sound, 6);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 126, \sound, 6);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 128, \sound, 6);};
					)}

					// In1 is plugged in and In3 is plugged in -- AM RM, FM
					{(samIns[samtn].value != "0") && (samIns[samtn3].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in2, 0, \sound, 5);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in2, 128, \sound, 7);};
					)};
				};

				// Actions for input 3:
				samIns[samtn3].action = {arg field;
					case
					// In1 and In2 are 0 i.e. not plugged in -- FM only
					{(samIns[samtn].value == "0") && (samIns[samtn2].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in3, 0, \sound, 0);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 16, \sound, 3);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 18, \sound, 3);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 20, \sound, 3);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 22, \sound, 3);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 24, \sound, 3);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 26, \sound, 3);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 28, \sound, 3);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 30, \sound, 3);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 32, \sound, 3);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 34, \sound, 3);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 36, \sound, 3);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 38, \sound, 3);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 40, \sound, 3);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 42, \sound, 3);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 44, \sound, 3);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 46, \sound, 3);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 48, \sound, 3);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 50, \sound, 3);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 52, \sound, 3);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 54, \sound, 3);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 56, \sound, 3);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 58, \sound, 3);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 60, \sound, 3);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 62, \sound, 3);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 64, \sound, 3);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 66, \sound, 3);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 68, \sound, 3);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 70, \sound, 3);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 72, \sound, 3);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 74, \sound, 3);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 76, \sound, 3);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 78, \sound, 3);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 80, \sound, 3);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 82, \sound, 3);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 84, \sound, 3);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 86, \sound, 3);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 88, \sound, 3);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 90, \sound, 3);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 92, \sound, 3);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 94, \sound, 3);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 96, \sound, 3);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 98, \sound, 3);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 100, \sound, 3);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 102, \sound, 3);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 104, \sound, 3);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 106, \sound, 3);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 108, \sound, 3);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 110, \sound, 3);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 112, \sound, 3);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 114, \sound, 3);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 116, \sound, 3);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 118, \sound, 3);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 120, \sound, 3);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 122, \sound, 3);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 124, \sound, 3);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 126, \sound, 3);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 128, \sound, 3);};
					)}
					// In1 is plugged, In2 is not plugged in -- RM and FM
					{(samIns[samtn].value != "0") && (samIns[samtn2].value == "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in3, 0, \sound, 1);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 16, \sound, 5);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 18, \sound, 5);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 20, \sound, 5);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 22, \sound, 5);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 24, \sound, 5);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 26, \sound, 5);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 28, \sound, 5);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 30, \sound, 5);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 32, \sound, 5);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 34, \sound, 5);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 36, \sound, 5);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 38, \sound, 5);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 40, \sound, 5);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 42, \sound, 5);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 44, \sound, 5);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 46, \sound, 5);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 48, \sound, 5);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 50, \sound, 5);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 52, \sound, 5);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 54, \sound, 5);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 56, \sound, 5);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 58, \sound, 5);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 60, \sound, 5);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 62, \sound, 5);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 64, \sound, 5);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 66, \sound, 5);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 68, \sound, 5);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 70, \sound, 5);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 72, \sound, 5);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 74, \sound, 5);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 76, \sound, 5);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 78, \sound, 5);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 80, \sound, 5);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 82, \sound, 5);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 84, \sound, 5);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 86, \sound, 5);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 88, \sound, 5);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 90, \sound, 5);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 92, \sound, 5);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 94, \sound, 5);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 96, \sound, 5);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 98, \sound, 5);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 100, \sound, 5);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 102, \sound, 5);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 104, \sound, 5);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 106, \sound, 5);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 108, \sound, 5);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 110, \sound, 5);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 112, \sound, 5);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 114, \sound, 5);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 116, \sound, 5);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 118, \sound, 5);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 120, \sound, 5);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 122, \sound, 5);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 124, \sound, 5);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 126, \sound, 5);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 128, \sound, 5);};
					)}

					// In1 is not plugged, In2 is plugged in -- AM and FM
					{(samIns[samtn].value == "0") && (samIns[samtn2].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in3, 0, \sound, 2);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 16, \sound, 6);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 18, \sound, 6);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 20, \sound, 6);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 22, \sound, 6);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 24, \sound, 6);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 26, \sound, 6);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 28, \sound, 6);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 30, \sound, 6);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 32, \sound, 6);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 34, \sound, 6);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 36, \sound, 6);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 38, \sound, 6);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 40, \sound, 6);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 42, \sound, 6);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 44, \sound, 6);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 46, \sound, 6);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 48, \sound, 6);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 50, \sound, 6);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 52, \sound, 6);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 54, \sound, 6);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 56, \sound, 6);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 58, \sound, 6);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 60, \sound, 6);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 62, \sound, 6);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 64, \sound, 6);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 66, \sound, 6);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 68, \sound, 6);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 70, \sound, 6);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 72, \sound, 6);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 74, \sound, 6);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 76, \sound, 6);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 78, \sound, 6);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 80, \sound, 6);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 82, \sound, 6);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 84, \sound, 6);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 86, \sound, 6);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 88, \sound, 6);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 90, \sound, 6);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 92, \sound, 6);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 94, \sound, 6);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 96, \sound, 6);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 98, \sound, 6);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 100, \sound, 6);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 102, \sound, 6);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 104, \sound, 6);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 106, \sound, 6);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 108, \sound, 6);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 110, \sound, 6);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 112, \sound, 6);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 114, \sound, 6);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 116, \sound, 6);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 118, \sound, 6);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 120, \sound, 6);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 122, \sound, 6);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 124, \sound, 6);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 126, \sound, 6);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 128, \sound, 6);};
					)}

					// In1 is plugged in and In2 is plugged in -- FM RM, AM
					{(samIns[samtn].value != "0") && (samIns[samtn2].value != "0")} {
						switch (field.value,
							"0", {field.background_(Color.black);
								samplers[samN].getSynth.set(\in3, 0, \sound, 4);},
							"16", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 16, \sound, 7);},
							"18", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 18, \sound, 7);},
							"20", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 20, \sound, 7);},
							"22", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 22, \sound, 7);},
							"24", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 24, \sound, 7);},
							"26", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 26, \sound, 7);},
							"28", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 28, \sound, 7);},
							"30", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 30, \sound, 7);},
							"32", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 32, \sound, 7);},
							"34", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 34, \sound, 7);},
							"36", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 36, \sound, 7);},
							"38", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 38, \sound, 7);},
							"40", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 40, \sound, 7);},
							"42", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 42, \sound, 7);},
							"44", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 44, \sound, 7);},
							"46", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 46, \sound, 7);},
							"48", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 48, \sound, 7);},
							"50", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 50, \sound, 7);},
							"52", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 52, \sound, 7);},
							"54", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 54, \sound, 7);},
							"56", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 56, \sound, 7);},
							"58", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 58, \sound, 7);},
							"60", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 60, \sound, 7);},
							"62", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 62, \sound, 7);},
							"64", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 64, \sound, 7);},
							"66", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 66, \sound, 7);},
							"68", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 68, \sound, 7);},
							"70", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 70, \sound, 7);},
							"72", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 72, \sound, 7);},
							"74", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 74, \sound, 7);},
							"76", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 76, \sound, 7);},
							"78", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 78, \sound, 7);},
							"80", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 80, \sound, 7);},
							"82", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 82, \sound, 7);},
							"84", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 84, \sound, 7);},
							"86", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 86, \sound, 7);},
							"88", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 88, \sound, 7);},
							"90", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 90, \sound, 7);},
							"92", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 92, \sound, 7);},
							"94", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 94, \sound, 7);},
							"96", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 96, \sound, 7);},
							"98", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 98, \sound, 7);},
							"100", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 100, \sound, 7);},
							"102", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 102, \sound, 7);},
							"104", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 104, \sound, 7);},
							"106", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 106, \sound, 7);},
							"108", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 108, \sound, 7);},
							"110", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 110, \sound, 7);},
							"112", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 112, \sound, 7);},
							"114", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 114, \sound, 7);},
							"116", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 116, \sound, 7);},
							"118", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 118, \sound, 7);},
							"120", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 120, \sound, 7);},
							"122", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 122, \sound, 7);},
							"124", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 124, \sound, 7);},
							"126", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 126, \sound, 7);},
							"128", {field.background_(Color.red);
								samplers[samN].getSynth.set(\in3, 128, \sound, 7);};
					)};
				};

				// Output Buttons
				2.do{arg i; samOutBs.add(Button(samplerCV[samNo], Rect(20 + (i*50), 93, 40, 30)))};
				samOutBs[samHideN].states_([[busTest, Color.white, Color.black], [busTest, Color.white, Color.red] ]);
				samOutBs[samHideN+1].states_([["0", Color.white, Color.red], ["0", Color.white, Color.black]]);

				// Toggle Output Button
				samOutBs[samhd1].action_({arg i;
					samplers[samN].getSynth.set(\out, busTest);
					samOutBs[samhd1].value = 1;
					samOutBs[samhd1+1].value = 1;
				});
				samOutBs[samhd1+1].action_({arg i;
					samplers[samN].getSynth.set(\out, 0);
					samOutBs[samhd1].value = 0;
					samOutBs[samhd1+1].value = 0;
				});

				// Show/Hide Buttons
				2.do{arg i; samB.add(Button(samplerCV[samNo], Rect(120 + (i*48), 50, 45, 25)))};
				samB[samHideN].states_([["Show", Color.black, Color.grey]]);
				samB[samHideN+1].states_([["Hide", Color.black, Color.grey]]);
				// Show and Hide actions
				samB[samHideN].action_({arg i;
					samplers[samN].showWindow;
				});
				samB[samHideN+1].action_({arg i;
					samplers[samN].hideWindow;
				});

				// Delete Button
				samDelete.add(Button(samplerCV[samNo], Rect(168, 80, 45, 25)));
				samDelete[samNo].states_([["Delete", Color.black, Color.grey]]);
				samDelete[samNo].action_({arg i;
					samplers[samN].closeWindow;
					samplerCV[samN].remove;
				});

				// Osc amp button launches a small window
				samAmp.add(Button(samplerCV[samNo], Rect(120, 80, 45, 25)));
				samAmp[samNo].states_([["AMP", Color.black, Color.grey]]);
				samAmp[samNo].action_({arg i, w;
					var win, title, text, slid;
					win = UserView(nil, Rect(300, 400, 90, 250));
					win.background = Color.grey(0.3);
					title = CompositeView(win, Rect(0, 0, 90, 30));
					text = StaticText(title, Rect(10, 5, 90, 30));
					text.string = "S" + samplerN + "AMP";
					text.stringColor = Color.white;
					slid = Slider(win, Rect(30, 60, 30, 150));
					slid.alwaysOnTop = true;
					slid.knobColor_(Color.grey(0.2));
					slid.value = 0.4;
					slid.action_({
						samplers[samN].getSynth.set(\amp, slid.value);
					});
					title.background = Color.grey;
					win.front;
					win.alwaysOnTop = true;
					win.mouseLeaveAction_({arg view, x, y;
						view.close;
					});
				});

				// Rm, Am, FM text:
				samModText = Array.fill(3, {arg i; StaticText(samplerCV[samNo], Rect(30 + (i*60), 20, 200, 30))});
				samModText.do{arg i;
					i.stringColor = Color.white;
				};
				samModText[0].string = "RM";
				samModText[1].string = "AM";
				samModText[2].string = "FM";

				// Output Bus text
				samOutText = StaticText(samplerCV[samNo], Rect(25, 70, 30, 30));
				samOutText.string = "OUT";
				samOutText.stringColor = Color.white;

				// Increment samNo.
				samNo = samNo +1;
				samplerNo = samplerNo +1;
				// Increment BusNo.
				busNo = busNo + 1;
				// Increment samTextN
				samTextN = samTextN + 3;
				// Incremenent samHideN.
				samHideN = samHideN + 2;
				// Increment true number
				samtN = samtN + 3;
				// Increment bus test
				bt = bt + 2;
			});

			// Display scope button:
			plusB[2].action_({
				scope.active_(true);
				if(scopeV.visible == false, {scopeV.visible = true;},
					{scopeV.visible = false;});
			});

			// Frequency scope
			scopeV = UserView(nil, Rect(0, 0, Window.availableBounds.right, Window.availableBounds.bottom).center_(Window.availableBounds.center));
			scopeV.background = Color.black;
			scope = FreqScopeView(scopeV, Window.availableBounds);
			scope.freqMode = 0;
			scopeV.front;
			scopeV.userCanClose = false;
			scopeV.visible = false;

			// Display window and the scrollview
			w.front;
			w.alwaysOnTop = true;
			scrollView.front;

			"Loaded WaveTable Modular Synthesizer GUI".postln;

			// Synth window closing:
			w.onClose_({
				"WaveTable Modular Synthesizer CLosed".postln;
				// Close the modular Playground:
				scrollView.close;
				case
				{(scopeV != nil)} {
					scope.kill;
					scopeV.close;};
				// Close and free synths
				case
				{(waveTableDividers.size != 0)} {
					waveTableDividers.do{arg i;
						i.closeWindow;};
				};
				case
				{(samplers.size != 0)} {
					samplers.do{arg i;
						i.closeWindow;};
				};
			});

			// Modular Playground closing:
			scrollView.onClose_({
				// Close the modular Playground:
				w.close;
				case
				{(scopeV != nil)} {
					scope.kill;
					scopeV.close;};
				// Close and free synths
				case
				{(waveTableDividers.size != 0)} {
					waveTableDividers.do{arg i;
						i.closeWindow;};
				};
				case
				{(samplers.size != 0)} {
					samplers.do{arg i;
						i.closeWindow;};
				};
			});

			// Keep updating/refreshing the view:
			Routine({while({scrollView.isClosed.not}, {v.refresh; v.bounds = scrollView.innerBounds; view.bounds = scrollView.innerBounds;
				(1/25).wait})}).play(AppClock);
		};

		// Handle functions in order:
		s = Server.local;
		s.freeAll;
		s.newBusAllocators;
		s.newBufferAllocators;
		// Buffer.freeAll;
		s.waitForBoot(reboot);
		s.waitForBoot(loadSynth);
		s.waitForBoot(loadGui);
	}

}