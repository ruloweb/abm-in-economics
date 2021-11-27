install-mason:
	mvn install:install-file -Dfile=./lib/mason.20.jar -DgroupId=edu.gmu.eclab -DartifactId=mason -Dversion=20.0 -Dpackaging=jar -DgeneratePom=true

build:
	mvn clean package

run:
	java -cp target/simple-market-1.0-SNAPSHOT-jar-with-dependencies.jar com/ruloweb/abm/economics/simplemarket/SimpleMarketUI