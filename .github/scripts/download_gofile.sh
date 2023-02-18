#!/bin/bash
set -f # No globbing
set -C # No clobbering

if [[ $# -ne 1 || (! "$1" =~ ^https://gofile\.io/d/[0-9a-zA-Z]+$ && ! "$1" =~ ^https://gofile\.io/\?c=[0-9a-zA-Z]+$) ]]; then
  echo 'Usage: gofile.io-dl URL' >&2
  exit 1
fi

url="$1"
if [[ "${url}" == *'?c='* ]]; then
  code="${url##*=}"
else
  code="${url##*/}"
fi

server="$(curl -s "https://apiv2.gofile.io/getServer?c=${code}" | python3 -c 'import json,sys; print(json.loads(sys.stdin.read().strip())["data"]["server"])')"
if [[ "${server}" != srv-file?? ]]; then
  echo "Unexpected server value: ${server}" >&2
  exit 1
fi

read -d '' -r size md5 name link < <(curl -s "https://${server}.gofile.io/getUpload?c=${code}" | python3 -c 'import json,sys; obj = json.loads(sys.stdin.read().strip())'$'\n''for f in obj["data"]["files"].values():'$'\n'' print(f["size"], f["md5"], f["name"], f["link"])')

if [[ "${name}" == *'/'* || "${link}" == *' '* || "${link}" != "https://${server}.gofile.io/download/"* ]]; then
  echo 'Cannot download file:' >&2
  echo "name: ${name}" >&2
  echo "link: ${link}" >&2
  echo "size: ${size}" >&2
  echo "md5: ${md5}" >&2
  exit 1
fi

if [[ -e "./${name}" ]]; then
  echo "./${name} already exists" >&2
  exit 1
fi

curl "${link}" >"./${name}"

declare -i actualSize=$(stat -c %s "./${name}")
if [[ ${actualSize} -ne ${size} ]]; then
  echo "Size mismatch: expected ${size}, got ${actualSize}" >&2
  exit 1
fi

md5sum -c <<<"${md5}  ./${name}"
