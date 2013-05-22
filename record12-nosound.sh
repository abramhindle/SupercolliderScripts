rm output.mkv
ffmpeg  -f x11grab -r 30 -s 960x1080 -i :0.0 -vcodec libx264 -vpre faster -threads 0 -y output.mkv
