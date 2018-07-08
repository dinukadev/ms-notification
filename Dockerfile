# Import base image
#include base image URL

# Create log file directory and set permission
RUN groupadd -r ms-notification && useradd -r --create-home -g ms-notification ms-notification
RUN if [ ! -d /var/log/ ];then mkdir /var/log/;fi
RUN chown -R ms-notification:ms-notification /var/log

# Move project artifact
ADD target/ms-notification-*.jar /home/ms-notification/

RUN touch /etc/ld.so.conf.d/java.conf
RUN echo $JAVA_HOME/lib/amd64/jli > /etc/ld.so.conf.d/java.conf
RUN ldconfig

RUN setcap CAP_NET_BIND_SERVICE=+eip $JAVA_HOME/bin/java

USER ms-notification

# Launch application server
ENTRYPOINT exec $JAVA_HOME/bin/java $XMS $XMX -jar -Dspring.profiles.active=$ENVIRONMENT  /home/ms-notification/ms-notification-*.jar --db.password="$DB_PASSWORD"
