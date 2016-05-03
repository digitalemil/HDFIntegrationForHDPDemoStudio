HDFIntegrationForHDPDemoStudio
==============================

This repository should be used with applications built with HDPDemoStudio (https://github.com/digitalemil/HDPDemoStudio.git)

HDPDemoStudio generates a UI with which a user can generate JSON messages. These messages are then send to Kafka for further processing in Storm or Spark. Although generating the messages via a UI is comfortable it's not a realistic scenario. In a real-world scenario the messages would be generated by sensors or other field equipment. We are simulating this scenario with HDF. This repository contains a flow.xml.gz file which makes HDF listen on port 8082 for incoming messages on http://yoursandboxip:8081/data/publish. The incoming messages are enriched through a InvokeScriptedProcessor adding the fields id,event_timestamp & location if they are not yet included by the message. The PutKafka processor takes the enriched message and sends it to the Kafka topic which was generated for you by HDPDemoStudio. In most cases I think you will use curl to send messages to HDF. E.g.:

```
curl -H "Content-Type: application/json" -X POST -d '{"deviceid":"mydev", "heartrate":"180", "username":"me"}' http://sandbox:8082/data/publish
```

You need to replace the fields 'deviceid', 'heartrate', 'username' with the fields you defined when creating your application with HDPDemoStudio.

Installation instructions:

1. Download HDF-1.2.0.0-91.tar from the Hortonworks website: http://hortonworks.com/downloads/#dataflow

2. Copy the file to your HDP sandbox: scp ~/Downloads/HDF-1.2.0.0-91.tar root@sandbox:/root

3. Ssh to your sandbox: ssh root@sandbox

4. Extract tar-ball: tar xf HDF-1.2.0.0-91.tar

5. Change Nifi port: vi /root/HDF-1.2.0.0/nifi/conf/nifi.properties (Set nifi.web.http.port to a free port e.g. 8081)

6. Copy flow.xml.gz from this repository to /root/HDF-1.2.0.0/nifi/conf/ (on sandbox)

7. Start nifi: /root/HDF-1.2.0.0/nifi/bin/nifi.sh start

8. Browse to: http://sandbox:8081/nifi (If you specified a different port above modify the URL accordingly)

9. Configure the PutKafka Processor. Set the topic name either to the topic you defined in HDPDemoStudio (Storm reads on this topic) or add an extra "-spark" for using SparkStreaming


And then feed data into your application:
curl ...




