# Pollaris
A modular, extensible file system polling service with pluggable backends (Local FS, AWS S3) and configurable actions based on file locations. Designed for easy integration, flexible polling intervals, and customizable event-driven workflows.
Pollaris

# Features
 * Supports local filesystem and Amazon S3 backends
 * Configurable pollers via YAML files
 * Flexible scheduling policies 
 * Actions such as logging events or no-ops
 * Extensible architecture with pluggable components



# Getting Started

# Prerequisites
* Java 11 or higher
* Maven 3+
* AWS credentials with permissions to access the configured S3 buckets. Credentials should be exported as environment variable in the terminal where pollaris is executed. The variables needed are:
    * AWS_ACCESS_KEY_ID
    * AWS_SECRET_ACCESS_KEY
    * AWS_REGION

# Building
```
git clone https://github.com/gpfarina/pollaris.git
cd pollaris
mvn clean install
```
# Running
 ## Configure your config.yaml with poller settings
 * find an example in  src/main/java/com/pollaris/driver/configuration.yaml
 ## Run the application:
 * java -jar target/pollaris.jar path/to/config.yaml
