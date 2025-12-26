FROM ubuntu:jammy

COPY target/elastic_fetcher_api /elastic_fetcher_api

CMD ["/elastic_fetcher_api"]
