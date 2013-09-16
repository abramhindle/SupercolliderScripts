s.boot
s.scope
s.freqscope
~mbrown = { BPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(4, 60, 1), MouseY.kr(0.1, 2, 1) ) }.play;
GUI.cursorPosition.x
~lbrown = { LPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(4, 100, 1), MouseY.kr(0.1, 2, 1) ) }.play;

( 
  var syn, sound;
  syn = SynthDef.new("oscbrown", { arg freq=20, amp=0.1;
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

~ba = Buffer.read(s, "1.alphabet.wav")
~ab = Buffer.read(s, "1.lixin-alphabet.wav")


a = {PlayBuf.ar(1, ~ba)}.play
a = {[0,PlayBuf.ar(1, ~ba)]}.play

(
{
	var rate, trigger, frames;
	frames = ~ba.numFrames;
	rate = [1,1.01];
	trigger = Impulse.kr(rate);
	PlayBuf.ar(1, ~ba, 1, trigger, frames*Line.kr(0,1,10)) *
	  EnvGen.kr(Env.linen(0.01,0.96,0.01), trigger) * rate;
}.play;
)


(
~kbus1 = Bus.control;
~kbus2 = Bus.control;
{
	var speed, direction;
	speed = In.kr(~kbus1, 1) * 0.2 + 1;
	direction = In.kr(~kbus2);
	Out.ar([1,1],PlayBuf.ar(1, ~ab, (speed * direction), loop: 1))
}.play;
)
{Out.kr(~kbus1, LFNoise0.kr(66))}.play;
{Out.kr(~kbus2, LFClipNoise.kr(1/5))}.play;


~kbus3 = Bus.control;
~kbus4 = Bus.control;
{Out.kr(~kbus3, SinOsc.kr(3).range(340,540))}.play;
{Out.kr(~kbus4, LFPulse.kr(6).range(240,640))}.play;
SynthDef("Switch",{arg freq=440; Out.ar(0, SinOsc.ar(freq, 0, 0.3)) }).add;
x = Synth("Switch");
x.map(\freq, ~kbus3);
x.map(\freq, ~kbus4);
~kbus5= Bus.control;
// We can ref m!
y = {
	arg m = 0.5;
	var kb3, kb4;
	kb3 = In.kr(~kbus3);
	kb4 = In.kr(~kbus4);
	Out.kr(~kbus5, (m*kb4) + (1.0 - m)*kb3)
}.play;

x.map(\freq, ~kbus4);
x.map(\freq, ~kbus5);
y.map(\m, ~kbus4);
y.map(\m, ~kbus2);

~v = Bus.control;
y.map(\m,~v)
~v = 0.25

)

~paths = PathName.new("./1.wavs/")
~bs = ~paths.files.collect({arg path; Buffer.read(s,path.fullPath)})
~bsplay = ~bs.collect(_.play)
~bsplay.do(_.free)
{|x| x.fullPath}.(~paths.files)

~additive = {
   var f = {
    arg divisor=4;
	Mix.ar(
		Array.fill(12,
			{
				arg count;
				var harm;
				harm = (count + 1) * 110;
				SinOsc.ar(harm, mul: max([0,0], SinOsc.kr((count+1)/divisor))
				)*1/(count+1)
			})
	)*0.7
   };
   f
}

~x = Array.fill(32, {arg count; ~additive.(count) });
~xp = ~x.collect(_.play);
~xpbus= Bus.control;
~xp.do({|x| x.map(\divisor,~xpbus) })
1.to(11).do{|i| ~xp[i].free}
x = {Out.kr(~xpbus, SinOsc.kr(1).range(-12,12))}.play;
x.free;
x = {arg freq=1.0,mm=12.0; Out.kr(~xpbus, SinOsc.kr(freq).range(-1*mm,mm))}.play;
x.set(\freq,1000.1)
x.set(\mm,16.0)
x.free;
{ arg rate=60.0; Impulse.kr(rate)}.plot
x = { arg rate=1.0,mul=1.0; Out.kr(~xpbus,Impulse.kr(rate, mul: mul))}.play;
x.free;
~xpbus.set(1.00001)
{ Blip.ar(40,XLine.kr(1,200,2, doneAction: 2),0.2) }.play;

{ Blip.ar(XLine.kr(40,20,2, doneAction: 2),Line.kr(1,200,2, doneAction: 2)) }.play;

{ Out.ar([0,1],Blip.ar(200,Line.kr(1,100,20),0.2)) }.play;
{ Blip.ar(XLine.kr(20000,200,6),100,0.2) }.play;
{Line.kr(1,10,1)}.plot
{ Blip.ar(60,Impulse.kr(Line.kr(20,200,2, doneAction: 2)))}.play;

(
play {
    DynKlang.ar(`[
        [800, 1000, 1200] + SinOsc.kr([2, 3, 4.2], 0, [13, 24, 12]),
        [0.3, 0.3, 0.3],
        [pi,pi,pi]
    ]
) * 0.1
};
)

{ Klank.ar(`[[800, 1071, 1153, 1723], nil, [1, 1, 1, 1]], Impulse.ar(2, 0, 0.1)) }.play;

{ Klank.ar(`[[20,40,800, 1071, 1153, 1723], nil, [2,2,1, 1, 1, 1]], Impulse.ar(0.1, 0, 0.1)!2) }.play;

{ Klank.ar(`[[800, 1071, 1353, 1723], nil, [1, 1, 1, 1]], Dust.ar(8, 0.1)) }.play;

{ Klank.ar(`[[800, 1071, 1353, 1723], nil, [1, 1, 1, 1]], PinkNoise.ar(0.007)) }.play;

{ Klank.ar(`[[200, 671, 1153, 1723], nil, [1, 1, 1, 1]], PinkNoise.ar([0.007, 0.007])) }.play;

{ Klank.ar(`[[20,40,80], nil, [2.1, 4.01, 8.001]], PinkNoise.ar([0.007, 0.006])) }.play;

{ Klank.ar(`[[20,40,80,200,666], nil, [2.1, 4.01, 8.001,1,1]], PinkNoise.ar([0.007, 0.006])) }.play;

{ Klank.ar(`[[201,66,900], nil, [2.1, 4.01, 8.001]], PinkNoise.ar([0.007, 0.006])) }.play;


{ Klank.ar(`[[201,66,900], nil, [2.1, 4.01, 8.001]], Dust.ar([7.007, 8.006],0.1)) }.play;

{ Klank.ar(`[[20,66,88,60,900], nil, [1,1,1,5,1]], GrayNoise.ar([0.007, 0.006])) }.play;

{ Klank.ar(`[[900,1101,8000], nil, [1,1,1]], GrayNoise.ar([0.007, 0.006])) }.play;

{ Klank.ar(`[[900,1101,8000,100,90], nil, [1,1,1]], BrownNoise.ar([0.007, 0.006])) }.play;
x = {	
	var muls=[0.007,0.006],freqs, ringtimes;
	freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	DynKlank.ar(`[freqs, nil, ringtimes ], GrayNoise.ar(muls))
}.play;
~xxbus = Bus.control;
~xxbus.set(Array.rand(4,2000,8000))
x.setn(\freqs,Array.rand(4,2000,8000))
x.setn(\freqs,Array.exprand(32,20.0,80.0))

x.set(\muls,[0.03,0.009])
x.setn(\ringtimes,Array.rand(4,1.0,2.0))
x.map(\freqs,~xxbus)
~xplay = {Out.kr(~xxbus, [MouseX.kr(20,400), MouseY.kr(20,1100), MouseX.kr(40,800), MouseY.kr(100,1100)])}.play;
~xplay.free;

~xplay2 = {Out.kr(~xxbus, [Dust.kr(20,400),PinkNoise.kr(400,200),PinkNoise.kr(80,200),PinkNoise.kr(1000,200)])}.play;
~xplay2.free;
(type: \note).play

( 
SynthDef.new("oscbrown", { 
	arg freq=20, amp=0.1, dr=1,gate=1;
	  Out.ar([0,1], 
		  Linen.kr(gate, 0.1, 1, dr, 2) * BPF.ar(BrownNoise.ar(0.1!2), 
			  20 + 100*abs(SinOsc.kr(MouseY.kr(0.01,freq,0))), 1));
}).add;
)


(type: \note, instrument: \oscbrown, freq: 440, amp: 1.0, sustain: 2).play

( 
SynthDef.new("ss", { 
	arg freq=20, amp=0.1, dr=1,gate=1;
	Out.ar(0,SinOsc.ar( freq, mul: Linen.kr(gate, 0.1, 1, dr, 2)))
}).add;
)

(type: \note, instrument: \ss, freq: 440.rand, amp: 1.0, sustain: 0.5).play
p = Pbind(*[
	dur: 0.5, instrument: \ss, freq: Pseq([440,500,440,500,200,60,200,60,500]), amp: 1.0
]);
p.play
p = Pbind(*[
	dur: 0.01, instrument: \ss, freq: Pbrown(20,2000,20,4000), amp: 0.01
]);
p.play
p.stop
z = ()
z.lol = "huh"
x = z.copy
x
z.lol ="what"
x.sub = {|x,i| x.lol ++ i}
x.sub("www")

p = ()
p.pb = Pbind(*[
	dur: 0.01, instrument: \ss, freq: Pbrown(20,2000,20,4000), amp: 0.01
]);
p.pbplay = p.pb.play
p.pbplay.stop

SynthDef("MKL", { 
	arg 
	Klank.ar(`[[900,1101,8000], nil, [1,1,1]], GrayNoise.ar([0.007, 0.006])) }
).add;
	//SynthDef("Switch",{arg freq=440; Out.ar(0, SinOsc.ar(freq, 0, 0.3)) }).add;

SynthDef("DKL",{
	arg out = 0;
	//arg freqs = [800, 1071, 1153, 1723], ringtimes = [1,1,1,1], muls = [0.007,0.006];
	var freqs, ringtimes, signal, muls;
	muls = [0.007, 0.006];
	freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	signal = DynKlank.ar(`[freqs, nil, ringtimes ], GrayNoise.ar(muls));
	Out.ar(out, signal);
}).add;

SynthDef("DKL",{
	arg out = 0, freqs = [800, 1071, 1153, 1723], ringtimes = [1, 1, 1, 1], muls = [0.007, 0.007];
	//arg freqs = [800, 1071, 1153, 1723], ringtimes = [1,1,1,1], muls = [0.007,0.006];
	//var freqs, ringtimes, signal, muls;
	var signal;//, muls;
	//muls = [0.007, 0.006];
	//freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	//ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	signal = DynKlank.ar(`[freqs, nil, ringtimes ], PinkNoise.ar(muls)* [1.0,1.0]);
	Out.ar(out, signal);
}).add;



SynthDef("DKL",{
	arg out = 0;
	var freqs, ringtimes, signal, muls, amps;
	freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	muls = Control.names([\muls]).kr([0.007,0.007]);
	amps = Control.names([\amps]).kr([0.25,0.25,0.25,0.25]);
	signal = DynKlank.ar(`[freqs, amps, ringtimes ], PinkNoise.ar(muls));
	Out.ar(out, signal);
}).add;

SynthDef("DKLG",{
	arg out = 0;
	var freqs, ringtimes, signal, amps;
	freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	amps = Control.names([\amps]).kr([0.25,0.25,0.25,0.25]);
	signal = DynKlang.ar(`[freqs, amps, ringtimes ]);
	Out.ar(out, signal);
}).add;

~dklg = Synth("DKLG")
~dkl = Synth('DKL');


{ Klank.ar(`[[800, 1071, 1153, 1723], nil, [1, 1, 1, 1]], Impulse.ar(2, 0, 0.1)) }.play;
{ Klank.ar(`[[800, 1071, 1153, 1723], nil, [1, 1, 1, 1]], Impulse.ar(2, 0, 0.1)) }.play;
{ Klank.ar(`[[800, 1071, 1153, 1723], nil, [1, 1, 1, 1]], Impulse.ar(2, 0, 0.1)) }.play;
{ Klank.ar(`[[800, 1071, 1153, 1723], nil, [1, 1, 1, 1]], Impulse.ar(2, 0, 0.1)) }.play;
{ Klank.ar(`[[800, 1071, 1353, 1723], nil, [1, 1, 1, 1]], Dust.ar(8, 0.1)) }.play;
{ Klank.ar(`[[800, 1071, 1353, 1723], nil, [1, 1, 1, 1]], Dust.ar(8, 0.1)) }.play;
{ DynKlank.ar(`[[200, 671, 1153, 1723], nil, [1, 1, 1, 1]], PinkNoise.ar([0.007, 0.007])) }.play;




~dkl.setn(\freqs, [200,671,1153,1723]);
~dkl.setn(\ringtimes, [1,1,1,1]);
~dkl.setn(\muls, [0.007,0.007]);

~dkl.setn(\ringtimes, Array.rand(4, 0.2, 4) );
~dkl.setn(\muls, Array.rand(2, 0.001, 0.01) );


(
// set them from outside later:
SynthDef('help-dynKlank', {
    var freqs, ringtimes, signal;
    freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
    ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
    signal = DynKlank.ar(`[freqs, nil, ringtimes ], Impulse.ar(2, 0, 0.1));
    Out.ar(0, signal);
}).add;
)
a = Synth('help-dynKlank');
a.setn(\freqs, Array.rand(4, 500, 2000));
a.setn(\ringtimes, Array.rand(4, 0.2, 4) );

Synth.new("DKL")

SynthDef("KL", {	
	arg muls=[0.007,0.006],freqs=[800,1071,1153,1723], ringtimes=[1,1,1,1];
	//freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
	//ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
	//DynKlank.ar(`[freqs, nil, ringtimes ], GrayNoise.ar(muls))
	SinOsc.ar(freqs[0])
}).load(s);
[1,2][0]
~kl = Synth("KL");


~dkl = Synth.new("DKL");
~dkl.setn(\freqs,Array.rand(4,2000,8000));

~dkl = SynthDefAutogui(\DKL)
Quarks.gui

~envs = {
	arg dkl;
	var envV,win,n;
	n=6;
	envV = EnvelopeView();
	envV.drawLines_(true)
    .selectionColor_(Color.red)
    .drawRects_(true)
    .resize_(5)
	//    .step_(0.05)
    .action_({arg b; 
		var f,r,freqs,ringtimes;
		f = Array.fill((n-1), { arg i; b.value[0][i] });
		r = Array.fill((n-1), { 
			arg i; 
			var x1,y1,o;
			x1 = abs(b.value[0][i+1] -  b.value[0][i]);
			y1 = abs(b.value[1][i+1] -  b.value[1][i]);
			o = sqrt(x1*x1 + y1*y1);
			o
		});
		[b.index, b.value].postln;
		r.postln;
		r = (1.0 - r/(2.0.sqrt)) * 8000.0 + 20;
		//r.postln;
		dkl.setn(\freqs, r);
		dkl.setn(\ringtimes, f * 9.0 + 0.01 );
	})
	//.thumbSize_(5)
	//    .value_([[0.0, 0.1, 0.5, 1.0],[0.1,1.0,0.8,0.0]]);
    .value_([ 0.0!n , 1.0!n]);
	envV.gridOn_(true);
	envV.style_(\rects);

	//envV.setEnv(Env.new(0!16));
	win = Window();
	win.layout_(GridLayout.columns([envV]));
	win.front;
};

~envs.(~dkl);
~envs.(~dklg);

(
n=20;
w = Window.new.front;
m = MultiSliderView(w,Rect(10,10,n*13+2,100)); //default thumbWidth is 13
m.value=Array.fill(n, {|v| v*0.05}); // size is set automatically when you set the value
m.action = { arg q;
    q.value.postln;
};
)
(
n=20;
w = Window.new.front;
m = MultiSliderView(w,Rect(10,10,n*13+2,100)); //default thumbWidth is 13
m.value=Array.fill(n, {|v| v*0.05}); // size is set automatically when you set the value
m.action = { arg q;
    q.value.postln;
};
)

~sliders = {
	arg dkl;
	var n,w,m,o,p,q;
	n = 8;
	w = Window.new;
	m = MultiSliderView(w);//, Rect(0, 0, 350, 100));   
	m.value_(Array.fill(n, {0.01}));
	m.elasticMode_(1);
	m.isFilled_(true); // width in pixels of each stick
	//m.indexThumbSize_(2.0); // spacing on the value axis
	m.gap_(4);
	m.action_({arg b; 
		var r = linexp(b.value,0,1,20,2000);
		r.postln;
		dkl.setn(\freqs, r)
	});	
	o = MultiSliderView(w);
	o.elasticMode_(1);
	o.value_(Array.fill(n, {0.01}));
	o.isFilled_(true); // width in pixels of each stick
	//m.indexThumbSize_(2.0); // spacing on the value axis
	o.gap_(4);
	o.action_({arg b; 
		dkl.setn(\ringtimes, linexp(b.value,0,1,0.001,8))
	});	
	q = MultiSliderView(w);
	q.elasticMode_(1);
	q.value_(Array.fill(n, {0.01}));
	q.isFilled_(true); // width in pixels of each stick
	//m.indexThumbSize_(2.0); // spacing on the value axis
	q.gap_(4);
	q.action_({arg b; 
		dkl.setn(\amps, linexp(b.value,0,1,0.001,0.5))
	});		
	p = MultiSliderView(w);
	p.elasticMode_(1);
	p.value_([0.006,0.007]);
	p.isFilled_(true); // width in pixels of each stick
	//m.indexThumbSize_(2.0); // spacing on the value axis
	p.gap_(4);
	p.action_({arg b; 
		var r;
		//b.value.postln;
		r = linexp(b.value,0,1,0.001,0.1);
		//r.postln;
		dkl.setn(\muls, r)
	});	
	w.layout_(GridLayout.columns([m,o,q,p]));
	w.front;
};

~sliders.(~dklg)
~sliders.(~dkl)

linexp(0.5,0,1,20,1000)
~dkl.setn(\muls,[0.007,0.0001])

play({ Klang.ar(`[ Array.rand(12, 600.0, 1000.0), nil, nil ], 1, 0) * 0.05 });
{ Pulse.ar(XLine.kr(40,4000,6),0.1, 0.2) }.play;
{ Pulse.ar(XLine.kr(40,4000,6),0.1, 0.2) }.play;
{ Pulse.ar(200, Line.kr(0.01,0.99,8), 0.2) }.play;
{ Pulse.ar(200, Line.kr(0.01,0.99,8), 0.2) }.play;
// two band limited square waves thru a resonant low pass filter
{ RLPF.ar(Pulse.ar([100.rand,100.rand],0.5,0.1), XLine.kr(8000,400,5), 0.05) }.play;

