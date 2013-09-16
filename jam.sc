

SynthDef("bee",	{
	arg out = 0;
	Out.ar(out,
		RLPF.ar(
			Blip.ar()))
}).load(s)

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

x=Synth(\additive)

~x = Mix.ar(Array.fill(32, {|d| Synth(\additive,[divisor: d])}));
