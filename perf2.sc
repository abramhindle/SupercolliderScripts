s.boot
s.scope
~mbrown = { BPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(4, 60, 1), MouseY.kr(0.1, 2, 1) ) }.play;
GUI.cursorPosition.x
~lbrown = { LPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(4, 100, 1), MouseY.kr(0.1, 2, 1) ) }.play;

( 
  var syn, sound;
  syn = SynthDef.new("oscbrown", { arg freq, amp;
	  Out.ar([0,1], 
		  amp * BPF.ar(BrownNoise.ar(0.1!2), 
			  20 + 100*abs(SinOsc.kr(MouseY.kr(0.01,freq,0))), 1));
  }); 
  syn.load(s); 
)

~ob = Synth.new("oscbrown",[\freq, 0.1, \amp, 0.1]);
~ob.free
~ob.set(\amp, 0.5)
~ob.set(\freq, 0.01)
~ob.set(\freq, 20)
~ob.set(\freq, 40)
~ob.set(\freq, 440)

~sound = Synth.new("example1",[\freq, 1000, \amp, 0.1]);
~sound.free()