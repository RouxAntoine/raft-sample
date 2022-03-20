.PHONY: clean run jar fat-jar

GRADLE_CLI=./gradlew
JAR_NAME=./build/libs/RaftSample-1.0.0-SNAPSHOT

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
	$(GRADLE_CLI) jar && java -jar "$(JAR_NAME).jar" --id=3 --peers=0.0.0.0:1027,127.0.0.1:1024,127.0.0.1:1025,127.0.0.1:1026

fat-jar:
	$(GRADLE_CLI) uberJar && java -jar "$(JAR_NAME)-uber.jar" --id=4 --peers=0.0.0.0:1028,127.0.0.1:1024,127.0.0.1:1025,127.0.0.1:1026,127.0.0.1:1027

check-port:
	sudo netstat -nltuAav -p udp | grep 1024

release:
	$(GRADLE_CLI) jreleaserFullRelease