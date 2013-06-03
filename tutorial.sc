Server.default = s = Server.internal.boot
play({SinOsc.ar(LFNoise0.kr(12, mul: 600, add: 1000), 0.3)})
play({RLPF.ar(Dust.ar([12,15]), LFNoise1.ar(1/[3,4], 1500, 1600), 0.02)})
play({RLPF.ar(Dust.ar([1,100]), LFNoise1.ar(1/[3,4], 1500, 1600), 0.02)})
play({RLPF.ar(Dust.ar([50,50]), LFNoise0.ar(1/[5,10], 1500, 1600), 0.02)})
play({
	var sines = 100, speed = 10;
	Mix.fill(sines,
		{arg x;
			Pan2.ar(
				SinOsc.ar(exprand(100,10000),
					mul: max(0,
						LFNoise1.kr(speed) + 
						Line.kr(1,-1,30)
					)
				), 
				rand2(1.0)
			)
		}
	)
})

play({
	var sines = 256, speed = 0.1, time = 60;
	Mix.fill(sines,
		{arg x;
			Pan2.ar(
				SinOsc.ar(exprand(20,440),
					mul: max(0,
						LFNoise1.kr(speed) *
						Line.kr(1,0,time)
					)
				), 
				rand2(1.0)
			)
		}
	)
})

				
dup("echo",20)
25.midicps
{|x| log(x.midicps)}.dup(128).plot

{Blip.ar(100,LFNoise2.kr([2,3], 400, 1), 0.3)}.play

{PMOsc.ar(440,MouseY.kr(1,550),MouseX.kr(1,15))}.play

({
Blip.ar(
	TRand.kr( //freq
		100, 1000,
		Impulse.kr(Line.kr(1,20,60))),
	TRand.kr( //harm
		1, 10,
		Impulse.kr(Line.kr(1,20,60))),
	Linen.kr(
		Impulse.kr(Line.kr(1,20,60)),
		0,
		0.5,
		1/Line.kr(1,20,60)) // trigger
)
}.play
)
{Impulse.kr(Line.kr(1,20,60))}.plot

Env.perc(0.001, 1, 1, -8).test.plot;


x = {CombC.ar(Impulse.ar(Line.kr(1,20,60)),maxdelaytime:1.0,delaytime:1.0,decaytime:1.0)}.play

{ CombC.ar(Decay.ar(Dust.ar(1,0.5), 0.2, WhiteNoise.ar), 0.2, 0.2, 3) }.play;
{ CombN.ar(WhiteNoise.ar(0.01), 0.01, XLine.kr(0.0001, 0.01, 20), 0.2) }.play;
{ CombN.ar(WhiteNoise.ar(0.01), 0.01, XLine.kr(0.0001, 0.01, 20), -0.2) }.play;
{ CombN.ar(BrownNoise.ar(0.01),0.01, 0.01,0.2) }.play;

{RLPF.ar(in:Dust.ar(100,0.5), freq: 440, rq: 1, mul: 1, add: 0)}.play;
