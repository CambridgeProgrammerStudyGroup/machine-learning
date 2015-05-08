#!/usr/bin/env python
import csv
from PIL import Image
import PIL.ImageOps
import sys

reader = csv.reader(sys.stdin)
next(reader,None)
for ri, row in enumerate(reader):
  label = row[0]
  im = Image.new("L", (28,28))
  for pi, p in enumerate(row[1:]):
    x = int(pi/28)
    y = int(pi-(pi/28)*28)
    im.putpixel((x,y), int(p))
  im = PIL.ImageOps.invert(im.rotate(-90).transpose(Image.FLIP_LEFT_RIGHT))
  im.save("digits/{}.png".format(ri))
  print("saved images/{}.png of {}".format(ri, label))
