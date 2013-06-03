o = Server.local.options;
o.memSize=512000;
s.boot();
s.scope();

~len = 240;
Task {~len.sleep; 
	[41,440,1440].do({|freq| {CombL.ar(LFPulse.ar([freq*1.1,freq*0.9] + SinOsc.kr([0.5.rand,0.4.rand])))}.play});
	{SinOsc.ar(LFSaw.kr(0.1,mul:[59,61]))}.play;
	{SinOsc.ar([59,61])}.play;
	{SinOsc.ar([440,450])}.play;
	{SinOsc.ar([1440,1460])}.play;
	{SinOsc.ar([440,480])}.play;
	{SinOsc.ar([3440,2460])}.play;
}.play;	

~maketimer = {
	arg len=~len;
	var timer;
	timer = ();
	timer.bus = Bus.control;
	timer.line = {Out.kr(timer.bus, Line.kr(0,1,len))}.play;
	timer;
};

~timer = ~maketimer.();
~timer.bus.get;

{SinOsc.ar(40 - 20*In.kr(~timer.bus)!2,mul:0.4)}.play;


// ~low = ();
// ~low['f'] = { arg freq=40; SinOsc.ar(freq, mul: In.kr(~time.bus) ) };
// ~low['pl'] = { ~low['p'] = ~low['f'].play; };
// ~low['pl'].();
// ~low['p'].set(\freq, 100);
// ~low['p'].set(\freq, 45);

// ~makedust = {
// 	x = ();
// 	x['f'] = { arg freq=1,delay=0.01,decay=1; CombC.ar(Dust.ar(freq), 1.0, delaytime: delay, decaytime: decay) };
// 	x['pl'] = { x['p'] = x['f'].play };
// 	x
// };
// ~d = [];
// ~d.add( ~makedust.());
// ~d[0].pl;
// ~d[0]['p'].set(\freq, 20);
// ~d[0]['p'].map(\delay, 0.01);
// ~d[0]['p'].set(\decay, -0.1);
// ~time.bus.get()
// ~d[0]['bus'] = Bus.control;
// ~d[0]['p'].map(\delay, ~d[0]['bus'])
// ~d[0]['bp'] = { arg decay=0.01; Out.kr(~d[0]['bus'], In.kr(~time.bus) * decay) }.play;
// ~d[0]['bp'].set(\decay, 0.01);
// ~time.bus.get
// 	
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
// Synth.new("blipsaw",[\freq, 20]);

~freqs = Range(50,120);
~blips = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips.do({|blip| blip.set(\amp,0.05)});
~freqs = Range(1,50);
~blips4 = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips4.do({|blip| blip.set(\amp,0.05)});
~blips4.do({|blip| blip.set(\hmul,25.rand)});
~blips4.do({|blip| blip.set(\fadd,50.rand)});


~blips2 = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips2.do({|b| b.map(\fadd, ~timer.bus)});
//~blips3 = [0.01].collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips.do({|blip| blip.set(\freq,[60,210,440,666].choose.rand)});

~blips.choose.do({|blip| blip.set(\harmfreq,[0.1,0.3,1,3].choose.rand)});
~blips.do({|blip| blip.set(\harmfreq,[0.01,0.05,0.1,0.2].choose.rand)});

~blips.do({|blip| blip.set(\ffreq,[0.1,2,10].choose.rand)});
~blips.do({|blip| blip.set(\ffmul,[1,10,120].choose.rand)});
~blips.choose.do({|blip| blip.set(\hmul,[1,10,120].choose.rand)});

~blips2.do({|blip| blip.set(\hmul,[1,10,30].choose.rand)});
~blips2.do({|blip| blip.set(\freq,[10,44,240.0].choose.rand)});
~blips2.do(_.set(\freq,1));
~blips.do(_.set(\freq,1));
~blips4.do(_.set(\freq,1));
~collset = {
	arg collection, symb, f;
	collection.do({|x| x.set(symb, f.())});
};

~collset.(~blips,\freq,{40.rand});
~collset.(~blips,\hmul,{40.rand});