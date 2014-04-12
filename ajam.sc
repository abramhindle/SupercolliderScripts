// =====================================================================
// SuperCollider Workspace
// =====================================================================
(
SynthDef(\singrain, { |freq = 440, amp = 0.2, sustain = 1|
    var sig;
    sig = SinOsc.ar(freq, 0, amp) * EnvGen.kr(Env.perc(0.01, sustain), doneAction: 2);
    Out.ar(0, sig ! 2);    // sig ! 2 is the same as [sig, sig]
}).add;
)

~s = "XYZ XYZ XYZ XYZ xyz";
~waits = ( " ".ascii[0] : 1, ".".ascii[0] : 2, 
	",".ascii[0] : 4, "/".ascii[0] : 8 ,
	"\\".ascii[0] : 16, "|".ascii[0] : 32 );

~tasker = { arg trigger, notes="aaa", basewait = 0.125;
	var ret = ();
	ret.nnotes = notes;
	ret.notes = {
		arg obj, newnotes;
		ret.nnotes = newnotes;
		ret.nnotes.postln;
	};
	ret.notes(notes);
	ret.t = Task({
		loop {
			ret.nnotes.ascii.do({ |midi|
				var orig = midi;
				midi = midi - 32;
				if (~waits[orig] != nil, {
					(~waits[orig]*basewait).wait;
				}, {
					if(midi > 0, {
						trigger.(midi);
						//Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.1]);
					});
					basewait.wait;
				});
			});
		}
	}).play;
	ret.end = {|self| ret.t.stop(); };
	ret.start = {|self| ret.t.play(); };
	ret
};
~t = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.1]);},
	notes:"aa b");
~t.notes("aAaA|i want to fuck\\");

~t.notes("aAaA|i want to fuck\\");

~tl = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.3]);},
	notes:"aa b");
~tl.notes("i want to eat a big sandwich I don't know how big though");

~ts = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.05]);},
	notes:"aa b");
~ts.notes("i want to eat a big sandwich I don't know how big though");

~ts2 = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.01]);},
	notes:"aa b");
~ts2.notes("i want to eat a big sandwich I don't know how big though");

~ts4 = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.001]);},
	notes:"aa b");
~ts4.notes("i want to eat a big sandwich I don't know how big though");

~tsk = ();

10.do({|v|

	~tsk[v] = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.1, sustain: 0.1/v]);},
		notes:"aa b");
	~tsk[v].notes("I wAnt To Eat A Big Sandwich I Don't Know How BIG THOUGH");

});

10.do({|v|
	~tsk[v].notes("A A B A cccz")
})

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




SynthDef("additive",
	{
		arg divisor=4,out=0;
		Out.ar(out,
			Mix.ar(
				Array.fill(12,
					{
						arg count;
						var harm;
						harm = (count + 1) * 110;
						//SinOsc.ar(harm, mul: SinOsc.ar(harm)/(count+1.0)
					    SinOsc.ar(harm, mul: max([0,0], SinOsc.kr((count+1)/divisor)))
					})				

			)*0.7
		)
	}).load(s);

SynthDef("bee",	{
	arg out = 0;
	Out.ar(out,
		RLPF.ar(
			Blip.ar()))
}).load(s)

SynthDef("blipsaw",
	{ 
		arg out=0,freq=60,fadd=0,ffreq=10,ffmul=10,ffadd=0,harmfreq=0.1,hmul=3,amp=0.2,sustain=0.1;
		Out.ar(out,
			CombC.ar(
				Blip.ar(freq+fadd+LFSaw.kr(ffreq,mul:ffmul)!2,LFSaw.kr(harmfreq,mul:hmul),amp),
				1.0,
				0.2,
				1.0
			)		
			* EnvGen.kr(Env.perc(0.01, sustain), doneAction: 2)
		)
	}
).load(s);


~tsk2 = ();

10.do({|v|

	~tsk2[v] = ~tasker.( {|midi| Synth(\noiseGrain, [amp: 0.1, sustain: 0.2/v]);},
		notes:"aa b");
	~tsk2[v].notes("I wAnt To Eat A Big Sandwich I Don't Know How BIG THOUGH");

});

10.do({|v|
	~tsk[v].notes("A A B A cccz");
	~tsk2[v].notes("A A B A cccz")
})



~tks2.do({|v| v.stop() })



~t
(
t = Task({
	var basewait = 0.125;
    loop {
        ~s.ascii.do({ |midi|
			var orig = midi;
			midi = midi - 32;
			if (~waits[orig] != nil, {
				(~waits[orig]*basewait).wait;
			}, {
				if(midi > 0, {
					Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.1]);
				});
				basewait.wait;
			});
        });
    }
}).play;
)
t.stop;
"xyz".do({|x| x.postln()})
"6".ascii
"x y z".ascii
"abcABC0123".ascii
"-".ascii

~s = "AAA BBB fsd fusdiiwshfiusdh uihdhsdh fs"
~s = "i want to eat some sandwiches"
~s = "I WANT TO EAT SOME SANDWICHES!~"
~s = " Voicemail for ad-guys, \"creatives\", designers, paradigm-shifters, art school students "
~s = "+,..a/,..,/a,.,.,.a,.,/.,/.a/,/.,a/.,/."
~s = "a a,a.a/a\\a|a"
~s = "012345678:<><>==?"
"x".ascii[0]
x = ()
x["a"] = "abc"
x["b"]
t.set