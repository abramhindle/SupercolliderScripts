s.boot;
s.scope;
s.freqscope;

SynthDef("DKL",{
   arg out = 0, scale = 0.1;
   var freqs, ringtimes, signal, muls, amps;
   freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
   ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
   muls = Control.names([\muls]).kr([0.007,0.007]);
   amps = Control.names([\amps]).kr([0.25,0.25,0.25,0.25]);
   signal = scale * DynKlank.ar(`[freqs, amps, ringtimes ], PinkNoise.ar(muls))!2;
   Out.ar(out, signal);
}).add;

SynthDef("DKLG",{
   arg out = 0, scale = 0.1;
   var freqs, ringtimes, signal, amps;
   freqs = Control.names([\freqs]).kr([800, 1071, 1153, 1723]);
   ringtimes = Control.names([\ringtimes]).kr([1, 1, 1, 1]);
   amps = Control.names([\amps]).kr([0.25,0.25,0.25,0.25]);
   signal = scale * DynKlang.ar(`[freqs, amps, ringtimes ])!2;
   Out.ar(out, signal);
}).add;

~dklg = Synth("DKLG");
~dklg.autogui;
//~dklg.stop
~dkl = Synth('DKL');
~dkl.autogui;
//~dkl = SynthDefAutogui(\DKL);
//~dklg = SynthDefAutogui(\DKLG);

~envs = {
   arg dkl;
   var envV,win,n;
   n=6;
   envV = EnvelopeView();
   envV.drawLines_(true)
    .selectionColor_(Color.red)
    .step_(0)
    .drawRects_(true)
    .resize_(5)
    .action_({arg b; 
      var f,r,freqs,ringtimes;
      f = Array.fill((n-1), { arg i; b.value[0][i] });
      r = Array.fill((n-1), { 
      	arg i; 
      	var x1,y1,o;
      	x1 = abs(b.value[0][i+1] -  b.value[0][i]);
      	y1 = abs(b.value[1][i+1] -  b.value[1][i]);
      	o = sqrt(x1*x1 + y1*y1);
      	o
      });
      [b.index, b.value].postln;
      r.postln;
      r = (r/(2.0.sqrt)) * 8000.0 + 20;
      dkl.setn(\freqs, r);
      dkl.setn(\ringtimes, f * 9.0 + 0.01 );
   })
    .value_([ 0.0!n , 1.0!n]);
	//envV.gridOn_(true);
   envV.style_(\rects);

   //envV.setEnv(Env.new(0!16));
   win = Window();
   win.layout_(GridLayout.columns([envV]));
   win.front;
};

~envs.(~dkl);
~envs.(~dklg);



~sliders = {
   arg dkl;
   var n,w,m,o,p,q;
   n = 8;
   w = Window.new;
   m = MultiSliderView(w);//, Rect(0, 0, 350, 100));   
   m.value_(Array.fill(n, {0.001}));
   m.elasticMode_(1);
   m.isFilled_(true); // width in pixels of each stick
   //m.indexThumbSize_(2.0); // spacing on the value axis
   m.gap_(4);
   m.action_({arg b; 
      var r = linexp(b.value,0,1,20,2000);
      r.postln;
      dkl.setn(\freqs, r)
   });   
   o = MultiSliderView(w);
   o.elasticMode_(1);
   o.value_(Array.fill(n, {0.01}));
   o.isFilled_(true); // width in pixels of each stick
   //m.indexThumbSize_(2.0); // spacing on the value axis
   o.gap_(4);
   o.action_({arg b; 
      dkl.setn(\ringtimes, linexp(b.value,0,1,0.001,8))
   });   
   q = MultiSliderView(w);
   q.elasticMode_(1);
   q.value_(Array.fill(n, {0.01}));
   q.isFilled_(true); // width in pixels of each stick
   //m.indexThumbSize_(2.0); // spacing on the value axis
   q.gap_(4);
   q.action_({arg b; 
      dkl.setn(\amps, linexp(b.value,0,1,0.001,0.5))
   });   	
   p = MultiSliderView(w);
   p.elasticMode_(1);
   p.value_([0.006,0.007]);
   p.isFilled_(true); // width in pixels of each stick
   //m.indexThumbSize_(2.0); // spacing on the value axis
   p.gap_(4);
   p.action_({arg b; 
      var r;
      //b.value.postln;
      r = linexp(b.value,0,1,0.001,0.1);
      //r.postln;
      dkl.setn(\muls, r)
   });   
   w.layout_(GridLayout.columns([m,o,q,p]));
   w.front;
};

~sliders.(~dklg);
~sliders.(~dkl);
