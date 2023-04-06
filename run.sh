java \
	-Dcom.sun.management.jmxremote=true \
	-Djava.rmi.server.hostname=127.0.0.1 \
	-Dcom.sun.management.jmxremote.host=0.0.0.0 \
	-Dcom.sun.management.jmxremote.port=9991 \
	-Dcom.sun.management.jmxremote.rmi.port=9991 \
	-Dcom.sun.management.jmxremote.ssl=false \
	-Dcom.sun.management.jmxremote.registry.ssl=false \
	-Dcom.sun.management.jmxremote.authenticate=false \
	-Djava.net.preferIPv4Stack=true \
	-jar $1


