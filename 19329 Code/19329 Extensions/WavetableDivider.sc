/*
Wavetable Divider Class,
A wavetable lookup Synthesizer that allows users to divide up its table.
Maths equations are selected to choose the functions of particular divisions

Part of the Wavetable Modular Synthesizer Project.
(c) 2013-2014, Candidate Number 19329
Advanced Computer Music, University of Sussex, Autumn 2013.
*/

WavetableDivider {

	var w, v, cvp, titlecv, titletxt, plot, sine;
	var wNo;
	var <>winNo = 1;
	var divisionsMenu;
	var divMenu1, divMenu2, divMenu3;
	var cspec;
	var cspec2;
	var fknob;
	var plotb1, plotb2, plotb3;
	var maths;
	var <>array;
	var <>xbuf;
	var divisions = 1;
	var divMathArray;
	var int, int2, int3;
	var divmenus;
	var sr = 44100;
	var divmaths;
	var txtFreqPlot;
	var divtxt1, divtxt2, divtxt3, divtxt4;
	var presetbs, presetText, presetText2, presetDict, myPath;
	var lfob;
	var lfoOn = false;
	var lfoKnob = false;
	var lfoText;
	var font, font2;
	var <>synth;
	var pdivisions = 0;
	var parray = 1;
	var pfknob = 2;
	var pdivMenu11 = 3;
	var pdivMenu12 = 4;
	var pdivMenu13 = 5;
	var pdivMenu14 = 6;
	var pdivMenu21 = 7;
	var pdivMenu22 = 8;
	var pdivMenu23 = 9;
	var pdivMenu24 = 10;
	var pdivMenu31 = 11;
	var pdivMenu32 = 12;
	var pdivMenu33 = 13;
	var pdivMenu34 = 14;
	var pdivMath1 = 15;
	var pdivMath2 = 16;
	var pdivMath3 = 17;
	var pdivMath4 = 18;
	var pdivMath5 = 19;
	var pdivMath6 = 20;
	var pdivMath7 = 21;
	var pdivMath8 = 22;
	var pdivMath9 = 23;
	var pdivMath10 = 24;
	var pdivMath11 = 25;
	var pdivMath12 = 26;
	var pxbuf = 27;
	var pdivmaths1 = 28;
	var pdivmaths2 = 29;
	var pdivmaths3 = 30;
	var pdivmaths4 = 31;
	var pdivmaths5 = 32;
	var pdivmaths6 = 33;
	var pdivmaths7 = 34;
	var pdivmaths8 = 35;
	var pdivmaths9 = 36;
	var pdivmaths10 = 37;
	var pdivmaths11 = 38;
	var pdivmaths12 = 39;
	var <>modulator = 1;
	var s;

	*new {
		^super.newCopyArgs();
	}

	setSynth { arg synth;
		this.synth = synth;
	}

	setWindowNumber { arg wn;
		this.winNo = wn;
	}

	setArray { arg synth;
		this.array = Array.fill(sr, {arg i;
			0;});
	}

	setXbuf { arg array;
		this.xbuf = Buffer.loadCollection(s, array);
	}

	setModulator { arg modulatorArray;
		this.modulator = modulatorArray;
	}

	new2 {

		"Loading Wavetable Divider...please wait...".postln;
		s = Server.local;

		// Set up dictionary for presets:
		presetDict = Dictionary.new;

		// Fonts:
		font = Font("Chalkduster", 35, true);
		font2 = Font("Chalkduster", 16);

		divmaths = Array.fill(12, {arg i;
			0;
		});

		// Utilise equations from MathsEquations class:
		maths = MathsEquations();
		maths.setMaths;
		divMathArray = Array.fill(12, {arg i; maths.getZero });

		// Set up windows:
		w = Window("WaveTable Divider", Rect(rrand(400, 800), 100, 500, 780), false);
		v = UserView(w, Rect(0, 0, 500, 780));
		v.background = Color.grey;
		titlecv = CompositeView(v, Rect(0, 0, 500, 80));
		titlecv.background = Color.grey(0.3);
		titletxt = StaticText(titlecv, Rect(40, -10, 500, 80));
		titletxt.string = "WAVETABLE DIVIDER";
		titletxt.font_(font);
		titletxt.stringColor = Color.white;

		cvp = CompositeView(v, Rect(100, 560, 300, 200));
		plot = Plotter("test", parent: cvp).value_((divMathArray[0]));

		// Menus:
		divisionsMenu = PopUpMenu(v, Rect(180, 120, 120, 30));
		divMenu1 = Array.fill(4, {arg i; PopUpMenu(v, Rect(20, 180+(i*60), 120, 30))});
		divMenu2 = Array.fill(4, {arg i; PopUpMenu(v, Rect(180, 180+(i*60), 120, 30))});
		divMenu3 = Array.fill(4, {arg i; PopUpMenu(v, Rect(340, 180+(i*60), 120, 30))});

		// Menu Items:
		divisionsMenu.items_(["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]);
		4.do{ arg i;
			divMenu1[i].items_(["0", "2pi 360° (Sine)", "pi/6 30°", "pi/4 45°", "pi/3 60°", "pi/2 90°", "2pi/3 120°", "3pi/4 135°", "5pi/6 150°", "pi 180°", "7pi/6 210°", "5pi/4 225°", "4pi/3 240°", "3pi/2 270°", "5pi/3 300°", "7pi/4 315°", "11pi/6 330°", "Sawtooth", "Inverse Saw", "Saw Var.1", "Square wave", "Noise"]);
			divMenu2[i].items_(["0", "2pi 360° (Sine)", "pi/6 30°", "pi/4 45°", "pi/3 60°", "pi/2 90°", "2pi/3 120°", "3pi/4 135°", "5pi/6 150°", "pi 180°", "7pi/6 210°", "5pi/4 225°", "4pi/3 240°", "3pi/2 270°", "5pi/3 300°", "7pi/4 315°", "11pi/6 330°", "Sawtooth", "Inverse Saw", "Saw Var.1", "Square wave", "Noise"]);
			divMenu3[i].items_(["0", "2pi 360° (Sine)", "pi/6 30°", "pi/4 45°", "pi/3 60°", "pi/2 90°", "2pi/3 120°", "3pi/4 135°", "5pi/6 150°", "pi 180°", "7pi/6 210°", "5pi/4 225°", "4pi/3 240°", "3pi/2 270°", "5pi/3 300°", "7pi/4 315°", "11pi/6 330°", "Sawtooth", "Inverse Saw", "Saw Var.1", "Square wave", "Noise"]);
		};

		divmenus = List();
		divmenus.add(divMenu1);
		divmenus.add(divMenu2);
		divmenus.add(divMenu3);

		// Division Menu Action:
		divisionsMenu.action_({ arg menu;
			case
			{ (menu.value == 0)} {
				divisions = 1;
			}
			{ (menu.value == 1) } {
				divisions = 2;
			}
			{ (menu.value == 2) } {
				divisions = 3;
			}
			{ (menu.value == 3) } {
				divisions = 4;
			}
			{ (menu.value == 4) } {
				divisions = 5;
			}
			{ (menu.value == 5) } {
				divisions = 6;
			}
			{ (menu.value == 6) } {
				divisions = 7;
			}
			{ (menu.value == 7) } {
				divisions = 8;
			}
			{ (menu.value == 8) } {
				divisions = 9;
			}
			{ (menu.value == 9) } {
				divisions = 10;
			}
			{ (menu.value == 10) } {
				divisions = 11;
			}
			{ (menu.value == 11) } {
				divisions = 12;
			};
			array = Array.fill(sr, {arg i;
				if(sr/divisions*i == 0,
					{
						0;
					},
					{
						if(divisions == 1,
							{
								switch (divmaths[0],
									0, {0;},
									1, {(sin(1*2pi*(i/sr)))},
									2, {sin(1*(pi/6)*(i/sr));},
									3, {sin(1*(pi/4)*(i/sr));},
									4, {sin(1*(pi/3)*(i/sr));},
									5, {sin(1*(pi/2)*(i/sr));},
									6, {sin(1*(2pi/3)*(i/sr));},
									7, {sin(1*(3pi/4)*(i/sr));},
									8, {sin(1*(5pi/6)*(i/sr));},
									9, {sin(1*pi*(i/sr));},
									10, {sin(1*(7pi/6)*(i/sr));},
									11, {sin(1*(5pi/4)*(i/sr));},
									12, {sin(1*(4pi/3)*(i/sr));},
									13, {sin(1*(3pi/2)*(i/sr));},
									14, {sin(1*(5pi/3)*(i/sr));},
									15, {sin(1*(7pi/4)*(i/sr));},
									16, {sin(1*(11pi/6)*(i/sr));},
									17, {(Array.fill(50,{arg j;
										var harmonic = j+1;
										((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
									}).sum*0.3;)},
									18, {Array.fill(50,{arg j;
										var harmonic = j+1;
										(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
									}).sum*(2pi)*0.04;},
									19, {Array.fill(50,{arg j;
										var harmonic = j+1;
										sin(2pi*harmonic*1*(i/sr))/harmonic
									}).sum*0.3;},
									20, {Array.fill(50,{arg j;
										var harmonic = j+1;
										(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
									}).sum*(4/pi)*0.3;},
									21, {(0.2.rand2);}
								);
							},
							{
								if(i < (sr/divisions),
									{
										switch (divmaths[0],
											0, {0;},
											1, {(sin(1*2pi*(i/sr)))},
											2, {sin(1*(pi/6)*(i/sr));},
											3, {sin(1*(pi/4)*(i/sr));},
											4, {sin(1*(pi/3)*(i/sr));},
											5, {sin(1*(pi/2)*(i/sr));},
											6, {sin(1*(2pi/3)*(i/sr));},
											7, {sin(1*(3pi/4)*(i/sr));},
											8, {sin(1*(5pi/6)*(i/sr));},
											9, {sin(1*pi*(i/sr));},
											10, {sin(1*(7pi/6)*(i/sr));},
											11, {sin(1*(5pi/4)*(i/sr));},
											12, {sin(1*(4pi/3)*(i/sr));},
											13, {sin(1*(3pi/2)*(i/sr));},
											14, {sin(1*(5pi/3)*(i/sr));},
											15, {sin(1*(7pi/4)*(i/sr));},
											16, {sin(1*(11pi/6)*(i/sr));},
											17, {(Array.fill(50,{arg j;
												var harmonic = j+1;
												((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
											}).sum*0.3;)},
											18, {Array.fill(50,{arg j;
												var harmonic = j+1;
												(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
											}).sum*(2pi)*0.04;},
											19, {Array.fill(50,{arg j;
												var harmonic = j+1;
												sin(2pi*harmonic*1*(i/sr))/harmonic
											}).sum*0.3;},
											20, {Array.fill(50,{arg j;
												var harmonic = j+1;
												(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
											}).sum*(4/pi)*0.3;},
											21, {(0.2.rand2);}
										);
									},
									{
										if(i < ((sr/divisions)*2), {
											switch (divmaths[1],
												0, {0;},
												1, {(sin(1*2pi*(i/sr)))},
												2, {sin(1*(pi/6)*(i/sr));},
												3, {sin(1*(pi/4)*(i/sr));},
												4, {sin(1*(pi/3)*(i/sr));},
												5, {sin(1*(pi/2)*(i/sr));},
												6, {sin(1*(2pi/3)*(i/sr));},
												7, {sin(1*(3pi/4)*(i/sr));},
												8, {sin(1*(5pi/6)*(i/sr));},
												9, {sin(1*pi*(i/sr));},
												10, {sin(1*(7pi/6)*(i/sr));},
												11, {sin(1*(5pi/4)*(i/sr));},
												12, {sin(1*(4pi/3)*(i/sr));},
												13, {sin(1*(3pi/2)*(i/sr));},
												14, {sin(1*(5pi/3)*(i/sr));},
												15, {sin(1*(7pi/4)*(i/sr));},
												16, {sin(1*(11pi/6)*(i/sr));},
												17, {(Array.fill(50,{arg j;
													var harmonic = j+1;
													((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
												}).sum*0.3;)},
												18, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
												}).sum*(2pi)*0.04;},
												19, {Array.fill(50,{arg j;
													var harmonic = j+1;
													sin(2pi*harmonic*1*(i/sr))/harmonic
												}).sum*0.3;},
												20, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
												}).sum*(4/pi)*0.3;},
												21, {(0.2.rand2);}
											);
											},
											{
												if(i < ((sr/divisions)*3), {
													switch (divmaths[2],
														0, {0;},
														1, {(sin(1*2pi*(i/sr)))},
														2, {sin(1*(pi/6)*(i/sr));},
														3, {sin(1*(pi/4)*(i/sr));},
														4, {sin(1*(pi/3)*(i/sr));},
														5, {sin(1*(pi/2)*(i/sr));},
														6, {sin(1*(2pi/3)*(i/sr));},
														7, {sin(1*(3pi/4)*(i/sr));},
														8, {sin(1*(5pi/6)*(i/sr));},
														9, {sin(1*pi*(i/sr));},
														10, {sin(1*(7pi/6)*(i/sr));},
														11, {sin(1*(5pi/4)*(i/sr));},
														12, {sin(1*(4pi/3)*(i/sr));},
														13, {sin(1*(3pi/2)*(i/sr));},
														14, {sin(1*(5pi/3)*(i/sr));},
														15, {sin(1*(7pi/4)*(i/sr));},
														16, {sin(1*(11pi/6)*(i/sr));},
														17, {(Array.fill(50,{arg j;
															var harmonic = j+1;
															((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
														}).sum*0.3;)},
														18, {Array.fill(50,{arg j;
															var harmonic = j+1;
															(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
														}).sum*(2pi)*0.04;},
														19, {Array.fill(50,{arg j;
															var harmonic = j+1;
															sin(2pi*harmonic*1*(i/sr))/harmonic
														}).sum*0.3;},
														20, {Array.fill(50,{arg j;
															var harmonic = j+1;
															(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
														}).sum*(4/pi)*0.3;},
														21, {(0.2.rand2);}
													);
													},
													{
														if(i < ((sr/divisions)*4), {
															switch (divmaths[3],
																0, {0;},
																1, {(sin(1*2pi*(i/sr)))},
																2, {sin(1*(pi/6)*(i/sr));},
																3, {sin(1*(pi/4)*(i/sr));},
																4, {sin(1*(pi/3)*(i/sr));},
																5, {sin(1*(pi/2)*(i/sr));},
																6, {sin(1*(2pi/3)*(i/sr));},
																7, {sin(1*(3pi/4)*(i/sr));},
																8, {sin(1*(5pi/6)*(i/sr));},
																9, {sin(1*pi*(i/sr));},
																10, {sin(1*(7pi/6)*(i/sr));},
																11, {sin(1*(5pi/4)*(i/sr));},
																12, {sin(1*(4pi/3)*(i/sr));},
																13, {sin(1*(3pi/2)*(i/sr));},
																14, {sin(1*(5pi/3)*(i/sr));},
																15, {sin(1*(7pi/4)*(i/sr));},
																16, {sin(1*(11pi/6)*(i/sr));},
																17, {(Array.fill(50,{arg j;
																	var harmonic = j+1;
																	((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																}).sum*0.3;)},
																18, {Array.fill(50,{arg j;
																	var harmonic = j+1;
																	(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																}).sum*(2pi)*0.04;},
																19, {Array.fill(50,{arg j;
																	var harmonic = j+1;
																	sin(2pi*harmonic*1*(i/sr))/harmonic
																}).sum*0.3;},
																20, {Array.fill(50,{arg j;
																	var harmonic = j+1;
																	(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																}).sum*(4/pi)*0.3;},
																21, {(0.2.rand2);}
															);
															},
															{
																if(i < ((sr/divisions)*5), {
																	switch (divmaths[4],
																		0, {0;},
																		1, {(sin(1*2pi*(i/sr)))},
																		2, {sin(1*(pi/6)*(i/sr));},
																		3, {sin(1*(pi/4)*(i/sr));},
																		4, {sin(1*(pi/3)*(i/sr));},
																		5, {sin(1*(pi/2)*(i/sr));},
																		6, {sin(1*(2pi/3)*(i/sr));},
																		7, {sin(1*(3pi/4)*(i/sr));},
																		8, {sin(1*(5pi/6)*(i/sr));},
																		9, {sin(1*pi*(i/sr));},
																		10, {sin(1*(7pi/6)*(i/sr));},
																		11, {sin(1*(5pi/4)*(i/sr));},
																		12, {sin(1*(4pi/3)*(i/sr));},
																		13, {sin(1*(3pi/2)*(i/sr));},
																		14, {sin(1*(5pi/3)*(i/sr));},
																		15, {sin(1*(7pi/4)*(i/sr));},
																		16, {sin(1*(11pi/6)*(i/sr));},
																		17, {(Array.fill(50,{arg j;
																			var harmonic = j+1;
																			((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																		}).sum*0.3;)},
																		18, {Array.fill(50,{arg j;
																			var harmonic = j+1;
																			(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																		}).sum*(2pi)*0.04;},
																		19, {Array.fill(50,{arg j;
																			var harmonic = j+1;
																			sin(2pi*harmonic*1*(i/sr))/harmonic
																		}).sum*0.3;},
																		20, {Array.fill(50,{arg j;
																			var harmonic = j+1;
																			(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																		}).sum*(4/pi)*0.3;},
																		21, {(0.2.rand2);}
																	);
																	},
																	{
																		if(i < ((sr/divisions)*6), {
																			switch (divmaths[5],
																				0, {0;},
																				1, {(sin(1*2pi*(i/sr)))},
																				2, {sin(1*(pi/6)*(i/sr));},
																				3, {sin(1*(pi/4)*(i/sr));},
																				4, {sin(1*(pi/3)*(i/sr));},
																				5, {sin(1*(pi/2)*(i/sr));},
																				6, {sin(1*(2pi/3)*(i/sr));},
																				7, {sin(1*(3pi/4)*(i/sr));},
																				8, {sin(1*(5pi/6)*(i/sr));},
																				9, {sin(1*pi*(i/sr));},
																				10, {sin(1*(7pi/6)*(i/sr));},
																				11, {sin(1*(5pi/4)*(i/sr));},
																				12, {sin(1*(4pi/3)*(i/sr));},
																				13, {sin(1*(3pi/2)*(i/sr));},
																				14, {sin(1*(5pi/3)*(i/sr));},
																				15, {sin(1*(7pi/4)*(i/sr));},
																				16, {sin(1*(11pi/6)*(i/sr));},
																				17, {(Array.fill(50,{arg j;
																					var harmonic = j+1;
																					((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																				}).sum*0.3;)},
																				18, {Array.fill(50,{arg j;
																					var harmonic = j+1;
																					(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																				}).sum*(2pi)*0.04;},
																				19, {Array.fill(50,{arg j;
																					var harmonic = j+1;
																					sin(2pi*harmonic*1*(i/sr))/harmonic
																				}).sum*0.3;},
																				20, {Array.fill(50,{arg j;
																					var harmonic = j+1;
																					(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																				}).sum*(4/pi)*0.3;},
																				21, {(0.2.rand2);}
																			);
																			},
																			{
																				if(i < ((sr/divisions)*7), {
																					switch (divmaths[6],
																						0, {0;},
																						1, {(sin(1*2pi*(i/sr)))},
																						2, {sin(1*(pi/6)*(i/sr));},
																						3, {sin(1*(pi/4)*(i/sr));},
																						4, {sin(1*(pi/3)*(i/sr));},
																						5, {sin(1*(pi/2)*(i/sr));},
																						6, {sin(1*(2pi/3)*(i/sr));},
																						7, {sin(1*(3pi/4)*(i/sr));},
																						8, {sin(1*(5pi/6)*(i/sr));},
																						9, {sin(1*pi*(i/sr));},
																						10, {sin(1*(7pi/6)*(i/sr));},
																						11, {sin(1*(5pi/4)*(i/sr));},
																						12, {sin(1*(4pi/3)*(i/sr));},
																						13, {sin(1*(3pi/2)*(i/sr));},
																						14, {sin(1*(5pi/3)*(i/sr));},
																						15, {sin(1*(7pi/4)*(i/sr));},
																						16, {sin(1*(11pi/6)*(i/sr));},
																						17, {(Array.fill(50,{arg j;
																							var harmonic = j+1;
																							((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																						}).sum*0.3;)},
																						18, {Array.fill(50,{arg j;
																							var harmonic = j+1;
																							(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																						}).sum*(2pi)*0.04;},
																						19, {Array.fill(50,{arg j;
																							var harmonic = j+1;
																							sin(2pi*harmonic*1*(i/sr))/harmonic
																						}).sum*0.3;},
																						20, {Array.fill(50,{arg j;
																							var harmonic = j+1;
																							(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																						}).sum*(4/pi)*0.3;},
																						21, {(0.2.rand2);}
																					);
																					},
																					{
																						if(i < ((sr/divisions)*8), {
																							switch (divmaths[7],
																								0, {0;},
																								1, {(sin(1*2pi*(i/sr)))},
																								2, {sin(1*(pi/6)*(i/sr));},
																								3, {sin(1*(pi/4)*(i/sr));},
																								4, {sin(1*(pi/3)*(i/sr));},
																								5, {sin(1*(pi/2)*(i/sr));},
																								6, {sin(1*(2pi/3)*(i/sr));},
																								7, {sin(1*(3pi/4)*(i/sr));},
																								8, {sin(1*(5pi/6)*(i/sr));},
																								9, {sin(1*pi*(i/sr));},
																								10, {sin(1*(7pi/6)*(i/sr));},
																								11, {sin(1*(5pi/4)*(i/sr));},
																								12, {sin(1*(4pi/3)*(i/sr));},
																								13, {sin(1*(3pi/2)*(i/sr));},
																								14, {sin(1*(5pi/3)*(i/sr));},
																								15, {sin(1*(7pi/4)*(i/sr));},
																								16, {sin(1*(11pi/6)*(i/sr));},
																								17, {(Array.fill(50,{arg j;
																									var harmonic = j+1;
																									((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																								}).sum*0.3;)},
																								18, {Array.fill(50,{arg j;
																									var harmonic = j+1;
																									(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																								}).sum*(2pi)*0.04;},
																								19, {Array.fill(50,{arg j;
																									var harmonic = j+1;
																									sin(2pi*harmonic*1*(i/sr))/harmonic
																								}).sum*0.3;},
																								20, {Array.fill(50,{arg j;
																									var harmonic = j+1;
																									(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																								}).sum*(4/pi)*0.3;},
																								21, {(0.2.rand2);}
																							);
																							},
																							{
																								if(i < ((sr/divisions)*9), {
																									switch (divmaths[8],
																										0, {0;},
																										1, {(sin(1*2pi*(i/sr)))},
																										2, {sin(1*(pi/6)*(i/sr));},
																										3, {sin(1*(pi/4)*(i/sr));},
																										4, {sin(1*(pi/3)*(i/sr));},
																										5, {sin(1*(pi/2)*(i/sr));},
																										6, {sin(1*(2pi/3)*(i/sr));},
																										7, {sin(1*(3pi/4)*(i/sr));},
																										8, {sin(1*(5pi/6)*(i/sr));},
																										9, {sin(1*pi*(i/sr));},
																										10, {sin(1*(7pi/6)*(i/sr));},
																										11, {sin(1*(5pi/4)*(i/sr));},
																										12, {sin(1*(4pi/3)*(i/sr));},
																										13, {sin(1*(3pi/2)*(i/sr));},
																										14, {sin(1*(5pi/3)*(i/sr));},
																										15, {sin(1*(7pi/4)*(i/sr));},
																										16, {sin(1*(11pi/6)*(i/sr));},
																										17, {(Array.fill(50,{arg j;
																											var harmonic = j+1;
																											((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																										}).sum*0.3;)},
																										18, {Array.fill(50,{arg j;
																											var harmonic = j+1;
																											(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																										}).sum*(2pi)*0.04;},
																										19, {Array.fill(50,{arg j;
																											var harmonic = j+1;
																											sin(2pi*harmonic*1*(i/sr))/harmonic
																										}).sum*0.3;},
																										20, {Array.fill(50,{arg j;
																											var harmonic = j+1;
																											(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																										}).sum*(4/pi)*0.3;},
																										21, {(0.2.rand2);}
																									);
																									},
																									{
																										if(i < ((sr/divisions)*10), {
																											switch (divmaths[9],
																												0, {0;},
																												1, {(sin(1*2pi*(i/sr)))},
																												2, {sin(1*(pi/6)*(i/sr));},
																												3, {sin(1*(pi/4)*(i/sr));},
																												4, {sin(1*(pi/3)*(i/sr));},
																												5, {sin(1*(pi/2)*(i/sr));},
																												6, {sin(1*(2pi/3)*(i/sr));},
																												7, {sin(1*(3pi/4)*(i/sr));},
																												8, {sin(1*(5pi/6)*(i/sr));},
																												9, {sin(1*pi*(i/sr));},
																												10, {sin(1*(7pi/6)*(i/sr));},
																												11, {sin(1*(5pi/4)*(i/sr));},
																												12, {sin(1*(4pi/3)*(i/sr));},
																												13, {sin(1*(3pi/2)*(i/sr));},
																												14, {sin(1*(5pi/3)*(i/sr));},
																												15, {sin(1*(7pi/4)*(i/sr));},
																												16, {sin(1*(11pi/6)*(i/sr));},
																												17, {(Array.fill(50,{arg j;
																													var harmonic = j+1;
																													((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																												}).sum*0.3;)},
																												18, {Array.fill(50,{arg j;
																													var harmonic = j+1;
																													(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																												}).sum*(2pi)*0.04;},
																												19, {Array.fill(50,{arg j;
																													var harmonic = j+1;
																													sin(2pi*harmonic*1*(i/sr))/harmonic
																												}).sum*0.3;},
																												20, {Array.fill(50,{arg j;
																													var harmonic = j+1;
																													(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																												}).sum*(4/pi)*0.3;},
																												21, {(0.2.rand2);}
																											);
																											},
																											{
																												if(i < ((sr/divisions)*11), {
																													switch (divmaths[10],
																														0, {0;},
																														1, {(sin(1*2pi*(i/sr)))},
																														2, {sin(1*(pi/6)*(i/sr));},
																														3, {sin(1*(pi/4)*(i/sr));},
																														4, {sin(1*(pi/3)*(i/sr));},
																														5, {sin(1*(pi/2)*(i/sr));},
																														6, {sin(1*(2pi/3)*(i/sr));},
																														7, {sin(1*(3pi/4)*(i/sr));},
																														8, {sin(1*(5pi/6)*(i/sr));},
																														9, {sin(1*pi*(i/sr));},
																														10, {sin(1*(7pi/6)*(i/sr));},
																														11, {sin(1*(5pi/4)*(i/sr));},
																														12, {sin(1*(4pi/3)*(i/sr));},
																														13, {sin(1*(3pi/2)*(i/sr));},
																														14, {sin(1*(5pi/3)*(i/sr));},
																														15, {sin(1*(7pi/4)*(i/sr));},
																														16, {sin(1*(11pi/6)*(i/sr));},
																														17, {(Array.fill(50,{arg j;
																															var harmonic = j+1;
																															((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																														}).sum*0.3;)},
																														18, {Array.fill(50,{arg j;
																															var harmonic = j+1;
																															(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																														}).sum*(2pi)*0.04;},
																														19, {Array.fill(50,{arg j;
																															var harmonic = j+1;
																															sin(2pi*harmonic*1*(i/sr))/harmonic
																														}).sum*0.3;},
																														20, {Array.fill(50,{arg j;
																															var harmonic = j+1;
																															(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																														}).sum*(4/pi)*0.3;},
																														21, {(0.2.rand2);}
																													);
																													},
																													{
																														if(i < ((sr/divisions)*12), {
																															switch (divmaths[11],
																																0, {0;},
																																1, {(sin(1*2pi*(i/sr)))},
																																2, {sin(1*(pi/6)*(i/sr));},
																																3, {sin(1*(pi/4)*(i/sr));},
																																4, {sin(1*(pi/3)*(i/sr));},
																																5, {sin(1*(pi/2)*(i/sr));},
																																6, {sin(1*(2pi/3)*(i/sr));},
																																7, {sin(1*(3pi/4)*(i/sr));},
																																8, {sin(1*(5pi/6)*(i/sr));},
																																9, {sin(1*pi*(i/sr));},
																																10, {sin(1*(7pi/6)*(i/sr));},
																																11, {sin(1*(5pi/4)*(i/sr));},
																																12, {sin(1*(4pi/3)*(i/sr));},
																																13, {sin(1*(3pi/2)*(i/sr));},
																																14, {sin(1*(5pi/3)*(i/sr));},
																																15, {sin(1*(7pi/4)*(i/sr));},
																																16, {sin(1*(11pi/6)*(i/sr));},
																																17, {(Array.fill(50,{arg j;
																																	var harmonic = j+1;
																																	((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																}).sum*0.3;)},
																																18, {Array.fill(50,{arg j;
																																	var harmonic = j+1;
																																	(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																}).sum*(2pi)*0.04;},
																																19, {Array.fill(50,{arg j;
																																	var harmonic = j+1;
																																	sin(2pi*harmonic*1*(i/sr))/harmonic
																																}).sum*0.3;},
																																20, {Array.fill(50,{arg j;
																																	var harmonic = j+1;
																																	(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																}).sum*(4/pi)*0.3;},
																																21, {(0.2.rand2);}
																															);
																															},
																															{
																																if(i < ((sr/divisions)*13), {
																																	switch (divmaths[12],
																																		0, {0;},
																																		1, {(sin(1*2pi*(i/sr)))},
																																		2, {sin(1*(pi/6)*(i/sr));},
																																		3, {sin(1*(pi/4)*(i/sr));},
																																		4, {sin(1*(pi/3)*(i/sr));},
																																		5, {sin(1*(pi/2)*(i/sr));},
																																		6, {sin(1*(2pi/3)*(i/sr));},
																																		7, {sin(1*(3pi/4)*(i/sr));},
																																		8, {sin(1*(5pi/6)*(i/sr));},
																																		9, {sin(1*pi*(i/sr));},
																																		10, {sin(1*(7pi/6)*(i/sr));},
																																		11, {sin(1*(5pi/4)*(i/sr));},
																																		12, {sin(1*(4pi/3)*(i/sr));},
																																		13, {sin(1*(3pi/2)*(i/sr));},
																																		14, {sin(1*(5pi/3)*(i/sr));},
																																		15, {sin(1*(7pi/4)*(i/sr));},
																																		16, {sin(1*(11pi/6)*(i/sr));},
																																		17, {(Array.fill(50,{arg j;
																																			var harmonic = j+1;
																																			((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																		}).sum*0.3;)},
																																		18, {Array.fill(50,{arg j;
																																			var harmonic = j+1;
																																			(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																		}).sum*(2pi)*0.04;},
																																		19, {Array.fill(50,{arg j;
																																			var harmonic = j+1;
																																			sin(2pi*harmonic*1*(i/sr))/harmonic
																																		}).sum*0.3;},
																																		20, {Array.fill(50,{arg j;
																																			var harmonic = j+1;
																																			(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																		}).sum*(4/pi)*0.3;},
																																		21, {(0.2.rand2);}
																																	);
																																	},
																																	{
																																	};
																																);
																															};
																														);
																													};
																												);
																											};
																										);
																									};
																								);
																							};
																						);
																					};
																				);
																			};
																		);
																	};
																);
															};
														);
													};
												);
											};
										);
									};
								);
							};
						);
					};
				);
			});
			plot.value_((array));
			// store in dictionary

			presetDict.add(parray -> array);

			presetDict.add(pdivisions -> divisions.value);

			xbuf = Buffer.loadCollection(s, array);

			synth.set(\bufnum, xbuf.bufnum);

			presetDict.add(pxbuf -> xbuf.value);


		});

		// Menu Actions for divisions 1 - 4:
		int = 0;
		4.do{ arg i;
			divMenu1[i].action_({arg menu;
				case
				{(divMenu1[i] == divMenu1[0])} {
					int = 0;
				}
				{(divMenu1[i] == divMenu1[1])} {
					int = 1;
				}
				{(divMenu1[i] == divMenu1[2])} {
					int = 2;
				}
				{(divMenu1[i] == divMenu1[3])} {
					int = 3;
				};
				case
				{(menu.value == 0)} {
					divmaths[int] = 0;
					divMathArray[int] = maths.getZero;
				}
				{(menu.value == 1)} {
					divmaths[int] = 1;
					divMathArray[int] = maths.getSin360;
				}
				{(menu.value == 2)} {
					divmaths[int] = 2;
					divMathArray[int] = maths.getSin30;
				}
				{(menu.value == 3)} {
					divmaths[int] = 3;
					divMathArray[int] = maths.getSin45;
				}
				{(menu.value == 4)} {
					divmaths[int] = 4;
					divMathArray[int] = maths.getSin60;
				}
				{(menu.value == 5)} {
					divmaths[int] = 5;
					divMathArray[int] = maths.getSin90;
				}
				{(menu.value == 6)} {
					divmaths[int] = 6;
					divMathArray[int] = maths.getSin120;
				}
				{(menu.value == 7)} {
					divmaths[int] = 7;
					divMathArray[int] = maths.getSin135;
				}
				{(menu.value == 8)} {
					divmaths[int] = 8;
					divMathArray[int] = maths.getSin150;
				}
				{(menu.value == 9)} {
					divmaths[int] = 9;
					divMathArray[int] = maths.getSin180;
				}
				{(menu.value == 10)} {
					divmaths[int] = 10;
					divMathArray[int] = maths.getSin210;
				}
				{(menu.value == 11)} {
					divmaths[int] = 11;
					divMathArray[int] = maths.getSin225;
				}
				{(menu.value == 12)} {
					divmaths[int] = 12;
					divMathArray[int] = maths.getSin240;
				}
				{(menu.value == 13)} {
					divmaths[int] = 13;
					divMathArray[int] = maths.getSin270;
				}
				{(menu.value == 14)} {
					divmaths[int] = 14;
					divMathArray[int] = maths.getSin300;
				}
				{(menu.value == 15)} {
					divmaths[int] = 15;
					divMathArray[int] = maths.getSin315;
				}
				{(menu.value == 16)} {
					divmaths[int] = 16;
					divMathArray[int] = maths.getSin330;
				}
				{(menu.value == 17)} {
					divmaths[int] = 17;
					divMathArray[int] = maths.getSaw1;
				}
				{(menu.value == 18)} {
					divmaths[int] = 18;
					divMathArray[int] = maths.getSaw2;
				}
				{(menu.value == 19)} {
					divmaths[int] = 19;
					divMathArray[int] = maths.getSaw3;
				}
				{(menu.value == 20)} {
					divmaths[int] = 20;
					divMathArray[int] = maths.getSquare1;
				}
				{(menu.value == 21)} {
					divmaths[int] = 21;
					divMathArray[int] = maths.getNoise1;
				};
				array = Array.fill(sr, {arg i;
					if(sr/divisions*i == 0,
						{
							0;
						},
						{
							if(divisions == 1,
								{
									switch (divmaths[0],
										0, {0;},
										1, {(sin(1*2pi*(i/sr)))},
										2, {sin(1*(pi/6)*(i/sr));},
										3, {sin(1*(pi/4)*(i/sr));},
										4, {sin(1*(pi/3)*(i/sr));},
										5, {sin(1*(pi/2)*(i/sr));},
										6, {sin(1*(2pi/3)*(i/sr));},
										7, {sin(1*(3pi/4)*(i/sr));},
										8, {sin(1*(5pi/6)*(i/sr));},
										9, {sin(1*pi*(i/sr));},
										10, {sin(1*(7pi/6)*(i/sr));},
										11, {sin(1*(5pi/4)*(i/sr));},
										12, {sin(1*(4pi/3)*(i/sr));},
										13, {sin(1*(3pi/2)*(i/sr));},
										14, {sin(1*(5pi/3)*(i/sr));},
										15, {sin(1*(7pi/4)*(i/sr));},
										16, {sin(1*(11pi/6)*(i/sr));},
										17, {(Array.fill(50,{arg j;
											var harmonic = j+1;
											((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
										}).sum*0.3;)},
										18, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
										}).sum*(2pi)*0.04;},
										19, {Array.fill(50,{arg j;
											var harmonic = j+1;
											sin(2pi*harmonic*1*(i/sr))/harmonic
										}).sum*0.3;},
										20, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
										}).sum*(4/pi)*0.3;},
										21, {(0.2.rand2);}
									);
								},
								{
									if(i < (sr/divisions),
										{
											switch (divmaths[0],
												0, {0;},
												1, {(sin(1*2pi*(i/sr)))},
												2, {sin(1*(pi/6)*(i/sr));},
												3, {sin(1*(pi/4)*(i/sr));},
												4, {sin(1*(pi/3)*(i/sr));},
												5, {sin(1*(pi/2)*(i/sr));},
												6, {sin(1*(2pi/3)*(i/sr));},
												7, {sin(1*(3pi/4)*(i/sr));},
												8, {sin(1*(5pi/6)*(i/sr));},
												9, {sin(1*pi*(i/sr));},
												10, {sin(1*(7pi/6)*(i/sr));},
												11, {sin(1*(5pi/4)*(i/sr));},
												12, {sin(1*(4pi/3)*(i/sr));},
												13, {sin(1*(3pi/2)*(i/sr));},
												14, {sin(1*(5pi/3)*(i/sr));},
												15, {sin(1*(7pi/4)*(i/sr));},
												16, {sin(1*(11pi/6)*(i/sr));},
												17, {(Array.fill(50,{arg j;
													var harmonic = j+1;
													((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
												}).sum*0.3;)},
												18, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
												}).sum*(2pi)*0.04;},
												19, {Array.fill(50,{arg j;
													var harmonic = j+1;
													sin(2pi*harmonic*1*(i/sr))/harmonic
												}).sum*0.3;},
												20, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
												}).sum*(4/pi)*0.3;},
												21, {(0.2.rand2);}
											);
										},
										{
											if(i < ((sr/divisions)*2), {
												switch (divmaths[1],
													0, {0;},
													1, {(sin(1*2pi*(i/sr)))},
													2, {sin(1*(pi/6)*(i/sr));},
													3, {sin(1*(pi/4)*(i/sr));},
													4, {sin(1*(pi/3)*(i/sr));},
													5, {sin(1*(pi/2)*(i/sr));},
													6, {sin(1*(2pi/3)*(i/sr));},
													7, {sin(1*(3pi/4)*(i/sr));},
													8, {sin(1*(5pi/6)*(i/sr));},
													9, {sin(1*pi*(i/sr));},
													10, {sin(1*(7pi/6)*(i/sr));},
													11, {sin(1*(5pi/4)*(i/sr));},
													12, {sin(1*(4pi/3)*(i/sr));},
													13, {sin(1*(3pi/2)*(i/sr));},
													14, {sin(1*(5pi/3)*(i/sr));},
													15, {sin(1*(7pi/4)*(i/sr));},
													16, {sin(1*(11pi/6)*(i/sr));},
													17, {(Array.fill(50,{arg j;
														var harmonic = j+1;
														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
													}).sum*0.3;)},
													18, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
													}).sum*(2pi)*0.04;},
													19, {Array.fill(50,{arg j;
														var harmonic = j+1;
														sin(2pi*harmonic*1*(i/sr))/harmonic
													}).sum*0.3;},
													20, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
													}).sum*(4/pi)*0.3;},
													21, {(0.2.rand2);}
												);
												},
												{
													if(i < ((sr/divisions)*3), {
														switch (divmaths[2],
															0, {0;},
															1, {(sin(1*2pi*(i/sr)))},
															2, {sin(1*(pi/6)*(i/sr));},
															3, {sin(1*(pi/4)*(i/sr));},
															4, {sin(1*(pi/3)*(i/sr));},
															5, {sin(1*(pi/2)*(i/sr));},
															6, {sin(1*(2pi/3)*(i/sr));},
															7, {sin(1*(3pi/4)*(i/sr));},
															8, {sin(1*(5pi/6)*(i/sr));},
															9, {sin(1*pi*(i/sr));},
															10, {sin(1*(7pi/6)*(i/sr));},
															11, {sin(1*(5pi/4)*(i/sr));},
															12, {sin(1*(4pi/3)*(i/sr));},
															13, {sin(1*(3pi/2)*(i/sr));},
															14, {sin(1*(5pi/3)*(i/sr));},
															15, {sin(1*(7pi/4)*(i/sr));},
															16, {sin(1*(11pi/6)*(i/sr));},
															17, {(Array.fill(50,{arg j;
																var harmonic = j+1;
																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
															}).sum*0.3;)},
															18, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
															}).sum*(2pi)*0.04;},
															19, {Array.fill(50,{arg j;
																var harmonic = j+1;
																sin(2pi*harmonic*1*(i/sr))/harmonic
															}).sum*0.3;},
															20, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
															}).sum*(4/pi)*0.3;},
															21, {(0.2.rand2);}
														);
														},
														{
															if(i < ((sr/divisions)*4), {
																switch (divmaths[3],
																	0, {0;},
																	1, {(sin(1*2pi*(i/sr)))},
																	2, {sin(1*(pi/6)*(i/sr));},
																	3, {sin(1*(pi/4)*(i/sr));},
																	4, {sin(1*(pi/3)*(i/sr));},
																	5, {sin(1*(pi/2)*(i/sr));},
																	6, {sin(1*(2pi/3)*(i/sr));},
																	7, {sin(1*(3pi/4)*(i/sr));},
																	8, {sin(1*(5pi/6)*(i/sr));},
																	9, {sin(1*pi*(i/sr));},
																	10, {sin(1*(7pi/6)*(i/sr));},
																	11, {sin(1*(5pi/4)*(i/sr));},
																	12, {sin(1*(4pi/3)*(i/sr));},
																	13, {sin(1*(3pi/2)*(i/sr));},
																	14, {sin(1*(5pi/3)*(i/sr));},
																	15, {sin(1*(7pi/4)*(i/sr));},
																	16, {sin(1*(11pi/6)*(i/sr));},
																	17, {(Array.fill(50,{arg j;
																		var harmonic = j+1;
																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																	}).sum*0.3;)},
																	18, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																	}).sum*(2pi)*0.04;},
																	19, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		sin(2pi*harmonic*1*(i/sr))/harmonic
																	}).sum*0.3;},
																	20, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																	}).sum*(4/pi)*0.3;},
																	21, {(0.2.rand2);}
																);
																},
																{
																	if(i < ((sr/divisions)*5), {
																		switch (divmaths[4],
																			0, {0;},
																			1, {(sin(1*2pi*(i/sr)))},
																			2, {sin(1*(pi/6)*(i/sr));},
																			3, {sin(1*(pi/4)*(i/sr));},
																			4, {sin(1*(pi/3)*(i/sr));},
																			5, {sin(1*(pi/2)*(i/sr));},
																			6, {sin(1*(2pi/3)*(i/sr));},
																			7, {sin(1*(3pi/4)*(i/sr));},
																			8, {sin(1*(5pi/6)*(i/sr));},
																			9, {sin(1*pi*(i/sr));},
																			10, {sin(1*(7pi/6)*(i/sr));},
																			11, {sin(1*(5pi/4)*(i/sr));},
																			12, {sin(1*(4pi/3)*(i/sr));},
																			13, {sin(1*(3pi/2)*(i/sr));},
																			14, {sin(1*(5pi/3)*(i/sr));},
																			15, {sin(1*(7pi/4)*(i/sr));},
																			16, {sin(1*(11pi/6)*(i/sr));},
																			17, {(Array.fill(50,{arg j;
																				var harmonic = j+1;
																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																			}).sum*0.3;)},
																			18, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																			}).sum*(2pi)*0.04;},
																			19, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				sin(2pi*harmonic*1*(i/sr))/harmonic
																			}).sum*0.3;},
																			20, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																			}).sum*(4/pi)*0.3;},
																			21, {(0.2.rand2);}
																		);
																		},
																		{
																			if(i < ((sr/divisions)*6), {
																				switch (divmaths[5],
																					0, {0;},
																					1, {(sin(1*2pi*(i/sr)))},
																					2, {sin(1*(pi/6)*(i/sr));},
																					3, {sin(1*(pi/4)*(i/sr));},
																					4, {sin(1*(pi/3)*(i/sr));},
																					5, {sin(1*(pi/2)*(i/sr));},
																					6, {sin(1*(2pi/3)*(i/sr));},
																					7, {sin(1*(3pi/4)*(i/sr));},
																					8, {sin(1*(5pi/6)*(i/sr));},
																					9, {sin(1*pi*(i/sr));},
																					10, {sin(1*(7pi/6)*(i/sr));},
																					11, {sin(1*(5pi/4)*(i/sr));},
																					12, {sin(1*(4pi/3)*(i/sr));},
																					13, {sin(1*(3pi/2)*(i/sr));},
																					14, {sin(1*(5pi/3)*(i/sr));},
																					15, {sin(1*(7pi/4)*(i/sr));},
																					16, {sin(1*(11pi/6)*(i/sr));},
																					17, {(Array.fill(50,{arg j;
																						var harmonic = j+1;
																						((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																					}).sum*0.3;)},
																					18, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																					}).sum*(2pi)*0.04;},
																					19, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						sin(2pi*harmonic*1*(i/sr))/harmonic
																					}).sum*0.3;},
																					20, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																					}).sum*(4/pi)*0.3;},
																					21, {(0.2.rand2);}
																				);
																				},
																				{
																					if(i < ((sr/divisions)*7), {
																						switch (divmaths[6],
																							0, {0;},
																							1, {(sin(1*2pi*(i/sr)))},
																							2, {sin(1*(pi/6)*(i/sr));},
																							3, {sin(1*(pi/4)*(i/sr));},
																							4, {sin(1*(pi/3)*(i/sr));},
																							5, {sin(1*(pi/2)*(i/sr));},
																							6, {sin(1*(2pi/3)*(i/sr));},
																							7, {sin(1*(3pi/4)*(i/sr));},
																							8, {sin(1*(5pi/6)*(i/sr));},
																							9, {sin(1*pi*(i/sr));},
																							10, {sin(1*(7pi/6)*(i/sr));},
																							11, {sin(1*(5pi/4)*(i/sr));},
																							12, {sin(1*(4pi/3)*(i/sr));},
																							13, {sin(1*(3pi/2)*(i/sr));},
																							14, {sin(1*(5pi/3)*(i/sr));},
																							15, {sin(1*(7pi/4)*(i/sr));},
																							16, {sin(1*(11pi/6)*(i/sr));},
																							17, {(Array.fill(50,{arg j;
																								var harmonic = j+1;
																								((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																							}).sum*0.3;)},
																							18, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																							}).sum*(2pi)*0.04;},
																							19, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								sin(2pi*harmonic*1*(i/sr))/harmonic
																							}).sum*0.3;},
																							20, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																							}).sum*(4/pi)*0.3;},
																							21, {(0.2.rand2);}
																						);
																						},
																						{
																							if(i < ((sr/divisions)*8), {
																								switch (divmaths[7],
																									0, {0;},
																									1, {(sin(1*2pi*(i/sr)))},
																									2, {sin(1*(pi/6)*(i/sr));},
																									3, {sin(1*(pi/4)*(i/sr));},
																									4, {sin(1*(pi/3)*(i/sr));},
																									5, {sin(1*(pi/2)*(i/sr));},
																									6, {sin(1*(2pi/3)*(i/sr));},
																									7, {sin(1*(3pi/4)*(i/sr));},
																									8, {sin(1*(5pi/6)*(i/sr));},
																									9, {sin(1*pi*(i/sr));},
																									10, {sin(1*(7pi/6)*(i/sr));},
																									11, {sin(1*(5pi/4)*(i/sr));},
																									12, {sin(1*(4pi/3)*(i/sr));},
																									13, {sin(1*(3pi/2)*(i/sr));},
																									14, {sin(1*(5pi/3)*(i/sr));},
																									15, {sin(1*(7pi/4)*(i/sr));},
																									16, {sin(1*(11pi/6)*(i/sr));},
																									17, {(Array.fill(50,{arg j;
																										var harmonic = j+1;
																										((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																									}).sum*0.3;)},
																									18, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																									}).sum*(2pi)*0.04;},
																									19, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										sin(2pi*harmonic*1*(i/sr))/harmonic
																									}).sum*0.3;},
																									20, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																									}).sum*(4/pi)*0.3;},
																									21, {(0.2.rand2);}
																								);
																								},
																								{
																									if(i < ((sr/divisions)*9), {
																										switch (divmaths[8],
																											0, {0;},
																											1, {(sin(1*2pi*(i/sr)))},
																											2, {sin(1*(pi/6)*(i/sr));},
																											3, {sin(1*(pi/4)*(i/sr));},
																											4, {sin(1*(pi/3)*(i/sr));},
																											5, {sin(1*(pi/2)*(i/sr));},
																											6, {sin(1*(2pi/3)*(i/sr));},
																											7, {sin(1*(3pi/4)*(i/sr));},
																											8, {sin(1*(5pi/6)*(i/sr));},
																											9, {sin(1*pi*(i/sr));},
																											10, {sin(1*(7pi/6)*(i/sr));},
																											11, {sin(1*(5pi/4)*(i/sr));},
																											12, {sin(1*(4pi/3)*(i/sr));},
																											13, {sin(1*(3pi/2)*(i/sr));},
																											14, {sin(1*(5pi/3)*(i/sr));},
																											15, {sin(1*(7pi/4)*(i/sr));},
																											16, {sin(1*(11pi/6)*(i/sr));},
																											17, {(Array.fill(50,{arg j;
																												var harmonic = j+1;
																												((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																											}).sum*0.3;)},
																											18, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																											}).sum*(2pi)*0.04;},
																											19, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												sin(2pi*harmonic*1*(i/sr))/harmonic
																											}).sum*0.3;},
																											20, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																											}).sum*(4/pi)*0.3;},
																											21, {(0.2.rand2);}
																										);
																										},
																										{
																											if(i < ((sr/divisions)*10), {
																												switch (divmaths[9],
																													0, {0;},
																													1, {(sin(1*2pi*(i/sr)))},
																													2, {sin(1*(pi/6)*(i/sr));},
																													3, {sin(1*(pi/4)*(i/sr));},
																													4, {sin(1*(pi/3)*(i/sr));},
																													5, {sin(1*(pi/2)*(i/sr));},
																													6, {sin(1*(2pi/3)*(i/sr));},
																													7, {sin(1*(3pi/4)*(i/sr));},
																													8, {sin(1*(5pi/6)*(i/sr));},
																													9, {sin(1*pi*(i/sr));},
																													10, {sin(1*(7pi/6)*(i/sr));},
																													11, {sin(1*(5pi/4)*(i/sr));},
																													12, {sin(1*(4pi/3)*(i/sr));},
																													13, {sin(1*(3pi/2)*(i/sr));},
																													14, {sin(1*(5pi/3)*(i/sr));},
																													15, {sin(1*(7pi/4)*(i/sr));},
																													16, {sin(1*(11pi/6)*(i/sr));},
																													17, {(Array.fill(50,{arg j;
																														var harmonic = j+1;
																														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																													}).sum*0.3;)},
																													18, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																													}).sum*(2pi)*0.04;},
																													19, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														sin(2pi*harmonic*1*(i/sr))/harmonic
																													}).sum*0.3;},
																													20, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																													}).sum*(4/pi)*0.3;},
																													21, {(0.2.rand2);}
																												);
																												},
																												{
																													if(i < ((sr/divisions)*11), {
																														switch (divmaths[10],
																															0, {0;},
																															1, {(sin(1*2pi*(i/sr)))},
																															2, {sin(1*(pi/6)*(i/sr));},
																															3, {sin(1*(pi/4)*(i/sr));},
																															4, {sin(1*(pi/3)*(i/sr));},
																															5, {sin(1*(pi/2)*(i/sr));},
																															6, {sin(1*(2pi/3)*(i/sr));},
																															7, {sin(1*(3pi/4)*(i/sr));},
																															8, {sin(1*(5pi/6)*(i/sr));},
																															9, {sin(1*pi*(i/sr));},
																															10, {sin(1*(7pi/6)*(i/sr));},
																															11, {sin(1*(5pi/4)*(i/sr));},
																															12, {sin(1*(4pi/3)*(i/sr));},
																															13, {sin(1*(3pi/2)*(i/sr));},
																															14, {sin(1*(5pi/3)*(i/sr));},
																															15, {sin(1*(7pi/4)*(i/sr));},
																															16, {sin(1*(11pi/6)*(i/sr));},
																															17, {(Array.fill(50,{arg j;
																																var harmonic = j+1;
																																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																															}).sum*0.3;)},
																															18, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																															}).sum*(2pi)*0.04;},
																															19, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																sin(2pi*harmonic*1*(i/sr))/harmonic
																															}).sum*0.3;},
																															20, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																															}).sum*(4/pi)*0.3;},
																															21, {(0.2.rand2);}
																														);
																														},
																														{
																															if(i < ((sr/divisions)*12), {
																																switch (divmaths[11],
																																	0, {0;},
																																	1, {(sin(1*2pi*(i/sr)))},
																																	2, {sin(1*(pi/6)*(i/sr));},
																																	3, {sin(1*(pi/4)*(i/sr));},
																																	4, {sin(1*(pi/3)*(i/sr));},
																																	5, {sin(1*(pi/2)*(i/sr));},
																																	6, {sin(1*(2pi/3)*(i/sr));},
																																	7, {sin(1*(3pi/4)*(i/sr));},
																																	8, {sin(1*(5pi/6)*(i/sr));},
																																	9, {sin(1*pi*(i/sr));},
																																	10, {sin(1*(7pi/6)*(i/sr));},
																																	11, {sin(1*(5pi/4)*(i/sr));},
																																	12, {sin(1*(4pi/3)*(i/sr));},
																																	13, {sin(1*(3pi/2)*(i/sr));},
																																	14, {sin(1*(5pi/3)*(i/sr));},
																																	15, {sin(1*(7pi/4)*(i/sr));},
																																	16, {sin(1*(11pi/6)*(i/sr));},
																																	17, {(Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																	}).sum*0.3;)},
																																	18, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																	}).sum*(2pi)*0.04;},
																																	19, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		sin(2pi*harmonic*1*(i/sr))/harmonic
																																	}).sum*0.3;},
																																	20, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																	}).sum*(4/pi)*0.3;},
																																	21, {(0.2.rand2);}
																																);
																																},
																																{
																																	if(i < ((sr/divisions)*13), {
																																		switch (divmaths[12],
																																			0, {0;},
																																			1, {(sin(1*2pi*(i/sr)))},
																																			2, {sin(1*(pi/6)*(i/sr));},
																																			3, {sin(1*(pi/4)*(i/sr));},
																																			4, {sin(1*(pi/3)*(i/sr));},
																																			5, {sin(1*(pi/2)*(i/sr));},
																																			6, {sin(1*(2pi/3)*(i/sr));},
																																			7, {sin(1*(3pi/4)*(i/sr));},
																																			8, {sin(1*(5pi/6)*(i/sr));},
																																			9, {sin(1*pi*(i/sr));},
																																			10, {sin(1*(7pi/6)*(i/sr));},
																																			11, {sin(1*(5pi/4)*(i/sr));},
																																			12, {sin(1*(4pi/3)*(i/sr));},
																																			13, {sin(1*(3pi/2)*(i/sr));},
																																			14, {sin(1*(5pi/3)*(i/sr));},
																																			15, {sin(1*(7pi/4)*(i/sr));},
																																			16, {sin(1*(11pi/6)*(i/sr));},
																																			17, {(Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																			}).sum*0.3;)},
																																			18, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																			}).sum*(2pi)*0.04;},
																																			19, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				sin(2pi*harmonic*1*(i/sr))/harmonic
																																			}).sum*0.3;},
																																			20, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																			}).sum*(4/pi)*0.3;},
																																			21, {(0.2.rand2);}
																																		);
																																		},
																																		{
																																		};
																																	);
																																};
																															);
																														};
																													);
																												};
																											);
																										};
																									);
																								};
																							);
																						};
																					);
																				};
																			);
																		};
																	);
																};
															);
														};
													);
												};
											);
										};
									);
								};
							);
						};
					);
				});
				plot.value_((array));
				presetDict.add(parray -> array);
				presetDict.add(pdivMenu11 -> divMenu1[0].value);
				presetDict.add(pdivMenu12 -> divMenu1[1].value);
				presetDict.add(pdivMenu13 -> divMenu1[2].value);
				presetDict.add(pdivMenu14 -> divMenu1[3].value);
				presetDict.add(pdivMath1 -> divMathArray[0].value);
				presetDict.add(pdivMath2 -> divMathArray[1].value);
				presetDict.add(pdivMath3 -> divMathArray[2].value);
				presetDict.add(pdivMath4 -> divMathArray[3].value);
				presetDict.add(pdivmaths1 -> divmaths[0].value);
				presetDict.add(pdivmaths2 -> divmaths[1].value);
				presetDict.add(pdivmaths3 -> divmaths[2].value);
				presetDict.add(pdivmaths4 -> divmaths[3].value);

				xbuf = Buffer.loadCollection(s, array);
				synth.set(\bufnum, xbuf.bufnum);
				presetDict.add(pxbuf -> xbuf.value);

			});
		};

		// menu actions for menus 5 - 8
		int2 = 4;
		4.do{ arg i;
			divMenu2[i].action_({arg menu;
				case
				{(divMenu2[i] == divMenu2[0])} {
					int2 = 4;
				}
				{(divMenu2[i] == divMenu2[1])} {
					int2 = 5;
				}
				{(divMenu2[i] == divMenu2[2])} {
					int2 = 6;
				}
				{(divMenu2[i] == divMenu2[3])} {
					int2 = 7;
				};
				case
				{(menu.value == 0)} {
					divmaths[int2] = 0;
					divMathArray[int2] = maths.getZero;
				}
				{(menu.value == 1)} {
					divmaths[int2] = 1;
					divMathArray[int2] = maths.getSin360;
				}
				{(menu.value == 2)} {
					divmaths[int2] = 2;
					divMathArray[int2] = maths.getSin30;
				}
				{(menu.value == 3)} {
					divmaths[int2] = 3;
					divMathArray[int2] = maths.getSin45;
				}
				{(menu.value == 4)} {
					divmaths[int2] = 4;
					divMathArray[int2] = maths.getSin60;
				}
				{(menu.value == 5)} {
					divmaths[int2] = 5;
					divMathArray[int2] = maths.getSin90;
				}
				{(menu.value == 6)} {
					divmaths[int2] = 6;
					divMathArray[int2] = maths.getSin120;
				}
				{(menu.value == 7)} {
					divmaths[int2] = 7;
					divMathArray[int2] = maths.getSin135;
				}
				{(menu.value == 8)} {
					divmaths[int2] = 8;
					divMathArray[int2] = maths.getSin150;
				}
				{(menu.value == 9)} {
					divmaths[int2] = 9;
					divMathArray[int2] = maths.getSin180;
				}
				{(menu.value == 10)} {
					divmaths[int2] = 10;
					divMathArray[int2] = maths.getSin210;
				}
				{(menu.value == 11)} {
					divmaths[int2] = 11;
					divMathArray[int2] = maths.getSin225;
				}
				{(menu.value == 12)} {
					divmaths[int2] = 12;
					divMathArray[int2] = maths.getSin240;
				}
				{(menu.value == 13)} {
					divmaths[int2] = 13;
					divMathArray[int2] = maths.getSin270;
				}
				{(menu.value == 14)} {
					divmaths[int2] = 14;
					divMathArray[int2] = maths.getSin300;
				}
				{(menu.value == 15)} {
					divmaths[int2] = 15;
					divMathArray[int2] = maths.getSin315;
				}
				{(menu.value == 16)} {
					divmaths[int2] = 16;
					divMathArray[int2] = maths.getSin330;
				}
				{(menu.value == 17)} {
					divmaths[int2] = 17;
					divMathArray[int2] = maths.getSaw1;
				}
				{(menu.value == 18)} {
					divmaths[int2] = 18;
					divMathArray[int2] = maths.getSaw2;
				}
				{(menu.value == 19)} {
					divmaths[int2] = 19;
					divMathArray[int2] = maths.getSaw3;
				}
				{(menu.value == 20)} {
					divmaths[int2] = 20;
					divMathArray[int2] = maths.getSquare1;
				}
				{(menu.value == 21)} {
					divmaths[int2] = 21;
					divMathArray[int2] = maths.getNoise1;
				};
				array = Array.fill(sr, {arg i;
					if(sr/divisions*i == 0,
						{
							0;
						},
						{
							if(divisions == 1,
								{
									switch (divmaths[0],
										0, {0;},
										1, {(sin(1*2pi*(i/sr)))},
										2, {sin(1*(pi/6)*(i/sr));},
										3, {sin(1*(pi/4)*(i/sr));},
										4, {sin(1*(pi/3)*(i/sr));},
										5, {sin(1*(pi/2)*(i/sr));},
										6, {sin(1*(2pi/3)*(i/sr));},
										7, {sin(1*(3pi/4)*(i/sr));},
										8, {sin(1*(5pi/6)*(i/sr));},
										9, {sin(1*pi*(i/sr));},
										10, {sin(1*(7pi/6)*(i/sr));},
										11, {sin(1*(5pi/4)*(i/sr));},
										12, {sin(1*(4pi/3)*(i/sr));},
										13, {sin(1*(3pi/2)*(i/sr));},
										14, {sin(1*(5pi/3)*(i/sr));},
										15, {sin(1*(7pi/4)*(i/sr));},
										16, {sin(1*(11pi/6)*(i/sr));},
										17, {(Array.fill(50,{arg j;
											var harmonic = j+1;
											((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
										}).sum*0.3;)},
										18, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
										}).sum*(2pi)*0.04;},
										19, {Array.fill(50,{arg j;
											var harmonic = j+1;
											sin(2pi*harmonic*1*(i/sr))/harmonic
										}).sum*0.3;},
										20, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
										}).sum*(4/pi)*0.3;},
										21, {(0.2.rand2);}
									);
								},
								{
									if(i < (sr/divisions),
										{
											switch (divmaths[0],
												0, {0;},
												1, {(sin(1*2pi*(i/sr)))},
												2, {sin(1*(pi/6)*(i/sr));},
												3, {sin(1*(pi/4)*(i/sr));},
												4, {sin(1*(pi/3)*(i/sr));},
												5, {sin(1*(pi/2)*(i/sr));},
												6, {sin(1*(2pi/3)*(i/sr));},
												7, {sin(1*(3pi/4)*(i/sr));},
												8, {sin(1*(5pi/6)*(i/sr));},
												9, {sin(1*pi*(i/sr));},
												10, {sin(1*(7pi/6)*(i/sr));},
												11, {sin(1*(5pi/4)*(i/sr));},
												12, {sin(1*(4pi/3)*(i/sr));},
												13, {sin(1*(3pi/2)*(i/sr));},
												14, {sin(1*(5pi/3)*(i/sr));},
												15, {sin(1*(7pi/4)*(i/sr));},
												16, {sin(1*(11pi/6)*(i/sr));},
												17, {(Array.fill(50,{arg j;
													var harmonic = j+1;
													((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
												}).sum*0.3;)},
												18, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
												}).sum*(2pi)*0.04;},
												19, {Array.fill(50,{arg j;
													var harmonic = j+1;
													sin(2pi*harmonic*1*(i/sr))/harmonic
												}).sum*0.3;},
												20, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
												}).sum*(4/pi)*0.3;},
												21, {(0.2.rand2);}
											);
										},
										{
											if(i < ((sr/divisions)*2), {
												switch (divmaths[1],
													0, {0;},
													1, {(sin(1*2pi*(i/sr)))},
													2, {sin(1*(pi/6)*(i/sr));},
													3, {sin(1*(pi/4)*(i/sr));},
													4, {sin(1*(pi/3)*(i/sr));},
													5, {sin(1*(pi/2)*(i/sr));},
													6, {sin(1*(2pi/3)*(i/sr));},
													7, {sin(1*(3pi/4)*(i/sr));},
													8, {sin(1*(5pi/6)*(i/sr));},
													9, {sin(1*pi*(i/sr));},
													10, {sin(1*(7pi/6)*(i/sr));},
													11, {sin(1*(5pi/4)*(i/sr));},
													12, {sin(1*(4pi/3)*(i/sr));},
													13, {sin(1*(3pi/2)*(i/sr));},
													14, {sin(1*(5pi/3)*(i/sr));},
													15, {sin(1*(7pi/4)*(i/sr));},
													16, {sin(1*(11pi/6)*(i/sr));},
													17, {(Array.fill(50,{arg j;
														var harmonic = j+1;
														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
													}).sum*0.3;)},
													18, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
													}).sum*(2pi)*0.04;},
													19, {Array.fill(50,{arg j;
														var harmonic = j+1;
														sin(2pi*harmonic*1*(i/sr))/harmonic
													}).sum*0.3;},
													20, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
													}).sum*(4/pi)*0.3;},
													21, {(0.2.rand2);}
												);
												},
												{
													if(i < ((sr/divisions)*3), {
														switch (divmaths[2],
															0, {0;},
															1, {(sin(1*2pi*(i/sr)))},
															2, {sin(1*(pi/6)*(i/sr));},
															3, {sin(1*(pi/4)*(i/sr));},
															4, {sin(1*(pi/3)*(i/sr));},
															5, {sin(1*(pi/2)*(i/sr));},
															6, {sin(1*(2pi/3)*(i/sr));},
															7, {sin(1*(3pi/4)*(i/sr));},
															8, {sin(1*(5pi/6)*(i/sr));},
															9, {sin(1*pi*(i/sr));},
															10, {sin(1*(7pi/6)*(i/sr));},
															11, {sin(1*(5pi/4)*(i/sr));},
															12, {sin(1*(4pi/3)*(i/sr));},
															13, {sin(1*(3pi/2)*(i/sr));},
															14, {sin(1*(5pi/3)*(i/sr));},
															15, {sin(1*(7pi/4)*(i/sr));},
															16, {sin(1*(11pi/6)*(i/sr));},
															17, {(Array.fill(50,{arg j;
																var harmonic = j+1;
																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
															}).sum*0.3;)},
															18, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
															}).sum*(2pi)*0.04;},
															19, {Array.fill(50,{arg j;
																var harmonic = j+1;
																sin(2pi*harmonic*1*(i/sr))/harmonic
															}).sum*0.3;},
															20, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
															}).sum*(4/pi)*0.3;},
															21, {(0.2.rand2);}
														);
														},
														{
															if(i < ((sr/divisions)*4), {
																switch (divmaths[3],
																	0, {0;},
																	1, {(sin(1*2pi*(i/sr)))},
																	2, {sin(1*(pi/6)*(i/sr));},
																	3, {sin(1*(pi/4)*(i/sr));},
																	4, {sin(1*(pi/3)*(i/sr));},
																	5, {sin(1*(pi/2)*(i/sr));},
																	6, {sin(1*(2pi/3)*(i/sr));},
																	7, {sin(1*(3pi/4)*(i/sr));},
																	8, {sin(1*(5pi/6)*(i/sr));},
																	9, {sin(1*pi*(i/sr));},
																	10, {sin(1*(7pi/6)*(i/sr));},
																	11, {sin(1*(5pi/4)*(i/sr));},
																	12, {sin(1*(4pi/3)*(i/sr));},
																	13, {sin(1*(3pi/2)*(i/sr));},
																	14, {sin(1*(5pi/3)*(i/sr));},
																	15, {sin(1*(7pi/4)*(i/sr));},
																	16, {sin(1*(11pi/6)*(i/sr));},
																	17, {(Array.fill(50,{arg j;
																		var harmonic = j+1;
																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																	}).sum*0.3;)},
																	18, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																	}).sum*(2pi)*0.04;},
																	19, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		sin(2pi*harmonic*1*(i/sr))/harmonic
																	}).sum*0.3;},
																	20, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																	}).sum*(4/pi)*0.3;},
																	21, {(0.2.rand2);}
																);
																},
																{
																	if(i < ((sr/divisions)*5), {
																		switch (divmaths[4],
																			0, {0;},
																			1, {(sin(1*2pi*(i/sr)))},
																			2, {sin(1*(pi/6)*(i/sr));},
																			3, {sin(1*(pi/4)*(i/sr));},
																			4, {sin(1*(pi/3)*(i/sr));},
																			5, {sin(1*(pi/2)*(i/sr));},
																			6, {sin(1*(2pi/3)*(i/sr));},
																			7, {sin(1*(3pi/4)*(i/sr));},
																			8, {sin(1*(5pi/6)*(i/sr));},
																			9, {sin(1*pi*(i/sr));},
																			10, {sin(1*(7pi/6)*(i/sr));},
																			11, {sin(1*(5pi/4)*(i/sr));},
																			12, {sin(1*(4pi/3)*(i/sr));},
																			13, {sin(1*(3pi/2)*(i/sr));},
																			14, {sin(1*(5pi/3)*(i/sr));},
																			15, {sin(1*(7pi/4)*(i/sr));},
																			16, {sin(1*(11pi/6)*(i/sr));},
																			17, {(Array.fill(50,{arg j;
																				var harmonic = j+1;
																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																			}).sum*0.3;)},
																			18, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																			}).sum*(2pi)*0.04;},
																			19, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				sin(2pi*harmonic*1*(i/sr))/harmonic
																			}).sum*0.3;},
																			20, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																			}).sum*(4/pi)*0.3;},
																			21, {(0.2.rand2);}
																		);

																		},
																		{
																			if(i < ((sr/divisions)*6), {
																				switch (divmaths[5],
																					0, {0;},
																					1, {(sin(1*2pi*(i/sr)))},
																					2, {sin(1*(pi/6)*(i/sr));},
																					3, {sin(1*(pi/4)*(i/sr));},
																					4, {sin(1*(pi/3)*(i/sr));},
																					5, {sin(1*(pi/2)*(i/sr));},
																					6, {sin(1*(2pi/3)*(i/sr));},
																					7, {sin(1*(3pi/4)*(i/sr));},
																					8, {sin(1*(5pi/6)*(i/sr));},
																					9, {sin(1*pi*(i/sr));},
																					10, {sin(1*(7pi/6)*(i/sr));},
																					11, {sin(1*(5pi/4)*(i/sr));},
																					12, {sin(1*(4pi/3)*(i/sr));},
																					13, {sin(1*(3pi/2)*(i/sr));},
																					14, {sin(1*(5pi/3)*(i/sr));},
																					15, {sin(1*(7pi/4)*(i/sr));},
																					16, {sin(1*(11pi/6)*(i/sr));},
																					17, {(Array.fill(50,{arg j;
																						var harmonic = j+1;
																						((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																					}).sum*0.3;)},
																					18, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																					}).sum*(2pi)*0.04;},
																					19, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						sin(2pi*harmonic*1*(i/sr))/harmonic
																					}).sum*0.3;},
																					20, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																					}).sum*(4/pi)*0.3;},
																					21, {(0.2.rand2);}
																				);
																				},
																				{
																					if(i < ((sr/divisions)*7), {
																						switch (divmaths[6],
																							0, {0;},
																							1, {(sin(1*2pi*(i/sr)))},
																							2, {sin(1*(pi/6)*(i/sr));},
																							3, {sin(1*(pi/4)*(i/sr));},
																							4, {sin(1*(pi/3)*(i/sr));},
																							5, {sin(1*(pi/2)*(i/sr));},
																							6, {sin(1*(2pi/3)*(i/sr));},
																							7, {sin(1*(3pi/4)*(i/sr));},
																							8, {sin(1*(5pi/6)*(i/sr));},
																							9, {sin(1*pi*(i/sr));},
																							10, {sin(1*(7pi/6)*(i/sr));},
																							11, {sin(1*(5pi/4)*(i/sr));},
																							12, {sin(1*(4pi/3)*(i/sr));},
																							13, {sin(1*(3pi/2)*(i/sr));},
																							14, {sin(1*(5pi/3)*(i/sr));},
																							15, {sin(1*(7pi/4)*(i/sr));},
																							16, {sin(1*(11pi/6)*(i/sr));},
																							17, {(Array.fill(50,{arg j;
																								var harmonic = j+1;
																								((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																							}).sum*0.3;)},
																							18, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																							}).sum*(2pi)*0.04;},
																							19, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								sin(2pi*harmonic*1*(i/sr))/harmonic
																							}).sum*0.3;},
																							20, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																							}).sum*(4/pi)*0.3;},
																							21, {(0.2.rand2);}
																						);
																						},
																						{
																							if(i < ((sr/divisions)*8), {
																								switch (divmaths[7],
																									0, {0;},
																									1, {(sin(1*2pi*(i/sr)))},
																									2, {sin(1*(pi/6)*(i/sr));},
																									3, {sin(1*(pi/4)*(i/sr));},
																									4, {sin(1*(pi/3)*(i/sr));},
																									5, {sin(1*(pi/2)*(i/sr));},
																									6, {sin(1*(2pi/3)*(i/sr));},
																									7, {sin(1*(3pi/4)*(i/sr));},
																									8, {sin(1*(5pi/6)*(i/sr));},
																									9, {sin(1*pi*(i/sr));},
																									10, {sin(1*(7pi/6)*(i/sr));},
																									11, {sin(1*(5pi/4)*(i/sr));},
																									12, {sin(1*(4pi/3)*(i/sr));},
																									13, {sin(1*(3pi/2)*(i/sr));},
																									14, {sin(1*(5pi/3)*(i/sr));},
																									15, {sin(1*(7pi/4)*(i/sr));},
																									16, {sin(1*(11pi/6)*(i/sr));},
																									17, {(Array.fill(50,{arg j;
																										var harmonic = j+1;
																										((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																									}).sum*0.3;)},
																									18, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																									}).sum*(2pi)*0.04;},
																									19, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										sin(2pi*harmonic*1*(i/sr))/harmonic
																									}).sum*0.3;},
																									20, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																									}).sum*(4/pi)*0.3;},
																									21, {(0.2.rand2);}
																								);
																								},
																								{
																									if(i < ((sr/divisions)*9), {
																										switch (divmaths[8],
																											0, {0;},
																											1, {(sin(1*2pi*(i/sr)))},
																											2, {sin(1*(pi/6)*(i/sr));},
																											3, {sin(1*(pi/4)*(i/sr));},
																											4, {sin(1*(pi/3)*(i/sr));},
																											5, {sin(1*(pi/2)*(i/sr));},
																											6, {sin(1*(2pi/3)*(i/sr));},
																											7, {sin(1*(3pi/4)*(i/sr));},
																											8, {sin(1*(5pi/6)*(i/sr));},
																											9, {sin(1*pi*(i/sr));},
																											10, {sin(1*(7pi/6)*(i/sr));},
																											11, {sin(1*(5pi/4)*(i/sr));},
																											12, {sin(1*(4pi/3)*(i/sr));},
																											13, {sin(1*(3pi/2)*(i/sr));},
																											14, {sin(1*(5pi/3)*(i/sr));},
																											15, {sin(1*(7pi/4)*(i/sr));},
																											16, {sin(1*(11pi/6)*(i/sr));},
																											17, {(Array.fill(50,{arg j;
																												var harmonic = j+1;
																												((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																											}).sum*0.3;)},
																											18, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																											}).sum*(2pi)*0.04;},
																											19, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												sin(2pi*harmonic*1*(i/sr))/harmonic
																											}).sum*0.3;},
																											20, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																											}).sum*(4/pi)*0.3;},
																											21, {(0.2.rand2);}
																										);
																										},
																										{
																											if(i < ((sr/divisions)*10), {
																												switch (divmaths[9],
																													0, {0;},
																													1, {(sin(1*2pi*(i/sr)))},
																													2, {sin(1*(pi/6)*(i/sr));},
																													3, {sin(1*(pi/4)*(i/sr));},
																													4, {sin(1*(pi/3)*(i/sr));},
																													5, {sin(1*(pi/2)*(i/sr));},
																													6, {sin(1*(2pi/3)*(i/sr));},
																													7, {sin(1*(3pi/4)*(i/sr));},
																													8, {sin(1*(5pi/6)*(i/sr));},
																													9, {sin(1*pi*(i/sr));},
																													10, {sin(1*(7pi/6)*(i/sr));},
																													11, {sin(1*(5pi/4)*(i/sr));},
																													12, {sin(1*(4pi/3)*(i/sr));},
																													13, {sin(1*(3pi/2)*(i/sr));},
																													14, {sin(1*(5pi/3)*(i/sr));},
																													15, {sin(1*(7pi/4)*(i/sr));},
																													16, {sin(1*(11pi/6)*(i/sr));},
																													17, {(Array.fill(50,{arg j;
																														var harmonic = j+1;
																														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																													}).sum*0.3;)},
																													18, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																													}).sum*(2pi)*0.04;},
																													19, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														sin(2pi*harmonic*1*(i/sr))/harmonic
																													}).sum*0.3;},
																													20, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																													}).sum*(4/pi)*0.3;},
																													21, {(0.2.rand2);}
																												);
																												},
																												{
																													if(i < ((sr/divisions)*11), {
																														switch (divmaths[10],
																															0, {0;},
																															1, {(sin(1*2pi*(i/sr)))},
																															2, {sin(1*(pi/6)*(i/sr));},
																															3, {sin(1*(pi/4)*(i/sr));},
																															4, {sin(1*(pi/3)*(i/sr));},
																															5, {sin(1*(pi/2)*(i/sr));},
																															6, {sin(1*(2pi/3)*(i/sr));},
																															7, {sin(1*(3pi/4)*(i/sr));},
																															8, {sin(1*(5pi/6)*(i/sr));},
																															9, {sin(1*pi*(i/sr));},
																															10, {sin(1*(7pi/6)*(i/sr));},
																															11, {sin(1*(5pi/4)*(i/sr));},
																															12, {sin(1*(4pi/3)*(i/sr));},
																															13, {sin(1*(3pi/2)*(i/sr));},
																															14, {sin(1*(5pi/3)*(i/sr));},
																															15, {sin(1*(7pi/4)*(i/sr));},
																															16, {sin(1*(11pi/6)*(i/sr));},
																															17, {(Array.fill(50,{arg j;
																																var harmonic = j+1;
																																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																															}).sum*0.3;)},
																															18, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																															}).sum*(2pi)*0.04;},
																															19, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																sin(2pi*harmonic*1*(i/sr))/harmonic
																															}).sum*0.3;},
																															20, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																															}).sum*(4/pi)*0.3;},
																															21, {(0.2.rand2);}
																														);
																														},
																														{
																															if(i < ((sr/divisions)*12), {
																																switch (divmaths[11],
																																	0, {0;},
																																	1, {(sin(1*2pi*(i/sr)))},
																																	2, {sin(1*(pi/6)*(i/sr));},
																																	3, {sin(1*(pi/4)*(i/sr));},
																																	4, {sin(1*(pi/3)*(i/sr));},
																																	5, {sin(1*(pi/2)*(i/sr));},
																																	6, {sin(1*(2pi/3)*(i/sr));},
																																	7, {sin(1*(3pi/4)*(i/sr));},
																																	8, {sin(1*(5pi/6)*(i/sr));},
																																	9, {sin(1*pi*(i/sr));},
																																	10, {sin(1*(7pi/6)*(i/sr));},
																																	11, {sin(1*(5pi/4)*(i/sr));},
																																	12, {sin(1*(4pi/3)*(i/sr));},
																																	13, {sin(1*(3pi/2)*(i/sr));},
																																	14, {sin(1*(5pi/3)*(i/sr));},
																																	15, {sin(1*(7pi/4)*(i/sr));},
																																	16, {sin(1*(11pi/6)*(i/sr));},
																																	17, {(Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																	}).sum*0.3;)},
																																	18, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																	}).sum*(2pi)*0.04;},
																																	19, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		sin(2pi*harmonic*1*(i/sr))/harmonic
																																	}).sum*0.3;},
																																	20, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																	}).sum*(4/pi)*0.3;},
																																	21, {(0.2.rand2);}
																																);
																																},
																																{
																																	if(i < ((sr/divisions)*13), {
																																		switch (divmaths[12],
																																			0, {0;},
																																			1, {(sin(1*2pi*(i/sr)))},
																																			2, {sin(1*(pi/6)*(i/sr));},
																																			3, {sin(1*(pi/4)*(i/sr));},
																																			4, {sin(1*(pi/3)*(i/sr));},
																																			5, {sin(1*(pi/2)*(i/sr));},
																																			6, {sin(1*(2pi/3)*(i/sr));},
																																			7, {sin(1*(3pi/4)*(i/sr));},
																																			8, {sin(1*(5pi/6)*(i/sr));},
																																			9, {sin(1*pi*(i/sr));},
																																			10, {sin(1*(7pi/6)*(i/sr));},
																																			11, {sin(1*(5pi/4)*(i/sr));},
																																			12, {sin(1*(4pi/3)*(i/sr));},
																																			13, {sin(1*(3pi/2)*(i/sr));},
																																			14, {sin(1*(5pi/3)*(i/sr));},
																																			15, {sin(1*(7pi/4)*(i/sr));},
																																			16, {sin(1*(11pi/6)*(i/sr));},
																																			17, {(Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																			}).sum*0.3;)},
																																			18, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																			}).sum*(2pi)*0.04;},
																																			19, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				sin(2pi*harmonic*1*(i/sr))/harmonic
																																			}).sum*0.3;},
																																			20, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																			}).sum*(4/pi)*0.3;},
																																			21, {(0.2.rand2);}
																																		);
																																		},
																																		{
																																		};
																																	);
																																};
																															);
																														};
																													);
																												};
																											);
																										};
																									);
																								};
																							);
																						};
																					);
																				};
																			);
																		};
																	);
																};
															);
														};
													);
												};
											);
										};
									);
								};
							);
						};
					);
				});
				plot.value_((array));

				presetDict.add(pdivMenu21 -> divMenu2[0].value);
				presetDict.add(pdivMenu22 -> divMenu2[1].value);
				presetDict.add(pdivMenu23 -> divMenu2[2].value);
				presetDict.add(pdivMenu24 -> divMenu2[3].value);
				presetDict.add(pdivMath5 -> divMathArray[4].value);
				presetDict.add(pdivMath6 -> divMathArray[5].value);
				presetDict.add(pdivMath7 -> divMathArray[6].value);
				presetDict.add(pdivMath8 -> divMathArray[7].value);
				presetDict.add(pdivmaths5 -> divmaths[4].value);
				presetDict.add(pdivmaths6 -> divmaths[5].value);
				presetDict.add(pdivmaths7 -> divmaths[6].value);
				presetDict.add(pdivmaths8 -> divmaths[7].value);
				presetDict.add(parray -> array);

				xbuf = Buffer.loadCollection(s, array);
				synth.set(\bufnum, xbuf.bufnum);
				presetDict.add(pxbuf -> xbuf.value);
			});
		};

		// menu actions for menus 9 - 12
		int3 = 8;
		4.do{ arg i;
			divMenu3[i].action_({arg menu;
				case
				{(divMenu3[i] == divMenu3[0])} {
					int3 = 8;
				}
				{(divMenu3[i] == divMenu3[1])} {
					int3 = 9;
				}
				{(divMenu3[i] == divMenu3[2])} {
					int3 = 10;
				}
				{(divMenu3[i] == divMenu3[3])} {
					int3 = 11;
				};
				case
				{(menu.value == 0)} {
					divmaths[int3] = 0;
					divMathArray[int3] = maths.getZero;
				}
				{(menu.value == 1)} {
					divmaths[int3] = 1;
					divMathArray[int3] = maths.getSin360;
				}
				{(menu.value == 2)} {
					divmaths[int3] = 2;
					divMathArray[int3] = maths.getSin30;
				}
				{(menu.value == 3)} {
					divmaths[int3] = 3;
					divMathArray[int3] = maths.getSin45;
				}
				{(menu.value == 4)} {
					divmaths[int3] = 4;
					divMathArray[int3] = maths.getSin60;
				}
				{(menu.value == 5)} {
					divmaths[int3] = 5;
					divMathArray[int3] = maths.getSin90;
				}
				{(menu.value == 6)} {
					divmaths[int3] = 6;
					divMathArray[int3] = maths.getSin120;
				}
				{(menu.value == 7)} {
					divmaths[int3] = 7;
					divMathArray[int3] = maths.getSin135;
				}
				{(menu.value == 8)} {
					divmaths[int3] = 8;
					divMathArray[int3] = maths.getSin150;
				}
				{(menu.value == 9)} {
					divmaths[int3] = 9;
					divMathArray[int3] = maths.getSin180;
				}
				{(menu.value == 10)} {
					divmaths[int3] = 10;
					divMathArray[int3] = maths.getSin210;
				}
				{(menu.value == 11)} {
					divmaths[int3] = 11;
					divMathArray[int3] = maths.getSin225;
				}
				{(menu.value == 12)} {
					divmaths[int3] = 12;
					divMathArray[int3] = maths.getSin240;
				}
				{(menu.value == 13)} {
					divmaths[int3] = 13;
					divMathArray[int3] = maths.getSin270;
				}
				{(menu.value == 14)} {
					divmaths[int3] = 14;
					divMathArray[int3] = maths.getSin300;
				}
				{(menu.value == 15)} {
					divmaths[int3] = 15;
					divMathArray[int3] = maths.getSin315;
				}
				{(menu.value == 16)} {
					divmaths[int3] = 16;
					divMathArray[int3] = maths.getSin330;
				}
				{(menu.value == 17)} {
					divmaths[int3] = 17;
					divMathArray[int3] = maths.getSaw1;
				}
				{(menu.value == 18)} {
					divmaths[int3] = 18;
					divMathArray[int3] = maths.getSaw2;
				}
				{(menu.value == 19)} {
					divmaths[int3] = 19;
					divMathArray[int3] = maths.getSaw3;
				}
				{(menu.value == 20)} {
					divmaths[int3] = 20;
					divMathArray[int3] = maths.getSquare1;
				}
				{(menu.value == 21)} {
					divmaths[int3] = 21;
					divMathArray[int3] = maths.getNoise1;
				};
				array = Array.fill(sr, {arg i;
					if(sr/divisions*i == 0,
						{
							0;
						},
						{
							if(divisions == 1,
								{
									switch (divmaths[0],
										0, {0;},
										1, {(sin(1*2pi*(i/sr)))},
										2, {sin(1*(pi/6)*(i/sr));},
										3, {sin(1*(pi/4)*(i/sr));},
										4, {sin(1*(pi/3)*(i/sr));},
										5, {sin(1*(pi/2)*(i/sr));},
										6, {sin(1*(2pi/3)*(i/sr));},
										7, {sin(1*(3pi/4)*(i/sr));},
										8, {sin(1*(5pi/6)*(i/sr));},
										9, {sin(1*pi*(i/sr));},
										10, {sin(1*(7pi/6)*(i/sr));},
										11, {sin(1*(5pi/4)*(i/sr));},
										12, {sin(1*(4pi/3)*(i/sr));},
										13, {sin(1*(3pi/2)*(i/sr));},
										14, {sin(1*(5pi/3)*(i/sr));},
										15, {sin(1*(7pi/4)*(i/sr));},
										16, {sin(1*(11pi/6)*(i/sr));},
										17, {(Array.fill(50,{arg j;
											var harmonic = j+1;
											((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
										}).sum*0.3;)},
										18, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
										}).sum*(2pi)*0.04;},
										19, {Array.fill(50,{arg j;
											var harmonic = j+1;
											sin(2pi*harmonic*1*(i/sr))/harmonic
										}).sum*0.3;},
										20, {Array.fill(50,{arg j;
											var harmonic = j+1;
											(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
										}).sum*(4/pi)*0.3;},
										21, {(0.2.rand2);}
									);
								},
								{
									if(i < (sr/divisions),
										{
											switch (divmaths[0],
												0, {0;},
												1, {(sin(1*2pi*(i/sr)))},
												2, {sin(1*(pi/6)*(i/sr));},
												3, {sin(1*(pi/4)*(i/sr));},
												4, {sin(1*(pi/3)*(i/sr));},
												5, {sin(1*(pi/2)*(i/sr));},
												6, {sin(1*(2pi/3)*(i/sr));},
												7, {sin(1*(3pi/4)*(i/sr));},
												8, {sin(1*(5pi/6)*(i/sr));},
												9, {sin(1*pi*(i/sr));},
												10, {sin(1*(7pi/6)*(i/sr));},
												11, {sin(1*(5pi/4)*(i/sr));},
												12, {sin(1*(4pi/3)*(i/sr));},
												13, {sin(1*(3pi/2)*(i/sr));},
												14, {sin(1*(5pi/3)*(i/sr));},
												15, {sin(1*(7pi/4)*(i/sr));},
												16, {sin(1*(11pi/6)*(i/sr));},
												17, {(Array.fill(50,{arg j;
													var harmonic = j+1;
													((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
												}).sum*0.3;)},
												18, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
												}).sum*(2pi)*0.04;},
												19, {Array.fill(50,{arg j;
													var harmonic = j+1;
													sin(2pi*harmonic*1*(i/sr))/harmonic
												}).sum*0.3;},
												20, {Array.fill(50,{arg j;
													var harmonic = j+1;
													(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
												}).sum*(4/pi)*0.3;},
												21, {(0.2.rand2);}
											);
										},
										{
											if(i < ((sr/divisions)*2), {
												switch (divmaths[1],
													0, {0;},
													1, {(sin(1*2pi*(i/sr)))},
													2, {sin(1*(pi/6)*(i/sr));},
													3, {sin(1*(pi/4)*(i/sr));},
													4, {sin(1*(pi/3)*(i/sr));},
													5, {sin(1*(pi/2)*(i/sr));},
													6, {sin(1*(2pi/3)*(i/sr));},
													7, {sin(1*(3pi/4)*(i/sr));},
													8, {sin(1*(5pi/6)*(i/sr));},
													9, {sin(1*pi*(i/sr));},
													10, {sin(1*(7pi/6)*(i/sr));},
													11, {sin(1*(5pi/4)*(i/sr));},
													12, {sin(1*(4pi/3)*(i/sr));},
													13, {sin(1*(3pi/2)*(i/sr));},
													14, {sin(1*(5pi/3)*(i/sr));},
													15, {sin(1*(7pi/4)*(i/sr));},
													16, {sin(1*(11pi/6)*(i/sr));},
													17, {(Array.fill(50,{arg j;
														var harmonic = j+1;
														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
													}).sum*0.3;)},
													18, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
													}).sum*(2pi)*0.04;},
													19, {Array.fill(50,{arg j;
														var harmonic = j+1;
														sin(2pi*harmonic*1*(i/sr))/harmonic
													}).sum*0.3;},
													20, {Array.fill(50,{arg j;
														var harmonic = j+1;
														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
													}).sum*(4/pi)*0.3;},
													21, {(0.2.rand2);}
												);
												},
												{
													if(i < ((sr/divisions)*3), {
														switch (divmaths[2],
															0, {0;},
															1, {(sin(1*2pi*(i/sr)))},
															2, {sin(1*(pi/6)*(i/sr));},
															3, {sin(1*(pi/4)*(i/sr));},
															4, {sin(1*(pi/3)*(i/sr));},
															5, {sin(1*(pi/2)*(i/sr));},
															6, {sin(1*(2pi/3)*(i/sr));},
															7, {sin(1*(3pi/4)*(i/sr));},
															8, {sin(1*(5pi/6)*(i/sr));},
															9, {sin(1*pi*(i/sr));},
															10, {sin(1*(7pi/6)*(i/sr));},
															11, {sin(1*(5pi/4)*(i/sr));},
															12, {sin(1*(4pi/3)*(i/sr));},
															13, {sin(1*(3pi/2)*(i/sr));},
															14, {sin(1*(5pi/3)*(i/sr));},
															15, {sin(1*(7pi/4)*(i/sr));},
															16, {sin(1*(11pi/6)*(i/sr));},
															17, {(Array.fill(50,{arg j;
																var harmonic = j+1;
																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
															}).sum*0.3;)},
															18, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
															}).sum*(2pi)*0.04;},
															19, {Array.fill(50,{arg j;
																var harmonic = j+1;
																sin(2pi*harmonic*1*(i/sr))/harmonic
															}).sum*0.3;},
															20, {Array.fill(50,{arg j;
																var harmonic = j+1;
																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
															}).sum*(4/pi)*0.3;},
															21, {(0.2.rand2);}
														);
														},
														{
															if(i < ((sr/divisions)*4), {
																switch (divmaths[3],
																	0, {0;},
																	1, {(sin(1*2pi*(i/sr)))},
																	2, {sin(1*(pi/6)*(i/sr));},
																	3, {sin(1*(pi/4)*(i/sr));},
																	4, {sin(1*(pi/3)*(i/sr));},
																	5, {sin(1*(pi/2)*(i/sr));},
																	6, {sin(1*(2pi/3)*(i/sr));},
																	7, {sin(1*(3pi/4)*(i/sr));},
																	8, {sin(1*(5pi/6)*(i/sr));},
																	9, {sin(1*pi*(i/sr));},
																	10, {sin(1*(7pi/6)*(i/sr));},
																	11, {sin(1*(5pi/4)*(i/sr));},
																	12, {sin(1*(4pi/3)*(i/sr));},
																	13, {sin(1*(3pi/2)*(i/sr));},
																	14, {sin(1*(5pi/3)*(i/sr));},
																	15, {sin(1*(7pi/4)*(i/sr));},
																	16, {sin(1*(11pi/6)*(i/sr));},
																	17, {(Array.fill(50,{arg j;
																		var harmonic = j+1;
																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																	}).sum*0.3;)},
																	18, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																	}).sum*(2pi)*0.04;},
																	19, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		sin(2pi*harmonic*1*(i/sr))/harmonic
																	}).sum*0.3;},
																	20, {Array.fill(50,{arg j;
																		var harmonic = j+1;
																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																	}).sum*(4/pi)*0.3;},
																	21, {(0.2.rand2);}
																);
																},
																{
																	if(i < ((sr/divisions)*5), {
																		switch (divmaths[4],
																			0, {0;},
																			1, {(sin(1*2pi*(i/sr)))},
																			2, {sin(1*(pi/6)*(i/sr));},
																			3, {sin(1*(pi/4)*(i/sr));},
																			4, {sin(1*(pi/3)*(i/sr));},
																			5, {sin(1*(pi/2)*(i/sr));},
																			6, {sin(1*(2pi/3)*(i/sr));},
																			7, {sin(1*(3pi/4)*(i/sr));},
																			8, {sin(1*(5pi/6)*(i/sr));},
																			9, {sin(1*pi*(i/sr));},
																			10, {sin(1*(7pi/6)*(i/sr));},
																			11, {sin(1*(5pi/4)*(i/sr));},
																			12, {sin(1*(4pi/3)*(i/sr));},
																			13, {sin(1*(3pi/2)*(i/sr));},
																			14, {sin(1*(5pi/3)*(i/sr));},
																			15, {sin(1*(7pi/4)*(i/sr));},
																			16, {sin(1*(11pi/6)*(i/sr));},
																			17, {(Array.fill(50,{arg j;
																				var harmonic = j+1;
																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																			}).sum*0.3;)},
																			18, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																			}).sum*(2pi)*0.04;},
																			19, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				sin(2pi*harmonic*1*(i/sr))/harmonic
																			}).sum*0.3;},
																			20, {Array.fill(50,{arg j;
																				var harmonic = j+1;
																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																			}).sum*(4/pi)*0.3;},
																			21, {(0.2.rand2);}
																		);
																		},
																		{
																			if(i < ((sr/divisions)*6), {
																				switch (divmaths[5],
																					0, {0;},
																					1, {(sin(1*2pi*(i/sr)))},
																					2, {sin(1*(pi/6)*(i/sr));},
																					3, {sin(1*(pi/4)*(i/sr));},
																					4, {sin(1*(pi/3)*(i/sr));},
																					5, {sin(1*(pi/2)*(i/sr));},
																					6, {sin(1*(2pi/3)*(i/sr));},
																					7, {sin(1*(3pi/4)*(i/sr));},
																					8, {sin(1*(5pi/6)*(i/sr));},
																					9, {sin(1*pi*(i/sr));},
																					10, {sin(1*(7pi/6)*(i/sr));},
																					11, {sin(1*(5pi/4)*(i/sr));},
																					12, {sin(1*(4pi/3)*(i/sr));},
																					13, {sin(1*(3pi/2)*(i/sr));},
																					14, {sin(1*(5pi/3)*(i/sr));},
																					15, {sin(1*(7pi/4)*(i/sr));},
																					16, {sin(1*(11pi/6)*(i/sr));},
																					17, {(Array.fill(50,{arg j;
																						var harmonic = j+1;
																						((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																					}).sum*0.3;)},
																					18, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																					}).sum*(2pi)*0.04;},
																					19, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						sin(2pi*harmonic*1*(i/sr))/harmonic
																					}).sum*0.3;},
																					20, {Array.fill(50,{arg j;
																						var harmonic = j+1;
																						(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																					}).sum*(4/pi)*0.3;},
																					21, {(0.2.rand2);}
																				);
																				},
																				{
																					if(i < ((sr/divisions)*7), {
																						switch (divmaths[6],
																							0, {0;},
																							1, {(sin(1*2pi*(i/sr)))},
																							2, {sin(1*(pi/6)*(i/sr));},
																							3, {sin(1*(pi/4)*(i/sr));},
																							4, {sin(1*(pi/3)*(i/sr));},
																							5, {sin(1*(pi/2)*(i/sr));},
																							6, {sin(1*(2pi/3)*(i/sr));},
																							7, {sin(1*(3pi/4)*(i/sr));},
																							8, {sin(1*(5pi/6)*(i/sr));},
																							9, {sin(1*pi*(i/sr));},
																							10, {sin(1*(7pi/6)*(i/sr));},
																							11, {sin(1*(5pi/4)*(i/sr));},
																							12, {sin(1*(4pi/3)*(i/sr));},
																							13, {sin(1*(3pi/2)*(i/sr));},
																							14, {sin(1*(5pi/3)*(i/sr));},
																							15, {sin(1*(7pi/4)*(i/sr));},
																							16, {sin(1*(11pi/6)*(i/sr));},
																							17, {(Array.fill(50,{arg j;
																								var harmonic = j+1;
																								((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																							}).sum*0.3;)},
																							18, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																							}).sum*(2pi)*0.04;},
																							19, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								sin(2pi*harmonic*1*(i/sr))/harmonic
																							}).sum*0.3;},
																							20, {Array.fill(50,{arg j;
																								var harmonic = j+1;
																								(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																							}).sum*(4/pi)*0.3;},
																							21, {(0.2.rand2);}
																						);
																						},
																						{
																							if(i < ((sr/divisions)*8), {
																								switch (divmaths[7],
																									0, {0;},
																									1, {(sin(1*2pi*(i/sr)))},
																									2, {sin(1*(pi/6)*(i/sr));},
																									3, {sin(1*(pi/4)*(i/sr));},
																									4, {sin(1*(pi/3)*(i/sr));},
																									5, {sin(1*(pi/2)*(i/sr));},
																									6, {sin(1*(2pi/3)*(i/sr));},
																									7, {sin(1*(3pi/4)*(i/sr));},
																									8, {sin(1*(5pi/6)*(i/sr));},
																									9, {sin(1*pi*(i/sr));},
																									10, {sin(1*(7pi/6)*(i/sr));},
																									11, {sin(1*(5pi/4)*(i/sr));},
																									12, {sin(1*(4pi/3)*(i/sr));},
																									13, {sin(1*(3pi/2)*(i/sr));},
																									14, {sin(1*(5pi/3)*(i/sr));},
																									15, {sin(1*(7pi/4)*(i/sr));},
																									16, {sin(1*(11pi/6)*(i/sr));},
																									17, {(Array.fill(50,{arg j;
																										var harmonic = j+1;
																										((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																									}).sum*0.3;)},
																									18, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																									}).sum*(2pi)*0.04;},
																									19, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										sin(2pi*harmonic*1*(i/sr))/harmonic
																									}).sum*0.3;},
																									20, {Array.fill(50,{arg j;
																										var harmonic = j+1;
																										(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																									}).sum*(4/pi)*0.3;},
																									21, {(0.2.rand2);}
																								);
																								},
																								{
																									if(i < ((sr/divisions)*9), {
																										switch (divmaths[8],
																											0, {0;},
																											1, {(sin(1*2pi*(i/sr)))},
																											2, {sin(1*(pi/6)*(i/sr));},
																											3, {sin(1*(pi/4)*(i/sr));},
																											4, {sin(1*(pi/3)*(i/sr));},
																											5, {sin(1*(pi/2)*(i/sr));},
																											6, {sin(1*(2pi/3)*(i/sr));},
																											7, {sin(1*(3pi/4)*(i/sr));},
																											8, {sin(1*(5pi/6)*(i/sr));},
																											9, {sin(1*pi*(i/sr));},
																											10, {sin(1*(7pi/6)*(i/sr));},
																											11, {sin(1*(5pi/4)*(i/sr));},
																											12, {sin(1*(4pi/3)*(i/sr));},
																											13, {sin(1*(3pi/2)*(i/sr));},
																											14, {sin(1*(5pi/3)*(i/sr));},
																											15, {sin(1*(7pi/4)*(i/sr));},
																											16, {sin(1*(11pi/6)*(i/sr));},
																											17, {(Array.fill(50,{arg j;
																												var harmonic = j+1;
																												((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																											}).sum*0.3;)},
																											18, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																											}).sum*(2pi)*0.04;},
																											19, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												sin(2pi*harmonic*1*(i/sr))/harmonic
																											}).sum*0.3;},
																											20, {Array.fill(50,{arg j;
																												var harmonic = j+1;
																												(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																											}).sum*(4/pi)*0.3;},
																											21, {(0.2.rand2);}
																										);
																										},
																										{
																											if(i < ((sr/divisions)*10), {
																												switch (divmaths[9],
																													0, {0;},
																													1, {(sin(1*2pi*(i/sr)))},
																													2, {sin(1*(pi/6)*(i/sr));},
																													3, {sin(1*(pi/4)*(i/sr));},
																													4, {sin(1*(pi/3)*(i/sr));},
																													5, {sin(1*(pi/2)*(i/sr));},
																													6, {sin(1*(2pi/3)*(i/sr));},
																													7, {sin(1*(3pi/4)*(i/sr));},
																													8, {sin(1*(5pi/6)*(i/sr));},
																													9, {sin(1*pi*(i/sr));},
																													10, {sin(1*(7pi/6)*(i/sr));},
																													11, {sin(1*(5pi/4)*(i/sr));},
																													12, {sin(1*(4pi/3)*(i/sr));},
																													13, {sin(1*(3pi/2)*(i/sr));},
																													14, {sin(1*(5pi/3)*(i/sr));},
																													15, {sin(1*(7pi/4)*(i/sr));},
																													16, {sin(1*(11pi/6)*(i/sr));},
																													17, {(Array.fill(50,{arg j;
																														var harmonic = j+1;
																														((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																													}).sum*0.3;)},
																													18, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																													}).sum*(2pi)*0.04;},
																													19, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														sin(2pi*harmonic*1*(i/sr))/harmonic
																													}).sum*0.3;},
																													20, {Array.fill(50,{arg j;
																														var harmonic = j+1;
																														(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																													}).sum*(4/pi)*0.3;},
																													21, {(0.2.rand2);}
																												);
																												},
																												{
																													if(i < ((sr/divisions)*11), {
																														switch (divmaths[10],
																															0, {0;},
																															1, {(sin(1*2pi*(i/sr)))},
																															2, {sin(1*(pi/6)*(i/sr));},
																															3, {sin(1*(pi/4)*(i/sr));},
																															4, {sin(1*(pi/3)*(i/sr));},
																															5, {sin(1*(pi/2)*(i/sr));},
																															6, {sin(1*(2pi/3)*(i/sr));},
																															7, {sin(1*(3pi/4)*(i/sr));},
																															8, {sin(1*(5pi/6)*(i/sr));},
																															9, {sin(1*pi*(i/sr));},
																															10, {sin(1*(7pi/6)*(i/sr));},
																															11, {sin(1*(5pi/4)*(i/sr));},
																															12, {sin(1*(4pi/3)*(i/sr));},
																															13, {sin(1*(3pi/2)*(i/sr));},
																															14, {sin(1*(5pi/3)*(i/sr));},
																															15, {sin(1*(7pi/4)*(i/sr));},
																															16, {sin(1*(11pi/6)*(i/sr));},
																															17, {(Array.fill(50,{arg j;
																																var harmonic = j+1;
																																((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																															}).sum*0.3;)},
																															18, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																															}).sum*(2pi)*0.04;},
																															19, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																sin(2pi*harmonic*1*(i/sr))/harmonic
																															}).sum*0.3;},
																															20, {Array.fill(50,{arg j;
																																var harmonic = j+1;
																																(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																															}).sum*(4/pi)*0.3;},
																															21, {(0.2.rand2);}
																														);
																														},
																														{
																															if(i < ((sr/divisions)*12), {
																																switch (divmaths[11],
																																	0, {0;},
																																	1, {(sin(1*2pi*(i/sr)))},
																																	2, {sin(1*(pi/6)*(i/sr));},
																																	3, {sin(1*(pi/4)*(i/sr));},
																																	4, {sin(1*(pi/3)*(i/sr));},
																																	5, {sin(1*(pi/2)*(i/sr));},
																																	6, {sin(1*(2pi/3)*(i/sr));},
																																	7, {sin(1*(3pi/4)*(i/sr));},
																																	8, {sin(1*(5pi/6)*(i/sr));},
																																	9, {sin(1*pi*(i/sr));},
																																	10, {sin(1*(7pi/6)*(i/sr));},
																																	11, {sin(1*(5pi/4)*(i/sr));},
																																	12, {sin(1*(4pi/3)*(i/sr));},
																																	13, {sin(1*(3pi/2)*(i/sr));},
																																	14, {sin(1*(5pi/3)*(i/sr));},
																																	15, {sin(1*(7pi/4)*(i/sr));},
																																	16, {sin(1*(11pi/6)*(i/sr));},
																																	17, {(Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																	}).sum*0.3;)},
																																	18, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																	}).sum*(2pi)*0.04;},
																																	19, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		sin(2pi*harmonic*1*(i/sr))/harmonic
																																	}).sum*0.3;},
																																	20, {Array.fill(50,{arg j;
																																		var harmonic = j+1;
																																		(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																	}).sum*(4/pi)*0.3;},
																																	21, {(0.2.rand2);}
																																);
																																},
																																{
																																	if(i < ((sr/divisions)*13), {
																																		switch (divmaths[12],
																																			0, {0;},
																																			1, {(sin(1*2pi*(i/sr)))},
																																			2, {sin(1*(pi/6)*(i/sr));},
																																			3, {sin(1*(pi/4)*(i/sr));},
																																			4, {sin(1*(pi/3)*(i/sr));},
																																			5, {sin(1*(pi/2)*(i/sr));},
																																			6, {sin(1*(2pi/3)*(i/sr));},
																																			7, {sin(1*(3pi/4)*(i/sr));},
																																			8, {sin(1*(5pi/6)*(i/sr));},
																																			9, {sin(1*pi*(i/sr));},
																																			10, {sin(1*(7pi/6)*(i/sr));},
																																			11, {sin(1*(5pi/4)*(i/sr));},
																																			12, {sin(1*(4pi/3)*(i/sr));},
																																			13, {sin(1*(3pi/2)*(i/sr));},
																																			14, {sin(1*(5pi/3)*(i/sr));},
																																			15, {sin(1*(7pi/4)*(i/sr));},
																																			16, {sin(1*(11pi/6)*(i/sr));},
																																			17, {(Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(i/sr))
																																			}).sum*0.3;)},
																																			18, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(-1**harmonic)*(sin(2pi*harmonic*1*(i/sr)))/harmonic
																																			}).sum*(2pi)*0.04;},
																																			19, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				sin(2pi*harmonic*1*(i/sr))/harmonic
																																			}).sum*0.3;},
																																			20, {Array.fill(50,{arg j;
																																				var harmonic = j+1;
																																				(sin(2pi*((2*harmonic)-1)*1*(i/sr)))/(2*harmonic-1)
																																			}).sum*(4/pi)*0.3;},
																																			21, {(0.2.rand2);}
																																		);
																																		},
																																		{
																																		};
																																	);
																																};
																															);
																														};
																													);
																												};
																											);
																										};
																									);
																								};
																							);
																						};
																					);
																				};
																			);
																		};
																	);
																};
															);
														};
													);
												};
											);
										};
									);
								};
							);
						};
					);
				});
				plot.value_((array));

				presetDict.add(parray -> array);
				presetDict.add(pdivMenu31 -> divMenu3[0].value);
				presetDict.add(pdivMenu32 -> divMenu3[1].value);
				presetDict.add(pdivMenu33 -> divMenu3[2].value);
				presetDict.add(pdivMenu34 -> divMenu3[3].value);
				presetDict.add(pdivMath9 -> divMathArray[8].value);
				presetDict.add(pdivMath10 -> divMathArray[9].value);
				presetDict.add(pdivMath11 -> divMathArray[10].value);
				presetDict.add(pdivMath12 -> divMathArray[11].value);
				presetDict.add(pdivmaths9 -> divmaths[8].value);
				presetDict.add(pdivmaths10 -> divmaths[9].value);
				presetDict.add(pdivmaths11 -> divmaths[10].value);
				presetDict.add(pdivmaths12 -> divmaths[11].value);

				xbuf = Buffer.loadCollection(s, array);
				synth.set(\bufnum, xbuf.bufnum);
				presetDict.add(pxbuf -> xbuf.value);
			});
		};

		// Plot Buttons:
		plotb1 = Array.fill(4, {arg i; Button(v, Rect(145, 180+(i*60), 30, 25))});
		plotb2 = Array.fill(4, {arg i; Button(v, Rect(305, 180+(i*60), 30, 25))});
		plotb3 = Array.fill(4, {arg i; Button(v, Rect(465, 180+(i*60), 30, 25))});

		plotb1.do{arg i;
			i.states_([["+", Color.white, Color.grey]]);
		};
		plotb2.do{arg i;
			i.states_([["+", Color.white, Color.grey]]);
		};
		plotb3.do{arg i;
			i.states_([["+", Color.white, Color.grey]]);
		};
		4.do{arg i;
			var j = i + 4;
			var k = i + 8;
			plotb1[i].action_({
				divMathArray[i].plot;
			});
			plotb2[i].action_({
				divMathArray[j].plot;
			});
			plotb3[i].action_({
				divMathArray[k].plot;
			});
		};

		// frequency knob:
		fknob = Knob(v, Rect(190, 430, 100, 100));
		fknob.mode = \vert;
		cspec= ControlSpec(10, 8000, 'exponential', 0, 0);
		cspec2= ControlSpec(0.1, 20, 'exponential', 0, 0);
		fknob.action_({
			// change mapping accoding to LFO on/off
			if(lfoOn == false, {
				synth.set(\rate, cspec.map(fknob.value));
				},
				{
					synth.set(\rate, cspec2.map(fknob.value));
			});
			if(lfoKnob == true, {
				synth.set(\rate2, cspec2.map(fknob.value));
			});
			presetDict.add(pfknob -> fknob.value);
		});
		fknob.color_([Color.white, Color.grey(0.3), Color.white, Color.grey(0.3)]);

		// Preset Text:
		presetText = StaticText(titlecv, Rect(40, 50, 460, 25));
		presetText.string = "PRESET:";
		presetText.font_(font2);
		presetText.stringColor = Color.white;

		// Window Number
		wNo = StaticText(titlecv, Rect(400, 50, 460, 25));
		wNo.string = "WT:" + winNo;
		wNo.font_(font2);
		wNo.stringColor = Color.white;

		// Plot/Frequency text
		txtFreqPlot = Array.fill(2, {arg i; StaticText(v, Rect(190, 405 + (i*130), 200, 20))});
		txtFreqPlot[0].string = "    FREQUENCY";
		txtFreqPlot[1].string = "WAVEFORM PLOT";

		// Diviion text:
		divtxt1 = StaticText(v, Rect(190, 95, 100, 20));
		divtxt1.string = "No. of Divisions";
		divtxt2 = Array.fill(4, {arg i; StaticText(v, Rect(60, 160 + (i*60), 200, 20))});
		divtxt3 = Array.fill(4, {arg i; StaticText(v, Rect(220, 160 + (i*60), 200, 20))});
		divtxt4 = Array.fill(4, {arg i; StaticText(v, Rect(380, 160 + (i*60), 200, 20))});
		divtxt2[0].string = "DIV. 1";
		divtxt2[1].string = "DIV. 2";
		divtxt2[2].string = "DIV. 3";
		divtxt2[3].string = "DIV. 4";
		divtxt3[0].string = "DIV. 5";
		divtxt3[1].string = "DIV. 6";
		divtxt3[2].string = "DIV. 7";
		divtxt3[3].string = "DIV. 8";
		divtxt4[0].string = "DIV. 9";
		divtxt4[1].string = "DIV. 10";
		divtxt4[2].string = "DIV. 11";
		divtxt4[3].string = "DIV. 12";

		// Preset Buttons:
		presetbs = Array.fill(2, {arg i; Button(v, Rect(50 + (i*290), 440, 90, 40))});
		presetbs[0].states_([["LOAD", Color.white, Color.grey(0.3, 0.7)]]);
		presetbs[1].states_([["SAVE", Color.white, Color.grey(0.3, 0.7)]]);

		lfob = Button(v, Rect(340, 500, 40, 40));
		lfob.states_([["OFF", Color.white, Color.grey(0.3, 0.7)], ["ON", Color.white, Color.red(0.5, 0.7)]]);
		// Need to say if lfo is true, then lfo is false. if lfo is false then true
		lfob.action_({arg i;
			if(lfoOn == false, {
				lfoOn = true;
				lfoKnob = true;
				synth.set(\rate, cspec2.map(fknob.value));
				},
				{
					lfoOn = false;
					lfoKnob = false;
					synth.set(\rate, cspec.map(fknob.value));
			});
		});

		lfoText = StaticText(v, Rect(385, 518, 100, 30));
		lfoText.string = "LFO trigger";
		lfoText.stringColor = Color.white;

		//load
		presetbs[0].action_({arg i;
			"Loading Wavetable Divider presets...please wait...(Takes a little while)...".postln;

			Dialog.openPanel({arg path;

				presetDict = Object.readArchive(path);
				myPath = PathName.new(path);
				presetText.string = "PRESET:" + myPath.fileName;

				divisions = presetDict.at(0);
				divisionsMenu.value = presetDict.at(0)-1;
				array = presetDict.at(1);

				fknob.value = presetDict.at(2);
				divMenu1[0].value = presetDict.at(3);
				divMenu1[1].value = presetDict.at(4);
				divMenu1[2].value = presetDict.at(5);
				divMenu1[3].value = presetDict.at(6);
				divMenu2[0].value = presetDict.at(7);
				divMenu2[1].value = presetDict.at(8);
				divMenu2[2].value = presetDict.at(9);
				divMenu2[3].value = presetDict.at(10);
				divMenu3[0].value = presetDict.at(11);
				divMenu3[1].value = presetDict.at(12);
				divMenu3[2].value = presetDict.at(13);
				divMenu3[3].value = presetDict.at(14);
				divMathArray[0] = presetDict.at(15);
				divMathArray[1] = presetDict.at(16);
				divMathArray[2] = presetDict.at(17);
				divMathArray[3] = presetDict.at(18);
				divMathArray[4] = presetDict.at(19);
				divMathArray[5] = presetDict.at(20);
				divMathArray[6] = presetDict.at(21);
				divMathArray[7] = presetDict.at(22);
				divMathArray[8] = presetDict.at(23);
				divMathArray[9] = presetDict.at(24);
				divMathArray[10] = presetDict.at(25);
				divMathArray[11] = presetDict.at(26);

				xbuf = Buffer.loadCollection(s, array);

				divmaths[0] = presetDict.at(28);
				divmaths[1] = presetDict.at(29);
				divmaths[2] = presetDict.at(30);
				divmaths[3] = presetDict.at(31);
				divmaths[4] = presetDict.at(32);
				divmaths[5] = presetDict.at(33);
				divmaths[6] = presetDict.at(34);
				divmaths[7] = presetDict.at(35);
				divmaths[8] = presetDict.at(36);
				divmaths[9] = presetDict.at(37);
				divmaths[10] = presetDict.at(38);
				divmaths[11] = presetDict.at(39);

				synth.set(\rate, cspec.map(fknob.value), \bufnum, xbuf.bufnum);
				plot.value_((array));

				("Loaded Wavetable Divider Preset:" + myPath.fileName).postln;
		})});
		// save
		presetbs[1].action_({arg i;

			"Saving Wavetable Divider preset...".postln;

			Dialog.savePanel({arg path;
				presetDict.writeArchive(path);
				myPath = PathName.new(path);
				presetText.string = "PRESET:" + myPath.fileName;

				("Saved Wavetable Divider Preset:" + myPath.fileName).postln;
		})});

		array = Array.fill(sr, {arg i;
			0});

		// Set up presets
		presetDict.add(pdivisions -> divisions.value);
		presetDict.add(parray -> array);
		presetDict.add(pfknob -> fknob.value);
		presetDict.add(pdivMenu11 -> divMenu1[0].value);
		presetDict.add(pdivMenu12 -> divMenu1[1].value);
		presetDict.add(pdivMenu13 -> divMenu1[2].value);
		presetDict.add(pdivMenu14 -> divMenu1[3].value);
		presetDict.add(pdivMenu21 -> divMenu2[0].value);
		presetDict.add(pdivMenu22 -> divMenu2[1].value);
		presetDict.add(pdivMenu23 -> divMenu2[2].value);
		presetDict.add(pdivMenu24 -> divMenu2[3].value);
		presetDict.add(pdivMenu31 -> divMenu3[0].value);
		presetDict.add(pdivMenu32 -> divMenu3[1].value);
		presetDict.add(pdivMenu33 -> divMenu3[2].value);
		presetDict.add(pdivMenu34 -> divMenu3[3].value);
		presetDict.add(pdivMath1 -> divMathArray[0].value);
		presetDict.add(pdivMath2 -> divMathArray[1].value);
		presetDict.add(pdivMath3 -> divMathArray[2].value);
		presetDict.add(pdivMath4 -> divMathArray[3].value);
		presetDict.add(pdivMath5 -> divMathArray[4].value);
		presetDict.add(pdivMath6 -> divMathArray[5].value);
		presetDict.add(pdivMath7 -> divMathArray[6].value);
		presetDict.add(pdivMath8 -> divMathArray[7].value);
		presetDict.add(pdivMath9 -> divMathArray[8].value);
		presetDict.add(pdivMath10 -> divMathArray[9].value);
		presetDict.add(pdivMath11 -> divMathArray[10].value);
		presetDict.add(pdivMath12 -> divMathArray[11].value);
		presetDict.add(pxbuf -> xbuf.value);
		presetDict.add(pdivmaths1 -> divmaths[0].value);
		presetDict.add(pdivmaths2 -> divmaths[1].value);
		presetDict.add(pdivmaths3 -> divmaths[2].value);
		presetDict.add(pdivmaths4 -> divmaths[3].value);
		presetDict.add(pdivmaths5 -> divmaths[4].value);
		presetDict.add(pdivmaths6 -> divmaths[5].value);
		presetDict.add(pdivmaths7 -> divmaths[6].value);
		presetDict.add(pdivmaths8 -> divmaths[7].value);
		presetDict.add(pdivmaths9 -> divmaths[8].value);
		presetDict.add(pdivmaths10 -> divmaths[9].value);
		presetDict.add(pdivmaths11 -> divmaths[10].value);
		presetDict.add(pdivmaths12 -> divmaths[11].value);

		// Show window:
		w.front;
		w.userCanClose_(false);
		w.onClose_({synth.free});

		"Loaded waveTable".postln;
		synth.set(\rate, cspec.map(fknob.value));
	}

	getSynth {
		^synth.value;
	}

	getArray {
		^array.value;
	}

	getXbuf {
		^xbuf.value;
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