## SIMBKSD

### Development
Untuk development, dapat menjalankan aplikasi dengan menggunakan Embedded Tomcat App Server, sebagai berikut:
`mvn spring-boot:run`

Atau mengemas aplikasi dalam sebuah War file, lalu dideploy di local standalone Tomcat App server (dalam `webapps` folder dari Tomcat).

### Mengemas WAR file untuk Dev, UAT, Prod

#### Dev
`mvn package -Pdev`

#### UAT
`mvn package -Puat`

`mvn package -Puat -DskipTests` untuk mengemas aplikasi tanpa melakukan test terlebih dahulu.

#### Prod
`mvn package -Pprod`

War file akan dihasilkan di folder `target`

### Test

### Install Playwright

`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"`

#### Test Generator

`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://localhost:8443/SIMBKSD --ignore-https-errors"`
