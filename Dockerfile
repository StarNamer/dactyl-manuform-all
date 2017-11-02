FROM clojure:lein-2.7.1

WORKDIR /dactyl

COPY project.clj .
RUN lein deps

RUN mkdir -p things
COPY src src
CMD lein run
