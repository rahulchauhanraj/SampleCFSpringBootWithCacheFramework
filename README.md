
Sample Predix Microservice Template using Spring Rest Template with UAA auth and Cache Framework
==============

Welcome to Predix Microservice CF Spring, a Predix Backend Microservice Template.  

This Microservice Template project shares the following characteristics

* Externalized Properties files
* REST implementation and framework
* Cloud ready with a Manifest file
* Application with uaa auth
* Application with cache framework
* Continuous Integration capable


1. Download the project   
  ```
  $ git clone https://github.com/rahulchauhanraj/SampleCFSpringBootWithCacheFramework.git
  
  $ cd SampleCFSpringBootWithCacheFramework
  
  $ mvn clean package  
  
    note: mvn clean install may run integration tests against services you may not have set up yet
  ```
2. Push to cloud  
  
  Take a look at the [SampleCFSpringBootWithCacheFramework manifest.yml](manifest.yml) which provides properties and instructions for [pushing cloud foundry]
  ```
  $ cf push 
 
