#!/bin/bash

link=$(cat ../../dl_link)
python gofile-downloader.py "$link"
