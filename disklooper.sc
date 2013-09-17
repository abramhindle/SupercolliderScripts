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
		arg win;
		var label, done, play, loop, loopsyn, cv,gl,remove;
		cv = View(win);
		cv.minSize_(Size(200,50));
		label = TextField();
		done = Button();
		done.states=[["Stop Recording".asString, Color.black, Color.red]];
		play = Button();
		play.states=[["Play".asString, Color.black, Color.green]];
		remove = Button();
		remove.states=[["X".asString, Color.green, Color.red]];
		loop = Button();
		loop.states=[
			["Loop?".asString, Color.black, Color.green],
			["Looping".asString, Color.black, Color.red],
		];
		cv.layout_(GridLayout.columns([remove],[label],[done],[play],[loop]));
		remove.action_({arg button;
			cv.remove();
		});

		done.action_({ arg button;
			done.states=[["", Color.black, Color.black]];
			done.action_({});
			done.remove();
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
			//win.name = tf.value;
			1;
		});
	    cv
	};
	ret
};

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
//~looperw.();

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
	var wl,add,remove,win,mktxt,mklooper, initui, scv, vi, master, rec,n=1;
	//win = Window("Loop ", Rect(0,0,300,768), scroll: true).layout_(VLayout()).front;//layout_( FlowLayout()).front;		
	rec = Rect(0,0,300,768);
	//win = Window("Loop ", rec, scroll: true);//.layout_(VLayout()).front;//layout_( FlowLayout()).front;		
	//win.layout_(VLayout());
	//master = win;

	win = Window("Loop ", rec, scroll: true);
	vi = View(win);	
	vi.minSize_(Size(400,400));
	vi.layout_(VLayout());
	master = vi;

	//vi = View(win);

	//win.asView.maxSize_(Size(1024,768));
	//win = Window("Loop ", scroll: true).layout_(VLayout()).front;//layout_( FlowLayout()).front;		
	//win.maxSize_(Size(1024,768));
	//scv = ScrollView(win);//, rec);//, Rect(0, 0, 300, 300)).hasBorder_(true);
	//scv.minSize_(Size(300,768));
	//vi = View(win);//,rec);//, Rect(0, 0, 500, 500));
	//vi.minSize_(Size(300,2000));
	//vi.maxSize_(Size(1000,30000));
	//vi.minSizeHint = Size(300,200);
	//vi.layout_(VLayout());
	//vi.layout.spacing = 5;
	//vi.layout.margins = [10,10,10,10];
	//vi.layout_(FlowLayout(FlowLayout( vi.bounds, 10@10, 20@5 );));
	//vi.decorator = FlowLayout( vi.bounds,  10@10, 20@5 );
	//vi.addFlowLayout( 10@10, 20@5 );

	//scv.layout_(VLayout());
	//master = win;
	//master = win;
	//vi.resize_(5);
	//scv.resize_(5);
	//vi.resize_( 5 );
	//win.addFlowLayout( 10@10, 20@5 );
	//win.
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
		//wl.add(element);
		//ml = wl.collect( {|x| [x] });
		//win.layout.free;
		//win.layout_( GridLayout.rows( *ml ) );	
	};
	remove = {
		arg row;
		var ml;
		wl.at(row).remove;
		wl.removeAt(row);	
		ml = wl.collect( {|x| [x] });
		win.layout.free;
		win.layout_( GridLayout.rows( *ml ) );
	};
	mklooper = {
		var bounds,ui, looper = ~recordit.(dur: 30.0);		
		bounds = master.asView.bounds;
		master.asView.resizeTo(bounds.width,50*(n+1));
		n = n + 1;
		ui = looper.ui(master);
		add.(ui);
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

	//add.(mktxt.("0"));
	//add.(mktxt.("1"));
	//add.(mktxt.("2"));
	//add.(mktxt.("3"));
	//remove.(0);
	//remove.(1);
	//add.(mktxt.("4"));
	//wl
};

~newlooper.();

//~l = ~recordit.(dur: 30.0)