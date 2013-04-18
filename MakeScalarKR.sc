
MakeScalarKR {
	var last;
	var mbus;
	var pf;
	var f;
	*new { arg sbase,f;
		^super.new.init(sbase,f)
	}
	init { 
		arg sbase,myf;
		f = myf;
		last = 0.0;
		mbus = Bus.control(sbase, 1);	
		pf = f.play(sbase, mbus);
	}
	v {
		mbus.get({|x| last=x;});
		^last
	}
	free {
		pf.free
	}
}
