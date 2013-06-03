// =====================================================================
// SuperCollider Workspace
// =====================================================================

s.quit;
s.boot;
s.scope;
UI.qt;

~nm = 16;
~motos = [];
~motomask = [];
(
SynthDef("mymoto", { arg out=0,lp=100,iclip=0.4,freq=0.2,delay=0.2,sadd=21,smul=10;
    var x,y ;
	var kclip = LFSaw.kr(0.1,0,iclip,iclip);
    x = RLPF.ar(LFPulse.ar(SinOsc.kr(freq, 0, smul, sadd), [0,0.1], 0.1),
        lp, 0.1).clip2(kclip);
	y = CombN.ar(x, delay, delay, -4);
    Out.ar(out, y);
}).load(s);
);
~newmotos = { ~motos.do{|x| x.free};  ~motos = {Synth.new("mymoto")}.dup(~nm); 
	~motomask = 1.dup(~nm);
};
~newmotos.();
//~motos = {Synth.new("mymoto")}.dup(~nm);
~mutemotos = { arg v=false; ~motos.do({|moto| moto.run(v)}) };
~getmotos = { ~motos.select({ arg moto, i; if(~motomask[i] > 0,{true},{false})}) };
~getmotos = { ~motos.select({ arg moto, i; ~motomask[i] > 0}) };
~getmotos.();


~motoclip = {arg iclip=0.01;~getmotos.().do({|moto| moto.set(\iclip, iclip.rand)})};
~motodelay = {arg delay=0.2; ~getmotos.().do({|moto| moto.set(\delay, delay.rand)})};
~motofreq = {arg freq=0.2; ~getmotos.().do({|moto| moto.set(\freq, freq.rand)})};
~motolp = {arg lp=60;~getmotos.().do({|moto| moto.set(\lp, lp.rand)})};
~motosadd = {arg lp=21;~getmotos.().do({|moto| moto.set(\sadd, lp.rand)})};
~motosmul = {arg lp=10;~getmotos.().do({|moto| moto.set(\smul, lp.rand)})};
~motosadd.(10);
~motosmul.(100);
//~motolp.(30);
//~motoclip.(0.00001);
//~getmotos.().do({|moto| moto.run(false)});
//~mutemotos.()
//~mutemotos.(true)
//{|x| x + 1}.([1,2,3])
//~motos;
//1/exp(1/1)



~sl2 = Slider2D();
~sl3 = Slider2D();
~sl4 = Slider2D();

~c1 = NumberBox();
~c2 = NumberBox();
~c3 = NumberBox();
~c4 = NumberBox();
~c5 = NumberBox();
~c6 = NumberBox();
~ttexts = ["lp","freq","smul","delay","clip","sadd"];
~fmap = { arg f,l; var out = Array.new(size(l)); l.do({|elm| var oelm = f.(elm); out.add(oelm)}); out};
~sts = ~fmap.({|t| var x = StaticText(); x.string = t; x},~ttexts);
~sts;

~buttons = Array.new(~nm);
~nm.do({|i|
	var b = Button();
	b.states_([
		[0, Color.black, Color.black],
		[1, Color.red, Color.red],
	]);
	b.value = 1;
	b.action_({ arg butt;
		//~motos[i].run(butt.value);
		~motomask[i] = butt.value;
	});
	b.resize_(5);
	~buttons.add(b);
});
~buttons;
~allbutton = Button();
~allbutton.states_([
		[0, Color.blue , Color.blue],
		[1, Color.green, Color.green],
]);
~allbutton.action_({ arg button;
	~buttons.do({|butt|
		butt.value = ~allbutton.value;
	});

});

w=Window().layout_( GridLayout.rows(
	[~sts[0],~sts[1],~sts[2],~allbutton],
	[~c1,~c3,~c5],
	[~sts[3],~sts[4],~sts[5]],
	[~c2,~c4,~c6],
	[~sl2,~sl3,~sl4,GridLayout.columns(~buttons)])).front;
//w = Window.new("UI", Rect(200,200,200,200));
//b = Button.new(w,Rect(10,0,80,30)).states_([["Hide"],["Show"]]);
//~c = NumberBox(w, Rect(20, 20, 150, 20));
//~sl = Slider.new(w,Rect(95,0,150,30));
//~sl2 = Slider2D.new(w,Rect(10,42,90,90));
//~sl3 = Slider2D.new(w,Rect(101,42,90,90));

//~sl.action_({
//	~c.value_(~sl.value)
//});
//~c1 = NumberBox(w, Rect(0, 0, 50, 20));
//~c2 = NumberBox(w, Rect(0, 21, 50, 20));
//~c3 = NumberBox(w, Rect(70, 0, 50, 20));
//~c4 = NumberBox(w, Rect(70, 21, 50, 20));
~c1.action_({
	~motolp.(~c1.value*400);
});
~c2.action_({
	~motodelay.(~c2.value);
});
~c3.action_({
	~motofreq.(~c3.value*200);
});
~c4.action_({
	~motoclip.(1/exp(1/~c4.value));
});
~c5.action_({
	~motosmul.(100*~c5.value);
});
~c6.action_({
	~motosadd.(30*~c6.value);
});


~sl2.action_({
	~c1.valueAction = ~sl2.x;
	~c2.valueAction = ~sl2.y;
});
~sl3.action_({
	~c3.valueAction = ~sl3.x;
	~c4.valueAction = ~sl3.y;
});
~sl4.action_({
	~c5.valueAction = ~sl4.x;
	~c6.valueAction = ~sl4.y;
});


~getmotos.();

1;
