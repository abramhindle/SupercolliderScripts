// =====================================================================
// SuperCollider Workspace
// =====================================================================
// A bunch of crap I stole or made from tutorials
s.boot();

(
    x = { 
    	(
    		{
    			RHPF.ar(
    				OnePole.ar(BrownNoise.ar, 0.99), 
    				LPF.ar(BrownNoise.ar, 14) * 200 + 500, 0.03, 0.003)
    		}!2
		) 
    	+ 
    	(
    		{
    			RHPF.ar(
    				OnePole.ar(BrownNoise.ar, 0.99), 
    				LPF.ar(BrownNoise.ar, 20) * 400 + 1000, 0.03, 0.005)
    		}!2
		) 
    	* 4 
    }
);
( v = {
          (
			  x.play;
			  x.play;
			  x.play;
		  )
  }
);
v.play;
v.free;

~base = 440;
( // trigger an envelope
{
	var trig;
	var rmod;
	trig = SinOsc.ar(1) > 0.1;
	rmod = SinOsc.ar(20,0,1) > 0.1;
	Out.ar(0,
		EnvGen.kr(Env.perc, trig, doneAction: 0)
			* SinOsc.ar(~base * rmod,0,0.1)
	)
}.play(s);)
~base=2000;

(
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
);

Tdef(\geoff).play;


Tdef(\geoff, { 
	loop { // do forever (well, until we replace the definition..)
		Synth(\noiseGrain, [\freq, exprand(300, 5000)]); // start random grain on the server
		0.1.wait; // wait a little
	}
});

SynthDef(\noiseGrain).freq=440;

( 
  var syn, sound;

  syn = SynthDef.new("example1", { arg freq;
   Out.ar([0,1], SinOsc.ar(freq));
  });
 
  syn.load(s); 
)
~sound = Synth.new("example1",[\freq, 40]);
~sound.set(\freq, 25);
~sss =[ 
	Synth.new("example1",[\freq, 40]),
	Synth.new("example1",[\freq, 40]),
	Synth.new("example1",[\freq, 40]),
	Synth.new("example1",[\freq, 40])
]
(x = {
     4.do({arg index;
		 ~sss[index].set(\freq, exprand(1,1000))
	 })
 }
)
x.play

( ).play;
Pbind.new.play
Pbind(\freq, 660, \pan, -1).play
(freq: 660, pan: -1).play

// event generator on the default instrument
//Prout is a generator routine!
( 
  Pbind(
   \dur,  0.4,
   \note,  Prout({ // crazy little process that will eventually stop
      var bangs;
      
      bangs = 24;
      // do full loop twice
     
      bangs.do({ arg index;
	   index.yield;
      });
    })
  ).play;

  // code stolen from http://www.perl.com/lpt/a/2004/08/31/livecode.html
 )
 (
  Pbind(
   \dur, 0.4,
   \note, Pfunc({
   
      12.rand
     })
  ).play
 )
// ({}) is a functional

 (
  Ptpar([0, 
   Pbind(\dur, 0.8,
    \octave, 4,
    \degree, [1, 3, 5],
    \amp, 0.2),
   0, Pbind(\dur, 0.01,
    \octave, 5,
    \degree, Pseq([4, 4, 5, 5, 6, 5, 5, 4, 4, 6, 6, 5, 5, 4, 5, 5, 6, 6], inf),
    \amp, 0.3)
   ]).play
 )




   
   (
   c = Conductor.make({arg thisConductor, arrSize, vol, freq, dur;
    
    vol .spec_(\db);
    freq.spec_(\freq);
   arrSize.sp(10, 1, 50, 1); // sp( val, min, max, step, warp)
    dur.sp(0.45, 0.1, 1, 0);
     
    thisConductor.pattern_(Pbind (
     \db,  vol, 
     \dur, dur,
     \freq, Prout ({
     
         var arr, diff;
        
       arr = [];
       
        
       {arr.size < arrSize.value}. while({
        
        arr = arr.add(freq.value);
          arr.do({ arg item, index;
        
         item.yield;
      });
     });
    })
   ));
  });
  
  c.show;
  )

Quarks.gui

// Schedule 32 play events!
(
   32.do({ arg index;
	   SystemClock.sched(index*0.1,{().play});
   })
)
// Change a process

