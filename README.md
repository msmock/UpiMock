# UPI Mock
Mock for UPI queries with either VN (AHVN13) or SPID to query one of the 100 test patient data (patients.csv).

Published on endpoint: http://localhost:[PORTNUMBER]/wsproxy/services/SpidQueryService2

## run
java -jar UpiMock.jar [PORTNUMBER]

## Note
- The /data and /template folder must be in the folder the java vm is started with the above command
- Available test patients are listed in the csv file
- Test requests and Postman export is avialbale in the test folder

## Open Issues
- Poor error handling. Soap error should be created instead of HTML response.
- Contnet type limited to text/xml