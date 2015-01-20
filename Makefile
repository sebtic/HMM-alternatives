all: clean i686-linux i686-mingw32 x86_64-mingw32 x86_64-linux

prepare:
	USE="-fortran -openmp" crossdev -S x86_64-pc-mingw32
	USE="-fortran -openmp" crossdev -S i686-pc-mingw32 
	USE="-fortran -openmp" crossdev -S i686-pc-linux-gnu 
	USE="-fortran -openmp" crossdev -S x86_64-pc-linux-gnu

clean:
	find . -iname "target" -exec rm -rf {} \; || true

i686-linux:
	find . -iname "target" -exec rm -rf {} \; || true
	CROSSDEV=1 mvn -P\!linux-amd64,linux-i386,\!linux-amd64-support,linux-i386-support,linux-i386 clean || true
	CROSSDEV=1 mvn -P\!linux-amd64,linux-i386,\!linux-amd64-support,linux-i386-support,linux-i386 install deploy

i686-mingw32:
	find . -iname "target" -exec rm -rf {} \; || true
	CROSSDEV=1 mvn -P\!linux-amd64,windows-i386,\!linux-amd64-support,windows-i386-support,windows-i386 clean || true
	CROSSDEV=1 mvn -P\!linux-amd64,windows-i386,\!linux-amd64-support,windows-i386-support,windows-i386 install deploy

x86_64-mingw32: 
	find . -iname "target" -exec rm -rf {} \; || true
	CROSSDEV=1 mvn -P\!linux-amd64,windows-amd64,\!linux-amd64-support,windows-amd64-support,windows-amd64 clean || true
	CROSSDEV=1 mvn -P\!linux-amd64,windows-amd64,\!linux-amd64-support,windows-amd64-support,windows-amd64 install deploy

x86_64-linux: 
	find . -iname "target" -exec rm -rf {} \; || true
	mvn -Plinux-i386-support,linux-amd64-support,windows-i386-support,windows-amd64-support,doxygen-support,test-support,javadoc-support,alldeps-support clean || true
	mvn -Plinux-i386-support,linux-amd64-support,windows-i386-support,windows-amd64-support,doxygen-support,test-support,javadoc-support,alldeps-support install deploy 
	#site-deploy


