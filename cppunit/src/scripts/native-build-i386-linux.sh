#!/bin/bash

pwd

basedir=$(pwd)
mode=generic
processor=generic

rm -rf ${basedir}/build/
mkdir -p ${basedir}/build/${mode}/${processor}
(cd ${basedir}/../src/native && tar -cpz .) | (cd ${basedir}/build/${mode}/${processor} && tar -xpz)

cd ${basedir}/build/${mode}/${processor}
(which libtoolize) && libtoolize --copy --force
(which autoreconf) && autoreconf --force --install

chmod 744 config/install-sh

./configure "--prefix=${basedir}/build/${mode}/${processor}/installdir" --without-gnu-ld --disable-doxygen --disable-dot --disable-latex-docs --disable-static
export
pwd
ls
make && make install
errorcode=$?

mkdir -p ${basedir}/native/${mode}/lib ${basedir}/native/${mode}/include ${basedir}/native/${mode}/config

res=$(cp ${basedir}/build/${mode}/${processor}/installdir/lib/*.so ${basedir}/native/${mode}/lib/ 2>&1)
res=$(cp ${basedir}/build/${mode}/${processor}/installdir/lib/*.a ${basedir}/native/${mode}/lib/ 2>&1)

res=$(cp -r ${basedir}/build/${mode}/${processor}/installdir/include ${basedir}/native/${mode}/ 2>&1)

res=$(cp -r ${basedir}/../src/*.pc ${basedir}/native/${mode}/config/ 2>&1)

res=$(cp ${basedir}/build/${mode}/${processor}/COPYING ${basedir}/native/${mode}/ 2>&1)
res=$(cp ${basedir}/build/${mode}/${processor}/README ${basedir}/native/${mode}/ 2>&1)
res=$(cp ${basedir}/build/${mode}/${processor}/AUTHORS ${basedir}/native/${mode}/ 2>&1)
res=$(cp ${basedir}/build/${mode}/${processor}/THANKS ${basedir}/native/${mode}/ 2>&1)

exit ${errorcode}
