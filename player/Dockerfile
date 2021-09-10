FROM adoptopenjdk:11-jdk-hotspot

COPY build/libs/player.jar /player.jar

ENTRYPOINT ["java", "-XshowSettings:vm", "-jar", "/player.jar"]