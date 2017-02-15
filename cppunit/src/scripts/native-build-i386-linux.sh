#!/bin/bash

mode=generic
processor=generic

rm -rf /work/target/build/
mkdir -p /work/target/build/${mode}/${processor}
(cd /work/target/../src/native && tar -cpz .) | (cd /work/target/build/${mode}/${processor} && tar -xpz)

cd /work/target/build/${mode}/${processor}
which libtoolize && libtoolize --copy --force
which autoreconf && autoreconf --force --install

chmod 744 config/install-sh

./configure "--prefix=/work/target/build/${mode}/${processor}/installdir" --without-gnu-ld --disable-doxygen --disable-dot --disable-latex-docs --disable-static
make -j $(grep -c ^processor /proc/cpuinfo) && make install
errorcode=$?

mkdir -p /work/target/native/${mode}/lib /work/target/native/${mode}/include /work/target/native/${mode}/config

res=$(cp /work/target/build/${mode}/${processor}/installdir/lib/*.so /work/target/native/${mode}/lib/ 2>&1)
res=$(cp /work/target/build/${mode}/${processor}/installdir/lib/*.a /work/target/native/${mode}/lib/ 2>&1)

res=$(cp -r /work/target/build/${mode}/${processor}/installdir/include /work/target/native/${mode}/ 2>&1)

res=$(cp -r /work/target/../src/*.pc /work/target/native/${mode}/config/ 2>&1)

res=$(cp /work/target/build/${mode}/${processor}/COPYING /work/target/native/${mode}/ 2>&1)
res=$(cp /work/target/build/${mode}/${processor}/README /work/target/native/${mode}/ 2>&1)
res=$(cp /work/target/build/${mode}/${processor}/AUTHORS /work/target/native/${mode}/ 2>&1)
res=$(cp /work/target/build/${mode}/${processor}/THANKS /work/target/native/${mode}/ 2>&1)

exit ${errorcode}
