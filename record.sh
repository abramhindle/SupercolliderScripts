rm output.mkv
(sleep 2; jack_connect SuperCollider:out_1 ffmpeg:input_1) &
(sleep 2; jack_connect SuperCollider:out_2 ffmpeg:input_2) &
ffmpeg -f jack -ac 2 -i ffmpeg -f x11grab -r 30 -s 1920x1080 -i :0.0 -acodec pcm_s16le -vcodec libx264 -vpre faster -threads 0 -y output.mkv
