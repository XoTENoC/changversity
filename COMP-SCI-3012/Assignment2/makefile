SRC_MAIN = src/main/java
SRC_TEST = src/test/java
BIN = bin
LIB = lib
JARS = $(LIB)/junit-jupiter-api-5.9.2.jar \
       $(LIB)/junit-jupiter-engine-5.9.2.jar \
       $(LIB)/hamcrest-core-1.3.jar \
       $(LIB)/junit-platform-console-standalone-1.9.2.jar \
       $(LIB)/gson-2.8.9.jar

CLASSPATH = $(LIB)/*:$(BIN)

JC = javac
JA = java

SOURCES_MAIN = $(wildcard $(SRC_MAIN)/**/*.java)
SOURCES_TEST = $(wildcard $(SRC_TEST)/**/*.java)

all: compile test

compile: $(JARS) $(BIN)
	$(JC) -g -cp $(CLASSPATH) -d $(BIN) $(SOURCES_MAIN)

aggServer: compile
	$(JA) -cp $(CLASSPATH) main.java.server.AggregationServerImpl

contentServer: compile
	$(JA) -cp $(CLASSPATH) main.java.server.ContentServerImpl

client: compile
	$(JA) -cp $(CLASSPATH) main.java.client.GETClientImpl

test: compileTest runTest

$(BIN):
	mkdir -p $(BIN)

$(LIB):
	mkdir -p $(LIB)

$(LIB)/gson-2.8.9.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar

$(LIB)/junit-jupiter-api-5.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.9.2/junit-jupiter-api-5.9.2.jar

$(LIB)/junit-jupiter-engine-5.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.9.2/junit-jupiter-engine-5.9.2.jar

$(LIB)/hamcrest-core-1.3.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

$(LIB)/junit-platform-console-standalone-1.9.2.jar: | $(LIB)
	curl -o $@ https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/junit-platform-console-standalone-1.9.2.jar

compileTest: $(JARS) $(BIN)
	$(JC) -cp $(CLASSPATH) -d $(BIN) $(SOURCES_TEST)

runTest: compileTest
	$(JA) -cp $(CLASSPATH) org.junit.platform.console.ConsoleLauncher --scan-class-path

clean:
	rm -rf $(BIN)
	rm -rf $(LIB)
	rm -rf *.class