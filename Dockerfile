FROM maven:3.6.3-jdk-11
ADD ./target/server-jar-with-dependencies.jar ./
RUN mkdir -p ./snark
COPY ./snark/ ./snark2
EXPOSE 25333 25334
CMD java -jar server-jar-with-dependencies.jar