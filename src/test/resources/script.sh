ffmpeg -y -i bow.mp4 -vf "
drawtext=
  text=This is lengthy text and  more text:
  fontcolor=white:
  fontsize=20:
  x=(w/2)-(tw/2):
  y=h-th-10:
  borderw=3:
  bordercolor=black:
  fontfile=impact.ttf
  " output.mp4 && ffplay output.mp4
