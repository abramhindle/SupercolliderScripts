s.options.numBuffers = 16000;
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
	arg output= 0, bufnum = 0, loop = 0, rate = 1.0, amp = 1.0, channels = 1;
	//var channel = 0;
	//channels.get({|x| channel =x});
	Out.ar(output,
		amp * PlayBuf.ar(1, bufnum, rate, BufRateScale.kr(bufnum), loop: loop, doneAction: 2)!2);
}).load(s);
~drecorditname = 1;








(
SynthDef(\singrain, { |freq = 440, amp = 0.2, sustain = 1|
    var sig;
    sig = SinOsc.ar(freq, 0, amp) * EnvGen.kr(Env.perc(0.01, sustain), doneAction: 2);
    Out.ar(0, sig ! 2);    // sig ! 2 is the same as [sig, sig]
}).add;
)


// tasker allows for looping and what not

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

//~t = ~tasker.( {|midi| Synth(\singrain, [freq: midi.midicps, amp: 0.2, sustain: 0.1]);},
//	notes:"aa b");
//~t.notes("aAaA|");





~drecordit = {
	arg input = 0, dur = 1.0, buff = false, name = "";
	var bnum, ret, rec=false, startTime, ttt,startrecording=true;
	if((name==""), {
		name = ~drecorditname.asString;
	});
	~drecorditname = ~drecorditname + 1;
	if ((buff==false),{ buff = Buffer.alloc(s, s.sampleRate * dur, 2) },{startrecording=false});
	bnum = buff.bufnum;
	ret = ();
	ret.bnum = bnum;
	ret.channels = buff.numChannels;
	startTime = thisThread.clock.seconds;
	if((startrecording),{ rec = Synth(\recordit, [\input, input, \bufnum, bnum]);});
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
		arg obj, looping = 0, rate = 1.0, amp=1.0;
		ret.bnum.postln;
		Synth(\pb,[\bufnum, ret.bnum, \loop, looping, \rate, rate, \amp, amp, \channels, ret.channels]) 
	};
	// got a tasker
	ret.task = ~tasker.( {|midi|
		var adiff = midi - (97 - 32), rate;
		"tasker".postln;
		// 
		rate = 1.0 + (adiff / 48.0);
		rate.postln;
		ret.syn(0, ret.rate * rate, ret.vol)
	}, notes:"   ");
	ret.rate = 1.0;
	ret.vol = 1.0;
	ret.ui = {
		arg obj, win, rupdate;
		var label, done, play, loop, loopsyn=0, cv,gl,remove,rcb, rateknobr, volknob, sequencer;
		rcb = rupdate;
		cv = View(win);
		cv.minSize_(Size(400,50));
		volknob = Knob();
		rateknobr = Knob();
		//rateknobr.minSize_(Size(20,20));
		label = TextField();
		label.string_(name);
		label.minSize_(Size(50,10));
		sequencer = TextField();
		sequencer.string_(" ");
		done = Button();
		done.states=[["Stop Recording".asString, Color.black, Color.red]];
		play = Button();
		play.states=[["Play".asString, Color.black, Color.green]];
		remove = Button();
		remove.minSize_(Size(5,5));
		remove.maxSize_(Size(20,50));
		remove.states=[["X".asString, Color.green, Color.red]];
		loop = Button();
		loop.states=[
			["Loop?".asString, Color.black, Color.green],
			["Looping".asString, Color.black, Color.red],
		];
		cv.layout_(GridLayout.columns([remove],[label],[done],[play],[loop],[rateknobr],[volknob],[sequencer]));
		volknob.value_(0.5);
		volknob.action_({
			ret.vol = 0.001 + 2 * volknob.value;
			if((loopsyn!=0),{loopsyn.set(\amp, ret.vol)});
		});
		rateknobr.value_(0.5);
		//rateknobr.orientation_(\horizontal);
		rateknobr.action_({
			ret.rate = 0.01 + 2 * rateknobr.value;
			if((loopsyn!=0),{loopsyn.set(\rate, ret.rate)});
		});
		remove.action_({arg button;
			cv.remove();
			"Calling update".postln;
			rcb.postln;
			rcb.();
			loopsyn.free;
		});		
		if((rec==false),{
			done.remove.()},{
				done.action_({ arg button;
					done.states=[["", Color.black, Color.black]];
					done.action_({});
					done.remove();
					ret.off();
				});
			});
		//if((rec==false), { done.action(done); });
		play.action_({arg button;
			ret.syn(0,  ret.rate, ret.vol);
		});
		loop.action_({arg button;
			if ((button.value == 1), 
				{
					loopsyn = ret.syn(1, ret.rate, ret.vol);
				},
				{
					loopsyn.free;
				}
			);
		});
		label.action_({arg tf;
			//win.name = tf.value;
			1;
		});
		sequencer.action_({arg tf;
			"change value".postln;
			ret.task.notes(tf.value);
			1;
		});
	    cv
	};
	ret
};


