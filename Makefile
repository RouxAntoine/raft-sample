.PHONY: clean run jar fat-jar

GRADLE_CLI=./gradlew

clean:
	$(GRADLE_CLI) clean

run:
	$(GRADLE_CLI) run

0:
	$(GRADLE_CLI) run --args="--id=0 --peers=0.0.0.0:1024,127.0.0.1:1025,127.0.0.1:1026"

1:
	$(GRADLE_CLI) run --args="--id=1 --peers=0.0.0.0:1025,127.0.0.1:1024,127.0.0.1:1026"

2:
	$(GRADLE_CLI) run --args="--id=2 --peers=0.0.0.0:1026,127.0.0.1:1025,127.0.0.1:1024"

jar:
	$(GRADLE_CLI) jar && java -jar ./build/libs/FluxTest-1.0.0-SNAPSHOT.jar

fat-jar:
	$(GRADLE_CLI) uberJar && java -jar ./build/libs/FluxTest-1.0.0-SNAPSHOT-uber.jar

check-port:
	sudo netstat -nltuAav -p udp | grep 1024