o = Server.local.options;
o.memSize=512000;
s.boot();
s.scope();
s.freqscope();
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

x.free;
x = {
	arg freq = 30, lfreq = 10, lmul = 40, ladd=20;
	var mul = LFNoise0.kr(freq: lfreq, mul: lmul, add: ladd); 
	SinOsc.ar(freq: Impulse.kr(freq: freq, mul: mul)!2)
}.play;
x.set(\freq, 1000.rand);
x.set(\lmul, 200.rand);
x.set(\lfreq, 30.rand);
x.set(\ladd, 100.rand);


SynthDef("sine2",
	{ 
		arg out=0,amp=1,f1=50,f2=51;
		Out.ar([0,1],
			SinOsc.ar([f1,f2],mul:amp)
		)
	}
).load(s)

x = {SinOsc.ar([49,51])}.play;
x.free;
~y = List.new();
~ya = { arg f1,f2;
	var syn = Synth.new("sine2",[\f1,f1,\f2,f2]);
	~y.add(syn);
	~xywindow.([syn],[\f1,1,1000],[\f2,1,1000],rand: false, name: "" + ~y.size );
};
~ya.(50,51);	
~ya.(50,51);	
~ya.(30,31);	
~xywindow.(~y,[\f1,1,60],[\f2,1,60],name: "everything y")
~y.do{|x| x.free; }

y.add(Synth.new("sine2",[\f1,50,\f2,51]))
y.add(Synth.new("sine2",[\f1,47,\f2,48]))

~xywindow.([y[1]],[\f1,1,60],[\f2,1,60]);
~xywindow.([y[2]],[\f1,1,60],[\f2,1,60]);

y = List.new();

y.add({SinOsc.ar([50,51])}.play);
y.add({SinOsc.ar([51,50])}.play);
y.add({SinOsc.ar([49,52])}.play);
y.add({SinOsc.ar([54,55])}.play);
y.add({SinOsc.ar([53,52])}.play);
y.add({SinOsc.ar([47,52])}.play);
y.add({SinOsc.ar([44,46])}.play);
y.add({SinOsc.ar([5,6])}.play);
y.add({SinOsc.ar([10,11])}.play);
y.add({SinOsc.ar([9,7])}.play);
y.do{|x| x.run(false) };
y.do{|x| x.run(true) };
y.choose.run(false)

b = {SinOsc.ar([33,36])}.play;

b = {SinOsc.ar([22,23])}.play;
b = {SinOsc.ar([26,28])}.play;


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

~freqs = Range(60,120);
~blips = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips.do({|blip| blip.set(\amp,0.05)});
~blips[0].set(\freq,440);
~blips[1].set(\freq,220);
~blips[2].set(\freq,110);
30.do{|x| ~blips[x].set(\freq,(x+1)*110); }
~blips[3].set(\freq,330);
~blips[4].set(\freq,330);
~blips[5].set(\freq,330);

~freqs = Range(1,50);
~blips4 = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips4.do({|blip| blip.set(\amp,0.05)});
~blips4.do({|blip| blip.set(\hmul,25.rand)});
~blips4.do({|blip| blip.set(\fadd,50.rand)});


~blips2 = ~freqs.collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips2.do({|b| b.map(\fadd, ~timer.bus)});
//~blips3 = [0.01].collect({|f| Synth.new("blipsaw",[\freq, f])});
~blips.do({|blip| blip.set(\freq,[60,210,440,666].choose.linrand)});

~blips.do({|blip| blip.set(\freq,[20,40,60,80].choose * 10.rand + 20)});


~blips.choose.do({|blip| blip.set(\harmfreq,[0.1,0.3,1,3].choose.rand)});
~blips.do({|blip| blip.set(\harmfreq,[0.01,0.05,0.1,0.2].choose.linrand)});

~blips.do({|blip| blip.set(\ffreq,[0.1,2,10].choose.rand)});
~blips.do({|blip| blip.set(\ffmul,[1,10,120].choose.rand)});
~blips.choose.do({|blip| blip.set(\hmul,[1,10,120].choose.rand)});

~blips2.do({|blip| blip.set(\hmul,[1,10,30].choose.rand)});
~blips2.do({|blip| blip.set(\freq,[10,44,240.0].choose.rand)});
~blips2.do(_.set(\freq,1));
~blips.do(_.set(\freq,40.rand));
~blips.do({|blip| blip.set(\freq,240.rand)});

~blips4.do(_.set(\freq,1));
~collset = {
	arg collection, symb, f;
	collection.do({|x| x.set(symb, f.())});
};

~collset.(~blips,\freq,{40.rand});
~collset.(~blips,\hmul,{40.rand});

~xywindow = {
	arg collection, arg1, arg2, rand=true, name="Panel";
	var w,f,sl2,c1,c2,filter;
	sl2 = Slider2D();
	c1 = NumberBox();
	c2 = NumberBox();
	sl2.action_({
		c1.valueAction = sl2.x;
		c2.valueAction = sl2.y;		
	});	
	filter = {|x| x };
	if ( rand, { filter = {|x| x.rand} });
	c1.action_({
		collection.do({|x| 
			x.set(arg1[0],
				(c1.value()*(arg1[2] - arg1[1]) - arg1[1]).rand
			)
		})
	});
	c2.action_({
		collection.do({|x| 
			x.set(arg1[0],
				(c2.value()*(arg2[2] - arg2[1]) - arg2[1]).rand
			)
		})
	});
	// broken here
	w=Window(name: name).layout_( GridLayout.columns([
		c1,c2,
		//GridLayout.rows([StaticText(""+arg1[0]),c1]),
		//GridLayout.rows([StaticText(""+arg2[0]),c2]),
		sl2])).front;
};
~xywindow.(~blips,[\freq,0,120],[\hmul,0,60],name:"freq+hmul");

~xywindow.(~blips,[\harmfreq,0,120],[\hmul,0,60],name:"harmfreq+hmul");
~xywindow.(~blips,[\freq,0,1200],[\ffreq,0,120],name:"freq + ffreq");


~xywindow.(~blips4,[\freq,0,1200],[\ffreq,0,120],name:"b4: freq + ffreq");
~xywindow.(~blips4,[\harmfreq,0,10],[\hmul,0,120],name:"b4: harmfreq+hmul");


~xywindow.(~blips,[\fadd,0,120],[\fmul,0,120],name:"FADD+FMUL");


~blips.do({|blip| blip.set(\amp,0.05)});

~xywindow.(y,[\freq,0,1200],[\phase,0,7]);
~blips4.size.do{|i| ~blips[i].set(\freq,(1+i)*40.1.rand)};
~blips4.do{|blip| blip.set(\amp, 0.05.rand)};
~blips.size.do{|i| ~blips[i].set(\freq,(1+i)*40.1.rand)};
~blips.do{|blip| blip.set(\amp, 0.05.rand)};
