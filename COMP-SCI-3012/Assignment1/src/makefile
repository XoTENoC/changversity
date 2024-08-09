SRC = ./
BIN = bin
LIB = lib
JARS = $(LIB)/junit-jupiter-api-5.9.2.jar \
       $(LIB)/junit-jupiter-engine-5.9.2.jar \
       $(LIB)/hamcrest-core-1.3.jar \
       $(LIB)/junit-platform-console-standalone-1.9.2.jar

CLASSPATH = $(LIB)/*:$(BIN)

JC = javac
JA = java

SOURCES = Calculator.java CalculatorServer.java CalculatorClient.java CalculatorClient2.java CalculatorImplementation.java

all: compile test

compile:
	$(JC) -g $(SOURCES)

server: compile
	$(JA) CalculatorServer

client: compile
	$(JA) CalculatorClient

client2: compile
	$(JA) CalculatorClient2


test: compileTest runTest

$(BIN):
	mkdir -p $(BIN)

$(LIB):
	mkdir -p $(LIB)

$(LIB)/junit-jupiter-api-5.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.9.2/junit-jupiter-api-5.9.2.jar

$(LIB)/junit-jupiter-engine-5.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.9.2/junit-jupiter-engine-5.9.2.jar

$(LIB)/hamcrest-core-1.3.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

$(LIB)/junit-platform-console-standalone-1.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/junit-platform-console-standalone-1.9.2.jar
	
compileTest: $(JARS) $(BIN)
	$(JC) -cp $(CLASSPATH) -d $(BIN) $(SRC)*.java

runTest: compile
	$(JA) -cp $(CLASSPATH) org.junit.platform.console.ConsoleLauncher --scan-class-path


clean:
	rm -rf $(BIN)
	rm -rf $(LIB)
	rm -rf *.class
