# X28 Ontology XML Import Task 

The app downloads a zip file from the X28 sFtp server, extracts and process the XML file in a Spring Batch Task. 

### Deployment to Spring Cloud DataFlow


##### [REST API](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#api-guide-resources-index) 

```bash
# Register 'App'
curl 'http://dev.job-room.ch:9393/apps/task/x28ontologyimport' -i -X POST \
  -d 'force=true&uri=maven://ch.admin.seco.service.reference:app-x28-ontology-import-task:<version>'

# Delete existing task
curl http://dev.job-room.ch:9393/tasks/definitions/x28-ontology-import-task -i -X DELETE

# Create stream and deploy
curl http://dev.job-room.ch:9393/tasks/definitions -X POST \
  -d 'name=x28-ontology-import-task&definition=x28ontologyimport'
```

##### Dashboard

[Register Task](http://dev.job-room.ch:9393/dashboard/#/apps)

- `Register Application(s)`
  - name = x28ontologyimport
  - type = task
  - App URI = `maven://ch.admin.seco.service.reference:app-x28-ontology-import-task:jar:<version>` \
  - `Register` 

[Create Task](http://dev.job-room.ch:9393/dashboard/#/tasks/apps)
- `Create Definition` of the registered task
- Enter all required properties to access the sFtp server
- `Submit`


### Deployment from command line
Run **x28-ontology-import** as seperate task from command-line:

```bash
curl https://alvch.jfrog.io/alvch/global/ch/admin/seco/jobs/services/jobadservice/app-x28-ontology-import-task/<version>/app-x28-ontology-import-task-<version>.jar

java -Xmx256m -jar app-x28-ontology-import-task-<version>.jar 
  --jobroom.x28.sftp.host=admin.x28.ch
  --jobroom.x28.sftp.port=22
  --jobroom.x28.sftp.username=<x28.username>
  --jobroom.x28.sftp.password=<x28.password>
  --spring.datasource.driverClassName=org.postgresql.Driver
  --spring.cloud.task.name=x28-ontology-task
  --spring.datasource.username=spring-cloud-dataflow
  --spring.datasource.password=<db.password>
  --spring.datasource.url=jdbc:postgresql://dev.job-room.ch:5440/spring-cloud-dataflow
  --server.port=0
  --spring.cloud.task.executionid=<executionid>
```
