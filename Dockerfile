FROM java:8.0-alpine3.19

WORKDIR /app

COPY . .

RUN go mod download && \
      CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -o runner -ldflags="-s -w" ./cmd/lambda

FROM scratch

COPY --from=0 /app/runner /usr/local/bin/runner
COPY --from=0 /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

ENTRYPOINT [ "runner" ]
