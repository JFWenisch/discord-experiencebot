FROM ubuntu:18.04


ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update -y
RUN apt-get install -y console-setup sudo wget curl unzip openjdk-8-jdk-headless \
	&& apt-get install -y --no-install-recommends software-properties-common

RUN useradd --create-home -s /bin/bash user && \
    adduser user sudo && \
    echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers 

RUN mkdir /images
RUN chmod -R 777 /images

USER user
ENV USER=user


COPY --chown=user:user discord-bot/target/discord-*jar-with-dependencies.jar /discord-experiencebot.jar
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENTRYPOINT ["java","-jar","discord-experiencebot.jar"]
