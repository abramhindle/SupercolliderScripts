s.boot();
~br = {BrownNoise.ar(1)!2};
~brps = {~br.play}.dup(4);
~brps.do( { |b|  b.free() } );
[1,2,3].map({arg a; a * a})
~x = [1,2,3].do({arg a; a * a})
{arg a; a*a}.valueArray([1,2,3])
~f = {|x| x*2};
~f.([1,2,3])
~mbrown = { BPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(4, 60, 1), MouseY.kr(0.1, 2, 1) ) }.play;
~mbrown.free
( 
  var syn, sound;

  syn = SynthDef.new("example1", { arg freq, amp;
   Out.ar([0,1], amp * SinOsc.ar(freq));
  });
 
  syn.load(s); 
)
~sound = Synth.new("example1",[\freq, 1000, \amp, 0.1]);
~sound.set(\freq, 25);
~sound.set(\freq, 33);
~sound.set(\freq, 60);
~sound.set(\freq, 440);
~sound.set(\amp, 0.5);
~sound.free;
~sss =[ 
	Synth.new("example1",[\freq, 40, \amp, 1]),
	Synth.new("example1",[\freq, 40, \amp, 1]),
	Synth.new("example1",[\freq, 40, \amp, 1]),
	Synth.new("example1",[\freq, 40, \amp, 1])
]
(x = {
     4.do({arg index;
		 ~sss[index].set(\freq, 1000.rand)
	 })
 }
);
x.()
x.play();
loop {
	x.();
	1.wait;
}
~sss.do({|s| s.free})
(
   1.wait;
   x.();
)
~del = 0.1
Tdef(\task, { 
	loop { // do forever (well, until we replace the definition..)
		x.();
		~del.wait; // wait a little
	}
});
Tdef(\task).play;
Tdef(\task).stop;
~del = 0.2
~del = 4.0
~sss[0].set(\amp, 0.1)
~ampchange = {   ~sss.do({|ss| ss.set(\amp, 0.1.rand)}) }
~ampchange.();
~adel = 1.0
Tdef(\amptask, { 
	loop { // do forever (well, until we replace the definition..)
		~ampchange.();
		~adel.rand.wait; // wait a little
	}
});
Tdef(\amptask).play;
Tdef(\amptask).stop;
~adel = 0.1
~adel = 2.0
~harmonizer = {
	var base;
	base = 20 + exprand(1,100);
	4.do({arg index;
		~sss[index].set(\freq, base + index*base)
	})
};
~harmonizer.();
Tdef(\harmtask, { 
	loop { // do forever (well, until we replace the definition..)
		~harmonizer.();
		//~del.wait();
		( (GUI.cursorPosition.x) / 960 ).wait;//rand.wait;
	}
});
Tdef(\harmtask).play;
Tdef(\harmtask).stop;
~x = MouseX.kr(1,4,1)
(~x + 0).get(0)
s.quit
w = SCWindow.new("my own scope", Rect(20, 20, 400, 500));
s.scope
4.do({|i| ~sss[i].run(false); });
4.do({|i| ~sss[i].run(true); });

a = NodeProxy.new.play;
a.fadeTime = 2; // fadeTime specifies crossfade
// set the source
a.source = { SinOsc.ar([350, 351.3], 0, 0.2) };
a.source = { Pulse.ar([350, 351.3] / 4, 0.4) * 0.2 };
a.source = Pbind(\dur, 0.03, \freq, Pbrown(0, 1, 0.1, inf).linexp(0, 1, 200, 350));
a.stop


