# marketo

Download the project from the github

Open application property file under src/main/resources folder

1. change the server port if required
2. change the value of "exportfilepath"  - The downloaded lead/activity file will be stored under this folder, hence it should be having write access

Open logback.xml file under src/main/resources folder
1. change the property LOG_FILE value to your directory -- This will write the log file in the respective location


To run this application

mvn clean install

then copy the jar file and upload it into your server

Connect to your serverthen run

java -jar jarname &

it will deploy the application and run in backgroud

once the server ups then run the below commands


API

1. To check server is up 

    curl -i  http://localhost:8081/api/v1/marketo

    Response : 
    HTTP/1.1 200 
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 2
    Date: Thu, 22 Nov 2018 16:14:38 GMT

    OK
    
2. To download the Lead data from marketo server

	curl -i -G -d  "startAt=2017-01-01T00:00:00Z&endAt=2017-01-31T00:00:00Z" http://localhost:8081/api/v1/marketo/getLeadData

  Response:
  HTTP/1.1 202 
  Content-Type: text/plain;charset=UTF-8
  Content-Length: 80
  Date: Thu, 22 Nov 2018 16:15:36 GMT

  Lead Extract Submitted Successfully !!! Please check your folder after sometime.
  
  Check the folder/log file to see the status.


3. To download Activity Data from marketo server

  
  curl -i -G -d  "startAt=2017-01-01T00:00:00Z&endAt=2017-01-31T00:00:00Z" http://localhost:8081/api/v1/marketo/getActivityData
  
  Response:
  HTTP/1.1 202 
  Content-Type: text/plain;charset=UTF-8
  Content-Length: 80
  Date: Thu, 22 Nov 2018 16:15:36 GMT

  Activity Extract Submitted Successfully !!! Please check your folder after sometime.
  
  
  Check the folder/log file to see the status.

4. To upload file to marketo server

	curl -i -X POST -H "Content-Type: multipart/form-data" -F file=@"/home/krishnan/marketodata/Leadupload.csv" http://localhost:8081/api/v1/marketo/uploadLeadData

  Response:
  HTTP/1.1 202 
  Content-Type: text/plain;charset=UTF-8
  Content-Length: 42
  Date: Thu, 22 Nov 2018 16:18:36 GMT

  Upload Lead data submitted succefully !!!
  
  please check the log file for any issues

