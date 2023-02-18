#!/bin/bash

id=$(sed -E 's/.*gofile.io\/d\/(\w*?)/\1/' <<<$1)
echo "Downloading $id"

server=$(curl -s https://apiv2.gofile.io/getServer?c=$id | jq -r '.data.server')
files=$(curl -s https://$server.gofile.io/getUpload?c=$id | jq '.data.files')

for key in $(jq 'keys | .[]' <<<$files); do
  file=$(jq ".[$key]" <<<$files)
  url=$(jq -r '.link' <<<$file)
  name=$(jq -r '.name' <<<$file)
  echo
  echo "Downloading $name"
  curl $url -o "$name"
done
