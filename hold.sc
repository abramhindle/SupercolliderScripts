s.boot()

~mousex = MouseX.kr(1,10,1)
~mp = ({ ~mousex.poll() }).play
~last = 0.0;
({
	var mousex, mp;
	mousex = MouseX.kr(1,10,1);
	mp = mousex.play;
	mp.get({|x| ~last = x;});
}).play;


{ TrigAvg.kr(MouseX.kr(0,1000).round(100), MouseY.kr(-1, 1)).poll }.play;
{ TrigAvg.kr(SinOsc.ar(1), Impulse.kr(0.5)).poll }.play;
x = { WAmp.kr(WhiteNoise.ar(), 5).poll }.play;
x.free;
x.free;

1.0

~last
~mkmousex = {
	arg start=40,end=440,del=0;
	var last = 0.0;
	var mbus = Bus.control(s, 1);
	var mbrown = { 
		MouseX.kr(start, end, del);
	}.play(s,mbus);
	var getmousex = {
		mbus.get({|v| last = v;});
		last;
	};
	getmousex;
};
~mkmousex.();
~getmousex = ~mkmousex.();
~getmousex.();

~mkscalarfun = {
	arg f={ 0 };
	var last = 0.0;
	var mbus = Bus.control(s, 1);	
	var pf = f.play(s,mbus);
	var scalarf = {
		mbus.get({|v| last = v;});
		last;
	};
	scalarf;
};
~mousescalarf = ~mkscalarfun.({ var start=40, end=440, del=0; MouseX.kr(start, end, del); });
~mousescalarf.();
s.boot()
~mkr = MakeScalarKR(s,{ MouseX.kr(10,400,1) });
~mkr
~mkr.v()
~mkr.v().()
~mkr.f
~mkr.f.()
~mkr.a
~last = 0
~last

~mbrown.free
Platform.userExtensionDir;   // Extensions available only to your user account

s.boot