~sss[0].stop
~sss[0].play
"JITLib extensions".include;
~mbrown[0] = { BPF.ar(BrownNoise.ar(0.1!2), MouseX.kr(40, 100, 1), MouseY.kr(0.1, 2, 1) ) }.play;
~mbrown[0].end(1)
~pink = { PinkNoise.ar(0.5) };
~pp = ~pink.play
~pp.free
~pp.free
~xp.free;
~x = { BPF.ar(SinOsc.ar(MouseX.kr(1,440)) * Impulse.ar(MouseY.kr(1,100)).dup ,60,10) };
~xp = ~x.play;
(
var syn, bub;
syn = SynthDef("mymoto", { arg out=0,lp=100,iclip=0.4,freq=0.2,delay=0.2;
    var x,y ;
	var kclip = LFSaw.kr(0.1,0,iclip,iclip);
    x = RLPF.ar(LFPulse.ar(SinOsc.kr(freq, 0, 10, 21), [0,0.1], 0.1),
        lp, 0.1).clip2(kclip);
	y = CombN.ar(x, delay, delay, -4);
    Out.ar(out, y);
});
syn.load(s);
)
var bub = SynthDef("bubbles", { arg out=0;
    var f, zout;
    f = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps;
    zout = CombN.ar(SinOsc.ar(f, 0, 0.04), 0.2, 0.2, -4); // echoing sine wave
    Out.ar(out, zout);
});
bub.load(s);
)
~moto = Synth.new("moto-rev")
~moto.free
//syn = SynthDef("moto-rev", { arg out=0,lp=100,iclip=0.4,freq=0.2,delay=0.2;
~moto.set(\lp,60);
~moto.set(\iclip,0.4);
~moto.set(\iclip,0.001);

~moto.set(\freq,0.2,\iclip, 0.4,\lp, 60, \delay, 0.2);
~moto.set(\freq,0.2,\iclip, 0.4,\lp, 60, \delay, 0.01);
~moto.set(\freq,0.2,\iclip, 0.1,\lp, 1200, \delay, 0.1);
~moto.set(\freq,99,\iclip, 0.9,\lp, 1200, \delay, 0.4);
~nm = 32;
~motos = {Synth.new("moto-rev")}.dup(~nm);
~getmotos = {arg n=4; {~motos.choose}.dup(4) }
~getmotos = {arg n=4; [~motos[30],~motos[31]]}

~getmotos.()
~motoclip = {arg iclip=0.01;~getmotos.().do({|moto| moto.set(\iclip, iclip.rand)})};
~motodelay = {arg delay=0.2; ~getmotos.().do({|moto| moto.set(\delay, delay.rand)})};
~motofreq = {arg freq=0.2; ~getmotos.().do({|moto| moto.set(\freq, freq.rand)})};
~motolp = {arg lp=60;~getmotos.().do({|moto| moto.set(\lp, lp.rand)})};

~motos.do({|moto| moto.set(\lp, 8000.rand)});
~motos.do({|moto| moto.set(\freq, 0.01.rand)});
~motos.do({|moto| moto.set(\delay, 0.001.rand)});
~motofs = [~motodelay,~motofreq,~motolp,~motoclip];
~randmotochange = { ~motofs[~motofs.size.rand].(); };
~randmotochange.();
~motoclip.(0.00001);
~motolp.(200);
~motodelay.(0.01);
~motofreq.(200);

~adel = 4.0;
Tdef(\mototask, { 
	loop { // do forever (well, until we replace the definition..)
		~randmotochange.();
		~adel.wait; // wait a little
	}
});
Tdef(\mototask).play;
Tdef(\mototask).stop;

~adeldel = 6.0;
~amotodel = 0.1;
Tdef(\dingtask, { 
	loop { // do forever (well, until we replace the definition..)
		~motodelay.(~amotodel);
		~adeldel.rand.wait; // wait a little
	}
});
Tdef(\dingtask).play;
Tdef(\dingtask).stop;
30.do{|i| ~motos[i].run(false) }

25.do({|moto| moto.set(\iclip, iclip.rand)})



{ [1,2,3,4].choose }.dup(3)

~bub = Synth.new("bubbles")
~bub.free
~moto.set(\out

~lp = { LFPulse.ar(50) * 0.1 }.play;
~lp.free;