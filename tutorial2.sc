s.boot;
s.scope;
x = {
	arg freq=60,t_gate = 0;
	var sig, env, len, freqenv;
	len = 5;
	//env = XLine.kr(1,0.01,len,doneAction:2);
	env = EnvGen.kr(Env([0,1,0.5,0],[0.2,1,1,1],[3,-3,-6]),t_gate);
	freqenv = EnvGen.kr(Env([0,1],[3.2],[-6]),t_gate);
	sig = Pulse.ar((freq * freqenv  + 20)!2) * env;
}.play;
x.set(\t_gate, 1);

x = {
	arg freq=60;
	var sig, env, len, freqenv;
	len = 5;
	//env = XLine.kr(1,0.01,len,doneAction:2);
	env = EnvGen.kr(Env([0,1,0.5,0],[0.2,1,1,1],[3,-3,-6]),doneAction:2);
	freqenv = EnvGen.kr(Env([0,1],[3.2],[-6]));
	sig = Pulse.ar((freq * freqenv  + ExpRand(20,1000))!2) * env;
}.play;
Quarks.gui

SynthDef(\woop,{
	arg freq=60;
	var sig, env, len, freqenv;
	len = 5;
	//env = XLine.kr(1,0.01,len,doneAction:2);
	env = EnvGen.kr(Env([0,1,0.5,0],[0.2,1,1,1],[3,-3,-6]),doneAction:2);
	freqenv = EnvGen.kr(Env([0,1],[3.2],[-6]));
	sig = Pulse.ar((freq * freqenv  + ExpRand(20,1000))!2) * env;
}).add;
z = SynthDefAutogui(\woop)
SynthDefAutogui
a = SynthDef(\test, {arg out = 3, freq = 100; Out.ar(out, SinOsc.ar(freq))}) ;
z = SynthDefAutogui(\test) ;

SynthDef("blipsaw",
	{ 
		arg out=0,freq=60,fadd=0,ffreq=10,ffmul=10,ffadd=0,harmfreq=0.1,hmul=3,amp=0.2;
		Out.ar(out,
			CombC.ar(
				Blip.ar(freq+fadd+LFSaw.kr(ffreq,mul:ffmul)!2,LFSaw.kr(harmfreq,mul:hmul),amp),
				1.0,
				0.2,
				1.0
			)		
		)
	}
).load(s);

y = SynthDefAutogui(\blipsaw);
x = SynthDefAutogui(\blipsaw);


SynthDef(\noiseGrain, 
	{ arg out = 0, freq=800, sustain=0.001, amp=0.5, pan = 0; // this are the arguments of the synth function
		var window;
		window = Env.perc(0.002, sustain, amp); // exponential decay envelope
		Out.ar(out, // write to output bus
			Pan2.ar( // panning
				Ringz.ar(PinkNoise.ar(0.1), freq, 2.6), // filtered noise
				pan
			) * EnvGen.ar(window, doneAction:2) // multiplied by envelope
		)
	}
).store;

n = SynthDefAutogui(\noiseGrain)

SynthDef("mymoto", { arg out=0,lp=100,iclip=0.4,freq=0.2,delay=0.2,sadd=21,smul=10;
    var x,y ;
	var kclip = LFSaw.kr(0.1,0,iclip,iclip);
    x = RLPF.ar(LFPulse.ar(SinOsc.kr(freq, 0, smul, sadd), [0,0.1], 0.1),
        lp, 0.1).clip2(kclip);
	y = CombN.ar(x, delay, delay, -4);
    Out.ar(out, y);
}).load(s);

n = SynthDefAutogui(\mymoto)



SynthDef("bubbles", { 
	arg out=0, sawfreq=0.4, sawmul=24.0, sawfreq2=8.0, 
	    sawfreq3=7.23, sawmul2=3, sawadd2=80, sinemul=0.04,
	    decaytime=0.2, combmul = -4, amp=0.3;
    var f, zout;
    f = LFSaw.kr(sawfreq, 0, sawmul, LFSaw.kr([sawfreq2,sawfreq3], 0, sawmul2, sawadd2)).midicps;
    zout = CombN.ar(SinOsc.ar(f, 0, sinemul), 1.0, decaytime, combmul); // echoing sine wave
    Out.ar(out, (zout *amp)!2);
}).load(s);

SynthDefAutogui(\bubbles)