~dumpalltodisk = {
	1000.do {|i|
		{
			var fn, buff = s.cachedBufferAt(i);
			fn = ["./looper",i,".aiff"].join("");
			buff.write(fn.asString);
		}.try;
	}
};

//~dumpalltodisk.();

~newlooper = {
	arg files = [];
	var wl,update,add,remove,win,mktxt,mklooper, initui, scv, vi, master, rec,n=1;
	rec = Rect(0,0,1024,768);
	win = Window("Loop ", rec, scroll: true);
	vi = View(win);	
	vi.minSize_(Size(1024,768));
	vi.layout_(VLayout());
	master = vi;
	mktxt = {
		arg str;
		var st = StaticText(w);
		st.string = str;
		st
	};
	wl = List [];
	add = {
		arg element;
		var ml;
		master.layout.add(element,0,\topleft);
	};
	update = {
		var height, bounds;
		bounds = master.asView.bounds;
		height = (master.asView.children.size+1)*50;
		master.asView.resizeTo(bounds.width, height); //50*(n+1));		
	};
	mklooper = {
		arg file=false;
		var bounds,ui, looper, height;
		if(file==false,
			{looper = ~drecordit.(dur: 30.0)},
			{
				var buff;
				file.postln;
				"Reading the buffer!".postln;
				buff = Buffer.read(s, file);
				"Buffer read!".postln;
				looper = ~drecordit.( dur: 30.0, buff: buff, name: file.basename.splitext.[0] );
			}
		);
		//n = n + 1;
		ui = looper.ui(master, update);
		//add.(ui);
		update.();
	};
	initui = {
		var button;
		button = Button(master);
		button.states=[["Add record".asString, Color.black, Color.red]];
		button.action_({ arg button;
			mklooper.();
		});
		add.(button);
	};
	initui.();
	files.do( {|file|
		mklooper.( file);
	});
	win.front;
};

//~newlooper.();
~aiffs = "./loopers/*.aiff".pathMatch;
~newlooper.(files: ~aiffs);

~newlooper.(files: []);

~wavs = "/home/hindle1/projects/bubble-warp/wavs/1.harmonic*wav".pathMatch;
~newlooper.(files: ~wavs);

~wavs = "/home/hindle1/projects/bubble-warp/wavs/*wav".pathMatch;
~newlooper.(files: ~wavs);

~newlooper.(files: ("/home/hindle1/projects/oldburn/Harbinger/notes/sine2/*wav".pathMatch));
//11111
~newlooper.(files: ("/home/hindle1/projects/mostitch/old/*wav".pathMatch));
~newlooper.(files: ("/opt/hindle1/hdprojects/oldburn/EventGUI/melontron/palette/*wav".pathMatch));
~newlooper.(files: "/opt/hindle1/hdprojects/oldburn/old-pinion-projects/projects/line_scratch/samples2/*wav".pathMatch);
["2",1,"1"].join("")

~newlooper.(files: "/home/hindle1/Music/AudacitySaves/atr-sounds/*wav".pathMatch);

~newlooper.(files: "/home/hindle1/projects/rapid-fire-instruments/wavs/*wav".pathMatch);

