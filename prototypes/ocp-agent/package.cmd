call mvn package -P windows-x86_64 -DskipTests
call mvn package -P windows-x86 -DskipTests
call mvn package -P linux-x86_64 -DskipTests
call mvn package -P linux-x86 -DskipTests
call mvn package -P macosx-cacao-x86_64 -DskipTests
call mvn package -P macosx-cacao-x86 -DskipTests