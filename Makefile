all: Temper.class

Temper.class: Temper.java hidapi-1.1.jar
	javac -cp hidapi-1.1.jar $<

run: Temper.class
	java -cp '.:hidapi-1.1.jar' Temper

clean:
	rm -f *.class
