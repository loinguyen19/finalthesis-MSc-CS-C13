.PHONY: help
help: ## Prints help message.
	@ grep -h -E '^[a-zA-Z0-9_-].+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[1m%-30s\033[0m %s\n", $$1, $$2}'

MYSQL_CONTAINER := mysql-cqrses-api

.PHONY: setup
setup: ## Provisions local mysql db
	@ echo 'Setup local CQRSES db'
	@ docker rm -f ${MYSQL_CONTAINER} 2> /dev/null
	@ docker run -d --rm \
		--name ${MYSQL_CONTAINER} \
		--publish=8009:8009 --publish=8008:8008 \
		--env=MYSQL_AUTH=none \
		-e MYSQL_ROOT_PASSWORD=${MySQL_pass} \
		-e MYSQL_ALLOW_EMPTY_PASSWORD=false \
		-e MYSQL_apoc_export_file_enabled=true \
		-e MYSQL_apoc_import_file_enabled=true \
		-e MYSQL_apoc_import_file_use__mysql__config=true \
		-e MYSQL_PLUGINS=\[\"apoc\"\] \
	mysql:8.0-oracle

localenv: ## Provisions local dev environment.
	@ docker compose up --build

.PHONY: tear-down
tear-down: ## Tears down local Neo4j database.
	@ docker compose down --remove-orphans

.PHONY: tests
# the -p 1 was set to avoid data race which interferes with the integration tests against the local mysql instance
tests: ## Run unit tests.
	@ go mod tidy && \
 		go clean -testcache &&\
 		go test -timeout 300s -p 1 ./...


.PHONY: smoke-tests
smoke-tests: ## Run unit tests.
	go mod tidy && \
		go clean -testcache && \
		go test -v -timeout 300s ./health/smoketests/.

.PHONY: build
build: ## Compiles the binary.
	@ go mod tidy && \
  		CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -o runner -ldflags="-s -w" .

# staging aws account
AWS_ID := 715147558601
# prod: 816811978979
IMAGE_REPO := $(AWS_ID).dkr.ecr.eu-central-1.amazonaws.com
IMAGE_REPO_NAME := cim/api
IMAGE_TAG := v0.0.1-alpha.0

.PHONY: build-image
build-image: ## Build docker image.
	@ docker build -t $(IMAGE_REPO)/$(IMAGE_REPO_NAME):$(IMAGE_TAG) .

.PHONY: push-image
push-image: ## Push docker image to the registry.
	@ docker push $(IMAGE_REPO)/$(IMAGE_REPO_NAME):$(IMAGE_TAG)

.PHONY: ecr-auth
ecr-auth: ## Authenticates with the registry:
	@	aws ecr get-login-password --region eu-central-1 | \
		docker login --username AWS --password-stdin $(AWS_ID).dkr.ecr.eu-central-1.amazonaws.com
