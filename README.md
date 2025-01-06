Step to produce the ecommerce application

OS: Ubuntu for Window or MacOS (can use Window, but check conditions if any)

1. Run file Axon Server attached by commands below:
You need to access to the right directory of axon server (in the external-services)
	cd axonserver
	java -jar axonserver.jar
Note: please review if anything is good. If not, there can be something like this: you are running 2 axon server simultaneously or follow the instruction

If "Connecting to AxonServer node [localhost:8124] failed: NOT_FOUND: [AXONIQ-1302] default: not found in any replication group" >> check this line already in axonserver.properties yet??
"AXONIQ.AXONSERVER.STANDALONE=true"

2. Download Kafka via this website: https://kafka.apache.org/downloads
	Choose the latest supported version
	Setup on your environment/ IDE
	run: "cd kafka-3.9.0-src"
	run: "./gradlew jar -PscalaVersion=2.13.14'"
	run: "bin/zookeeper-server-start.sh config/zookeeper.properties" (Please make sure zookeeper activated before Kafka server)
	run: "bin/kafka-server-start.sh config/server.properties"


3. Download and login your MySQL account. And follow these steps:
	Create database named: "cqrses" by command: "create database cqrses". Or you can double check the database name in ./resources/application.properties file
	Use that database: "use cqrses"
	Copy all the DB script in IDE from the directory: ./resources/DBLoadScript.sql (create tables and insert values for tables)
	Paste and run in MySQL Workbench
4. Run application
5. Use postman or HTTP Client in file to test API
6. Run the test in directory for more information: 

-----------
Setup Prometheus
2. Install Prometheus
Step 1: Download and Install Prometheus
Download Prometheus from the official site, extract it, and configure it.

Step 2: Configure Prometheus
Edit the prometheus.yml file to scrape metrics from your application. Add your application endpoint under scrape_configs:

yaml
Copy code
scrape_configs:
  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['localhost:8080'] # Replace 8080 with your app's port
Step 3: Start Prometheus
Run Prometheus:

bash
Copy code
./prometheus --config.file=prometheus.yml (prometheus --config.file=prometheus.yml)
Prometheus will now scrape metrics from your Spring Boot application.

Run command to check Prometheus live: "sudo systemctl status prometheus"
Start server Prometheus command: "sudo systemctl start prometheus"

Grafana Ubuntu

Run command to check Prometheus live: "sudo systemctl status grafana-server"
Start server Prometheus command: "sudo systemctl start grafana-server"

Step 1: Add the Grafana Repository
Import the Grafana GPG key:

bash
Copy code
sudo apt-get install -y software-properties-common wget
wget -q -O - https://packages.grafana.com/gpg.key | sudo gpg --dearmor -o /usr/share/keyrings/grafana-archive-keyring.gpg
Add the Grafana APT repository to your system:

bash
Copy code
echo "deb [signed-by=/usr/share/keyrings/grafana-archive-keyring.gpg] https://packages.grafana.com/oss/deb stable main" | sudo tee /etc/apt/sources.list.d/grafana.list
Step 2: Update the Package List
Run the following command to update your APT package list:

bash
Copy code
sudo apt update
Step 3: Install Grafana
After updating, install Grafana with:

bash
Copy code
sudo apt install -y grafana
Step 4: Start the Grafana Service
Start the Grafana server:

bash
Copy code
sudo systemctl start grafana-server
Enable it to start on boot:

bash
Copy code
sudo systemctl enable grafana-server
Step 5: Verify Installation
Check if Grafana is running:

bash
Copy code
sudo systemctl status grafana-server
You should see the service running. Access Grafana at http://localhost:3000.

3. Install Grafana (Window)
Step 1: Download and Install Grafana
Download Grafana from the official site, install it, and start the server.

Step 2: Log in to Grafana
Access Grafana in your browser at http://localhost:3000 (default username/password: admin/admin).

Step 3: Add Prometheus as a Data Source
Navigate to Configuration > Data Sources in Grafana.
Click Add data source.
Select Prometheus.
Enter the Prometheus URL (e.g., http://localhost:9090).
Save & Test the connection.
4. Create Dashboards in Grafana
Example Dashboards:
Data Integrity: Display metrics for validation errors, duplicate records, and empty fields using custom counters in your application.
Performance: Monitor request latency, response time, and materialized view update duration.
Security: Visualize failed login attempts, unauthorized access counts, or SQL injection attempts.
Predefined Panels:
You can use Prometheus queries to create Grafana panels.

GitHub repository:
CQRS: https://github.com/loinguyen19/finalthesis-MSc-CS-C13 
Conventional EDA: https://github.com/loinguyen19/finalthesis-MSc-CS-C13/tree/conventional-eda 
