FROM java:8
COPY src/ /usr/src/st
WORKDIR /usr/src/st
EXPOSE 3010
RUN javac Main.java
CMD ["java", "Main"]
