# Allow Netty's native library loading (restricted method under JDK 24+)
MAVEN_OPTS="${MAVEN_OPTS:+$MAVEN_OPTS }--enable-native-access=ALL-UNNAMED" mvn -q compile exec:java
