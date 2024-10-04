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

#### Prod
`mvn package -Pprod`

War file akan dihasilkan di folder `target`

### Test

`mvn test` untuk menjalankan test (tanpa e2e)

`mvn verify` atau `mvn failsafe:integration-test` untuk menjalankan e2e test.

### Install Playwright

`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"`

#### Test Generator

`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://localhost:8443/SIMBKSD --ignore-https-errors"`

### Scheduled Jobs

`DevUtilityService->downloadMissingFile()` mendownload file naskah kerja sama yang missing. 
