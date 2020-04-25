######################
#      Makefile      #
######################

FILE_NAME = documentation.tex

LATEX = xelatex
BIBER = biber
RUSTY_SWAGGER = rusty-swagger

all: clean all1
all1: clean updateproject swagger la la2 la3 
no: clean updateproject swagger la la2 
docker-build: updateproject docker

help: 
	@awk 'BEGIN {FS = ":.*##"; printf " \nnoiapp-backend\n\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)


dev-run: docker-build ## Builds and runs containerized server in dev configuration 
	docker run --rm -d -p 80:8080 -e JAVA_OPTS="-Dspring.profiles.active=dev" --name=dev_noiapp noiapp/noiapp-backend:develop

dev-stop: ## stops the dev server
	docker stop dev_noiapp

updateproject:
	mvn -f dpppt-backend-sdk/pom.xml clean package
	#cp dpppt-backend-sdk/dpppt-backend-sdk-ws/generated/swagger/swagger.yaml documentation/yaml/sdk.yaml

swagger:
	cd documentation; $(RUSTY_SWAGGER) --file ../dpppt-backend-sdk/dpppt-backend-sdk-ws/generated/swagger/swagger.yaml

la:
	cd documentation;$(LATEX) $(FILE_NAME)
bib:
	cd documentation;$(BIBER) $(FILE_NAME)
la2:
	cd documentation;$(LATEX) $(FILE_NAME)
la3:
	cd documentation;$(LATEX) $(FILE_NAME)
show:
	cd documentation; open $(FILE_NAME).pdf &

docker:
	cp dpppt-backend-sdk/dpppt-backend-sdk-ws/target/dpppt-backend-sdk-ws-1.0.0-SNAPSHOT.jar ws-sdk/ws/bin/dpppt-backend-sdk-ws-1.0.0.jar
	mkdir -p ws-sdk/ws/conf
	rm -f ws-sdk/ws/conf/*
	cp dpppt-backend-sdk/dpppt-backend-sdk-ws/src/main/resources/logback.xml ws-sdk/ws/conf/dpppt-backend-sdk-ws-logback.xml
	cp dpppt-backend-sdk/dpppt-backend-sdk-ws/src/main/resources/*.properties ws-sdk/ws/conf
	docker build -t noiapp/noiapp-backend:develop ws-sdk/
	


clean:
	@rm -f documentation/*.log documentation/*.aux documentation/*.dvi documentation/*.ps documentation/*.blg documentation/*.bbl documentation/*.out documentation/*.bcf documentation/*.run.xml documentation/*.fdb_latexmk documentation/*.fls documentation/*.toc
