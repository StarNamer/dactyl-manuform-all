FROM clojure:lein-2.8.1
WORKDIR /dactyl
COPY project.clj ./
RUN git clone https://github.com/adereth/unicode-math && cd unicode-math && lein install
RUN lein deps
CMD lein uberjar
