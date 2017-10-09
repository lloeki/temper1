all: Temper.class

javahidapi:
	git clone https://github.com/codeminders/javahidapi.git

Temper.class: Temper.java hidapi-1.1.jar
	javac -cp hidapi-1.1.jar $<

run: Temper.class
	java -cp '.:hidapi-1.1.jar' Temper

hidapi-1.1.jar: javahidapi
	cd javahidapi && git checkout ac7cf8790baf63a51ee53617a5596baeac28942b
	cd javahidapi && make -C mac
	cd javahidapi && ant
	cd javahidapi && ant dist
	cp javahidapi/dist/lib/hidapi-1.1.jar .

clean:
	rm -f *.class
