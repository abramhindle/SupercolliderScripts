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
	arg output= 0, bufnum = 0, loop = 0, rate = 1.0, amp = 1.0;
	Out.ar(output,
		amp * PlayBuf.ar(2, bufnum, rate, BufRateScale.kr(bufnum), loop: loop, doneAction: 2));
}).load(s);
~drecorditname = 1;

~drecordit = {
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
		arg obj, looping = 0, rate = 1.0, amp=1.0;
		ret.bnum.postln;
		Synth(\pb,[\bufnum, ret.bnum, \loop, looping, \rate, rate, \amp, amp]) 
	};
	ret.rate = 1.0;
	ret.vol = 1.0;
	ret.ui = {
		arg obj, win, rupdate;
		var label, done, play, loop, loopsyn=0, cv,gl,remove,rcb, rateknobr, volknob;
		rcb = rupdate;
		cv = View(win);
		cv.minSize_(Size(200,50));
		volknob = Knob();
		rateknobr = Knob();
		//rateknobr.minSize_(Size(20,20));
		label = TextField();
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
		cv.layout_(GridLayout.columns([remove],[label],[done],[play],[loop],[rateknobr],[volknob]));
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
		done.action_({ arg button;
			done.states=[["", Color.black, Color.black]];
			done.action_({});
			done.remove();
			ret.off();
		});
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
	    cv
	};
	ret
};


~dumpalltodisk = {
	1000.do {|i|
		{
			var fn, buff = s.cachedBufferAt(i);
			fn = "./looper"+i+".aiff";
			buff.write(fn.asString);
		}.try;
	}
};



~newlooper = {
	var wl,update,add,remove,win,mktxt,mklooper, initui, scv, vi, master, rec,n=1;
	rec = Rect(0,0,400,768);
	win = Window("Loop ", rec, scroll: true);
	vi = View(win);	
	vi.minSize_(Size(400,400));
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
		"Inside update".postln;
		bounds = master.asView.bounds;
		height = (master.asView.children.size+1)*50;
		height.postln;
		"update".postln;
		master.asView.resizeTo(bounds.width, height); //50*(n+1));		
	};
	mklooper = {
		var bounds,ui, looper = ~drecordit.(dur: 30.0), height;		
		n = n + 1;
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
	win.front;
};

~newlooper.();
