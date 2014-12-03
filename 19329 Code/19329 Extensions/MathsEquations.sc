/*
Mathematical Equations Class,
The core mathematics, used to construct wavetables.
Used to look up array content for particular equations.

Part of the Wavetable Modular Synthesizer Project.
(c) 2013-2014, Candidate Number 19329
Advanced Computer Music, University of Sussex, Autumn 2013.
*/

MathsEquations {

	var zero, sin360, sin30, sin45, sin60, sin90, sin120, sin135, sin150, sin180, sin210, sin225, sin240, sin270, sin300, sin315, sin330;
	var saw1, saw2, saw3;
	var square1;
	var noise1;
	var freq= 1, numharmonics=50, sr=44100;
	var mult = 0.02;

	*new {
		^super.newCopyArgs();
	}

	setMaths {

		// Zero
		zero = Array.fill(sr, {arg i;
			0;
		});

		// 1. 2pi 0°/360°
		sin360 = Array.fill(sr, {arg i;
			sin(1*2pi*(i/sr));
		});

		// 2. pi/6 30°
		sin30 = Array.fill(sr, {arg i;
			sin(1*(pi/6)*(i/sr));
		});

		// 3. pi/4 45°
		sin45 = Array.fill(sr, {arg i;
			sin(1*(pi/4)*(i/sr));
		});

		// 4. pi/3 60°
		sin60 = Array.fill(sr, {arg i;
			sin(1*(pi/3)*(i/sr));
		});

		// 5. pi/2 90°
		sin90 = Array.fill(sr, {arg i;
			sin(1*(pi/2)*(i/sr));
		});

		// 6. 2pi/3 120°
		sin120 = Array.fill(sr, {arg i;
			sin(1*(2pi/3)*(i/sr));
		});

		// 7. 3pi/4 135°
		sin135 = Array.fill(sr, {arg i;
			sin(1*(3pi/4)*(i/sr));
		});

		// 8. 5pi/6 150°
		sin150 = Array.fill(sr, {arg i;
			sin(1*(5pi/6)*(i/sr));
		});

		// 9. pi 180°
		sin180 = Array.fill(sr, {arg i;
			sin(1*pi*(i/sr));
		});

		// 10. 7pi/6 210°
		sin210 = Array.fill(sr, {arg i;
			sin(1*(7pi/6)*(i/sr));
		});

		// 11. 5pi/4 225°
		sin225 = Array.fill(sr, {arg i;
			sin(1*(5pi/4)*(i/sr));
		});

		// 12. 4pi/3 240°
		sin240 = Array.fill(sr, {arg i;
			sin(1*(4pi/3)*(i/sr));
		});

		// 13. 3pi/2 270°
		sin270 = Array.fill(sr, {arg i;
			sin(1*(3pi/2)*(i/sr));
		});

		// 14. 5pi/3 300°
		sin300 = Array.fill(sr, {arg i;
			sin(1*(5pi/3)*(i/sr));
		});

		// 15. 7pi/4 315°
		sin315 = Array.fill(sr, {arg i;
			sin(1*(7pi/4)*(i/sr));
		});

		// 16. 11pi/6 330°
		sin330 = Array.fill(sr, {arg i;
			sin(1*(11pi/6)*(i/sr));
		});

		// 17. Sawtooth Wave
		saw1 = Array.fill(sr, {arg n;
			Array.fill(numharmonics,{arg j;
				var harmonic = j+1;
				((-1)**(harmonic+1))/harmonic*sin(2pi*harmonic*(n/sr))
			}).sum*0.3;
		});

		// 18. Inverse Sawtooth Wave
		saw2 = Array.fill(sr, {arg n;
			Array.fill(numharmonics,{arg j;
				var harmonic = j+1;
				(-1**harmonic)*(sin(2pi*harmonic*freq*(n/sr)))/harmonic
			}).sum*(2pi)*0.04;
		});

		// 19. Sawtooth Wave manipulation
		saw3 = Array.fill(sr, {arg n;
			Array.fill(numharmonics,{arg j;
				var harmonic = j+1;
				sin(2pi*harmonic*freq*(n/sr))/harmonic
			}).sum*0.3;
		});

		// 20. Square wave
		square1 = Array.fill(sr, {arg n;
			Array.fill(numharmonics,{arg j;
				var harmonic = j+1;
				(sin(2pi*((2*harmonic)-1)*freq*(n/sr)))/(2*harmonic-1)
			}).sum*(4/pi)*0.3;
		});

		// 21. Noise (random floats between 0 - 0.2)
		noise1 = Array.fill(sr, {arg i;
			(0.4.rand2);
		});
	}

	getZero {
		^zero.value;
	}

	getSin360 {
		^sin360.value;
	}

	getSin30 {
		^sin30.value;
	}

	getSin45 {
		^sin45.value;
	}

	getSin60 {
		^sin60.value;
	}

	getSin90 {
		^sin90.value;
	}

	getSin120 {
		^sin120.value;
	}

	getSin135 {
		^sin135.value;
	}

	getSin150 {
		^sin150.value;
	}

	getSin180 {
		^sin180.value;
	}

	getSin210 {
		^sin210.value;
	}

	getSin225 {
		^sin225.value;
	}

	getSin240 {
		^sin240.value;
	}

	getSin270 {
		^sin270.value;
	}

	getSin300 {
		^sin300.value;
	}

	getSin315 {
		^sin315.value;
	}

	getSin330 {
		^sin330.value;
	}

	getSaw1 {
		^saw1.value;
	}

	getSaw2 {
		^saw2.value;
	}

	getSaw3 {
		^saw3.value;
	}

	getSquare1 {
		^square1.value;
	}

	getNoise1 {
		^noise1.value;
	}

	getFreq {
		^freq.value;
	}

	getHarmonics {
		^numharmonics.value;
	}

	getMult {
		^mult.value;
	}

	getSr {
		^sr.value;
	}

}
