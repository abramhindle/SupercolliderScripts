s.boot;
s.scope;
s.freqscope;

SynthDef.new(\recordit,{
	arg input = 0, bufnum = 0;
	var in;
	in = SoundIn.ar( input ) ! 2;
	RecordBuf.ar( in, bufnum, doneAction: 2, loop: 0);
}).load(s);
SynthDef.new(\pb,{
	arg output= 0, bufnum = 0, loop = 0, rate = 1.0;
	Out.ar(output,
		PlayBuf.ar(2, bufnum, rate, BufRateScale.kr(bufnum), loop: loop, doneAction: 2));
}).load(s);
~recorditname = 1;
~recordit = {
	arg input = 0, dur = 1.0;
	var buff, bnum, ret, rec, startTime, name,ttt;
	name = ~recorditname.asString;
	~recorditname = ~recorditname + 1;
	buff = Buffer.alloc(s, s.sampleRate * dur, 2);
	bnum = buff.bufnum;
	ret = ();
	ret.bnum = bnum;
	startTime = thisThread.clock.seconds;
	rec = Synth(\recordit, [\input, input, \bufnum, bnum]);	
	ret.rec = rec;
	ret.off = {
		var ndur,newbuff, oldb,  nbnum, size;
		ndur = thisThread.clock.seconds - startTime;
		size = (s.sampleRate * ndur).asInt;
		rec.free;
		newbuff = Buffer.alloc(s, size, 2);
		newbuff.postln;
		nbnum = newbuff.bufnum;
		buff.copyData(newbuff, 0, 0, size);
		buff.free;
		buff = newbuff;
		bnum = buff.bufnum;
		ret.bnum = bnum;
		buff
	};
	ret.ttt = { // |oops,looping|
		arg oops, looping = 1;
		looping.postln;
	};
	ret.syn = { 
		arg obj, looping = 0, rate = 1.0;
		ret.bnum.postln;
		Synth(\pb,[\bufnum, ret.bnum, \loop, looping, \rate, rate]) 
	};
	ret.ui = {
		var win, label, done, play, loop, loopsyn;
		label = TextField();
		done = Button();
		done.states=[["Stop Recording".asString, Color.black, Color.red]];
		play = Button();
		play.states=[["Play".asString, Color.black, Color.green]];
		loop = Button();
		loop.states=[
			["Loop?".asString, Color.black, Color.green],
			["Looping".asString, Color.black, Color.red],
		];
		win=Window("Loop " + name).layout_( GridLayout.rows([label],[done],[play],[loop]) ).front;		
		done.action_({ arg button;
			done.states=[["", Color.black, Color.black]];
			done.action_({});
			ret.off();
		});
		play.action_({arg button;
			ret.syn();
		});
		loop.action_({arg button;
			if ((button.value == 1), 
				{
					loopsyn = ret.syn(1);
				},
				{
					loopsyn.free;
				}
			);
		});
		label.action_({arg tf;
			win.name = tf.value;
		});
	};
	ret
};
// ~ret = ~recordit.(dur: 30.0);
// ~ret.ui;
// ~ret.ttt(6);
// x = ~ret.ttt; x.(1);
// //5.wait;
// //~ret.off;
// //~ret.syn
// ~ret.syn(looping: 1)

// ~ret.ttt

~looperw = {
	var win, spawner;
	spawner = Button();
	spawner.states=[["Spawn".asString, Color.black, Color.red]];
	win=Window("Looper").layout_( GridLayout.rows([spawner]) ).front;		
	spawner.action_({ arg button;
		var looper;
		looper = ~recordit.(dur: 30.0);
		looper.ui;
	});
};
~looperw.();
// (
// w = Window.new("The Four Noble Truths");
// 
// b = Button(w, Rect(20, 20, 340, 30))
//         .states_([
//             ["there is suffering", Color.black, Color.red],
//             ["the origin of suffering", Color.white, Color.black],
//             ["the cessation of suffering", Color.red, Color.white],
//             ["there is a path to cessation of suffering", Color.blue, Color.clear]
//         ])
//         .action_({ arg butt;
//             butt.value.postln;
//         });
// w.front;
// )
// Synth(\pb,[\bufnum, 86, \loop, 1, \rate, 1]) 


~dumpalltodisk = {
	1000.do {|i|
		{
			var fn, buff = s.cachedBufferAt(i);
			fn = "./looper"+i+".aiff";
			buff.write(fn.asString);
		}.try;
	}
};
	
//b = Buffer.alloc(s, s.sampleRate, 2);