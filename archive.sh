#!/bin/sh

tag="$1"
if [ -z "${tag}" ]; then
    echo "no tag given."
    exit 1
fi

name="SimpleMediaScanner-${tag}"

git archive --format=tar --prefix=${name}/ ${tag} | xz > ${name}.tar.xz

# vim: tabstop=4 shiftwidth=4 expandtab softtabstop=4
